package mvvm;

import java.io.File;
import javafx.collections.ObservableList;
import model.Player;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import model.Game;
import model.Joker;
import model.OriginatorQuestion;
import model.Question;
import model.Result;

public class ViewModelAnswers extends AViewModel {

    private final ObservableList<Question> qls;

    private final OriginatorQuestion currentQuestion;
    private final Player player1;
    private final Player player2;
    private final Game game;
    private MediaPlayer music;

    private final SimpleBooleanProperty toValidateAnswer = new SimpleBooleanProperty();
    private final SimpleBooleanProperty toCancel = new SimpleBooleanProperty();

    private final SimpleStringProperty labelMatchInfo = new SimpleStringProperty();
    private final SimpleStringProperty labelPosQuestion = new SimpleStringProperty();
    private final SimpleStringProperty labelCurrentQuestion = new SimpleStringProperty();
    private final SimpleStringProperty labelCurrentPoints = new SimpleStringProperty();
    private final SimpleStringProperty labelTotalPoints = new SimpleStringProperty();
    private final SimpleBooleanProperty csRightAnswerFound = new SimpleBooleanProperty();
    private final SimpleBooleanProperty deselectOk = new SimpleBooleanProperty(false);

    private final ObservableList<String> csAvailableAnswers = FXCollections.observableArrayList();
    private final IntegerProperty csRightAnswer = new SimpleIntegerProperty(0);

    private final SimpleBooleanProperty boolButton1 = new SimpleBooleanProperty();
    private final SimpleBooleanProperty boolButton2 = new SimpleBooleanProperty();
    private final SimpleBooleanProperty boolButton3 = new SimpleBooleanProperty();
    private final SimpleBooleanProperty boolButton4 = new SimpleBooleanProperty();

    private final SimpleStringProperty setErrorMsgTo = new SimpleStringProperty();
    private final SimpleStringProperty setSuccessMsgTo = new SimpleStringProperty();

    // joker
    private final SimpleBooleanProperty showBoxJoker = new SimpleBooleanProperty(false);
    private final SimpleBooleanProperty callJoker = new SimpleBooleanProperty();
    private final SimpleStringProperty setJoker = new SimpleStringProperty();
    
    // last wrong answer index
    private final SimpleIntegerProperty lastWrongAnswer = new SimpleIntegerProperty(-1);
    
    private final SimpleStringProperty result = new SimpleStringProperty();
    
    public ViewModelAnswers(Game game,
            ObservableList<Question> list, Mediator mediator) {
        this.mediator = mediator;
        this.qls = list;
        this.game = game;
        this.player1 = game.getMatch().getPlayer1();
        this.player2 = game.getMatch().getPlayer2();

        this.currentQuestion = new OriginatorQuestion(qls);
        
        initData();
        configApplicativeLogic();
        initMusic();
    }

    private void initData() {
        labelMatchInfo.set("Match " + player1.getName() + " - " + player2.getName());
        labelPosQuestion.set(currentQuestion.getPos() + " / " + qls.size());
        labelCurrentQuestion.set(currentQuestion.getName() + " ?");
        labelCurrentPoints.set(currentQuestion.getPoints() + " points");

        showBoxJoker();
        
        csAvailableAnswers.clear();
        for (String q : currentQuestion.getAvailableAnswers()) {
            csAvailableAnswers.add(q);
        }

        int maxPossibleScore = qls.stream()
                .map(q -> q.getPoints())
                .reduce(0, (a, b) -> a + b);
        game.setMaxPossibleScore(maxPossibleScore);
        

        labelTotalPoints.set("Points gagnés: " + game.getTotalPoints()
                + " / " + game.getMaxPossibleScore());
    }

    // Config Listeners
    
    private void configApplicativeLogic() {
        configToValidateAnswer();
        configToCancel();
        configCallJoker();
    }
    
    private void configToValidateAnswer(){
        toValidateAnswer.addListener((o, old, newValue) -> {
            if (isReleased(old, newValue)) {
                
                if(game.getMatch().getResult() != null) {
                    // the game is over
                    mediator.gameOver(game.getMatch());
                }
                else if (noAnswer()) {
                    clearAndSetErrorMessage("Sélectionnez une reponse");
                }
                else
                {
                    boolean notLastTurn = true;
                    
                    if(goodAnswer()) {
                        game.updateVictory(currentQuestion);
                        
                        labelTotalPoints.set("Points gagnés: " + game.getTotalPoints()
                            + " / " + game.getMaxPossibleScore());
                        
                        // changes to the next question or false
                        notLastTurn = currentQuestion.won();
                    }
                    else // wrong answer
                    {
                        // changes to the next question or false
                        notLastTurn = currentQuestion.lost(indexAnswered());
                    }

                    Result res = null;
                    
                    if (notLastTurn) {
                        refreshQuestion();
                        res = game.manageStop(false);
                    } else {
                        // last turn
                        res = game.manageStop(true);
                    }
                    
                    if(null != res) {
                        result.set(res.name());
                        playMusicIfWin();
                    }
                }
            }
        });
    }
    
    private void configToCancel(){
        toCancel.addListener((o, old, newValue) -> {
            if (isReleased(old, newValue)) {
                mediator.cancelAnswerQuestions();
            }
        });
    }
    
