package gui.main;

import gui.components.MainMenu;
import gui.store.DocumentTabPane;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This launches project and creates and imports the necessary sections of the
 * GUI.
 * 
 * @author Owen Pemberton
 * @author Tom Clarke
 *
 */
public class MainWindow extends Application {

	private DocumentTabPane tabController;
	private MainMenu mainMenu;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.getIcons().add(new Image(getClass().getResource("/fsa_small_simple.png").toExternalForm()));

		BorderPane root = new BorderPane();
		tabController = new DocumentTabPane();
		mainMenu = new MainMenu(tabController);

		root.setTop(mainMenu);
		root.setCenter(tabController);

		Scene scene = new Scene(root, 1000, 700);
		primaryStage.setMinWidth(800);
		primaryStage.setMinHeight(500);
		primaryStage.setTitle("FSA Generator");
		primaryStage.setScene(scene);
		primaryStage.show();

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				System.exit(0);
			}
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
}
