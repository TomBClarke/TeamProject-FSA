package gui.userdata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import gui.store.RegexDocument;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Scene;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Creates and holds the stage for loading or saving.
 * 
 * @author Tom Clarke
 *
 */
public class LoadSaveScreen {

	protected final static int LOAD = 0;
	protected final static int SAVE = 1;
	private final static String FILE_NAME = "user_data.c5";
	private final static String EMPTY_LINE = "(New save slot)";

	private Stage stage;
	private ObservableList<SavedRegex> data = FXCollections.observableArrayList();
	private SavedDocuments listDoc;

	// For saving:
	private String regex;

	// For loading:
	private RegexDocument regexDocument;

	/**
	 * This is for LOADING regular expressions.
	 * 
	 * @param regexDocument
	 *            The document we push the new regular expression to.
	 */
	public LoadSaveScreen(RegexDocument regexDocument) {
		this.regexDocument = regexDocument;
		setupData();
		makeMainScreen("Load a regular expression", LOAD);
	}

	/**
	 * This is for SAVING regular expressions
	 * 
	 * @param regex
	 *            The regular expression
	 */
	public LoadSaveScreen(String regex) {
		this.regex = regex;
		setupData();
		data.add(new SavedRegex(EMPTY_LINE));
		makeMainScreen("Save: " + regex, SAVE);
	}

	/**
	 * Creates the main screen
	 * 
	 * @param title
	 *            The title of the window.
	 * @param type
	 *            Whether it will be used for load/save
	 */
	private void makeMainScreen(String title, int type) {
		BorderPane root = new BorderPane();

		listDoc = new SavedDocuments(data);
		root.setCenter(listDoc);
		root.setBottom(new LoadSaveButtons(this, type));

		float dim = 400;
		Scene scene = new Scene(root, dim, dim);
		stage = new Stage();
		stage.getIcons().add(new Image(getClass().getResource("/fsa_small_simple.png").toExternalForm()));
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.setMinWidth(dim);
		stage.setMinHeight(dim);
		stage.setMaxHeight(dim);
		stage.setMaxWidth(dim);
		stage.setTitle(title);
		stage.setScene(scene);

		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				save();
			}
		});

		stage.show();
	}

	/**
	 * Closes the window
	 */
	protected void close() {
		save();
		stage.close();
	}

	/**
	 * Set up the data
	 */
	private void setupData() {
		try {
			createTableData();
		} catch (FileNotFoundException e) {
			try {
				PrintWriter writer;
				writer = new PrintWriter(FILE_NAME, "UTF-8");
				writer.close();
				createTableData();
			} catch (Exception e1) {
				fail("Could not create file.");
				close();
			}
		} catch (Exception e) {
			fail("Could not read file.");
			close();
		}
	}

	/**
	 * Reads in the table and creates the list of regular expressions.
	 * 
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	private void createTableData() throws FileNotFoundException, Exception {
		BufferedReader br = new BufferedReader(new FileReader(FILE_NAME));
		String line;
		while ((line = br.readLine()) != null) {
			if (!line.equals(""))
				data.add(new SavedRegex(line));
		}
		br.close();
	}

	/**
	 * Shows an error message and then closes the screen.
	 * 
	 * @param errorMsg
	 *            The error message.
	 */
	private void fail(String errorMsg) {
		this.showError(errorMsg);
	}

	/**
	 * Shows an error message
	 * 
	 * @param errorMsg
	 *            The message to display.
	 */
	public void showError(String errorMsg) {
		/*Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("An issue has occured");
		alert.setHeaderText(null);
		alert.setContentText(errorMsg);
		alert.showAndWait();*/
	}

	/**
	 * Inserts the regular expression to save into the selected index.
	 */
	public void addNewRegex() {
		int indexToSaveAt = listDoc.getSelectionModel().getFocusedIndex();

		if (indexToSaveAt == -1)
			indexToSaveAt = data.size() - 1;

		data.set(indexToSaveAt, new SavedRegex(regex));
	}

	/**
	 * Saves the current list to disk.
	 */
	public void save() {
		int indexToSaveAt = listDoc.getSelectionModel().getFocusedIndex();

		if (indexToSaveAt == -1)
			indexToSaveAt = data.size() - 1;

		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(FILE_NAME));
			for (int i = 0; i < data.size(); i++) {
				if (i == data.size() - 1 && data.get(i).getRegex().equals(EMPTY_LINE)) {
					break;
				} else {
					if (i != 0)
						bw.write("\n");
					bw.write(data.get(i).getRegex());
				}
			}
			bw.close();
		} catch (IOException e) {
			fail("Could not save.");
			close();
		}
	}

	/**
	 * Loads the selected regex and attempts generation.
	 */
	public void load() {
		int indexToLoad = listDoc.getSelectionModel().getFocusedIndex();
		if (indexToLoad > -1) {
			String regex = data.get(indexToLoad).getRegex();
			regexDocument.setRegexInput(regex);
			regexDocument.attemptGenerate(regex);
			close();
		} else {
			fail("Nothing selected to load.");
		}
	}

	/**
	 * Deletes the item in the currently selected index.
	 */
	public void delete() {
		int indexToDelete = listDoc.getSelectionModel().getFocusedIndex();
		if (indexToDelete > -1) {
			if (!data.get(indexToDelete).getRegex().equals(EMPTY_LINE)) {
				data.remove(indexToDelete, indexToDelete + 1);
				listDoc.updateList(data);
			}
		} else {
			fail("Nothing selected to delete.");
		}
	}
}
