package com.example.application.views.collegebasketballprediction;

import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;
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
import java.io.InputStreamReader;

@PageTitle("College Basketball Predictions")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Uses(Icon.class)
public class CollegeBasketballPredictionsView extends Composite<VerticalLayout> {
    private List<Game> games;
    private Grid<Game> grid;

    public CollegeBasketballPredictionsView() {
        HorizontalLayout layoutRow = new HorizontalLayout();
        DatePicker datePicker = new DatePicker();
        ComboBox comboBox = new ComboBox();
        H1 h1 = new H1();
        grid = new Grid();

        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");

        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");

        datePicker.setLabel("Date picker");
        datePicker.setWidth("min-content");
        datePicker.setValue(LocalDate.now());
        datePicker.addValueChangeListener(event -> updateGridData(event.getValue()));

        comboBox.setLabel("Combo Box");
        comboBox.setWidth("min-content");
        setComboBoxData(comboBox);

        h1.setText("Games");
        layoutRow.setAlignSelf(FlexComponent.Alignment.END, h1);
        h1.setWidth("max-content");

        grid.setWidth("100%");
        grid.getStyle().set("flex-grow", "0");
        setGridData(grid); // Reads in data and adds to grid
        updateGridData(LocalDate.now()); // Update data immediately to grab games for today

        getContent().add(layoutRow);
        layoutRow.add(datePicker);
        layoutRow.add(comboBox);
        layoutRow.add(h1);

        getContent().add(grid);
    }

    private void setComboBoxData(ComboBox comboBox) {

        List<String> items = new ArrayList<>(
                Arrays.asList("All", "American East", "American Athletic", "Atlantic 10",
                        "Atlantic Coast", "Atlantic Sun", "Big 12", "Big East", "Big Sky",
                        "Big South", "Big Ten", "Big West", "Colonial Athletic", "Conference USA",
                        "Horizon", "Independents (DI)", "Ivy League", "Metro Atlantic Athletic",
                        "Mid-American", "Mid-Eastern", "Missouri Valley", "Mountain West",
                        "Northeast", "Ohio Valley", "Pac-12", "Patriot League", "Southeastern",
                        "Southern", "Southland", "Southwestern Athletic", "Summit League",
                        "Sun Belt", "West Coast", "Western Athletic"));

        comboBox.setItems(items);
        comboBox.setValue(items.get(0));
    }

    private void setGridData(Grid<Game> grid) {

        // Have some data
        /*
         * List<Game> games = Arrays.asList(
         * new Game("Oklahoma", "Oklahoma State", "89", "102", "35%", "65%", null),
         * new Game("Iowa State", "Cincinnati", "112", "101", "75%", "25%", null),
         * new Game("Kansas", "Baylor", "134", "99", "82%", "18%", null));
         * 
         * grid.addColumn(Game::getTeams).setHeader("Game").setSortable(false).setKey(
         * "Teams");
         * grid.addColumn(Game::getScores).setHeader("Score (Home - Visitor)").
         * setSortable(false).setKey("Scores");
         * grid.addColumn(Game::getPercents).setHeader("Win % (Home - Visitor)").
         * setSortable(false).setKey("Percent");
         */
        grid.addColumn(Game::getTeams).setHeader("Game").setSortable(false).setKey("Teams");
        grid.addColumn(Game::getScores).setHeader("Score (Home - Visitor)").setSortable(false).setKey("Scores");
        grid.addColumn(Game::getPercents).setHeader("Win % (Home - Visitor)").setSortable(false).setKey("Percent");

        try {
            games = loadGamesFromCSV("Schedule.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("here: " + games.size()); // debug check size of games list

        grid.setItems(games);

        // end::snippet[]
    }

    public static List<Game> loadGamesFromCSV(String filePath) throws IOException {
        List<Game> games = new ArrayList<>();

        ClassPathResource resource = new ClassPathResource("Schedule.csv");
        InputStream inputStream = resource.getInputStream();

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
                            gameDate));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return games;
    }

    private void updateGridData(LocalDate selectedDate) {
        // System.out.println("here: " + selectedDate); // debug

        // Grab the sublist of games from the selected date
        if (selectedDate != null) {
            List<Game> filteredGames = games.stream()
                    .filter(game -> game.getGameDate().equals(selectedDate))
                    .collect(Collectors.toList());
            grid.setItems(filteredGames);
        } else {
            grid.setItems((Game) null); // Clear the grid or handle a null date as needed
        }
    }

}
