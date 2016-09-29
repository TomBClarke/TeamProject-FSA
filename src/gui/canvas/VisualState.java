package gui.canvas;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Holds a state
 * 
 * @author Owen Pemberton
 *
 */
public class VisualState {

	private int id;
	private boolean accepting;
	private boolean initial;
	private StateHighlights highlight;
	private int x;
	private int y;
	private String label;

	/**
	 * @param id
	 *            The ID of the state.
	 * @param accepting
	 *            If the state is accepting.
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @param label
	 *            The label
	 */
	public VisualState(int id, boolean accepting, int x, int y, String label) {
		this.id = id;
		this.accepting = accepting;
		this.x = x;
		this.y = y;
		this.label = label;
		this.highlight = StateHighlights.NORMAL;
	}

	/**
	 * @param id
	 *            The ID of the state.
	 * @param accepting
	 *            If the state is accepting.
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @param label
	 *            The label
	 * @param hl
	 *            What the state should be highlighted as
	 */
	public VisualState(int id, boolean accepting, int x, int y, String label, StateHighlights hl) {
		this.id = id;
		this.accepting = accepting;
		this.x = x;
		this.y = y;
		this.label = label;
		this.highlight = StateHighlights.valueOf(hl.toString());
	}

	/**
	 * Gets the ID of the state.
	 * 
	 * @return The ID.
	 */
	public int getId() {
		return id;
	}

	/**
	 * Finds if it is an initial state.
	 * 
	 * @return If the state is an initial state.
	 */
	public boolean isInitialState() {
		return initial;
	}

	/**
	 * Sets if the state is an initial state.
	 * 
	 * @param initial
	 *            If it is an initial state.
	 */
	public void setInitial(boolean initial) {
		this.initial = initial;
	}

	/**
	 * Finds if the state is an accepting state.
	 * 
	 * @return If it is an accepting state.
	 */
	public boolean isAccepting() {
		return accepting;
	}

	/**
	 * Gets the X coordinate of the state.
	 * 
	 * @return The x coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get the Y coordinate of the state.
	 * 
	 * @return The y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Gets the label of the state.
	 * 
	 * @return The label.
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Gets the highlight of the state.
	 * 
	 * @return The highlight
	 */
	public StateHighlights getHL() {
		return highlight;
	}

	/**
	 * Sets the highlight of the state.
	 * 
	 * @param hl
	 *            THe highlight
	 */
	public void setHL(StateHighlights hl) {
		this.highlight = StateHighlights.valueOf(hl.toString());
	}

