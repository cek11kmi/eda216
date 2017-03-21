
public class Pallet {
	private String id;
	private String cookie;
	private String delivered;
	private String customer;
	private String produced;
	private boolean blocked;

	public Pallet(String id, String cookie, String delivered, String customer, String produced, boolean blocked) {
		this.id = id;
		this.cookie = cookie;
		this.delivered = delivered;
		this.customer = customer;
		this.produced = produced;
		this.blocked = blocked;
	}

	public String getId() {
		return id;
	}

	public String getCookie() {
		return cookie;
	}

	public String getDelivered() {
		return delivered;
	}

	public String getCustomer() {
		return customer;
	}

	public String getProduced() {
		return produced;
	}

	public boolean getBlocked() {
		return blocked;
	}
}
