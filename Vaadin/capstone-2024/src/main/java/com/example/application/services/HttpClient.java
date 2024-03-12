import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.concurrent.CompletableFuture;

@Service
public class HttpClient { // Class name corrected to follow Java naming conventions

    private final RestTemplate restTemplate;

    public HttpClient() { // Constructor name corrected to match the class name
        this.restTemplate = new RestTemplate();
    }

    public CompletableFuture<String> fetchDataFromFlask(String url) {
        return CompletableFuture.supplyAsync(() -> restTemplate.getForObject(url, String.class));
    }
}