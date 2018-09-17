package bjc.inflexion;

import org.junit.Test;

import static org.junit.Assert.*;

import static bjc.inflexion.InflectionML.inflect;

public class InflectionMLTest {
	@Test
	public void testNumDirective() {
		assertEquals("no results", inflect("<#n:0> <N:results>"));
		assertEquals("7 results",  inflect("<#n:7> <N:results>"));

		// FIXME
		//
		// Adjust this to use <V> for were/was when it is implemented
		assertEquals("no items were found", inflect("<#n:0> <N:item> were found"));
		assertEquals("no item was found", inflect("<#s:0> <N:item> was found"));

		assertEquals("a result", inflect("<#a:1> <N:results>"));
		assertEquals("3 results", inflect("<#a:3> <N:results>"));

		assertEquals("an outcome", inflect("<#a:1> <N:outcomes>"));
		assertEquals("3 outcomes", inflect("<#a:3> <N:outcomes>"));
	}
}
