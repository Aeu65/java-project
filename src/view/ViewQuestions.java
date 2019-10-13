package view;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import model.Category;
import model.Question;
import mvvm.AViewModel;
import mvvm.ViewModelQuestions;

public class ViewQuestions extends VBox {

    private final ViewModelQuestions viewModel;

//  Containers:
    private final HBox boxTitle = new HBox();
    private final BorderPane boxMatchInfo = new BorderPane();
    private final HBox boxCategories = new HBox();
    private final BorderPane boxSection = new BorderPane();
    private final VBox boxAvailable = new VBox();
    private final VBox boxDetails = new VBox();
    private final VBox btnGroup = new VBox();
    private final VBox boxSelected = new VBox();
    private final HBox boxSubmit = new HBox(5);

//  Labels:
    private final Label labelTitle = new Label("Construction questionnaire");
    private final Label labelCategories = new Label("Catégories");
    private final Label labelMatchVS = new Label();
    private final Label labelNbQuestions = new Label();
    private final Label labelAvailable = new Label("Questions disponibles");
    private final Label labelSelected = new Label("Questions choisies");
    private final Label labelAvailablePoints = new Label("Points disponibles");
    private final Label labelChoosenPoints = new Label("Points questionnaire");

    private final Label labelCurrentQuestion = new Label("Question courante");
    private final Label labelCurrentPoints = new Label("X points");
    private final Label labelAnswers = new Label("Réponses");

    Label btn1Label = new Label("select first");
    Label btn2Label = new Label("select second");
    Label btn3Label = new Label("select third");
    Label btn4Label = new Label("select fourth");

//  Lists (and list equivalents)
    private final ListView listAvailable = new ListView();
    private final ListView listChoosen = new ListView();

    BorderPane bp1 = new BorderPane();
    BorderPane bp2 = new BorderPane();
    BorderPane bp3 = new BorderPane();
    BorderPane bp4 = new BorderPane();

//  Buttons
    RadioButton button1 = new RadioButton();
    RadioButton button2 = new RadioButton();
    RadioButton button3 = new RadioButton();
    RadioButton button4 = new RadioButton();

    private final Button toRight = new Button("->");
    private final Button toLeft = new Button("<-");

    private final Button buttonOk = new Button("Valider");
    private final Button buttonCancel = new Button("Annuler");

// ComboBoxes
    private final ComboBox<Category> comboCategories = new ComboBox<>();

//  Properties
    private final SimpleBooleanProperty toSelectFirstAvailable = new SimpleBooleanProperty();
    private final SimpleBooleanProperty toSelectFirstChoosen = new SimpleBooleanProperty();
    private final SimpleBooleanProperty closeStage = new SimpleBooleanProperty(false);

    private final Stage stage;

    public ViewQuestions(Stage stage, AViewModel viewModel) {
        this.viewModel = (ViewModelQuestions) viewModel;
        this.stage = stage;

        configComponents();
        configBindings();
        configListeners();

        Scene scene = new Scene(this, 1200, 800);
        scene.getStylesheets().add("/view/Style.css/");
        stage.setTitle("Question Game");
        stage.setScene(scene);
        stage.setResizable(false);
    }

    private void configComponents() {
        settingBoxTitle();
        settingBoxMatchInfo();

        settingBoxCategories();

        settingBoxAvailable();
        settingBoxDetails();
        settingBoxSelected();
        settingBoxSection();
        settingBoxSubmit();

        settingRoot();
    }

    private void settingBoxTitle() {
        labelTitle.getStyleClass().add("menu");
        boxTitle.setAlignment(Pos.CENTER);
        boxTitle.getChildren().add(labelTitle);
    }

    private void settingBoxMatchInfo() {
        labelMatchVS.getStyleClass().add("menu");
        labelNbQuestions.getStyleClass().add("menu");
        labelMatchVS.setText("Match " + viewModel.getPlayer1().toString()
                + " - " + viewModel.getPlayer2().toString()
        );

        boxMatchInfo.setLeft(labelMatchVS);
        boxMatchInfo.setRight(labelNbQuestions);
    }

    private void settingBoxCategories() {
        labelCategories.getStyleClass().add("size16");

        comboCategories.getItems().addAll(viewModel.getCatLs());
        comboCategories.getSelectionModel().select(viewModel.getRoot());

        boxCategories.setPadding(new Insets(20, 0, 0, 0));
        boxCategories.setSpacing(5);
        boxCategories.getChildren().addAll(labelCategories, comboCategories);
    }

    private void settingBoxAvailable() {
        labelAvailable.getStyleClass().add("menu");

        boxAvailable.setPrefWidth((1200) / 3);
        boxAvailable.setAlignment(Pos.CENTER);

        labelAvailablePoints.getStyleClass().add("size16");

        boxAvailable.getChildren().addAll(labelAvailable, listAvailable,
                labelAvailablePoints);
    }

