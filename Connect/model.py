import json
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import accuracy_score

def parse_json(json_file_path):
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

    return game_list

def train_model(game_list):
    # Convert your game_list into a DataFrame
    df = pd.DataFrame(game_list)

    # Map location values to numerical format
    location_mapping = {'@': 1, 'N': 5, '.vs': 10}
    df['location'] = df['location'].map(location_mapping)

    # Map outcome values to numerical format
    outcome_mapping = {'W': 1, 'L': 0}
    df['outcome'] = df['outcome'].map(outcome_mapping)

    # Define X and y
    X = df[['game_differential', 'location']]
    y = df['outcome']

    # Split the data into training and testing sets
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

    # Initialize and train the logistic regression model
    model = LogisticRegression()
    model.fit(X_train, y_train)

    # Make predictions
    y_pred = model.predict(X_test)

    # Calculate accuracy
    accuracy = accuracy_score(y_test, y_pred)

    return model, accuracy

def predict(model, new_instance_data):
    # Format the new instance data
    new_instance_df = pd.DataFrame([new_instance_data])

    # Map location value to numerical format
    location_mapping = {'@': 1, 'N': 5, '.vs': 10}
    new_instance_df['location'] = new_instance_df['location'].map(location_mapping)

    # Extract features for prediction
    new_instance_X = new_instance_df[['game_differential', 'location']]

    # Make prediction
    prediction = model.predict(new_instance_X)

    return prediction
