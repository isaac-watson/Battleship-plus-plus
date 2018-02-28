package sophomoreproject.battleship.ships;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import sophomoreproject.battleship.R;
/**
 * Created by isaac on 1/31/2018.
 */

public class AircraftCarrier extends Ship implements ShipInterface{
    public AircraftCarrier(Context context, int row, int column) {
        super(context, row, column);
        setName("Aircraft Carrier");
        setShipSize(5);
        setSC(4);
        setnMove(1);
        setHitpoints(4000);
        setdamage(350);
        setDC(1);
        setnShots(3);
        shipImage = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.aircraftcarrier), 128*getShipSize(), 128, false);
    }

    @Override
    public void ability() {

    }
}
