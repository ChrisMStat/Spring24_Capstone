package com.example.application.views.collegebasketballprediction;

import com.example.application.data.SamplePerson;
import com.example.application.services.SamplePersonService;
import com.example.application.views.MainLayout;
//import com.helger.commons.csv.CSVParser;
//import com.helger.commons.csv.CSVReader;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.util.SharedUtil;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import com.vaadin.flow.theme.lumo.LumoUtility.Gap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvException;

//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;

@PageTitle("College Basketball Predictions")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Uses(Icon.class)
public class CollegeBasketballPredictionsView extends Composite<VerticalLayout> {

    // grid for displaying games
    Grid<String[]> grid;

    public CollegeBasketballPredictionsView() {

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

        // CONFERENCE SELECTOR: formatting and settings for the conference dropdown selector object
        ComboBox comboBox = new ComboBox();
        comboBox.setLabel("Select Conference");
        comboBox.setWidth("min-content");
        // call another method to fill in the information for the conferences
        setComboBoxData(comboBox);

        // TEAM SEARCH BAR: formatting and settings for the conference dropdown selector object
        TextField textField = new TextField();
        textField.setLabel("Search for a Team");
        textField.setWidth("min-content");

        // GAME GRID: grid for displaying games
        //Grid<Game> grid = new Grid(); // old code; please delete eventually
        grid = new Grid();
        grid.setWidth("100%");
        grid.getStyle().set("flex-grow", "0");
        //setGridData(grid); // old code; eventually delete along with method eventually

        // gets .csv file and loads it into grid
        readFromClassPath();

        // add everything to the page
        getContent().add(layoutRow);
        layoutRow.add(datePicker);
        layoutRow.add(comboBox);
        layoutRow.add(textField);
        layoutRow.add(h1);
        getContent().add(grid);
    }


    /**
     *
     * Method to fill in the information for the conferences
     * dropdown selector box
     *
     */
    private void setComboBoxData(ComboBox comboBox) {

        // List of all conferences
        List<String> items = new ArrayList<>(
                Arrays.asList("All", "American East", "American Athletic", "Atlantic 10",
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
     * POSSIBLY AN OUTDATED METHOD
     * IF SO, PLEASE DELETE EVENTUALLY
     *
     */
    //private void setGridData(Grid<Game> grid) {
    private void setGridData(Grid<String[]> grid) {


/*
        // Have some data
        List<Game> games = Arrays.asList(
                new Game("Oklahoma", "Oklahoma State", "89", "102", "35%", "65%"),
                new Game("Iowa State", "Cincinnati", "112", "101", "75%", "25%"),
                new Game("Kansas", "Baylor", "134", "99", "82%", "18%"));

        grid.addColumn(Game::getTeams).setHeader("Game").setSortable(false).setKey("Teams");
        grid.addColumn(Game::getScores).setHeader("Score (Home - Visitor)").setSortable(false).setKey("Scores");
        grid.addColumn(Game::getPercents).setHeader("Win % (Home - Visitor)").setSortable(false).setKey("Percent");

        grid.setItems(games);

 */
/*
    CSVImport data = new CSVImport();
    List<String[]> games = data.getAllGames();

    String[] headers = games.get(0);

    grid.removeAllColumns();

        // headers
        for (int i = 0; i < headers.length; i++) {
            int colIndex = i;
            grid.addColumn(row -> row[colIndex])
                    .setHeader(SharedUtil.camelCaseToHumanFriendly(headers[colIndex]));
        }

        //grid.setItems(games.subList(1, games.size()).toString());
        grid.setItems(games.subList(1, games.size()));

 */



/*
        InputStreamReader csvFileReader = new InputStreamReader(
                getClass().getResourceAsStream("/Users/chrisstatton/Documents/School/Capstone/Schedule.csv"),
                StandardCharsets.UTF_8
        );

        CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
        CSVReader reader = new CSVReaderBuilder(csvFileReader).withCSVParser(parser).build();

        //Grid<String[]> grid = new Grid<>();
        try {
            List<String> entries = reader.readAll();
            // Assume the first row contains headers
            //String headers = entries.get(0);

            // Setup a grid with random data
            for (int i = 0; i < entries.length; i++) {
                final int columnIndex = i;
                //String header = headers[i];
                //String humanReadableHeader = SharedUtil.camelCaseToHumanFriendly(header);
                //grid.addColumn(str -> str[columnIndex]).setHeader(humanReadableHeader);
                grid.addColumn();
            }
            grid.setItems(entries.subList(1, entries.size()));
            //add(grid);
        } catch (IOException | CsvException e) {
            grid.addColumn(nop -> "Unable to load CSV: " + e.getMessage()).setHeader("Failed to import CSV file");
        }
*/


        // end::snippet[]
    }


    /**
     *
     * Method to read in the .csv file
     * for the games schedule/list
     *
     */
    private void readFromClassPath()
    {
        displayCsv(getClass().getClassLoader().getResourceAsStream("Schedule.csv"));
    }


    /**
     *
     * Method for actually displaying the .csv data
     * in a grid object
     *
     */
    private void displayCsv(InputStream resourceAsStream)
    {
        var parser = new CSVParserBuilder().withSeparator(',').build();
        var reader = new CSVReaderBuilder(new InputStreamReader(resourceAsStream)).withCSVParser(parser).build();

        try {
            var entries = reader.readAll();

            //var headers = entries.get(0);

            List<String> top = Arrays.asList(
                    new String("Value"),
                    new String("Date"),
                    new String("Day"),
                    new String("Type"),
                    new String("Name"),
                    new String("Win/Loss"),
                    new String("Scored"),
                    new String("Allowed"));

            for (int i = 0; i < top.size(); i++) {
                int colIndex = i;
                grid.addColumn(row -> row[colIndex]).setHeader(top.get(colIndex));
            }



            grid.setItems(entries.subList(1, entries.size()));

        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
    }


}
