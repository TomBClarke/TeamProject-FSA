package automaton.thompsons;

/**
 * Tree-like representation of a regular expression
 *
 * @author Danyil Ilchenko
 * @author Botond Megyesfalvi
 *
 */
public class ConBlock {

	private BlockType type;
	private Boolean nesting;
	private ConBlock[] insides;
	private char elem;

	/**
	 * Create a new instance
	 * 
	 * @param a
	 *            The character
	 */
	public ConBlock(char a) {
		nesting = false;
		type = BlockType.ELEM;
		elem = a;
	}

	/**
	 * Create a new instance
	 * 
	 * @param type
	 *            The type
	 * @param insides
	 *            The further ConBlocks
	 */
	public ConBlock(BlockType type, ConBlock[] insides) {
		nesting = true;
		this.type = type;
		this.insides = insides;
	}

	/**
	 * Show the ConBlock as a string
	 * 
	 * @return The string
	 */
	public String toString() {
		if (nesting) {
			switch (type) {
			case DISJ:
				return "(" + insides[0].toString() + "|" + insides[1].toString() + ")";
			case STAR:
				return "((" + insides[0].toString() + ")*)";
			case CONCAT:
				return "(" + insides[0].toString() + insides[1].toString() + ")";
			case EMPTY:
				return "\u03B5";
			default:
				return null;
			}
		} else {
			return "" + elem;
		}
	}

	/**
	 * Get the type
	 * 
	 * @return The type
	 */
	public BlockType getType() {
		return type;
	}

	/**
	 * Get the further ConBlocks
	 * 
	 * @return The further ConBlocks
	 */
	public ConBlock[] getInsides() {
		return insides;
	}

	/**
	 * Get the element
	 * 
	 * @return The element
	 */
	public char getElement() {
		return elem;
	}

	/**
	 * Calculates how many x coordinates does it take to display the automaton
	 * created from this ConBlock
	 * 
	 * @param c
	 *            the ConBlock
	 * @return an integer representing how many x coordinates does it take to
	 *         display
	 */
	public int getXs() {
		int sum = 0;
		switch (type) {
		case ELEM:
			sum = 0;
			break;
		case EMPTY:
			sum = 0;
			break;
		case CONCAT:
			sum = 1 + insides[0].getXs() + insides[1].getXs();
			break;
		case DISJ:
			sum = 2 + Math.max(insides[0].getXs(), insides[1].getXs());
			break;
		case STAR:
			sum = 2 + insides[0].getXs();
			break;
		default:
			break;
		}
		return sum;
	}

	/**
	 * Calculates how many y coordinates are there going to be above our first element of the conblock 
	 * created from a ConBlock
	 * 
	 * @param c
	 *            the ConBlock
	 * @return an integer representing how many y coordinates does it take to
	 *         display
	 */
	public int getYs() {
		int sum = 0;
		switch (type) {
		case ELEM:
			sum = 0;
			break;
		case EMPTY:
			sum = 0;
			break;
		case CONCAT:
			sum = Math.max(insides[0].getYs(), insides[1].getYs());
			break;
		case DISJ:
			sum = 1 + insides[0].getYsHelp();
			break;
		case STAR:
			sum = insides[0].getYs();
			break;
		default:
			break;
		}
		return sum;
	}

	/**
	 * A helper function. At the beginning we start looking at how many y coordinates are there going to be above our first element of the conblock,
	 * once we reach a disjunction this helper function will be called, which will calculate how many y coordinates does it take to expand the disjunction's first conblock completely.
	 */
	private int getYsHelp() {
		int sum = 0;
		switch (type) {
		case ELEM:
			sum = 0;
			break;
		case CONCAT:
			sum = Math.max(insides[0].getYsHelp(), insides[0].getYsHelp());
			break;
		case DISJ:
			sum = 2 + insides[0].getYsHelp() + insides[0].getYsHelp();
			break;
		case STAR:
			sum = insides[0].getYsHelp();
			break;
		case EMPTY:
			sum = 0;
			break;
		default:
			break;
		}
		return sum;
	}

}