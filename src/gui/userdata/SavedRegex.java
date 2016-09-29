package gui.userdata;

import javafx.beans.property.SimpleStringProperty;

/**
 * Holds a saved regex.
 * 
 * @author Tom Clarke
 *
 */
public class SavedRegex {

	private final SimpleStringProperty regex;

	/**
	 * @param regex
	 *            The regular expression
	 */
	public SavedRegex(String regex) {
		this.regex = new SimpleStringProperty(regex);
	}

	/**
	 * Gets the regex
	 * 
	 * @return The regex
	 */
	public String getRegex() {
		return regex.get();
	}
}
