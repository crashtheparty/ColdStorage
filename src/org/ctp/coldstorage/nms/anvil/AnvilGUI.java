package org.ctp.coldstorage.nms.anvil;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.crashapi.inventory.InventoryData;
import org.ctp.crashapi.nms.anvil.AnvilSlot;
import org.ctp.crashapi.utils.LocationUtils;

public abstract class AnvilGUI {

	private Player player;

	private CSAnvilClickEventHandler handler;

	private HashMap<AnvilSlot, ItemStack> items = new HashMap<>();

	private Inventory inv;

	private Listener listener;

	private InventoryData data;

	private boolean choice;

	public AnvilGUI(Player player, final CSAnvilClickEventHandler handler, InventoryData data, boolean choice) {
		this.player = player;
		setHandler(handler);
		setData(data);
		setChoice(choice);

		this.listener = new Listener() {
			@EventHandler
			public void onInventoryClick(InventoryClickEvent event) {
				if (event.getWhoClicked() instanceof Player) if (event.getInventory().equals(inv)) {
					event.setCancelled(true);
					ItemStack item = event.getCurrentItem();
					int slot = event.getRawSlot();
					String name = "";

					if (item != null) if (item.hasItemMeta()) {
						ItemMeta meta = item.getItemMeta();

						if (meta.hasDisplayName()) name = meta.getDisplayName();
					}
					CSAnvilClickEvent clickEvent = new CSAnvilClickEvent(AnvilSlot.bySlot(slot), name, data, choice);

					handler.onAnvilClick(clickEvent);

					if (clickEvent.getWillClose()) {
						event.getWhoClicked().closeInventory();
						data.setInventory();
					}

					if (clickEvent.getWillDestroy()) {
						LocationUtils.checkAnvilBreak(player, data.getBlock(), data, false);
						destroy();
					}
				}
			}

			@EventHandler
			public void onInventoryClose(InventoryCloseEvent event) {
				if (event.getPlayer() instanceof Player) {
					Inventory inv = event.getInventory();

					if (inv.equals(AnvilGUI.this.inv)) {
						inv.clear();
						destroy();
						Bukkit.getScheduler().scheduleSyncDelayedTask(ColdStorage.getPlugin(), () -> data.setInventory(data.getItems()), 2l);
					}
				}
			}

			@EventHandler
			public void onPlayerQuit(PlayerQuitEvent event) {
				if (event.getPlayer().equals(getPlayer())) destroy();
			}
		};

		Bukkit.getPluginManager().registerEvents(listener, ColdStorage.getPlugin());
	}

	public Player getPlayer() {
		return player;
	}

	public void setSlot(AnvilSlot slot, ItemStack item) {
		items.put(slot, item);
	}

	public abstract void open();

	public void destroy() {
		player = null;
		setHandler(null);
		items = null;

		HandlerList.unregisterAll(listener);

		listener = null;
	}

	public CSAnvilClickEventHandler getHandler() {
		return handler;
	}

	public void setHandler(CSAnvilClickEventHandler handler) {
		this.handler = handler;
	}

	public InventoryData getData() {
		return data;
	}

	public void setData(InventoryData anvil) {
		data = anvil;
	}

	public void setInventory(Inventory inv) {
		this.inv = inv;
	}

	protected ItemStack getItemStack() {
		return new ItemStack(Material.NAME_TAG);
	}

	public boolean isChoice() {
		return choice;
	}

	public void setChoice(boolean choice) {
		this.choice = choice;
	}
}