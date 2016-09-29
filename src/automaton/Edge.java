package automaton;

/**
 * Class representing an edge
 *
 * @author Mihnea Patentasu
 * @author Piotr Wilczynski
 * @author Danyil Ilchenko
 * @author Botond Megyesfalvi
 *
 */
public class Edge {

	private State s;
	public NewChar input;
	private String name;
	private boolean expression;

	/**
	 * Create a new instance
	 * 
	 * @param state
	 *            The state the edge goes to
	 * @param input
	 *            The character
	 */
	public Edge(State state, NewChar input) {
		this.s = state;
		this.input = input;
		this.expression = false;
	}

	/**
	 * Create a new instance
	 * 
	 * @param state
	 *            The state the edge goes to
	 * @param name
	 *            The string showing which part of the RegEx this edge
	 *            corresponds to
	 */
	public Edge(State state, String name) {
		this.s = state;
		this.name = name;
		this.input = new NewChar();
		this.expression = true;
	}

	/**
	 * Get the state the edge goes to
	 * 
	 * @return The state the edge goes to
	 */
	public State getState() {
		return s;
	}

	/**
	 * Get the character
	 * 
	 * @return The character
	 */
	public NewChar getInput() {
		return input;
	}

	/**
	 * Get the character
	 * 
	 * @return The character
	 */
	public String getName() {
		if (expression)
			return name;
		return input.toString();
	}

	/**
	 * Show the edge as a string
	 * 
	 * @return The string
	 */
	public String toString() {
		return getName() + s.getId();
	}
}
