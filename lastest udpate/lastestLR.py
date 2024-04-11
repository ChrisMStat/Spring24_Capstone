import json

json_file_path = 'Schedule-Formatted.json'
win_percentage_file_path = 'win_percentages.txt'

# Read win percentages from the file
win_percentages = []
outcomes = []  # Separate variable to store outcomes

with open(win_percentage_file_path, 'r') as file:
    lines = file.readlines()
    for i in range(0, len(lines), 2):  # Read every two lines
        team_line = lines[i].strip()
        if i + 1 < len(lines):  # Check if there are enough lines remaining
            opponent_line = lines[i + 1].strip()

            team_name = team_line.split(", ")[0].split(": ")[1]
            team_win_percentage = float(team_line.split(", ")[1].split(": ")[1][:-1]) / 100.0

            opponent_name = opponent_line.split(", ")[0].split(": ")[1]
            opponent_win_percentage = float(opponent_line.split(", ")[1].split(": ")[1][:-1]) / 100.0

            # Convert win percentage to outcome ('W' or 'L')
            if team_win_percentage > 0.5:  # If the team's win percentage is above 50%
                outcome = 'W'
            else:
                outcome = 'L'

            win_percentages.append((team_win_percentage, opponent_win_percentage))
            outcomes.append(outcome)

# print(win_percentages)
# print(outcomes)



new_data = []
# Open the JSON file for reading
with open(json_file_path, 'r') as file:
    data = json.load(file)

    # Iterate through the teams
    for team in data['teams']:
        # Check if the team has a 'schedule' key and it is not empty
        if 'schedule' in team and team['schedule']:
            new_data.append(team)


game_list = []

for i, team in enumerate(new_data):
    team_name = team['name']
    wins = 0
    losses = 0
    pointsScored = 0
    pointsAllowed = 0
    total_games = 0  # Track the total number of games played

    # Iterate through the team's schedule to calculate wins, losses, points scored, and points allowed
    for game in team['schedule']:
        if game['outcome'] == 'W':
            wins += 1
        elif game['outcome'] == 'L':
            losses += 1

        if game['pointsScored']:
            pointsScored += int(game['pointsScored'])
        if game['pointsAllowed']:
            pointsAllowed += int(game['pointsAllowed'])

        # Increment the total games played
        total_games += 1

    # Calculate winning percentage and points differential
    games_played = wins + losses
    win_percentage = wins / games_played * 100 if games_played > 0 else 0
    differential = pointsScored - pointsAllowed

    # Calculate average points scored per game
    average_points_scored = pointsScored / (wins+losses) if (wins+losses) > 0 else 0

    # Extracting schedule information
    team_schedule = []
    for game in team['schedule']:
        if game['location'] != "BYE" and i < len(win_percentages):
            location = game['location']
            opponent = game['opponent']
            outcome = game['outcome']
            ptsScored = game['pointsScored']
            ptsAllowed = game['pointsAllowed']
            team_win_percentage, opponent_win_percentage = win_percentages[i]

            # Calculate the differential for this game
            game_differential = int(ptsScored) - int(ptsAllowed)

            # Add game information to the team's schedule list, including the differential for this game
            game_info = {
                "team": team_name,
                #"team_win_percentage": team_win_percentage,
                #"opponent_win_percentage": opponent_win_percentage,
                "average_points_scored": average_points_scored, 
                "location": location,
                "opponent": opponent,
                "outcome": outcome,
                "ptsScored": ptsScored,
                "ptsAllowed": ptsAllowed,
                "differential": differential,
                "game_differential": game_differential  # Adding the differential per game
            }
            team_schedule.append(game_info)

    # Add team information to the overall list
    game_list.extend(team_schedule)

# Add win percentages for each game in game_list
for i, game in enumerate(game_list):
    team_win_percentage, opponent_win_percentage = win_percentages[i]
    game['team_win_percentage'] = team_win_percentage
    game['opponent_win_percentage'] = opponent_win_percentage
    
# Print the resulting game_list
# for game in game_list:
#     print(game)

##########################################################################################################
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression
from sklearn.metrics import accuracy_score

