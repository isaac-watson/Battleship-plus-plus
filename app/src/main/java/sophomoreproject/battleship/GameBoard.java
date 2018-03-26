package sophomoreproject.battleship;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import sophomoreproject.battleship.ships.Ship;

/**
 * Created by isaac on 1/31/2018.
 */
public class GameBoard implements GameBoardInterface, Panel {

    private Ship[][] board;
    private int boardRows;
    private int boardColumns;
    private static final int DEFAULT_ROWS = 16;
    private static final int DEFAULT_COLUMNS = 24;
    private HashSet<Ship> shipSet;
    private Rect waterBox;
    private Drawable waterImage;
    private boolean isScrolling = false;
    public boolean ready = false; //ready should be set to true when it is the user's turn, and they aren't placing ships anymore. Otherwise be false
    private Point locator = new Point(0, 0);
    private Point masterPoint = new Point(0, 0);
    private Context context;
    private GamePanel gp;
    private int playerTurn;
    private Player p1, p2;
    private TextView winningText = null;
    private Layout winningScreen = null;
    public GameBoard(Context context, GamePanel gp) {
        this.context = context;
        this.gp = gp;
        boardRows = DEFAULT_ROWS;
        boardColumns = DEFAULT_COLUMNS;
        board = new Ship[boardRows][boardColumns];
        shipSet = new HashSet<>();
        waterBox = new Rect();
        waterBox.set(0, 0, 128 * 24, 128 * 16);
        waterImage = context.getResources().getDrawable(R.drawable.water_old);
        waterImage.setBounds(waterBox);
        p1 = new Player();
        p2 = new Player();
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

    Point getMasterPoint() {
        return masterPoint;
    }

    public HashSet<Ship> getShipSet() {
        return shipSet;
    }

    public Player getP1() {
        return p1;
    }

    public void setP1(Player p1) {
        this.p1 = p1;
    }

    public Player getP2() {
        return p2;
    }

    public void setP2(Player p2) {
        this.p2 = p2;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }

    /**
     * A method to add a cruiser to the board. If the shipSize is greater than 1, then depending on the direction of the Ship,
     * the Ship[][] will add multiple of the same object in the corresponding spot.
     * <p>
     * After it adds the cruiser to board, it adds the front of the cruiser to the HashSet shipSet and sets the Ship's coordinates.
     * <p>
     * Still needs checks on the boundaries of the array
     * <p>
     * Will be updated when user input is available
     */
    @Override
    public void addShip(Ship aShip, int xPos, int yPos) throws IndexOutOfBoundsException {
        int shipSize = aShip.getShipSize();
        boolean isHorizontal = aShip.getHorizontal();
        boolean direction = aShip.getDirection();

        /*if (xPos < 0 || yPos < 0) {
            throw new IllegalArgumentException("There is no negative position on the board");
        }
        if (xPos > boardColumns || yPos > boardRows) {
            throw new IllegalArgumentException("Can't place a ship beyond the board's boundaries A");
        }
        if (!checkIndexBoundaries(aShip)) {
            throw new IllegalStateException("Can't place a ship beyond the board's boundaries B");
        }*/
        if (playerTurn == 0 && xPos > 11) {
            throw new IllegalArgumentException("It's player 1's turn and you're trying to place into player 2's territory");
        } else if (playerTurn == 1 && xPos < 12) {
            throw new IllegalArgumentException("It's player 2's turn and you're trying to place into player 1's territory");
        }
        if (!checkPlacementInEnemy(aShip, xPos)) {
            throw new IllegalArgumentException("checkPlacementInEnemy returns false");
        }
        if (nullCountOfShipSize(shipSize, xPos, yPos, isHorizontal, direction) != shipSize) {
            throw new IllegalStateException("There is already a ship there");
        }


        aShip.setRowCoord(yPos);
        aShip.setColumnCoord(xPos);
        if (playerTurn == 0) {
            if (hasEnoughPoints(aShip, p1)) {
                p1.setAvailablePoints(p1.getAvailablePoints() - aShip.getShipCost());
            } else {
                throw new IllegalStateException("player 1 doesn't have enough points");
            }
        }
        if (playerTurn == 1) {
            if (hasEnoughPoints(aShip, p2)) {
                p2.setAvailablePoints(p2.getAvailablePoints() - aShip.getShipCost());
            } else {
                throw new IllegalStateException("player 2 doesn't have enough points");
            }
        }

        if (shipSize > 1) {
            if (isHorizontal) {
                if (direction) {
                    for (int i = 0; i < shipSize; i++) {
                        if (board[yPos][xPos - i] == null) {
                            board[yPos][xPos - i] = aShip;
                        }
                    }
                } else if (!direction) {
                    for (int i = 0; i < shipSize; i++) {
                        if (board[yPos][xPos + i] == null) {
                            board[yPos][xPos + i] = aShip;
                        }
                    }
                }
            } else {
                if (direction) {
                    for (int i = 0; i < shipSize; i++) {
                        if (board[yPos + i][xPos] == null) {
                            board[yPos + i][xPos] = aShip;
                        }

                    }
                } else {
                    for (int i = 0; i < shipSize; i++) {
                        if (board[yPos - i][xPos] == null) {
                            board[yPos - i][xPos] = aShip;
                        }
                    }
                }
            }
        } else {
            board[yPos][xPos] = aShip;
        }
        shipSet.add(aShip);
        aShip.setRowCoord(yPos);
        aShip.setColumnCoord(xPos);
    }

    /**
     * A method to move the ships in the board
     */
    @Override
    public void move(Ship aShip, int xPos, int yPos, int pmove) {
        int shipX = aShip.getColumnCoord();
        int shipY = aShip.getRowCoord();
        int shipSize = aShip.getShipSize();
        int nMove = aShip.getnMove();
        boolean isHorizontal = aShip.getHorizontal();
        boolean direction = aShip.getDirection();

        if (!checkIndexBoundaries(aShip)) {
            throw new IllegalStateException("Can't place a ship beyond the board's boundaries");
        } else if (nullCountOfShipSize(shipSize, xPos, yPos, isHorizontal, direction) != shipSize) {
            throw new IllegalStateException("There is already a ship there");
        } else if (pmove > nMove) {
            throw new IllegalStateException("This ship cannot move that many spaces");
        } else {
            if (shipSize > 1) {
                if (isHorizontal) {
                    if (direction) {

                        xPos = xPos + pmove;
                        for (int i = 0; i < shipSize; i++) {
                            board[yPos][xPos - i] = aShip;
                        }
                    } else if (!direction) {

                        xPos = xPos - pmove;
                        for (int i = 0; i < shipSize; i++) {
                            board[yPos][xPos + i] = aShip;
                        }
                    }
                } else if (!isHorizontal) {
                    if (direction) {

                        yPos = yPos + pmove;
                        for (int i = 0; i < shipSize; i++) {
                            board[yPos - i][xPos] = aShip;
                        }
                    } else {

                        yPos = yPos - pmove;
                        for (int i = 0; i < shipSize; i++) {
                            board[yPos + i][xPos] = aShip;
                        }
                    }
                }
            }
            updateShipInSet(aShip);
        }
    }

    @Override
    public void rotateLeft(Ship aShip, int xPos, int yPos) {
        int shipX = aShip.getColumnCoord();
        int shipY = aShip.getRowCoord();
        int shipSize = aShip.getShipSize();

        boolean isHorizontal = aShip.getHorizontal();
        boolean direction = aShip.getDirection();

        if (!checkIndexBoundaries(aShip)) {
            throw new IllegalStateException("Can't place a ship beyond the board's boundaries");
        } else if (nullCountOfShipSize(shipSize, xPos, yPos, isHorizontal, direction) != shipSize) {
            throw new IllegalStateException("There is already a ship there");
        } else {
            if (shipSize > 1) {
                if (isHorizontal) {
                    if (direction) {

                        aShip.setHorizontal(false);
                        for (int i = 0; i < shipSize; i++) {
                            board[yPos - i][xPos] = aShip;
                        }
                    } else if (!direction) {

                        aShip.setHorizontal(false);


                        for (int i = 0; i < shipSize; i++) {
                            board[yPos + i][xPos] = aShip;
                        }
                    }
                } else if (!isHorizontal) {
                    if (direction) {

                        aShip.setHorizontal(true);
                        aShip.setDirection(false);
                        for (int i = 0; i < shipSize; i++) {
                            board[yPos][xPos - i] = aShip;
                        }
                    } else {

                        aShip.setHorizontal(true);
                        aShip.setDirection(true);
                        for (int i = 0; i < shipSize; i++) {
                            board[yPos][xPos + i] = aShip;
                        }
                    }
                }
            }
            updateShipInSet(aShip);
        }
        shipSet.add(aShip);
    }

    /**
     * A method to move the ships in the board.
     */
    @Override
    public void rotateRight(Ship aShip, int xPos, int yPos) {
        int shipX = aShip.getColumnCoord();
        int shipY = aShip.getRowCoord();
        int shipSize = aShip.getShipSize();

        boolean isHorizontal = aShip.getHorizontal();
        boolean direction = aShip.getDirection();

        if (!checkIndexBoundaries(aShip)) {
            throw new IllegalStateException("Can't place a ship beyond the board's boundaries");
        } else if (nullCountOfShipSize(shipSize, xPos, yPos, isHorizontal, direction) != shipSize) {
            throw new IllegalStateException("There is already a ship there");
        } else {
            if (shipSize > 1) {
                if (isHorizontal) {
                    if (direction) {

                        aShip.setHorizontal(false);
                        aShip.setDirection(false);
                        for (int i = 0; i < shipSize; i++) {
                            board[yPos + i][xPos] = aShip;
                        }
                    } else if (!direction) {

                        aShip.setHorizontal(false);
                        aShip.setDirection(true);

                        for (int i = 0; i < shipSize; i++) {
                            board[yPos - i][xPos] = aShip;
                        }
                    }
                } else if (!isHorizontal) {
                    if (direction) {

                        aShip.setHorizontal(true);

                        for (int i = 0; i < shipSize; i++) {
                            board[yPos][xPos + i] = aShip;
                        }
                    } else {

                        aShip.setHorizontal(true);

                        for (int i = 0; i < shipSize; i++) {
                            board[yPos][xPos - i] = aShip;
                        }
                    }
                }
            }
            updateShipInSet(aShip);
        }
    }

    /**
     * A method to see if a player has enough points to place a ship on the board
     *
     * @param aShip  the ship to be placed so we can get it's cost
     * @param player player 1 or player 2
     * @return true if they have enough, false if they don't
     */
    public boolean hasEnoughPoints(Ship aShip, Player player) {
        int playerPoints = player.getAvailablePoints();
        int shipCost = aShip.getShipCost();

        if (playerPoints - shipCost < 0) {
            return false;
        }

        return true;
    }

    /**
     * A method to see if your ship would be placed within the enemy's area
     *
     * @param aShip the ship to be placed
     * @param x     the x position of the head of the ship
     * @return true if it WOULD NOT go in enemy territory (across red line),
     * false if it WOULD go in enemy territory
     */
    public boolean checkPlacementInEnemy(Ship aShip, int x) {
        int shipSize = aShip.getShipSize();
        boolean isHorizontal = aShip.getHorizontal();
        boolean direction = aShip.getDirection();

        if (playerTurn == 0 && !direction && isHorizontal) {
            if (shipSize + x > 12) {
                return false;
            }
        } else if (playerTurn == 1 && direction && isHorizontal) {
            if (x - shipSize < 11) {
                return false;
            }
        }
        return true;
    }

    /**
     * A boolean that checks if the area around the ship would go outside the index of the board.
     *
     * @return true if it does not go outside the index of the board. false if it would.
     */

    @Override
    public boolean checkIndexBoundaries(Ship aShip) {
        int shipSize = aShip.getShipSize();
        int shipX = aShip.getColumnCoord();
        int shipY = aShip.getRowCoord();
        boolean isHorizontal = aShip.getHorizontal();
        boolean direction = aShip.getDirection();

        if (direction && !isHorizontal && shipY < shipSize - 1) {
            return false;
        } else if (!direction && !isHorizontal && shipY + (shipSize - 1) > boardColumns) {
            return false;
        } else if (direction && isHorizontal && shipX - (shipSize - 1) < 0) {
            return false;
        } else if (!direction && isHorizontal && shipX + (shipSize - 1) > boardRows) {
            return false;
        }
        return true;
    }

    /**
     * A method that checks the board to see if there is a ship or not where you would want to place a ship
     *
     * @param shipSize     The size of the ship
     * @param startX       The column position of the front of the ship
     * @param startY       The row position of the front of the ship
     * @param isHorizontal The way the ship is facing
     * @param direction    Left or Right if it's horizontal, Up or Down if it's vertical
     * @return counter, the amount of nulls in the board of where the ship would take a spot.
     */
    @Override
    public int nullCountOfShipSize(int shipSize, int startX, int startY, boolean isHorizontal, boolean direction) {
        int counter = 0;
        if (isHorizontal) {
            if (direction) {
                for (int i = 0; i < shipSize; i++) {
                    if (board[startY][startX - i] == null) {
                        counter++;
                    }
                }
            } else if (!direction) {
                for (int i = 0; i < shipSize; i++) {
                    if (board[startY][startX + i] == null) {
                        counter++;
                    }
                }
            }
        } else if (!isHorizontal) {
            if (direction) {
                for (int i = 0; i < shipSize; i++) {
                    if (board[startY + i][startX] == null) {
                        counter++;
                    }
                }
            } else if (!direction) {
                for (int i = 0; i < shipSize; i++) {
                    if (board[startY - i][startX] == null) {
                        counter++;
                    }
                }
            }
        }
        return counter;
    }


    /**
     * A method to update the map's position on the board
     *
     * @param point where the user has dragged the top-left corner of the map
     */
    public void update(Point point) {
        waterBox.set(point.x, point.y, point.x + 128 * 24, point.y + 128 * 16);
        masterPoint = point;
        waterImage.setBounds(waterBox);

        for (Ship ship : shipSet) {
            ship.update(point);
        }
    }

    /**
     * A method to draw the map and its contents onto the screen during a cycle of the game loop
     *
     * @param canvas the main canvas of the game
     */
    public void draw(Canvas canvas) {
        waterImage.draw(canvas);

        for (Ship ship : shipSet) {
            ship.draw(canvas);
        }
    }

    @Override
    public void update() {

    }

    public boolean contains(Point point) {
        return waterBox.contains(point.x, point.y);
    }

    /**
     * Gets the amount of ships in the shipSet
     *
     * @return int count is the amount of ships
     */
    public int shipCount() {
        int count = 0;
        if (shipSet.isEmpty()) {
            return 0;
        }
        Iterator<Ship> shipIterator = shipSet.iterator();
        while (shipIterator.hasNext()) {
            count++;
        }
        return count;
    }

    /**
     * Note: If you change the variables of an object, they do not get updated automatically.
     * HashSets are unordered, so it's easy enough to remove the object from the set and add it back
     * with its updated variables.
     * <p>
     * Use this method when you modify a ship that is currently on the board
     *
     * @param targetShip the Ship from the shipSet to be updated
     */
    public void updateShipInSet(Ship targetShip) {
        if (shipSet.contains(targetShip)) {
            shipSet.remove(targetShip);
            shipSet.add(targetShip);
        }
    }

    public void onTouchEvent(MotionEvent event) {
        final int SCROLL_TOLERANCE = 10;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                locator.set((int) event.getX(), (int) event.getY());
                break;
            case MotionEvent.ACTION_MOVE:
                //Only scroll if the user has moved their finger more than SCROLL_TOLERANCE pixels
                if (Math.abs(locator.x - event.getX()) > SCROLL_TOLERANCE
                        || Math.abs(locator.y - event.getY()) > SCROLL_TOLERANCE
                        || isScrolling) {
                    isScrolling = true;
                    masterPoint.set(masterPoint.x + (int) event.getX() - locator.x, masterPoint.y + (int) event.getY() - locator.y);
                    final int SCREEN_WIDTH = Resources.getSystem().getDisplayMetrics().widthPixels;
                    final int SCREEN_HEIGHT = Resources.getSystem().getDisplayMetrics().heightPixels;
                    final int MIN_GRID_SPACES = 5;


                    //Will not let the user scroll anymore if there are only MIN_GRID_SPACES left on the screen.
                    if (masterPoint.x > SCREEN_WIDTH - 128 * MIN_GRID_SPACES)
                        masterPoint.x = SCREEN_WIDTH - 128 * MIN_GRID_SPACES;
                    else if (masterPoint.x < -128 * (24 - MIN_GRID_SPACES))
                        masterPoint.x = -128 * (24 - MIN_GRID_SPACES);
                    if (masterPoint.y > SCREEN_HEIGHT - 128 * MIN_GRID_SPACES)
                        masterPoint.y = SCREEN_HEIGHT - 128 * MIN_GRID_SPACES;
                    else if (masterPoint.y < -128 * (16 - MIN_GRID_SPACES))
                        masterPoint.y = -128 * (16 - MIN_GRID_SPACES);

                    //update the locator to the current position of the finger
                    locator.set((int) event.getX(), (int) event.getY());
                    break;
                }
            case MotionEvent.ACTION_UP:
                if (!isScrolling && ready) //User only clicked a ship, didn't swipe screen over one
                {
                    Ship selected = board[((int) event.getY() - masterPoint.y) / 128][((int) event.getX() - masterPoint.x) / 128];

                    for (Object next : gp.panels) {
                        if (next instanceof ShipPanel)
                            gp.panels.remove(next);
                    }

                    if (selected != null) {
                        System.out.println("Selected a " + selected.getName());

                        ShipPanel sp = new ShipPanel(context, selected, gp);
                        gp.panels.add(sp);
                    }
                }
                isScrolling = false;
        }
    }