    private void configCallJoker(){
        callJoker.addListener((o, old, newValue) -> {
            if(isReleased(old, newValue) && null != currentQuestion.getJoker()) {
                currentQuestion.consumeJoker();
                showBoxJoker();
            }
        });
    }

    // /Config Listeners
    
    private int indexAnswered() {
        int index = -1;
        
             if(boolButton1.getValue()) index = 1;
        else if(boolButton2.getValue()) index = 2;
        else if(boolButton3.getValue()) index = 3;
        else if(boolButton4.getValue()) index = 4;
        
        return index;
    }
    
    private boolean noAnswer() {
        return indexAnswered() == -1;
    }
    
    private boolean goodAnswer() {
        return indexAnswered() == currentQuestion.getRightAnswer();
    }

    
    
    private void clearAndSetErrorMessage(String msg) {
        clearMessage();
        setErrorMsgTo.set(msg);
    }

    private void clearMessage() {
        setSuccessMsgTo.set(null);
        setErrorMsgTo.set(null);
    }

    private void refreshQuestion() {
        clearMessage();
        labelPosQuestion.set(currentQuestion.getPos() + " / " + qls.size());
        labelCurrentQuestion.set(currentQuestion.getName() + " ?");
        labelCurrentPoints.set(currentQuestion.getPoints() + " points");
        
        lastWrongAnswer.set(-1);
        lastWrongAnswer.set(currentQuestion.getLastWrongAnswer());
        
        showBoxJoker();
        
        csAvailableAnswers.clear();
        for (String q : currentQuestion.getAvailableAnswers()) {
            csAvailableAnswers.add(q);
        }
        unselectAllRadioButtons();
    }

    private void unselectAllRadioButtons() {
        deselectOk.set(true);
        deselectOk.set(false);
    }
    
    private void showBoxJoker() {
        showBoxJoker.set(false);
        
        if(null != currentQuestion.getJoker()) {
            if(currentQuestion.getJokerUsedStatus() != Joker.UNUSED_JOKER) {
                setJoker.set(currentQuestion.getJoker());
            } else {
                setJoker.set(null);
            }
            showBoxJoker.set(true);
        }
    }
    
    // Utils
    private boolean isReleased(boolean old, boolean newValue) {
        return old && !newValue;
    }
    
    // Properties
    
    public SimpleStringProperty labelMatchInfoProperty() {
        return labelMatchInfo;
    }

    public SimpleStringProperty labelPosQuestionProperty() {
        return labelPosQuestion;
    }

    public SimpleStringProperty labelCurrentQuestionProperty() {
        return labelCurrentQuestion;
    }

    public SimpleStringProperty labelCurrentPointsProperty() {
        return labelCurrentPoints;
    }

    public SimpleStringProperty labelTotalPointsProperty() {
        return labelTotalPoints;
    }

    public SimpleListProperty<String> csAvailableAnswersProperty() {
        return new SimpleListProperty<>(csAvailableAnswers);
    }

    public IntegerProperty csRightAnswerProperty() {
        return csRightAnswer;
    }

    public SimpleBooleanProperty csRightAnswerFoundProperty() {
        return csRightAnswerFound;
    }

    public SimpleBooleanProperty deselectOkProperty() {
        return deselectOk;
    }

    public SimpleStringProperty setErrorMsgToProperty() {
        return setErrorMsgTo;
    }

    public SimpleStringProperty setSuccessMsgToProperty() {
        return setSuccessMsgTo;
    }

    public SimpleBooleanProperty showBoxJokerProperty() {
        return showBoxJoker;
    }
    
    public SimpleStringProperty setJokerProperty() {
        return setJoker;
    }
    
    public SimpleStringProperty setResultProperty() {
        return result;
    }
    
    public SimpleIntegerProperty setLastWrongAnswerProperty() {
        return lastWrongAnswer;
    }
    
    // binding from the view
    public void bindBoolButton1(ReadOnlyBooleanProperty prop) {
        boolButton1.bind(prop);
    }

    public void bindBoolButton2(ReadOnlyBooleanProperty prop) {
        boolButton2.bind(prop);
    }

    public void bindBoolButton3(ReadOnlyBooleanProperty prop) {
        boolButton3.bind(prop);
    }

    public void bindBoolButton4(ReadOnlyBooleanProperty prop) {
        boolButton4.bind(prop);
    }

    public void bindToAddQuestion(ReadOnlyBooleanProperty prop) {
        toValidateAnswer.bind(prop);
    }

    public void bindToCancel(ReadOnlyBooleanProperty prop) {
        toCancel.bind(prop);
    }

    public void bindCallJoker(ReadOnlyBooleanProperty prop) {
        callJoker.bind(prop);
    }
    
    // Music
    
    private void initMusic(){
        String musicPath = new File("src/musiques/GAIN_JOUEUR_2.mp3").toURI().toString();
        music = new MediaPlayer( new Media(musicPath));
    }
    
    private void playMusicIfWin(){
        if(null != result.getValue() && result.getValue().equals("GAIN_JOUEUR_2")){
            music.play();
        }
    }   

}
