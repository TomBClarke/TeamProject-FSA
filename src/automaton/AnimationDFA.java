package automaton;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import gui.canvas.EdgeHighlights;
import gui.canvas.StateHighlights;
import gui.canvas.VisualEdge;
import gui.canvas.VisualRepresentation;
import gui.canvas.VisualState;

/**
 * Class that takes NFA and converts it to DFA it also wraps DFA into structure
 * most suitable for visualisation to supply to visualiser.
 * 
 * @author Danyil Ilchenko
 * @author Tom Clarke
 */
public class AnimationDFA extends Animation {

	public final String frameTitle = "DFA";

	/**
	 * ArrayList of object-representations of NFA used by visualising module.
	 */
	private ArrayList<VisualRepresentation> nfaFrames;

	/**
	 * Array of sets of states each containing epsilon closure of NFA states
	 * with array index that corresponds to every states' state id.
	 */
	private Set<State>[] closure;

	/**
	 * ArrayList of closures each representing DFA state that closes a loop
	 * spanning more than 1 state.
	 */
	private ArrayList<Set<State>> loopcl;

	/**
	 * ArrayList of DFA states corresponding to loopcl, used as source of
	 * references for creating edges.
	 */
	private ArrayList<State> knot;

	/** The alphabet shared by NFA and DFA. */
	private Character[] alphabet;

	/**
	 * ArrayList of booleans corresponding to each closure, shows whether a
	 * given closure would produce an accepting DFA state.
	 */
	private boolean[] accpt;

	/** The representation of NFA in visualiser, used to highlight states. */
	private VisualRepresentation lastNfaFrame;

	private ArrayList<String> allLabels;

	/**
	 * Instantiates a new animation dfa.
	 *
	 * @param a
	 *            NFA
	 * @param lastNfaFrame
	 *            the last nfa frame
	 */
	@SuppressWarnings("unchecked")
	public AnimationDFA(Automaton a, VisualRepresentation lastNfaFrame) {
		// initialise required structures

		super();
		nfaFrames = new ArrayList<VisualRepresentation>();
		State[] states = a.getStates();
		closure = (HashSet<State>[]) Array.newInstance(HashSet.class, a.getNumberOfStates());
		loopcl = new ArrayList<Set<State>>();
		knot = new ArrayList<State>();
		accpt = new boolean[a.getNumberOfStates()];
		this.lastNfaFrame = lastNfaFrame;
		allLabels = new ArrayList<String>();

		// compute inner closure (epsilon move on depth 1 only) for each NFA
		// state
		Set<State> c;
		ArrayList<Edge> edges;
		for (int i = 0; i < states.length; i++) {
			c = new HashSet<State>();
			c.add(states[i]);
			edges = states[i].getEdges();
			for (int j = 0; j < edges.size(); j++)
				if (edges.get(j).getInput().isEpsilon())
					c.add(edges.get(j).getState());
			closure[i] = c;
		}
		expand();

		// Create DFA with a starting state
		super.automaton = new Automaton();
		State start = new State(super.automaton.getNumberOfStates(), accpt[0], "1");
		super.automaton.addState(start);
		super.automaton.setAlphabet(a.getAlphabet());
		alphabet = a.getAlphabet().toArray(new Character[] {});

		String newLabel = "";

		for (State n : closure[0]) {
			if (newLabel.length() > 0)
				newLabel += ", ";
			newLabel += (n.getId() + 1);
		}

		// Set the text describing what the algorithm is doing at its current
		// stage
		String text = "Start of the construction, dummy DFA state with empty closure";
		nfaFrames.add(lastNfaFrame.copy(text));
		VisualRepresentation startingdfaVr = new VisualRepresentation(super.automaton, text, frameTitle);
		allLabels.add("1 : { " + newLabel + " }");
		startingdfaVr.addLabels((ArrayList<String>) allLabels.clone());
		frames.add(startingdfaVr);

		// construct closures
		construct(start, closure[0]);
		// add the final frame
		text = "Final graph";
		nfaFrames.add(lastNfaFrame.copy(text));
		VisualRepresentation endingdfaVr = new VisualRepresentation(super.automaton, text, frameTitle);
		endingdfaVr.addLabels((ArrayList<String>) allLabels.clone());
		frames.add(endingdfaVr);

		// store the final DFA representation without NFA (for use in
		// TestString)
		finalDFAwithoutNFA = frames.get(frames.size() - 1);
		finalDFAwithoutNFA.setLabelListVisible(false);

		// Set the coordinates for states in the final frame
		automaton.setCoords();
		// Propagate these new coordinates through previous frames
		propagateCoordinates();

		combineFrames();
	}

