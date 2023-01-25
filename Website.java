package uk.ac.ed.inf;


import com.google.gson.Gson;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static uk.ac.ed.inf.App.*;


/**
 * Website class represents the website accessed by the drone system.
 * It contains methods used to obtain and process data from this website.
 */

public class Website {
    final private String name;
    final private String port;


    /**
     * Constructor for the Website object
     *
     * @param name is the name of the machine
     * @param port is the port of the machine
     */
    public Website(String name, String port) {
        this.name = name;
        this.port = port;

    }

    /**
     * This method returns the delivery cost of items chosen from the website.
     *
     * @param chosenItems is a String of variable length, each string being an item which has been chosen and will have a cost associated
     * @return total cost of the delivery including the standard delivery charge however will return 0 if nothing is selected
     */
    public int getDeliveryCost(String... chosenItems) {
        String urlString = "http://" + this.name + ":" + this.port + "/menus/menus.json";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Shop[] shops = new Gson().fromJson(response.body(), Shop[].class);
            int deliveryTotal = STANDARD_DELIVERY_CHARGE;
            for (String singleItem : chosenItems) {
                for (Shop singleShop : shops) {
                    ArrayList<Shop.Item> shopMenu = singleShop.getMenu();
                    for (Shop.Item item : shopMenu) {
                        String itemString = item.getItem();
                        if (itemString.equals(singleItem)) {
                            deliveryTotal = deliveryTotal + item.getPence();
                        }
                    }
                }
            }
            return deliveryTotal;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Given an item in a shop will return the what3words location of the shop from the website.
     * @param orderItem : string name of item in shop
     * @return What 3 words locaiton of shop
     */
    public String getDeliveryLocation(String orderItem) {
        String location = "";
        String urlString = "http://" + this.name + ":" + this.port + "/menus/menus.json";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            Shop[] shops = new Gson().fromJson(response.body(), Shop[].class);
            for (Shop singleShop : shops) {
                ArrayList<Shop.Item> shopMenu = singleShop.getMenu();
                for (Shop.Item item : shopMenu) {
                    String itemString = item.getItem();
                    if (itemString.equals(orderItem)) {
                        location = singleShop.getLocation();
                        break;
                    }
                }
            }
        } catch (IOException | InterruptedException ioException) {
            ioException.printStackTrace();
        }
        return location;
    }

    /**
     * Provides the list of points defining the polygon borders of the no-fly zone.
     * @return Array list of polygons defined as an array list of geojson points
     */
    public List<List<Point>> noFlyZone() {
        String urlString = "http://" + this.name + ":" + this.port + "/buildings/no-fly-zones.geojson";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        List<List<Point>> totalPoints = new ArrayList<>();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            FeatureCollection featCol = FeatureCollection.fromJson(response.body());
            List<Feature> list = featCol.features();
            assert list != null;
            for (Feature feat : list) {
                Polygon geo = (Polygon) feat.geometry();
                assert geo != null;
                List<List<Point>> allPoint = geo.coordinates();
                List<Point> allPointNeat = allPoint.stream().flatMap(List::stream).collect(Collectors.toList());
                totalPoints.add(allPointNeat);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return totalPoints;
    }

    /**
     * Returns all landmarks used for traversing the area, including the ones defined within the website and the extra
     * calculated by using the boundary of the confinement zone.
     * @return list of landmarks defined as longlat locations
     */
    public List<LongLat> getLandmarks() {
        String urlString = "http://" + this.name + ":" + this.port + "/buildings/landmarks.geojson";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        List<LongLat> totalPoints = new ArrayList<>();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            FeatureCollection featCol = FeatureCollection.fromJson(response.body());
            List<Feature> list = featCol.features();
            assert list != null;
            for (Feature feat : list) {
                Point geo = (Point) feat.geometry();
                assert geo != null;
                List<Double> allPoint = geo.coordinates();
                LongLat longLat = new LongLat(allPoint.get(0), allPoint.get(1));
                totalPoints.add(longLat);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        totalPoints.add(APPLETONTOWER);
        ArrayList<LongLat> boundaryLandmarks = new ArrayList<>();
        double longCent = (BOUNDARY_LONG1 + BOUNDARY_LONG2) / 2;
        double latCent = (BOUNDARY_LAT1 + BOUNDARY_LAT2) / 2;
        LongLat center = new LongLat(longCent, latCent);
        LongLat midTop = new LongLat(longCent, (BOUNDARY_LAT2-((BOUNDARY_LAT2-BOUNDARY_LAT1)/100)));
        LongLat midBottom = new LongLat(longCent, (BOUNDARY_LAT1-((BOUNDARY_LAT1-BOUNDARY_LAT2)/100)));
        LongLat midLeft = new LongLat((BOUNDARY_LONG1-((BOUNDARY_LONG1-BOUNDARY_LONG2)/100)),latCent);
        LongLat midRight = new LongLat((BOUNDARY_LONG2-((BOUNDARY_LONG2-BOUNDARY_LONG1)/100)),latCent);
        boundaryLandmarks.add(midTop);
        boundaryLandmarks.add(midBottom);
        boundaryLandmarks.add(midLeft);
        boundaryLandmarks.add(midRight);
        boundaryLandmarks.add(center);

        for (LongLat lngLat : boundaryLandmarks) {
            if (lngLat.isConfined() && totalPoints.size() <TOTALLANDMARKS){
                totalPoints.add(lngLat);
            }
        }
        return totalPoints;
    }

    /**
     * Given a what 3 words location will turn this into a usable lonlat location representation using the data from
     * the website.
     * @param what3words string what3words location found on website
     * @return Corresponding longitude and latitude location representation in the form of class longlat
     */
    public LongLat whatThreetoLongLat(String what3words) {
        //check if words is three long
        String[] splitWords = what3words.split("\\.");
        String urlString = "http://" + this.name + ":" + this.port + "/words/" + splitWords[0] + "/" + splitWords[1] + "/" + splitWords[2] + "/details.json";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            What3Words w3w = new Gson().fromJson(response.body(), What3Words.class);
            double lat = w3w.getCoordinates().getLat();
            double lng = w3w.getCoordinates().getLng();
            return new LongLat(lng, lat);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        System.err.println("what three words has no associated Longitude or Latitude");
        return new LongLat(0, 0);

    }
}
