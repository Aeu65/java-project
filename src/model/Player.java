package model;

public class Player implements Comparable<Player> {

    private final String name;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Player p) {
        return this.getName().compareTo(p.getName());
    }

    @Override
    public String toString() {
        return name;
    }
}
