package main.java.Player;

import main.java.Utils.Colour;

/**
 * @author Priyesh
 *
 * This class is used to represent the human player in the game.
 *
 */

public class HumanPlayer extends Player{

    private String playerName;

    public HumanPlayer(String playerName, Colour colour) {
        super(colour);
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

}
