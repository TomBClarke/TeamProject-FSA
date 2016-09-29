package gui.components;

import gui.store.Document;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * This creates the controls for playback in the GUI.
 * 
 * @author Tom CLarke
 *
 */
public class PlaybackControls extends HBox {

	protected final static String BTN_STYLE = "-fx-base: #4169E1;";
	protected final static String BTN_PLAY = "-fx-base: #66FF66;";
	protected final static Font defFont = Font.font("Aerial", 16.0);

	private Button btnBackStep;
	private Button btnSlower;
	private Button btnReset;
	private Button btnPlayPause;
	private Button btnFaster;
	private Button btnForwardStep;
	private Button btnSkipToEnd;
	private Slider sliderBar;

	private final Document document;
	private final CanvasBox parent;

	private final int spdp0p5 = 3000;
	private final int spdp1p0 = 2000;
	private final int spdp1p5 = 1000;
	private final int spdp2p0 = 500;
	private final int spdm0p5 = -spdp0p5;
	private final int spdm1p0 = -spdp1p0;
	private final int spdm1p5 = -spdp1p5;
	private final int spdm2p0 = -spdp2p0;

	private int speed = spdp1p0;
	private boolean isPlaying = false;

	/**
	 * @param model
	 *            The automata model
	 */
	public PlaybackControls(final Document model, CanvasBox parent) {
		super();
		this.document = model;
		this.parent = parent;

		this.setPadding(new Insets(0.0, 5.0, 5.0, 10.0));
		this.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

		setupPlayControlArea();

	}

