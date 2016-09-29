package gui.store;

import java.util.ArrayList;

import automaton.Animation;
import gui.canvas.VisualRepresentation;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Holds the help document for the user.
 * 
 * @author Owen Pemberton
 *
 */
public class AboutDocument extends BorderPane implements Document {

	private Tab tab;
	private WebView webView;

	/**
	 * @param tab
	 *            The tab holding this document
	 */
	public AboutDocument(Tab tab) {
		super();
		this.tab = tab;

		webView = new WebView();
		WebEngine webEngine = webView.getEngine();
		webEngine.load(getClass().getResource("/about.html").toExternalForm());

		this.setCenter(webView);
	}

	@Override
	public void drawDocument() {
		return;
	}

	@Override
	public void printDocument() {
		return;
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
	public void showSpeed(String string) {
		return;
	}

	@Override
	public String getLabel(int index) {
		return "About";
	}

	@Override
	public String getCurrentRegex() {
		return "";
	}

	@Override
	public boolean isGenerated() {
		return true;
	}

	@Override
	public void setCurrentFrameIndex(int i) {
		return;
	}

	@Override
	public int getCurrentFrameIndex() {
		return 0;
	}

	@Override
	public int getFrameCount() {
		return 0;
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
		return null;
	}

	@Override
	public VisualRepresentation getFinalVisualRepresentation() {
		return null;
	}

	@Override
	public void setColourBlindMode(boolean colourBlindMode) {
		return;
	}

	@Override
	public ArrayList<String> getCurrentListArray() {
		return null;
	}
}
