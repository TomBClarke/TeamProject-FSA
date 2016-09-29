package gui.canvas;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Holds an edge between two nodes.
 * 
 * @author Tom Clarke
 * @author Owen Pemberton
 *
 */
public class VisualEdge {

	private int source;
	private int destination;
	private String input;
	private EdgeHighlights edgeHighlight;

	/**
	 * @param source
	 *            The start node
	 * @param destination
	 *            The end node
	 * @param s
	 *            The label
	 */
	public VisualEdge(int source, int destination, String s) {
		this.source = source;
		this.destination = destination;
		this.input = s;
		edgeHighlight = EdgeHighlights.NORMAL;
	}

	/**
	 * @param source
	 *            The start node
	 * @param destination
	 *            The end node
	 * @param s
	 *            The label
	 * @param h
	 *            The highlight for the edge to have
	 */
	public VisualEdge(int source, int destination, String s, EdgeHighlights h) {
		this.source = source;
		this.destination = destination;
		this.input = s;
		edgeHighlight = EdgeHighlights.valueOf(h.toString());
		;
	}

	/**
	 * Sets the highlight for the edge
	 * 
	 * @param h
	 *            The highlight to show
	 */
	public void setHighlight(EdgeHighlights h) {
		edgeHighlight = EdgeHighlights.valueOf(h.toString());
	}

	/**
	 * Gets the set highlight for the state.
	 * 
	 * @return The set highlight
	 */
	public EdgeHighlights getHighlight() {
		return edgeHighlight;
	}

	/**
	 * Gets the ID of the start node.
	 * 
	 * @return The ID of the start node.
	 */
	public int getFrom() {
		return source;
	}

	/**
	 * Gets the destination state ID.
	 * 
	 * @return Gets the ID of the destination state.
	 */
	public int getTo() {
		return destination;
	}

	/**
	 * Gets the label of the input.
	 * 
	 * @return the label of the input.
	 */
	public String getInput() {
		return input;
	}

	/**
	 * Draws edges
	 *
	 * @param canvas
	 *            The canvas to draw on.
	 * @param x1
	 *            The source x
	 * @param y1
	 *            The source y
	 * @param x2
	 *            The destination x
	 * @param y2
	 *            The destination y
	 * @param type
	 *            The type of edge
	 */
	public void draw(AutomataCanvas canvas, int x1, int y1, int x2, int y2, VisualEdgeType type) {

		double s1x = x1 * canvas.getACScaleX() + AutomataCanvas.BORDER_X;
		double s1y = y1 * canvas.getACScaleY() + AutomataCanvas.BORDER_Y;
		double s2x = x2 * canvas.getACScaleX() + AutomataCanvas.BORDER_X;
		double s2y = y2 * canvas.getACScaleY() + AutomataCanvas.BORDER_Y;

		canvas.getG().setStroke(edgeHighlightStroke(canvas.isColourBlindModeOn()));

		float curviness = (float) (2f * (Math.sqrt(Math.abs(x2 - x1))));

		switch (type) {
		case Straight: {
			// Straight line
			canvas.getG().strokeLine(s1x, s1y, s2x, s2y);

			drawText(canvas, (s1x + s2x) / 2, ((s1y + s2y) / 2) - 5);
			drawArrow(canvas, s1x, s1y, s2x, s2y);
			break;
		}
		case CurvedUpForward: {
			double midX, midY;
			// curve source-->dest
			midX = Math.abs(s1x + s2x) / 2.0;
			midY = (Math.abs(s1y + s2y) / 2.0) + canvas.getStateSize() * curviness;

			canvas.getG().beginPath();
			canvas.getG().bezierCurveTo(s1x, s1y, midX, midY, s2x, s2y);
			canvas.getG().stroke();

			drawText(canvas, midX,
					(Math.abs(s1y + s2y) / 2.0) + 10.0 + canvas.getStateSize() / 2.0 * Math.sqrt(curviness));

			drawArrow(canvas, midX, midY, s2x, s2y);
			break;

		}
		case CurvedDownBack: {
			double midX, midY;
			// curve source-->dest
			midX = Math.abs(s1x + s2x) / 2.0;
			midY = (Math.abs(s1y + s2y) / 2.0) - canvas.getStateSize() * curviness;

			canvas.getG().beginPath();
			canvas.getG().bezierCurveTo(s1x, s1y, midX, midY, s2x, s2y);
			canvas.getG().stroke();

			drawText(canvas, midX, (Math.abs(s1y + s2y) / 2.0) - (canvas.getStateSize() / 2.0 * Math.sqrt(curviness)));

			drawArrow(canvas, midX, midY, s2x, s2y);
			break;
		}
		case Recursive: {
			// self edge
			double cX = s1x - (canvas.getStateSize() / 2);
			double cY = s1y - (canvas.getStateSize() * 1.2);

			canvas.getG().strokeOval(cX, cY, canvas.getStateSize(), canvas.getStateSize());
			drawText(canvas, cX + canvas.getStateSize(), cY);
			drawArrow(canvas, s1x + canvas.getStateSize(), s1y - canvas.getStateSize(), s2x, s2y);
			break;
		}
		}
	}