	/**
	 * Make sure both NFA and DFA are correctly displayed together on the
	 * canvas.
	 */
	private void combineFrames() {
		int maxY = 0;
		int maxId = 0;
		for (VisualState s : nfaFrames.get(nfaFrames.size() - 1).getStates()) {
			if (s.getY() > maxY)
				maxY = s.getY();
			if (s.getId() > maxId)
				maxId = s.getId();
		}

		// add spacing so they don't overlap
		maxY += 2;
		maxId++;

		for (int i = 0; i < frames.size(); i++) {
			VisualRepresentation nfaFrame = nfaFrames.get(i);
			VisualRepresentation dfaFrame = frames.get(i);

			ArrayList<VisualState> states = new ArrayList<VisualState>();
			ArrayList<VisualEdge> edges = new ArrayList<VisualEdge>();

			for (VisualState s : nfaFrame.getStates()) {
				VisualState ns = new VisualState(s.getId(), s.isAccepting(), s.getX(), s.getY(), s.getLabel(),
						s.getHL());
				states.add(ns);
				if (s.isInitialState())
					states.get(states.size() - 1).setInitial(true);
			}
			for (VisualEdge e : nfaFrame.getEdges())
				edges.add(new VisualEdge(e.getFrom(), e.getTo(), e.getInput(), e.getHighlight()));

			for (VisualState s : dfaFrame.getStates()) {
				states.add(new VisualState(s.getId() + maxId, s.isAccepting(), s.getX(), s.getY() + maxY, s.getLabel(),
						s.getHL()));
				if (s.isInitialState()) {
					states.get(states.size() - 1).setInitial(true);
				}
			}
			for (VisualEdge e : dfaFrame.getEdges())
				edges.add(new VisualEdge(e.getFrom() + maxId, e.getTo() + maxId, e.getInput(), e.getHighlight()));

			VisualRepresentation newVR = new VisualRepresentation(states, edges, dfaFrame.getText(), frameTitle);
			ArrayList<String> labels = dfaFrame.getLabels();
			newVR.addLabels(labels);
			newVR.setLabelListVisible(true);
			frames.set(i, newVR);
		}
	}

	/**
	 * Expand closure to span whole NFA.
	 */
	private void expand() {
		Queue<State> q = new LinkedList<State>();
		State[] temp;
		State s;
		// for every state in closure add its own closure to the current one
		for (int i = 0; i < closure.length; i++) {

			q.addAll(closure[i]);
			while (!q.isEmpty()) {

				s = q.poll();
				temp = closure[s.getId()].toArray(new State[] {});
				if (s.isAccepting())
					accpt[i] = true;

				for (int j = 0; j < temp.length; j++)
					if (!closure[i].contains(temp[j])) {
						closure[i].add(temp[j]);
						q.add(temp[j]);
					}
			}
		}
	}

