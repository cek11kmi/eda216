
public class DeliveredPallets {

	private String palletId;
	private String datetime;
	
	public DeliveredPallets(String palletId, String datetime){
		this.palletId = palletId;
		this.datetime = datetime;
	}
	
	public String getPalletId(){
		return palletId;
	}
	
	public String getDateTime(){
		return datetime;
	}
}
