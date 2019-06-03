package org.ctp.coldstorage.permissions;

public class Permission {

	private String permission;
	private int checkOrder, numStorages;
	
	public Permission(String permission, int checkOrder, int numStorages) {
		this.permission = permission;
		this.checkOrder = checkOrder;
		this.numStorages = numStorages;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

	public int getCheckOrder() {
		return checkOrder;
	}

	public void setCheckOrder(int checkOrder) {
		this.checkOrder = checkOrder;
	}

	public int getNumStorages() {
		return numStorages;
	}

	public void setNumStorages(int numStorages) {
		this.numStorages = numStorages;
	}
}
