package main.java.Others;

import main.java.Utils.Colour;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.*;

/**
 * @author Priyesh
 * <p>
 * This class is used to represent the board in the game.
 * It contains the positions of the board and the tokens placed on the board.
 * It also contains the logic to validate the placement of the tokens.
 */

public class Board {

    private Map<Position, Integer> boardPositions;
    private Map<Position, Token> occupiedPosition;

    private ObjectProperty<Position> tokenPlacedPosition;

    private Position oldPosition;

    private Map<Integer, List<Token>> millSets;

    private int millId; // Mill id is used to identify the mill formation


    /**
     * Constructor for the board class which initializes the board positions and the occupied positions.
     */
    public Board() {
        boardPositions = new Position().getAllPositions();
        occupiedPosition = new HashMap<>();
        tokenPlacedPosition = new SimpleObjectProperty<>();
        millSets = new HashMap<>();
        millId = 0;
    }

    /**
     * Getter to get current position of the token
     */
    public Position getOldPosition() {
        return oldPosition;
    }

    /**
     * Setter to set current position of the token
     */
    public void setOldPosition(Position oldPosition) {
        this.oldPosition = oldPosition;
    }

    /**
     * A Map of all the positions on the board and their corresponding position index
     */
    public Map<Position, Integer> getBoardPositions() {
        return boardPositions;
    }

    /**
     * Setter to set the board positions
     */
    public void setBoardPositions(Map<Position, Integer> boardPositions) {
        this.boardPositions = boardPositions;
    }

    /**
     * A Map of all the positions on the board which are occupied by the tokens
     */
    public Map<Position, Token> getOccupiedPosition() {
        return occupiedPosition;
    }

    /**
     * Setter to set the occupied positions
     */
    public void setOccupiedPosition(Map<Position, Token> occupiedPosition) {
        this.occupiedPosition = occupiedPosition;
    }

    /**
     * This method is used to place a token on the board
     */
    public Position getTokenPlacedPosition() {
        return tokenPlacedPosition.get();
    }

    public ObjectProperty<Position> tokenPlacedPositionProperty() {
        return tokenPlacedPosition;
    }

    /**
     *
     * @param tokenPlacedPosition
     */
    public void setTokenPlacedPosition(Position tokenPlacedPosition) {
        this.tokenPlacedPosition.set(tokenPlacedPosition);
    }

    /**
     * MillId is used to identify the mill formations on the board. Mill id are always unique and increasing.
     * It it used to determine the tokens that are part of the mill.
     *
     */
    public void increaseMillId() {
        this.millId = this.millId + 1;
    }

    /**
     * This method is used to validate whether a token can be placed at the new position from the current position
     *
     * @param position the position on the board where the token is at
     */
    public List<Position> getValidPositions(Position position) {
        Integer p1 = boardPositions.get(position);
        List<Position> validPositions = new ArrayList<>();

        if (p1 < 8 && p1 % 2 == 0) {
            validPositions.add(getKeyByValue(boardPositions, p1 - 1));
            validPositions.add(getKeyByValue(boardPositions, p1 + 1));
            validPositions.add(getKeyByValue(boardPositions, p1 + 8));
        } else if (p1 == 8) {
            validPositions.add(getKeyByValue(boardPositions, p1 - 7));
            validPositions.add(getKeyByValue(boardPositions, p1 - 1));
            validPositions.add(getKeyByValue(boardPositions, p1 + 8));
        } else if (p1 < 16 && p1 % 2 == 0) {
            validPositions.add(getKeyByValue(boardPositions, p1 - 8));
            validPositions.add(getKeyByValue(boardPositions, p1 + 1));
            validPositions.add(getKeyByValue(boardPositions, p1 + 8));
            validPositions.add(getKeyByValue(boardPositions, p1 - 1));
        }else if (p1 == 16) {
            validPositions.add(getKeyByValue(boardPositions, p1 - 7));
            validPositions.add(getKeyByValue(boardPositions, p1 - 1));
            validPositions.add(getKeyByValue(boardPositions, p1 + 8));
            validPositions.add(getKeyByValue(boardPositions, p1 - 8));
        }else if (p1 < 24 && p1 % 2 == 0) {
            validPositions.add(getKeyByValue(boardPositions, p1 - 1));
            validPositions.add(getKeyByValue(boardPositions, p1 + 1));
            validPositions.add(getKeyByValue(boardPositions, p1 - 8));
        }else if (p1 == 24 ) {
            validPositions.add(getKeyByValue(boardPositions, p1 - 1));
            validPositions.add(getKeyByValue(boardPositions, p1 - 7));
            validPositions.add(getKeyByValue(boardPositions, p1 - 8));}
        else if (p1 ==1 || p1 == 9 || p1 == 17) {
            validPositions.add(getKeyByValue(boardPositions, p1 + 1));
            validPositions.add(getKeyByValue(boardPositions, p1 + 7));
        }else{
            validPositions.add(getKeyByValue(boardPositions, p1 - 1));
            validPositions.add(getKeyByValue(boardPositions, p1 + 1));
        }

        return validPositions;
    }

