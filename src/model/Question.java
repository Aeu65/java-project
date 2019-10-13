package model;

import element.*;
import java.util.List;
import java.util.Random;

public final class Question implements Comparable<Question>, TreeElem, IQuestion {

    private static final Random GEN = new Random();
    
    private final String name;
    private final int points;
    private final List<String> availableAnswers;
    private final int rightAnswer;
    private final String joker;
    private final boolean jokerIsGood;

    private Category parent;

    public Question(Elem e, Category parent) {
        this.name = e.name;
        this.parent = parent;
        this.points = e.points;
        this.availableAnswers = e.responses;
        this.rightAnswer = e.numCorrectResponse;
        
        if(e.points == 3) {
            this.joker = randomJoker(e.hint, e.fakeHint);
            jokerIsGood = this.joker == e.hint;
        } else {
            this.joker = null;
            jokerIsGood = false;
        }
    }

    // Utils
    private boolean chanceJoker() { return GEN.nextInt(5) == 0; }
    // Constructor helper
    public final String randomJoker(String hint, String fakeHint) {
        if(chanceJoker()) {
            return fakeHint;
        }
        return hint;
    }

    // Getters
    public String getName() { return name; }
    public int getPoints() { return points; }
    public List<String> getAvailableAnswers() { return availableAnswers; }
    public int getRightAnswer() { return rightAnswer; }
    public Category getParent() { return parent; }
    public String getJoker() { return joker; }
    public boolean jokerIsGood() { return jokerIsGood; }

    @Override
    public String toString() { return name; }

    @Override
    public int compareTo(Question o) {
        return this.name.compareTo(o.name);
    }

}

