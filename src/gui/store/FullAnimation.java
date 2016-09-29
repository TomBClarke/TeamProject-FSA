package gui.store;

import gui.canvas.VisualRepresentation;

import java.util.ArrayList;

import automaton.Animation;
import automaton.AnimationDFA;
import automaton.AnimationNFA;

/**
 * This holds each animation in sequence and switches between them.
 * 
 * @author Tom Clarke
 *
 */
public class FullAnimation {

	public final static int NFA = 0;
	public final static int DFA = 1;

	public final static String LBL_NFA = "Generation";
	public final static String LBL_DFA = "Determinising";

	private int maxCurrent = 2;
	private int current;
	private ArrayList<Animation> animations;

	/**
	 * Create a new instance
	 */
	public FullAnimation() {
		current = NFA;
		animations = new ArrayList<Animation>();

		// initialise all of the array needed
		for (int i = 0; i < maxCurrent; i++) {
			animations.add(null);
		}
	}

	/**
	 * Skips to a certain stage of animation (use provides indexes)
	 * 
	 * @param i
	 *            The animation to change to.
	 */
	public void skipTo(int i) {
		if (i < maxCurrent && i > -1)
			current = i;
	}

	/**
	 * Adds an animation.
	 * 
	 * @param a
	 *            The animation to add
	 */
	public void setAnimation(Animation a) {
		if (a instanceof AnimationNFA)
			animations.set(NFA, a);
		else if (a instanceof AnimationDFA)
			animations.set(DFA, a);
	}

	/**
	 * Moves to the next animation
	 * 
	 * @return If there is an animation to move to.
	 */
	protected boolean advance() {
		if (current < animations.size() - 1) {
			current++;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Moves to the previous animation.
	 * 
	 * @return If there is an animation to go back to.
	 */
	protected boolean regress() {
		if (current > 0) {
			current--;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Gets the currently selected animation.
	 * 
	 * @return The animation.
	 */
	public Animation getCurrent() {
		return animations.get(current);
	}

	/**
	 * Gets the current index(of animation).
	 * 
	 * @return The index
	 */
	public int getCurrentIndex() {
		return current;
	}

	/**
	 * Gets the label that describes the currently selected animation.
	 * 
	 * @return The label for the current animation.
	 */
	public String getCurrentLabel() {
		return getLabel(current);
	}

	/**
	 * Gets the label of a given index.
	 * 
	 * @param index
	 *            The index of the label to find.
	 * @return The label.
	 */
	public String getLabel(int index) {
		switch (current + index) {
		case NFA:
			return LBL_NFA;
		case DFA:
			return LBL_DFA;
		default:
			return null;
		}
	}

	/**
	 * Gets the last frame of the last animation.
	 * 
	 * @return The final frame
	 */
	public VisualRepresentation getLastVisualRepresentation() {
		return animations.get(DFA).getFinalDFAwithoutNFA();
	}

}
