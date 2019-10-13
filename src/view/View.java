package view;

import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Match;
import model.Player;
import model.Result;
import model.Tournament;
import mvvm.ViewModel;

public class View extends VBox {

//      Container:   
    private final VBox boxTournamentList = new VBox();
    private final HBox boxTournamentSet = new HBox();
    private final VBox boxRegisteredList = new VBox();
    private final VBox boxMatchList = new VBox();
    private final BorderPane boxMatchSetBar = new BorderPane();
    private final GridPane boxMatchSetBar2 = new GridPane();

//      GraphicElements:
//      -- TounamentList:  
    private final Label tournamentLabel = new Label("Listes des tournois");
    private final ListView tournamentList = new ListView();

//      --RegisteredList:
    private final Label registeredLabel = new Label("Inscrits");
    private final ListView registeredList = new ListView();

//      --MatchList:
    private final Label matchLabel = new Label("Matchs");
    private final TableView<Match> matchTable = new TableView<>();
    private final TableColumn<Match, Player> columnPlayer1 = new TableColumn<>("Joueur 1");
    private final TableColumn<Match, Player> columnPlayer2 = new TableColumn<>("Joueur 2");
    private final TableColumn<Match, Result> columnResult = new TableColumn<>("Résultat");

//      --Messages:
    private final HBox messages = new HBox();
    private final Label error = new Label("");
    private final Label success = new Label("");

    private final ComboBox<String> combo1 = new ComboBox<>();
    private final ComboBox<String> combo2 = new ComboBox<>();
    private final ComboBox<String> combo3 = new ComboBox<>();

//    ButtonBar buttonBar = new ButtonBar();
    private final Button buttonAdd = new Button("Ajouter");
    private final Button buttonUpd = new Button("Modifier");
    private final Button buttonDel = new Button("Supprimer");
    private final Button buttonPlay = new Button("Jouer");

//    Properties
    private final IntegerProperty numTournamentSelected = new SimpleIntegerProperty(-1);
    private final IntegerProperty numMatchSelected = new SimpleIntegerProperty(-1);
    private final IntegerProperty numMatchToSelect = new SimpleIntegerProperty(-1);

    private final StringProperty combo1ToSelect = new SimpleStringProperty(null);
    private final StringProperty combo2ToSelect = new SimpleStringProperty(null);
    private final StringProperty combo3ToSelect = new SimpleStringProperty(null);

    private final StringProperty setErrorMsgTo = new SimpleStringProperty(null);
    private final StringProperty setSuccessMsgTo = new SimpleStringProperty(null);

    private final BooleanProperty showDeleteDialog = new SimpleBooleanProperty(false);
    private final BooleanProperty deleteConfirmation = new SimpleBooleanProperty(false);

    private final ViewModel viewModel;

    public View(Stage primaryStage, ViewModel viewModel) {
        this.viewModel = viewModel;

        configComponents();
        configBindings();
        configListeners();

        Scene scene = new Scene(this, 1200, 800);
        scene.getStylesheets().add("/view/Style.css/");
        primaryStage.setTitle("Tournaments Project");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
    }

    private void configComponents() {
        settingRoot();
        settingTournamentList();
        settingTournamentSet();
        settingRegisteredList();
        settingMatchList();
        settingBar();
    }

    private void configBindings() {
        configDataBindings();
        configActionsBindings();
        configViewModelBindings();
    }

    private void configDataBindings() {
        // ListView
        tournamentList.itemsProperty().bind(viewModel.tournamentListProperty());
        registeredList.itemsProperty().bind(viewModel.playersProperty());

        // TableView
        matchTable.itemsProperty().bind(viewModel.matchesProperty());

        // ComboBoxes
        combo1.itemsProperty().bind(viewModel.playersNamesProperty());
        combo2.itemsProperty().bind(viewModel.playersNamesProperty());
        combo3.itemsProperty().bind(viewModel.resultListProperty());

        // Success and Error Messages
        setErrorMsgTo.bind(viewModel.setErrorMsgToProperty());
        setSuccessMsgTo.bind(viewModel.setSuccessMsgToProperty());
    }

