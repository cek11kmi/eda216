import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

	private TextField customerTF;
	private Label invalidCustomerInputMessage;
	private Button searchPalletByCustomer;

	private TextField time1TF;
	private TextField time2TF;
	private Label invalidTimeMessage;
	private Button searchPalletByTime;

	private TableView<SearchPalletDataHolder> table;
	private final ObservableList<SearchPalletDataHolder> tableData = FXCollections.observableArrayList(

	);

	// External objects
	private Database db;

	public TabSearchPallet(int gap, int padding, Database db) {
		this.db = db;

		initializeComponents(gap, padding);

	}

	@SuppressWarnings("unchecked")
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

		Label customer = new Label("Search for pallets by customer");

		customerTF = new TextField();
		customerTF.setPromptText("E.g. Småbröd AB");

		grid.add(customer, 0, 6);
		grid.add(customerTF, 0, 7);

		invalidCustomerInputMessage = new Label("");
		invalidCustomerInputMessage.setTextFill(Color.RED);
		invalidCustomerInputMessage.setWrapText(true);

		grid.add(invalidCustomerInputMessage, 0, 8);

		Label time = new Label("Search for pallets by time");
		Label timeFrom = new Label("From:");
		Label timeTo = new Label("To:");

		time1TF = new TextField();
		time1TF.setPromptText("E.g. 2017-03-28 12:24:55");

		time2TF = new TextField();
		time2TF.setPromptText("E.g. 2017-03-28 12:24:55");

		grid.add(time, 0, 9);
		grid.add(timeFrom, 0, 10);
		grid.add(time1TF, 0, 11);

		grid.add(timeTo, 0, 12);
		grid.add(time2TF, 0, 13);

		invalidTimeMessage = new Label("");
		invalidTimeMessage.setTextFill(Color.RED);
		invalidTimeMessage.setWrapText(true);

		grid.add(invalidTimeMessage, 0, 14);

		// Add vertical box (vBox1) components

		// Vertical box (Is put to the right in hBox, contains a horizontal box)
		VBox vBox2 = new VBox();
		vBox2.setAlignment(Pos.TOP_RIGHT);

		// Create pallet
		searchPalletByBarcode = new Button("Search");
		searchPalletByBarcode.setOnAction(e -> searchPalletByBarcode());

		searchPalletByProduct = new Button("Search");
		searchPalletByProduct.setOnAction(e -> searchPalletByProduct());

		searchPalletByCustomer = new Button("Search");
		searchPalletByCustomer.setOnAction(e -> searchPalletsByCustomer());

		searchPalletByTime = new Button("Search");
		searchPalletByTime.setOnAction(e -> searchPalletsByTime());

		grid.add(searchPalletByBarcode, 1, 1);
		grid.add(searchPalletByProduct, 1, 4);
		grid.add(searchPalletByCustomer, 1, 7);
		grid.add(searchPalletByTime, 1, 13);
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
		palletIdColumn.setCellValueFactory(new PropertyValueFactory<SearchPalletDataHolder, String>("palletId"));
		palletIdColumn.setMinWidth(30);
		palletIdColumn.setMaxWidth(30);
		TableColumn<SearchPalletDataHolder, String> productNameColumn = new TableColumn<SearchPalletDataHolder, String>(
				"Cookie");
		productNameColumn.setCellValueFactory(new PropertyValueFactory<SearchPalletDataHolder, String>("productName"));

		TableColumn<SearchPalletDataHolder, String> deliveredDateColumn = new TableColumn<SearchPalletDataHolder, String>(
				"Delivery");
		deliveredDateColumn
				.setCellValueFactory(new PropertyValueFactory<SearchPalletDataHolder, String>("deliveryDate"));

		TableColumn<SearchPalletDataHolder, String> customerColumn = new TableColumn<SearchPalletDataHolder, String>(
				"Customer");
		customerColumn.setCellValueFactory(new PropertyValueFactory<SearchPalletDataHolder, String>("customer"));

		TableColumn<SearchPalletDataHolder, String> productionDateColumn = new TableColumn<SearchPalletDataHolder, String>(
				"Produced");
		productionDateColumn
				.setCellValueFactory(new PropertyValueFactory<SearchPalletDataHolder, String>("productionDate"));

		table.getColumns().addAll(palletIdColumn);
		table.getColumns().addAll(productNameColumn);
		table.getColumns().addAll(deliveredDateColumn);
		table.getColumns().addAll(customerColumn);
		table.getColumns().addAll(productionDateColumn);

		vBox2.getChildren().add(table);

		hBox.getChildren().add(vBox2);
		HBox.setHgrow(vBox2, Priority.ALWAYS);
	}

	private void searchPalletByBarcode() {
		// Restore any previous error marks (e.g. text field marked yellow)
		restoreInvalidInputs();

		// Get Strings from text fields
		String barCode = barCodeTF.getText();
		int barCodeInt = 0;

		// Check if all text fields contains text of acceptable length
		if (barCode.length() > 0 && barCode.length() < 101) {
			try {
				barCodeInt = Integer.parseInt(barCode);
			} catch (NumberFormatException e) {
				invalidBarCodeInput();
				return;
			}
			try {
				restoreInvalidInputs();
				clearTextFields();
				List<Pallet> palletList = new LinkedList<Pallet>();
				Pallet p = db.getPalletByID(barCodeInt);
				if (p == null) {
					palletList.add(db.getPalletByIDRestricted(barCodeInt));
				} else {
					palletList.add(p);
				}

				addPalletsToTable(palletList);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			invalidBarCodeInput();
		}
	}

	private void searchPalletByProduct() {
		restoreInvalidInputs();
		String product = productTF.getText();

		if (product.length() > 0 && product.length() < 101) {
			try {
				if (db.getCookies().contains(product)) {
					emptyTable();
					restoreInvalidInputs();
					clearTextFields();
					List<Pallet> palletList = db.getPalletsByProduct(product);
					if (palletList.size() != 0) {
						addPalletsToTable(palletList);
					} else {
						palletList = db.getPalletsByProductRestricted(product);
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
		table.refresh();
	}

	private void searchPalletsByCustomer() {
		restoreInvalidInputs();
		String customer = customerTF.getText();

		if (customer.length() > 0 && customer.length() < 101) {
			try {
				if (db.getCustomers().contains(customer)) {
					emptyTable();
					restoreInvalidInputs();
					clearTextFields();
					List<Pallet> palletList = db.getPalletsByCustomer(customer);
					if (palletList.size() != 0) {
						addPalletsToTable(palletList);
					}
				} else {
					invalidCustomerInput();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			invalidCustomerInput();
		}
		table.refresh();
	}

	private void searchPalletsByTime() {
		restoreInvalidInputs();
		String time1 = time1TF.getText();
		String time2 = time2TF.getText();

		if (time1.length() == 19 && time2.length() == 19) {
			emptyTable();
			restoreInvalidInputs();
			clearTextFields();
			try {
				List<Pallet> palletList = db.getPalletsByTime(time1, time2);
				if (palletList.size() != 0) {
					addPalletsToTable(palletList);
				} else {
					palletList = db.getPalletsByTimeRestricted(time1, time2);
					addPalletsToTable(palletList);
				}
			} catch (SQLException e) {
				invalidTimeInput();
			}
		} else {
			invalidTimeInput();
		}
		table.refresh();
	}

	private void invalidBarCodeInput() {
		// Mark as YELLOW
		barCodeTF.setStyle("-fx-background-color: #ffff0052");
		addInvalidInputMessage(barCodeTF.getText() + " is not a valid bar code");
	}

	private void invalidProductInput() {
		// Mark as YELLOW
		productTF.setStyle("-fx-background-color: #ffff0052");
		addInvalidProductInputMessage(productTF.getText() + " is not a valid cookie name");
	}

	private void invalidCustomerInput() {
		// Mark as YELLOW
		customerTF.setStyle("-fx-background-color: #ffff0052");
		addInvalidCustomerInputMessage(customerTF.getText() + " is not a valid customer name");
	}

	private void invalidTimeInput() {
		// Mark as YELLOW
		time1TF.setStyle("-fx-background-color: #ffff0052");
		time2TF.setStyle("-fx-background-color: #ffff0052");
		addInvalidTimeInputMessage(time1TF.getText() + " and/or " + time2TF.getText() + " not valid");
	}

	public void restoreInvalidInputs() {
		// Restore text field colors
		barCodeTF.setStyle("");
		productTF.setStyle("");
		customerTF.setStyle("");
		time1TF.setStyle("");
		time2TF.setStyle("");

		// Set the error message label text to an "empty" string
		invalidBarcodeInputMessage.setText("");
		invalidProductInputMessage.setText("");
		invalidCustomerInputMessage.setText("");
		invalidTimeMessage.setText("");
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

	private void addInvalidCustomerInputMessage(String message) {
		if (invalidCustomerInputMessage.getText().length() == 0) {
			invalidCustomerInputMessage.setText(invalidCustomerInputMessage.getText() + message);
		} else {
			invalidCustomerInputMessage.setText(invalidCustomerInputMessage.getText() + ", " + message);
		}
	}

	private void addInvalidTimeInputMessage(String message) {
		if (invalidTimeMessage.getText().length() == 0) {
			invalidTimeMessage.setText(invalidTimeMessage.getText() + message);
		} else {
			invalidTimeMessage.setText(invalidTimeMessage.getText() + ", " + message);
		}
	}

	public void clearTextFields() {
		barCodeTF.setText("");
		productTF.setText("");
		customerTF.setText("");
		time1TF.setText("");
		time2TF.setText("");
	}

	private void emptyTable() {
		tableData.clear();
	}

	public void addPalletsToTable(List<Pallet> palletList) {
		emptyTable();
		if (palletList != null && !palletList.isEmpty()) {
			for (Pallet p : palletList) {
				insertData(p);
			}
		}
	}

	private void insertData(Pallet p) {
		tableData.add(new SearchPalletDataHolder(p.getId(), p.getCookie(), p.getDelivered(), p.getCustomer(),
				p.getProduced()));
	}

}
