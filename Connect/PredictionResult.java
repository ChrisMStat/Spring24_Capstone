import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PredictionService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public PredictionResult predictGameOutcome(PredictionRequest request) throws Exception {
        String requestBody = objectMapper.writeValueAsString(request);
        
        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:5000/predict"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build();

        HttpResponse<String> response = httpClient.send(httpRequest, BodyHandlers.ofString());
        
        return objectMapper.readValue(response.body(), PredictionResult.class);
    }
    
   public static class PredictionRequest {
        private int game_differential;
        private String location; 

        public PredictionRequest(int game_differential, String location) {
            this.game_differential = game_differential;
            this.location = location;
        }

        // Getters and setters
        public int getGame_differential() {
            return game_differential;
        }

        public void setGame_differential(int game_differential) {
            this.game_differential = game_differential;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }
    
    public static class PredictionResult {
        private int[] prediction; 

        // Getters and setters
        public int[] getPrediction() {
            return prediction;
        }

        public void setPrediction(int[] prediction) {
            this.prediction = prediction;
        }
    }
}

//NEEDED FOR CollegeBasketballPredictionsView.java
// import com.vaadin.flow.component.button.Button;
// import com.vaadin.flow.component.notification.Notification;
// import com.example.application.services.PredictionService.PredictionRequest;
// import com.example.application.services.PredictionService.PredictionResult;

//  private void predictOutcome() {
//         try {
//             // Example data, replace with actual game data retrieval
//             int gameDifferential = 10; // This should come from your UI or game selection
//             String location = "@"; // This should come from your UI or game selection

//             PredictionRequest request = new PredictionRequest(gameDifferential, location);
            
//             PredictionService predictionService = new PredictionService();
//             PredictionResult result = predictionService.predictGameOutcome(request);
            
//             // Display the prediction result
//             showPredictionResult(result.getPrediction());
//         } catch (Exception e) {
//             Notification.show("Error making prediction: " + e.getMessage());
//         }
//     }
//     private void showPredictionResult(int prediction) {
//         String outcome = prediction == 1 ? "Win" : "Loss";
//         Notification.show("Predicted Outcome: " + outcome, 3000, Notification.Position.MIDDLE);
//     }   
//          Button predictButton = new Button("Predict Outcome");
//         predictButton.addClickListener(e -> predictOutcome());
//         getContent().add(predictButton)