	/**
	 * Draws text for the edge.
	 * 
	 * @param canvas
	 *            The canvas to draw on
	 * @param x
	 *            The x coordinate of the text
	 * @param y
	 *            The y coordinate of the text
	 */
	private void drawText(AutomataCanvas canvas, double x, double y) {
		canvas.getG().setTextAlign(TextAlignment.CENTER);
		canvas.getG().setFill(
				canvas.isColourBlindModeOn() ? CanvasColours.EDGE_TEXT_COLOUR_CB : CanvasColours.EDGE_TEXT_COLOUR);
		canvas.getG().setFont(new Font(16.0));
		canvas.getG().fillText(String.valueOf(input), x, y, 1000);
	}

	/**
	 * Draws an arrow.
	 * 
	 * @param canvas
	 *            The canvas to draw on
	 * @param stateSize
	 *            The size of states (so it is positioned correctly)
	 * @param s1x
	 *            The X coordinate of the point it is facing away from.
	 * @param s1y
	 *            The Y coordinate of the point it is facing away from.
	 * @param s2x
	 *            The X coordinate of the point it is facing towards.
	 * @param s2y
	 *            The Y coordinate of the point it is facing towards.
	 */
	private void drawArrow(AutomataCanvas canvas, double s1x, double s1y, double s2x, double s2y) {
		canvas.getG().setStroke(edgeHighlightStroke(canvas.isColourBlindModeOn()));

		double vX = s1x - s2x;
		double vY = s1y - s2y;
		double magV = Math.sqrt(vX * vX + vY * vY);
		double aX1 = s2x + (canvas.getStateSize() / 2) * vX / magV;
		double aY1 = s2y + (canvas.getStateSize() / 2) * vY / magV;
		double aX2 = s2x + (canvas.getStateSize() * 0.7) * vX / magV;
		double aY2 = s2y + (canvas.getStateSize() * 0.7) * vY / magV;

		double p = 5;
		double h = 10;
		double pp = Math.atan((aY2 - aY1) / (aX2 - aX1));
		double ppp = (Math.PI / 2) - p - pp;
		double d = h * Math.cos(ppp);
		double e1 = h * Math.sin(ppp);
		double aX3 = aX2 + e1;
		double aY3 = aY2 + d;

		canvas.getG().strokeLine(aX1, aY1, aX3, aY3);

		ppp = (Math.PI / 2) + p - pp;
		d = h * Math.cos(ppp);
		e1 = h * Math.sin(ppp);
		aX3 = aX2 + e1;
		aY3 = aY2 + d;

		canvas.getG().strokeLine(aX1, aY1, aX3, aY3);
	}

	/**
	 * Finds the stroke colour for the state.
	 * 
	 * @param cb
	 *            If colour blind mode is on.
	 * @return The colour
	 */
	private Color edgeHighlightStroke(boolean cb) {
		if (cb) {
			switch (edgeHighlight) {
			case NORMAL:
				return CanvasColours.EDGE_OUTLINE_COLOUR_CB;
			case RED:
				return CanvasColours.EDGE_OUTLINE_COLOUR_RED_CB;
			case YELLOW:
				return CanvasColours.EDGE_OUTLINE_COLOUR_YELLOW_CB;
			case GREEN:
				return CanvasColours.EDGE_OUTLINE_COLOUR_GREEN_CB;
			default:
				return CanvasColours.EDGE_OUTLINE_COLOUR_CB;
			}
		} else {
			switch (edgeHighlight) {
			case NORMAL:
				return CanvasColours.EDGE_OUTLINE_COLOUR;
			case RED:
				return CanvasColours.EDGE_OUTLINE_COLOUR_RED;
			case YELLOW:
				return CanvasColours.EDGE_OUTLINE_COLOUR_YELLOW;
			case GREEN:
				return CanvasColours.EDGE_OUTLINE_COLOUR_GREEN;
			default:
				return CanvasColours.EDGE_OUTLINE_COLOUR;
			}

		}
	}
}
