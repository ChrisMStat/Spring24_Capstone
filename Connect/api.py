from flask import Flask, request, jsonify
from model import parse_json, train_model, predict

app = Flask(__name__)

# Parse JSON data and train model
game_list = parse_json('Schedule-Formatted.json')
model, accuracy = train_model(game_list)
print(f"Model Accuracy: {accuracy:.2f}")

@app.route('/predict', methods=['POST'])
def predict_route():
    data = request.json
    prediction = predict(model, data)
    return jsonify({'prediction': prediction.tolist()})

if __name__ == '__main__':
    app.run(debug=True)
