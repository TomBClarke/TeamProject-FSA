package gui.userdata;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

/**
 * Creates the buttons for loading or saving
 * 
 * @author Tom Clarke
 *
 */
public class LoadSaveButtons extends HBox {

	private Button btnSaveLoad;
	private Button btnCancel;
	private Button btbDelete;

	/**
	 * @param loadSaveScreen
	 *            The main load/save window.
	 * @param type
	 *            Whether it is a load or save operation.
	 */
	public LoadSaveButtons(LoadSaveScreen loadSaveScreen, int type) {
		super(10);

		this.setPadding(new Insets(2.0));

		Font defFont = Font.font("Aerial", 16.0);

		if (type == LoadSaveScreen.LOAD)
			btnSaveLoad = new Button("Load");
		else
			btnSaveLoad = new Button("Save");

		btnSaveLoad.setFont(defFont);
		btnSaveLoad.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (type == LoadSaveScreen.LOAD) {
					loadSaveScreen.load();
				} else {
					loadSaveScreen.addNewRegex();
					loadSaveScreen.close();
				}
			}
		});

		btnCancel = new Button("Cancel");
		btnCancel.setFont(defFont);
		btnCancel.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				loadSaveScreen.close();
			}
		});

		btbDelete = new Button("Delete");
		btbDelete.setFont(defFont);
		btbDelete.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				loadSaveScreen.delete();
			}
		});

		this.getChildren().addAll(btnSaveLoad, btnCancel, btbDelete);
		this.setAlignment(Pos.CENTER);
	}
}
