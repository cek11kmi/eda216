import java.sql.SQLException;

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


public class TabBlockPallet {
	// Layout holder
	public HBox hBox;

	// Main components

	
	private TextField time1TF;
	private TextField time2TF;
	private TextField productTF;
	private Label invalidProductMessage;
	private Label invalidTimeMessage;
	private Button blockPalletsByTimeAndCookie;



	private TableView<BlockPalletDataHolder> table;
	private final ObservableList<BlockPalletDataHolder> tableData = FXCollections
			.observableArrayList(

	);

	// External objects
	private Database db;


	public TabBlockPallet(int gap, int padding, Database db) {
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
		

		// Vertical box (Is put in the left in hBox, contains "grid" and
		// "invalidInputMessage"-label)
		VBox vBox1 = new VBox();
		vBox1.setSpacing(gap);

		// Vertical box (vBox1) components
		
		Label time = new Label("Block pallets by time and cookie name");
		Label timeFrom = new Label("From:");
		Label timeTo = new Label("To:");
		
		time1TF = new TextField();
		time1TF.setPromptText("E.g. 2017-03-28 12:24:55");
		
		time2TF = new TextField();
		time2TF.setPromptText("E.g. 2017-03-28 12:24:55");
		
		
		Label product = new Label("Cookie name");
		
		productTF = new TextField();
		productTF.setPromptText("E.g. Nut ring");
		
		grid.add(time, 0, 0);
		grid.add(timeFrom, 0, 1);
		grid.add(time1TF, 0, 2);
		
		grid.add(timeTo, 0, 3);
		grid.add(time2TF, 0, 4);
		
		
		invalidTimeMessage = new Label("");
		invalidTimeMessage.setTextFill(Color.RED);
		invalidTimeMessage.setWrapText(true);
		invalidProductMessage = new Label("");
		invalidProductMessage.setTextFill(Color.RED);
		invalidProductMessage.setWrapText(true);
		
		
		grid.add(product, 0, 5);
		grid.add(productTF, 0, 6);
		grid.add(invalidTimeMessage, 0, 7);
		grid.add(invalidProductMessage, 0, 8);
		
		// Add vertical box (vBox1) components

		// Vertical box (Is put to the right in hBox, contains a horizontal box)
		VBox vBox2 = new VBox();
		vBox2.setAlignment(Pos.TOP_RIGHT);

		// Create pallet

		
		blockPalletsByTimeAndCookie = new Button("Block");
		blockPalletsByTimeAndCookie.setOnAction(e -> blockPalletsByTimeAndCookie());

		grid.add(blockPalletsByTimeAndCookie, 1, 6);
		vBox1.getChildren().add(grid);
		
		
		
		// Add major component holders (vBox1 and vBox2) to the horizontal box
		// (hBox)
		hBox.getChildren().add(vBox1);
		
		
		table = new TableView<BlockPalletDataHolder>();
		table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Removes
																					// unspecified
																					// empty
																					// column
																					// inuserTable
		table.setItems(tableData);

		TableColumn<BlockPalletDataHolder, String> palletIdColumn = new TableColumn<BlockPalletDataHolder, String>(
				"ID");
		palletIdColumn
				.setCellValueFactory(new PropertyValueFactory<BlockPalletDataHolder, String>("palletId"));
		palletIdColumn.setMinWidth(30);
		palletIdColumn.setMaxWidth(30);
		TableColumn<BlockPalletDataHolder, String> productNameColumn = new TableColumn<BlockPalletDataHolder, String>(
				"Cookie");
		productNameColumn
				.setCellValueFactory(new PropertyValueFactory<BlockPalletDataHolder, String>("productName"));

		TableColumn<BlockPalletDataHolder, String> productionDateColumn = new TableColumn<BlockPalletDataHolder, String>(
				"Produced");
		productionDateColumn
				.setCellValueFactory(new PropertyValueFactory<BlockPalletDataHolder, String>("productionDate"));
		
		TableColumn<BlockPalletDataHolder, String> blockedColumn = new TableColumn<BlockPalletDataHolder, String>(
				"Blocked");
		blockedColumn
				.setCellValueFactory(new PropertyValueFactory<BlockPalletDataHolder, String>("blocked"));

		

		table.getColumns().addAll(palletIdColumn);
		table.getColumns().addAll(productNameColumn);
		table.getColumns().addAll(productionDateColumn);
		table.getColumns().addAll(blockedColumn);


		vBox2.getChildren().add(table);
		
		hBox.getChildren().add(vBox2);
		HBox.setHgrow(vBox2, Priority.ALWAYS);
		

	}

	
	private void blockPalletsByTimeAndCookie(){
		String start = time1TF.getText();
		String end = time2TF.getText();
		String product = productTF.getText();
		
		if (start.length() == 19 && end.length() == 19) {
			try {
				if (db.getCookies().contains(product)){
					if (db.blockPalletsByTimeAndCookie(start, end, product)){
						clearTextFields();
						restoreInvalidInputs();
						addAllBlockedPalletsToTable();
					} else {
						//invalidBlockMessage();
						System.out.println("Fanns inget att blocka");
					}
				} else {
					invalidProductInput();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			invalidTimeInput();
		}

		
		
	}

	private void invalidProductInput() {
		// Mark as YELLOW
		productTF.setStyle("-fx-background-color: #ffff0052");
		addInvalidProductInputMessage(productTF.getText() + " is not a valid cookie name");
	}

	private void invalidTimeInput() {
		//Mark as YELLOW
		time1TF.setStyle("-fx-background-color: #ffff0052");
		time2TF.setStyle("-fx-background-color: #ffff0052");
		addInvalidTimeInputMessage(time1TF.getText() + " and/or " + time2TF.getText() + " not valid");
	}
//	
	public void restoreInvalidInputs() {
		// Restore text field colors
		productTF.setStyle("");
		time1TF.setStyle("");
		time2TF.setStyle("");

		// Set the error message label text to an "empty" string
		invalidProductMessage.setText("");
		invalidTimeMessage.setText("");
	}

	private void addInvalidProductInputMessage(String message) {
		if (invalidProductMessage.getText().length() == 0) {
			invalidProductMessage.setText(invalidProductMessage.getText() + message);
		} else {
			invalidProductMessage.setText(invalidProductMessage.getText() + ", " + message);
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
		productTF.setText("");
		time1TF.setText("");
		time2TF.setText("");
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
	
	public void addAllBlockedPalletsToTable(){
		emptyTable();
		List<Pallet> palletList;
		try {
			palletList = db.getAllBlockedPallets();
			if (palletList.size() != 0){
				for (Pallet p : palletList){
					insertData(p);
				}
			} else {
				System.out.println("No blocked pallets");
			}
			table.refresh();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	private void insertData(Pallet p) {
		tableData.add(new BlockPalletDataHolder(p.getId(),p.getCookie(),p.getProduced(), p.getBlocked()));
	}

}


