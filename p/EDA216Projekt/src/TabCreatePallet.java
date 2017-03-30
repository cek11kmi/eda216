import java.sql.SQLException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TabCreatePallet {
	// Layout holder
	public HBox hBox;

	// Main components
	private TextField cookieNameTF;
	private Label invalidInputMessage;
	private Button createPallet;

	// POP UP (PIN code)
	private Stage palletLabelStage;
	private Scene palletLabelScene;
	private Label palletLabel;

	// External objects
	private Database db;
	private Stage primaryStage;

	public TabCreatePallet(int gap, int padding, Database db, Stage primaryStage) {
		this.db = db;
		this.primaryStage = primaryStage;
		initializeComponents(gap, padding);
		initializePalletLabelPopUp(gap, padding);
	}

	private void initializeComponents(int gap, int padding) {
		// Horizontal box
		hBox = new HBox();
		hBox.setSpacing(gap);
		hBox.setPadding(new Insets(padding, padding, padding, padding));

		// Grid pane (Is put at the top in "vBox1", contains labels and text
		// fields)
		GridPane grid = new GridPane();
		grid.setHgap(gap);
		grid.setVgap(gap);

		// Grid pane (grid) components
		Label name = new Label("Cookie name");

		cookieNameTF = new TextField();

		cookieNameTF.setPromptText("E.g. Nut ring");

		// Add grid pane (grid) components
		grid.add(name, 0, 0);
		grid.add(cookieNameTF, 0, 1);

		// Vertical box (Is put in the left in hBox, contains "grid" and
		// "invalidInputMessage"-label)
		VBox vBox1 = new VBox();
		vBox1.setSpacing(gap);

		// Vertical box (vBox1) components
		invalidInputMessage = new Label("");
		invalidInputMessage.setTextFill(Color.RED);
		invalidInputMessage.setWrapText(true);

		// Add vertical box (vBox1) components

		grid.add(invalidInputMessage, 0, 2);



		// Create pallet
		createPallet = new Button("Create pallet");
		createPallet.setOnAction(e -> createPallet());

		// Add components to horizontal box (hBox2)
		grid.add(createPallet, 1, 1);

		vBox1.getChildren().add(grid);

		// Add major component holders (vBox1 and vBox2) to the horizontal box
		// (hBox)
		hBox.getChildren().add(vBox1);

	}

	private void createPallet() {
		// Restore any previous error marks (e.g. text field marked yellow)
		restoreInvalidInputs();

		// Get Strings from text fields
		String cookieName = cookieNameTF.getText();

		// Check if all text fields contains text of acceptable length
		if (cookieName.length() > 0 && cookieName.length() < 101) {
			try {
				if (db.getCookies().contains(cookieName)) {
					int palletId = db.addPallet(cookieName);
					if (palletId != 0) {
						restoreInvalidInputs();
						clearTextField();
						showPalletLabelPopUp(Integer.toString(palletId));
					}
				} else {
					invalidNameInput();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			invalidNameInput();
		}
	}

	private void invalidNameInput() {
		// Mark as YELLOW
		cookieNameTF.setStyle("-fx-background-color: #ffff0052");
		addInvalidInputMessage(cookieNameTF.getText() + " is not a valid cookie name");
	}

	public void restoreInvalidInputs() {
		// Restore text field colors
		cookieNameTF.setStyle("");

		// Set the error message label text to an "empty" string
		invalidInputMessage.setText("");
	}

	private void addInvalidInputMessage(String message) {
		if (invalidInputMessage.getText().length() == 0) {
			invalidInputMessage.setText(invalidInputMessage.getText() + message);
		} else {
			invalidInputMessage.setText(invalidInputMessage.getText() + ", " + message);
		}
	}

	private void initializePalletLabelPopUp(int gap, int padding) {
		// Vertical box
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(padding, padding, padding, padding));
		vBox.setAlignment(Pos.CENTER);

		// Grid pane
		GridPane PINCodeGrid = new GridPane();
		PINCodeGrid.setHgap(gap);
		PINCodeGrid.setVgap(gap);

		// Grid pane (PINCodeGrid) components
		Label message = new Label("The pallet's barcode number is: ");
		palletLabel = new Label("");

		// Add grid pane (PINCodeGrid) components
		PINCodeGrid.add(message, 0, 0);
		PINCodeGrid.add(palletLabel, 1, 0);

		// Add the layout holder (PINcodeGrid) to the vBox
		vBox.getChildren().add(PINCodeGrid);

		// Scene
		palletLabelScene = new Scene(vBox, 300, 60); // Arbitrary integers
	}

	private void showPalletLabelPopUp(String palletLabel) {
		// Load new PIN code
		this.palletLabel.setText(palletLabel);

		// Stage
		palletLabelStage = new Stage();
		palletLabelStage.setTitle("Pallet barcode");
		palletLabelStage.initModality(Modality.APPLICATION_MODAL);
		palletLabelStage.initOwner(primaryStage);
		palletLabelStage.setScene(palletLabelScene);
		palletLabelStage.show();
	}

	private void clearTextField() {
		cookieNameTF.setText("");
	}

}
