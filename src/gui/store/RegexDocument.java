package gui.store;

import java.util.ArrayList;

import automaton.Animation;
import automaton.AnimationDFA;
import automaton.AnimationNFA;
import automaton.thompsons.ConBlock;
import automaton.thompsons.RegexParser;
import gui.canvas.VisualRepresentation;
import gui.canvas.VisualState;
import gui.components.CanvasListContainer;
import gui.components.PlaybackControls;
import gui.components.RegexControls;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tab;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * This holds and handles the frames of the visualisation.
 * 
 * @author Owen Pemberton
 * @author Tom Clarke
 *
 */
public class RegexDocument extends BorderPane implements Document {

	private CanvasListContainer gCanvasContainer;
	private PlaybackControls gPlaybackControls;
	private RegexControls gRegexControls;

	private int currentFrame = 0;
	private FullAnimation animations;
	private Animation animation;

	private boolean generated = false;
	private boolean loading = false;
	private boolean generatedOnce = false;
	private boolean colourBlindMode = false;
	private String currentRegex;
	private Tab tab;
	private HBox combineBox;

	/**
	 * @param newTab
	 *            The tab which holds the document
	 * @param colourBlindMode
	 *            If colour blind mode should be used
	 */
	public RegexDocument(Tab newTab, boolean colourBlindMode) {
		super();
		this.tab = newTab;
		this.colourBlindMode = colourBlindMode;
		this.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
		initControls();
		generated = false;
	}

	public boolean currentFrameListVisible() {
		if (isGenerated()) {
			return animation.getFrame(currentFrame).getLabelListVisible();
		}
		return false;
	}

