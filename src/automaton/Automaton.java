package automaton;

import gui.canvas.VisualRepresentation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Class representing an automaton
 *
 * @author Piotr Wilczynski
 * @author Owen Pemberton
 * @author Danyil Ilchenko
 * @author Botond Megyesfalvi
 * 
 */
public class Automaton {

	/** The alphabet. */
	private Set<Character> alphabet;

	/** The states. */
	private ArrayList<State> states;

	/**
	 * Create a new automaton with a given start and end state.
	 */
	public Automaton() {
		alphabet = new HashSet<Character>();
		states = new ArrayList<State>();
	}

	/**
	 * Show the automaton as a string.
	 *
	 * @return the string
	 */
	public String toString() {
		VisualRepresentation pic = new VisualRepresentation(this, "Test Frame (toString)", "");
		return pic.toString();
	}

	/**
	 * Get the start state.
	 *
	 * @return The start state
	 */
	public State getStart() {
		return states.get(0);
	}

	/**
	 * Add a character to the alphabet.
	 *
	 * @param c
	 *            The character to be added
	 */
	public void addChar(Character c) {
		alphabet.add(c);
	}

	/**
	 * Get the alphabet.
	 *
	 * @return The alphabet
	 */
	public Set<Character> getAlphabet() {
		return alphabet;
	}

	/**
	 * Set the alphabet.
	 *
	 * @param alphabet
	 *            The alphabet
	 */
	public void setAlphabet(Set<Character> alphabet) {
		this.alphabet = alphabet;
	}

	/**
	 * Sets y coordinates.
	 *
	 * @param state
	 *            the state to set Y coordinate to
	 * @param accumulator
	 *            the accumulator
	 * @return the int
	 */
	public int setYCoordinates(State state, int accumulator) {
		ArrayList<Edge> edges = new ArrayList<Edge>(state.getEdges());
		Set<Integer> loops = state.getLoops();
		boolean end = state.isEndOfRecPat();
		boolean oseol = true;

		int upperLimit = accumulator;

		for (int i = 0; i < edges.size(); i++) {
			if (!(edges.get(i).getState().equals(state) || (end && edges.get(i).getState().isStartOfRecPat()
					&& loops.contains(edges.get(i).getState().getId())))) {
				accumulator = setYCoordinates(edges.get(i).getState(), accumulator);
				oseol = false;
			}
		}

		if (oseol) {
			state.setY(accumulator);
			// System.out.println(state.getId() + " y coord is : " +
			// accumulator);
			return 1 + accumulator;
		}

		state.setY(((accumulator - 1 - upperLimit) / 2 + upperLimit));

		return accumulator;
	}

	/**
	 * Sets the coords.
	 */
	public void setCoords() {
		setYCoordinates(getStart(), 0);
		setXCoordinates(getStart(), 0);
	}

	/**
	 * Sets the x coordinates.
	 *
	 * @param s
	 *            the s
	 * @param x
	 *            the x
	 */
	public void setXCoordinates(State s, int x) {
		s.setX(x);
		boolean end = s.isEndOfRecPat();
		Set<Integer> loops = s.getLoops();
		for (Edge edge : s.getEdges()) {

			if (!(s.equals(edge.getState())
					|| (end && edge.getState().isStartOfRecPat() && loops.contains(edge.getState().getId()))))
				setXCoordinates(edge.getState(), x + 1);
		}
	}

	/**
	 * Gets the number of states.
	 *
	 * @return the number of states
	 */
	public int getNumberOfStates() {
		return states.size();
	}

	/**
	 * Adds the states.
	 *
	 * @param sts
	 *            the sts
	 */
	public void addStates(ArrayList<State> sts) {
		states.addAll(sts);
	}

	/**
	 * Adds the state.
	 *
	 * @param s
	 *            the s
	 */
	public void addState(State s) {
		states.add(s);
	}

	/**
	 * Gets the states.
	 *
	 * @return the states
	 */
	public State[] getStates() {
		return states.toArray(new State[] {});
	}
}