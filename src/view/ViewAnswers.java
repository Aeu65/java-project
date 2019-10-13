package view;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import mvvm.AViewModel;
import mvvm.ViewModelAnswers;

public class ViewAnswers extends VBox {

    private final ViewModelAnswers viewModel;

//        Containers:

    private final VBox boxHead = new VBox();
    private final VBox boxBody = new VBox();
    private final VBox boxFooter = new VBox();

    private final HBox boxCurrentQuestion = new HBox();
    private final HBox boxTitle = new HBox();
    private final BorderPane boxMatchInfo = new BorderPane();

    private final VBox btnGroup = new VBox();
    private final HBox boxSubmit = new HBox(5);

    private final HBox boxErrorOrSuccessMsg = new HBox();
    
    private final StackPane boxJoker = new StackPane();

//        Labels:
    private final Label labelTitle = new Label("Réponses au questionnaire");
    private final Label labelMatchInfo = new Label("Match Alice - Bob");
    private final Label labelPosQuestion = new Label("4/5");

    private final Label labelCurrentQuestion = new Label("Question courante");
    private final Label labelCurrentPoints = new Label("X points");
    private final Label labelAnswers = new Label("Réponses");

    private final Label labelTotalPoints = new Label("Points gagnés : 4/10");

    private final Label btn1Label = new Label("select first");
    private final Label btn2Label = new Label("select second");
    private final Label btn3Label = new Label("select third");
    private final Label btn4Label = new Label("select fourth");

    private final Label labelSuccessMsg = new Label();
    private final Label labelErrorMsg = new Label();

    private final Label labelJoker = new Label();
    
//        Lists:
    private final BorderPane bp1 = new BorderPane();
    private final BorderPane bp2 = new BorderPane();
    private final BorderPane bp3 = new BorderPane();
    private final BorderPane bp4 = new BorderPane();

//        Buttons:
    private final RadioButton button1 = new RadioButton();
    private final RadioButton button2 = new RadioButton();
    private final RadioButton button3 = new RadioButton();
    private final RadioButton button4 = new RadioButton();

    private final Button ok = new Button("Valider");
    private final SimpleBooleanProperty deselectOk = new SimpleBooleanProperty();
    private final Button cancel = new Button("Annuler");
    
    private final Button joker = new Button("Joker");

//        Properties:
    private final SimpleStringProperty setErrorMsgTo = new SimpleStringProperty();
    private final SimpleStringProperty setSuccessMsgTo = new SimpleStringProperty();

    private final SimpleStringProperty setResult = new SimpleStringProperty();
    
    // joker
    private final SimpleBooleanProperty showBoxJoker = new SimpleBooleanProperty();
    private final SimpleStringProperty setJoker = new SimpleStringProperty();
    
    // last wrong answer index
    private final SimpleIntegerProperty lastWrongAnswer = new SimpleIntegerProperty(-1);
    
    public ViewAnswers(Stage stage, AViewModel viewModel) {
        this.viewModel = (ViewModelAnswers) viewModel;

        configComponents();
        configListeners();
        configBindings();

        Scene scene = new Scene(this, 450, 800);
        scene.getStylesheets().add("/view/Style.css/");
        stage.setTitle("Question Game");
        stage.setScene(scene);
        stage.setResizable(false);
    }

    private void configComponents() {
        settingRoot();

        settingBoxHead();
        settingBoxBody();
        settingBoxFooter();
    }

    private void settingRoot() {
        this.getChildren().addAll(boxHead, boxBody, boxFooter);
        this.getStyleClass().add("this");
        // this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(0, 15, 0, 15));
    }

    private void settingBoxHead() {
        boxHead.getChildren().addAll(boxTitle, boxMatchInfo);
        boxHead.setSpacing(25);
        boxHead.setPrefHeight(100);
        boxHead.setPrefWidth(420);
        boxHead.setPadding(new Insets(10, 0, 0, 0));

        settingBoxTitle();
        settingBoxMatchInfo();
    }

    private void settingBoxTitle() {
        boxTitle.getChildren().add(labelTitle);
        boxTitle.setAlignment(Pos.CENTER);

        labelTitle.getStyleClass().add("menu");
    }

