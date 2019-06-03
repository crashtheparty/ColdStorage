package org.ctp.coldstorage.inventory;

public interface Anvilable {

	public boolean isEditing();
	
	public void setEditing(boolean editing);
	
	public void setItemName(String name);
	
	public void setInventory();
	
	public void setChoice(String choice);
	
	public boolean isChoice();
	
	public void close(boolean external);
}
