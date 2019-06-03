package org.ctp.coldstorage.version;

public class Version {

	private String versionName;
	private VersionType type;
	
	public Version(String name, VersionType type) {
		versionName = name;
		this.setType(type);
	}
	
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	
	public VersionType getType() {
		return type;
	}

	public void setType(VersionType type) {
		this.type = type;
	}
}
