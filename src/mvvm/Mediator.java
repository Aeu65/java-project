package mvvm;

import java.util.HashMap;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Game;
import model.Match;
import model.Player;
import model.Question;
import view.ViewAnswers;
import view.ViewQuestions;

public class Mediator {

    private final HashMap<String, AViewModel> viewModels = new HashMap<>();

    private final Stage primaryStage;
    private final Stage prepareQuestions;
    private final Stage answerQuestions;

    public Mediator(Stage primaryStage) {
        this.primaryStage = primaryStage;

        this.prepareQuestions = new Stage(StageStyle.DECORATED);
        this.prepareQuestions.initModality(Modality.APPLICATION_MODAL);
        this.prepareQuestions.initOwner(primaryStage);

        this.answerQuestions = new Stage(StageStyle.DECORATED);
        this.answerQuestions.initModality(Modality.APPLICATION_MODAL);
        this.answerQuestions.initOwner(primaryStage);
    }

    public void addViewModel(AViewModel v) {
        viewModels.put(v.getClass().getSimpleName(), v);
    }

    public void launchQuestions(Player p1, Player p2) {
        Game game = new Game(new Match(p1, p2, null));

        ViewModelQuestions viewModel = new ViewModelQuestions(game, this);
        this.addViewModel(viewModel);

        ViewQuestions view = new ViewQuestions(prepareQuestions, viewModel);

        prepareQuestions.show();
    }

    public void cancelPrepareQuestions() {
        prepareQuestions.close();
    }

    public void cancelAnswerQuestions() {
        answerQuestions.close();
    }

    public void launchAnswers(Game game, ObservableList<Question> li) {
        ViewModelAnswers viewModel = new ViewModelAnswers(game, li, this);
        this.addViewModel(viewModel);

        ViewAnswers view = new ViewAnswers(answerQuestions, viewModel);

        prepareQuestions.hide();

        answerQuestions.showAndWait();
    }

    public void gameOver(Match m) {
        ViewModel vm
                = (ViewModel) viewModels.get(ViewModel.class.getSimpleName());
        vm.gameOver(m);
        answerQuestions.close();
    }
}
