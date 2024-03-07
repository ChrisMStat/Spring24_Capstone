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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

@PageTitle("College Basketball Predictions")
@Route(value = "home", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@Uses(Icon.class)
public class CollegeBasketballPredictionsView extends Composite<VerticalLayout> {

    public CollegeBasketballPredictionsView() {
        HorizontalLayout layoutRow = new HorizontalLayout();
        DatePicker datePicker = new DatePicker();
        ComboBox comboBox = new ComboBox();
        H1 h1 = new H1();
        Grid<Game> grid = new Grid();
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
        layoutRow.addClassName(Gap.MEDIUM);
        layoutRow.setWidth("100%");
        layoutRow.setHeight("min-content");
        datePicker.setLabel("Date picker");
        datePicker.setWidth("min-content");
        datePicker.setValue(LocalDate.now());
        comboBox.setLabel("Combo Box");
        comboBox.setWidth("min-content");
        setComboBoxData(comboBox);
        h1.setText("Games");
        layoutRow.setAlignSelf(FlexComponent.Alignment.END, h1);
        h1.setWidth("max-content");
        grid.setWidth("100%");
        grid.getStyle().set("flex-grow", "0");
        setGridData(grid);
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
        List<Game> games = Arrays.asList(
                new Game("Oklahoma", "Oklahoma State", "89", "102", "35%", "65%"),
                new Game("Iowa State", "Cincinnati", "112", "101", "75%", "25%"),
                new Game("Kansas", "Baylor", "134", "99", "82%", "18%"));

        grid.addColumn(Game::getTeams).setHeader("Game").setSortable(false).setKey("Teams");
        grid.addColumn(Game::getScores).setHeader("Score (Home - Visitor)").setSortable(false).setKey("Scores");
        grid.addColumn(Game::getPercents).setHeader("Win % (Home - Visitor)").setSortable(false).setKey("Percent");

        grid.setItems(games);

        // end::snippet[]
    }






}
