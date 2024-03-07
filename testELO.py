import json
from datetime import datetime
from collections import defaultdict
import random

# Can optimize K, hta
# Idea for improvement - initialize elo ratings better

# Update the elo of both teams
def update_elo(ra, rb, outcome):
    K = 60 # Hyper-parameter, max change in elo, can be optimized
    Ea = 1 / (1 + 10 ** ((rb - ra) / 400))
    result = 1 if outcome == 'W' else 0
    
    new_ra = ra + K * (result - Ea)
    new_rb = rb + K * ((1 - result) - (1 - Ea))
    
    return new_ra, new_rb

# Return the probability of team A winning
def calculate_win_probability(elo_a, elo_b):
    return 1 / (1 + 10 ** ((elo_b - elo_a) / 400))

# Simulate the outcome of a game based on win probability
def simulate_game_outcome(elo_a, elo_b):
    win_probability_a = calculate_win_probability(elo_a, elo_b)
    return random.random() < win_probability_a  # Returns true if team A wins, false otherwise

# Put the games in order of date played
def reformat_data_to_chronological(filename):
    with open(filename, 'r') as file:
        data = json.load(file)

    games_by_date = {}
    processed_games = set()

    # Iterate through each team's schedule
    for team_data in data['teams']:
        for game in team_data['schedule']:
            # Skip bye days
            if not game.get('outcome'):
                continue
            
            opponent = game['opponent']
            date = game['timestamp']
            game_id = '_'.join(sorted([team_data['name'], opponent]) + [date])
            
            # Add each unique game
            if game_id not in processed_games:
                processed_games.add(game_id)
                
                if date not in games_by_date:
                    games_by_date[date] = []
                games_by_date[date].append({
                    'date': date,
                    'team': team_data['name'],
                    'opponent': opponent,
                    'outcome': game['outcome']
                })

    # Sort the list by date
    chronological_games = []
    for date in sorted(games_by_date.keys(), key=lambda x: datetime.strptime(x, "%m/%d/%Y")):
        chronological_games.extend(games_by_date[date])

    return chronological_games

# V Running V

# Put games in chronological order
filename = 'Schedule-Formatted.json'
chronological_games = reformat_data_to_chronological(filename)
with open(filename, 'r') as file:
    data = json.load(file)
    
# Initialize elo ratings for all teams
elo_ratings = defaultdict(lambda: 1500)

# Initialize variables for tracking predictions by month
monthly_predictions = defaultdict(lambda: {'total': 0, 'correct': 0})

# Initialize variables for tracking predictions
total_simple_predictions = 0
correct_simple_predictions = 0
total_simulated_predictions = 0
correct_simulated_predictions = 0

# Set home team advantage, can be optimized
hta = 40

# Go through each game
for game in chronological_games:
    team_a = game['team']
    team_b = game['opponent']
    actual_winner_is_a = game['outcome'] == 'W'
    game_date = datetime.strptime(game['date'], "%m/%d/%Y")
    game_month = game_date.strftime("%Y-%m")  # Format as Year-Month for monthly tracking
    
    # Simple prediction based on elo ratings
    predicted_winner_is_a_simple = (elo_ratings[team_a] + hta) >= elo_ratings[team_b]
    if predicted_winner_is_a_simple == actual_winner_is_a:
        monthly_predictions[game_month]['correct'] += 1
        correct_simple_predictions += 1
    monthly_predictions[game_month]['total'] += 1
    total_simple_predictions += 1

    # Simulated prediction based on win probabilities
    win_probability_a = calculate_win_probability(elo_ratings[team_a], elo_ratings[team_b])
    simulated_winner_is_a = random.random() < win_probability_a
    if simulated_winner_is_a == actual_winner_is_a:
        correct_simulated_predictions += 1
    total_simulated_predictions += 1

    # Update elo ratings based on the actual result
    if actual_winner_is_a:
        elo_ratings[team_a], elo_ratings[team_b] = update_elo(elo_ratings[team_a], elo_ratings[team_b], 'W')
    else:
        elo_ratings[team_b], elo_ratings[team_a] = update_elo(elo_ratings[team_b], elo_ratings[team_a], 'W')

# Calculate prediction accuracies for each approach
simple_prediction_accuracy = (correct_simple_predictions / total_simple_predictions) * 100
simulated_prediction_accuracy = (correct_simulated_predictions / total_simulated_predictions) * 100

# Accuracy assuming higher elo team always wins
print(f"Simple Prediction Accuracy: {simple_prediction_accuracy:.2f}%")
# Accuracy by simulating the games based on elo rating
print(f"Simulated Prediction Accuracy: {simulated_prediction_accuracy:.2f}%\n")

# Calculate and print prediction accuracy for each month
for month, predictions in sorted(monthly_predictions.items()):
    accuracy = (predictions['correct'] / predictions['total']) * 100
    print(f"{month} Simple Prediction Accuracy: {accuracy:.2f}% {predictions['correct']} / {predictions['total']}")

# Sort teams by their elo rating in descending order
sorted_teams_by_elo = sorted(elo_ratings.items(), key=lambda x: x[1], reverse=True)

# Write the team rankings to a file
output_filename = 'team_rankings.txt'
with open(output_filename, 'w') as file:
    for rank, (team, elo) in enumerate(sorted_teams_by_elo, start=1):
        file.write(f"{rank}, {team}, {elo:.2f}\n")
