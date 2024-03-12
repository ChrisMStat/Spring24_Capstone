import json
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression


# Load and process the JSON data
json_file_path = 'Schedule-Formatted.json'
new_data = []
with open(json_file_path, 'r') as file:
    data = json.load(file)
    for team in data['teams']:
        if 'schedule' in team and team['schedule']:
            new_data.append(team)


game_list = []
for team in new_data:
    team_name = team['name']
    wins, losses, pointsScored, pointsAllowed, total_games = 0, 0, 0, 0, 0
    for game in team['schedule']:
        if game['outcome'] == 'W':
           wins += 1
        elif game['outcome'] == 'L':
             losses += 1
    pointsScored += int(game.get('pointsScored', 0) if game.get('pointsScored', '0').isdigit() else 0)
    pointsAllowed += int(game.get('pointsAllowed', 0) if game.get('pointsAllowed', '0').isdigit() else 0)
    total_games += 1
    win_percentage = 100 * wins / total_games if total_games else 0
    differential = pointsScored - pointsAllowed
    average_points_scored = pointsScored / total_games if total_games else 0
    team_schedule = []
    for game in team['schedule']:
        if game['location'] != "BYE":
            game_info = {
                "team": team_name,
                "win_percentage": win_percentage,
                "average_points_scored": average_points_scored,
                "location": game['location'],
                "opponent": game['opponent'],
                "outcome": game['outcome'],
                "ptsScored": game['pointsScored'],
                "ptsAllowed": game['pointsAllowed'],
                "differential": differential,
                "game_differential": int(game['pointsScored']) - int(game['pointsAllowed'])
            }
            team_schedule.append(game_info)
    game_list.extend(team_schedule)


# Preprocess for the model
df = pd.DataFrame(game_list)
df['location'] = df['location'].map({'@': 1, 'N': 5, 'vs.': 10})
df['outcome'] = df['outcome'].map({'W': 1, 'L': 0})


# Model training
X = df[['game_differential', 'location']]
y = df['outcome']
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
model = LinearRegression()
model.fit(X_train, y_train)


# Rank the first 10 teams based on model's predictions
teams = df['team'].unique()[:10]  # Limiting to first 10 teams
rankings = []
for team in teams:
    predicted_outcomes = []
    for opponent in teams:
        if opponent != team:
            for location in ['@', 'N', 'vs.']:
                team_avg_score = df[df['team'] == team]['average_points_scored'].iloc[0]
                opponent_avg_score = df[df['team'] == opponent]['average_points_scored'].iloc[0]
                location_val = {'@': 1, 'N': 5, 'vs.': 10}[location]
                differential_avg = team_avg_score - opponent_avg_score
                predicted_outcome = model.predict([[differential_avg, location_val]])
                predicted_outcomes.append(predicted_outcome[0])
    avg_predicted_outcome = sum(predicted_outcomes) / len(predicted_outcomes)
    rankings.append({'team': team, 'predicted_strength': avg_predicted_outcome})


# Sort and print the top 10 team rankings
rankings.sort(key=lambda x: x['predicted_strength'], reverse=True)
rankings_json = json.dumps(rankings, indent=4)
print(rankings_json)


# Optionally, save the rankings to a JSON file
with open('team_rankings_top_10.json', 'w') as outfile:
    json.dump(rankings, outfile, indent=4)


print("Top 10 rankings computation and JSON output successful.")