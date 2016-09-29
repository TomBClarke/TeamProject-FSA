package gui.store;

import java.util.ArrayList;

import automaton.Animation;
import automaton.AnimationTestString;
import gui.canvas.VisualRepresentation;
import gui.canvas.VisualState;
import gui.components.CanvasListContainer;
import gui.components.PlaybackControls;
import gui.components.TestStringControls;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.control.Tab;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

/**
 * A document to store all information about a string to test in an automaton
 * and its animation.
 * 
 * @author Owen Pemberton
 * @author Tom Clarke
 *
 */
public class TestStringDocument extends BorderPane implements Document {

	private Tab tab;

	private CanvasListContainer gCanvasContainer;
	private PlaybackControls gPlaybackControls;
	private TestStringControls gTestStringControls;
	private HBox combineBox;

	private int currentFrame = 0;
	private boolean generatedOnce = false;
	private boolean colourBlindMode = false;

	private String word;
	private Animation animation;
	private final VisualRepresentation baseGraph;

	/**
	 * @param newTab
	 *            The tab that holds this
	 * @param word
	 *            The word to test
	 * @param animation
	 *            The animation object
	 */
	public TestStringDocument(Tab newTab, VisualRepresentation graph, boolean colourBlindMode) {
		super();
		this.tab = newTab;
		this.baseGraph = graph;
		this.colourBlindMode = colourBlindMode;

		this.setBackground(new Background(new BackgroundFill(Color.DARKGREY, CornerRadii.EMPTY, Insets.EMPTY)));
		this.setMinHeight(0.0);
		this.setMinWidth(0.0);

		initControls();
	}

	/**
	 * Gets the inputed string by the user and tests it on the automata.
	 * 
	 * @param s
	 *            The user string
	 */
	public void runString(String s) {
		currentFrame = 0;
		s = s.replaceAll("\\s+", "");

		if (s == null) {
			tab.setText("Tab");
			generatedOnce = false;

			if (gPlaybackControls != null)
				gPlaybackControls.setSliderBarOff();

			drawDocument();
			return;
		}

		// generate the test string animation
		word = s;
		animation = new AnimationTestString(baseGraph.copy(), word);

		if (s.length() < 14)
			tab.setText(s);
		else
			tab.setText(s.substring(0, 10) + "...");

		if (!generatedOnce) {
			// show playback controls
			gPlaybackControls = new PlaybackControls(this, gCanvasContainer.getCanvasBox());
			combineBox.getChildren().add(gPlaybackControls);
			HBox.setHgrow(gPlaybackControls, Priority.ALWAYS);
			gTestStringControls.setAlignment(Pos.CENTER_LEFT);

			generatedOnce = true;
		}

		gPlaybackControls.setSliderBarOff();
		gPlaybackControls.updateGUI();
		gCanvasContainer.getCanvasBox().hideNextPrevBtns();
		gPlaybackControls.setSliderBarOff();

		gCanvasContainer.getCanvasBox().getCanvasPane().setGraph(animation.getFrame(currentFrame));
		gCanvasContainer.getCanvasBox().showGeneralText(animation.getFrame(currentFrame).getText());
		gPlaybackControls.setSliderBarLimits(0, animation.getFrameCount() - 1);

		this.setCurrentFrameIndex(0);
		drawDocument();

	}

	/**
	 * Loads the controls for the panel.
	 */
	private void initControls() {
		gCanvasContainer = new CanvasListContainer(this, false);
		gTestStringControls = new TestStringControls(this);
		gPlaybackControls = new PlaybackControls(this, gCanvasContainer.getCanvasBox());

		// combine the RegexControl and PlaybackControls
		combineBox = new HBox();
		combineBox.getChildren().add(gTestStringControls);

		HBox.setHgrow(combineBox, Priority.ALWAYS);
		HBox.setHgrow(gTestStringControls, Priority.ALWAYS);

		this.setBottom(combineBox);
		this.setCenter(gCanvasContainer);
	}

	@Override
	public void drawDocument() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (generatedOnce) {
					double graphWidth = 1;
					double graphHeight = 1;
					int totalNodeCount = 0;

					// caluclate graph width and height
					for (VisualState s : animation.getFrame(currentFrame).getStates()) {
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
					
					gCanvasContainer.getCanvasBox().getCanvasPane().setGraph(animation.getFrame(currentFrame));
					gCanvasContainer.getCanvasBox().getCanvasPane().setColourBlindMode(colourBlindMode);
					gCanvasContainer.getCanvasBox().getCanvasPane().draw(graphWidth, graphHeight, totalNodeCount);
					
					// tidying
					if (flatX)
						for (VisualState s : animation.getFrame(currentFrame).getStates())
							s.setCoords(0, s.getY());
					if (flatY)
						for (VisualState s : animation.getFrame(currentFrame).getStates())
							s.setCoords(s.getX(), 0);
				} else {
					gCanvasContainer.getCanvasBox().getCanvasPane().draw("Type in a test string and click Run");
				}
			}
		});
	}

	@Override
	public void advanceAnimation() {
		return;
	}

	@Override
	public void regressAnimation() {
		return;
	}

	@Override
	public int getCurrentFrameIndex() {
		return currentFrame;
	}

	@Override
	public String getLabel(int index) {
		if (index >= 0 && index < animation.getFrameCount()) {
			return animation.getFrame(index).getText();
		}
		return "";
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
	public String getCurrentRegex() {
		return word;
	}

	@Override
	public boolean isGenerated() {
		return true;
	}

	@Override
	public void setCurrentFrameIndex(int i) {
		currentFrame = i;
		gCanvasContainer.getCanvasBox().showGeneralText(animation.getFrame(currentFrame).getText());
		drawDocument();
	}

	@Override
	public void showSpeed(String speed) {
		gCanvasContainer.getCanvasBox().showSpeed(speed);
	}

	@Override
	public int getFrameCount() {
		return animation.getFrameCount();
	}

	@Override
	public Tab getTab() {
		return tab;
	}

	@Override
	public boolean isRegexDocument() {
		return false;
	}

	@Override
	public Animation getAnimation() {
		return animation;
	}

	@Override
	public VisualRepresentation getFinalVisualRepresentation() {
		return baseGraph;
	}

	@Override
	public void setColourBlindMode(boolean colourBlindMode) {
		this.colourBlindMode = colourBlindMode;
		drawDocument();
	}

	@Override
	public ArrayList<String> getCurrentListArray() {
		return null;
	}
}