	@Override
	public void drawDocument() {
		if (currentFrameListVisible() != gCanvasContainer.isListViewCurrentlyVisible()) {
			gCanvasContainer.resizeForm(currentFrameListVisible());
		}
		if (getCurrentListArray() != null) {
			gCanvasContainer.setListItems(getCurrentListArray());
		}
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (isGenerated()) {
					double graphWidth = 0;
					double graphHeight = 0;
					int totalNodeCount = 0;

					for (VisualState s : animation.getFrame(animation.getFrameCount() - 1).getStates()) {
						double xCoord = s.getX();
						double yCoord = s.getY();
						graphWidth = (graphWidth < xCoord ? xCoord : graphWidth);
						graphHeight = (graphHeight < yCoord ? yCoord : graphHeight);
						totalNodeCount++;
					}

					// puts automata in the middle of the screen.
					boolean flatX = false;
					boolean flatY = false;
					if (graphWidth == 0) {
						flatX = true;
						graphWidth = 2;
						for (VisualState s : animation.getFrame(currentFrame).getStates()) {
							s.setCoords(1, s.getY());
						}
					}
					if (graphHeight == 0) {
						flatY = true;
						graphHeight = 2;
						for (VisualState s : animation.getFrame(currentFrame).getStates()) {
							s.setCoords(s.getX(), 1);
						}
					}

					gCanvasContainer.getCanvasBox().getCanvasPane().setColourBlindMode(colourBlindMode);
					gCanvasContainer.getCanvasBox().getCanvasPane().setGraph(animation.getFrame(currentFrame));
					gCanvasContainer.getCanvasBox().getCanvasPane().draw(graphWidth, graphHeight, totalNodeCount);

					// tidying
					if (flatX)
						for (VisualState s : animation.getFrame(currentFrame).getStates())
							s.setCoords(0, s.getY());
					if (flatY)
						for (VisualState s : animation.getFrame(currentFrame).getStates())
							s.setCoords(s.getX(), 0);

				} else {
					gCanvasContainer.getCanvasBox().getCanvasPane()
							.draw("Type in a regular expression and click Generate");
				}
			}
		});
	}

	@Override
	public String getCurrentRegex() {
		if (isGenerated())
			return currentRegex;
		else
			return null;
	}

	/**
	 * Sets the controller objects of the document.
	 */
	private void initControls() {
		gCanvasContainer = new CanvasListContainer(this, true);
		gRegexControls = new RegexControls(this);

		// combine the RegexControl and PlaybackControls
		combineBox = new HBox();
		combineBox.getChildren().add(gRegexControls);

		HBox.setHgrow(combineBox, Priority.ALWAYS);
		HBox.setHgrow(gRegexControls, Priority.ALWAYS);

		this.setBottom(combineBox);
		this.setCenter(gCanvasContainer);
	}

	/**
	 * Takes a input regular expression and tried to generate an animation of
	 * it.
	 * 
	 * @param s
	 *            The regex
	 */
	public void attemptGenerate(String s) {
		// s = s.replaceAll("\\s+", "");

		if (s == null) {
			tab.setText("Tab");
			generated = false;

			if (gPlaybackControls != null)
				gPlaybackControls.setSliderBarOff();

			drawDocument();
			return;
		}

		makeEverythingVisible();

		currentRegex = s;
		loading = true;

		if (s.length() < 14)
			tab.setText(s);
		else
			tab.setText(s.substring(0, 10) + "...");

		gPlaybackControls.setSliderBarOff();

		// draw the loading screen to show we haven't frozen
		drawDocument();

		gCanvasContainer.getCanvasBox().hideNextPrevBtns();

		final String regex = s;
		Task<Void> load = new Task<Void>() {
			@Override
			public Void call() {
				if (!RegexParser.isWellFormed(regex.toCharArray())) {
					Platform.runLater(new Runnable() {
						@Override
						public void run() {
							gCanvasContainer.getCanvasBox().resetCanvas();
							gCanvasContainer.getCanvasBox().hideNextPrevBtns();
							gCanvasContainer.getCanvasBox().showGeneralText("");
							Alert alert = new Alert(AlertType.WARNING);
							alert.setTitle("Error");
							alert.setHeaderText(null);
							alert.setContentText("Bad regular expression entered");
							alert.showAndWait();
							generated = false;
							loading = false;

							drawDocument();
						}
					});
					return null;
				}

				ConBlock c = RegexParser.parse(regex.toCharArray());
				Animation nfa = new AnimationNFA(c);
				Animation dfa = new AnimationDFA(nfa.getAutomaton(), nfa.getFrame(nfa.getFrameCount() - 1));

				animations = new FullAnimation();
				animations.setAnimation(nfa);
				animations.setAnimation(dfa);

				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						startAnimation();
					}
				});

				return null;
			}
		};
		new Thread(load).start();
	}

	/**
	 * Enables everything, meant for use after first generation.
	 */
	private void makeEverythingVisible() {
		if (!generatedOnce) {
			FadeTransition ftout = new FadeTransition(Duration.millis(300), combineBox);
			ftout.setFromValue(1.0);
			ftout.setToValue(0.0);
			ftout.play();

			gPlaybackControls = new PlaybackControls(this, gCanvasContainer.getCanvasBox());
			combineBox.getChildren().add(gPlaybackControls);
			HBox.setHgrow(gPlaybackControls, Priority.ALWAYS);
			gRegexControls.setAlignment(Pos.CENTER_LEFT);

			FadeTransition ftin = new FadeTransition(Duration.millis(300), combineBox);
			ftin.setFromValue(0.0);
			ftin.setToValue(1.0);
			ftin.play();

			generatedOnce = true;
		}
	}

	/**
	 * Starts the current animation from the beginning on the screen.
	 */
	private void startAnimation() {
		animation = animations.getCurrent();
		generated = true;
		loading = false;
		gPlaybackControls.setSliderBarOff();
		gCanvasContainer.getCanvasBox().getCanvasPane().setGraph(animation.getFrame(currentFrame));
		gCanvasContainer.getCanvasBox().showGeneralText(animations.getCurrentLabel());
		gPlaybackControls.setSliderBarLimits(0, animation.getFrameCount() - 1);
		drawDocument();
	}

	@Override
	public void advanceAnimation() {
		boolean adv = animations.advance();
		if (adv)
			startAnimation();
	}

	@Override
	public void regressAnimation() {
		boolean reg = animations.regress();
		if (reg)
			startAnimation();
	}

	/**
	 * Moves the visualisation to the desired animation.
	 * 
	 * @param i
	 *            The index of the animation.
	 */
	public void setAnimation(int i) {
		animations.skipTo(i);
		startAnimation();
	}

	@Override
	public boolean isGenerated() {
		return generated;
	}

	/**
	 * Tests to see if the animation is loading.
	 * 
	 * @return If it is loading.
	 */
	public boolean isLoading() {
		return loading;
	}

	@Override
	public int getCurrentFrameIndex() {
		return currentFrame;
	}

	@Override
	public int getFrameCount() {
		return animation.getFrameCount();
	}

	@Override
	public void setCurrentFrameIndex(int x) {
		if (isGenerated()) {
			if (x < animations.getCurrent().getFrameCount() && x >= 0) {
				currentFrame = x;
				gCanvasContainer.getCanvasBox().showGeneralText(animation.getFrame(currentFrame).getText());
				drawDocument();
			}
		}
	}

	/**
	 * Sets the text input for the tab.
	 * 
	 * @param regex
	 *            The regex to display.
	 */
	public void setRegexInput(String regex) {
		gRegexControls.setRegexInput(regex);
	}

	@Override
	public void showSpeed(String speed) {
		gCanvasContainer.getCanvasBox().showSpeed(speed);
	}

	@Override
	public String getLabel(int index) {
		return animations.getLabel(index);
	}

	@Override
	public void printDocument() {
		PrinterJob job = PrinterJob.createPrinterJob();
		if (job.showPrintDialog(null)) {
			if (job != null) {
				boolean success = job.printPage(gCanvasContainer.getCanvasBox().getCanvasPane());
				if (success) {
					job.endJob();
				}
			}
		}
	}

	@Override
	public Tab getTab() {
		return tab;
	}

	@Override
	public boolean isRegexDocument() {
		return true;
	}

	@Override
	public Animation getAnimation() {
		return animation;
	}

	@Override
	public VisualRepresentation getFinalVisualRepresentation() {
		return animations.getLastVisualRepresentation();
	}

	@Override
	public void setColourBlindMode(boolean colourBlindMode) {
		this.colourBlindMode = colourBlindMode;
		drawDocument();
	}

	@Override
	public ArrayList<String> getCurrentListArray() {
		if (isGenerated()) {
			return animation.getFrame(currentFrame).getLabels();
		}
		return null;
	}
}
