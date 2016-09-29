package automaton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Class representing a state
 *
 * @author Piotr Wilczynski
 * @author Danyil Ilchenko
 * @author Botond Megyesfalvi
 *
 */
public class State {

	private boolean accepting;
	private boolean startOfRecPat;
	private boolean endOfRecPat;
	private int id;
	private int x, y, ylimit;
	private ArrayList<Edge> edges;
	private Set<Integer> loops;
	private String label;

	/**
	 * Create a new state
	 * 
	 * @param id
	 *            The ID
	 * @param name
	 *            The name
	 * @param accepting
	 *            Is accepting
	 */
	public State(int id, boolean accepting, String label) {
		this.startOfRecPat = false;
		this.endOfRecPat = false;
		this.id = id;
		this.accepting = accepting;
		this.edges = new ArrayList<Edge>();
		this.loops = new HashSet<Integer>();
		this.label = label;
	}

	/**
	 * Add an edge
	 * 
	 * @param edge
	 *            The edge
	 */
	public void addEdge(Edge edge) {
		edges.add(edge);
	}

	/**
	 * Remove an edge
	 * 
	 * @param edge
	 *            The edge to be removed
	 */
	public void removeEdge(Edge edge) {
		edges.remove(edge);
	}

	/**
	 * Check if accepting
	 * 
	 * @return True if accepting
	 */
	public boolean isAccepting() {
		return accepting;
	}

	/**
	 * Get the ID
	 * 
	 * @return The ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * Gets the label of the state.
	 * 
	 * @return The label.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Get the edges
	 * 
	 * @return The edges
	 */
	public ArrayList<Edge> getEdges() {
		return edges;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Set the x coordinate
	 * 
	 * @param x
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Set the y coordinate
	 * 
	 * @param y
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Get the x coordinate
	 * 
	 * @return
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * Get the y coordinate
	 * 
	 * @return
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * Get Y limit
	 * 
	 * @return Y limit
	 */
	public int getYLimit() {
		return this.ylimit;
	}

	/**
	 * Set Y limit
	 * 
	 * @param y
	 *            Y limit
	 */
	public void setYLimit(int y) {
		this.ylimit = y;
	}

	/**
	 * Show the coordinates
	 * 
	 * @return The string of the coordinates
	 */
	public String showCoord() {
		return "State: " + id + " x= " + x + " y= " + y;
	}

	/**
	 * Sets whether this state is the start of a recursive path.
	 * 
	 * @param ssrp
	 *            If it should be the start of a recursive path.
	 */
	public void setStartOfRecPat(boolean ssrp) {
		startOfRecPat = ssrp;
	}

	/**
	 * Sets whether this state is the end of a recursive path.
	 * 
	 * @param serp
	 *            If it should be the end of a recursive path.
	 */
	public void setEndOfRecPat(boolean serp) {
		endOfRecPat = serp;
	}

	/**
	 * Gets if it is the start of a recursive path
	 * 
	 * @return If it is the start of a recursive path
	 */
	public boolean isStartOfRecPat() {
		return startOfRecPat;
	}

	/**
	 * Gets if it is the end of a recursive path
	 * 
	 * @return If it is the end of a recursive path
	 */
	public boolean isEndOfRecPat() {
		return endOfRecPat;
	}

	/**
	 * Adds a new state to a loop
	 * 
	 * @param id
	 *            A new ID to loop on
	 */
	public void addLoop(int id) {
		loops.add(id);
	}

	/**
	 * Gets all of the ID in the loop
	 * 
	 * @return The list of IDs
	 */
	public Set<Integer> getLoops() {
		return loops;
	}

	public String toString() {
		return "<State: " + id + ">";
	}
}
