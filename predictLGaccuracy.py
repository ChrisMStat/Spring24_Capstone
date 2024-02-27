import json

json_file_path = 'Schedule-Formatted.json'

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

for team in new_data:
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
        if game['location'] != "BYE":
            location = game['location']
            opponent = game['opponent']
            outcome = game['outcome']
            ptsScored = game['pointsScored']
            ptsAllowed = game['pointsAllowed']

            # Calculate the differential for this game
            game_differential = int(ptsScored) - int(ptsAllowed)

            # Add game information to the team's schedule list, including the differential for this game
            game_info = {
                "team": team_name,
                "win_percentage": win_percentage,
                "average_points_scored": average_points_scored,  # Adding the average points scored per game
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

# Print the resulting game_list
# for game in game_list:
#     print(game)


##########################################################################################################
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LinearRegression
from sklearn.metrics import accuracy_score

def predict(team, opponent, location):
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

    X_columns = ['game_differential', 'location']
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

    # print("Accuracy:", accuracy)
    # print(y_pred_rounded)
    # print(y_test)

    # Define a list of feature values for a new instance
    new_instance = [[differential_avg, location_val]]


    # Predict the outcomes for the new instances
    predicted_outcomes = regr.predict(new_instance)

    #print("Predicted outcomes for new instances:", predicted_outcomes)
    return predicted_outcomes





############################################################################################################
def predict_game_result(team, opponent, location):
    
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

    if not team_exist and not opponent_exist:
        return "Cannot predict this game because of lack of data for these two teams"
    elif team_exist and not opponent_exist:
        return f"{team} wins the game."
    elif not team_exist and opponent_exist:
        return f"{opponent} wins the game."
    else:
        result = predict(team, opponent, location)
        if result < 0.5:
            #return f"{opponent} wins the game."
            return "L"
        else:
            #return f"{team} wins the game."
            return "W"


# # Example usage:
# team_name = input("Enter team's name: ")
# opponent_name = input("Enter opponent's name: ")
# game_location = input("Enter location (type @ for away, .vs for home, and N for neutral location): ")

# game_prediction = predict_game_result(team_name, opponent_name, game_location)
# print(game_prediction)


      
#############################################################################################################

# Create a dictionary to hold columns from game_list
game_data = {
    'team': [],
    'opponent': [],
    'location': []
}

# Populate the dictionary with data from the first 500 rows of game_list
for game in game_list[:500]:
    game_data['team'].append(game['team'])
    game_data['opponent'].append(game['opponent'])
    game_data['location'].append(game['location'])

outcome_pred = []

# Test each row
for i in range(len(game_data['team'])):
    team = game_data['team'][i]
    opponent = game_data['opponent'][i]
    location = game_data['location'][i]

    prediction = predict_game_result(team, opponent, location)
    outcome_pred.append(prediction)
    #print(f"Prediction for row {i + 1}: {prediction}")

# Extract the first 500 outcomes from game_list
actual_outcomes = [game['outcome'] for game in game_list[:500]]

# Compare actual outcomes with predicted outcomes
correct_predictions = sum(1 for actual, predicted in zip(actual_outcomes, outcome_pred) if actual == predicted)
accuracy = correct_predictions / len(actual_outcomes) * 100

print(f"Accuracy: {accuracy:.2f}%")


##########################################################################################################################

# import random

# # Randomly select 200 rows from game_list
# random_games = random.sample(game_list, 700)

# # Create a dictionary to hold columns from random_games
# game_data = {
#     'team': [],
#     'opponent': [],
#     'location': []
# }

# # Populate the dictionary with data from the randomly selected rows
# for game in random_games:
#     game_data['team'].append(game['team'])
#     game_data['opponent'].append(game['opponent'])
#     game_data['location'].append(game['location'])

# outcome_pred = []

# # Test each row
# for i in range(len(game_data['team'])):
#     team = game_data['team'][i]
#     opponent = game_data['opponent'][i]
#     location = game_data['location'][i]

#     prediction = predict_game_result(team, opponent, location)
#     outcome_pred.append(prediction)
#     #print(f"Prediction for row {i + 1}: {prediction}")

# # Extract the outcomes for the randomly selected games
# actual_outcomes = [game['outcome'] for game in random_games]

# # Compare actual outcomes with predicted outcomes
# correct_predictions = sum(1 for actual, predicted in zip(actual_outcomes, outcome_pred) if actual == predicted)
# accuracy = correct_predictions / len(actual_outcomes) * 100

# print(f"Accuracy: {accuracy:.2f}%")
            
            