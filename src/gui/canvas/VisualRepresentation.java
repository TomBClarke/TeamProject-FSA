package gui.canvas;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import automaton.Automaton;
import automaton.Edge;
import automaton.State;

/**
 * Holds a model of a automata
 * 
 * @author Tom Clarke
 * @author Owen Pemberton
 * @author Botond Megyesfalvi
 * @author Piotr Wilczynski
 *
 */
public class VisualRepresentation {

	private ArrayList<VisualState> states;
	private ArrayList<VisualEdge> edges;
	private String currentText;

	private boolean showLabelList = false;
	private ArrayList<String> labelSetList;

	private String[] progText = new String[4];
	private String title;

	/**
	 * @param a
	 *            The automata to convert, ready for rending.
	 * @param text
	 *            The text
	 * @param title
	 *            The title
	 */
	public VisualRepresentation(Automaton a, String text, String title) {
		this.title = title;
		for (int i = 0; i < progText.length; i++) {
			progText[i] = null;
		}

		states = new ArrayList<VisualState>();
		edges = new ArrayList<VisualEdge>();
		currentText = text;
		labelSetList = new ArrayList<String>();
		// initialise the state queue and visited list
		Queue<State> stateQueue = new LinkedList<State>();
		ArrayList<Integer> visited = new ArrayList<Integer>();

		// Add the initial state
		stateQueue.add(a.getStart());

		// if the state queue isn't empty, visit the state at the head of the
		// queue
		while (!stateQueue.isEmpty()) {
			State s = stateQueue.poll();
			if (!visited.contains((Integer) s.getId())) {
				// add to the visited list
				visited.add(s.getId());
				// add this state to the state list
				states.add(new VisualState(s.getId(), s.isAccepting(),
						s.getX(), s.getY(), s.getLabel()));
				states.get(states.size() - 1)
						.setInitial(s.equals(a.getStart()));
				// add each edge to the edge list and the destination state to
				// the stateQueue
				for (Edge edge : s.getEdges()) {
					edges.add(new VisualEdge(s.getId(),
							edge.getState().getId(), edge.getName()));
					stateQueue.add(edge.getState());
				}
			}
		}
	}

	/**
	 * @param states
	 *            The states of the graph to use
	 * @param edges
	 *            The edges of the graph to use
	 * @param text
	 *            The text for this frame
	 * @param title
	 *            The title
	 */
	public VisualRepresentation(ArrayList<VisualState> states,
			ArrayList<VisualEdge> edges, String text, String title) {
		this.states = states;
		this.edges = edges;
		this.currentText = text;
		labelSetList = new ArrayList<String>();
		this.title = title;
	}

	/**
	 * Sets a new title for the frame
	 * 
	 * @param title
	 *            The new title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Gets the current title.
	 * 
	 * @return The current title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Gets the progress string array
	 * 
	 * @return The progress text
	 */
	public String[] getProgText() {
		for (int i = 0; i < progText.length; i++) {
			if (progText[i] == null)
				return null;
		}
		return progText;
	}

	/**
	 * Sets the progress string array
	 * 
	 * @param progText
	 *            The new progress array
	 */
	public void setProgText(String[] progText) {
		this.progText = progText;
	}

	/**
	 * Gets an array the edges of the graph.
	 * 
	 * @return All of the edges (in an array)
	 */
	public VisualEdge[] getEdges() {
		return edges.toArray(new VisualEdge[] {});
	}

	/**
	 * Gets all of the edges (array list)
	 * 
	 * @return ArrayList of the edges
	 */
	public ArrayList<VisualEdge> getEdgesArrayList() {
		return edges;
	}

	/**
	 * Gets a state in the graph.
	 * 
	 * @param id
	 *            The id of the state
	 * @return The state
	 */
	public VisualState getState(int id) {
		for (VisualState s : states) {
			if (s.getId() == id)
				return s;
		}
		return null;
	}

	/**
	 * Gets the text associated with the frame.
	 * 
	 * @return The text for the frame.
	 */
	public String getText() {
		return currentText;
	}

	/**
	 * Sets the text for the frame.
	 * 
	 * @param text
	 *            The new text.
	 */
	public void setText(String text) {
		currentText = text;
	}

	/**
	 * Gets all of the states of the graph.
	 * 
	 * @return The nodes of the graph
	 */
	public VisualState[] getStates() {
		return states.toArray(new VisualState[] {});
	}

	/**
	 * @return The list of labels for each closure of the DFA
	 */
	public ArrayList<String> getLabels() {
		return labelSetList;
	}

	/**
	 * Add a label the the list of set labels
	 * 
	 * @param label
	 *            The label detailing the set of state IDs of a closure
	 */
	public void addSetLabel(String label) {
		labelSetList.add(label);
	}

	/**
	 * Adds an entire list of labels to the frame.
	 * 
	 * @param extraList
	 *            The list
	 */
	public void addLabels(ArrayList<String> extraList) {
		labelSetList.addAll(extraList);
	}

	/**
	 * @return Whether or not the label list should be visible for this frame
	 */
	public boolean getLabelListVisible() {
		return showLabelList;
	}

	/**
	 * @param visible
	 *            Set whether the label list is visible for this frame
	 */
	public void setLabelListVisible(boolean visible) {
		showLabelList = visible;
	}

	/**
	 * Creates a copy with no references of the current frame.
	 * 
	 * @return A copy of this object with no references
	 */
	public VisualRepresentation copy() {
		ArrayList<VisualState> statesCopy = new ArrayList<VisualState>();
		ArrayList<VisualEdge> edgesCopy = new ArrayList<VisualEdge>();

		for (VisualState s : states) {
			statesCopy.add(new VisualState(s.getId(), s.isAccepting(),
					s.getX(), s.getY(), s.getLabel(), s.getHL()));
			statesCopy.get(statesCopy.size() - 1)
					.setInitial(s.isInitialState());
		}

		for (VisualEdge e : edges) {
			edgesCopy.add(new VisualEdge(e.getFrom(), e.getTo(), e.getInput(),
					e.getHighlight()));
		}

		VisualRepresentation vrCopy = new VisualRepresentation(statesCopy,
				edgesCopy, currentText, title);
		for (String s : this.labelSetList) {
			vrCopy.addSetLabel(s);
		}
		vrCopy.setLabelListVisible(this.getLabelListVisible());
		return vrCopy;
	}

	/**
	 * Copies the frame, and sets a new piece of text for it.
	 * 
	 * @param text
	 *            The new text to display
	 * @return A copy of the frame
	 */
	public VisualRepresentation copy(String text) {
		VisualRepresentation copied = this.copy();
		copied.setText(text);
		return copied;
	}
}
