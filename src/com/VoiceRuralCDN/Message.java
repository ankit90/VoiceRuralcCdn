package com.VoiceRuralCDN;

public class Message {

	private int size;
	
	private String fileName;

	private String Title;
	
	private String Desc;

	private String Tags;

	private String Time_stamp;
	
	private String Conference_stamp;
	
	public Message(){
		
	}
	
	public Message(int size, String _fileName, String _Title, String _Desc, String _Tags, String _Time_stamp, String _Conference_stamp) {
		this.size = size;
		this.fileName = _fileName;
		this.Title = _Title;
		this.Desc = _Desc;
		this.Tags  = _Tags;
		this.Time_stamp = _Time_stamp;
		this.Conference_stamp = _Conference_stamp;
		
	}
	
	public int getsize() {
		return size;
	}

	public void setsize(int _size) {
		this.size = _size;
	}
	
	public String getfileName() {
		return fileName;
	}

	public void setfileName(String _fileName) {
		this.fileName = _fileName;
	}
	
	public String getTitle() {
		return Title;
	}

	public void setTitle(String _Title) {
		this.Title = _Title;
	}

	public String getDesc() {
		return Desc;
	}

	public void setDesc(String _Desc) {
		this.Desc = _Desc;
	}
	
	public String getTags() {
		return Tags;
	}

	public void setTags(String _Tags) {
		this.Tags = _Tags;
	}
	
	public void appendTags(String _Tags) {
		this.Tags = this.Tags + _Tags;
	}
	
	public String getTime_stamp() {
		return Time_stamp;
	}

	public void setTime_stamp(String _Time_stamp) {
		this.Time_stamp = _Time_stamp;
	}
	
	public String getConference_stamp() {
		return Conference_stamp;
	}

	public void setConference_stamp(String _Conference_stamp) {
		this.Conference_stamp = _Conference_stamp;
	}
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Message Details - ");
		if (getsize() == 1)
		sb.append("size>5MB");
		else sb.append("size<5MB");
		sb.append(", ");
		sb.append("Title:" + getTitle());
		sb.append(", ");
		sb.append("Desc:" + getDesc());
		sb.append(", ");
		sb.append("Tags:" + getTags());
		sb.append(", ");
		sb.append("Time_stamp:" + getTime_stamp().toString());
		sb.append(", ");
		sb.append("Conference_stamp:" + getConference_stamp().toString());
		sb.append(".");
		
		return sb.toString();
	}
}
