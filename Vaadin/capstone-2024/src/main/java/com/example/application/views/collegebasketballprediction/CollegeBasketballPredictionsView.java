package com.example.application.views.collegebasketballprediction;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.accordion.Accordion;
import com.vaadin.flow.component.accordion.AccordionPanel;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
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
import java.io.InputStreamReader;
//new imports for http 
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.json.JSONArray;
import org.json.JSONException;

@PageTitle("College Basketball Predictions")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Uses(Icon.class)
public class CollegeBasketballPredictionsView extends Composite<VerticalLayout> {
 

    private List<Game> games; // list to hold all games
    private Grid<Game> grid; // the grid to display the games
    private static List<String> conferenceNames = new ArrayList<>(); // list to hold all conferences names
    private static JSONArray jsonArray; // this is what will contain the prediction results (see method initHttpConnectionAndLoadData())

    /**********************************
     *
     * CollegeBasketballPredictionsView()
     *
     * This is the main constructor.
     *
     *
     **********************************/

    public CollegeBasketballPredictionsView() {

        /********************************
         *
         * INITIAL PAGE SETUP
         *
         * General Page setup and formatting settings
         *
         ********************************/

        // page formatting
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");

        // LAYOUT: This is the row that contains the date, conference, and search bars
        HorizontalLayout layoutRow = new HorizontalLayout();
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");

        // HEADER: formatting and settings for the header
        H1 h1 = new H1();
        h1.setText("2022-2023 Games");
        layoutRow.setAlignSelf(FlexComponent.Alignment.END, h1);
        h1.setWidth("max-content");

        /********************************
         *
         * OPTION BOXES
         *
         * I.e. the Date Selector, the Conference Selector, and
         * the Search Bar for finding a specific team
         *
         ********************************/

        // DATE SELECTOR: formatting and settings for the date selector object
        DatePicker datePicker = new DatePicker();
        datePicker.setLabel("Select Date");
        datePicker.setWidth("min-content");
        datePicker.setValue(LocalDate.now());

        // CONFERENCE SELECTOR: formatting and settings for the conference dropdown selector object
        ComboBox comboBox = new ComboBox();
        comboBox.setLabel("Select Conference");
        comboBox.setWidth("min-content");

        // TEAM SEARCH BAR: formatting and settings for the conference dropdown selector object
        TextField textField = new TextField();
        textField.setLabel("Search for a Team");
        textField.setWidth("min-content");

        /********************************
         *
         * GAME GRID
         *
         * I.e. the settings for creating
         * the grid that displays all of the basketball games
         *
         ********************************/

        // GAME GRID: formatting and settings and initialization
        grid = new Grid();
        grid.setWidth("100%");
        grid.getStyle().set("flex-grow", "0");
        setGridData(grid); // Reads in data and adds to grid

        /********************************
         *
         * CONFERENCE NAMES
         *
         * Adds all the conference names from the .csv file to
         * the dropdown option box
         *
         * These names are found in the .csv. The .csv file is read
         * in the loadGamesFromCSV method which is called in the
         * setGridData method
         *
         ********************************/

        // CONFERENCE LIST: add list of conferences to combo box dropdown
        final List items = new ArrayList<>( conferenceNames );
        items.add( 0, "ALL" );
        comboBox.setItems( items );

        // default conference to option 1: 'ALL'
        comboBox.setValue(items.get(0));

        /********************************
         *
         * ACTION LISTENERS
         *
         * These are what "update" which games are displayed
         * based on the user-selected conference, date, etc.
         *
         ********************************/

        // ACTION LISTENERS: for date, conference, and team search
        datePicker.addValueChangeListener(event -> updateGridData(event.getValue(), comboBox.getValue().toString(), textField.getValue().toLowerCase()));
        comboBox.addValueChangeListener(event -> updateGridData(datePicker.getValue(), event.getValue().toString(),textField.getValue().toLowerCase()));
        textField.addValueChangeListener(event -> updateGridData(datePicker.getValue(), comboBox.getValue().toString(), event.getValue().toLowerCase()));

        /**********************************
         *
         * UPDATE GRID AND DISPLAY ENTIRE PAGE
         *
         * Update the page now that everything is created and loaded correctly.
         *
         * It will use default filter/option values.
         * I.e. it will use today's date, the 'ALL' conference option,
         * and the search bar is empty
         *
         *********************************/

        // Update data immediately to grab games for today
        updateGridData(datePicker.getValue(), comboBox.getValue().toString(), textField.getValue());

        // DISPLAY: Add and display everything to the page
        getContent().add(layoutRow);
        layoutRow.add(h1);
        layoutRow.add(datePicker);
        layoutRow.add(comboBox);
        layoutRow.add(textField);
        getContent().add(grid);
    }

