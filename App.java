package uk.ac.ed.inf;

import com.mapbox.geojson.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpClient;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;


/**
 * Main project application
 */
public class App {

    public static final HttpClient client = HttpClient.newHttpClient();
    public static final LongLat APPLETONTOWER = new LongLat(-3.186874, 55.944494);
    static final int STANDARD_DELIVERY_CHARGE = 50;

    // Boundary positions providing the box within the drone must operate - The confinement zone.
    static final double BOUNDARY_LAT1 = 55.942617; // This value must represent the smallest Latitude
    static final double BOUNDARY_LAT2 = 55.946233;
    static final double BOUNDARY_LONG1 = -3.184319; // this value must represent the greatest longitude
    static final double BOUNDARY_LONG2 = -3.192473;

    // Distance Tolerance is the precision in distance allowed to be considered close
    static final double D_TOLERANCE = 0.00015;

    //Distance the drone can move in a straight line
    static final double DISTANCE = 0.00015;

    //Value used to determine a stationary drone
    static final int FLOATING = -999;
    //distance the drone travels before retuning home
    static final double DISTANCEBEFORERETURNHOME = 0.200;

    //maxmimum number of landmarks used for the calculations
    static final int TOTALLANDMARKS = 7;

    /**
     * Produces list of deliveries and drone flight path populating their respective database tables
     * and produces geojson formatted txt file from this flightpath.
     *
     * @param  args "date" in terms of mm dd yyyy followed by website port number and database port number
     *              (i.e mm dd yyyy wbpt dbpt)
     */
    public static void main(String[] args) {
        Date date = Date.valueOf(args[2] + "-" + args[1]+ "-" +args[0]);
        String webPort = args[3];
        String dbPort = args[4];


        long startTime = System.nanoTime();
        Website website = new Website("localhost", webPort);
        Database db = new Database("localhost", dbPort);
        db.createDeliveriesTable();
        db.createFlightPathTable();

        Orders orders = new Orders(APPLETONTOWER, db, website);
        orders.setMoves(date);
        db.fillDeliveriesTable(orders.getMoves());

        ArrayList<ArrayList<Delivery>> flightpath = new ArrayList<>();
        Delivery firstDayDeliveries = orders.getMoves().get(0);
        Line line = new Line(firstDayDeliveries.getLocations().get(0), firstDayDeliveries.getLocations().get(1));
        for (Delivery i : orders.getMoves()) {
            flightpath.add(line.splitMove(i, website));
        }

        List<Point> pl = new ArrayList<>();
        ArrayList<Delivery> flatened = new ArrayList<>();
        double lat0 = APPLETONTOWER.latitude;
        double lng0 = APPLETONTOWER.longitude;
        pl.add(Point.fromLngLat(lng0, lat0));
        for (ArrayList<Delivery> i : flightpath) {
            for (Delivery j : i) {
                double lat = j.getLocations().get(j.getLocations().size() - 1).latitude;
                double lng = j.getLocations().get(j.getLocations().size() - 1).longitude;
                pl.add(Point.fromLngLat(lng, lat));
                flatened.add(j);
            }
        }
        LineString x = LineString.fromLngLats(pl);
        Feature f = Feature.fromGeometry(x);
        FeatureCollection fc = FeatureCollection.fromFeature(f);
        String finalgeo = fc.toJson();

        db.fillFlightPathTable(flatened);
        try {
            File myObj = new File("drone-"+args[0]+"-"+args[1]+"-"+args[2]+".geojson");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        try {
            FileWriter myWriter = new FileWriter("drone-"+args[0]+"-"+args[1]+"-"+args[2]+".geojson");
            myWriter.write(finalgeo);
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        long endTime   = System.nanoTime();
        long totalTime = (endTime - startTime)/1000;
        System.out.println("Total time taken: "+totalTime);
    }
}