    private void configActionsBindings() {
        numTournamentSelected.bind(viewModel.numTournamentSelectedProperty());
        numMatchSelected.bind(viewModel.numMatchSelectedProperty());
        numMatchToSelect.bind(viewModel.numMatchToSelectProperty());

        combo1ToSelect.bind(viewModel.combo1ToSelectProperty());
        combo2ToSelect.bind(viewModel.combo2ToSelectProperty());
        combo3ToSelect.bind(viewModel.combo3ToSelectProperty());

        showDeleteDialog.bind(viewModel.showDeleteDialogProperty());
    }

    private void configViewModelBindings() {
        viewModel.bindNumTournamentSelectedProperty(
                getTournamentListSelectionModel().selectedIndexProperty()
        );

        viewModel.bindNumMatchSelectedProperty(
                getMatchTableSelectionModel().selectedIndexProperty()
        );

        viewModel.bindIsAddBtnPressedProperty(
                buttonAdd.armedProperty()
        );
        viewModel.bindIsUpdBtnPressedProperty(
                buttonUpd.armedProperty()
        );

        viewModel.bindIsDelBtnPressedProperty(
                buttonDel.armedProperty()
        );
        viewModel.bindIsPlayBtnPressedProperty(
                buttonPlay.armedProperty()
        );

        viewModel.bindCombo1SelectedProperty(
                getCombo1SelectionModel().selectedItemProperty()
        );
        viewModel.bindCombo2SelectedProperty(
                getCombo2SelectionModel().selectedItemProperty()
        );
        viewModel.bindCombo3SelectedProperty(
                getCombo3SelectionModel().selectedItemProperty()
        );

        viewModel.bindDeleteConfirmationProperty(deleteConfirmation);
    }

    private SelectionModel<Tournament> getTournamentListSelectionModel() {
        return tournamentList.getSelectionModel();
    }

    private SelectionModel<Match> getMatchTableSelectionModel() {
        return matchTable.getSelectionModel();
    }

    private SelectionModel<String> getCombo1SelectionModel() {
        return combo1.getSelectionModel();
    }

    private SelectionModel<String> getCombo2SelectionModel() {
        return combo2.getSelectionModel();
    }

    private SelectionModel<String> getCombo3SelectionModel() {
        return combo3.getSelectionModel();
    }

    private void configListeners() {
        configNumTournamentSelectedListener();
        configNumMatchSelectedListener();
        configNumMatchToSelectListener();

        configCombo1ToSelectListener();
        configCombo2ToSelectListener();
        configCombo3ToSelectListener();

        configSetErrorMsgTo();
        configSetSuccessMsgTo();

        configShowDeleteDialog();
    }

    private void settingRoot() {
        this.getChildren().addAll(boxTournamentList, boxTournamentSet);
        this.setPadding(new Insets(10, 20, 20, 20));
        this.getStyleClass().add("this");
    }

    private void settingTournamentList() {
        boxTournamentList.getChildren().addAll(tournamentLabel, tournamentList);
        boxTournamentList.setPadding(new Insets(0, 0, 10, 0));

        tournamentLabel.getStyleClass().add("menu");
        tournamentLabel.setTranslateX(500);

        tournamentList.setMaxWidth(1170);
        tournamentList.setMinWidth(355);

        // disable le bas tant que un tournois n'est pas sélectionné
        boxTournamentSet.setDisable(true);
    }

    private void settingTournamentSet() {
        boxTournamentSet.getChildren().addAll(boxRegisteredList, boxMatchList);
        boxTournamentSet.setMaxHeight(385);
    }

    private void settingRegisteredList() {
        boxRegisteredList.getChildren().addAll(registeredLabel, registeredList);
        boxRegisteredList.setPadding(new Insets(0, 20, 0, 0));
        registeredLabel.getStyleClass().add("menu");

        registeredList.setMinWidth(565);
        registeredLabel.setMaxWidth(565);
    }

    private void settingMatchList() {
        boxMatchList.getChildren().addAll(matchLabel, matchTable, boxMatchSetBar, boxMatchSetBar2);
        matchTable.getColumns().addAll(columnPlayer1, columnPlayer2, columnResult);
        matchLabel.getStyleClass().add("menu");

        matchTable.getStyleClass().add("matchTable");
        matchTable.setMinWidth(585);
        matchLabel.setMinWidth(585);
        boxMatchSetBar.setMinWidth(585);
        matchTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        columnPlayer1.setCellValueFactory(new PropertyValueFactory<Match, Player>("namePlayer1"));
        columnPlayer2.setCellValueFactory(new PropertyValueFactory<Match, Player>("namePlayer2"));
        columnResult.setCellValueFactory(new PropertyValueFactory<Match, Result>("result"));
    }

