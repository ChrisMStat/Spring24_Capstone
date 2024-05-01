from flask import Flask, jsonify
import json


app = Flask(__name__)


# Load the precomputed rankings from the file
def load_rankings(file_path):
    with open(file_path, 'r') as file:
        rankings = json.load(file)
    return rankings


rankings = load_rankings('win_percentages.json')


@app.route('/rankings', methods=['GET'])
def get_rankings():
    # Serve the loaded rankings as JSON
    return jsonify(rankings)


if __name__ == '__main__':
    app.run(debug=True)