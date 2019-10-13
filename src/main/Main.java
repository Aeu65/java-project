package main;

import javafx.application.Application;
import javafx.stage.Stage;
import model.TournamentList;
import mvvm.Mediator;
import mvvm.ViewModel;
import view.View;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        TournamentList tournamentList = new TournamentList();
        Mediator mediator = new Mediator(primaryStage);

        ViewModel viewModel = new ViewModel(tournamentList, mediator);
        mediator.addViewModel(viewModel);

        View view = new View(primaryStage, viewModel);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
