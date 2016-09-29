package gui.components;

import gui.canvas.AutomataCanvas;
import gui.store.Document;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * This class holds the main canvas for the visualisation to be drawn upon and
 * methods for doing so.
 * 
 * @author Tom Clarke
 * @author Owen Pemberton
 *
 */
public abstract class CanvasBox extends StackPane {

	private final Font labelFont = Font.font("Aerial", 20.0);
	private final Color labelFill = Color.WHITE;
	private final Insets labelPad = new Insets(20);
	private final String BTN_STYLE = "-fx-base: #4169E1;";
	private final Font defFont = Font.font("Aerial", 16.0);

	private final Document document;
	private AutomataCanvas canvasPane;
	private final Label speedLabel;
	private final Label generalLabel;
	private final Button previous;
	private final Button next;

	private final boolean hasProgressionButtons;

	/**
	 * @param document
	 *            The automata model
	 * @param hasProgressionButtons
	 *            Has progress buttons or not
	 */
	public CanvasBox(Document document, boolean hasProgressionButtons) {
		super();
		this.document = document;
		this.hasProgressionButtons = hasProgressionButtons;

		this.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

		this.setMinHeight(0.0);
		this.setMinWidth(0.0);

		speedLabel = new Label();
		speedLabel.setFont(labelFont);
		speedLabel.setTextFill(labelFill);
		speedLabel.setPadding(labelPad);
		speedLabel.setOpacity(0.0);

		generalLabel = new Label();
		generalLabel.setFont(labelFont);
		generalLabel.setTextFill(labelFill);
		generalLabel.setPadding(labelPad);
		generalLabel.setOpacity(0.0);
		generalLabel.setWrapText(true);

		next = new Button("Next");
		next.setStyle(BTN_STYLE);
		next.setFont(defFont);
		next.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				document.advanceAnimation();
				next.setVisible(false);
			}
		});

		previous = new Button("Next");
		previous.setStyle(BTN_STYLE);
		previous.setFont(defFont);
		previous.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				document.regressAnimation();
				previous.setVisible(false);
			}
		});

		hideNextPrevBtns();

		// Create the canvas
		canvasPane = new AutomataCanvas();

		this.getChildren().addAll(canvasPane, speedLabel, generalLabel, next, previous);
		StackPane.setAlignment(generalLabel, Pos.BOTTOM_LEFT);
		StackPane.setAlignment(speedLabel, Pos.BOTTOM_RIGHT);
		StackPane.setAlignment(next, Pos.TOP_RIGHT);
		StackPane.setAlignment(previous, Pos.TOP_LEFT);
	}

	/**
	 * Reset the canvas.
	 */
	public void resetCanvas() {
		canvasPane.setGraph(null);
	}

	/**
	 * Gets the main canvas
	 * 
	 * @return The canvas
	 */
	public AutomataCanvas getCanvasPane() {
		return canvasPane;
	}

	/**
	 * Shows the next animation.
	 */
	public void showNextBtn() {
		if (document.getLabel(1) != null && hasProgressionButtons) {
			next.setVisible(true);
			next.setText("Next (" + document.getLabel(1) + ")");
		}
	}

	/**
	 * Shows the previous animation.
	 */
	public void showPrevBtn() {
		if (document.getLabel(-1) != null && hasProgressionButtons) {
			previous.setVisible(true);
			previous.setText("Previous (" + document.getLabel(-1) + ")");
		}
	}

	/**
	 * Hide the previous and next buttons.
	 */
	public void hideNextPrevBtns() {
		next.setVisible(false);
		previous.setVisible(false);
	}

	/**
	 * Shows the current speed in a nice way on screen.
	 * 
	 * @param speed
	 *            The string to show
	 */
	public void showSpeed(String speed) {
		showTip(speed, 1000, speedLabel);
	}

	/**
	 * Shows text in the bottom left, meant for information. Should at least
	 * show the stage of the visualisation when it gets there. Shows for 3000ms.
	 * 
	 * @param text
	 *            The string to show
	 */
	public void showGeneralText(String text) {
		// showTip(text, 3000, generalLabel);
		generalLabel.setOpacity(1.0);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				generalLabel.setText(text);
			}
		});
	}

	/**
	 * Shows text of the screen for a while.
	 * 
	 * @param text
	 *            The text to display
	 * @param timeToDisp
	 *            The time for it to be displayed
	 * @param label
	 *            The label to show the text on.
	 */
	private void showTip(String text, int timeToDisp, Label label) {
		Task<Void> play = new Task<Void>() {
			@Override
			public Void call() {
				try {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							label.setText(text);
						}
					});
					label.setOpacity(1.0);
					Thread.sleep(timeToDisp);
					if (label.getText().equals(text)) {
						FadeTransition ft = new FadeTransition(Duration.millis(1000), label);
						ft.setFromValue(1.0);
						ft.setToValue(0.0);
						ft.play();
					}
				} catch (InterruptedException e) {
					System.out.println("Error showing text: " + e.getMessage());
				}
				return null;
			}
		};
		new Thread(play).start();
	}

	/**
	 * Called when the size of the canvas needs to be updated
	 * @param showList Whether the list of state names should be shown
	 */
	public abstract void invalidateSize(boolean showList);

	/**
	 * Sets the size of the canvas
	 * 
	 * @param width
	 *            The new width
	 * @param height
	 *            The new height
	 */
	public void setCanvasSize(double width, double height) {
		canvasPane.setWidth(width);
		canvasPane.setHeight(height);
		resetCanvas();
		document.drawDocument();
	}
}