	/**
	 * Updates the coordinates of the state.
	 * 
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 */
	public void setCoords(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Draws a state
	 * 
	 * @param canvas
	 *            The canvas to draw on
	 * @param sCoords
	 *            The coordinates of the state
	 */
	public void draw(AutomataCanvas canvas) {
		canvas.getG().setStroke(stateHighlightStroke(canvas.isColourBlindModeOn()));
		canvas.getG().setFill(stateHighlightFill(canvas.isColourBlindModeOn()));
		canvas.getG().setLineWidth(AutomataCanvas.LINE_WIDTH);
		if (accepting && highlight == StateHighlights.NORMAL)
			canvas.getG().setFill(canvas.isColourBlindModeOn() ? CanvasColours.STATE_ACCEPTING_COLOUR_CB
					: CanvasColours.STATE_ACCEPTING_COLOUR);

		if (this.isInitialState()) {
			// Initial state (diamond)
			drawDiamond(canvas);
		} else {
			// Normal state (circle);
			drawCircle(canvas);
		}

		double textX = getX() * canvas.getACScaleX() + AutomataCanvas.BORDER_X;
		double textY = getY() * canvas.getACScaleY() + AutomataCanvas.BORDER_Y + 5;
		canvas.getG().setTextAlign(TextAlignment.CENTER);
		canvas.getG().setFill(
				canvas.isColourBlindModeOn() ? CanvasColours.STATE_TEXT_COLOUR_CB : CanvasColours.STATE_TEXT_COLOUR);
		canvas.getG().setFont(new Font(18.0));
		canvas.getG().fillText(label, textX, textY, canvas.getStateSize());
	}

	/**
	 * Draws a diamond
	 * 
	 * @param canvas
	 *            The canvas to draw the diamond on.
	 */
	private void drawDiamond(AutomataCanvas canvas) {
		double diff = (canvas.getStateSize() / 2);

		double x = (getX() * canvas.getACScaleX() + AutomataCanvas.BORDER_X + canvas.getStateSize() / 2) - diff;
		double y = (getY() * canvas.getACScaleY() + AutomataCanvas.BORDER_Y + canvas.getStateSize() / 2) - diff;

		double[] xd = { x - diff, x, x + diff, x };
		double[] yd = { y, y + diff, y, y - diff };
		canvas.getG().fillPolygon(xd, yd, 4);
		canvas.getG().strokePolygon(xd, yd, 4);

		if (accepting) {
			double accdiff = AutomataCanvas.ACCEPTING_DIST * 0.7;
			double[] xda = { x - diff + accdiff, x, x + diff - accdiff, x };
			double[] yda = { y, y + diff - accdiff, y, y - diff + accdiff };
			canvas.getG().strokePolygon(xda, yda, 4);
		}
	}

	/**
	 * Draws a circle
	 * 
	 * @param canvas
	 *            The canvas to draw the diamond on.
	 */
	private void drawCircle(AutomataCanvas canvas) {
		double diff = (canvas.getStateSize() / 2);
		double cx, cy, cd;
		cx = (getX() * canvas.getACScaleX() + AutomataCanvas.BORDER_X) - diff;
		cy = (getY() * canvas.getACScaleY() + AutomataCanvas.BORDER_Y) - diff;

		cd = canvas.getStateSize();

		canvas.getG().fillOval(cx, cy, cd, cd);
		canvas.getG().strokeOval(cx, cy, cd, cd);

		if (accepting) {
			cx += (AutomataCanvas.ACCEPTING_DIST / 2);
			cy += (AutomataCanvas.ACCEPTING_DIST / 2);
			cd -= AutomataCanvas.ACCEPTING_DIST;
			canvas.getG().strokeOval(cx, cy, cd, cd);
		}
	}

	/**
	 * Finds the fill colour for the state.
	 * 
	 * @param cb The colourblind mode
	 * @return The colour
	 */
	private Color stateHighlightFill(boolean cb) {
		if (cb) {
			switch (highlight) {
			case NORMAL:
				return CanvasColours.STATE_FILL_COLOUR_CB;
			case RED:
				return CanvasColours.STATE_FILL_COLOUR_RED_CB;
			case YELLOW:
				return CanvasColours.STATE_FILL_COLOUR_YELLOW_CB;
			case GREEN:
				return CanvasColours.STATE_FILL_COLOUR_GREEN_CB;
			default:
				return CanvasColours.STATE_FILL_COLOUR_CB;
			}
		} else {
			switch (highlight) {
			case NORMAL:
				return CanvasColours.STATE_FILL_COLOUR;
			case RED:
				return CanvasColours.STATE_FILL_COLOUR_RED;
			case YELLOW:
				return CanvasColours.STATE_FILL_COLOUR_YELLOW;
			case GREEN:
				return CanvasColours.STATE_FILL_COLOUR_GREEN;
			default:
				return CanvasColours.STATE_FILL_COLOUR;
			}
		}
	}

	/**
	 * Finds the stroke colour for the state.
	 * 
	 * @param cb The colourblind mode
	 * @return The colour
	 */
	private Color stateHighlightStroke(boolean cb) {
		if (cb) {
			switch (highlight) {
			case NORMAL:
				return CanvasColours.STATE_OUTLINE_COLOUR_CB;
			case RED:
				return CanvasColours.STATE_OUTLINE_COLOUR_RED_CB;
			case YELLOW:
				return CanvasColours.STATE_OUTLINE_COLOUR_YELLOW_CB;
			case GREEN:
				return CanvasColours.STATE_OUTLINE_COLOUR_GREEN_CB;
			default:
				return CanvasColours.STATE_OUTLINE_COLOUR_CB;
			}
		} else {
			switch (highlight) {
			case NORMAL:
				return CanvasColours.STATE_OUTLINE_COLOUR;
			case RED:
				return CanvasColours.STATE_OUTLINE_COLOUR_RED;
			case YELLOW:
				return CanvasColours.STATE_OUTLINE_COLOUR_YELLOW;
			case GREEN:
				return CanvasColours.STATE_OUTLINE_COLOUR_GREEN;
			default:
				return CanvasColours.STATE_OUTLINE_COLOUR;
			}
		}
	}
}
