import json
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler, OneHotEncoder
from sklearn.compose import ColumnTransformer
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Dropout
from datetime import datetime
import numpy as np

# Load data
with open('Schedule-Formatted.json', 'r') as file:
    data = json.load(file)

# Prepare DataFrame team names and additional features
matches = []
for team in data['teams']:
    home_team = team['name']  # Home team
    league = team['league']
    for match in team['schedule']:
        if match['outcome']:  # Ignore games without outcomes
            timestamp = datetime.strptime(match['timestamp'], "%m/%d/%Y")
            month = timestamp.month
            matches.append({
                'home_team': home_team,
                'away_team': match['opponent'],  # Away team
                'league': league,
                'dayOfWeek': match['dayOfWeek'],
                'location': match['location'],
                'pointsScored': int(match['pointsScored']),
                'pointsAllowed': int(match['pointsAllowed']),
                'pointDifference': int(match['pointsScored']) - int(match['pointsAllowed']),
                'seasonTime': 'early' if month < 12 else ('mid' if month < 3 else 'late'),
                'outcome': 1 if match['outcome'] == 'W' else 0
            })
df = pd.DataFrame(matches)

# Define features and labels
features = ['home_team', 'away_team', 'league', 'dayOfWeek', 'location', 'pointsScored', 'pointsAllowed', 'pointDifference', 'seasonTime']
X = df[features]
y = df['outcome']

#####################################################

# Preprocessing steps
numerical_features = ['pointsScored', 'pointsAllowed', 'pointDifference']
categorical_features = ['home_team', 'away_team', 'league', 'dayOfWeek', 'location', 'seasonTime']

preprocessor = ColumnTransformer(
    transformers=[
        ('num', StandardScaler(), numerical_features),
        ('cat', OneHotEncoder(handle_unknown='ignore'), categorical_features)
    ])

# Split data
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
X_train, X_val, y_train, y_val = train_test_split(X_train, y_train, test_size=0.25, random_state=42)

# Apply preprocessing
X_train_transformed = preprocessor.fit_transform(X_train)
X_val_transformed = preprocessor.transform(X_val)
X_test_transformed = preprocessor.transform(X_test)

# Convert to dense arrays
X_train_transformed_dense = X_train_transformed.toarray()
X_val_transformed_dense = X_val_transformed.toarray()
X_test_transformed_dense = X_test_transformed.toarray()

# Model definition
model = Sequential([
    Dense(128, activation='relu', input_shape=[X_train_transformed_dense.shape[1]]),
    Dropout(0.5),
    Dense(64, activation='relu'),
    Dropout(0.5),
    Dense(1, activation='sigmoid')
])

model.compile(optimizer='adam', loss='binary_crossentropy', metrics=['accuracy'])

# Train the model
model.fit(X_train_transformed_dense, y_train, epochs=10, batch_size=32, validation_data=(X_val_transformed_dense, y_val))

# Evaluate the model
evaluation = model.evaluate(X_test_transformed_dense, y_test)
print(f'Test Loss: {evaluation[0]}, Test Accuracy: {evaluation[1]}')

# Predictions made
predictions_probabilities = model.predict(X_test_transformed_dense)
predictions = (predictions_probabilities > 0.5).astype(int)

# TeamB in the comparison DataFrame
X_test_with_predictions = X_test.reset_index(drop=True)
y_test_reset = y_test.reset_index(drop=True)
predictions_series = pd.Series(predictions.flatten(), name="Predicted Outcome")

comparison_df = pd.concat([X_test_with_predictions, y_test_reset, predictions_series], axis=1)
comparison_df['Actual Outcome'] = comparison_df['outcome']
comparison_df.drop('outcome', axis=1, inplace=True)

# Print prediction results
for index, row in comparison_df.iterrows():
    predicted_winner = "Predicted: Home Win" if row['Predicted Outcome'] == 1 else "Predicted: Home Lose"
    actual_winner = "Actual: Home Win" if row['Actual Outcome'] == 1 else "Actual: Home Lose"
    print(f"Match {index + 1}: {row['home_team']} vs {row['away_team']}, {predicted_winner}, {actual_winner}")