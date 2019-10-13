package model;

public class Match {

    private final Player player1;
    private final Player player2;
    private Result result;

    public Match(Player player1, Player player2, Result result) {
        this.player1 = player1;
        this.player2 = player2;
        this.result = result;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public Result getResult() {
        return result;
    }

//    String
    public String getNamePlayer1() {
        return player1.getName();
    }

    public String getNamePlayer2() {
        return player2.getName();
    }

    @Override
    public String toString() {
        return "Match{" + "player1=" + player1 + ", player2=" + player2 + ", result=" + result + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Match other = (Match) obj;

        if (!this.player1.getName().equals(other.player1.getName())) {
            return false;
        }
        if (!this.player2.getName().equals(other.player2.getName())) {
            return false;
        }
        if (!this.result.toString().equals(other.result.toString())) {
            return false;
        }
        return true;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
