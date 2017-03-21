import java.sql.SQLException;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TabSearchPallet {
	// Layout holder
	public HBox hBox;

	// Main components
	private TextField barCodeTF;
	private Label invalidBarcodeInputMessage;
	private Button searchPalletByBarcode;

	private TextField productTF;
	private Label invalidProductInputMessage;
	private Button searchPalletByProduct;

	// POP UP (PIN code)
	private Stage palletLabelStage;
	private Scene palletLabelScene;
	private Label palletLabel;

	private TableView<SearchPalletDataHolder> table;
	private final ObservableList<SearchPalletDataHolder> tableData = FXCollections
			.observableArrayList(

	);

	// External objects
	private Database db;
	private Stage primaryStage;

	public TabSearchPallet(int gap, int padding, Database db, Stage primaryStage) {
		this.db = db;
		this.primaryStage = primaryStage;
		initializeComponents(gap, padding);
		initializePalletIdPopUp(gap, padding);
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
		Label barcodeName = new Label("Search for pallet with barcode");

		barCodeTF = new TextField();

		barCodeTF.setPromptText("E.g. 1");

		// Add grid pane (grid) components
		grid.add(barcodeName, 0, 0);
		grid.add(barCodeTF, 0, 1);

		// Vertical box (Is put in the left in hBox, contains "grid" and
		// "invalidInputMessage"-label)
		VBox vBox1 = new VBox();
		vBox1.setSpacing(gap);

		// Vertical box (vBox1) components
		invalidBarcodeInputMessage = new Label("");
		invalidBarcodeInputMessage.setTextFill(Color.RED);
		invalidBarcodeInputMessage.setWrapText(true);

		grid.add(invalidBarcodeInputMessage, 0, 2);

		Label productName = new Label("Search for pallets containing products");

		productTF = new TextField();
		productTF.setPromptText("E.g. Nut ring");

		grid.add(productName, 0, 3);
		grid.add(productTF, 0, 4);

		invalidProductInputMessage = new Label("");
		invalidProductInputMessage.setTextFill(Color.RED);
		invalidProductInputMessage.setWrapText(true);

		grid.add(invalidProductInputMessage, 0, 5);
		// Add vertical box (vBox1) components

		// Vertical box (Is put to the right in hBox, contains a horizontal box)
		VBox vBox2 = new VBox();
		vBox2.setAlignment(Pos.TOP_RIGHT);

		// Create pallet
		searchPalletByBarcode = new Button("Search pallet");
		searchPalletByBarcode.setOnAction(e -> searchPalletByBarcode());

		searchPalletByProduct = new Button("Search pallet");
		searchPalletByProduct.setOnAction(e -> searchPalletByProduct());

		grid.add(searchPalletByBarcode, 1, 1);
		grid.add(searchPalletByProduct, 1, 4);
		vBox1.getChildren().add(grid);

		// Add major component holders (vBox1 and vBox2) to the horizontal box
		// (hBox)
		hBox.getChildren().add(vBox1);
		
		
		table = new TableView<SearchPalletDataHolder>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Removes
																					// unspecified
																					// empty
																					// column
																					// inuserTable
		table.setItems(tableData);

		TableColumn<SearchPalletDataHolder, String> palletIdColumn = new TableColumn<SearchPalletDataHolder, String>(
				"ID");
		palletIdColumn
				.setCellValueFactory(new PropertyValueFactory<SearchPalletDataHolder, String>("ID"));
		TableColumn<SearchPalletDataHolder, String> productNameColumn = new TableColumn<SearchPalletDataHolder, String>(
				"Cookie");
		palletIdColumn
				.setCellValueFactory(new PropertyValueFactory<SearchPalletDataHolder, String>("Cookie"));
		
		TableColumn<SearchPalletDataHolder, String> deliveredDateColumn = new TableColumn<SearchPalletDataHolder, String>(
				"Delivered");
		palletIdColumn
				.setCellValueFactory(new PropertyValueFactory<SearchPalletDataHolder, String>("Delivered"));
		
		TableColumn<SearchPalletDataHolder, String> customerColumn = new TableColumn<SearchPalletDataHolder, String>(
				"Customer");
		palletIdColumn
				.setCellValueFactory(new PropertyValueFactory<SearchPalletDataHolder, String>("Customer"));
		
		TableColumn<SearchPalletDataHolder, String> productionDateColumn = new TableColumn<SearchPalletDataHolder, String>(
				"Produced");
		palletIdColumn
				.setCellValueFactory(new PropertyValueFactory<SearchPalletDataHolder, String>("Produced"));

		table.getColumns().addAll(palletIdColumn);
		table.getColumns().addAll(productNameColumn);
		table.getColumns().addAll(deliveredDateColumn);
		table.getColumns().addAll(customerColumn);
		table.getColumns().addAll(productionDateColumn);

		// // Add grid pane (PINCodeGrid) components
		// PINCodeGrid.add(message, 0, 0);
		// PINCodeGrid.add(palletLabel, 1, 0);
		//
		// // Add the layout holder (PINcodeGrid) to the vBox
		vBox2.getChildren().add(table);
		
		hBox.getChildren().add(vBox2);
		HBox.setHgrow(vBox2, Priority.ALWAYS);
	}

	private void searchPalletByBarcode() {
		// Restore any previous error marks (e.g. text field marked yellow)
		restoreInvalidInputs();

		// Get Strings from text fields
		String cookieName = barCodeTF.getText();

		// Check if all text fields contains text of acceptable length
		if (cookieName.length() > 0 && cookieName.length() < 101) {
			try {
				if (db.getCookies().contains(cookieName)) {
					int palletId = db.addPallet(cookieName);
					if (palletId != 0) {
						restoreInvalidInputs();
						clearTextField();
						// showPalletLabelPopUp(Integer.toString(palletId));
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

	private void searchPalletByProduct() {
		restoreInvalidInputs();
		String product = productTF.getText();
		if (product.length() > 0 && product.length() < 101) {
			try {
				if (db.getCookies().contains(product)) {
					List<Pallet> palletList = db.getPalletsByProduct(product);
					if (palletList.size() != 0) {
						restoreInvalidInputs();
						clearTextField();
						for (Pallet p : palletList) {
							System.out.println(p);
						}
						addPalletsToTable(palletList);
					}
				} else {
					invalidProductInput();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			invalidProductInput();
		}

	}

	private void invalidNameInput() {
		// Mark as YELLOW
		barCodeTF.setStyle("-fx-background-color: #ffff0052");
		addInvalidInputMessage(barCodeTF.getText() + " is not a valid cookie name");
	}

	private void invalidProductInput() {
		// Mark as YELLOW
		productTF.setStyle("-fx-background-color: #ffff0052");
		addInvalidProductInputMessage(productTF.getText() + " is not a valid cookie name");
	}

	public void restoreInvalidInputs() {
		// Restore text field colors
		barCodeTF.setStyle("");

		// Set the error message label text to an "empty" string
		invalidBarcodeInputMessage.setText("");
	}

	private void addInvalidProductInputMessage(String message) {
		if (invalidProductInputMessage.getText().length() == 0) {
			invalidProductInputMessage.setText(invalidProductInputMessage.getText() + message);
		} else {
			invalidProductInputMessage.setText(invalidProductInputMessage.getText() + ", " + message);
		}
	}

	private void addInvalidInputMessage(String message) {
		if (invalidBarcodeInputMessage.getText().length() == 0) {
			invalidBarcodeInputMessage.setText(invalidBarcodeInputMessage.getText() + message);
		} else {
			invalidBarcodeInputMessage.setText(invalidBarcodeInputMessage.getText() + ", " + message);
		}
	}

	@SuppressWarnings("unchecked")
	private void initializePalletIdPopUp(int gap, int padding) {
		// Vertical box
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(padding, padding, padding, padding));
		vBox.setAlignment(Pos.CENTER);

		// Grid pane
		GridPane palletIdGrid = new GridPane();
		palletIdGrid.setHgap(gap);
		palletIdGrid.setVgap(gap);

		

		// Scene
		palletLabelScene = new Scene(vBox, 300, 60); // Arbitrary integers
	}

	private void showPalletIdTablePopUp(List<Pallet> palletList) {
		addPalletsToTable(palletList);

		// Stage
		palletLabelStage = new Stage();
		palletLabelStage.setTitle("Search results");
		palletLabelStage.initModality(Modality.APPLICATION_MODAL);
		palletLabelStage.initOwner(primaryStage);
		palletLabelStage.setScene(palletLabelScene);
		palletLabelStage.show();

	}

	private void clearTextField() {
		barCodeTF.setText("");
	}

	private void emptyTable() {
		tableData.clear();
	}

	public void addPalletsToTable(List<Pallet> palletList) {
		emptyTable();
		if (palletList != null) {
			for (Pallet p : palletList) {
				insertData(p);

			}
		}
	}

	private void insertData(Pallet p) {
		tableData.add(new SearchPalletDataHolder(p.getId(),p.getCookie(),p.getDelivered(),p.getCustomer(),p.getProduced()));

	}

}
