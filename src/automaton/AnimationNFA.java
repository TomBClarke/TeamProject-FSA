package automaton;

import automaton.thompsons.BlockType;
import automaton.thompsons.ConBlock;
import gui.canvas.EdgeHighlights;
import gui.canvas.StateHighlights;
import gui.canvas.VisualEdge;
import gui.canvas.VisualRepresentation;
import gui.canvas.VisualState;

/**
 * Class building an automaton and containing particular snapshots of it
 * 
 * @author Botond Megyesfalvi
 * @author Danyil Ilchenko
 * 
 */
public class AnimationNFA extends Animation {

	public final String frameTitle = "NFA";
	private int recPatS;

	/**
	 * Create a new instance
	 * 
	 * @param c
	 *            The tree-like representation of a regular expression
	 * 
	 */
	public AnimationNFA(ConBlock c) {
		super();
		finalDFAwithoutNFA = null;
		recPatS = 0;

		State start = new State(0, false, "1");
		State end = new State(1, true, "2");
		start.setX(0);
		end.setX(c.getXs() + 1);
		int i = c.getYs();
		start.setYLimit(0);
		start.setY(i);
		end.setY(i);
		end.setYLimit(0);

		super.automaton = new Automaton();
		super.automaton.addState(start);
		super.automaton.addState(end);

		Edge firstEdge = new Edge(end, c.toString());
		start.addEdge(firstEdge);

		frames.add(new VisualRepresentation(automaton, "Creates starting and Accepting states.", frameTitle));

		VisualRepresentation v = new VisualRepresentation(automaton,
				"The highlighted edge is going to be expanded next.", frameTitle);
		v.getEdgesArrayList().get(v.getEdgesArrayList().size() - 1).setHighlight(EdgeHighlights.YELLOW);
		;
		frames.add(v);
		start.removeEdge(firstEdge);
		buildUp(start, end, c);

		frames.add(new VisualRepresentation(automaton, "The NFA is completely built up. Click next to Deteminise",
				frameTitle));

		// No need to propagate coordinates because we never remove states in
		// NFA
	}

