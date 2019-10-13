package model;

import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

public class TournamentList {

    private ObservableList<Tournament> tournamentTab = FXCollections.observableArrayList();

    public TournamentList() {
        initData();
    }

    public Tournament getTournament(int numLine) {
        return tournamentTab.get(numLine);
    }

    public ObservableList<Tournament> getTournaments() {
        return tournamentTab;
    }

    public int nbTournament() {
        return tournamentTab.size();
    }

    public boolean addTournament(Tournament t) {
        tournamentTab.add(t);
        return true;
    }

//    Init
    private void initData() {

        // tournament 1
        Player daria = new Player("Daria");
        Player jane = new Player("Jane");
        Player tom = new Player("Tom");
        Player trent = new Player("Trent");

        ObservableSet<Player> players1 = FXCollections.observableSet();
        players1.add(daria);
        players1.add(jane);
        players1.add(tom);
        players1.add(trent);

        Match m1 = new Match(daria, jane, Result.GAIN_JOUEUR_1);
        Match m2 = new Match(tom, trent, Result.GAIN_JOUEUR_1);

        ArrayList<Match> matchs1 = new ArrayList<>();
        matchs1.add(m1);
        matchs1.add(m2);

        Tournament t1 = new Tournament("Daria", players1, matchs1);

        this.addTournament(t1);

        // tournament 2
        Player harry = new Player("Harry");
        Player ron = new Player("Ron");
        Player hermione = new Player("Hermione");
        Player hagrid = new Player("Hagrid");

        ObservableSet<Player> players2 = FXCollections.observableSet();
        players2.add(harry);
        players2.add(ron);
        players2.add(hermione);
        players2.add(hagrid);

        Match m3 = new Match(harry, ron, Result.GAIN_JOUEUR_1);
        Match m4 = new Match(hermione, hagrid, Result.MATCH_NUL);

        ArrayList<Match> matchs2 = new ArrayList<>();
        matchs2.add(m3);
        matchs2.add(m4);

        Tournament t2 = new Tournament("Harry Potter", players2, matchs2);

        this.addTournament(t2);
    }
}
