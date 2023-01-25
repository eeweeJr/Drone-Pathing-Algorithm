package uk.ac.ed.inf;

import java.util.ArrayList;


/**
 * Shop class converts the raw webserver json data on the shops into a usable object.
 */
public class Shop {
    String name;
    String location;
    ArrayList<Item> menu;

    /**
     * This is a constructor for the shop class
     *
     * @param name     is the name of the shop
     * @param location is the location of this shop in terms of "what3words"
     * @param menu     is the menu from the shop
     */

    public Shop(String name, String location, ArrayList<Item> menu) {
        this.name = name;
        this.location = location;
        this.menu = menu;
    }

    public String getLocation() {
        return location;
    }

    public ArrayList<Item> getMenu() {
        return menu;
    }

    /**
     * This static nested class converts the items within each shop menu into a usable object.
     */

    static class Item {
        String item;
        int pence;

        /**
         * constructor for the Item class
         *
         * @param item  is the item name
         * @param pence is the price of the item in pence
         */
        public Item(String item, int pence) {
            this.item = item;
            this.pence = pence;
        }

        public String getItem() {
            return item;
        }

        public int getPence() {
            return pence;
        }

    }
}
