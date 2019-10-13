package model;

import java.util.List;

public interface IQuestion {
    public String getName();
    public int getPoints();
    public List<String> getAvailableAnswers();
    public int getRightAnswer();
    public Category getParent();
    public String getJoker();
    public boolean jokerIsGood();
}