    private void settingBar() {

        buttonAdd.setDisable(false);
        buttonUpd.setDisable(true);
        buttonDel.setDisable(true);

        boxMatchSetBar.setRight(buttonDel);
        boxMatchSetBar.setLeft(messages);
        boxMatchSetBar.setPadding(new Insets(20, 0, 20, 0));

        boxMatchSetBar2.setHgap(5);
        boxMatchSetBar2.setVgap(5);

        buttonDel.getStyleClass().add("buttonDel");

        // error and success messages
        error.getStyleClass().add("error");
        success.getStyleClass().add("success");

        error.fontProperty().set(new Font(16.0));
        success.fontProperty().set(new Font(16.0));

        messages.getChildren().add(error);
        messages.getChildren().add(success);

        // combo boxes
        combo1.setPromptText("Joueur 1");
        combo2.setPromptText("Joueur 2");
        combo3.setPromptText("Résultat");

        // combo3 setting values
        boxMatchSetBar2.add(combo1, 0, 0);
        boxMatchSetBar2.add(combo2, 1, 0);
        boxMatchSetBar2.add(combo3, 2, 0);
        boxMatchSetBar2.add(buttonAdd, 3, 0);
        boxMatchSetBar2.add(buttonUpd, 4, 0);
        boxMatchSetBar2.add(buttonPlay, 5, 0);
    }

    private void configNumTournamentSelectedListener() {
        numTournamentSelected.addListener((o, old, newValue) -> {
            int index = newValue.intValue();
            if (index != -1) {
                boxTournamentDisable(false);

                buttonUpdDisable(true);
                buttonDelDisable(true);
            } else {
                boxTournamentDisable(true);
            }
        });
    }

    private void configNumMatchSelectedListener() {
        numMatchSelected.addListener((o, old, newValue) -> {
            int index = newValue.intValue();
            if (index != -1) {
                buttonUpdDisable(false);
                buttonDelDisable(false);
            } else {
                buttonUpdDisable(true);
                buttonDelDisable(true);
            }
        });
    }

    private void configNumMatchToSelectListener() {
        numMatchToSelect.addListener((o, old, newValue) -> {
            int index = newValue.intValue();
            if (index != -1) {
                getMatchTableSelectionModel().select(index);
            }

        });
    }

    private void configCombo1ToSelectListener() {
        combo1ToSelect.addListener((o, old, newValue) -> {
            combo1.valueProperty().set(newValue);
        });
    }

    private void configCombo2ToSelectListener() {
        combo2ToSelect.addListener((o, old, newValue) -> {
            combo2.valueProperty().set(newValue);
        });
    }

    private void configCombo3ToSelectListener() {
        combo3ToSelect.addListener((o, old, newValue) -> {
            combo3.valueProperty().set(newValue);
        });
    }

    private void configSetErrorMsgTo() {
        setErrorMsgTo.addListener((o, old, newValue) -> {
            error.setText(newValue);
        });
    }

    private void configSetSuccessMsgTo() {
        setSuccessMsgTo.addListener((o, old, newValue) -> {
            success.setText(newValue);
        });
    }

    private void configShowDeleteDialog() {
        showDeleteDialog.addListener((o, old, newValue) -> {
            if (!old && newValue) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Demande de confirmation");
                alert.setHeaderText("Suppression du match");
                alert.setContentText("Souhaitez-vous vraiment supprimer ce match ?");
                Optional<ButtonType> action = alert.showAndWait();
                if (action.get() == ButtonType.OK) {
                    deleteConfirmation.set(true);
                    deleteConfirmation.set(false);
                }
            }
        });
    }

    private void boxTournamentDisable(boolean on) {
        boxTournamentSet.setDisable(on);
    }

    private void buttonAddDisable(boolean on) {
        buttonAdd.setDisable(on);
    }

    private void buttonDelDisable(boolean on) {
        buttonDel.setDisable(on);
    }

    private void buttonUpdDisable(boolean on) {
        buttonUpd.setDisable(on);
    }
}
