package model;

import element.Elem;
import element.Elements;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Game {

    private final ObservableList<Question> qls = FXCollections.observableArrayList();
    private final ObservableList<Question> choosenls = FXCollections.observableArrayList();
    private final ArrayList<Category> catLs = new ArrayList<>();

    public static final int MAXNBPOINTS = 10;
    private Match match;
    private Category currentlySelected;
    private final Category root;
    
    // Managing the state of the game
    private int totalPoints = 0;
    private int maxPossibleScore = 0;
    private Result result;
    

    public Game(Match m) {
        this.match = m;

        File f = new File("src/data/Questions.JSON");
        List<Elem> elemList = Elements.loadElemsFromFile(f.getPath());
        root = new Category(new Elem("All", null, 0, 0, elemList, null, null), null);

        flattenTree(root, catLs, qls);
    }

    public boolean maxNbChoosenReached(Question current) {
//        current sum of points for choosen question list
        int currentNbPoints
                = choosenls.stream()
                        .map(q -> q.getPoints())
                        .reduce(0, (a, b) -> a + b);
//         to which we add the current nb of points
        currentNbPoints += current.getPoints();

        return !choosenls.contains(current)
                && currentNbPoints <= MAXNBPOINTS;
    }

//     flatten the tree starting from the node e
//     then adds all categories/questions (including e) recursively
    public void flattenTree(TreeElem e,
            List<Category> categories,
            List<Question> questions) {
        if (e instanceof Category) {
            Category e2 = (Category) e;

            if (null != categories) {
                categories.add(e2);
            }

            for (TreeElem f : e2.getChildren()) {
                flattenTree(f, categories, questions);
            }
        } else if (null != questions) {
            Question q = (Question) e;
            questions.add(q);
        }
    }

//    refreshes the list of questions
    public void refreshQls(Category g) {
        qls.clear();
        flattenTree(g, null, qls);
    }

    public void removeQuestion(Question q) {
        q.getParent().removeChild(q);
        refreshQls(currentlySelected);
    }

    public void restoreChild(Question q) {
        q.getParent().addChild(q);
        refreshQls(currentlySelected);
    }

    public void setCurrentlySelected(Category currentlySelected) {
        this.currentlySelected = currentlySelected;
    }

    public void setMaxPossibleScore(int maxPossibleScore) {
        this.maxPossibleScore = maxPossibleScore;
    }
    
//    Getters
    public Match getMatch() {
        return match;
    }

    public ObservableList<Question> getQls() {
        return qls;
    }

    public ObservableList<Question> getChoosenls() {
        return choosenls;
    }

    public Category getRoot() {
        return root;
    }

    public ArrayList<Category> getCatLs() {
        return catLs;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public int getMaxPossibleScore() {
        return maxPossibleScore;
    }
    
    // Manage the state of the game
    
    public void updateVictory(OriginatorQuestion currentQuestion) {
        switch(currentQuestion.getJokerUsedStatus()) {
                case UNUSED_JOKER:
                    totalPoints += currentQuestion.getPoints();
                    break;
                case USED_BAD_JOKER:
                    totalPoints += 2;
                    break;
                case USED_GOOD_JOKER:
                default:
                    totalPoints += 1;
                    break;
        }
    }
    
    public Result manageStop(boolean lastTurn) {
        int middle = maxPossibleScore / 2;
        
        if(totalPoints > middle) {
            result = Result.GAIN_JOUEUR_2;
            
            // we can't know in advance if the user has lost the game
            // because,
            // with the Memento Pattern
            // and the 'Replay wrongly answered questions' feature
            // the player can at any moment inverse the odds
            // by answering correcly all of them (with a bit of chance)
            
        } else if (lastTurn) {
            if (totalPoints == middle) {
                result = Result.MATCH_NUL;
            } else if (totalPoints > middle) {
                result = Result.GAIN_JOUEUR_2;
            } else {
                result = Result.GAIN_JOUEUR_1;
            }
        }
        
        if(result != null) {
            match.setResult(result);
        }
        
        return result;
    }
    
}
