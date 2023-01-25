package uk.ac.ed.inf;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static uk.ac.ed.inf.App.APPLETONTOWER;
import static uk.ac.ed.inf.App.DISTANCEBEFORERETURNHOME;

/**
 * Orders class provides a methods used for determining Delivery Classes based off of order numbers
 *
 */
public class Orders {

    private LongLat droneLocation;
    private final Database database;
    private final Website website;
    private ArrayList<Delivery> moves;

    /**
     * Orders class constructor
     * @param droneLocation : Drones starting location as a LongLat
     * @param database : Database class with name and port
     * @param website :Website class with name and port
     */

    public Orders(LongLat droneLocation, Database database, Website website) {
        this.droneLocation = droneLocation;
        this.database = database;
        this.website = website;

    }

    public LongLat getDroneLocation() {
        return droneLocation;
    }

    public void setDroneLocation(LongLat droneLocation) {
        this.droneLocation = droneLocation;
    }

    public Database getDatabase() {
        return database;
    }

    public Website getWebsite() {
        return website;
    }

    public ArrayList<Delivery> getMoves() {return moves;}

    /**
     * performs a greedy best first search to decide the route for the day
     * @param date : for finding route on the given date
     */
    public void setMoves(Date date) {
        ArrayList<Delivery> deliveryDecisionFinal = new ArrayList<>();
        ArrayList<String> allOrderNumbers = this.getDatabase().getOrderNos(date);
        while (!allOrderNumbers.isEmpty()) {
            long startTime = System.nanoTime();
            ArrayList<Delivery> finalisedOptionalRoute = new ArrayList<>();
            for (String orderNo : allOrderNumbers) { //creates temporary choice which is a ist of all the routes for the day with longlats
                ArrayList<Delivery> optionalRoute = new ArrayList<>();
                ArrayList<LongLat> longLats = this.getDatabase().getAllFlightLocations(this.getWebsite(), orderNo);
                ArrayList<LongLat> finalStop = new ArrayList<>();

                finalStop.add(longLats.get(longLats.size() - 1));
                longLats.remove(longLats.size() - 1);
                LongLat droneLocation = this.getDroneLocation();
                while (!longLats.isEmpty()) { //while there are still longlats in the order number left to visit
                    Delivery optionalDelivery = getNextDistances(longLats, orderNo, droneLocation);
                    droneLocation = optionalDelivery.getLocations().get(optionalDelivery.getLocations().size() - 1);
                    optionalRoute.add(optionalDelivery);
                    for (LongLat lnglat : optionalDelivery.getLocations()) {
                        longLats.remove(lnglat);
                    }
                }
                droneLocation = (optionalRoute.get(optionalRoute.size() - 1).getLocations()).get((optionalRoute.get(optionalRoute.size() - 1)).getLocations().size() - 1);
                optionalRoute.add(getNextDistances(finalStop, orderNo, droneLocation));
                finalisedOptionalRoute.add(totalDistance(optionalRoute));


            }

            Delivery bestValueRoute = finalisedOptionalRoute.get(0);
            int totCostInitial = getTotalCostofOrder(this.getDatabase(), this.getWebsite(), bestValueRoute.getOrderNo());
            bestValueRoute.setCost(totCostInitial);
            for (Delivery choice : finalisedOptionalRoute) {
                double first = bestValueRoute.getDistance() / totCostInitial;

                int totCostCompare = getTotalCostofOrder(this.getDatabase(), this.getWebsite(), choice.getOrderNo());
                double compare = choice.getDistance() / totCostCompare;
                if (first > compare) { //compares best ratio of distance over price
                    bestValueRoute = choice;
                    bestValueRoute.setCost(totCostCompare);
                }
            }
            String w3w = this.getWebsite().getDeliveryLocation((bestValueRoute.getOrderNo()));
            bestValueRoute.setW3w(w3w);
            if (deliveryDecisionFinal.size() < 1) { //for first move
                bestValueRoute.setStart(this.getDroneLocation());
                deliveryDecisionFinal.add(bestValueRoute);
                allOrderNumbers.remove(bestValueRoute.getOrderNo());
                LongLat bestStart = bestValueRoute.getLocations().get(bestValueRoute.getLocations().size() - 1);
                this.setDroneLocation(bestStart);
                System.out.println(deliveryDecisionFinal);
                long endTime = System.nanoTime();
                long totalTime = (endTime - startTime)/1000;
                System.out.println("Add one route to delivery: "+totalTime);
            } else {
                bestValueRoute.setStart(this.getDroneLocation());
                deliveryDecisionFinal.add(bestValueRoute);
                allOrderNumbers.remove(bestValueRoute.getOrderNo());
                LongLat bestStart = bestValueRoute.getLocations().get(bestValueRoute.getLocations().size()-1);
                this.setDroneLocation(bestStart);
                long endTime = System.nanoTime();
                long totalTime = (endTime - startTime)/1000;
                System.out.println("Add one route to delivery: "+totalTime); // for checking runtime
            }
        }
        ArrayList<Delivery> finalisedDayRoute = new ArrayList<>();
        double distance = 0.;
        for (Delivery i : deliveryDecisionFinal) {
            if (distance < DISTANCEBEFORERETURNHOME) { //creates final list by removing values that make journey too long
                distance += i.getDistance();
                finalisedDayRoute.add(i);
            }
        }
        ArrayList<LongLat> home = new ArrayList<>();
        LongLat secondlast = finalisedDayRoute.get(finalisedDayRoute.size() - 1).getLocations().get(finalisedDayRoute.get(finalisedDayRoute.size() - 1).getLocations().size() - 1);
        home.add(APPLETONTOWER);
        Delivery finalmove = getNextDistances(home, "RETHOME", secondlast); //adds final move back to home
        System.out.println(finalisedDayRoute.size());
        finalisedDayRoute.add(finalmove);
        System.out.println(distance);
        this.moves = finalisedDayRoute;
    }

