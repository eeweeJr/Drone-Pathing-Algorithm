package uk.ac.ed.inf;


import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import  java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import static uk.ac.ed.inf.App.client;

/**
 * Menus class represents the menus used by the drone system. It contains methods used to operate on the Menus.
 */

public class Menus {
    static final int STANDARD_DELIVERY_CHARGE = 50;
    final public String name;
    final public String port;


    /**
     * Constructor for the Menus object
     *
     * @param name is the name of the machine
     * @param port is the port of the machine
     */
    public Menus(String name, String port) {
        this.name = name;
        this.port = port;

    }
    /**
     * This method returns the delivery cost of items chosen.
     *
     * @param chosenItems is a String of variable length, each string being an item which has been chosen and will have a cost associated
     * @return total cost of the delivery including the standard delivery charge however will return 0 if nothing is selected
     */
    public int getDeliveryCost(String... chosenItems) {
        String urlString = "http://" + this.name +":" + this.port + "/menus/menus.json" ;
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();
        {
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
        }
        return 0;
    }
}
