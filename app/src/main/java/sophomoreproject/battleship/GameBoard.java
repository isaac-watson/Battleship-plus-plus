package sophomoreproject.battleship;

import java.util.HashSet;
import java.util.Iterator;

import sophomoreproject.battleship.ships.Ship;

/**
 * Created by isaac on 1/31/2018.
 */
public class GameBoard implements GameBoardInterface {

    private Ship[][] board;
    private int boardRows;
    private int boardColumns;
    private static final int DEFAULT_ROWS = 16;
    private static final int DEFAULT_COLUMNS = 24;
    private HashSet<Ship> shipSet;

    public GameBoard() {
        boardRows = DEFAULT_ROWS;
        boardColumns = DEFAULT_COLUMNS;
        board = new Ship[boardRows][boardColumns];
        shipSet = new HashSet<Ship>();
    }

    public int getBoardRows() {
        return boardRows;
    }

    public void setBoardRows(int boardRows) {
        this.boardRows = boardRows;
    }

    public int getBoardColumns() {
        return boardColumns;
    }

    public void setBoardColumns(int boardColumns) {
        this.boardColumns = boardColumns;
    }

    public HashSet<Ship> getShipSet() {
        return shipSet;
    }

    /**
     * A method to add a ship to the board. If the shipSize is greater than 1, then depending on the direction of the Ship,
     * the Ship[][] will add multiple of the same object in the corresponding spot.
     *
     * After it adds the ship to board, it adds the front of the ship to the HashSet shipSet and sets the Ship's coordinates.
     *
     * Still needs checks on the boundaries of the array
     *
     * Will be updated when user input is available
     */
    @Override
    public void addShip(Ship aShip, int xPos, int yPos) {
        int shipSize = aShip.getShipSize();
        boolean isHorizontal = aShip.getHorizontal();
        boolean direction = aShip.getDirection();

        if (shipSize > 1) {
            if (isHorizontal) {
                if (direction) {
                    for (int i = 0; i < shipSize; i++) {
                        board[yPos][xPos - i] = aShip;
                    }
                } else {
                    for (int i = 0; i < shipSize; i++) {
                        board[yPos][xPos + i] = aShip;
                    }
                }
            } else if (!isHorizontal) {
                if (direction) {
                    for (int i = 0; i < shipSize; i++) {
                        board[yPos - i][xPos] = aShip;
                    }
                } else {
                    for (int i = 0; i < shipSize; i++) {
                        board[yPos + i][xPos] = aShip;
                    }
                }
            }
        } else {
            board[yPos][xPos] = aShip;
        }

        aShip.setRowCoord(yPos);
        aShip.setColumnCoord(xPos);
        shipSet.add(aShip);
    }

    /**
     * A method to move the ships in the board.
     *
     *
     */
    @Override
    public void move() {
        //Your implementation here
    }

    /**
     * A method to rotate the ships in the board
     */
    @Override
    public void rotate() {
        //Your implementation here
    }

    /**
     * Gets the amount of ships in the shipSet
     * @return int count is the amount of ships
     */
    public int shipCount() {
        int count = 0;
        if (shipSet.isEmpty()) {
            return 0;
        }
        Iterator<Ship> shipIterator = shipSet.iterator();
        while(shipIterator.hasNext()) {
            count++;
        }
        return count;
    }

    /**
     * Note: If you change the variables of an object, they do not get updated automatically.
     *       HashSets are unordered, so it's easy enough to remove the object from the set and add it back
     *       with its updated variables.
     *
     * Use this method when you modify a ship that is currently on the board
     * @param targetShip the Ship from the shipSet to be updated
     */
    public void updateShipInSet(Ship targetShip) {
        if(shipSet.contains(targetShip)) {
            shipSet.remove(targetShip);
            shipSet.add(targetShip);
        }
    }
}
