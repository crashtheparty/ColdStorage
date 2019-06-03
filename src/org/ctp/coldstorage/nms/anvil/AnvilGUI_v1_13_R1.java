package org.ctp.coldstorage.nms.anvil;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
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
import org.ctp.coldstorage.inventory.Anvilable;
import org.ctp.coldstorage.utils.ChatUtils;

import net.minecraft.server.v1_13_R1.BlockPosition;
import net.minecraft.server.v1_13_R1.ChatMessage;
import net.minecraft.server.v1_13_R1.ContainerAnvil;
import net.minecraft.server.v1_13_R1.EntityHuman;
import net.minecraft.server.v1_13_R1.EntityPlayer;
import net.minecraft.server.v1_13_R1.PacketPlayOutOpenWindow;

public class AnvilGUI_v1_13_R1 {
	private class AnvilContainer extends ContainerAnvil {
		public AnvilContainer(EntityHuman entity) {
			super(entity.inventory, entity.world, new BlockPosition(0, 0, 0), entity);
//			this.renameText = "Test";
		}

		@Override
		public boolean canUse(EntityHuman entityhuman) {
			return true;
		}
	}

	public enum AnvilSlot {
		INPUT_LEFT(0), INPUT_RIGHT(1), OUTPUT(2);

		private int slot;

		AnvilSlot(int slot) {
			this.slot = slot;
		}

		public int getSlot() {
			return slot;
		}

		public static AnvilSlot bySlot(int slot) {
			for(AnvilSlot anvilSlot: values()) {
				if (anvilSlot.getSlot() == slot) {
					return anvilSlot;
				}
			}

			return null;
		}
	}

	public class AnvilClickEvent {
		private AnvilSlot slot;

		private String name;

		private Anvilable anvil;

		private boolean close = true;
		private boolean destroy = true;
		private boolean choice = false;
		private Runnable runnable;

		public AnvilClickEvent(AnvilSlot slot, String name, Anvilable anvil, boolean choice) {
			this.slot = slot;
			this.name = name;
			this.anvil = anvil;
			this.choice = choice;
		}

		public AnvilSlot getSlot() {
			return slot;
		}

		public Anvilable getAnvil() {
			return anvil;
		}

		public String getName() {
			return name;
		}

		public boolean getWillClose() {
			return close;
		}

		public void setWillClose(boolean close) {
			this.close = close;
		}

		public boolean getWillDestroy() {
			return destroy;
		}

