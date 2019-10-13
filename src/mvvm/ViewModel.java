package mvvm;

import java.util.List;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Match;
import model.TournamentList;
import model.Tournament;
import model.MatchValidationStatus;
import model.Player;
import model.Result;

public class ViewModel extends AViewModel {

//    Observable Properties
    private final IntegerProperty numTournamentSelected = new SimpleIntegerProperty(-1);
    private final IntegerProperty numMatchSelected = new SimpleIntegerProperty(-1);
    private final IntegerProperty numMatchToSelect = new SimpleIntegerProperty(-1);

    private final StringProperty combo1Selected = new SimpleStringProperty(null);
    private final StringProperty combo2Selected = new SimpleStringProperty(null);
    private final StringProperty combo3Selected = new SimpleStringProperty(null);

    private final StringProperty combo1ToSelect = new SimpleStringProperty(null);
    private final StringProperty combo2ToSelect = new SimpleStringProperty(null);
    private final StringProperty combo3ToSelect = new SimpleStringProperty(null);

    private final BooleanProperty isAddBtnPressed = new SimpleBooleanProperty(false);
    private final BooleanProperty isUpdBtnPressed = new SimpleBooleanProperty(false);
    private final BooleanProperty isDelBtnPressed = new SimpleBooleanProperty(false);
    private final BooleanProperty isPlayBtnPressed = new SimpleBooleanProperty(false);

    private final StringProperty setErrorMsgTo = new SimpleStringProperty(null);
    private final StringProperty setSuccessMsgTo = new SimpleStringProperty(null);

    private final BooleanProperty showDeleteDialog = new SimpleBooleanProperty(false);
    private final BooleanProperty deleteConfirmation = new SimpleBooleanProperty(false);

    // Data
    private final TournamentList tournamentList;

    private Tournament currentlySelectedTournament = null;
    private Match currentlySelectedMatch = null;

    private final ObservableList<Player> players = FXCollections.observableArrayList();
    private final ObservableList<String> playersNames = FXCollections.observableArrayList();

    private final ObservableList<String> resultList = FXCollections.observableArrayList();

    private final ObservableList<Match> matches = FXCollections.observableArrayList();

    public ViewModel(TournamentList tournamentList, Mediator mediator) {
        this.tournamentList = tournamentList;
        this.mediator = mediator;

        for (Result enumValue : Result.values()) {
            resultList.add(enumValue.toString());
        }
        configApplicativeLogic();
    }

    private void configApplicativeLogic() {
        // listeners here...
        configNumTournamentSelected();
        configNumMatchSelected();
        configIsAddBtnPressed();
        configIsUpdBtnPressed();
        configIsDelBtnPressed();
        configDeleteConfirmation();
        configIsPlayBtnPressed();
    }
    
    private void configNumTournamentSelected(){
        numTournamentSelected.addListener((o, old, newValue) -> {
            int index = newValue.intValue();

            if (index >= 0 && index < tournamentList.nbTournament()) {
                currentlySelectedTournament = tournamentList.getTournament(index);

                players.clear();
                players.addAll(currentlySelectedTournament.getPlayers());

                playersNames.clear();
                for (Player player : players) {
                    playersNames.add(player.getName());
                }

                matches.clear();
                matches.addAll(currentlySelectedTournament.getMatchs());
                clearMsg();
            } else {
                currentlySelectedTournament = null;
                players.clear();
                playersNames.clear();
                matches.clear();

                clearComboBoxes();
                clearMsg();
            }
        });
    }
    
    private void configNumMatchSelected(){
        numMatchSelected.addListener((o, old, newValue) -> {
            if (null != currentlySelectedTournament) {
                int index = newValue.intValue();

                if (index >= 0 && index < matches.size()) {
                    currentlySelectedMatch = matches.get(index);

                    combo1ToSelect.setValue(currentlySelectedMatch.getNamePlayer1());
                    combo2ToSelect.setValue(currentlySelectedMatch.getNamePlayer2());
                    combo3ToSelect.setValue(currentlySelectedMatch.getResult().toString());

                    clearMsg();
                } else {
                    clearComboBoxes();
                    clearMsg();
                }
            }

        });
    }
    
