package sophomoreproject.battleship.ships;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import sophomoreproject.battleship.R;
/**
 * Created by isaac on 1/31/2018.
 */

public class Destroyer extends Ship implements ShipInterface{
    public Destroyer(Context context, int row, int column) {
        super(context, row, column);
        setName("destroyer");
        setShipSize(4);
        setShipCost(3);
        setnMove(2);
        setHitpoints(2500);
        setdamage(125);
        setnShots(4);
        setDamageCost(1);
        setfdamage(600);
        setFDamageCost(4);
        setbdamage(400);
        setBDamageCost(3);
        setRange(8);
        setFRange(4);
        setpmove(0);
        shipImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.destroyer), 128*getShipSize(), 128, false);
    }

    @Override
    public void ability() {

    }

}