		public void setWillDestroy(boolean destroy) {
			this.destroy = destroy;
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

	public interface AnvilClickEventHandler {
		void onAnvilClick(AnvilClickEvent event);
	}

	private Player player;

	private AnvilClickEventHandler handler;

	private HashMap<AnvilSlot, ItemStack> items = new HashMap<>();

	private Inventory inv;

	private Listener listener;

	private Anvilable anvil;
	
	private boolean willClose = false, choice;

	public AnvilGUI_v1_13_R1(Player player, final AnvilClickEventHandler handler, Anvilable anvil, boolean choice) {
		this.player = player;
		this.setHandler(handler);
		this.setAnvil(anvil);
		this.setChoice(choice);

		this.listener = new Listener(){
			@EventHandler
			public void onInventoryClick(InventoryClickEvent event) {
				if (event.getWhoClicked() instanceof Player) {

					if (event.getInventory().equals(inv)) {
						event.setCancelled(true);
						ItemStack item = event.getCurrentItem();
						int slot = event.getRawSlot();
						String name = "";

						if (item != null) {
							if (item.hasItemMeta()) {
								ItemMeta meta = item.getItemMeta();

								if (meta.hasDisplayName()) {
									name = meta.getDisplayName();
								}
							}
						}
						AnvilClickEvent clickEvent = new AnvilClickEvent(AnvilSlot.bySlot(slot), name, anvil, choice);

						handler.onAnvilClick(clickEvent);
						
						if (clickEvent.getWillClose()) {
							willClose = true;
							event.getWhoClicked().closeInventory();
							if(clickEvent.getRunnable() != null) {
								Bukkit.getScheduler().runTaskLater(ColdStorage.getPlugin(), clickEvent.getRunnable(), 1l);
							} else {
								anvil.setInventory();
							}
						}
					}
				}
			}

			@EventHandler
			public void onInventoryClose(InventoryCloseEvent event) {
				if (event.getPlayer() instanceof Player) {
					Inventory inv = event.getInventory();

					if (inv.equals(AnvilGUI_v1_13_R1.this.inv)) {
						inv.clear();
						destroy();
						if(!willClose) {
							anvil.setInventory();
						}
					}
				}
			}

			@EventHandler
			public void onPlayerQuit(PlayerQuitEvent event) {
				if (event.getPlayer().equals(getPlayer())) {
					destroy();
				}
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

	public void open() {
		EntityPlayer p = ((CraftPlayer) player).getHandle();

		AnvilContainer container = new AnvilContainer(p);

		// Set the items to the items from the inventory given
		inv = container.getBukkitView().getTopInventory();

		for(AnvilSlot slot: items.keySet()) {
			inv.setItem(slot.getSlot(), items.get(slot));
		}
		if(choice) {
			ItemStack confirm = new ItemStack(Material.EMERALD_BLOCK);
			ItemMeta confirmMeta = confirm.getItemMeta();
			confirmMeta.setDisplayName(ChatUtils.getMessage(ChatUtils.getCodes(), "inventory.create_remove.confirm_delete"));
			confirm.setItemMeta(confirmMeta);
			ItemStack deny = new ItemStack(Material.REDSTONE_BLOCK);
			ItemMeta denyMeta = confirm.getItemMeta();
			denyMeta.setDisplayName(ChatUtils.getMessage(ChatUtils.getCodes(), "inventory.create_remove.deny_delete"));
			deny.setItemMeta(denyMeta);
			inv.setItem(0, confirm);
			inv.setItem(1, deny);
		} else {
			inv.setItem(0, new ItemStack(Material.NAME_TAG));
		}

		// Counter stuff that the game uses to keep track of inventories
		int c = p.nextContainerCounter();

		// Send the packet
		p.playerConnection
				.sendPacket(new PacketPlayOutOpenWindow(c, "minecraft:anvil", new ChatMessage("Repairing"), 0));
		// Set their active container to the container
		p.activeContainer = container;

		// Set their active container window id to that counter stuff
		p.activeContainer.windowId = c;

		// Add the slot listener
		p.activeContainer.addSlotListener(p);
	}

	public void destroy() {
		player = null;
		setHandler(null);
		items = null;

		HandlerList.unregisterAll(listener);

		listener = null;
	}

	public AnvilClickEventHandler getHandler() {
		return handler;
	}

	public void setHandler(AnvilClickEventHandler handler) {
		this.handler = handler;
	}

	public Anvilable getAnvil() {
		return anvil;
	}

	public void setAnvil(Anvilable anvil) {
		this.anvil = anvil;
	}

	public static void createAnvil(Player player, Anvilable anvil, boolean choice) {
		AnvilClickEventHandler handler = new AnvilClickEventHandler(){
			public void onAnvilClick(AnvilClickEvent event) {
				if(event.isChoice()) {
					String c = "";
					if(event.getSlot() != null) {
						if(event.getSlot().slot == 0) {
							c = "confirm";
						} else if (event.getSlot().slot == 1) {
							c = "deny";
						}
					}
					if(c.equals("")) {
						event.setWillClose(false);
						event.setWillDestroy(false);
						return;
					}
					final String choice = c;
					event.setRunnable(new Runnable() {
						@Override
						public void run() {
							event.getAnvil().setChoice(choice);
						}
					});
				} else {
					if (event.getSlot() == null) {
						event.setWillClose(false);
						event.setWillDestroy(false);
						return;
					}
					if (event.getSlot().getSlot() != 2) {
						event.setWillClose(false);
						event.setWillDestroy(false);
						return;
					}
					if(event.getName().equals("")) {
						event.setWillClose(false);
						event.setWillDestroy(false);
						return;
					}
					event.setRunnable(new Runnable() {
						
						@Override
						public void run() {
							event.getAnvil().setItemName(event.getName());
						}
					});
				}
			}
		};
		AnvilGUI_v1_13_R1 gui = new AnvilGUI_v1_13_R1(player, handler, anvil, choice);
		gui.open();
	}

	public boolean isChoice() {
		return choice;
	}

	public void setChoice(boolean choice) {
		this.choice = choice;
	}

}