    private void configIsAddBtnPressed(){
        isAddBtnPressed.addListener((o, old, newValue) -> {
            if (old && !newValue) { // button released (clickED)

                // get values
                String player1 = combo1Selected.getValue();
                String player2 = combo2Selected.getValue();
                String result = combo3Selected.getValue();

                if (null == player1 || null == player2 || null == result) {
                    setErrorMsg("Un match nécessite deux joueurs et un résultat");
                } else {
                    Match m = new Match(new Player(player1), new Player(player2), Result.valueOf(result));

                    MatchValidationStatus status = Tournament.validate(m, matches);

                    switch (status) {
                        case OK:
                            // insertion dans la liste des matchs

                            if (currentlySelectedTournament.addMatch(m)) {
                                // refresh the list of matches that mirrors the data
                                matches.clear();
                                matches.addAll(currentlySelectedTournament.getMatchs());

                                numMatchToSelect.set(matches.size() - 1);

                                setSuccessMsg("OK");
                            }
                            break;

                        case SAME:
                            setErrorMsg("Un joueur ne peut pas jouer contre lui-meme");
                            break;

                        case COLLISION:
                            setErrorMsg("Un match entre les deux joueurs a déjà été joué");
                            break;
                    }
                }
            }
        });
    }
    
    private void configIsUpdBtnPressed(){
        isUpdBtnPressed.addListener((o, old, newValue) -> {
            if (old && !newValue
                    && null != currentlySelectedMatch) { // button released (clickED)

                // get values
                String player1 = combo1Selected.getValue();
                String player2 = combo2Selected.getValue();
                String result = combo3Selected.getValue();

                if (null == player1 || null == player2 || null == result) {
                    setErrorMsg("Un match nécessite deux joueurs et un résultat");
                } else {
                    Match m = new Match(new Player(player1), new Player(player2), Result.valueOf(result));

                    // matches without currentlySelectedMatch
                    List<Match> mls_filtered
                            = matches.stream()
                                    .filter(s -> !s.equals(currentlySelectedMatch))
                                    .collect(Collectors.toList());

                    MatchValidationStatus status = Tournament.validate(m, mls_filtered);

                    switch (status) {
                        case OK:
                            if (updateSelectedMatch(m)) {
                                int oldPos = numMatchSelected.intValue();

                                // refresh the list of matches that mirrors the data
                                matches.clear();
                                matches.addAll(currentlySelectedTournament.getMatchs());

                                numMatchToSelect.setValue(-1);
                                numMatchToSelect.setValue(oldPos);

                                setSuccessMsg("OK");
                            }
                            break;

                        case SAME:
                            setErrorMsg("Un joueur ne peut pas jouer contre lui-meme");
                            break;

                        case COLLISION:
                            setErrorMsg("Un match entre les deux joueurs a déjà été joué");
                            break;
                    }
                }
            }
        });
    }
    
    private void configIsDelBtnPressed(){
        isDelBtnPressed.addListener((o, old, newValue) -> {
            if (old && !newValue) {
                showDeleteDialog.set(false);
                showDeleteDialog.set(true);
            }
        });
    }
    
    private void configDeleteConfirmation(){
        deleteConfirmation.addListener((o, old, newValue) -> {
            if (!old && newValue
                    && null != currentlySelectedTournament
                    && null != currentlySelectedMatch) {

                if (currentlySelectedTournament.deleteMatch(numMatchSelected.intValue())) {
                    // refresh the list of matches that mirrors the data
                    matches.clear();
                    matches.addAll(currentlySelectedTournament.getMatchs());

                    numMatchToSelect.setValue(matches.size() - 1);

                    showDeleteDialog.setValue(false);
                    setSuccessMsg("OK");
                } else {
                    setErrorMsg("Something went wrong");
                }
            }
        });
    }
    
    private void configIsPlayBtnPressed(){
        isPlayBtnPressed.addListener((o, old, newValue) -> {
            if (old && !newValue) {
                String p1 = combo1Selected.getValue();
                String p2 = combo2Selected.getValue();

                if (null != p1 && null != p2) {
                    clearMsg();

                    Player player1 = new Player(p1);
                    Player player2 = new Player(p2);

                    Match m = new Match(player1, player2, Result.MATCH_NUL);

                    MatchValidationStatus status = Tournament.validate(m, matches);

                    switch (status) {
                        case OK:
                            clearMsg();
                            mediator.launchQuestions(player1, player2);
                            break;

                        case SAME:
                            setErrorMsg("Un joueur ne peut pas jouer contre lui-meme");
                            break;

                        case COLLISION:
                            setErrorMsg("Un match entre les deux joueurs a déjà été joué");
                            break;
                    }

                } else {
                    setErrorMsg("Veuillez choisir deux joueurs");
                }
            }
        });
    }

