package gui.canvas;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Holds the Canvas for the visualisation.
 * 
 * @author Tom Clarke
 * @author Owen Pemberton
 *
 */
public class AutomataCanvas extends Canvas {

	public final static float LINE_WIDTH = 2.0f;
	public final static float EDGE_WIDTH = 8.0f;
	public final static float ACCEPTING_DIST = 10.0f;

	private double SCALE_X = 100.0f;
	private double SCALE_Y = 100.0f;
	private double STATE_SIZE = 50.0f;

	public final static double BORDER_X = 100.0f;
	public final static double BORDER_Y = 150.0f;

	private boolean colourBlindMode = false;

	private final GraphicsContext g;

	private boolean isEmpty = true;
	private boolean isLoading = true;
	private VisualRepresentation graph;

	/**
	 * Create a new instance
	 */
	public AutomataCanvas() {
		super();
		g = this.getGraphicsContext2D();
		g.setTextAlign(TextAlignment.CENTER);
		draw("Loading...");
	}

	/**
	 * Gets the graphics context to draw on.
	 * 
	 * @return The context
	 */
	protected GraphicsContext getG() {
		return g;
	}

	/**
	 * Gets the scale for when drawing on the X axis.
	 * 
	 * @return The x scale.
	 */
	protected double getACScaleX() {
		return SCALE_X;
	}

	/**
	 * Gets the scale for when drawing on the Y axis.
	 * 
	 * @return The y scale.
	 */
	protected double getACScaleY() {
		return SCALE_Y;
	}

	/**
	 * Gets the state size.
	 * 
	 * @return The size of the states.
	 */
	protected double getStateSize() {
		return STATE_SIZE;
	}

	/**
	 * Set the graph of edges and states.
	 * 
	 * @param v
	 *            The graph
	 */
	public void setGraph(VisualRepresentation v) {
		if (v != null) {
			isEmpty = false;
			isLoading = false;
			graph = v;
		} else {
			isEmpty = true;
			isLoading = false;
			graph = null;
		}
	}

	/**
	 * Draw on the canvas (text).
	 */
	public void draw(String text) {
		resetCanvas();
		if (isEmpty || isLoading) {
			double canvasMidX = (this.getWidth() / 2);
			double canvasMidY = (this.getHeight() / 2);

			Image logo = new Image(getClass().getResource("/fsa_logo_small.png").toExternalForm());
			g.drawImage(logo, canvasMidX - (logo.getWidth() / 2), canvasMidY - logo.getHeight() + 100);
			g.setFill(this.isColourBlindModeOn() ? CanvasColours.NORM_TEXT_COLOUR_CB : CanvasColours.NORM_TEXT_COLOUR);
			g.setStroke(Color.TRANSPARENT);
			g.setTextAlign(TextAlignment.CENTER);
			g.setFont(new Font(18.0));
			g.fillText(text, canvasMidX, canvasMidY + 150);
		}
	}

	/**
	 * Draw the automata
	 * 
	 * @param graphWidth
	 *            The most number of states in the x direction
	 * @param graphHeight
	 *            The most number of states in the y direction
	 * @param totalNodeCount
	 *            The total number of states in the graph
	 */
	public void draw(double graphWidth, double graphHeight, int totalNodeCount) {
		resetCanvas();
		setupScaling(graphWidth, graphHeight, totalNodeCount);
		drawAutomata();
	}

	/**
	 * Clears the canvas and draws the background.
	 */
	private void resetCanvas() {
		g.clearRect(0, 0, this.getWidth(), this.getHeight());

		// draw black background
		g.setFill(Color.BLACK);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		// Draw a border for bounds checking
		g.setStroke(Color.ROYALBLUE);
		g.setLineWidth(LINE_WIDTH);
		g.strokeRect(5, 5, this.getWidth() - 10.0, this.getHeight() - 10.0);
	}

	/**
	 * Sets up the scaling for the next drawing.
	 * 
	 * @param graphWidth
	 *            The most number of states in the x direction
	 * @param graphHeight
	 *            The most number of states in the y direction
	 * @param totalNodeCount
	 *            The total number of states in the graph
	 */
	private void setupScaling(double graphWidth, double graphHeight, int totalNodeCount) {
		// Set up scaling for x and y
		double xLen = this.getWidth() - (BORDER_X * 2.0);
		double yLen = this.getHeight() - (BORDER_Y * 2.0);

		SCALE_X = xLen / (graphWidth);
		SCALE_Y = yLen / (graphHeight);

		// choose to scale based on X or Y
		double scaleValue = Math.max(graphWidth, graphHeight);

		double smallestDim = Math.min(this.getWidth() - (BORDER_X * 2.0), this.getHeight() - (BORDER_Y * 2.0));

		STATE_SIZE = smallestDim / (scaleValue * 2.0);

		if (STATE_SIZE > 100)
			STATE_SIZE = 100;
		if (STATE_SIZE < 40)
			STATE_SIZE = 40;
	}