    /**
     * Use this method when a ship is Hit or Missed
     *
     * @param AttackedShip the ship from shipSet that is being attacked
     * @param Hits         the amount of damage the ship from the shipSet is about to take
     */
    public boolean HitShips(Ship AttackedShip, int Hits) {
        AttackedShip.setHitpoints(AttackedShip.getHitpoints() - Hits);
        if (AttackedShip.getHitpoints() <= 0) {
            shipSet.remove(AttackedShip);
            return true;
        } else {
            return false;
        }
    }
    /**
     * Use this method to calculate the number of ships lost in order to determine if the player
     * lost
     *
     * @param PlayerShips the number of ships from shipSet that you lost
     */
    public boolean hasLost(HashSet<Ship> PlayerShips) {
        return PlayerShips.isEmpty();
    }
    /**
     * Use this method to calculate the number of the opponent's ships the player destroyed in
     * order to determine if the player win
     *
     * @param OpponentsShips the number of ships from shipSet that you destroyed
     */
    public boolean hasWon(HashSet<Ship> OpponentsShips) {
        return OpponentsShips.isEmpty();
    }
    /**
     * Use this method to calculate the number ships that the player still has in order
     * to determine if the player still has any ships left
     */
    public boolean hasShips(HashSet<Ship> PlayerShips) {
        if (PlayerShips.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
    /**
     * Use this method to end the game and display the win screen if one of the players is
     * out of ships
     */

    public void endGame(Player player, Player player2) {
        if(hasShips(player.getPlayerSet())==false || hasShips(player2.getPlayerSet())==false) {
            if (hasShips(player.getPlayerSet()) == false) {
                setWinText(player2);
            }
            if (hasShips(player2.getPlayerSet()) == false) {
                setWinText(player);
            }
        }
    }
    /**
     * Use this method to display the text for who won the game
     * */
    public void setWinText(Player player){
        if(player == p1){
            winningText.setText("Player 1 Wins!");
        }else{
            winningText.setText("Player 2 Wins!");
        }
    }
    public TextView getWinText(){
        return winningText;
    }
    public Layout displayWinScreen(TextView winningText){

    }
}
