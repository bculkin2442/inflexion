package bjc.inflexion.examples;

import java.util.Scanner;

import bjc.inflexion.EnglishUtils;

/**
 * Test class for checking indefinite articles
 * 
 * @author bjculkin
 *
 */
public class IndefTester {
	/**
	 * Main method.
	 * 
	 * @param args
	 *             Unused CLI args.
	 */
	public static void main(String[] args) {
		Scanner scn = new Scanner(System.in);

		System.out.print("Enter word: ");
		String word = scn.nextLine().trim();

		while (!word.equals("")) {
			System.out.printf("\t%s %s\n", EnglishUtils.pickIndefinite(word), word);

			System.out.print("Enter word: ");
			word = scn.nextLine().trim();
		}

		scn.close();
	}
}