def predict(team, opponent, location, team_win_percentage, opponent_win_percentage):
    team_pts = opponent_pts = location_val = 0

    for game in game_list:
        if game['team'] == team:
            team_pts = game['average_points_scored']
            break
        
    for game in game_list:
        if game['team'] == opponent:
            opponent_pts = game['average_points_scored']
            break
        
    if location == "@":
        location_val = 1
    elif location == "N":
        location_val = 5
    elif location == ".vs":
        location_val = 10    
   
    differential_avg = team_pts - opponent_pts   

    # Convert your game_list into a DataFrame
    df = pd.DataFrame(game_list)

    # Map location values to numerical format
    location_mapping = {'@': 1, 'N': 5, 'vs.': 10}
    df['location'] = df['location'].map(location_mapping)

    outcome_mapping = {'W': 1, 'L': 0}
    df['outcome'] = df['outcome'].map(outcome_mapping)

    X_columns = ['game_differential', 'location', 'team_win_percentage', 'opponent_win_percentage']  # Add win percentages
    y_column = ['outcome']

    # Check for missing values in the 'outcome' column
    missing_values = df['outcome'].isnull().sum()
    if missing_values > 0:
        # Handle missing values by dropping rows with missing values
        df.dropna(subset=['outcome'], inplace=True)

    # Extract X and y from the DataFrame
    X = df[X_columns]
    y = df[y_column]

    # Split the data into training and testing sets
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    # Initialize and train the linear regression model
    regr = LinearRegression()
    regr.fit(X_train, y_train)

    # Predict the outcomes for the test set
    y_pred = regr.predict(X_test)

    # Round the predicted values to the nearest integer (0 or 1)
    y_pred_rounded = y_pred.round().astype(int)

    # Convert any predicted values greater than 1 to 1
    y_pred_rounded[y_pred_rounded > 1] = 1

    # Calculate the accuracy
    accuracy = accuracy_score(y_test, y_pred_rounded)

    # Define a list of feature values for a new instance
    new_instance = [[differential_avg, location_val, team_win_percentage, opponent_win_percentage]]  # Include win percentages

    # Predict the outcomes for the new instances
    predicted_outcomes = regr.predict(new_instance)

    return predicted_outcomes  # Return the predicted outcome


############################################################################################################
'''
    User input and prediction
'''

# Read input from the user
team = input("Enter team's name: ")
opponent = input("Enter opponent's name: ")
location = input("Enter location (type @ for away, .vs for home, and N for neutral location): ")

# Check if input team and opponent are in D1
team_exist = False
opponent_exist = False

for game in game_list:
    if game['team'] == team:
        team_exist = True
        break
    
for game in game_list:
    if game['opponent'] == opponent:
        opponent_exist = True
        break

# Result in for 0 if team wins and 1 if team loses
result = 0
# print(team_exist)
# print(opponent_exist)

if not team_exist and not opponent_exist:
    print("Cannot predict this game because of lack of data for these two teams")
elif team_exist and (not opponent_exist):
    print(f"{team} wins the game.")
elif not team_exist and opponent_exist:
    print(f"{opponent} wins the game.")  
else:  
    # Get ELO win percentage    
    from getELO import EloCalculator
    # Create an instance of the EloCalculator class
    elo_calculator = EloCalculator('Schedule-Formatted.json')
    team_wp = elo_calculator.get_elo_win_percentage(team, opponent) / 100
    opponent_wp = 1 - team_wp
    
    result = predict(team, opponent, location, team_wp, opponent_wp)
    if result < 0.5:
        print(f"{opponent} wins the game.")
    else:
        print(f"{team} wins the game.")
    print(result)

      

##############################################################################################
'''
    Test algorithm prediction's accuracy
'''
# # Create a dictionary to hold columns from game_list
# game_data = {
#     'team': [],
#     'opponent': [],
#     'location': [],
#     'team_win_percentage': [],
#     'opponent_win_percentage': []
# }

# # Build a test for the first 500 rows of game_list
# for game in game_list:
#     game_data['team'].append(game['team'])
#     game_data['opponent'].append(game['opponent'])
#     game_data['location'].append(game['location'])
#     game_data['team_win_percentage'].append(game['team_win_percentage'])
#     game_data['opponent_win_percentage'].append(game['opponent_win_percentage'])

# outcome_pred = []

# # Test each row
# for i in range(len(game_data['team'])):
#     team = game_data['team'][i]
#     opponent = game_data['opponent'][i]
#     location = game_data['location'][i]
#     team_win_percentage = game_data['team_win_percentage'][i]
#     opponent_win_percentage = game_data['opponent_win_percentage'][i]
    

#     prediction = predict(team, opponent, location, team_win_percentage, opponent_win_percentage)
#     pred_outcome = ""
#     if prediction < 0.5:
#         pred_outcome = 'L'
#     else:
#         pred_outcome = 'W'
    
#     outcome_pred.append(pred_outcome)

# # Extract the first 500 outcomes from game_list
# actual_outcomes = [game['outcome'] for game in game_list]
# # print(actual_outcomes)

# # Compare actual outcomes with predicted outcomes
# correct_predictions = sum(1 for actual, predicted in zip(actual_outcomes, outcome_pred) if actual == predicted)
# accuracy = correct_predictions / len(actual_outcomes) * 100

# print(f"Accuracy: {accuracy:.2f}%")


'''
    Test accuracy of ELO percentage
'''
# # Compare the actual outcomes with the predicted outcomes
# actual_outcomes = [game['outcome'] for game in game_list]
# correct_predictions = sum(1 for actual, predicted in zip(actual_outcomes, outcomes) if actual == predicted)
# accuracy = correct_predictions / len(outcomes) * 100

# print(f"Accuracy: {accuracy:.2f}%")