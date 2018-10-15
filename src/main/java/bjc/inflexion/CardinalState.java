package bjc.inflexion;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.LongPredicate;

/*
 * @TODO 2/12/18 Ben Culkin :AdditionalCardinals
 * 
 * Add some built-in implementations for various things.
 *
 * By this, I mean for various unit scales, like custom and metric weights
 */
/**
 * Customizations for number cardinalization.
 * 
 * @author EVE
 *
 */
public class CardinalState {
	/**
	 * Alias type for converting numbers to cardinals.
	 * 
	 * @author EVE
	 *
	 */
	@FunctionalInterface
	public interface Cardinalizer extends BiFunction<Long, CardinalState, String> {
		/*
		 * Alias
		 */
	}

	/**
	 * Custom cardinals for numbers.
	 */
	public final Map<Long, String> customNumbers;

	/**
	 * Custom functions to apply to certain scales.
	 */
	public final Map<LongPredicate, Cardinalizer> customScales;

	/**
	 * Create a new set of cardinalization customizations.
	 * 
	 * @param customNumbers
	 *        The custom numbers to use.
	 * @param customScales
	 *        The custom scales to use.
	 */
	public CardinalState(Map<Long, String> customNumbers, Map<LongPredicate, Cardinalizer> customScales) {
		this.customNumbers = customNumbers;
		this.customScales = customScales;
	}

	/**
	 * Handle a custom cardinal number
	 * 
	 * @param number
	 *        The number to handle
	 * @return The number as a cardinal, or null if we don't handle it.
	 */
	public String handleCustom(long number) {
		if(customNumbers.containsKey(number)) {
			return customNumbers.get(number);
		}

		for(Entry<LongPredicate, Cardinalizer> ent : customScales.entrySet()) {
			if(ent.getKey().test(number)) {
				return ent.getValue().apply(number, this);
			}
		}

		return null;
	}
}
