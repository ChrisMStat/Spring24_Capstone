package com.example.application.views.about;

import com.example.application.views.MainLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import java.util.Collections;
import com.vaadin.flow.component.html.Span;


@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
public class AboutView extends VerticalLayout {

    public AboutView() {
        setSpacing(false);
        setAlignItems(Alignment.CENTER);

        // placeholder for OU emblem
        Image placeholderImage = new Image("images/placeholder.png", "Placeholder");
        placeholderImage.setWidth("300px");
        VerticalLayout firstRow = new VerticalLayout(placeholderImage);
        firstRow.setWidthFull();
        firstRow.setAlignItems(Alignment.CENTER);

        // placeholder img for team member headshots
        HorizontalLayout teamImagesLayout = new HorizontalLayout();
        teamImagesLayout.setWidthFull();
        teamImagesLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);

        String[] memberNames = {"Chris Statton", "Qi Fa (Darren) Dong", "Nick Rodriguez", "Nam Huynh", "Garrett Busey", "Blake reynolds"};

        for (int i = 0; i < 6; i++) {
            VerticalLayout imageAndName = new VerticalLayout();
            imageAndName.setAlignItems(Alignment.CENTER);
            Image img = new Image("images/placeholder.png", "Team Member " + (i + 1));
            img.setWidth("100px");
            img.setHeight("100px");
            Span name = new Span(memberNames[i]);
            imageAndName.add(img, name);
            teamImagesLayout.add(imageAndName);
        }

        // team motto/ about us info / algorithm info
        Paragraph aboutParagraph = new Paragraph("Welcome to Team C's Capstone Spring 2024 project. " +
                "We aim to provide comprehensive analytics and insights for college basketball enthusiasts.");
        aboutParagraph.getStyle().set("font-size", "20px"); // font size
        VerticalLayout aboutLayout = new VerticalLayout(aboutParagraph);
        aboutLayout.setWidthFull();
        aboutLayout.setAlignItems(Alignment.CENTER);


        Paragraph linearRegressionParagraph = new Paragraph("Linear Regression: By utilizing a linear regression model, " +
                "we analyze past game data to identify patterns and relationships between various game factors " +
                "(like team performance, player statistics, and home advantage) and the game outcomes.");
        linearRegressionParagraph.getStyle().set("font-size", "18px");

        VerticalLayout linearRegressionLayout = new VerticalLayout(linearRegressionParagraph);
        linearRegressionLayout.setMaxWidth("800px");
        linearRegressionLayout.setWidthFull();
        linearRegressionLayout.setAlignItems(Alignment.CENTER);

        Paragraph eloRankingParagraph = new Paragraph("ELO Ranking: Complementing our linear regression model, " +
                "we utilize the ELO ranking system to gauge the relative skill levels of teams. " +
                "The ELO system adjusts a team's rating based on game results, factoring in the opponent's strength for each match. " +
                "This dynamic ranking provides a reflection of a team's performance level, " +
                "further enhancing our prediction accuracy.");
        eloRankingParagraph.getStyle().set("font-size", "18px");

        VerticalLayout eloRankingLayout = new VerticalLayout(eloRankingParagraph);
        eloRankingLayout.setMaxWidth("800px");
        eloRankingLayout.setWidthFull();
        eloRankingLayout.setAlignItems(Alignment.CENTER);

        VerticalLayout thirdRow = new VerticalLayout(linearRegressionParagraph, eloRankingParagraph);
        thirdRow.setWidthFull();
        thirdRow.setAlignItems(Alignment.CENTER);
        thirdRow.getStyle().set("max-width", "800px");
        thirdRow.setJustifyContentMode(JustifyContentMode.CENTER);
        // Main About Us View
        add(firstRow, teamImagesLayout, thirdRow);

        setSizeFull();
        setJustifyContentMode(JustifyContentMode.CENTER);
        setDefaultHorizontalComponentAlignment(Alignment.CENTER);
        getStyle().set("text-align", "center");
    }

}
