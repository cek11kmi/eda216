
import javafx.beans.property.SimpleStringProperty;

public class BlockPalletDataHolder {

		private final SimpleStringProperty palletId;
		private final SimpleStringProperty productName;

			private final SimpleStringProperty productionDate;
			private final SimpleStringProperty blocked;

			
			public BlockPalletDataHolder(String palletId, String productName, String productionDate, String blocked) {
				this.palletId = new SimpleStringProperty(palletId);
				this.productName = new SimpleStringProperty(productName);
				this.productionDate = new SimpleStringProperty(productionDate);
				this.blocked = new SimpleStringProperty(blocked);
			}
			
			// GET
			public String getPalletId() { return palletId.get();}
			public String getProductName() { return productName.get();}
			public String getProductionDate() { return productionDate.get();}
			public String getBlocked() { return blocked.get();}

			
			// SET
			public void setPalletId(String value) { palletId.set(value);}
			public void setProductName(String value) { productName.set(value);}
			public void setProductionDate(String value) { productionDate.set(value);}
			public void setBlocked(String value) { blocked.set(value);}
			
}