    private void settingBoxDetails() {
        boxDetails.setAlignment(Pos.TOP_CENTER);

        labelCurrentPoints.getStyleClass().add("size14");
        labelAnswers.getStyleClass().add("size14");

        labelCurrentPoints.setPadding(new Insets(0, 0, 30, 0));
        labelAnswers.setPadding(new Insets(0, 0, 10, 0));

        ToggleGroup group = new ToggleGroup();

        button1.getStyleClass().add("label");
        button1.setToggleGroup(group);

        bp1.setLeft(btn1Label);
        bp1.setRight(button1);
        bp1.setPadding(new Insets(0, 0, 10, 0));

        button2.getStyleClass().add("label");
        button2.setToggleGroup(group);
        bp2.setLeft(btn2Label);
        bp2.setRight(button2);
        bp2.setPadding(new Insets(0, 0, 10, 0));

        button3.getStyleClass().add("label");
        button3.setToggleGroup(group);
        bp3.setLeft(btn3Label);
        bp3.setRight(button3);
        bp3.setPadding(new Insets(0, 0, 10, 0));

        button4.getStyleClass().add("label");
        button4.setToggleGroup(group);
        bp4.setLeft(btn4Label);
        bp4.setRight(button4);
        bp4.setPadding(new Insets(0, 0, 10, 0));

        btnGroup.getChildren().addAll(bp1, bp2, bp3, bp4);
        btnGroup.setPadding(new Insets(0, 60, 20, 60));
        btnGroup.setDisable(true);
        btnGroup.getStyleClass().add("clearer-disabled-state");
        btnGroup.setStyle("-fx-opacity: 1.0;");

        boxDetails.setSpacing(5);

        labelCurrentQuestion.getStyleClass().add("size18");
        labelCurrentQuestion.setWrapText(true);
        labelCurrentQuestion.setAlignment(Pos.CENTER);

        labelCurrentQuestion.setTextAlignment(TextAlignment.CENTER);

        // nasty hack
        HBox paddingBox = new HBox();
        paddingBox.setPrefHeight(200);
        paddingBox.setAlignment(Pos.TOP_CENTER);
        paddingBox.getChildren().add(labelCurrentQuestion);
        paddingBox.setPadding(new Insets(70, 0, 0, 0));

        boxDetails.getChildren().addAll(paddingBox, labelCurrentPoints,
                labelAnswers, btnGroup, toRight, toLeft);

        boxDetails.setPadding(new Insets(0, 20, 0, 20));

        boxDetails.setVisible(false);
    }

    private void settingBoxSelected() {
        labelSelected.getStyleClass().add("menu");

        boxSelected.setPrefWidth((1200) / 3);
        boxSelected.setAlignment(Pos.CENTER);

        labelChoosenPoints.getStyleClass().add("size16");

        boxSelected.getChildren().addAll(labelSelected, listChoosen,
                labelChoosenPoints);
    }

    private void settingBoxSection() {
        boxSection.setLeft(boxAvailable);
        boxSection.setCenter(boxDetails);
        boxSection.setRight(boxSelected);
        boxSection.setBottom(boxSubmit);
    }

    private void settingBoxSubmit() {
        boxSubmit.getChildren().addAll(buttonOk, buttonCancel);
        boxSubmit.setPrefHeight(50);
        boxSubmit.setAlignment(Pos.CENTER);
    }

    private void settingRoot() {
        this.getChildren().addAll(boxTitle, boxMatchInfo, boxCategories, boxSection);
        this.setPadding(new Insets(10, 20, 20, 20));
        this.getStyleClass().add("this");
    }

    private void configBindings() {
        configDataBindings();
        configActionsBindings();
        configViewModelBindings();
    }

    private void configDataBindings() {
        // Question lists
        listAvailable.itemsProperty().bind(viewModel.qlsProperty());
        listChoosen.itemsProperty().bind(viewModel.choosenlsProperty());

        // Currently selected Question
        labelCurrentQuestion.textProperty().bind(viewModel.csQuestionProperty());
        labelCurrentPoints.textProperty().bind(viewModel.csPointsProperty().asString());

        btn1Label.textProperty().bind(viewModel.csAvailableAnswersProperty().valueAt(0));
        btn2Label.textProperty().bind(viewModel.csAvailableAnswersProperty().valueAt(1));
        btn3Label.textProperty().bind(viewModel.csAvailableAnswersProperty().valueAt(2));
        btn4Label.textProperty().bind(viewModel.csAvailableAnswersProperty().valueAt(3));

        button1.selectedProperty().bind(viewModel.csRightAnswerProperty().isEqualTo(1));
        button2.selectedProperty().bind(viewModel.csRightAnswerProperty().isEqualTo(2));
        button3.selectedProperty().bind(viewModel.csRightAnswerProperty().isEqualTo(3));
        button4.selectedProperty().bind(viewModel.csRightAnswerProperty().isEqualTo(4));
    }

