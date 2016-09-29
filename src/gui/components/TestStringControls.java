package gui.components;

import gui.store.TestStringDocument;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

/**
 * Offers control of a document for testing strings in automata.
 * 
 * @author Owen Pemberton
 *
 */
public class TestStringControls extends HBox {

	private final TextField txtInput;
	private final Button btnRun;
	private final TestStringDocument document;

	/**
	 * @param d
	 *            The test string document to control.
	 */
	public TestStringControls(TestStringDocument d) {
		super();
		this.document = d;
		this.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		this.setSpacing(10);
		this.setAlignment(Pos.CENTER);
		this.setPadding(new Insets(0.0, 5.0, 5.0, 10.0));

		txtInput = new TextField();
		txtInput.setFont(PlaybackControls.defFont);
		txtInput.setPrefWidth(160);
		txtInput.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent ke) {
				if (ke.getCode().equals(KeyCode.ENTER) && !txtInput.getText().equals("")) {
					document.runString(txtInput.getText());
				}
			}
		});

		btnRun = new Button("Run String");
		btnRun.setStyle(PlaybackControls.BTN_STYLE);
		btnRun.setFont(PlaybackControls.defFont);
		btnRun.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				if (!txtInput.getText().equals("")) {
					document.runString(txtInput.getText());
				}
			}
		});
		this.getChildren().addAll(txtInput, btnRun);
		this.setSpacing(10);
		this.setAlignment(Pos.CENTER);
		this.setPadding(new Insets(0.0, 5.0, 5.0, 10.0));

	}

}
