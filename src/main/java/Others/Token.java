package main.java.Others;

import main.java.Utils.Colour;

/**
 * @author Priyesh
 * <p>
 * This class is used to represent the token.
 */

public class Token {

    private Colour colour;
    private Position position;

    private int isPartOfMillCount;  // each token can be part of 2 mills only

    private int[] millId;   // determines which mill the token is part of. Each mill has its own id

    public Token(Colour colour, Position position) {
        this.colour = colour;
        this.position = position;
        isPartOfMillCount = 0;
        millId = new int[]{0, 0};
    }

    /**
     * This method is used to get the colour of the token.
     * @return
     */
    public Colour getColour() {
        return colour;
    }

    /**
     * This method is used to set the colour of the token.
     * @param colour
     */
    public void setColour(Colour colour) {
        this.colour = colour;
    }

    /**
     * This method is used to get the position of the token.
     * @return
     */
    public Position getPosition() {
        return position;
    }

    /**
     * This method is used to set the position of the token.
     * @param position
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * This method is used to get the number of mills the token is part of.
     * @return
     */
    public int getIsPartOfMillCount() {
        return isPartOfMillCount;
    }

    /**
     * This method is used to set the number of mills the token is part of.
     * @param isPartOfMillCount
     */
    public void setIsPartOfMillCount(int isPartOfMillCount) {
        this.isPartOfMillCount = isPartOfMillCount;
    }

    /**
     * This method is used to increase the number of mills the token is part of.
     */
    public void increaseIsPartOfMillCount() {
        this.isPartOfMillCount = this.isPartOfMillCount + 1;
    }

    /**
     * This method is used to decrease the number of mills the token is part of.
     */
    public void decreaseIsPartOfMillCount() {
        this.isPartOfMillCount = this.isPartOfMillCount - 1;
    }

    /**
     * This method is used to get the mill id the token is part of.
     * @return
     */
    public int[] getMillId() {
        return millId;
    }

    /**
     * This method is used to set the mill id the token is part of.
     * @param millId
     */
    public void setMillId(int[] millId) {
        this.millId = millId;
    }

    /**
     * This method is used to update the mill id the token is part of.
     * @param id
     */
    public void updateMillId(int id) {
        if (millId[0] != 0) {
            millId[1] = id;
        } else {
            millId[0] = id;
        }
    }

    /**
     * This method is used to reset the mill id the token is part of.
     * @param id
     */
    public void resetMillId(int id){
        if (millId[0]==id){
            millId[0] =0;
        }else if (millId[1]==id){
            millId[1] = 0;
        }
    }

    /**
     * This method is used to reset the mill id the token is part of.
     */
    public void selfMillIdReset(){
        this.millId = new int[]{0,0};
    }

}
