package nl.bertriksikken.geo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class SimpleGeoModelTest {

    @Test
    public void testDistance() {
        double[] gouda = new double[]{52.01667, 4.70833};
        double[] groningen = new double[]{53.21917, 6.56667};
        SimpleGeoModel model = new SimpleGeoModel();
        double d = model.distance(gouda, groningen);

        Assertions.assertEquals(183557, d, 1);
    }

}