	/**
	 * Constructs the DFA using closures And prepares information about which
	 * states from NFA it used at each state of construction to be visualised.
	 *
	 * @param s
	 *            the current state of DFA that has edges added to it
	 * @param cl
	 *            the epsilon closure of NFA states corresponding to that DFA
	 *            state
	 */
	private void construct(State s, Set<State> cl) {
		State[] c = cl.toArray(new State[] {});
		// get array of NFA states
		VisualRepresentation frameCopy = lastNfaFrame.copy();
		VisualState[] vss = frameCopy.getStates();
		// boolean pass makes sure some operations are only done once for the
		// whole closure
		boolean pass = false;
		boolean recPatEnd = false;
		// go through the alphabet and for each character see if there is an
		// edge coming out of the current state

		for (int i = 0; i < alphabet.length; i++) {
			String newLabel = "";
			VisualEdge[] ves = lastNfaFrame.copy().getEdges();
			char move = (char) alphabet[i];
			Set<State> nSC = new HashSet<State>();
			boolean accepting = false;

			for (int j = 0; j < c.length; j++) {

				// highlight NFA states that belong to the current state closure
				if (!pass) {
					for (int l = 0; l < vss.length; l++) {
						if (vss[l].getId() == c[j].getId()) {
							vss[l].setHL(StateHighlights.YELLOW);
						}
					}
				}
				if (!pass && c[j].isStartOfRecPat() && !loopcl.contains(cl)) {
					s.setStartOfRecPat(true);
					loopcl.add(cl);
					knot.add(s);
				}
				// check if current DFA state must be the one to draw end edge
				// that closes a loop
				if (!pass && !recPatEnd && c[j].isEndOfRecPat())
					recPatEnd = true;
				// check if current character from the alphabet is a valid move
				Edge[] edges = c[j].getEdges().toArray(new Edge[] {});
				for (int k = 0; k < edges.length; k++) {
					if (!edges[k].getInput().isEpsilon() && edges[k].getInput().getChar() == move) {

						// highlight edges algorithm is traversing in NFA
						for (int p = 0; p < ves.length; p++)
							if (ves[p].getFrom() == c[j].getId() && ves[p].getTo() == edges[k].getState().getId()
									&& ves[p].getInput().equals(edges[k].getInput().toString()))
								ves[p].setHighlight(EdgeHighlights.YELLOW);

						// use closures to project edges between NFA states on
						// DFA
						nSC.addAll(closure[edges[k].getState().getId()]);
						if (accpt[edges[k].getState().getId()])
							accepting = true;
					}
				}
			}

			pass = true;
			// if new state closure is empty, current move is impossible from
			// the current state
			if (!nSC.isEmpty()) {

				for (State n : nSC) {
					if (newLabel.length() > 0)
						newLabel += ", ";
					newLabel += (n.getId() + 1);
				}

				addNfaFrames(vss, lastNfaFrame.copy().getEdges());
				String text = "Creating edges coming out of DFA state " + (s.getId() + 1)
						+ ". Closure of corresponding NFA states is highlighted.";

				s.setLabel((s.getId() + 1) + "");

				// create a snapshot of DFA in its current state
				addDfaFrames(s, text);

				// if closures are equal, move creates a self loop
				if (cl.equals(nSC)) {
					Edge e = new Edge(s, new NewChar(move));
					s.addEdge(e);
					// add explanation text to visualize and create new NFA
					// frame accordingly
					text = "Move \"" + move + "\" creates a self loop in state " + (s.getId() + 1);
					addNfaFrames(vss, ves);
					addDfaFrames(s, text, move + "");
				} else {
					// if current state might be the one to close a loop which
					// is not a self-loop with current move
					// check for the possibility
					boolean loopbroken = false;
					if (recPatEnd) {
						// check each closure that started a loop and see if the
						// current one matches
						for (int j = 0; j < loopcl.size(); j++) {
							if (nSC.equals(loopcl.get(j))) {
								Edge ne = new Edge(knot.get(j), new NewChar(move));
								s.addEdge(ne);
								s.setEndOfRecPat(true);
								s.addLoop(ne.getState().getId());
								loopbroken = true;
								text = "Move \"" + move + "\" loops back to state " + (knot.get(j).getId() + 1)
										+ " with closure { " + newLabel + " } from state " + (s.getId() + 1);
								addNfaFrames(vss, ves);

								// create a snapshot of DFA in its current state
								addDfaFrames(s, knot.get(j), text, move + "",
										(knot.get(j).getId() + 1) + ": { " + newLabel + " }");
								loopbroken = true;
							}
						}
					}
					// if there was no loops, create a new state in DFA
					if (!loopbroken) {

						State ns = new State(super.automaton.getNumberOfStates(), accepting,
								(super.automaton.getNumberOfStates() + 1) + "");
						super.automaton.addState(ns);
						Edge ne = new Edge(ns, new NewChar(move));
						s.addEdge(ne);
						text = "Move \"" + move + "\" creates and edge that leads to a new DFA state "
								+ (ns.getId() + 1) + " with closure { " + newLabel + " } from state " + (s.getId() + 1);
						addNfaFrames(vss, ves);
						addDfaFrames(s, ns, text, move + "", (ns.getId() + 1) + ": { " + newLabel + " }");
						construct(ns, nSC);
					}
				}
			}
		}
	}