    private void configActionsBindings() {
        toSelectFirstAvailable.bind(viewModel.toSelectFirstAvailableProperty());
        toSelectFirstChoosen.bind(viewModel.toSelectFirstChoosenProperty());
        closeStage.bind(viewModel.closeStageProperty());

        labelNbQuestions.textProperty()
                .bind(Bindings.concat("Nombre de questions:  ",
                        viewModel.choosenlsProperty().sizeProperty().asString()));

        labelAvailablePoints.textProperty()
                .bind(Bindings.concat("Points disponibles : ",
                        Bindings.createStringBinding(
                                () -> viewModel.qlsProperty()
                                        .stream()
                                        .map(q -> q.getPoints())
                                        .reduce(0, (a, b) -> a + b)
                                        .toString(),
                                viewModel.qlsProperty()
                        )));

        labelChoosenPoints.textProperty()
                .bind(
                        Bindings.concat(
                                "Points questionnaire : ",
                                Bindings.createStringBinding(
                                        () -> viewModel.choosenlsProperty()
                                                .stream()
                                                .map(q -> q.getPoints())
                                                .reduce(0, (a, b) -> a + b)
                                                .toString(),
                                        viewModel.choosenlsProperty()),
                                " / " + viewModel.maxNbPointsProperty().intValue()
                        )
                );

        buttonOk.disableProperty()
                .bind(viewModel.choosenlsProperty().sizeProperty().lessThan(1));

    }

    private void configViewModelBindings() {
        viewModel.bindCurrentlySelectedCategoryProperty(
                getComboCategoriesSelectionModel().selectedItemProperty()
        );

        viewModel.bindSelectedAvailableQuestionProperty(
                getListAvailableSelectionModel().selectedIndexProperty()
        );

        viewModel.bindSelectedChoosenQuestionProperty(
                getListChoosenSelectionModel().selectedIndexProperty()
        );

        viewModel.bindToAddQuestion(
                toRight.armedProperty()
        );

        viewModel.bindToRemoveQuestion(
                toLeft.armedProperty()
        );

        viewModel.bindToCancel(
                buttonCancel.armedProperty()
        );

        viewModel.bindToValidate(
                buttonOk.armedProperty()
        );
    }

    private SelectionModel<Question> getListAvailableSelectionModel() {
        return listAvailable.getSelectionModel();
    }

    private SelectionModel<Question> getListChoosenSelectionModel() {
        return listChoosen.getSelectionModel();
    }

    private SelectionModel<Category> getComboCategoriesSelectionModel() {
        return comboCategories.getSelectionModel();
    }

    private void configListeners() {
        configSelectedAvailableQuestionListener();
        configToSelectFirstAvailable();
        configToSelectFirstChoosen();
        configCloseStage();
    }

    private void configSelectedAvailableQuestionListener() {
        getListAvailableSelectionModel()
                .selectedIndexProperty().addListener((o, old, newValue) -> {

                    if (-1 == newValue.intValue() && -1 == getListChoosenSelectionModel().selectedIndexProperty().intValue()) {
                        boxDetails.setVisible(false);
                    } else {
                        boxDetails.setVisible(true);
                    }

                    if (-1 != newValue.intValue()) {
                        getListChoosenSelectionModel().select(-1);
                    }
                });

        getListChoosenSelectionModel()
                .selectedIndexProperty().addListener((o, old, newValue) -> {

                    if (-1 == newValue.intValue() && -1 == getListAvailableSelectionModel().selectedIndexProperty().intValue()) {
                        boxDetails.setVisible(false);
                    } else {
                        boxDetails.setVisible(true);
                    }

                    if (-1 != newValue.intValue()) {
                        getListAvailableSelectionModel().select(-1);
                    }
                });
    }

    private void configToSelectFirstAvailable() {
        toSelectFirstAvailable.addListener((o, old, newValue) -> {
            if (!old && newValue) {
                getListAvailableSelectionModel().selectFirst();
                listAvailable.requestFocus();
            }
        });
    }

    private void configToSelectFirstChoosen() {
        toSelectFirstChoosen.addListener((o, old, newValue) -> {
            if (!old && newValue) {
                getListChoosenSelectionModel().selectFirst();
                listChoosen.requestFocus();
            }
        });
    }

    private void configCloseStage() {
        closeStage.addListener((o, old, newValue) -> {
            if (!old && newValue) {
                stage.close();
            }
        });
    }
}
