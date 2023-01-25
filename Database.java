package uk.ac.ed.inf;

import java.sql.*;
import java.util.ArrayList;

/**
 * Database class represents and grants access to our database, providing methods
 * that access, create, fill, and make usable items from the database
 */

public class Database {
    final private String name;
    final private String port;
    private String loc;


    /**
     * database class constructor
     *
     * @param name :derby database machine name
     * @param port :derby databse port number
     */
    public Database(String name, String port) {
        this.name = name;
        this.port = port;
    }

    /**
     * Create deliveries table within database
     */
    public void createDeliveriesTable() {
        String jdbcString = "jdbc:derby://" + this.name + ":" + this.port + "/derbyDB";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            ResultSet resultSet = databaseMetadata.getTables(null, null, "DELIVERIES", null);
            if (resultSet.next()) {
                statement.execute("drop table deliveries");
            }
            statement.execute("create table deliveries(" + "orderNo char(8)," + "deliveredTo varchar(19)," + "costInPence int)");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     *Create flightpath table within database
     */
    public void createFlightPathTable() {
        String jdbcString = "jdbc:derby://" + this.name + ":" + this.port + "/derbyDB";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            Statement statement = conn.createStatement();
            DatabaseMetaData databaseMetadata = conn.getMetaData();
            ResultSet resultSet = databaseMetadata.getTables(null, null, "FLIGHTPATH", null);
            if (resultSet.next()) {
                statement.execute("drop table flightpath");
            }
            statement.execute("create table flightpath(" + "orderNo char(8)," + "fromLongitude double," + "fromLatitude double," + "angle integer," + "toLongitude double," + "toLatitude double)");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    /**
     * Given an array list of deliveries will fill the flight path table
     * @param deliveries : Filled deliveries data with orderNo angle and two LongLats
     */
    public void fillFlightPathTable(ArrayList<Delivery> deliveries) {
        String jdbcString = "jdbc:derby://" + this.name + ":" + this.port + "/derbyDB";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            PreparedStatement psFlight = conn.prepareStatement("insert into flightpath values (?, ?, ?, ?, ?, ?)");
            for (Delivery s : deliveries) {
                ArrayList<LongLat> ll = s.getLocations();
                psFlight.setString(1, s.getOrderNo());
                psFlight.setDouble(2, ll.get(0).longitude);
                psFlight.setDouble(3, ll.get(0).latitude);
                psFlight.setInt(4, s.getAngle());
                psFlight.setDouble(5, ll.get(1).longitude);
                psFlight.setDouble(6, ll.get(1).latitude);
                psFlight.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Given an array list of deliveries will fill deliveries table in database
     * @param deliveries :deliveries containing orderNo, location, and cost.
     */
    public void fillDeliveriesTable(ArrayList<Delivery> deliveries) {
        String jdbcString = "jdbc:derby://" + this.name + ":" + this.port + "/derbyDB";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            PreparedStatement psDeliveries = conn.prepareStatement("insert into deliveries values (?, ?, ?)");
            for (Delivery s : deliveries) {
                psDeliveries.setString(1, s.getOrderNo());
                psDeliveries.setString(2, s.getW3w());
                psDeliveries.setInt(3, s.getCost());
                psDeliveries.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Get all order numbers for a given date from the database
     * @param date  date in sql date format
     * @return Order Numbers as an Arraylist of strings for a given day
     */
    public ArrayList<String> getOrderNos(Date date) {
        ArrayList<String> orderNoList = new ArrayList<>();
        String jdbcString = "jdbc:derby://" + this.name + ":" + this.port + "/derbyDB";
        final String ordersQuery = "select orderNo from orders where deliveryDate=(?)";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            PreparedStatement psOrdersQuery = conn.prepareStatement(ordersQuery);
            psOrdersQuery.setDate(1, date);
            ResultSet rs = psOrdersQuery.executeQuery();
            while (rs.next()) {
                String orderNo = rs.getString("orderNo");
                orderNoList.add(orderNo);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //add null error
        return orderNoList;
    }

    /**
     * get items associated with each order number
     * @param orderNo hexadecimal unique order number
     * @return an Array list of items names associated with order number
     */
    public ArrayList<String> getOrders(String orderNo) {
        ArrayList<String> orders = new ArrayList<>();
        String jdbcString = "jdbc:derby://" + this.name + ":" + this.port + "/derbyDB";
        final String ordersQuery = "select item from orderDetails where orderNo=(?)";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            PreparedStatement psOrdersQuery = conn.prepareStatement(ordersQuery);
            psOrdersQuery.setString(1, orderNo);
            ResultSet rs = psOrdersQuery.executeQuery();
            while (rs.next()) {
                String item = rs.getString("item");
                orders.add(item);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //add null error
        return orders;
    }

    /**
     * Gets delivery location (drop off point) of a given order number
     * @param orderNo hexadecimal unqiue order number
     * @return What3Words location of the delivery location
     */
    public String getFinalDestination(String orderNo) {
        String jdbcString = "jdbc:derby://" + this.name + ":" + this.port + "/derbyDB";
        final String ordersQuery = "select deliverTo from orders where orderNo=(?)";
        try {
            Connection conn = DriverManager.getConnection(jdbcString);
            PreparedStatement psOrdersQuery = conn.prepareStatement(ordersQuery);
            psOrdersQuery.setString(1, orderNo);
            ResultSet rs = psOrdersQuery.executeQuery();
            while (rs.next()) {
                loc = rs.getString("deliverTo");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        //add null error
        return loc;
    }

    /**
     * Given an order Number will get the locations of all the items within it in a Long Lat object
     * @param website  website class with name and port
     * @param orderNo hexadecimal unqiue order number
     * @return Array list of locations (shops)
     */
    public ArrayList<LongLat> getAllFlightLocations(Website website, String orderNo) {
        ArrayList<LongLat> locations = new ArrayList<>();
        ArrayList<String> ordersItems = getOrders(orderNo);
        if (ordersItems != null) {
            for (String ordI : ordersItems) {
                LongLat loc = website.whatThreetoLongLat(website.getDeliveryLocation(ordI));
                if (!locations.contains(loc)) {
                    locations.add(loc);
                }
            }
        }
        locations.add(website.whatThreetoLongLat(getFinalDestination(orderNo)));
        return locations;
    }
}
