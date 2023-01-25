package uk.ac.ed.inf;

import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Line class defines a line from one LongLat location to another, and provides operations for these lines.
 */
public class Line {
    private final LongLat start;
    private final LongLat finish;

    /**
     *Line class constructor
     * @param start : LongLat representing where the calculations are calculated 'from'
     * @param finish : LongLat representing where the calculations are calculated 'to', the end of the directional line
     */

    public Line(LongLat start, LongLat finish) {
        this.start = start;
        this.finish = finish;
    }

    /**
     * Finds if a line intersects with the no-fly zone
     * @param web website where the no-fly zone data is located
     * @return True if it does not intersect the no-fly zone and False if it does intersect the no-fly zone
     */
    public boolean canMakeMove(Website web) {
        List<List<Point>> noFlyZone = web.noFlyZone();
        double det;
        double gamma;
        double lambda;

        double a = this.start.longitude;
        double b = this.start.latitude;

        double c = this.finish.longitude;
        double d = this.finish.latitude;
        for (List<Point> points : noFlyZone) {
            for (int i = 0; i < points.size() - 1; i++) {
                List<Double> e = points.get(i).coordinates();
                double p = e.get(0);
                double q = e.get(1);
                List<Double> x = points.get(i + 1).coordinates();
                double r = x.get(0);
                double s = x.get(1);
                det = ((c - a) * (s - q) - (r - p) * (d - b));
                if (det != 0) {
                    lambda = ((s - q) * (r - a) + (p - r) * (s - b)) / det;
                    gamma = ((b - d) * (r - a) + (c - a) * (s - b)) / det;
                    if ((0 < lambda && lambda < 1) && (0 < gamma && gamma < 1)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    /**
     * Splits Delivery into smaller deliveries with the same order number where each longlat represents a line at an
     * angle with which the drone can move (divisible by 10 and less than 360), where each delivery class is the
     * size of one move. It also adds a hover move when stopping at a shop or delivery location.
     * @param delivery  delivery class where distance is greater than that of a move
     * @param web : website
     * @return Array list of deliveries containing one move and the correct angle.
     */
    public ArrayList<Delivery> splitMove(Delivery delivery, Website web) {
        ArrayList<Delivery> splitDelivery = new ArrayList<>();

        for (int i = 0; i < delivery.getLocations().size(); i++) {
            LongLat a;
            if (i == 0) {
                a = delivery.getStart();
            } else {
                a = delivery.getLocations().get(i - 1);
            }
            LongLat b = delivery.getLocations().get(i);
            while (!a.closeTo(b)) {
                ArrayList<LongLat> lls = new ArrayList<>();
                lls.add(a);

                int ang = a.findAngle(b, web);
                a = a.nextPosition(ang);
                if (!a.isConfined()) {
                    System.err.println("Final destination not in ConfinZone : re-routing required");
                } else {
                    lls.add(a);
                    Delivery del = new Delivery(delivery.getOrderNo(), lls, a);
                    del.setAngle(ang);
                    splitDelivery.add(del);
                }
            }

            ArrayList<LongLat> llsFinal = new ArrayList<>();
            llsFinal.add(a); //reached desitnation so move is added where location is the same
            llsFinal.add(a);
            String orderNo = delivery.getOrderNo();
            if (!web.getLandmarks().contains(b)) {
                Delivery deliv = new Delivery(orderNo, llsFinal, a);
                deliv.setAngle(-999);
                splitDelivery.add(deliv);
                }
            }
        return splitDelivery;
    }
}