    /**
     * This method is used to place a new token on the board
     * In PLACEMENT phase, new tokens are created and placed on the board
     *
     * @param position the position on the board where the token is to be placed
     * @param colour   the colour of the token to be placed
     */
    //
    public void placeNewToken(Position position, Colour colour) {
        if (!occupiedPosition.containsKey(position)) {
            occupiedPosition.put(position, new Token(colour, position));
        } else {
            System.out.print("POSITION ALREADY HAVE TOKEN");
        }
    }

    /**
     * This method is used to move a token from one position to another
     * In MOVEMENT phase, tokens are moved from one position to another
     *
     * @param newPosition the position on the board where the token is to be placed
     */
    public void moveToken(Position newPosition) {
        //get the token
        Token token = occupiedPosition.get(oldPosition);

        //if token part of mill, moving cause mill to change
        if (token.getIsPartOfMillCount() > 0) {
            reduceIsMillCount(token);
        }

        //remove old position from list
        occupiedPosition.remove(oldPosition);

        //update token position and place back into list
        token.setPosition(newPosition);
        occupiedPosition.put(newPosition, token);
    }

    /**
     * Returns neighbouring positions where a mill is possible
     *
     * @param newPosition the position on the board where the token is to be placed
     */
    public List<Position> getNeighbours(Position newPosition) {
        Integer num1 = boardPositions.get(newPosition);
        List<Position> neighbours = new ArrayList<>();
        Position p2;
        Position p3;
        Position p4;
        Position p5;

        if (num1 % 2 == 0) {
            //ring 1
            if (num1 < 9) {
                p2 = getKeyByValue(boardPositions, (num1 + 8));
                p3 = getKeyByValue(boardPositions, (num1 + 16));
                p4 = getKeyByValue(boardPositions, (num1 - 1));
                p5 = getKeyByValue(boardPositions, (num1 + 1));
            }
            //ring 2
            else if (num1 < 17) {
                p2 = getKeyByValue(boardPositions, (num1 - 8));
                p3 = getKeyByValue(boardPositions, (num1 + 8));
                p4 = getKeyByValue(boardPositions, (num1 - 1));
                p5 = getKeyByValue(boardPositions, (num1 + 1));
            }
            //ring 3
            else {
                p2 = getKeyByValue(boardPositions, (num1 - 8));
                p3 = getKeyByValue(boardPositions, (num1 - 16));
                p4 = getKeyByValue(boardPositions, (num1 - 1));
                p5 = getKeyByValue(boardPositions, (num1 + 1));
            }

            if (num1 % 8 == 0) {
                p5 = getKeyByValue(boardPositions, (num1 - 7));
            }
        } else {
            if (num1 == 1 || num1 == 9 || num1 == 17) {
                p2 = getKeyByValue(boardPositions, (num1 + 7));
                p3 = getKeyByValue(boardPositions, (num1 + 6));
            } else {
                p2 = getKeyByValue(boardPositions, (num1 - 1));
                p3 = getKeyByValue(boardPositions, (num1 - 2));
            }
            p4 = getKeyByValue(boardPositions, (num1 + 1));
            p5 = getKeyByValue(boardPositions, (num1 + 2));

            if (num1 == 7 || num1 == 15 || num1 == 23) {
                p5 = getKeyByValue(boardPositions, (num1 - 6));
            }
        }

        neighbours.add(p2);
        neighbours.add(p3);
        neighbours.add(p4);
        neighbours.add(p5);
        return neighbours;
    }

