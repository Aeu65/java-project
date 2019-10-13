package mvvm;

import model.Player;
import model.Question;
import java.util.List;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import model.Category;
import model.Game;

public class ViewModelQuestions extends AViewModel {

    private Game game;

//    Properties
    private final SortedList<Question> qlsSorted;

    private final IntegerProperty selectedAvailableQuestion = new SimpleIntegerProperty(-1);
    private final IntegerProperty selectedChoosenQuestion = new SimpleIntegerProperty(-1);

//    Used to detect triggered buttons
    private final SimpleBooleanProperty toAddQuestion = new SimpleBooleanProperty();
    private final SimpleBooleanProperty toRemoveQuestion = new SimpleBooleanProperty();
    private final SimpleBooleanProperty toCancel = new SimpleBooleanProperty();
    private final SimpleBooleanProperty toValidate = new SimpleBooleanProperty();

    private final SimpleBooleanProperty toSelectFirstAvailable = new SimpleBooleanProperty();
    private final SimpleBooleanProperty toSelectFirstChoosen = new SimpleBooleanProperty();
    private final SimpleBooleanProperty closeStage = new SimpleBooleanProperty(false);

//    Currently selected
    private final SimpleObjectProperty currentlySelectedCategory;

    private final StringProperty csQuestion = new SimpleStringProperty(null);
    private final IntegerProperty csPoints = new SimpleIntegerProperty(0);
    private final ObservableList<String> csAvailableAnswers = FXCollections.observableArrayList();
    private final IntegerProperty csRightAnswer = new SimpleIntegerProperty(0);

    public ViewModelQuestions(Game game, Mediator mediator) {
        this.game = game;
        this.mediator = mediator;

        qlsSorted = new SortedList<>(game.getQls(), (a, b) -> a.compareTo(b));
        currentlySelectedCategory = new SimpleObjectProperty(game.getRoot(), "currentlySelectedCategory");
        game.setCurrentlySelected((Category) currentlySelectedCategory.getBean());

        configApplicativeLogic();
    }

    private void configApplicativeLogic() {
        // listeners...
        configCurrentlySelectedCategory();
        configSelectedAvailableQuestion();
        configSelectedChoosenQuestion();
        configToAddQuestion();
        configToRemoveQuestion();
        configToCancel();
        configToValidate();
    }
    
    private void configCurrentlySelectedCategory(){
        currentlySelectedCategory.addListener((o, old, newValue) -> {
            game.setCurrentlySelected((Category) newValue);
            game.refreshQls((Category) newValue);
        });
    }
    
    private void configSelectedAvailableQuestion(){
        selectedAvailableQuestion.addListener((o, old, newValue) -> {
            if (-1 != newValue.intValue()) {
                Question selected = qlsSorted.get(newValue.intValue());

                csQuestion.set(selected.getName());
                csPoints.set(selected.getPoints());
                csRightAnswer.set(selected.getRightAnswer());

                csAvailableAnswers.clear();
                for (String q : selected.getAvailableAnswers()) {
                    csAvailableAnswers.add(q);
                }
            }
        });
    }
    
    private void configSelectedChoosenQuestion(){
        selectedChoosenQuestion.addListener((o, old, newValue) -> {
            if (-1 != newValue.intValue()) {
                Question selected = game.getChoosenls().get(newValue.intValue());

                csQuestion.set(selected.getName());
                csPoints.set(selected.getPoints());
                csRightAnswer.set(selected.getRightAnswer());

                csAvailableAnswers.clear();
                for (String q : selected.getAvailableAnswers()) {
                    csAvailableAnswers.add(q);
                }
            }
        });
    }
    
    private void configToAddQuestion(){
         // bouton 'toRight'
        toAddQuestion.addListener((o, old, newValue) -> {
            if (isReleased(old, newValue)
                    && -1 != selectedAvailableQuestion.intValue()) {

                Question current = qlsSorted.get(selectedAvailableQuestion.intValue());

                if (game.maxNbChoosenReached(current)) {
                    game.getChoosenls().add(current);
                    game.removeQuestion(current);

                    toSelectFirstAvailable.set(false);
                    toSelectFirstAvailable.set(true);
                }

            }
        });
    }
    