    // custom methods to DRY Applicative Logic
    private void setErrorMsg(String msg) {
        setErrorMsgTo.setValue(msg);
        setSuccessMsgTo.setValue(null);
    }

    private void setSuccessMsg(String msg) {
        setErrorMsgTo.setValue(null);
        setSuccessMsgTo.setValue(msg);
    }

    private void clearMsg() {
        setErrorMsgTo.setValue(null);
        setSuccessMsgTo.setValue(null);
    }

    private void clearComboBoxes() {
        combo1ToSelect.setValue(null);
        combo2ToSelect.setValue(null);
        combo3ToSelect.setValue(null);
    }

    // Properties to be bound by the View
    public SimpleListProperty<Tournament> tournamentListProperty() {
        return new SimpleListProperty<>(tournamentList.getTournaments());
    }

    public SimpleListProperty<Player> playersProperty() {
        return new SimpleListProperty<>(players);
    }

    public SimpleListProperty<String> playersNamesProperty() {
        return new SimpleListProperty<>(playersNames);
    }

    public SimpleListProperty<Match> matchesProperty() {
        return new SimpleListProperty<>(matches);
    }

    public SimpleListProperty<String> resultListProperty() {
        return new SimpleListProperty<>(resultList);
    }

    public StringProperty combo1ToSelectProperty() {
        return combo1ToSelect;
    }

    public StringProperty combo2ToSelectProperty() {
        return combo2ToSelect;
    }

    public StringProperty combo3ToSelectProperty() {
        return combo3ToSelect;
    }

    public IntegerProperty numTournamentSelectedProperty() {
        return numTournamentSelected;
    }

    public IntegerProperty numMatchSelectedProperty() {
        return numMatchSelected;
    }

    public IntegerProperty numMatchToSelectProperty() {
        return numMatchToSelect;
    }

    public StringProperty setErrorMsgToProperty() {
        return setErrorMsgTo;
    }

    public StringProperty setSuccessMsgToProperty() {
        return setSuccessMsgTo;
    }

    public BooleanProperty showDeleteDialogProperty() {
        return showDeleteDialog;
    }

    // Local Properties bound to the View
    public void bindNumTournamentSelectedProperty(ReadOnlyIntegerProperty prop) {
        numTournamentSelected.bind(prop);
    }

    public void bindNumMatchSelectedProperty(ReadOnlyIntegerProperty prop) {
        numMatchSelected.bind(prop);
    }

    public void bindIsAddBtnPressedProperty(ReadOnlyBooleanProperty prop) {
        isAddBtnPressed.bind(prop);
    }

    public void bindIsUpdBtnPressedProperty(ReadOnlyBooleanProperty prop) {
        isUpdBtnPressed.bind(prop);
    }

    public void bindIsDelBtnPressedProperty(ReadOnlyBooleanProperty prop) {
        isDelBtnPressed.bind(prop);
    }

    public void bindIsPlayBtnPressedProperty(ReadOnlyBooleanProperty prop) {
        isPlayBtnPressed.bind(prop);
    }

    public void bindCombo1SelectedProperty(ReadOnlyObjectProperty<String> prop) {
        combo1Selected.bind(prop);
    }

    public void bindCombo2SelectedProperty(ReadOnlyObjectProperty<String> prop) {
        combo2Selected.bind(prop);
    }

    public void bindCombo3SelectedProperty(ReadOnlyObjectProperty<String> prop) {
        combo3Selected.bind(prop);
    }

    public void bindDeleteConfirmationProperty(ReadOnlyBooleanProperty prop) {
        deleteConfirmation.bind(prop);
    }

    // utils
    private boolean updateSelectedMatch(Match match) {
        if (numMatchSelected.intValue() < 0
                || numMatchSelected.intValue() >= matches.size()) {
            throw new RuntimeException("Index " + numMatchSelected + " out of bounds");
        }

        return currentlySelectedTournament.updateSelectedMatch(match, numMatchSelected.intValue());
    }

    public void gameOver(Match m) {
        MatchValidationStatus status = Tournament.validate(m, matches);
        if (status == MatchValidationStatus.OK
                && currentlySelectedTournament.addMatch(m)) {
            // refresh the list of matches that mirrors the data
            matches.clear();
            matches.addAll(currentlySelectedTournament.getMatchs());

            numMatchToSelect.set(matches.size() - 1);
        }
    }
}