	/**
	 * Adds the dfa frames.
	 *
	 * @param s
	 *            the current dfa state
	 * @param text
	 *            the to be displayed on the frame
	 */
	@SuppressWarnings("unchecked")
	private void addDfaFrames(State s, State to, String text, String move, String newSideLabel) {
		VisualRepresentation dfaVr = new VisualRepresentation(super.automaton, text, frameTitle);
		dfaVr.getState(s.getId()).setHL(StateHighlights.YELLOW);
		VisualEdge[] vedges = dfaVr.getEdges();
		for (int i = 0; i < vedges.length; i++) {
			if (vedges[i].getFrom() == s.getId() && vedges[i].getTo() == to.getId()
					&& move.equals(vedges[i].getInput())) {
				vedges[i].setHighlight(EdgeHighlights.YELLOW);
				break;
			}
		}
		if (!allLabels.contains(newSideLabel))
			allLabels.add(newSideLabel);
		dfaVr.addLabels((ArrayList<String>) allLabels.clone());
		frames.add(dfaVr);
	}

	/**
	 * Adds the dfa frames.
	 *
	 * @param s
	 *            the current dfa state
	 * @param text
	 *            the to be displayed on the frame
	 */
	@SuppressWarnings("unchecked")
	private void addDfaFrames(State s, String text, String move) {
		VisualRepresentation dfaVr = new VisualRepresentation(super.automaton, text, frameTitle);
		dfaVr.getState(s.getId()).setHL(StateHighlights.YELLOW);
		VisualEdge[] vedges = dfaVr.getEdges();
		for (int i = 0; i < vedges.length; i++) {
			if (vedges[i].getFrom() == s.getId() && vedges[i].getTo() == s.getId()
					&& move.equals(vedges[i].getInput())) {
				vedges[i].setHighlight(EdgeHighlights.YELLOW);
				break;
			}
		}
		dfaVr.addLabels((ArrayList<String>) allLabels.clone());
		frames.add(dfaVr);
	}

	/**
	 * Adds the dfa frames.
	 *
	 * @param s
	 *            the current dfa state
	 * @param text
	 *            the to be displayed on the frame
	 */
	@SuppressWarnings("unchecked")
	private void addDfaFrames(State s, String text) {
		VisualRepresentation dfaVr = new VisualRepresentation(super.automaton, text, frameTitle);
		dfaVr.getState(s.getId()).setHL(StateHighlights.YELLOW);
		dfaVr.addLabels((ArrayList<String>) allLabels.clone());
		frames.add(dfaVr);
	}

	/**
	 * Adds the nfa frames.
	 *
	 * @param vss
	 *            the array of visual states
	 * @param ves
	 *            the array of visual edges
	 */
	private void addNfaFrames(VisualState[] vss, VisualEdge[] ves) {
		ArrayList<VisualState> visSarr = new ArrayList<VisualState>(Arrays.asList(vss));
		ArrayList<VisualEdge> visEarr = new ArrayList<VisualEdge>(Arrays.asList(ves));
		nfaFrames.add(new VisualRepresentation(visSarr, visEarr, "", frameTitle));
	}
}