    private void configToRemoveQuestion(){
        // bouton 'toLeft'
        toRemoveQuestion.addListener((o, old, newValue) -> {
            if (isReleased(old, newValue)
                    && -1 != selectedChoosenQuestion.intValue()) {

                Question current = game.getChoosenls()
                        .get(selectedChoosenQuestion.intValue());

                if (!qlsSorted.contains(current)) {

                    game.restoreChild(current);
                    game.getChoosenls().remove(current);

                    toSelectFirstChoosen.set(false);
                    toSelectFirstChoosen.set(true);
                }

            }
        });
    }
    
    private void configToCancel(){
        toCancel.addListener((o, old, newValue) -> {
            if (isReleased(old, newValue)) {
                mediator.cancelPrepareQuestions();
            }
        });
    }
    
    private void configToValidate(){
        toValidate.addListener((o, old, newValue) -> {
            if (isReleased(old, newValue)) {
                closeStage.set(true);
                mediator.launchAnswers(game, game.getChoosenls());
            }
        });
    }

//    Public methods reacheable from the View
    public List<Category> getCatLs() {
        return game.getCatLs();
    }

    public Category getRoot() {
        return game.getRoot();
    }

//    Properties to be bound by the View
    public SimpleListProperty<Question> qlsProperty() {
        return new SimpleListProperty<>(qlsSorted);
    }

    public SimpleListProperty<Question> choosenlsProperty() {
        return new SimpleListProperty<>(game.getChoosenls());
    }

    public StringProperty csQuestionProperty() {
        return csQuestion;
    }

    public IntegerProperty csPointsProperty() {
        return csPoints;
    }

    public SimpleListProperty<String> csAvailableAnswersProperty() {
        return new SimpleListProperty<>(csAvailableAnswers);
    }

    public IntegerProperty csRightAnswerProperty() {
        return csRightAnswer;
    }

    public SimpleBooleanProperty toSelectFirstAvailableProperty() {
        return toSelectFirstAvailable;
    }

    public SimpleBooleanProperty toSelectFirstChoosenProperty() {
        return toSelectFirstChoosen;
    }

    public ReadOnlyIntegerProperty maxNbPointsProperty() {
        return new SimpleIntegerProperty(game.MAXNBPOINTS);
    }

    public SimpleBooleanProperty closeStageProperty() {
        return closeStage;
    }

    // Local Properties bound to the View
    public void bindCurrentlySelectedCategoryProperty(ReadOnlyObjectProperty<Category> prop) {
        currentlySelectedCategory.bind(prop);
    }

    public void bindSelectedAvailableQuestionProperty(ReadOnlyIntegerProperty prop) {
        selectedAvailableQuestion.bind(prop);
    }

    public void bindSelectedChoosenQuestionProperty(ReadOnlyIntegerProperty prop) {
        selectedChoosenQuestion.bind(prop);
    }

    public void bindToAddQuestion(ReadOnlyBooleanProperty prop) {
        toAddQuestion.bind(prop);
    }

    public void bindToRemoveQuestion(ReadOnlyBooleanProperty prop) {
        toRemoveQuestion.bind(prop);
    }

    public void bindToCancel(ReadOnlyBooleanProperty prop) {
        toCancel.bind(prop);
    }

    public void bindToValidate(ReadOnlyBooleanProperty prop) {
        toValidate.bind(prop);
    }

    // Util method
    private boolean isReleased(boolean old, boolean newValue) {
        return old && !newValue;
    }

    // Getters and Setters
    public Player getPlayer1() {
        return game.getMatch().getPlayer1();
    }

    public Player getPlayer2() {
        return game.getMatch().getPlayer2();
    }
}
