package bjc.inflexion;

import org.junit.Test;

import static org.junit.Assert.*;
import static bjc.inflexion.InflectPair.pair;

import static bjc.inflexion.InflectionML.inflect;

/**
 * Tests for inflection markup.
 * 
 * @author bjculkin
 *
 */
public class InflectionMLTest {
	/**
	 * Test inflection markup.
	 */
	@Test
	public void testML() {
		//////////////////////
		// Check # command   //
		//////////////////////

		// Check general inflection
		assertInflects("<#:%s> <N:indexes> %s found", pair("0 indexes were found", 0, "were"),
				pair("1 index was found", 1, "was"), pair("99 indexes were found", 99, "were"));

		// Check fancier inflection
		assertInflects("<#wnc:%d> <Noun:indexes> %s found", pair("no indexes were found", 0, "were"),
				pair("one index was found", 1, "was"), pair("99 indexes were found", 99, "were"));

		// Check count inflection
		assertInflects("<#w20:%d> <N:indexes> were found", pair("six indexes were found", 6),
				pair("nineteen indexes were found", 19), pair("20 indexes were found", 20));

		// Check 'n' option
		assertInflects("<#n:%d> <N:results>", pair("no results", 0), pair("7 results", 7));

		// FIXME
		//
		// Adjust this to use <V> for were/was when it is implemented
		// Check general inflection
		assertInflects("<#%s:%d> <N:item> %s found", pair("no items were found", "n", 0, "were"),
				pair("no item was found", "s", 0, "was"));

		// Check article picking
		assertInflects("<#a:%d> <N:%s>", pair("a result", 1, "results"), pair("3 results", 3, "results"),
				pair("an outcome", 1, "outcomes"), pair("7 outcomes", 7, "outcomes"));

		// Check 'w' option
		assertInflects("<#w:%d> <N:results>", pair("six results", 6), pair("ten results", 10),
				pair("11 results", 11));
		// Check 'o' option
		assertInflects("<#o:%d> <N:results>", pair("6th result", 6), pair("11th result", 11),
				pair("22nd result", 22));
		assertInflects("<#ow:%d> <N:results>", pair("first result", 1), pair("sixth result", 6),
				pair("22nd result", 22));
		assertInflects("<#o15:%d> <N:results>", pair("6th result", 6), pair("11th result", 11),
				pair("22 results", 22));

		// Check 'f' option
		assertInflects("Found <#f:%d> <N:matches>", pair("Found no matches", 0), pair("Found one match", 1),
				pair("Found a couple of matches", 2), pair("Found a few matches", 4),
				pair("Found several matches", 8), pair("Found many matches", 11));

		assertInflects("Searching for <Np:items>....found <#f1:%d>",
				pair("Searching for items....found none", 0),
				pair("Searching for items....found one", 1),
				pair("Searching for items....found a couple", 2),
				pair("Searching for items....found a few", 4),
				pair("Searching for items....found several", 8),
				pair("Searching for items....found many", 11));

		assertInflects("Found <#fs:%d> <N:matches>", pair("Found no match", 0),
				pair("Found several matches", 7));
		assertInflects("Found <#fa:%d> <N:matches>", pair("Found a match", 1),
				pair("Found several matches", 7));

		// Check 'e' option
		assertInflects("Found <#e:%d> <N:matches>", pair("Found no match", 0), pair("Found a match", 1),
				pair("Found ten matches", 10), pair("Found 12 matches", 12));

		// Check 'd' option
		assertInflects("<#d:%d><N:Match> found", pair("Match found", 1), pair("Matches found", 2));

		////////////////////////
		// Check N command      //
		///////////////////////

		// Check 'c' option
		assertInflects("<#:%d> <N:%s> found", pair("7 maximums found", 7, "maximum"),
				pair("7 formulas found", 7, "formula"), pair("7 corpuses found", 7, "corpuses"),
				pair("7 brothers found", 7, "brothers"));

		assertInflects("<#:%d> <Nc:%s> found", pair("7 maxima found", 7, "maximum"),
				pair("7 formulae found", 7, "formula"), pair("7 corpora found", 7, "corpus"),
				pair("7 brethren found", 7, "brother"));
	}

	private static void assertInflects(String exp, String real) {
		assertEquals(exp, inflect(real));
	}

	private static void assertInflects(String real, InflectPair... pairs) {
		for (InflectPair pair : pairs) {
			assertEquals(pair.exp, inflect(String.format(real, pair.pars)));
		}
	}
}
