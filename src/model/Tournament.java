package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;

public class Tournament extends Observable {

    private final String name;
    private ObservableSet<Player> players = FXCollections.observableSet();
    private final ArrayList<Match> matchs;

    public Tournament(String name, ObservableSet<Player> players, ArrayList<Match> matchs) {
        this.name = name;
        this.players = players;
        this.matchs = matchs;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

//   Player -----------------------------------
    public ObservableSet getPlayers() {
        return players;
    }

    public int nbPlayers() {
        return players.size();
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public ArrayList<String> getNamePlayerList() {
        ArrayList<String> namePlayers = new ArrayList<>();

        for (Player p : players) {
            namePlayers.add(p.getName());
        }
        return namePlayers;
    }

//    Match -----------------------------------
    public ArrayList<Match> getMatchs() {
        return matchs;
    }

    public Match selectedMatch(int numMatch) {
        return matchs.get(numMatch);
    }

    public int nbMatch() {
        return matchs.size();
    }

    public boolean addMatch(Match match) {
        return matchs.add(match);
    }

    public boolean updateSelectedMatch(Match match, int index) {
        matchs.set(index, match);
        return true;
    }

    public boolean deleteMatch(int index) {
        matchs.remove(index);
        return true;
    }

    // ---
    // Validate Match method
    public static MatchValidationStatus validate(Match m, List<Match> mls) {

        // premier cas de match invalide:
        // joueur 1 et joueur 2 sont la même personne
        if (m.getNamePlayer1().equals(m.getNamePlayer2())) {
            return MatchValidationStatus.SAME;
        }

        // recherche de collisions
        // on parcourt la liste des match
        for (Match existing : mls) {

            if (existing.getNamePlayer1().equals(m.getNamePlayer1())
                    && existing.getNamePlayer2().equals(m.getNamePlayer2())) {
                return MatchValidationStatus.COLLISION;
            }

            // check de collision du match inverse
            if (existing.getNamePlayer1().equals(m.getNamePlayer2())
                    && existing.getNamePlayer2().equals(m.getNamePlayer1())) {
                return MatchValidationStatus.COLLISION;
            }
        }
        return MatchValidationStatus.OK;
    }
}