    /**
     * Given an order number and a starting location and the places we should move to, it will produce the route towards
     * this whilst utilising the landmarks to avoid no-fly zones. Returning the shortest possible route.
     *
     * @param destinations an array list of longlats to travel to
     * @param orderNo hexadecimal unique ordernumber we wish to find the distance for
     * @param start starting location from which we get the distance
     * @return new delivery route that is the shortest that does not cross confin zone or through no-fly zone
     */
    private Delivery getNextDistances(ArrayList<LongLat> destinations, String orderNo, LongLat start) {
        ArrayList<Delivery> deliver = new ArrayList<>();
        List<LongLat> allLandmarks = this.getWebsite().getLandmarks();
        for (LongLat delivery : destinations) {
            ArrayList<LongLat> firstMove = new ArrayList<>();
            firstMove.add(delivery);
            Line line = new Line(start, delivery);
            if (line.canMakeMove(this.getWebsite()) && delivery.isConfined()) {
                Delivery oneLoc = new Delivery(orderNo, firstMove, start);
                oneLoc.setDistance(start.distanceTo(delivery));
                deliver.add(oneLoc);

            } else {
                for (LongLat landMark : allLandmarks) {
                    ArrayList<LongLat> moveViaLandmark = new ArrayList<>();
                    moveViaLandmark.add(landMark);
                    moveViaLandmark.add(delivery);
                    Line line1 = new Line(start, landMark);
                    Line line2 = new Line(landMark, delivery);
                    if ((line1.canMakeMove(this.getWebsite())) && (line2.canMakeMove(this.getWebsite()) &&  delivery.isConfined())) {
                        Delivery twoLocs = new Delivery(orderNo, moveViaLandmark, start);
                        twoLocs.setDistance(landMark.distanceTo(delivery) + start.distanceTo(landMark));
                        deliver.add(twoLocs);
                    }
                }
            }
        }
        if (deliver.isEmpty()) {
            System.err.println(start.longitude);
            System.err.println(start.latitude);
        }
        Delivery smallest = deliver.get(0);
        for (int i = 1; i < deliver.size(); i++) {
            Double b = smallest.getDistance();
            Double a = deliver.get(i).getDistance();
            if (a < b) {  // return shortest possible route
                smallest = deliver.get(i);
            }
        }
        return smallest;
    }


    /**
     * Calculate the total distance of all the deliveries
     * @param pathUntotaled list of deliveries with individual distances
     * @return totalled distance moved
     */
    private Delivery totalDistance(ArrayList<Delivery> pathUntotaled) {
        Double totalDistance = 0.;
        ArrayList<LongLat> allLongLats = new ArrayList<>();
        for (Delivery move : pathUntotaled) {
            totalDistance += move.getDistance();
            allLongLats.addAll(move.getLocations());
        }
        LongLat start = pathUntotaled.get(0).getLocations().get(0);
        Delivery delivery = new Delivery(pathUntotaled.get(0).getOrderNo(), allLongLats, start);
        delivery.setDistance(totalDistance);
        return delivery;
    }

    /**
     * Calculates the total cost of items for a given ordernumber including delivery cost
     * @param database : database class for accessing database values
     * @param website :website class for accessing website values
     * @param orderNo hexidecimal value unique representation order number
     * @return integer value representing cost on pence of order total
     */
    private int getTotalCostofOrder(Database database, Website website, String orderNo) {
        ArrayList<String> ordersItems = database.getOrders(orderNo);
        int totalCost = 0;
        if (ordersItems != null) {
            totalCost = website.getDeliveryCost();
            return totalCost;
        }
        return totalCost;
    }
}
