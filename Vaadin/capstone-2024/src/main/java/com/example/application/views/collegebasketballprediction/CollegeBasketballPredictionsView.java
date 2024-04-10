package com.example.application.views.collegebasketballprediction;

import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;

import com.example.application.services.HttpClient;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.core.io.ClassPathResource;
import java.io.InputStream;



//NEW IMPORT 
import com.vaadin.flow.component.textfield.TextArea;
import java.util.concurrent.CompletableFuture;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;

//NEW IMPORT FOR fetchDataAndDisplayJson
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.scene.control.TextArea; // For JavaFX


@PageTitle("College Basketball Predictions")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Uses(Icon.class)
public class CollegeBasketballPredictionsView extends Composite<VerticalLayout> {

    private HttpClient httpClient; //here for the service req
    private TextArea jsonOutput; 

    private List<Game> games; // list to hold all games
    private Grid<Game> grid; // the grid to display the games

    @Autowired
    public CollegeBasketballPredictionsView(HttpClient httpClient) { //passing in the defined service
         this.httpClient = httpClient; //declaring for use in this class
         // Initialization of jsonOutput
        jsonOutput = new TextArea("JSON Output");
        jsonOutput.setWidth("100%");
        jsonOutput.setHeight("300px"); // Adjust the height as needed
        getContent().add(jsonOutput); // Add the TextArea to your layout




        //Testing the text layout NEW
        // Create a button and add it to the layout
        Button showInfoButton = new Button("Show Info");
        getContent().add(showInfoButton); // Add the button to your main layout

        // Button click listener
        showInfoButton.addClickListener(event -> {
           try {
        // Attempt to fetch data and display it
        fetchDataAndDisplayJson(jsonOutput);
          } catch (Exception e) {
          // If fetching data fails, display an error message
        jsonOutput.setValue("Error displaying JSON");
       }
        }); //new button end


        // general page formatting
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");

        // This is the row that contains the date, conference, and search bars
        HorizontalLayout layoutRow = new HorizontalLayout();
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");

        // HEADER: formatting and settings for the header
        H1 h1 = new H1();
        h1.setText("Games");
        layoutRow.setAlignSelf(FlexComponent.Alignment.END, h1);
        h1.setWidth("max-content");

        // DATE SELECTOR: formatting and settings for the date selector object
        DatePicker datePicker = new DatePicker();
        datePicker.setLabel("Select Date");
        datePicker.setWidth("min-content");
        datePicker.setValue(LocalDate.now());
        //datePicker.addValueChangeListener(event -> updateGridData(event.getValue()));

        // CONFERENCE SELECTOR: formatting and settings for the conference dropdown selector object
        ComboBox comboBox = new ComboBox();
        comboBox.setLabel("Select Conference");
        comboBox.setWidth("min-content");
        setComboBoxData(comboBox); // Call another method to fill in the information for the conferences

        // TEAM SEARCH BAR: formatting and settings for the conference dropdown selector object
        TextField textField = new TextField();
        textField.setLabel("Search for a Team");
        textField.setWidth("min-content");


        // action listeners for date, conference, and team search
        datePicker.addValueChangeListener(event -> updateGridData(event.getValue(), comboBox.getValue().toString()));
        comboBox.addValueChangeListener(event -> updateGridData(datePicker.getValue(), event.getValue().toString()));
        //textField.addValueChangeListener(event -> updateGridData(datePicker.getValue(), event.getValue().toString(), textField.getValue().toString()));



        // GAME GRID: formatting and settings and initialization
        grid = new Grid();
        grid.setWidth("100%");
        grid.getStyle().set("flex-grow", "0");
        setGridData(grid); // Reads in data and adds to grid
        updateGridData(LocalDate.now(), comboBox.getValue().toString()); // Update data immediately to grab games for today

        // Add and display everything to the page
        getContent().add(layoutRow);
        layoutRow.add(h1);
        layoutRow.add(datePicker);
        layoutRow.add(comboBox);
        layoutRow.add(textField);
        getContent().add(grid);
    }

    //  private void fetchDataAndDisplayJson(TextArea jsonOutput) {
    //  try {
    //     CompletableFuture<String> jsonFuture = httpClient.fetchDataFromFlask("https://legendary-umbrella-4pgvr4vxqqwf5x9-5000.app.github.dev/rankings");
    //     jsonFuture.thenAccept(json -> UI.getCurrent().access(() -> jsonOutput.setValue(json)))
    //               .exceptionally(ex -> {
    //                   UI.getCurrent().access(() -> jsonOutput.setValue("Failed to fetch data: " + ex.getMessage()));
    //                   return null;
    //               });
    // } catch (Exception e) {
    //     UI.getCurrent().access(() -> jsonOutput.setValue("Failed to initiate data fetch: " + e.getMessage()));
    // }
    //     } //method for connection
        private void fetchDataAndDisplayJson( final TextArea jsonOutput )	{
    		//CompletableFuture<String> jsonFuture = httpClient.fetchDataFromFlask("https://legendary-umbrella-4pgvr4vxqqwf5x9-5000.app.github.dev/rankings");
            //jsonFuture.thenAccept(json -> UI.getCurrent().access(() -> jsonOutput.setValue(json)))
            //          .exceptionally(ex -> {
            //              UI.getCurrent().access(() -> jsonOutput.setValue("Failed to fetch data: " + ex.getMessage()));
            //              return null;
            //          });
    		HttpURLConnection conn = null;
    		try {
    			final URL url = new URL( "http://localhost:5000/rankings" );
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod( "GET" );
                conn.setRequestProperty( "Accept", "application/json" );
    
                if( conn.getResponseCode() != 200 ) {
                    throw new RuntimeException( "Failed : HTTP error code : "
                            + conn.getResponseCode() );
                }
    
    			final StringBuilder sb = new StringBuilder();
                try ( final BufferedReader br = new BufferedReader( 
    					new InputStreamReader( conn.getInputStream() ) ) ) {
    				String output;
    				while( (output = br.readLine() ) != null ) {
    					sb.append( output );
    				}
    				jsonOutput.setValue( sb.toString() );
    			}
    			catch( final Exception e ) {
    				jsonOutput.setValue( "Failed to initiate data fetch: " + e.getMessage() );
    				e.printStackTrace();
    			}
            }
    		catch( final Exception e ) {
    			jsonOutput.setValue( "Failed to initiate data fetch: " + e.getMessage() );
                e.printStackTrace();
            }
    		finally {
    			if( conn != null ) {
    				conn.disconnect();
    			}
    		}
        }



