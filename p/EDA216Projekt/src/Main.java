import java.sql.SQLException;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application {
	private Stage primaryStage;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage arg0) throws Exception {
		GUI gui = null;
		// Save stage
		primaryStage = arg0;

		Database db = new Database();
		try {
			// Create GUI object
			gui = new GUI(primaryStage, db);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// Stage configurations (stage = window)
		primaryStage.setTitle("Operatorfönster");
		if(gui != null){
			primaryStage.setScene(gui.getScene());
		}
		else {
			System.out.println("Error starting the application");
			System.exit(0);
		}

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			public void handle(WindowEvent event) {
				// Closes system when operator window is closed
				db.closeConnection();
				System.exit(0);
			}

		});

		primaryStage.show();

	}

}
