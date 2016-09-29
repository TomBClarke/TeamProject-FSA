package automaton;

/**
 * Class representing a character
 *
 * @author Botond Megyesfalvi
 * @author Owen Pemberton
 * @author Danyil Ilchenko
 *
 */
public class NewChar {

	private boolean epsilon;
	private char character;

	/**
	 * Create an empty character
	 */
	public NewChar() {
		epsilon = true;
	}

	/**
	 * Create a new character
	 * 
	 * @param c
	 *            The character
	 */
	public NewChar(char c) {
		epsilon = false;
		character = c;
	}

	/**
	 * Get the character
	 * 
	 * @return The character
	 */
	public char getChar() {
		assert (!epsilon);
		return character;
	}

	/**
	 * Check if the character is empty
	 * 
	 * @return True if the character is empty
	 */
	public boolean isEpsilon() {
		return epsilon;
	}

	/**
	 * Convert the character to a string
	 */
	public String toString() {
		if (epsilon) {
			return "\u03B5";
		}
		return new String("" + character);
	}
}
