package bjc.inflexion;

import org.junit.Test;

import static org.junit.Assert.*;

import static bjc.inflexion.InflectionML.inflect;

public class InflectionMLTest {
	@Test
	public void testNumDirective() {
		assertEquals("no results", inflect("<#n:0> <N:results>"));
		assertEquals("7 results",  inflect("<#n:7> <N:results>"));
	}
}
