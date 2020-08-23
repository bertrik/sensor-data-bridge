package nl.bertriksikken.geo;

/**
 * Simpe geo model that calculates basic geo approximations.
 */
public final class SimpleGeoModel {

    private static final double CIRCUMFERENCE = 40075e3;
    
    /**
     * Calculates approximate distance between two locations on earth.
     * 
     * Probably does not work around the poles, around the date line, on long distances.
     * 
     * @param p1 (lat,lon) pair (degrees)
     * @param p2 (lat,lon) pair (degrees)
     * @return approximate distance (meter)
     */
    double distance(double[] p1, double[] p2) {
        double[] middle = new double[2];
        middle[0] = (p1[0] + p2[0]) / 2;
        middle[1] = (p1[1] + p2[1]) / 2;

        double circle = CIRCUMFERENCE * Math.cos(Math.toRadians(middle[0]));
        double dlat = CIRCUMFERENCE * (p2[0] - p1[0]) / 360.0;
        double dlon = circle * (p2[1] - p1[1]) / 360.0;
        return Math.sqrt(dlat * dlat + dlon * dlon);
    }
}
