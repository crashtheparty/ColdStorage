package org.ctp.coldstorage.nms.anvil;

import org.bukkit.entity.Player;
import org.ctp.crashapi.inventory.InventoryData;
import org.ctp.crashapi.nms.anvil.AnvilClickEventHandler;

public interface CSAnvilClickEventHandler extends AnvilClickEventHandler {
	public static CSAnvilClickEventHandler getHandler(Player player, InventoryData data) {
		return event -> {
			if (event instanceof CSAnvilClickEvent) {
				CSAnvilClickEvent csEvent = (CSAnvilClickEvent) event;
				if (csEvent.isChoice()) {
					String c = "";
					if (csEvent.getSlot() != null) if (csEvent.getSlot().getSlot() == 0) c = "confirm";
					else if (csEvent.getSlot().getSlot() == 1) c = "deny";
					if (c.equals("")) {
						csEvent.setWillClose(false);
						csEvent.setWillDestroy(false);
						return;
					}
					final String choice = c;
					csEvent.setRunnable(new Runnable() {
						@Override
						public void run() {
							csEvent.getData().setItemName(choice);
						}
					});
				} else {
					if (csEvent.getSlot() == null) {
						csEvent.setWillClose(false);
						csEvent.setWillDestroy(false);
						return;
					}
					if (csEvent.getSlot().getSlot() != 2) {
						csEvent.setWillClose(false);
						csEvent.setWillDestroy(false);
						return;
					}
					if (csEvent.getName().equals("")) {
						csEvent.setWillClose(false);
						csEvent.setWillDestroy(false);
						return;
					}
					csEvent.setRunnable(new Runnable() {

						@Override
						public void run() {
							csEvent.getData().setItemName(event.getName());
						}
					});
				}
			}
		};
	}
}
