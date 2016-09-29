package gui.store;

import gui.canvas.VisualRepresentation;

import java.util.ArrayList;

import automaton.Animation;
import javafx.scene.control.Tab;

/**
 * A document holds a visualisation we want to show on a canvas.
 * 
 * @author Tom Clarke and Owen Pemberton
 *
 */
public interface Document {

	/**
	 * Makes the canvas render the desired content.
	 */
	public void drawDocument();

	/**
	 * Prints the canvas.
	 */
	public void printDocument();

	/**
	 * Tried to advance to the next animation.
	 */
	public void advanceAnimation();

	/**
	 * Tried to regress to the previous animation.
	 */
	public void regressAnimation();

	/**
	 * Gets the final graph as a VisualRepresentation
	 * 
	 * @return
	 */
	public VisualRepresentation getFinalVisualRepresentation();

	/**
	 * Shows the current speed in a nice way on screen.
	 * 
	 * @param speed
	 *            The string to show
	 */
	public void showSpeed(String string);

	/**
	 * Gets the label at the given index
	 * 
	 * @param index
	 *            The index of the label
	 * @return The label.
	 */
	public String getLabel(int index);

	/**
	 * Gets the current regex.
	 * 
	 * @return The current regex.
	 */
	public String getCurrentRegex();

	/**
	 * Tests to see if the animation is generated.
	 * 
	 * @return If it has been generated.
	 */
	public boolean isGenerated();

	/**
	 * Changes the current frame of animation to a desired index.
	 * 
	 * @param x
	 *            The new index.
	 */
	public void setCurrentFrameIndex(int i);

	/**
	 * Finds the current frame of animation.
	 * 
	 * @return The current frame of animation.
	 */
	public int getCurrentFrameIndex();

	/**
	 * Get the number of frames
	 * 
	 * @return The number of frames
	 */
	public int getFrameCount();

	/**
	 * Gets the currently tab connected to the document.
	 * 
	 * @return The tab holding the document
	 */
	public Tab getTab();

	/**
	 * Returns if the document holds a regex
	 * 
	 * @return If the docucment holds a regex.
	 */
	public boolean isRegexDocument();

	/**
	 * Gets the currently playing animation.
	 * 
	 * @return The current animation
	 */
	public Animation getAnimation();

	/**
	 * Sets whether colour blind mode is on or off.
	 * 
	 * @param colourBlindMode
	 *            The new colour blind mode status
	 */
	public void setColourBlindMode(boolean colourBlindMode);

	/**
	 * Gets the current list of labels.
	 * 
	 * @return The list of labels.
	 */
	public ArrayList<String> getCurrentListArray();
}
