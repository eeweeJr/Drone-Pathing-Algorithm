//package uk.ac.ed.inf;
//
//import org.junit.Test;
//
//import java.sql.Date;
//import java.util.ArrayList;
//
//import static org.junit.Assert.*;
//
//public class AppTest {
//
//    private static final String VERSION = "1.0.5";
//    private static final String RELEASE_DATE = "September 28, 2021";
//
//    private final LongLat appletonTower = new LongLat(-3.186874, 55.944494);
//    private final LongLat businessSchool = new LongLat(-3.1873,55.9430);
//    private final LongLat greyfriarsKirkyard = new LongLat(-3.1928,55.9469);
//
//    @Test
//    public void testIsConfinedTrueA(){
//        assertTrue(appletonTower.isConfined());
//    }
//
//    @Test
//    public void testIsConfinedTrueB(){
//        assertTrue(businessSchool.isConfined());
//    }
//
//    @Test
//    public void testIsConfinedFalse(){
//        assertFalse(greyfriarsKirkyard.isConfined());
//    }
//
//    private boolean approxEq(double d1, double d2) {
//        return Math.abs(d1 - d2) < 1e-12;
//    }
//
//    @Test
//    public void testDistanceTo(){
//        double calculatedDistance = 0.0015535481968716011;
//        assertTrue(approxEq(appletonTower.distanceTo(businessSchool), calculatedDistance));
//    }
//
//    @Test
//    public void testCloseToTrue(){
//        LongLat alsoAppletonTower = new LongLat(-3.186767933982822, 55.94460006601717);
//        assertTrue(appletonTower.closeTo(alsoAppletonTower));
//    }
//
//
//    @Test
//    public void testCloseToFalse(){
//        assertFalse(appletonTower.closeTo(businessSchool));
//    }
//
//
//    private boolean approxEq(LongLat l1, LongLat l2) {
//        return approxEq(l1.longitude, l2.longitude) &&
//                approxEq(l1.latitude, l2.latitude);
//    }
//
//    @Test
//    public void testAngle0(){
//        LongLat nextPosition = appletonTower.nextPosition(0);
//        LongLat calculatedPosition = new LongLat(-3.186724, 55.944494);
//        assertTrue(approxEq(nextPosition, calculatedPosition));
//    }
//
//    @Test
//    public void testAngle20(){
//        LongLat nextPosition = appletonTower.nextPosition(20);
//        LongLat calculatedPosition = new LongLat(-3.186733046106882, 55.9445453030215);
//        assertTrue(approxEq(nextPosition, calculatedPosition));
//    }
//
//    @Test
//    public void testAngle50(){
//        LongLat nextPosition = appletonTower.nextPosition(50);
//        LongLat calculatedPosition = new LongLat(-3.186777581858547, 55.94460890666647);
//        assertTrue(approxEq(nextPosition, calculatedPosition));
//    }
//
//    @Test
//    public void testAngle90(){
//        LongLat nextPosition = appletonTower.nextPosition(90);
//        LongLat calculatedPosition = new LongLat(-3.186874, 55.944644);
//        assertTrue(approxEq(nextPosition, calculatedPosition));
//    }
//
//    @Test
//    public void testAngle140(){
//        LongLat nextPosition = appletonTower.nextPosition(140);
//        LongLat calculatedPosition = new LongLat(-3.1869889066664676, 55.94459041814145);
//        assertTrue(approxEq(nextPosition, calculatedPosition));
//    }
//
//    @Test
//    public void testAngle190(){
//        LongLat nextPosition = appletonTower.nextPosition(190);
//        LongLat calculatedPosition = new LongLat(-3.1870217211629517, 55.94446795277335);
//        assertTrue(approxEq(nextPosition, calculatedPosition));
//    }
//
//    @Test
//    public void testAngle260(){
//        LongLat nextPosition = appletonTower.nextPosition(260);
//        LongLat calculatedPosition = new LongLat(-3.18690004722665, 55.944346278837045);
//        assertTrue(approxEq(nextPosition, calculatedPosition));
//    }
//
//    @Test
//    public void testAngle300(){
//        LongLat nextPosition = appletonTower.nextPosition(300);
//        LongLat calculatedPosition = new LongLat(-3.186799, 55.94436409618943);
//        assertTrue(approxEq(nextPosition, calculatedPosition));
//    }
//
//    @Test
//    public void testAngle350(){
//        LongLat nextPosition = appletonTower.nextPosition(350);
//        LongLat calculatedPosition = new LongLat(-3.1867262788370483, 55.94446795277335);
//        assertTrue(approxEq(nextPosition, calculatedPosition));
//    }
//
//    @Test
//    public void testAngle999(){
//        // The special junk value -999 means "hover and do not change position"
//        LongLat nextPosition = appletonTower.nextPosition(-999);
//        assertTrue(approxEq(nextPosition, appletonTower));
//    }
//    @Test
//    public void testDatabaseOrdernoOne() {
//        // The webserver must be running on port 9898 to run this test.
//        Database db = new Database("localhost", "1527");
//        ArrayList<String> lis = db.getOrderNos(Date.valueOf("2023-12-31"));
//        // Don't forget the standard delivery charge of 50p
//        assertTrue(lis.contains("1ad5f1ff") && lis.contains("fa1bbc89"));
//    }
//    @Test
//    public void testDatabaseOrdernoTwo() {
//        // The webserver must be running on port 9898 to run this test.
//        Database db = new Database("localhost", "1527");
//        ArrayList<String> lis = db.getOrderNos(Date.valueOf("2023-12-31"));
//        // Don't forget the standard delivery charge of 50p
//        //4583dffa
//        ArrayList<String> list = db.getOrders(lis.get(0));
//        assertEquals(list.get(1), "Jasmine green cap tea");
//    }
//
//    @Test
//    public void testMenusOne() {
//        // The webserver must be running on port 9898 to run this test.
//        Website menus = new Website("localhost", "9898");
//        int totalCost = menus.getDeliveryCost(
//                "Ham and mozzarella Italian roll"
//        );
//        // Don't forget the standard delivery charge of 50p
//        assertEquals(230 + 50, totalCost);
//    }
//    @Test
//    public void whatthreetolonglat() {
//        Website website = new Website("localhost","9898");
//        String shopLocations =("pest.round.peanut");
//        LongLat hi = new LongLat(-3.186103,55.944656);
//        LongLat  hello = website.whatThreetoLongLat(shopLocations);
//        System.out.println(hello.latitude);
//        System.out.println(hello.longitude);
//        System.out.println(hi.latitude);
//        System.out.println(hi.longitude);
//        assertEquals(hi,hello);
//    }
//
//    @Test
//    public void getdeliverylocation() {
//        Website website = new Website("localhost","9898");
//        String shopLocations =("pest.round.peanut");
//        assertEquals(shopLocations,website.getDeliveryLocation("Can of Fanta"));
//    }
//    @Test
//    public void canmakemove1() {
//        Website website = new Website("localhost","9898");
//        LongLat start = new LongLat(-3.186103,55.944656);
//        LongLat finish = new LongLat(-3.186874,55.944494);
//        Line line = new Line(start,finish);
//
//        assertTrue(line.canMakeMove(website));
//    }
//    @Test
//    public void canmakemove2() {
//        Website website = new Website("localhost","9898");
//        LongLat start = new LongLat(-3.184319,55.942617);
//        LongLat finish = new LongLat(-3.192473,55.946233);
//        Line line = new Line(start,finish);
//        assertFalse(line.canMakeMove(website));
//    }
//    @Test
//    public void getorderdeliverylocationfromdatabase() {
//        Database db = new Database("localhost","1527");
//        String shopLocations =("spell.stick.scale");
//        String orderNo = "1ad5f1ff";
//        LongLat hi = new LongLat(-3.186103,55.944656);
//        String result = db.getFinalDestination(orderNo);
//        assertEquals(shopLocations,result);
//    }
//
//    @Test
//    public void getallflightlocations() {
//        Website website = new Website("localhost","9898");
//        Database db = new Database("localhost","1527");
//        String orderNo = "1ad5f1ff";
//        ArrayList<LongLat> places = db.getAllFlightLocations(website,orderNo);
//        LongLat hi = new LongLat(-3.186103,55.944656);
//        //System.out.println(places);
//        assertTrue(places.contains(hi));
//    }
//    //@Test
//    //public void getMoves() {
//    //    Website website = new Website("localhost","9898");
//    //    Database db = new Database("localhost","1527");
//
//   //     Orders orders = new Orders(appletonTower,db,website);
//    //   System.out.println(orders.getMoves(Date.valueOf("2023-12-31")));
//
//    //}
//   // @Test
//   // public void splitDelivery() {
//   //    Website website = new Website("localhost","9898");
//   //     Database db = new Database("localhost","1527");
//    //    Orders orders = new Orders(appletonTower,db,website);
//    //    Delivery a =  orders.getMoves(Date.valueOf("2023-11-30")).get(0);
//    //    Line line = new Line(a.locations.get(0),a.locations.get(1));
//    //    for (Delivery fu:orders.getMoves(Date.valueOf("2023-11-30"))) {
//    //        System.out.println(line.splitMove(fu,website));
//    //    }
//    //}
//
//
//    @Test
//    public void testMenusTwo() {
//        // The webserver must be running on port 9898 to run this test.
//        Website menus = new Website("localhost", "9898");
//        int totalCost = menus.getDeliveryCost(
//                "Ham and mozzarella Italian roll",
//                "Salami and Swiss Italian roll"
//        );
//        // Don't forget the standard delivery charge of 50p
//        assertEquals(230 + 230 + 50, totalCost);
//    }
//
//    @Test
//    public void testMenusThree() {
//        // The webserver must be running on port 9898 to run this test.
//        Website menus = new Website("localhost", "9898");
//        int totalCost = menus.getDeliveryCost(
//                "Ham and mozzarella Italian roll",
//                "Salami and Swiss Italian roll",
//                "Flaming tiger latte"
//        );
//        // Don't forget the standard delivery charge of 50p
//        assertEquals(230 + 230 + 460 + 50, totalCost);
//    }
//
//    @Test
//    public void testMenusFourA() {
//        // The webserver must be running on port 9898 to run this test.
//        Website menus = new Website("localhost", "9898");
//        int totalCost = menus.getDeliveryCost(
//                "Ham and mozzarella Italian roll",
//                "Salami and Swiss Italian roll",
//                "Flaming tiger latte",
//                "Dirty matcha latte"
//        );
//        // Don't forget the standard delivery charge of 50p
//        assertEquals(230 + 230 + 460 + 460 + 50, totalCost);
//    }
//
//    @Test
//    public void testMenusFourB() {
//        // The webserver must be running on port 9898 to run this test.
//        Website menus = new Website("localhost", "9898");
//        int totalCost = menus.getDeliveryCost(
//                "Flaming tiger latte",
//                "Dirty matcha latte",
//                "Strawberry matcha latte",
//                "Fresh taro latte"
//        );
//        // Don't forget the standard delivery charge of 50p
//        assertEquals(4 * 460 + 50, totalCost);
//    }
//
//
//}