    /**
     * To check if a mill has formed after the player moved a token
     *
     * @param newPosition the position on the board where the token is to be placed
     */
    public boolean checkIfMill(Position newPosition) {
        List<Position> millNeighbours = new ArrayList<>();
        List<Position> neighbours = getNeighbours(newPosition);
        Token token = occupiedPosition.get(newPosition);

        int count = 0;
        for (int i = 0; i < 2; i++) {
            count = 0;
            for (int j = i; j < i + 2; j++) {
                Token neighbourToken = occupiedPosition.get(neighbours.get(i + j));
                if (neighbourToken != null) {
                    if (occupiedPosition.get(neighbours.get(i + j)).getColour() == token.getColour()) {
                        count++;
                        if (count == 2) {
                            increaseMillId();

                            Position p1 = neighbours.get(i + j - 1);
                            Position p2 = neighbours.get(i + j);

                            millNeighbours.add(p1);
                            millNeighbours.add(p2);

                            //update the token mill count
                            occupiedPosition.get(p1).increaseIsPartOfMillCount();
                            occupiedPosition.get(p2).increaseIsPartOfMillCount();
                            token.increaseIsPartOfMillCount();


                            //update the mill id of a token
                            occupiedPosition.get(p1).updateMillId(millId);
                            occupiedPosition.get(p2).updateMillId(millId);
                            token.updateMillId(millId);

                            //put the new mill to the hashmap
                            millSets.put(millId, Arrays.asList(token, occupiedPosition.get(p1),
                                    occupiedPosition.get(p2)));
                        }
                    }
                }
            }
        }

        return millNeighbours.size() >= 2;
    }

    /**
     * Check if a token can be removed or not
     * Cannot be removed if part of any mill
     *
     * @param tokenPosition the position on the board where the token is to be removed
     */
    public boolean canBeRemoved(Position tokenPosition, Boolean useReduceIsMillCount) {
        System.out.println(occupiedPosition);
        System.out.println("tokenPosition" + tokenPosition.toString());

        //loop for checking if any token can be removed, edge case where there exist some token that can be removed
        for (Token token : occupiedPosition.values()){
            System.out.println(token);
            if (occupiedPosition.get(tokenPosition)==null){
                return false;
            }
            if (token.getIsPartOfMillCount()==0 && token.getColour() == occupiedPosition.get(tokenPosition).getColour())  {
                Token t = occupiedPosition.get(tokenPosition);
                return t.getIsPartOfMillCount() == 0;
            }
        }
//        canBeRemoved1(tokenPosition);
        // for cases where force removal is enforced
        if (useReduceIsMillCount) {
            reduceIsMillCount(occupiedPosition.get(tokenPosition));
        }
        return true;

    }


    /**
     * Method removes token from occupiedPosition HashMap
     * Returns true or false based on if it can be removed
     *
     * @param tokenPosition the position on the board where the token is to be removed
     */
    public boolean removeToken(Position tokenPosition) {
        if (canBeRemoved(tokenPosition, true)) {
            occupiedPosition.remove(tokenPosition);
            return true;
        }
        return false;
    }

    /**
     * reduces the is part of mill count for a set of tokens
     * resets the mill id if no longer part of the mill
     *
     * @param token the token which is part of mill that has been moved
     */
    public void reduceIsMillCount(Token token) {
        int[] ids = token.getMillId().clone();
        List<Token> tokens = new ArrayList<>();

        if (millSets.get(ids[0]) != null) {
            tokens.addAll(millSets.get(ids[0]));
        }
        if (millSets.get(ids[1]) != null) {
            tokens.addAll(millSets.get(ids[1]));
        }
        for (Token t : tokens) {
            t.decreaseIsPartOfMillCount();
            t.resetMillId(ids[0]);
            t.resetMillId(ids[1]);
        }
    }


    //https://stackoverflow.com/questions/1383797/java-hashmap-how-to-get-key-from-value
    /**
     * Method to get the key from a value in a hashmap
     *
     * @param map the hashmap
     * @param value the value to be searched for
     */
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
