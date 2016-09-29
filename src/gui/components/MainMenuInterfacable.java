package gui.components;

import gui.store.Document;

/**
 * An interface for all the required buttons on the menu bar.
 * 
 * @author Owen Pemberton
 *
 */
public interface MainMenuInterfacable {

	/**
	 * Opens a new document.
	 * 
	 * @return The document generated which is associated with the new tab.
	 */
	public Document newDocument();

	/**
	 * Allows the user to load a document.
	 */
	public void openDocument();

	/**
	 * Saves the current automata.
	 */
	public void saveDocument();

	/**
	 * Prints the current automata.
	 */
	public void printDocument();

	/**
	 * Closes the tab.
	 */
	public void closeDocument();

	/**
	 * Shows the help document.
	 */
	public void showHelp();

	/**
	 * Shows the about document.
	 */
	public void showAbout();
	
	/**
	 * Opens a test string document based off of the currently selected
	 * document.
	 */
	public void openTestString();

	/**
	 * Toggles whether to display colour blind safe colours to the user.
	 */
	public void toggleColourBlindMode();

}