	/**
	 * Creates the play back control area.
	 */
	private void setupPlayControlArea() {
		sliderBar = new Slider();
		sliderBar.setStyle(BTN_STYLE);
		double sliderBarHeight = 40.0;
		double sliderBarWidth = 120.0;
		sliderBar.setMinHeight(sliderBarHeight);
		sliderBar.setMaxHeight(sliderBarHeight);
		sliderBar.setPrefHeight(sliderBarHeight);
		sliderBar.setMinWidth(sliderBarHeight);
		sliderBar.setPrefWidth(sliderBarWidth);
		sliderBar.setMaxWidth(sliderBarWidth);
		sliderBar.setMin(0);
		sliderBar.setMax(0);
		sliderBar.setMinorTickCount(0);
		sliderBar.setShowTickMarks(false);
		sliderBar.setShowTickLabels(false);
		sliderBar.setPadding(new Insets(2));

		sliderBar.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				document.setCurrentFrameIndex(newValue.intValue());
				updateGUI();
			}
		});

		btnBackStep = new Button("|◄");
		btnBackStep.setStyle(BTN_STYLE);
		btnBackStep.setTooltip(new Tooltip("Previous frame"));
		btnBackStep.setFont(defFont);
		btnBackStep.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (document.isGenerated()) {
					if (document.getCurrentFrameIndex() == 0) {
						document.regressAnimation();
					} else {
						document.setCurrentFrameIndex(document.getCurrentFrameIndex() - 1);
					}
					updateGUI();
				}
			}
		});

		btnReset = new Button("|◄◄");
		btnReset.setStyle(BTN_STYLE);
		btnReset.setTooltip(new Tooltip("Restart"));
		btnReset.setFont(defFont);
		btnReset.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (document.isGenerated()) {
					if (document.getCurrentFrameIndex() == 0) {
						document.regressAnimation();
					} else {
						document.setCurrentFrameIndex(0);
					}

					updateGUI();
				}
			}
		});

		btnPlayPause = new Button("►");
		btnPlayPause.setStyle(BTN_STYLE);
		btnPlayPause.setTooltip(new Tooltip("Play/Pause"));
		btnPlayPause.setFont(defFont);
		btnPlayPause.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (isPlaying) {
					pauseVis();
				} else {
					playVis();
				}
			}
		});

		btnForwardStep = new Button("►|");
		btnForwardStep.setStyle(BTN_STYLE);
		btnForwardStep.setTooltip(new Tooltip("Next frame"));
		btnForwardStep.setFont(defFont);
		btnForwardStep.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (document.isGenerated()) {
					if (document.getCurrentFrameIndex() == document.getFrameCount() - 1) {
						document.advanceAnimation();
					} else {
						document.setCurrentFrameIndex(document.getCurrentFrameIndex() + 1);
					}
					updateGUI();
				}
			}
		});

		btnSkipToEnd = new Button("►►|");
		btnSkipToEnd.setStyle(BTN_STYLE);
		btnSkipToEnd.setTooltip(new Tooltip("Skip to end"));
		btnSkipToEnd.setFont(defFont);
		btnSkipToEnd.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (document.isGenerated()) {
					if (document.getCurrentFrameIndex() == document.getFrameCount() - 1) {
						document.advanceAnimation();
					} else {
						document.setCurrentFrameIndex(document.getFrameCount() - 1);
					}
					updateGUI();
				}
			}
		});

		btnSlower = new Button("◄◄");
		btnSlower.setStyle(BTN_STYLE);
		btnSlower.setTooltip(new Tooltip("Slow down/re-wind"));
		btnSlower.setFont(defFont);
		btnSlower.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (document.isGenerated()) {
					decreaseSpeed();
				}
			}
		});

		btnFaster = new Button("►►");
		btnFaster.setStyle(BTN_STYLE);
		btnFaster.setTooltip(new Tooltip("Speed up"));
		btnFaster.setFont(defFont);
		btnFaster.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (document.isGenerated()) {
					increaseSpeed();
				}
			}
		});

		this.getChildren().addAll(sliderBar, btnReset, btnBackStep, btnSlower, btnPlayPause, btnFaster, btnForwardStep,
				btnSkipToEnd);
		this.setSpacing(2);

		this.setAlignment(Pos.CENTER_RIGHT);
	}

	/**
	 * Updates the slider and the frame text
	 */
	public void updateGUI() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				sliderBar.setValue(document.getCurrentFrameIndex());

				if (document.getCurrentFrameIndex() == 0) {
					parent.hideNextPrevBtns();
					parent.showPrevBtn();
				} else if (document.getCurrentFrameIndex() == document.getFrameCount() - 1) {
					parent.hideNextPrevBtns();
					parent.showNextBtn();
				} else
					parent.hideNextPrevBtns();
			}
		});
	}

	/**
	 * Sets the limits of the scroll bar (the amount of frames in the section).
	 * 
	 * @param min
	 *            The minimum value.
	 * @param max
	 *            The maximum value.
	 */
	public void setSliderBarLimits(int min, int max) {
		sliderBar.setValue(0);
		sliderBar.setMin((double) min);
		sliderBar.setMax((double) max);
	}

	/**
	 * Resets the slider bar
	 */
	public void setSliderBarOff() {
		sliderBar.setValue(0);
		sliderBar.setMin(0);
		sliderBar.setMax(0);
	}

	/**
	 * Updates the GUI and starts playing.
	 */
	public void playVis() {
		if (document.isGenerated()) {
			isPlaying = true;
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					btnPlayPause.setText("■");
					btnPlayPause.setStyle(BTN_PLAY);
					btnBackStep.setStyle(BTN_PLAY);
					btnSlower.setStyle(BTN_PLAY);
					btnReset.setStyle(BTN_PLAY);
					btnPlayPause.setStyle(BTN_PLAY);
					btnFaster.setStyle(BTN_PLAY);
					btnForwardStep.setStyle(BTN_PLAY);
					btnSkipToEnd.setStyle(BTN_PLAY);
					sliderBar.setStyle(BTN_PLAY);
				}
			});
			play();
		}
	}

	/**
	 * Updates the GUI and stops playing.
	 */
	public void pauseVis() {
		isPlaying = false;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				btnPlayPause.setText("►");
				btnPlayPause.setStyle(BTN_STYLE);
				btnBackStep.setStyle(BTN_STYLE);
				btnSlower.setStyle(BTN_STYLE);
				btnReset.setStyle(BTN_STYLE);
				btnPlayPause.setStyle(BTN_STYLE);
				btnFaster.setStyle(BTN_STYLE);
				btnForwardStep.setStyle(BTN_STYLE);
				btnSkipToEnd.setStyle(BTN_STYLE);
				sliderBar.setStyle(BTN_STYLE);
			}
		});
	}

	/**
	 * Plays the visualisation in a new thread.
	 */
	private void play() {
		// Starts it playing
		Task<Void> play = new Task<Void>() {
			@Override
			public Void call() {
				playFrame();
				return null;
			}
		};
		new Thread(play).start();
	}

	/**
	 * Waits the set time, then plays (if appropriate)
	 */
	private void playFrame() {
		try {
			Thread.sleep(Math.abs(speed));
			if (isPlaying) {
				if (speed > 0) {
					document.setCurrentFrameIndex(document.getCurrentFrameIndex() + 1);
					updateGUI();

					if (document.getCurrentFrameIndex() == document.getFrameCount() - 1) {
						pauseVis();
					} else {
						play();
					}
				} else {
					document.setCurrentFrameIndex(document.getCurrentFrameIndex() - 1);
					updateGUI();

					if (document.getCurrentFrameIndex() == 0) {
						pauseVis();
					} else {
						play();
					}
				}
			}
		} catch (InterruptedException e) {
			System.out.println("Problem with playback: " + e.getMessage());
		}
	}

	/**
	 * Increases the speed of the visualisation in play mode.
	 */
	private void increaseSpeed() {
		switch (speed) {
		case spdp0p5:
			speed = spdp1p0;
			break;
		case spdp1p0:
			speed = spdp1p5;
			break;
		case spdp1p5:
			speed = spdp2p0;
			break;
		case spdp2p0:
			speed = spdp2p0;
			break;
		case spdm0p5:
			speed = spdp0p5;
			break;
		case spdm1p0:
			speed = spdm0p5;
			break;
		case spdm1p5:
			speed = spdm1p0;
			break;
		case spdm2p0:
			speed = spdm1p5;
			break;
		default:
			speed = spdp2p0;
			break;
		}

		document.showSpeed("Speed: " + speedToString());
	}

	/**
	 * Decreases the speed of the visualisation in play mode.
	 */
	private void decreaseSpeed() {
		switch (speed) {
		case spdp0p5:
			speed = spdm0p5;
			break;
		case spdp1p0:
			speed = spdp0p5;
			break;
		case spdp1p5:
			speed = spdp1p0;
			break;
		case spdp2p0:
			speed = spdp1p5;
			break;
		case spdm0p5:
			speed = spdm1p0;
			break;
		case spdm1p0:
			speed = spdm1p5;
			break;
		case spdm1p5:
			speed = spdm2p0;
			break;
		case spdm2p0:
			speed = spdm2p0;
			break;
		default:
			speed = spdm2p0;
			break;
		}

		document.showSpeed("Speed: " + speedToString());
	}

	/**
	 * Converts the current speed to a normal way of looking at it.
	 * 
	 * @return The speed as a string
	 */
	private String speedToString() {
		switch (speed) {
		case spdp0p5:
			return "0.5x";
		case spdp1p0:
			return "1.0x";
		case spdp1p5:
			return "1.5x";
		case spdp2p0:
			return "2.0x";
		case spdm0p5:
			return "-0.5x";
		case spdm1p0:
			return "-1.0x";
		case spdm1p5:
			return "-1.5x";
		case spdm2p0:
			return "-2.0x";
		default:
			return "Error";
		}
	}
}
