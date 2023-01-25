package uk.ac.ed.inf;

import java.util.ArrayList;

/**
 * Class used to provide the information a drone needs to complete an order
 */
public class Delivery {
    private final String orderNo;
    private Double distance;
    private final ArrayList<LongLat> locations;
    private LongLat start;
    private int angle;
    private int cost;
    public String w3w;

    /**
     *Delivery class constructor
     * @param orderNo :hexidecimal unqiue order number for an order
     * @param locations :Long Lat locations that represent where the drone must move
     * @param start :The starting longLat location
     */
    public Delivery(String orderNo, ArrayList<LongLat> locations, LongLat start) {
        this.orderNo = orderNo;
        this.locations = locations;
        this.start = start;
    }

    public int getAngle() {
        return angle;
    }

    public void setAngle(int angle) {
        this.angle = angle;
    }

    public ArrayList<LongLat> getLocations() {
        return locations;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public String getW3w() {return w3w;}

    public void setW3w(String w3w) {
        this.w3w = w3w;
    }

    public LongLat getStart() {
        return start;
    }

    public void setStart(LongLat start) {
        this.start = start;
    }


}