    private void settingBoxMatchInfo() {
        boxMatchInfo.setLeft(labelMatchInfo);
        boxMatchInfo.setRight(labelPosQuestion);

        labelMatchInfo.getStyleClass().add("menu");
        labelPosQuestion.getStyleClass().add("menu");
    }

    private void settingBoxBody() {
        boxBody.getChildren().addAll(boxCurrentQuestion, boxErrorOrSuccessMsg,
                labelCurrentPoints, boxJoker, labelAnswers, btnGroup);
        boxBody.getStyleClass().add("bordered");

        boxBody.setPrefHeight(550);
        boxBody.setPrefWidth(420);
        boxBody.setPadding(new Insets(60, 10, 0, 10));
        boxBody.setAlignment(Pos.TOP_CENTER);

        settingBoxCurrentQuestion();
        settingBoxErrorOrSuccessMsg();
        settingLabelCurrentPoints();
        settingBoxJoker();
        settingLabelAnswers();
        settingBtnGroup();
    }

    private void settingBoxCurrentQuestion() {
        boxCurrentQuestion.getChildren().add(labelCurrentQuestion);
        boxCurrentQuestion.setAlignment(Pos.TOP_CENTER);

        boxCurrentQuestion.setPrefHeight(150);
        labelCurrentQuestion.getStyleClass().add("size18");
        labelCurrentQuestion.setTextAlignment(TextAlignment.CENTER);
        labelCurrentQuestion.setWrapText(true);
    }

    private void settingBoxErrorOrSuccessMsg() {
        boxErrorOrSuccessMsg.getChildren().addAll(labelSuccessMsg, labelErrorMsg);
        boxErrorOrSuccessMsg.setAlignment(Pos.TOP_CENTER);
        boxErrorOrSuccessMsg.setPrefHeight(50);

        labelSuccessMsg.getStyleClass().addAll("success", "size18");
        labelErrorMsg.getStyleClass().addAll("error", "size18");
    }

    private void settingLabelCurrentPoints() {
        labelCurrentPoints.getStyleClass().add("size14");
        labelCurrentPoints.setPadding(new Insets(0, 0, 35, 0));
    }

    private void settingBoxJoker() {
        boxJoker.setPadding(new Insets(5));
    }
    
    private void settingLabelAnswers() {
        labelAnswers.getStyleClass().add("size14");
        labelAnswers.setPadding(new Insets(25, 0, 20, 0));
    }

    private void settingBtnGroup() {
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
        btnGroup.setPadding(new Insets(0, 40, 0, 40));
    }

    private void settingBoxFooter() {
        boxFooter.getChildren().addAll(labelTotalPoints, boxSubmit);
        boxFooter.setAlignment(Pos.CENTER);
        boxFooter.setPrefHeight(150);
        boxFooter.setPrefWidth(420);
        boxFooter.setSpacing(50);

        settingLabelTotalPoints();
        settingBoxSubmit();
    }

    private void settingLabelTotalPoints() {
        labelTotalPoints.getStyleClass().add("size16");
    }

    private void settingBoxSubmit() {
        boxSubmit.getChildren().addAll(ok, cancel);
        boxSubmit.setAlignment(Pos.TOP_CENTER);
    }

    private void configBindings() {
        configDataBindings();
        configActionsBindings();
        configViewModelBindings();
    }

    private void configDataBindings() {
        labelMatchInfo.textProperty().bind(viewModel.labelMatchInfoProperty());
        labelPosQuestion.textProperty().bind(viewModel.labelPosQuestionProperty());
        labelCurrentQuestion.textProperty().bind(viewModel.labelCurrentQuestionProperty());
        labelCurrentPoints.textProperty().bind(viewModel.labelCurrentPointsProperty());

        btn1Label.textProperty().bind(viewModel.csAvailableAnswersProperty().valueAt(0));
        btn2Label.textProperty().bind(viewModel.csAvailableAnswersProperty().valueAt(1));
        btn3Label.textProperty().bind(viewModel.csAvailableAnswersProperty().valueAt(2));
        btn4Label.textProperty().bind(viewModel.csAvailableAnswersProperty().valueAt(3));

        labelTotalPoints.textProperty().bind(viewModel.labelTotalPointsProperty());

        setErrorMsgTo.bind(viewModel.setErrorMsgToProperty());
        setSuccessMsgTo.bind(viewModel.setSuccessMsgToProperty());
        
        setJoker.bind(viewModel.setJokerProperty());
        
        setResult.bind(viewModel.setResultProperty());
        
        lastWrongAnswer.bind(viewModel.setLastWrongAnswerProperty());
    }

