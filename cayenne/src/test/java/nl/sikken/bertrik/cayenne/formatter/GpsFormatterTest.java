package nl.sikken.bertrik.cayenne.formatter;

import java.nio.ByteBuffer;

import org.junit.Assert;
import org.junit.Test;

public final class GpsFormatterTest {

	@Test
	public void testEncodeDecode() {
		GpsFormatter formatter = new GpsFormatter();
		Double[] coords = new Double[] {52.0, 4.1, -3.5};

		// encode
		ByteBuffer bb = ByteBuffer.allocate(100);
		formatter.encode(bb, coords);

		// decode
		bb.flip();
		Double[] parsed = formatter.parse(bb);
		Assert.assertEquals(coords[0], parsed[0], 0.01);
		Assert.assertEquals(coords[1], parsed[1], 0.01);
		Assert.assertEquals(coords[2], parsed[2], 0.01);
	}
	
}