    /**
     *
     * Method to fill in the information for the conferences
     * dropdown selector box
     *
     **/
    private void setComboBoxData(ComboBox comboBox) {

        // List of all conferences
        List<String> items = new ArrayList<>(
                Arrays.asList("All", "American East", "American Athletic", "A-10",
                        "Atlantic Coast", "Atlantic Sun", "Big 12", "Big East", "Big Sky",
                        "Big South", "Big Ten", "Big West", "Colonial Athletic", "Conference USA",
                        "Horizon", "Independents (DI)", "Ivy League", "Metro Atlantic Athletic",
                        "Mid-American", "Mid-Eastern", "Missouri Valley", "Mountain West",
                        "Northeast", "Ohio Valley", "Pac-12", "Patriot League", "Southeastern",
                        "Southern", "Southland", "Southwestern Athletic", "Summit League",
                        "Sun Belt", "West Coast", "Western Athletic"));

        // add all conferences to box
        comboBox.setItems(items);

        // default to option 1: 'ALL' conferences
        comboBox.setValue(items.get(0));
    }

    /**
     *
     * Method to create game grid
     *
     **/
    private void setGridData(Grid<Game> grid) {

        // create columns and label headers
        grid.addColumn(Game::getTeams).setHeader("Game").setSortable(false).setKey("Teams");
        grid.addColumn(Game::getScores).setHeader("Score (Home - Visitor)").setSortable(false).setKey("Scores");
        grid.addColumn(Game::getPercents).setHeader("Win % (Home - Visitor)").setSortable(false).setKey("Percent");

        // try and get games from .csv file
        try {
            games = loadGamesFromCSV("Schedule.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("here: " + games.size()); // debug check size of games list

        // put games in grid
        grid.setItems(games);

    }

    /**
     *
     * Method to load in .csv file to get the list of games
     * and all of their data/information
     *
     *
     **/
    public static List<Game> loadGamesFromCSV(String filePath) throws IOException {

        // list for games
        List<Game> games = new ArrayList<>();

        // get file
        ClassPathResource resource = new ClassPathResource("Schedule.csv");
        InputStream inputStream = resource.getInputStream();

        // try and get the data for each game
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            String homeTeam = "";
            String conference = "";
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                // Capture the home team and their conference
                if (values.length == 2) {
                    homeTeam = values[0];
                    conference = values[1]; // Not using this right now, can check against the combobox
                }

                // Read lines that have game data (not byes), and where the home team is at home
                if ((values.length > 5) && (values[3].equals("vs."))) {
                    String awayTeam = values[4];
                    String homeScore = values[6]; // Grabbing final score
                    String awayScore = values[7]; // ^
                    String homeWinPercentage = "50%"; // Placeholder win percentage
                    String awayWinPercentage = "50%"; // ^
                    LocalDate gameDate = LocalDate.parse(values[1], DateTimeFormatter.ofPattern("MM/dd/yyyy"));

                    // Create Game object and add to games list
                    games.add(new Game(homeTeam, awayTeam, homeScore, awayScore, homeWinPercentage, awayWinPercentage,
                            gameDate, conference));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // return the full list of games
        return games;
    }


    /**
     *
     * Method to update the games in the grid based on the
     * date and selected conference
     *
     *
     **/
    private void updateGridData(LocalDate selectedDate, String conference) {
        // System.out.println("here: " + selectedDate); // debug

        System.out.println("here: " + selectedDate + ", " + conference); // debug



        // Grab the sublist of games from the selected date
        if (selectedDate != null && conference != null) {

            if (conference != "All")
            {
                List<Game> filteredGames = games.stream()
                        .filter(game -> (game.getGameDate().equals(selectedDate) && (game.getConference().contains(conference))))
                                .collect(Collectors.toList());
                grid.setItems(filteredGames);
            }
            else
            {
                List<Game> filteredGames = games.stream()
                        .filter(game -> game.getGameDate().equals(selectedDate))
                        .collect(Collectors.toList());
                grid.setItems(filteredGames);
            }
        } else {
            grid.setItems((Game) null); // Clear the grid or handle a null date as needed
        }
    }

}
