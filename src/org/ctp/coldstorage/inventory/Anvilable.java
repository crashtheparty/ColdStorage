package org.ctp.coldstorage.inventory;

public interface Anvilable {

	public boolean willEdit();
	
	public void setEdit(boolean edit);
	
	public void setItemName(String name);
	
	public void setInventory();
	
	public void setChoice(String choice);
	
	public boolean isChoice();
	
	public void close(boolean external);
}
