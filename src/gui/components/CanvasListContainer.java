package gui.components;

import java.util.ArrayList;

import gui.store.Document;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;

/**
 * Displays a list at the side of the automata canvas
 * 
 * @author Owen Pemberton
 *
 */
public class CanvasListContainer extends BorderPane {

	private CanvasBox canvasBox;
	private ListView<String> listView;
	private boolean listViewCurrentlyVisible = false;

	/**
	 * @param document
	 *            The connected document
	 * @param hasProgressionButtons
	 *            If the canvas has progress buttons
	 */
	public CanvasListContainer(Document document, boolean hasProgressionButtons) {
		final CanvasListContainer container = this;
		canvasBox = new CanvasBox(document, hasProgressionButtons) {
			@Override
			public void invalidateSize(boolean showList) {
				container.resizeForm(showList);
			}
		};
		listView = new ListView<String>();
		
		// Makes list not selectable
		listView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> observable, Object oldvalue, Object newValue) {
                Platform.runLater(new Runnable() {
                    public void run() {
                    	listView.getSelectionModel().select(-1);
                    }
                });

            }
        });
		
		if (document.getCurrentListArray() != null) {
			listView.setItems(FXCollections.observableArrayList(document.getCurrentListArray()));
		}

		this.widthProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if (document.isRegexDocument() && document.isGenerated()) {
					canvasBox.invalidateSize(
							document.getAnimation().getFrame(document.getCurrentFrameIndex()).getLabelListVisible());
				} else {
					canvasBox.invalidateSize(false);
				}
			}
		});
		this.heightProperty().addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable observable) {
				if (document.isRegexDocument() && document.isGenerated()) {
					canvasBox.invalidateSize(
							document.getAnimation().getFrame(document.getCurrentFrameIndex()).getLabelListVisible());
				} else {
					canvasBox.invalidateSize(false);
				}
			}
		});

		this.setRight(listView);
		this.setCenter(canvasBox);
	}

	/**
	 * Resizes the form
	 * 
	 * @param showList
	 *            Whether to show the list, this changes the size
	 */
	public void resizeForm(boolean showList) {
		final CanvasListContainer parentContainer = this;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (showList) {
					parentContainer.setRight(listView);
					listViewCurrentlyVisible = true;
					canvasBox.setCanvasSize(parentContainer.getWidth() - listView.getWidth(),
							parentContainer.getHeight());

				} else {
					parentContainer.setRight(null);
					listViewCurrentlyVisible = false;
					canvasBox.setCanvasSize(parentContainer.getWidth(), parentContainer.getHeight());

				}
			}
		});

	}

	/**
	 * Checks if the list is currently visible
	 * 
	 * @return Whether the list is currently visible
	 */
	public boolean isListViewCurrentlyVisible() {
		return listViewCurrentlyVisible;
	}

	/**
	 * Sets a new list.
	 * 
	 * @param list
	 *            The new list to display
	 */
	public void setListItems(ArrayList<String> list) {
		listView.setItems(FXCollections.observableArrayList(list));
	}

	/**
	 * Gets the container canvas box
	 * 
	 * @return This object's parent
	 */
	public CanvasBox getCanvasBox() {
		return canvasBox;
	}

}
