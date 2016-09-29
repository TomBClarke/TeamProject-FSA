package gui.components;

import gui.store.RegexDocument;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;

/**
 * Offers control for generating regular expressions.
 * 
 * @author Tom Clarke
 * @author Owen Pemberton
 *
 */
public class RegexControls extends HBox {

	private final TextField txtRegexInput;
	private final Button btnGenerate;
	private final RegexDocument document;

	/**
	 * @param document
	 *            The regex document to control
	 */
	public RegexControls(RegexDocument document) {
		super();
		this.document = document;
		txtRegexInput = new TextField();
		txtRegexInput.setFont(PlaybackControls.defFont);
		txtRegexInput.setPrefWidth(160);
		txtRegexInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				if (ke.getCode().equals(KeyCode.ENTER)) {
					generate();
				}
			}
		});

		btnGenerate = new Button("Generate");
		btnGenerate.setStyle(PlaybackControls.BTN_STYLE);
		btnGenerate.setFont(PlaybackControls.defFont);
		btnGenerate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				document.attemptGenerate(txtRegexInput.getText());
			}
		});

		this.getChildren().addAll(txtRegexInput, btnGenerate);
		this.setSpacing(10);
		this.setAlignment(Pos.CENTER);
		this.setPadding(new Insets(0.0, 5.0, 5.0, 10.0));
	}

	/**
	 * Submit the user string for generation.
	 */
	private void generate() {
		document.attemptGenerate(txtRegexInput.getText());
	}

	/**
	 * Sets the text input for the tab.
	 * 
	 * @param regex
	 *            The regex to display.
	 */
	public void setRegexInput(String regex) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtRegexInput.setText(regex);
			}
		});
	}
}
