package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import memento.CareTaker;
import memento.Memento;

public class OriginatorQuestion implements IQuestion {
    private final CareTaker careTaker;
    private static final Random GEN = new Random();
    
    // State
    private Question question;
    private Joker jokerUsedStatus = Joker.UNUSED_JOKER;
    private int lastWrongAnswer = -1;
    private int pos = 0;
    
    public OriginatorQuestion(List<Question> qls) {
        // creating the careTaker
        List<Memento> mementos = new ArrayList<>();
        for (int i = 0; i < qls.size(); ++i) {
            Memento m = new MementoImpl(qls.get(i), i +1);
            mementos.add(m);
        }
        careTaker = new CareTaker(mementos);
        
        // initialise to the Memento corresponding to the first question
        setCurrentState(careTaker.nextRemaining());
    }
    
    // Getters
    public Joker getJokerUsedStatus() { return jokerUsedStatus; }
    public int getLastWrongAnswer() { return lastWrongAnswer; }
    public int getPos() { return pos; }
    
    // IQuestion Getters
    @Override public String getName() { return question.getName(); }
    @Override public int getPoints() { return question.getPoints(); }
    @Override public List<String> getAvailableAnswers() {
	return question.getAvailableAnswers();
    }
    @Override public int getRightAnswer() { return question.getRightAnswer(); }
    @Override public Category getParent() { return question.getParent(); }
    @Override public String getJoker() { return question.getJoker(); }
    @Override public boolean jokerIsGood() { return question.jokerIsGood(); }
    
    // Util
    private boolean secondChance() { return GEN.nextInt(5) == 0; }
    
    // Public methods that change the current state
    
    public String consumeJoker() {
        if(question.jokerIsGood()) {
            jokerUsedStatus = Joker.USED_GOOD_JOKER;
        } else {
            jokerUsedStatus = Joker.USED_BAD_JOKER;
        }
        return question.getJoker();
    }
    
    // false = no remaining questions
    public boolean won() {
        careTaker.won();
        
        Memento next = null;
        
        if(secondChance()) {
            next = careTaker.nextSecondChance();
        }
        
        if(null == next) {
            next = careTaker.nextRemaining();
        }
        
        return setCurrentState(next);
    }
    
    // false = no remaining questions
    public boolean lost(int lastWrongAnswer) {
        this.lastWrongAnswer = lastWrongAnswer;
        careTaker.lost(new MementoImpl(this));
        return setCurrentState(careTaker.nextRemaining());
    }
    
    // Memento stuff
    
    private boolean setCurrentState(Memento m) {
        if(null == m) return false;
        
        if( !(m instanceof MementoImpl)) {
            throw new RuntimeException("Memento instance must be of type "
                    + MementoImpl.class.getName());
        }
        
        MementoImpl mi = (MementoImpl)m;
        this.question = mi.question;
        this.jokerUsedStatus = mi.jokerUsedStatus;
        this.lastWrongAnswer = mi.lastWrongAnswer;
        this.pos = mi.pos;
        return true;
    }
    
    private static class MementoImpl implements Memento {
        private final Question question;
        private Joker jokerUsedStatus = Joker.UNUSED_JOKER;
        private int lastWrongAnswer = -1;
        private int pos = 0;
        
        // to populate the careTaker list
        private MementoImpl(Question q, int i) {
            this.question = q;
            this.pos = i;
        }
        
        // to save and retrieve state
        private MementoImpl(OriginatorQuestion oq) {
            this.question = oq.question;
            this.jokerUsedStatus = oq.jokerUsedStatus;
            this.lastWrongAnswer = oq.lastWrongAnswer;
            this.pos = oq.pos;
        }
    }
}
