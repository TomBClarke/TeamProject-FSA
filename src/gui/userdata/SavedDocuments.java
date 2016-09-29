package gui.userdata;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Holds the table for the load/save stage.
 * 
 * @author Tom Clarke
 *
 */
public class SavedDocuments extends TableView<SavedRegex> {

	private final TableColumn<SavedRegex, String> mainCol = new TableColumn<SavedRegex, String>("Regular expressions");
	private ObservableList<SavedRegex> data;

	/**
	 * @param data
	 *            The data for displaying in the table
	 */
	public SavedDocuments(ObservableList<SavedRegex> data) {
		double width = 380;
		mainCol.setMinWidth(width);
		mainCol.setPrefWidth(width);
		mainCol.setMaxWidth(width);
		mainCol.setCellValueFactory(new PropertyValueFactory<SavedRegex, String>("regex"));

		this.data = data;
		this.setItems(this.data);
		this.getColumns().add(mainCol);
		this.getSelectionModel().select(null);
	}

	/**
	 * Updates the list of saved regular expressions to the user.
	 * 
	 * @param data
	 *            The list of saved strings
	 */
	public void updateList(ObservableList<SavedRegex> data) {
		this.data = data;
		this.setItems(this.data);
	}
}
