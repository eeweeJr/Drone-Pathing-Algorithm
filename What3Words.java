package uk.ac.ed.inf;

/**
 * Class defining the what3words location, parsing the json data in terms of its lnglat coordinates
 */
public class What3Words {

    private final LngLat coordinates;

    /**
     * Constructor for what3 words class
     * @param coordinates : lng and lat coordinate value of what3words location
     */

    public What3Words(LngLat coordinates) {
        this.coordinates = coordinates;
    }

    public LngLat getCoordinates() {
        return coordinates;
    }

    /**
     *
     * Nested class for the formatting of what 3 words coordinates
     */
    static class LngLat {
        private final double lng;
        private final double lat;

        /**
         * Class constructor for LngLat
         * @param lng : longitude of what3words location
         * @param lat :latitude of what3words location
         */
        public LngLat(double lng, double lat) {
            this.lng = lng;
            this.lat = lat;
        }

        public double getLng() {
            return lng;
        }

        public double getLat() {
            return lat;
        }
    }
}