	/**
	 * Build the automaton between 2 given states. This will expand the regular
	 * expression from the
	 * 
	 * @param start
	 *            The starting state
	 * @param end
	 *            The ending state
	 * @param c
	 *            The ConBlock
	 */
	private void buildUp(State start, State end, ConBlock c) {
		String text;
		VisualRepresentation v;
		switch (c.getType()) {
		case ELEM:

			// Creates an Edge of that element between the starting and ending
			// states.
			start.addEdge(new Edge(end, new NewChar(c.getElement())));

			// Add the character to the alphabet of the automaton.
			automaton.addChar(c.getElement());

			// Creates the visual representation of this and adds it to .the
			// other frames
			text = "It's a(n) '" + c.getElement() + "' Move, it is already expanded.";
			v = new VisualRepresentation(automaton, text, frameTitle);
			// Highlights the new edge
			for (VisualEdge t : v.getEdgesArrayList()) {
				if (t.getFrom() == start.getId() && t.getTo() == end.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}
			}
			frames.add(v);
			break;

		case EMPTY:

			// Creates an Edge of epsilon move between the starting and ending
			// states.
			start.addEdge(new Edge(end, new NewChar()));

			// Create the visual representation of this and adds it to .the
			// other frames
			text = "Evaluates the epmty regular expression, adds an epsilon move";
			v = new VisualRepresentation(automaton, text, frameTitle);
			// Highlights the new edge
			for (VisualEdge t : v.getEdgesArrayList()) {
				if (t.getFrom() == start.getId() && t.getTo() == end.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}
			}
			frames.add(v);

			break;

		case DISJ:

			if (recPatS > 0)
				recPatS++;
			// If it is a disjunction, we have 2 branches. Both containing 2
			// states, which are connected by an edge that represents the
			// conblock that needs to be expanded at that particular branch.
			Edge edgeToEnding = new Edge(end, new NewChar());

			// Create the 2 States at First Branch
			State stateA1 = new State(super.automaton.getNumberOfStates(), false,
					String.valueOf(super.automaton.getNumberOfStates() + 1));
			super.automaton.addState(stateA1);
			stateA1.setX(start.getX() + 1);
			State stateA2 = new State(super.automaton.getNumberOfStates(), false,
					String.valueOf(super.automaton.getNumberOfStates() + 1));
			super.automaton.addState(stateA2);
			int insideXs = Math.max(c.getInsides()[0].getXs(), c.getInsides()[1].getXs());
			stateA2.setX(start.getX() + 1 + insideXs + 1);
			int i = c.getInsides()[0].getYs();
			stateA1.setY(i + start.getYLimit());
			stateA2.setY(i + start.getYLimit());
			stateA1.setYLimit(start.getYLimit());
			stateA2.setYLimit(start.getYLimit());

			// From our starting state there is a possible epsilon move to
			// StateA1
			start.addEdge(new Edge(stateA1, new NewChar()));
			// From our StateA2 there is a possible epsilon move to ending
			stateA2.addEdge(edgeToEnding);

			// The move between the 2 states, it stores the string
			// representation of the conblock that will be expanded on that
			// branch
			Edge edgeBetweenA = new Edge(stateA2, c.getInsides()[0].toString());
			stateA1.addEdge(edgeBetweenA);

			// Same thing for the Second Branch
			State stateB1 = new State(super.automaton.getNumberOfStates(), false,
					String.valueOf(super.automaton.getNumberOfStates() + 1));
			super.automaton.addState(stateB1);
			stateB1.setX(start.getX() + 1);
			State stateB2 = new State(super.automaton.getNumberOfStates(), false,
					String.valueOf(super.automaton.getNumberOfStates() + 1));
			super.automaton.addState(stateB2);
			stateB2.setX(start.getX() + 1 + insideXs + 1);
			stateB1.setY(start.getY() + (c.getInsides()[1]).getYs() + 1);
			stateB2.setY(start.getY() + (c.getInsides()[1]).getYs() + 1);
			stateB1.setYLimit(start.getY() + 1);
			stateB2.setYLimit(start.getY() + 1);

			start.addEdge(new Edge(stateB1, new NewChar()));
			stateB2.addEdge(edgeToEnding);

			Edge edgeBetweenB = new Edge(stateB2, c.getInsides()[1].toString());
			stateB1.addEdge(edgeBetweenB);

			// Create a visual representation of the current automaton.
			text = "Expands the if statement, adds 4 extra states, and splits the regular expression into two smaller ones between states "
					+ (stateA1.getId() + 1) + "-" + (stateA2.getId() + 1) + " and " + (stateB1.getId() + 1) + "-"
					+ (stateB2.getId() + 1);
			v = new VisualRepresentation(automaton, text, frameTitle);

			// Highlight the recently added edges
			for (VisualEdge t : v.getEdgesArrayList()) {
				if (t.getFrom() == start.getId() && t.getTo() == stateA1.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}
				if (t.getFrom() == start.getId() && t.getTo() == stateB1.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}
				if (t.getFrom() == stateA2.getId() && t.getTo() == end.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}
				if (t.getFrom() == stateB2.getId() && t.getTo() == end.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}
				if (t.getFrom() == stateA1.getId() && t.getTo() == stateA2.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}
				if (t.getFrom() == stateB1.getId() && t.getTo() == stateB2.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}
			}

			// Highlight the recently added states
			for (VisualState t : v.getStates()) {
				if (t.getId() == stateA1.getId()) {
					t.setHL(StateHighlights.YELLOW);
				}
				if (t.getId() == stateA2.getId()) {
					t.setHL(StateHighlights.YELLOW);
				}
				if (t.getId() == stateB1.getId()) {
					t.setHL(StateHighlights.YELLOW);
				}
				if (t.getId() == stateB2.getId()) {
					t.setHL(StateHighlights.YELLOW);
				}

			}

			// Add it to the frames.
			frames.add(v);

			if (recPatS > 0 && c.getInsides()[0].getType() == BlockType.ELEM) {
				stateA2.setStartOfRecPat(true);
				recPatS--;
			}

			// Create a new visual representation, where the edge that is going
			// to be expanded will be highlighted. Add it to the frames.
			v = new VisualRepresentation(automaton, "The highlighted edge is going to be expanded next.", frameTitle);
			for (VisualEdge t : v.getEdgesArrayList()) {
				if (t.getFrom() == stateA1.getId() && t.getTo() == stateA2.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}
			}
			frames.add(v);

			// We start expanding the first branch, we need to delete the edge,
			// and we expand the conblock on that branch, between the 2 states.
			stateA1.removeEdge(edgeBetweenA);
			buildUp(stateA1, stateA2, c.getInsides()[0]);

			// Show automaton here
			// super.addFrame(new VisualRepresentation(a));

			if (recPatS > 0 && c.getInsides()[1].getType() == BlockType.ELEM) {
				stateB2.setStartOfRecPat(true);
				recPatS--;
			}

			// Create a new visual representation, where the edge that is going
			// to be expanded will be highlighted. Add it to the frames.
			v = new VisualRepresentation(automaton, "The highlighted edge is going to be expanded next.", frameTitle);
			for (VisualEdge t : v.getEdgesArrayList()) {
				if (t.getFrom() == stateB1.getId() && t.getTo() == stateB2.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}
			}
			frames.add(v);

			// We do the same with the second branch.
			stateB1.removeEdge(edgeBetweenB);
			buildUp(stateB1, stateB2, c.getInsides()[1]);

			break;
		case STAR:
			// We create 2 states
			State state1 = new State(super.automaton.getNumberOfStates(), false,
					String.valueOf(super.automaton.getNumberOfStates() + 1));
			super.automaton.addState(state1);
			State state2 = new State(super.automaton.getNumberOfStates(), false,
					String.valueOf(super.automaton.getNumberOfStates() + 1));
			super.automaton.addState(state2);
			state1.setX(start.getX() + 1);
			state2.setX(start.getX() + 1 + (c.getInsides()[0]).getXs() + 1);
			state1.setY(start.getY());
			state2.setY(start.getY());
			state1.setYLimit(start.getYLimit());
			state2.setYLimit(start.getYLimit());

			// There is an epsilon move between the starting state and ending
			// (because * allows 0), and state2 and ending as well.
			Edge edge = new Edge(end, new NewChar());
			// start.addEdge(edge);
			state2.addEdge(edge);

			// From the starting state there is an epsilon move to state 1
			Edge edgeToState1 = new Edge(state1, new NewChar());
			start.addEdge(edgeToState1);

			// There is a move between State 1 and State 2. It stores the string
			// representation of the conblock that will be expanded.
			Edge edgeToState2 = new Edge(state2, c.getInsides()[0].toString());
			Edge emptyToState2 = new Edge(state2, new NewChar());
			state1.addEdge(edgeToState2);
			start.addEdge(emptyToState2);

			// From state 2 we can go back to state 1. This makes the structure
			// recursive.
			Edge edgeToStart = new Edge(start, new NewChar());
			state2.addEdge(edgeToStart);

			// Create a visual representation of the current automaton.
			text = "Expands the Kleene star statement, adds 2 extra states and creates the loop by adding epsilon moves. The edge between "
					+ (state1.getId() + 1) + "-" + (state2.getId() + 1)
					+ " contains the remaining regular expression, which can be completely skipped or executed multiple times";

			v = new VisualRepresentation(automaton, text, frameTitle);

			// Highlight the recently added edges
			for (VisualEdge t : v.getEdgesArrayList()) {
				if (t.getFrom() == start.getId() && t.getTo() == state1.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}
				if (t.getFrom() == start.getId() && t.getTo() == state2.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}
				if (t.getFrom() == state2.getId() && t.getTo() == start.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}
				if (t.getFrom() == state2.getId() && t.getTo() == end.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}
				if (t.getFrom() == state1.getId() && t.getTo() == state2.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}

			}
			// Highlight the recently added states
			for (VisualState t : v.getStates()) {
				if (t.getId() == state1.getId()) {
					t.setHL(StateHighlights.YELLOW);
				}
				if (t.getId() == state2.getId()) {
					t.setHL(StateHighlights.YELLOW);
				}
			}
			// Add it to the frames.
			frames.add(v);

			if (c.getInsides()[0].getType() != BlockType.ELEM) {
				state2.setEndOfRecPat(true);
				recPatS++;
			}

			// Create a new visual representation, where the edge that is going
			// to be expanded will be highlighted. Add it to the frames.
			v = new VisualRepresentation(automaton, "The highlighted edge is going to be expanded next.", frameTitle);
			for (VisualEdge t : v.getEdgesArrayList()) {
				if (t.getFrom() == state1.getId() && t.getTo() == state2.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}
			}
			frames.add(v);

			// Start expanding the conblock inside the Star, we need to delete
			// the edge, and build up that conblock between the given two
			// states.
			state1.removeEdge(edgeToState2);
			buildUp(state1, state2, c.getInsides()[0]);

			break;

		case CONCAT:

			// Create one extra state
			state1 = new State(super.automaton.getNumberOfStates(), false,
					String.valueOf(super.automaton.getNumberOfStates() + 1));
			super.automaton.addState(state1);

			// Adds an edge between the start and the newly created state, that
			// represents the first part of the concat.
			edge = new Edge(state1, c.getInsides()[0].toString());
			start.addEdge(edge);
			// Adds an edge between the newly created state and the end, that
			// represents the second part of the concat.
			edgeToEnding = new Edge(end, c.getInsides()[1].toString());
			state1.addEdge(edgeToEnding);

			// Calculates the coordinates of the newly added state
			state1.setX(start.getX() + 1 + c.getInsides()[0].getXs());
			state1.setY(start.getY());
			state1.setYLimit(start.getYLimit());

			// Create a visual representation of the current automaton/
			text = "Expands the concatenation of two statements, adds 1 extra state and splits the regular expression into 2 smaller ones between the states "
					+ (start.getId() + 1) + "-" + (state1.getId() + 1) + " and " + (state1.getId() + 1) + "-"
					+ (end.getId() + 1);
			v = new VisualRepresentation(automaton, text, frameTitle);

			// Highlight the recently added edges
			for (VisualEdge t : v.getEdgesArrayList()) {
				if (t.getFrom() == start.getId() && t.getTo() == state1.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}
				if (t.getFrom() == state1.getId() && t.getTo() == end.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}
			}

			// Highlight the recently added states
			for (VisualState t : v.getStates()) {
				if (t.getId() == state1.getId()) {
					t.setHL(StateHighlights.YELLOW);
				}
			}

			// Add it to the frames
			frames.add(v);

			if (recPatS > 0 && c.getInsides()[0].getType() == BlockType.ELEM) {
				state1.setStartOfRecPat(true);
				recPatS--;
			}

			// Create a new visual representation, where the edge that is going
			// to be expanded will be highlighted. Add it to the frames.
			v = new VisualRepresentation(automaton, "The highlighted edge is going to be expanded next.", frameTitle);
			for (VisualEdge t : v.getEdgesArrayList()) {
				if (t.getFrom() == start.getId() && t.getTo() == state1.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}
			}
			frames.add(v);

			// We start expanding the first conblock from the concatenation, we
			// need to delete the edge, and build up that conblock between the
			// given two states.
			start.removeEdge(edge);
			buildUp(start, state1, c.getInsides()[0]);

			// Create a new visual representation, where the edge that is going
			// to be expanded will be highlighted. Add it to the frames.
			v = new VisualRepresentation(automaton, "The highlighted edge is going to be expanded next.", frameTitle);
			for (VisualEdge t : v.getEdgesArrayList()) {
				if (t.getFrom() == state1.getId() && t.getTo() == end.getId()) {
					t.setHighlight(EdgeHighlights.YELLOW);
				}
			}
			frames.add(v);

			// We start expanding the second conblock from the concatenation, we
			// need to delete the edge, and build up that conblock between the
			// given two states.
			state1.removeEdge(edgeToEnding);
			buildUp(state1, end, c.getInsides()[1]);

			break;

		default:
			break;
		}
	}

}