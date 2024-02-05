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
    #league = team['league']
    
    # Extracting schedule information
    team_schedule = []
    for game in team['schedule']:
        if game['location'] != "BYE":
            #timestamp = game['timestamp']
            location = game['location']
            opponent = game['opponent']
            outcome = game['outcome']
            ptsScored = game['pointsScored']
            ptsAllowed = game['pointsAllowed']

            # Add game information to the team's schedule list
            game_info = {
                "team": team_name,
                #"league": league,
                #"timestamp": timestamp,
                "location": location,
                "opponent": opponent,
                "outcome": outcome,
                "ptsScored": ptsScored,
                "ptsAllowed": ptsAllowed
            }
            team_schedule.append(game_info)

    # Add team information to the overall list
    game_list.extend(team_schedule)

# Print the resulting list
# for game in game_list:
#     print(game)


###################################################################################
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.neighbors import KNeighborsClassifier

# Convert your game_list into a DataFrame
df = pd.DataFrame(game_list)

# Convert categorical variables into numerical format
df = pd.get_dummies(df, columns=['team', 'location', 'opponent'])

# Split the data into features (X) and target variable (y)
X = df.drop(['outcome'], axis=1)
y = df['outcome']

# Split the data into training and testing sets
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

# Scale the features using StandardScaler
scaler = StandardScaler()
X_train_scaled = scaler.fit_transform(X_train)
X_test_scaled = scaler.transform(X_test)

# Initialize the KNeighborsClassifier
knn = KNeighborsClassifier(n_neighbors=10)
knn.fit(X_train_scaled, y_train)

# Create a DataFrame for the new sample
new_instance_data = {'team': ['Stonehill Skyhawks'], 'location': ['N'], 'opponent': ['Central Connecticut Blue Devils'], "ptsScored": 0, "ptsAllowed": 0}

# Convert to DataFrame
new_instance_df = pd.DataFrame(new_instance_data)

# Perform one-hot encoding using the same columns as in the training data
new_instance_df = pd.get_dummies(new_instance_df, columns=['team', 'location', 'opponent'])

# Format the sample test data
X_train_df = pd.DataFrame(X_train_scaled, columns=X_train.columns) # Create a DataFrame from X_train_scaled
new_instance_df = new_instance_df.reindex(columns=X_train_df.columns, fill_value=0) # Ensure the columns match the order in X_train

# Make predictions
y_pred = knn.predict(new_instance_df)

# Print the predicted outcome
print(f'The predicted outcome for the game is: {y_pred}')



#***Source
#https://www.datacamp.com/tutorial/k-nearest-neighbor-classification-scikit-learn