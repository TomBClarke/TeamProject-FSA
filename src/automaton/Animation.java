package automaton;

import gui.canvas.VisualRepresentation;
import gui.canvas.VisualState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Class containing particular snapshots of an automaton.
 * 
 * @author Piotr Wilczynski
 * @author Botond Megyesfalvi
 * 
 */
public class Animation {

	/** The frames */
	protected ArrayList<VisualRepresentation> frames;

	/** The automaton */
	protected Automaton automaton;
	protected VisualRepresentation finalDFAwithoutNFA;

	/**
	 * Create a new instance
	 */
	public Animation() {
		frames = new ArrayList<VisualRepresentation>();
	}

	/**
	 * Get a particular frame
	 *
	 * @param index
	 *            The index
	 * @return The frame
	 */
	public VisualRepresentation getFrame(int index) {
		return frames.get(index);
	}

	/**
	 * Get the number of frames
	 *
	 * @return The number of frames
	 */
	public int getFrameCount() {
		return frames.size();
	}

	/**
	 * Show the frames as a string
	 *
	 * @return The string
	 */
	public String toString() {
		String out;
		out = "Frames: " + this.getFrameCount() + "\n";
		for (int i = 0; i < this.getFrameCount(); i++) {
			out += ("Frame " + i + "\n");
			out += this.getFrame(i).toString();
		}
		return out;
	}

	/**
	 * Propagate coordinates
	 */
	protected void propagateCoordinates() {
		// initialise the state queue and visited list
		Queue<State> stateQueue = new LinkedList<State>();
		ArrayList<Integer> visited = new ArrayList<Integer>();

		// Add the initial state
		stateQueue.add(automaton.getStart());

		// if the state queue isn't empty, visit the state at the head of the
		// queue
		while (!stateQueue.isEmpty()) {
			State s = stateQueue.poll();
			if (!visited.contains((Integer) s.getId())) {
				// add to the visited list
				visited.add(s.getId());
				// revisit every node with the same id in every frame and set
				// coords
				for (VisualRepresentation tempFrame : frames) {
					for (VisualState tempState : tempFrame.getStates()) {
						if (tempState.getId() == s.getId())
							tempState.setCoords(s.getX(), s.getY());
					}
				}
				// add destination states of edges to the state list
				for (Edge edge : s.getEdges()) {
					stateQueue.add(edge.getState());
				}
			}
		}
	}

	/**
	 * Gets the all frames
	 *
	 * @return The all frames
	 */
	public ArrayList<VisualRepresentation> getAllFrames() {
		return frames;
	}

	/**
	 * Gets the last frame
	 *
	 * @return The last frame
	 */
	public VisualRepresentation getLastFrame() {
		return frames.get(frames.size() - 1);
	}

	/**
	 * Gets the automaton
	 *
	 * @return The automaton
	 */
	public Automaton getAutomaton() {
		return automaton;
	}

	/**
	 * Get the final DFA frame without NFA
	 * 
	 * @return The final DFA frame without NFA
	 */
	public VisualRepresentation getFinalDFAwithoutNFA() {
		return finalDFAwithoutNFA;
	}
}