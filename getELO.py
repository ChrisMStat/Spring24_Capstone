import json
import random
from datetime import datetime
from collections import defaultdict

class EloCalculator:
    def __init__(self, filename):
        self.elo_ratings = defaultdict(lambda: 1500)
        self.hta = 40 # Home Team Advantage, can be optimized
        self.games = self._reformat_data_to_chronological(filename)

    def _update_elo(self, ra, rb, outcome):
        K = 60  # Hyper-parameter, max change in elo, can be optimized
        Ea = 1 / (1 + 10 ** ((rb - ra) / 400))
        result = 1 if outcome == 'W' else 0

        new_ra = ra + K * (result - Ea)
        new_rb = rb + K * ((1 - result) - (1 - Ea))

        return new_ra, new_rb

    def _calculate_win_probability(self, elo_a, elo_b):
        return 1 / (1 + 10 ** ((elo_b - elo_a) / 400))

    def _simulate_game_outcome(self, elo_a, elo_b):
        win_probability_a = self._calculate_win_probability(elo_a, elo_b)
        return random.random() < win_probability_a  # Returns True if team A wins, False otherwise

    def _reformat_data_to_chronological(self, filename):
        with open(filename, 'r') as file:
            data = json.load(file)

        games = []

        # Iterate through each team's schedule
        for team_data in data['teams']:
            for game in team_data['schedule']:
                # Skip games with no outcome or those marked as bye
                if 'outcome' not in game or game['location'] == 'BYE':
                    continue

                opponent = game['opponent']
                date = game['timestamp']
                game_info = {
                    'date': date,
                    'team': team_data['name'],
                    'opponent': opponent,
                    'outcome': game['outcome']
                }
                games.append(game_info)

        return games

    def print_team_win_percentages(self):
        for game in self.games:
            team_a = game['team']
            team_b = game['opponent']
            win_percentage_a = self.get_elo_win_percentage(team_a, team_b)
            win_percentage_b = 100 - win_percentage_a  # Opponent's win percentage
            print(f"Team: {team_a}, Win Percentage: {win_percentage_a:.2f}%")
            print(f"Opponent: {team_b}, Win Percentage: {win_percentage_b:.2f}%")
            print()
            
        print(len(self.games))

    def get_elo_win_percentage(self, team_a, team_b):
        elo_a = self.elo_ratings[team_a]
        elo_b = self.elo_ratings[team_b]
        win_probability_a = self._calculate_win_probability(elo_a + self.hta, elo_b)
        return win_probability_a * 100  # Return win percentage for team A


# Example usage:
if __name__ == "__main__":
    filename = 'Schedule-Formatted.json'
    elo_calculator = EloCalculator(filename)
    elo_calculator.print_team_win_percentages()


