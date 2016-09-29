package automaton;

import gui.canvas.EdgeHighlights;
import gui.canvas.StateHighlights;
import gui.canvas.VisualEdge;
import gui.canvas.VisualRepresentation;
import gui.canvas.VisualState;

/**
 * Class containing the frames for testing a word
 * 
 * @author Owen Pemberton
 * @author Piotr Wilczynski
 *
 */
public class AnimationTestString extends Animation {

	public final String frameTitle = "Testing a Word";

	/**
	 * Create a Test String animation
	 * 
	 * @param graph
	 *            The graph to test on
	 * @param testString
	 *            The String to test
	 */
	public AnimationTestString(VisualRepresentation graph, String testString) {
		super();
		String word = testString;
		VisualRepresentation currentGraph = graph.copy();
		this.finalDFAwithoutNFA = currentGraph;
		// Adding the initial frame
		frames.add(currentGraph.copy());
		frames.get(frames.size() - 1).setText("The initial graph");
		frames.get(frames.size() - 1).setTitle(frameTitle);

		int currentIndex = 0;
		int currentEdgeIndex = 0;

		// get initial state
		VisualState s = currentGraph.getState(0);
		// for all edges from state s
		for (currentEdgeIndex = 0; currentEdgeIndex < currentGraph.getEdgesArrayList().size(); currentEdgeIndex++) {
			VisualEdge e = currentGraph.getEdgesArrayList().get(currentEdgeIndex);
			// check if we have reached the end of the string
			if (currentIndex >= word.length()) {
				// if we're at the end of the string and in an accepting state
				// then the string is accepted
				if (s.isAccepting()) {
					s.setHL(StateHighlights.GREEN);
					frames.add(currentGraph.copy());
					frames.get(frames.size() - 1).setText("String " + word
							+ " is accepting. We have reached the end of our string and it is currently at an accepting state");
					frames.get(frames.size() - 1).setProgText(new String[] { word, "", "", "" });
					frames.get(frames.size() - 1).setTitle(frameTitle);
					return;
				} else {
					// if we're not in an accepting state then fail.
					s.setHL(StateHighlights.RED);
					frames.add(currentGraph.copy());
					frames.get(frames.size() - 1).setText("String " + word
							+ " is not accepting. We have reached the end of our string and it is currently at a non-accepting state");
					frames.get(frames.size() - 1).setProgText(new String[] { "", "", word, "" });
					frames.get(frames.size() - 1).setTitle(frameTitle);
					return;
				}
			}

			// if the edge is from the current state AND the input is the first
			// character
			if (e.getFrom() == s.getId()) {
				// add frame to show checking of an edge
				e.setHighlight(EdgeHighlights.YELLOW);
				frames.add(currentGraph.copy());
				frames.get(frames.size() - 1).setText("Checking " + e.getInput() + " from State " + s.getLabel()
						+ " to State " + currentGraph.getState(e.getTo()).getLabel());
				frames.get(frames.size() - 1).setProgText(new String[] { word.substring(0, currentIndex),
						word.substring(currentIndex, currentIndex + 1), "", word.substring(currentIndex + 1) });
				frames.get(frames.size() - 1).setTitle(frameTitle);

				// get the next character to check
				String currentWordPart = "";
				if (word.length() > 1) {
					currentWordPart = word.substring(currentIndex, currentIndex + 1);
				} else {
					currentWordPart = word;
				}

				// check if the edge matches the current character
				if (e.getInput().equals(currentWordPart)) {
					// add frame to show following
					e.setHighlight(EdgeHighlights.GREEN);
					frames.add(currentGraph.copy());
					frames.get(frames.size() - 1).setText("Following " + e.getInput() + " from State " + s.getLabel()
							+ " to State " + currentGraph.getState(e.getTo()).getLabel());
					frames.get(frames.size() - 1).setProgText(new String[] { word.substring(0, currentIndex + 1), "",
							"", word.substring(currentIndex + 1) });
					frames.get(frames.size() - 1).setTitle(frameTitle);
					// follow the edge and reset the loop
					currentEdgeIndex = -1;
					currentIndex++;
					s = currentGraph.getState(e.getTo());
				} else {
					// show that the edge has been checked but not followed
					e.setHighlight(EdgeHighlights.RED);
					frames.add(currentGraph.copy());
					frames.get(frames.size() - 1)
							.setText("Edge " + e.getInput() + " from State " + s.getLabel() + " not followed.");
					frames.get(frames.size() - 1).setProgText(new String[] { word.substring(0, currentIndex), "",
							currentWordPart, word.substring(currentIndex + 1) });
					frames.get(frames.size() - 1).setTitle(frameTitle);
				}
			}
		}
		// we have run out of edges
		s.setHL(StateHighlights.RED);
		frames.add(currentGraph.copy());
		frames.get(frames.size() - 1).setText(
				"Not accepting - There are no more posible edges going out, and we haven't reached the end of the string");
		frames.get(frames.size() - 1).setProgText(new String[] { "", "", word, "" });
		frames.get(frames.size() - 1).setTitle(frameTitle);
	}
}