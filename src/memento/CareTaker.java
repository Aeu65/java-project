package memento;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CareTaker {
    private final Queue<Memento> remaining = new LinkedList<>();
    private final Queue<Memento> secondChance = new LinkedList<>();
    private Queue<Memento> activeQueue = remaining;
    
    public CareTaker(List<Memento> mementos) {
        remaining.addAll(mementos);
    }
    
    public void won() {
        activeQueue.poll();
    }
    
    public void lost(Memento m) {
        activeQueue.poll();
        secondChance.add(m);
    }
    
    public Memento nextRemaining() {
        activeQueue = remaining;
        return activeQueue.peek();
    }
    
    public Memento nextSecondChance() {
        activeQueue = secondChance; 
        return activeQueue.peek();
    }
}
