package gui.store;

import java.util.ArrayList;

import gui.components.MainMenuInterfacable;
import gui.userdata.LoadSaveScreen;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

/**
 * This handles the functions of the buttons in the menu bar.
 * 
 * @author Tom Clarke
 * @author Owen Pemberton
 *
 */
public class DocumentTabPane extends BorderPane implements MainMenuInterfacable {

	private TabPane tabPane;
	private ArrayList<Document> documents;

	private boolean colourBlindMode = false;

	public DocumentTabPane() {
		super();
		documents = new ArrayList<Document>();
		tabPane = new TabPane();
		this.setCenter(tabPane);
		newDocument();
	}

	@Override
	public void toggleColourBlindMode() {
		colourBlindMode = !colourBlindMode;
		for (Document d : documents) {
			d.setColourBlindMode(colourBlindMode);
		}
	}

	@Override
	public RegexDocument newDocument() {
		Tab newTab = new Tab("New Tab");

		RegexDocument newDocument = new RegexDocument(newTab, colourBlindMode);
		newTab.setContent(newDocument);

		documents.add(newDocument);
		tabPane.getTabs().add(newTab);
		tabPane.getSelectionModel().selectLast();

		newTab.setOnClosed(new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				documents.remove(newDocument);
			}
		});
		return newDocument;
	}

	@Override
	public void openDocument() {
		new LoadSaveScreen(newDocument());
	}

	@Override
	public void saveDocument() {
		Tab tabToRemove = getCurrentTab();
		if (tabToRemove != null) {
			Document currentDocument = getCurrentDocument();
			if (currentDocument instanceof RegexDocument) {
				String regex = currentDocument.getCurrentRegex();
				if (regex != null && !regex.equals("")) {
					new LoadSaveScreen(regex);
				} else {
					showError("No regex entered");
				}
			}
		} else {
			showError("No document selected.");
		}
	}

	@Override
	public void printDocument() {
		if (getCurrentTab() != null) {
			this.getCurrentDocument().printDocument();
		} else {
			showError("No document selected.");
		}
	}

	@Override
	public void closeDocument() {
		if (getCurrentTab() != null) {
			int index = tabPane.getSelectionModel().getSelectedIndex();
			documents.remove(index);
			tabPane.getTabs().remove(index);
		} else {
			System.exit(0);
		}
	}

	/**
	 * Gets the currently selected tab.
	 * 
	 * @return The current tab
	 */
	private Tab getCurrentTab() {
		if (tabPane.getSelectionModel().getSelectedIndex() > -1)
			return tabPane.getSelectionModel().getSelectedItem();
		else
			return null;
	}

	/**
	 * Gets the current document
	 * 
	 * @return The document
	 */
	private Document getCurrentDocument() {
		if (tabPane.getSelectionModel().getSelectedIndex() > -1) {
			if (documents.size() < 1)
				return null;
			else
				return documents.get(tabPane.getSelectionModel().getSelectedIndex());
		} else
			return null;
	}

	/**
	 * Shows an error message
	 * 
	 * @param errorMsg
	 *            The message to display.
	 */
	private void showError(String errorMsg) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("An issue has occured");
		alert.setHeaderText(null);
		alert.setContentText(errorMsg);

		alert.showAndWait();
	}

	@Override
	public void showHelp() {
		// check for an open help tab and select it if it exists
		for (Document d : documents) {
			if (d instanceof HelpDocument) {
				tabPane.getSelectionModel().select(d.getTab());
				return;
			}
		}
		Tab newTab = new Tab("Help");

		HelpDocument newDocument = new HelpDocument(newTab);
		newTab.setContent(newDocument);

		documents.add(newDocument);
		tabPane.getTabs().add(newTab);
		tabPane.getSelectionModel().selectLast();

		newTab.setOnClosed(new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				documents.remove(newDocument);
			}
		});
	}

	@Override
	public void showAbout() {
		// check for an open help tab and select it if it exists
		for (Document d : documents) {
			if (d instanceof AboutDocument) {
				tabPane.getSelectionModel().select(d.getTab());
				return;
			}
		}

		Tab newTab = new Tab("About");

		AboutDocument newDocument = new AboutDocument(newTab);
		newTab.setContent(newDocument);

		documents.add(newDocument);
		tabPane.getTabs().add(newTab);
		tabPane.getSelectionModel().selectLast();

		newTab.setOnClosed(new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				documents.remove(newDocument);
			}
		});
	}

	/**
	 * Gets the selected document, based on the selected tab.
	 * 
	 * @return The currently selected document
	 */
	private Document getSelectedDocument() {
		Tab selectedTab = getCurrentTab();
		for (Document d : documents) {
			if (d.getTab().equals(selectedTab)) {
				return d;
			}
		}
		return null;
	}

	@Override
	public void openTestString() {
		// check if the currently selected document is a regexdoc
		Document d = getSelectedDocument();
		if (d != null && d.isRegexDocument() && d.isGenerated()) {
			Tab newTab = new Tab("Test String - " + d.getCurrentRegex());

			TestStringDocument newDocument = new TestStringDocument(newTab, d.getFinalVisualRepresentation().copy(),
					colourBlindMode);
			newTab.setContent(newDocument);

			documents.add(newDocument);
			tabPane.getTabs().add(newTab);
			tabPane.getSelectionModel().selectLast();

			newTab.setOnClosed(new EventHandler<Event>() {
				@Override
				public void handle(Event e) {
					documents.remove(newDocument);
					tabPane.getTabs().remove(newTab);
				}
			});
		} else {
			showError("No regex entered");
		}
	}
}