	/**
	 * Draws the actual automata
	 */
	private void drawAutomata() {
		g.setStroke(Color.TRANSPARENT);
		g.setTextAlign(TextAlignment.CENTER);
		g.setFont(new Font(20.0));
		g.setFill(this.isColourBlindModeOn() ? CanvasColours.NORM_TEXT_COLOUR_CB : CanvasColours.NORM_TEXT_COLOUR);
		g.fillText(graph.getTitle(), this.getWidth() / 2, AutomataCanvas.BORDER_Y / 3);

		// Drawing edges
		for (VisualEdge e : graph.getEdges()) {
			int edgeSource = e.getFrom();
			int edgeDestination = e.getTo();

			int sX = graph.getState(edgeSource).getX();
			int sY = graph.getState(edgeSource).getY();
			int dX = graph.getState(edgeDestination).getX();
			int dY = graph.getState(edgeDestination).getY();

			boolean isSelfEdge = (sX == dX && sY == dY);
			boolean isBackwardsEdge = dX < sX;
			boolean jumpsEdges = false;

			// check if there are nodes inbetween
			for (VisualState s : graph.getStates()) {
				if (s.getId() == edgeSource || s.getId() == edgeDestination) {
					continue;
				}
				if (s.getY() == sY || s.getY() == dY) {
					if (s.getX() > sX && s.getX() < dX) {
						jumpsEdges = true;
						break;
					}
				}
			}

			if (isSelfEdge) {
				e.draw(this, sX, sY, dX, dY, VisualEdgeType.Recursive);
			} else if (isBackwardsEdge) {
				e.draw(this, sX, sY, dX, dY, VisualEdgeType.CurvedDownBack);
			} else if (jumpsEdges) {
				e.draw(this, sX, sY, dX, dY, VisualEdgeType.CurvedUpForward);
			} else {
				e.draw(this, sX, sY, dX, dY, VisualEdgeType.Straight);
			}
		}

		// Drawing states
		for (VisualState s : graph.getStates())
			s.draw(this);

		drawProgText(graph);
	}

	/**
	 * Draws progress text on the canvas (used for testing strings)
	 * 
	 * @param graph
	 *            The graph (frame) that holds the etxt
	 */
	private void drawProgText(VisualRepresentation graph) {
		String progText[] = graph.getProgText();
		if (progText == null)
			return;

		g.setStroke(Color.TRANSPARENT);
		g.setTextAlign(TextAlignment.LEFT);
		g.setFont(new Font(20.0));

		final double borderModifier = 3.0;
		final double charSpacing = 12.0;
		double currentSpacing = 0.0;

		g.setFill(this.isColourBlindModeOn() ? CanvasColours.NORM_TEXT_COLOUR_CB : CanvasColours.NORM_TEXT_COLOUR);
		g.fillText("Word: ", AutomataCanvas.BORDER_X / borderModifier + currentSpacing,
				AutomataCanvas.BORDER_Y / borderModifier);
		currentSpacing += 5 * charSpacing;

		// Green:
		g.setFill(this.isColourBlindModeOn() ? CanvasColours.NORM_TEXT_COLOUR_GREEN_CB
				: CanvasColours.NORM_TEXT_COLOUR_GREEN);
		g.fillText(progText[0], AutomataCanvas.BORDER_X / borderModifier + currentSpacing,
				AutomataCanvas.BORDER_Y / borderModifier);
		currentSpacing += progText[0].length() * charSpacing;
		// Yellow:
		g.setFill(this.isColourBlindModeOn() ? CanvasColours.NORM_TEXT_COLOUR_YELLOW_CB
				: CanvasColours.NORM_TEXT_COLOUR_YELLOW);
		g.fillText(progText[1], AutomataCanvas.BORDER_X / borderModifier + currentSpacing,
				AutomataCanvas.BORDER_Y / borderModifier);
		currentSpacing += progText[1].length() * charSpacing;
		// Red:
		g.setFill(this.isColourBlindModeOn() ? CanvasColours.NORM_TEXT_COLOUR_RED_CB
				: CanvasColours.NORM_TEXT_COLOUR_RED);
		g.fillText(progText[2], AutomataCanvas.BORDER_X / borderModifier + currentSpacing,
				AutomataCanvas.BORDER_Y / borderModifier);
		currentSpacing += progText[2].length() * charSpacing;
		// White:
		g.setFill(this.isColourBlindModeOn() ? CanvasColours.NORM_TEXT_COLOUR_CB : CanvasColours.NORM_TEXT_COLOUR);
		g.fillText(progText[3], AutomataCanvas.BORDER_X / borderModifier + currentSpacing,
				AutomataCanvas.BORDER_Y / borderModifier);
	}

	/**
	 * Sets whether to use colour blind colours.
	 * 
	 * @param colourBlindMode
	 */
	public void setColourBlindMode(boolean colourBlindMode) {
		this.colourBlindMode = colourBlindMode;
	}

	/**
	 * Checks if colour blind colours should be used.
	 * 
	 * @return If colour blind mode is on.
	 */
	public boolean isColourBlindModeOn() {
		return colourBlindMode;
	}
}