    private void configActionsBindings() {
        deselectOk.bind(viewModel.deselectOkProperty());
        
        showBoxJoker.bind(viewModel.showBoxJokerProperty());
    }

    private void configViewModelBindings() {
        viewModel.bindBoolButton1(button1.selectedProperty());
        viewModel.bindBoolButton2(button2.selectedProperty());
        viewModel.bindBoolButton3(button3.selectedProperty());
        viewModel.bindBoolButton4(button4.selectedProperty());

        viewModel.bindToAddQuestion(ok.armedProperty());
        viewModel.bindToCancel(cancel.armedProperty());
        
        viewModel.bindCallJoker(joker.armedProperty());
    }
    
    private void configListeners() {
        configDeselectOk();
        configSetErrorMsgTo();
        configSetSuccessMsgTo();
        configSetResult();
        configShowBoxJoker();
        configLastWrongAnswer();
    }
    
    private void configDeselectOk(){
        deselectOk.addListener((o, old, newValue) -> {
            button1.setSelected(false);
            button2.setSelected(false);
            button3.setSelected(false);
            button4.setSelected(false);
        });
    }
    private void configSetErrorMsgTo(){
        setErrorMsgTo.addListener((o, old, newValue) -> {
            labelErrorMsg.setText(newValue);
        });
    }
    private void configSetSuccessMsgTo(){
        setSuccessMsgTo.addListener((o, old, newValue) -> {
            labelSuccessMsg.setText(newValue);
        });
    }
    private void configSetResult(){
        setResult.addListener((o, old, newValue) -> {
            boxBody.getChildren().clear();
            boxBody.getChildren().remove(boxCurrentQuestion);
            boxHead.getChildren().remove(boxMatchInfo);

            Image i = new Image("images/" + newValue + ".jpg");

            ImageView iv = new ImageView();
            iv.setFitWidth(410);
            iv.setImage(i);
            boxBody.getChildren().add(iv);
            
            boxSubmit.getChildren().remove(cancel);
            ok.setText("Terminer");
        });
    }
    private void configShowBoxJoker(){
        showBoxJoker.addListener((o, old, newValue) -> {
            if(!old && newValue) { // show box
                boxJoker.getStyleClass().add("bordered");
                
                if(null == setJoker.getValue()) {
                    // joker unused
                    boxJoker.getChildren().add(joker);
                } else {
                    // joker used
                    labelJoker.setText(setJoker.getValue());
                    boxJoker.getChildren().add(labelJoker);
                }
            } else if (old && !newValue) { // hide box
                boxJoker.getChildren().remove(joker);
                boxJoker.getChildren().remove(labelJoker);
                boxJoker.getStyleClass().remove("bordered");
            }
        });
    }
    
    private void configLastWrongAnswer() {
        lastWrongAnswer.addListener((o, old, newValue) -> {
            int wrongIndex = newValue.intValue();
            
            if(-1 != wrongIndex) {
                // current answer is a 'second chance' answer
                labelPosQuestion.getStyleClass().add("red");
            }
            switch(wrongIndex) {
                case 1:
                    btn1Label.getStyleClass().add("red");
                    break;
                case 2:
                    btn2Label.getStyleClass().add("red");
                    break;
                case 3:
                    btn3Label.getStyleClass().add("red");
                    break;
                case 4:
                    btn4Label.getStyleClass().add("red");
                    break;
                case -1:
                default:
                    labelPosQuestion.getStyleClass().remove("red");
                    btn1Label.getStyleClass().remove("red");
                    btn2Label.getStyleClass().remove("red");
                    btn3Label.getStyleClass().remove("red");
                    btn4Label.getStyleClass().remove("red");
                    break;
            }
        });
    }
    
}
