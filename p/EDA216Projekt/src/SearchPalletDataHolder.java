import javafx.beans.property.SimpleStringProperty;

public class SearchPalletDataHolder {

		private final SimpleStringProperty palletId;
		private final SimpleStringProperty productName;
		private final SimpleStringProperty deliveredDate;
		private final SimpleStringProperty customer;
		private final SimpleStringProperty productionDate;

		
		public SearchPalletDataHolder(String palletId, String productName, String deliveredDate, String customer, String productionDate) {
			this.palletId = new SimpleStringProperty(palletId);
			this.productName = new SimpleStringProperty(productName);
			this.deliveredDate = new SimpleStringProperty(deliveredDate);
			this.customer = new SimpleStringProperty(customer);
			this.productionDate = new SimpleStringProperty(productionDate);
		}
		
		// GET
		public String getPalletId() { return palletId.get();}
		public String getProductName() { return productName.get();}
		public String getDeliverDate() { return deliveredDate.get();}
		public String getCustomer() { return customer.get();}
		public String getProductionDate() { return productionDate.get();}

		
		// SET
		public void setPalletId(String value) { palletId.set(value);}
		public void setProductName(String value) { productName.set(value);}
		public void setDeliveredDate(String value) { deliveredDate.set(value);}
		public void setCustomer(String value) { customer.set(value);}
		public void setProductionDate(String value) { productionDate.set(value);}
		
	}

