package org.ctp.coldstorage.version;

public enum VersionType{
	LIVE("live"), EXPERIMENTAL("experimental"), UPCOMING("upcoming"), UNKNOWN(null);
	
	private String type;
	
	VersionType(String type) {
		this.setType(type);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
