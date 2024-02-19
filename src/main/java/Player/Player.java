package main.java.Player;

import main.java.Utils.Colour;


abstract class Player {

    private boolean isTurn;
    private int totalPiecesOnBoard;
    private int totalPiecesToPlace;
    private Colour colour;

    public Player(Colour colour) {
        this.colour = colour;
        totalPiecesToPlace = 9;
        totalPiecesOnBoard = 0;
        isTurn = false;
    }

    /**
     * This method is used to check if it is the player's turn.
     * @return
     */
    public boolean isTurn() {
        return isTurn;
    }

    /**
     * This method is used to set the player's turn.
     * @param turn
     */
    public void setTurn(boolean turn) {
        isTurn = turn;
    }

    /**
     * This method is used to get the number of pieces the player has placed on the board.
     * This is to check for the gameover conditions.
     * @return
     */
    public int getTotalPiecesOnBoard() {
        return totalPiecesOnBoard;
    }

    /**
     * This method is used to set the number of pieces the player has placed on the board.
     * @param totalPiecesOnBoard
     */
    public void setTotalPiecesOnBoard(int totalPiecesOnBoard) {
        this.totalPiecesOnBoard = totalPiecesOnBoard;
    }

    /**
     * This method is used to get the number of pieces the player has left to place on the board.
     * @return
     */
    public int getTotalPiecesToPlace() {
        return totalPiecesToPlace;
    }

    /**
     * This method is used to set the number of pieces the player has left to place on the board.
     * @param totalPiecesToPlace
     */
    public void setTotalPiecesToPlace(int totalPiecesToPlace) {
        this.totalPiecesToPlace = totalPiecesToPlace;
    }

    /**
     * This method is used to get the colour of the player.
     * @return
     */
    public Colour getColour() {
        return colour;
    }

    /**
     * This method is used to set the colour of the player.
     * @param colour
     */
    public void setColour(Colour colour) {
        this.colour = colour;
    }

    /**
     * This method is used to remove a token from the board.
     */
    public void removeToken(){
        totalPiecesOnBoard--;
    }

    /**
     * This method is used to place a token on the board.
     */
    public void tilePlaced(){
        totalPiecesToPlace--;
        totalPiecesOnBoard++;
    }

    /**
     * This method is used to activate the player's turn on a turn based game.
     * @return
     */
    public void activateTurn(){
        isTurn = true;
    }

    /**
     * This method is used to deactivate the player's turn on a turn based game.
     * @return
     */
    public void deactivateTurn(){
        isTurn = false;
    }
}
