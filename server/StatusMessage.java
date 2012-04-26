public class StatusMessage {

	private String Status;
	
	public StatusMessage() {
		// TODO Auto-generated constructor stub
	}
	
	public StatusMessage(String _Status) {
		this.Status = _Status;
		
	}
	public String getStatus() {
		return Status;
	}

	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Status Message Details - ");
		sb.append("Status:" + getStatus());
		sb.append(".");
		
		return sb.toString();
	}
}
