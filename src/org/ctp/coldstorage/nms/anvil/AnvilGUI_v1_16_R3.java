package org.ctp.coldstorage.nms.anvil;

import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiFunction;

import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.ctp.crashapi.inventory.InventoryData;
import org.ctp.crashapi.nms.anvil.AnvilSlot;

import net.minecraft.server.v1_16_R3.*;

public class AnvilGUI_v1_16_R3 extends AnvilGUI {
	private class AnvilContainer extends ContainerAnvil {
		public AnvilContainer(EntityHuman entity, int windowId) {
			super(windowId, entity.inventory, at(entity.world, new BlockPosition(0, 0, 0)));
		}

		@Override
		public boolean canUse(EntityHuman entityhuman) {
			return true;
		}
	}

	private HashMap<AnvilSlot, ItemStack> items = new HashMap<>();

	public AnvilGUI_v1_16_R3(Player player, final CSAnvilClickEventHandler handler, InventoryData data, boolean choice) {
		super(player, handler, data, choice);
	}

	@Override
	public void setSlot(AnvilSlot slot, ItemStack item) {
		items.put(slot, item);
	}

	@Override
	public void open() {
		EntityPlayer p = ((CraftPlayer) getPlayer()).getHandle();

		// Counter stuff that the game uses to keep track of inventories
		int c = p.nextContainerCounter();

		AnvilContainer container = new AnvilContainer(p, c);

		// Set the items to the items from the inventory given
		Inventory inv = container.getBukkitView().getTopInventory();

		for(AnvilSlot slot: items.keySet())
			inv.setItem(slot.getSlot(), items.get(slot));

		inv.setItem(0, getItemStack());

		setInventory(inv);

		// Send the packet
		p.playerConnection.sendPacket(new PacketPlayOutOpenWindow(c, container.getType(), new ChatMessage("Repairing")));
		// Set their active container to the container
		p.activeContainer = container;

		// Add the slot listener
		p.activeContainer.addSlotListener(p);
	}

	public static void createAnvil(Player player, InventoryData data, boolean choice) {
		CSAnvilClickEventHandler handler = CSAnvilClickEventHandler.getHandler(player, data);
		AnvilGUI_v1_16_R3 gui = new AnvilGUI_v1_16_R3(player, handler, data, choice);
		gui.open();
	}

	static ContainerAccess at(final World world, final BlockPosition blockposition) {
		return new ContainerAccess() {
			// CraftBukkit start
			@Override
			public World getWorld() {
				return world;
			}

			@Override
			public BlockPosition getPosition() {
				return blockposition;
			}
			// CraftBukkit end

			@Override
			public <T> Optional<T> a(BiFunction<World, BlockPosition, T> bifunction) {
				return Optional.of(bifunction.apply(world, blockposition));
			}
		};
	}

}
