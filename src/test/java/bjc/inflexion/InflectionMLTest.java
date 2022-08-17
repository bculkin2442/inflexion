package bjc.inflexion;

import static bjc.inflexion.InflectionML.inflect;
import static bjc.inflexion.InflectionMLTest.InflectPair.pair;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

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
		// Check # command //
		//////////////////////

		// Check general inflection
		assertInflects("<#:%s> <N:indexes> %s found",
				pair("0 indexes were found", 0, "were"),
				pair("1 index was found", 1, "was"),
				pair("99 indexes were found", 99, "were"));

		// Check fancier inflection
		//
		// There was a 'c' option attached to the '#' directive, but I have no
		// recollection of what that should've done.
		assertInflects("<#wn:%d> <Noun:indexes> %s found",
				pair("no indexes were found", 0, "were"),
				pair("one index was found", 1, "was"),
				pair("99 indexes were found", 99, "were"));

		// Check count inflection
		assertInflects("<#w20:%d> <N:indexes> were found",
				pair("six indexes were found", 6),
				pair("nineteen indexes were found", 19),
				pair("20 indexes were found", 20));

		// Check 'n' option
		assertInflects("<#n:%d> <N:results>", pair("no results", 0),
				pair("7 results", 7));

		// FIXME
		//
		// Adjust this to use <V> for were/was when it is implemented
		// Check general inflection
		assertInflects("<#%s:%d> <N:item> %s found",
				pair("no items were found", "n", 0, "were"),
				pair("no item was found", "s", 0, "was"));

		// Check article picking
		assertInflects("<#a:%d> <N:%s>", pair("a result", 1, "results"),
				pair("3 results", 3, "results"), pair("an outcome", 1, "outcomes"),
				pair("7 outcomes", 7, "outcomes"));

		// Check 'w' option
		assertInflects("<#w:%d> <N:results>", pair("six results", 6),
				pair("ten results", 10), pair("11 results", 11));
		// Check 'o' option
		assertInflects("<#o:%d> <N:results>", pair("6th result", 6),
				pair("11th result", 11), pair("22nd result", 22));
		assertInflects("<#ow:%d> <N:results>", pair("first result", 1),
				pair("sixth result", 6), pair("22nd result", 22));
		assertInflects("<#o15:%d> <N:results>", pair("6th result", 6),
				pair("11th result", 11), pair("22 results", 22));

		// Check 'f' option
		assertInflects("Found <#f:%d> <N:matches>", pair("Found no matches", 0),
				pair("Found one match", 1), pair("Found a couple of matches", 2),
				pair("Found a few matches", 4), pair("Found several matches", 8),
				pair("Found many matches", 11));

		assertInflects("Searching for <np:items>....found <#f1:%d>",
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
		assertInflects("Found <#e:%d> <N:matches>", pair("Found no match", 0),
				pair("Found a match", 1), pair("Found ten matches", 10),
				pair("Found 12 matches", 12));

		// Check 'd' option
		assertInflects("<#d:%d><N:Match> found", pair("Match found", 1),
				pair("Matches found", 2));

		////////////////////////
		// Check N command //
		///////////////////////

		// Check 'c' option
		assertInflects("<#:%d> <N:%s> found", pair("7 maximums found", 7, "maximum"),
				pair("7 formulas found", 7, "formula"),
				pair("7 corpuses found", 7, "corpuses"),
				pair("7 brothers found", 7, "brothers"));

		assertInflects("<#:%d> <nc:%s> found", pair("7 maxima found", 7, "maximum"),
				pair("7 formulae found", 7, "formula"),
				pair("7 corpora found", 7, "corpus"),
				pair("7 brethren found", 7, "brother"));
	}

	/**
	 * Test compiled inflection markup.
	 */
	@Test
	public void testCompiledML() {
		assertCInflects("test literal string", pair("test literal string"));

		assertCInflects("test $1 string", pair("test literal string", "literal"),
				pair("test variable string", "variable"));
		//////////////////////
		// Check # command //
		//////////////////////

		// Check general inflection
		assertCInflects("<#:$1> <N:indexes> $2 found",
				pair("0 indexes were found", 0, "were"),
				pair("1 index was found", 1, "was"),
				pair("99 indexes were found", 99, "were"));

		// Check fancier inflection
		//
		// There was a 'c' option attached to the '#' directive, but I have no
		// recollection of what that should've done.
		//
		// As for the mixed-case options, the directive type can't be mixed case.
		assertCInflects("<#wn:$1> <N:indexes> $2 found",
				pair("no indexes were found", 0, "were"),
				pair("one index was found", 1, "was"),
				pair("99 indexes were found", 99, "were"));

		// Check count inflection
		assertCInflects("<#w20:$1> <N:indexes> were found",
				pair("six indexes were found", 6),
				pair("nineteen indexes were found", 19),
				pair("20 indexes were found", 20));

		// Check 'n' option
		assertCInflects("<#n:$1> <N:results>", pair("no results", 0),
				pair("7 results", 7));

		// FIXME
		//
		// Adjust this to use <V> for were/was when it is implemented
		// Check general inflection
		//
		// For now, compiled inflection strings don't support variable options
		assertCInflects("<#n:$1> <N:item> $2 found",
				pair("no items were found", 0, "were"));
		assertCInflects("<#ns:$1> <N:item> $2 found",
				pair("no item was found", 0, "was"));

		// Check article picking
		assertCInflects("<#a:$1> <N:$2>", pair("a result", 1, "results"),
				pair("3 results", 3, "results"), pair("an outcome", 1, "outcomes"),
				pair("7 outcomes", 7, "outcomes"));

		// Check 'w' option
		assertCInflects("<#w:$1> <N:results>", pair("six results", 6),
				pair("ten results", 10), pair("11 results", 11));
		// Check 'o' option
		assertCInflects("<#o:$1> <N:results>", pair("6th result", 6),
				pair("11th result", 11), pair("22nd result", 22));
		assertCInflects("<#ow:$1> <N:results>", pair("first result", 1),
				pair("sixth result", 6), pair("22nd result", 22));
		assertCInflects("<#o15:$1> <N:results>", pair("6th result", 6),
				pair("11th result", 11), pair("22 results", 22));

		// Check 'f' option
		assertCInflects("Found <#f:$1> <N:matches>", pair("Found no matches", 0),
				pair("Found one match", 1), pair("Found a couple of matches", 2),
				pair("Found a few matches", 4), pair("Found several matches", 8),
				pair("Found many matches", 11));

		// FIXME Don't require spaces to mark out directives.
		// - Ben Culkin, 10/28/18
		assertCInflects("Searching for <np:items> ....found <#f1:$1>",
				pair("Searching for items ....found none", 0),
				pair("Searching for items ....found one", 1),
				pair("Searching for items ....found a couple", 2),
				pair("Searching for items ....found a few", 4),
				pair("Searching for items ....found several", 8),
				pair("Searching for items ....found many", 11));

		assertCInflects("Found <#fns:$1> <N:matches>", pair("Found no match", 0),
				pair("Found several matches", 7));
		assertCInflects("Found <#fa:$1> <N:matches>", pair("Found a match", 1),
				pair("Found several matches", 7));

		// Check 'e' option
		assertCInflects("Found <#e:$1> <N:matches>", pair("Found no match", 0),
				pair("Found a match", 1), pair("Found ten matches", 10),
				pair("Found 12 matches", 12));

		// Check 'd' option
		assertCInflects("<#d:$1> <N:Match> found", pair(" Match found", 1),
				pair(" Matches found", 2));

		////////////////////////
		// Check N command //
		///////////////////////

		// Check 'c' option
		assertCInflects("<#:$1> <N:$2> found", pair("7 maximums found", 7, "maximum"),
				pair("7 formulas found", 7, "formula"),
				pair("7 corpuses found", 7, "corpuses"),
				pair("7 brothers found", 7, "brothers"));

		assertCInflects("<#:$1> <nc:$2> found", pair("7 maxima found", 7, "maximum"),
				pair("7 formulae found", 7, "formula"),
				pair("7 corpora found", 7, "corpus"),
				pair("7 brethren found", 7, "brother"));
	}

	private static void assertInflects(String real, InflectPair... pairs) {
		for (InflectPair pair : pairs) {
			assertEquals(pair.exp, inflect(String.format(real, pair.pars)));
		}
	}

	private static void assertCInflects(String real, InflectPair... pairs) {
		InflectionString strang = new InflectionString(real);

		for (InflectPair pair : pairs) {
			assertEquals(pair.exp, strang.inflect(pair.pars));
		}
	}

	static class InflectPair {
		public String exp;
		public Object[] pars;

		public InflectPair(String str, Object... pars) {
			this.exp = str;
			this.pars = pars;
		}

		public static InflectPair pair(String str, Object... pars) {
			return new InflectPair(str, pars);
		}
	}
}