    /**********************************
     *
     * initHttpConnectionAndLoadData()
     *
     * This method is what gets the prediction results
     * from api.py. To ensure this works as intended when running,
     * please see the README.md
     *
     **********************************/

    private void initHttpConnectionAndLoadData() {
            HttpURLConnection conn = null;
        try {
            final URL url = new URL("http://localhost:5000/rankings");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed: HTTP error code: " + conn.getResponseCode());
            }

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String output;
                while ((output = br.readLine()) != null) {
                    sb.append(output);
                }
            }
            // change JSONObject to JSONArray
            jsonArray = new JSONArray(sb.toString());
            Files.write(Paths.get("rankings.json"), sb.toString().getBytes());

            // DEBUGGING
            //System.out.println("JSON Array Response: " + jsonArray.toString());

        } catch (JSONException e) {
            System.out.println("Error processing JSON response: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O error: " + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**********************************
     *
     * setGridData()
     *
     * Create the Accordion Grid by reading data from the .csv file
     * This grid is what displays the basketball games and all of their
     * associated data (i.e. score, win percents, etc.)
     *
     **********************************/

    private void setGridData(Grid<Game> grid) {

        // calling function to get JSON OBJECT
        // this function is what gets the prediction results from api.py
        initHttpConnectionAndLoadData();

        // create columns and label headers
        grid.addColumn(new ComponentRenderer<>(game -> {
            Accordion accordion = new Accordion();
            AccordionPanel panel = accordion.add(game.getTeams(), new Span("Click to load details..."));
            accordion.close(); // Ensure the accordion starts closed

            accordion.addOpenedChangeListener(event -> {
                if (event.getOpenedPanel().isPresent() && event.getOpenedPanel().get() == panel) {

                    // calculate win percentage gradient
                    String gradientStyle = getGradientStyle(game.getPercents());

                    Span dateSpan = new Span("Date: " + game.getGameDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
                    Span winPercentSpan = new Span("Win Percentage: " + game.getPercents());
                    Span actualScore = new Span("Actual Score: " + game.getScores());

                    VerticalLayout detailsLayout = new VerticalLayout(dateSpan, winPercentSpan, actualScore);

                    // DEFAULT: This is for default color styling (i.e. plain background)
                    detailsLayout.getStyle().set("background", "default");

                    // COLOR: This is to color code the accordion drop down.
                    // It is currently coloring the entire accordion dropdown
                    //detailsLayout.getStyle().set("background", gradientStyle);

                    panel.setContent(detailsLayout);
                }
            });
            return accordion;
        })).setHeader("Game").setSortable(false).setKey("Teams");

        // try and load games from .csv file
        try {
            games = loadGamesFromCSV("Schedule.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // put games in grid
        grid.setItems(games);
    }

    /**********************************
     *
     * getGradientStyle()
     *
     * This is to color code the games for a
     * better user experience. It is NOT used when using
     * the default color styling in setGridData()
     *
     **********************************/

    private String getGradientStyle(String winPercents) {
        if (winPercents == null) return "";
        try {
            String[] percents = winPercents.split(" - ");
            double homeWinPercent = Double.parseDouble(percents[0].replace("%", ""));
            double visitorWinPercent = Double.parseDouble(percents[1].replace("%", ""));
            return String.format("linear-gradient(to right, red %f%%, blue %f%%)", homeWinPercent, visitorWinPercent);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            // Handle potential format errors
            e.printStackTrace();
            return "";
        }
    }

    /**********************************
     *
     * loadGamesFromCSV()
     *
     * load in .csv file to get the list of games
     * and all of their data/information
     *
     * This method also handles the JSON array parsing.
     * The JSON array containing ALL the prediction results is stepped
     * through to load in the correction prediction percentages for each game and team
     *
     **********************************/

    public static List<Game> loadGamesFromCSV(String filePath) throws IOException {

        // list for games
        List<Game> games = new ArrayList<>();

        // get csv file
        ClassPathResource resource = new ClassPathResource("Schedule.csv");
        InputStream inputStream = resource.getInputStream();

        /*
        *
        * The below code will try and read in each game from the csv file one-by-one to
        * get the needed information (date, team names, score, conference).
        * Then, for each game, the prediction JSON array (which has already been loaded in)
        * is checked to find matching team names with the currently read line from the csv file.
        * If a match is found, then the correct prediction results are passed along for that game,
        * and the next line of the csv is read in.
        *
        **/
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            String homeTeam = "";
            String conference = "";

            // read in each line of the csv one-by-one (each line is one game)
            while ((line = br.readLine()) != null) {

                String[] values = line.split(",");

                // Capture the home team and their conference
                if (values.length == 2) {
                    homeTeam = values[0];
                    conference = values[1];

                    // if the conferences does not already exist, add the new conference to the list
                    if (!conferenceNames.contains(conference))
                    {
                        conferenceNames.add(conference);
                    }
                }

                // Read lines that have game data (not byes), and where the home team is at home
                if ((values.length > 5) && (values[3].equals("vs."))) {
                    String awayTeam = values[4];
                    String homeScore = values[6]; // Grabbing final score
                    String awayScore = values[7]; // ^
                    String homeWinPercentage = "??%"; // Placeholder win percentage
                    String awayWinPercentage = "??%"; // ^
                    LocalDate gameDate = LocalDate.parse(values[1], DateTimeFormatter.ofPattern("MM/dd/yyyy"));

                    // Now, parse through the JSON array containing the prediction results to find this game's predictions
                    for (int i = 0; i < jsonArray.length(); i++) {

                        // get one game from the array corresponding to the current index, i
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        // the currently selected team name (i.e. home team)
                        String team = jsonObject.getString("team");

                        // the currently selected opponent (i.e. away team)
                        String opponent = jsonObject.getString("opponent");

                        // the corresponding prediction result for the home team
                        String win_percentage = jsonObject.getString("win_percentage");

                        // the corresponding prediction result for the away team
                        String opponent_win_percentage = jsonObject.getString("opponent_win_percentage");

                        // check that the currently selected teams from the JSON array are the SAME teams as the currently
                        // selected game from the csv
                        if (homeTeam.equals(team) && awayTeam.equals(opponent))
                        {
                            // DEBUGGING
                            //System.out.println("\nSUCCESS IF.... homeTeam = " + homeTeam + " ; current team = " + team + " ; awayTeam = " + awayTeam + " ; opponent = " + opponent + "\n");

                            // sets the predicted results for each team
                            homeWinPercentage = win_percentage;
                            awayWinPercentage = opponent_win_percentage;
                            break; // we have found a match (i.e. the teams/games from the JSON array and csv are the same)
                        }
                    }

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

    /**********************************
     *
     * updateGridData()
     *
     * update the games in the grid based on the
     * date and selected conference
     *
     **********************************/

    private void updateGridData(LocalDate selectedDate, String conference, String search) {

        // if the search bar contains any text
        if (search != null)
        {
            // if a date and conference have both been selected
            if (selectedDate != null && conference != null) {

                // a conference other than "All" has been selected
                if (!"ALL".equalsIgnoreCase( conference ))
                {
                    List<Game> filteredGames = games.stream()
                            .filter(game -> (game.getGameDate().equals(selectedDate) && (game.getConference().contains(conference)) && ((game.getHomeTeam().toLowerCase().contains(search)) || (game.getAwayTeam().toLowerCase().contains(search)))))
                            .collect(Collectors.toList());
                    grid.setItems(filteredGames);
                }

                // otherwise just grab the games on the specified date
                else
                {
                    List<Game> filteredGames = games.stream()
                            .filter(game -> (game.getGameDate().equals(selectedDate)) && ((game.getHomeTeam().toLowerCase().contains(search)) || (game.getAwayTeam().toLowerCase().contains(search))))
                            .collect(Collectors.toList());
                    grid.setItems(filteredGames);
                }
            } else {
                grid.setItems((Game) null); // Clear the grid or handle a null date as needed
            }
        }
        else {

            // Grab the sublist of games from the selected date and conference
            if (selectedDate != null && conference != null) {

                // a conference other than "All" has been selected
                if (!"ALL".equalsIgnoreCase( conference ))
                {
                    List<Game> filteredGames = games.stream()
                            .filter(game -> (game.getGameDate().equals(selectedDate) && (game.getConference().contains(conference))))
                            .collect(Collectors.toList());
                    grid.setItems(filteredGames);
                }

                // otherwise just grab the games on the specified date
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
}