package uk.ac.ed.inf;

import java.util.Objects;
import static uk.ac.ed.inf.App.*;
/**
 * LongLat class is used to represent a point using longitude and latitude.
 * It uses methods to move the point and determine this points position relative to other LongLat points.
 */


public class LongLat {


    public double longitude;
    public double latitude;

    /**
     * Constructor for a LongLat object
     *
     * @param longitude the longitude value of a point
     * @param latitude  the latitude value of a point
     */
    public LongLat(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Determines if this LongLat is within the confinement area.
     *
     * @return boolean True if this LongLat is within confinement area, False if otherwise
     */
    public boolean isConfined() {
        return (this.latitude >= BOUNDARY_LAT1 && this.latitude <= BOUNDARY_LAT2
                && this.longitude <= BOUNDARY_LONG1 && this.longitude >= BOUNDARY_LONG2);
    }

    /**
     * Determines the pythagorean distance between this LongLat and a second LongLat object.
     *
     * @param secondLongLat is the second LongLat upon which we will check the distance towards
     * @return distance as a double between two longLat objects
     */

    public double distanceTo(LongLat secondLongLat) {
        if (secondLongLat.isConfined()) {
            return Math.sqrt((this.latitude - secondLongLat.latitude) * (this.latitude - secondLongLat.latitude)
                    + (this.longitude - secondLongLat.longitude) * (this.longitude - secondLongLat.longitude));
        } else {
            throw new IllegalArgumentException("Second LongLat location (" + secondLongLat + ") is outside of the confinement area");
        }
    }


    /**
     * Determines if this LongLat object is within the set distance of a second LongLat object.
     *
     * @param secondLongLat is the second LongLat upon which we will check the distance towards
     * @return True if LongLat is within the distance determined to be close, False if otherwise
     */
    public boolean closeTo(LongLat secondLongLat) {
        return (distanceTo(secondLongLat) < D_TOLERANCE);
    }

    /**
     * Attempts to move this LongLat position to a new LongLat position in the direction of the angle given
     * however will also determine if there is no move to be made, specifically the floating constant is used for the angle,
     * and this LongLat will remain the same. This method will also make sure that the chosen angle of the move is within
     * the restrictions, specifically less than 360 and divisible by 10.
     *
     * @param angle is the angle in degrees at which the new LongLat position will be moved towards (East being 0 degrees, 90 degrees moving due north...)
     * @return new LongLat value moved the pre-determined move distance away at an angle defined within the parameter
     */
    public LongLat nextPosition(int angle) {
        if (angle != FLOATING) {
            if (angle >= 0 && angle <= 350 && angle % 10 == 0) {
                double rad = Math.toRadians(angle);
                double newLongitude = this.longitude + Math.cos(rad) * DISTANCE;
                double newLatitude = this.latitude + Math.sin(rad) * DISTANCE;
                return new LongLat(newLongitude, newLatitude);
            } else {
                throw new IllegalArgumentException("angle must be positive, less than 360 degrees, and divisible by 10");
            }
        } else {
            return new LongLat(this.longitude, this.latitude);
        }
    }

    /**
     * Given two longlats it will find the angle and return one that fits the criteria at which the drone can move
     * (divisible by 10 and less than 360)
     * @param ll2 the longlat at which we will calculate the angle to
     * @param website website to access the no-fly zone
     * @return integer angle value between 0 and 350 divisible by 10 towards ll2
     */
    public int findAngle(LongLat ll2, Website website) {
        double angleAsDouble = Math.toDegrees(Math.atan2(ll2.latitude - this.latitude, ll2.longitude - this.longitude));
        if (angleAsDouble < 0) { //not sure how this works
            angleAsDouble += 360;
        }
        int roundedAngle = (int) (Math.round(angleAsDouble / 10.0) * 10);
        if (roundedAngle == 360) {
            roundedAngle = 0;
        }
        LongLat lnglat = new LongLat(this.longitude, this.latitude);
        LongLat nextpos = nextPosition(roundedAngle);
        Line line = new Line(lnglat, nextpos);
        if (line.canMakeMove(website) && nextpos.isConfined()) {
            return roundedAngle;
        } else {
            return (roundedAngle - 10);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LongLat)) return false;
        LongLat longLat = (LongLat) o;
        return Double.compare(longLat.longitude, longitude) == 0 && Double.compare(longLat.latitude, latitude) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(longitude, latitude);
    }
}
