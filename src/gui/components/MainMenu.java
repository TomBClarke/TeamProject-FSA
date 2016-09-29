package gui.components;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * This class sets up the menu bar and assigns buttons their functions.
 * 
 * @author Owen Pemberton
 *
 */
public class MainMenu extends MenuBar {

	private Menu menuBarFile;

	private MenuItem menuItemNew;
	private MenuItem menuItemOpen;
	private MenuItem menuItemSave;
	private MenuItem menuItemPrint;
	private MenuItem menuItemClose;

	private Menu menuBarAutomata;
	private MenuItem menuItemTestString;

	private Menu menuHelp;
	private MenuItem menuItemShowHelp;
	private MenuItem menuItemShowAbout;
	private MenuItem menuItemToggleColourBlind;

	/**
	 * @param i
	 *            The interface detailing the methods needing to be implemented.
	 */
	public MainMenu(MainMenuInterfacable i) {
		super();
		menuBarFile = new Menu("File");

		menuItemNew = new MenuItem("New");
		menuItemNew.setOnAction((event) -> i.newDocument());

		menuItemOpen = new MenuItem("Open");
		menuItemOpen.setOnAction((event) -> i.openDocument());

		menuItemSave = new MenuItem("Save");
		menuItemSave.setOnAction((event) -> i.saveDocument());

		menuItemPrint = new MenuItem("Print");
		menuItemPrint.setOnAction((event) -> i.printDocument());

		menuItemClose = new MenuItem("Close");
		menuItemClose.setOnAction((event) -> i.closeDocument());

		menuBarFile.getItems().addAll(menuItemNew, menuItemSave, menuItemOpen, menuItemPrint, menuItemClose);

		menuBarAutomata = new Menu("Automata");

		menuItemTestString = new MenuItem("Test a Word");
		menuItemTestString.setOnAction((event) -> i.openTestString());

		menuBarAutomata.getItems().addAll(menuItemTestString);

		menuHelp = new Menu("View Help");
		menuItemToggleColourBlind = new MenuItem("Toggle Colour Blind");
		menuItemToggleColourBlind.setOnAction((event) -> i.toggleColourBlindMode());

		menuItemShowHelp = new MenuItem("Show Help");
		menuItemShowHelp.setOnAction((event) -> i.showHelp());
		
		menuItemShowAbout = new MenuItem("About");
		menuItemShowAbout.setOnAction((event) -> i.showAbout());

		menuHelp.getItems().addAll(menuItemToggleColourBlind, menuItemShowHelp, menuItemShowAbout);

		this.getMenus().addAll(menuBarFile, menuBarAutomata, menuHelp);
	}
}
