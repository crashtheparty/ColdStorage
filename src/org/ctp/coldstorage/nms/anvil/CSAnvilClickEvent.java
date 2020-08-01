package org.ctp.coldstorage.nms.anvil;

import org.ctp.crashapi.inventory.InventoryData;
import org.ctp.crashapi.nms.anvil.AnvilClickEvent;
import org.ctp.crashapi.nms.anvil.AnvilSlot;

public class CSAnvilClickEvent extends AnvilClickEvent {
	private boolean choice = false;
	private Runnable runnable;

	public CSAnvilClickEvent(AnvilSlot slot, String name, InventoryData anvil, boolean choice) {
		super(slot, name, anvil);
		this.choice = choice;
	}

	public Runnable getRunnable() {
		return runnable;
	}

	public void setRunnable(Runnable runnable) {
		this.runnable = runnable;
	}

	public boolean isChoice() {
		return choice;
	}

	public void setChoice(boolean choice) {
		this.choice = choice;
	}
}