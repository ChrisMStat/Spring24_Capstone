import json
from collections import OrderedDict

json_file_path = 'Schedule-Formatted.json'

# Open the JSON file for reading
with open(json_file_path, 'r') as file:
    data = json.load(file)

# Dictionary to store win and loss counts for each team
team_results = {}

# Iterate through the teams and their schedules
for team in data['teams']:
    team_name = team['name']
    wins = 0
    losses = 0
    win_per = 0
    pointsScored = 0
    pointsAllowed = 0
    differential = 0

    # Iterate through the team's schedule
    for game in team['schedule']:
        outcome = game['outcome']
        if outcome == 'W':
            wins += 1
        elif outcome == 'L':
            losses += 1
            
        if game['pointsScored']:
            pointsScored += int(game['pointsScored'])
        if game['pointsAllowed']:
            pointsAllowed += int(game['pointsAllowed'])
            
    # Calculate win percentage and points differential    
    differential = pointsScored - pointsAllowed
    if wins+losses > 0: 
        win_per = wins/(wins+losses)*100
        avg_score = pointsScored / (wins+losses)
        avg_allow = pointsAllowed / (wins+losses)
    else: win_per = 0
    
    # Store the results in the dictionary
    team_results[team_name] = {'Wins': wins, 'Losses': losses, 'Win Percentage': round(win_per, 3), 'Differential': differential, 'Average Points Scored': round(avg_score, 3), 'Average Points Allowed': round(avg_allow, 3)}

# Ranking base on win percentage and differential (for teams having same win percentage)    
sort_team_results = OrderedDict(sorted(team_results.items(), key=lambda x: (x[1]['Win Percentage'], x[1]['Differential']), reverse = True))

# Print the results
order = 1
for team_name, results in sort_team_results.items():
    print(f"{order}. {team_name}: Wins - {results['Wins']}, Losses - {results['Losses']}, Win Percentage - {results['Win Percentage']}, Differential - {results['Differential']}, Average Points Scored - {results['Average Points Scored']}, Average Points Allowed - {results['Average Points Allowed']}")
    order += 1
    
