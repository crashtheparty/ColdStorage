package org.ctp.coldstorage.utils.config;

import java.io.File;
import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.ctp.coldstorage.ColdStorage;
import org.ctp.coldstorage.utils.ChatUtils;
import org.ctp.coldstorage.utils.yamlconfig.YamlConfig;

public class LanguageFiles {

	private File languageFile, englishUSFile;
	private YamlConfig englishUS, language;
	private Configuration configuration;

	public LanguageFiles(Configuration configuration) {
		this.configuration = configuration;
		languageFile = new File(
				configuration.getDataFolder() + "/" + configuration.getMainConfig().getString("language_file"));
		createDefaultFiles();

		YamlConfig main = configuration.getMainConfig();
		boolean getFromConfig = true;
		if (main.getBoolean("reset_language")) {
			getFromConfig = false;
			main.set("reset_language", false);
			main.saveConfig();
		}

		save(getFromConfig);
	}

	private void save(boolean getFromConfig) {
		if(ColdStorage.getPlugin().isInitializing()) {
			ChatUtils.sendInfo("Loading language file...");
		}
		language = new YamlConfig(languageFile, null);
		if (getFromConfig) {
			language.getFromConfig();
		}
		language.copyDefaults(getLanguageFile());

		language.saveConfig();
		if(ColdStorage.getPlugin().isInitializing()) {
			ChatUtils.sendInfo("Language file initialized!");
		}
	}

	public YamlConfig getLanguageConfig() {
		return language;
	}

	public void setLanguageConfig(YamlConfig language) {
		this.language = language;
	}

	public void updateLanguage() {
		languageFile = new File(
				configuration.getDataFolder() + "/" + configuration.getMainConfig().getString("language_file"));
		createDefaultFiles();
		YamlConfig main = configuration.getMainConfig();
		boolean getFromConfig = true;
		if (main.getBoolean("reset_language")) {
			getFromConfig = false;
			main.set("reset_language", false);
			main.saveConfig();
		}
		save(getFromConfig);
	}

	private YamlConfig getLanguageFile() {
		switch (configuration.getLanguage()) {
		case US:
			return englishUS;
		}
		return englishUS;
	}

	private void createDefaultFiles() {
		try {
			File langs = new File(configuration.getDataFolder() + "/languages/");
			if (!langs.exists()) {
				langs.mkdirs();
			}
			englishUSFile = new File(configuration.getDataFolder() + "/languages/en_us.yml");
			YamlConfiguration.loadConfiguration(englishUSFile);

			defaultenglishUSFile();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void defaultenglishUSFile() {
		if (englishUS == null)
			englishUS = new YamlConfig(englishUSFile, new String[0]);

		englishUS.addDefault("coldstorage.starter", (ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "ColdStorage" 
				+ ChatColor.DARK_GRAY + "] " + ChatColor.WHITE).replace("§", "&"));
		
		englishUS.addDefault("commands.admin", "Opening admin inventory...");
		englishUS.addDefault("commands.open", "Opening Cold Storage...");
		englishUS.addDefault("commands.add_chest", "Right-click on a chest to allow importing/exporting items. Break chest to remove it.");
		englishUS.addDefault("commands.no_permission", "You do not have permission to use this command!");

		englishUS.addDefault("database.issue",
				"Issue with the plugin. Please contact an administrator to get this resolved.");

		englishUS.addDefault("exceptions.number_format",
				(ChatColor.RED + "String %num% is not a number.").replace("§", "&"));
		englishUS.addDefault("exceptions.number_too_low",
				(ChatColor.RED
						+ "Number %num% is too low for this value: lowest is %lowest%. Setting value to %lowest%.")
								.replace("§", "&"));
		englishUS.addDefault("exceptions.option_nonexistent",
				(ChatColor.RED + "Option %option% does not exist in %area%.").replace("§", "&"));
		englishUS.addDefault("exceptions.missing_storage_type",
				(ChatColor.RED + "There was an issue opening storage type - does not exist??").replace("§", "&"));
		englishUS.addDefault("exceptions.invalid_storage_type",
				(ChatColor.RED + "Invalid storage type!").replace("§", "&"));
		englishUS.addDefault("exceptions.invalid_permission_string.1",
				(ChatColor.RED + "Permission string \"%name%\" is not a valid permission string!").replace("§", "&"));
		englishUS.addDefault("exceptions.invalid_permission_string.2",
				(ChatColor.RED + "May only contain letters, numbers, dashes, and underscores.").replace("§", "&"));
		englishUS.addDefault("exceptions.permission_exists",
				(ChatColor.RED + "Permission with name %permission% already exists!").replace("§", "&"));
		englishUS.addDefault("exceptions.missing_permission",
				(ChatColor.RED + "Permission does not exist!").replace("§", "&"));
		englishUS.addDefault("exceptions.missing_draft",
				(ChatColor.RED + "There was an issue opening draft - does not exist??").replace("§", "&"));
		englishUS.addDefault("exceptions.missing_storage",
				(ChatColor.RED + "There was an issue opening storage - does not exist??").replace("§", "&"));
		englishUS.addDefault("exceptions.missing_money",
				(ChatColor.RED + "You do not have enough money to buy this: Need %money%.").replace("§", "&"));
		englishUS.addDefault("exceptions.incompatible_material",
				(ChatColor.RED + "%clicked_material% and %material% does not match!").replace("§", "&"));
		englishUS.addDefault("exceptions.incompatible_item", (ChatColor.RED + "The item metadata does not match!").replace("§", "&"));
		englishUS.addDefault("exceptions.bad_storage_type",
				(ChatColor.RED + "Storage Type %storage_type% doesn't exist - cannot insert new items.").replace("§", "&"));
		englishUS.addDefault("exceptions.item_limit_reached",
				(ChatColor.RED + "Item limit reached!").replace("§", "&"));
		englishUS.addDefault("exceptions.permissions_not_updated",
				(ChatColor.RED + "Issues removing permissions from storage types - try again or send an issue to the plugin creator.").replace("§", "&"));
		englishUS.addDefault("exceptions.invalid_block_break", (ChatColor.RED + "Cannot break an already set chest!").replace("§", "&"));
		englishUS.addDefault("exceptions.chest_exists", (ChatColor.RED + "Chest already exists in the database!").replace("§", "&"));
		englishUS.addDefault("exceptions.no_export", (ChatColor.RED + "This storage can't use export chests!").replace("§", "&"));
		englishUS.addDefault("exceptions.no_import", (ChatColor.RED + "This storage can't use import chests!").replace("§", "&"));
		englishUS.addDefault("exceptions.too_many_export", (ChatColor.RED + "Too many export chests already!").replace("§", "&"));
		englishUS.addDefault("exceptions.too_many_import", (ChatColor.RED + "Too many import chests already!").replace("§", "&"));
		englishUS.addDefault("exceptions.chest_in_use", (ChatColor.RED + "You can't break a chest that's in use!").replace("§", "&"));
		englishUS.addDefault("exceptions.cannot_modify_yourself", (ChatColor.RED + "Cannot modify your own storages!").replace("§", "&"));
		englishUS.addDefault("exceptions.missing_player", (ChatColor.RED + "Player does not exist!").replace("§", "&"));
		englishUS.addDefault("exceptions.not_your_chest", (ChatColor.RED + "You cannot delete someone else's chests!").replace("§", "&"));
		englishUS.addDefault("exceptions.chest_delete", (ChatColor.RED + "Issue with deleting chest, please talk to an admin.").replace("§", "&"));
		englishUS.addDefault("exceptions.has_chest_types", (ChatColor.RED + "Cannot delete a chest that is currently in use!").replace("§", "&"));
		englishUS.addDefault("exceptions.interact_event_cancelled", (ChatColor.RED + "Cannot claim this chest: event cancelled!").replace("§", "&"));
		
		englishUS.addDefault("info.true", "true");
		englishUS.addDefault("info.false", "false");
		englishUS.addDefault("info.free", "Free");
		englishUS.addDefault("info.unknown", "Unknown");
		englishUS.addDefault("info.bad_material", "Bad Material - Disabled");

		englishUS.addDefault("inventory.click.item_set", "Set to %item%.");
		englishUS.addDefault("inventory.click.canceled", "Canceled.");
		englishUS.addDefault("inventory.click.storage_item", "Select an item from your inventory to set storage item, or click the emerald to cancel.");
		englishUS.addDefault("inventory.click.item_cost", "Select an item from your inventory to set item cost, or click the emerald to cancel.");
		
		englishUS.addDefault("inventory.pagination.go_back", (ChatColor.LIGHT_PURPLE + "Go Back").replace("§", "&"));
		englishUS.addDefault("inventory.pagination.next_page", (ChatColor.YELLOW + "Next Page").replace("§", "&"));
		englishUS.addDefault("inventory.pagination.previous_page",
				(ChatColor.YELLOW + "Previous Page").replace("§", "&"));

		englishUS.addDefault("inventory.create_remove.delete_type",
				(ChatColor.DARK_RED + "Delete Type").replace("§", "&"));
		englishUS.addDefault("inventory.create_remove.delete", (ChatColor.DARK_RED + "Delete").replace("§", "&"));
		englishUS.addDefault("inventory.create_remove.confirm_delete",
				(ChatColor.GREEN + "Yes, Delete This").replace("§", "&"));
		englishUS.addDefault("inventory.create_remove.deny_delete",
				(ChatColor.RED + "No, Don't Delete").replace("§", "&"));
		englishUS.addDefault("inventory.create_remove.buy_cold_storage",
				(ChatColor.DARK_GREEN + "Create New Draft").replace("§", "&"));
		englishUS.addDefault("inventory.create_remove.create_draft",
				(ChatColor.DARK_GREEN + "Buy Cold Storage").replace("§", "&"));
		englishUS.addDefault("inventory.create_remove.create_storage_type",
				(ChatColor.DARK_GREEN + "Create New Storage Type").replace("§", "&"));
		englishUS.addDefault("inventory.create_remove.create_permission",
				(ChatColor.DARK_GREEN + "Create New Permission").replace("§", "&"));
		englishUS.addDefault("inventory.create_remove.create_global_permission",
				(ChatColor.DARK_GREEN + "Create New Global Permission").replace("§", "&"));
		englishUS.addDefault("inventory.create_remove.remove_storage",
				(ChatColor.DARK_RED + "Delete Storage").replace("§", "&"));
		englishUS.addDefault("inventory.create_remove.delete_draft",
				(ChatColor.DARK_RED + "Delete Draft").replace("§", "&"));

		englishUS.addDefault("inventory.adminlist.title", "Admin Functions");
		englishUS.addDefault("inventory.adminlist.modify_permissions",
				(ChatColor.DARK_PURPLE + "Modify Permissions").replace("§", "&"));
		englishUS.addDefault("inventory.adminlist.modify_global_permissions",
				(ChatColor.DARK_PURPLE + "Modify Global Permissions").replace("§", "&"));
		englishUS.addDefault("inventory.adminlist.modify_players",
				(ChatColor.DARK_PURPLE + "Modify Players").replace("§", "&"));
		englishUS.addDefault("inventory.adminlist.modify_storage_types",
				(ChatColor.DARK_PURPLE + "Modify Storage Types").replace("§", "&"));

		englishUS.addDefault("inventory.editstoragetype.title", "Edit Storage Type");
		englishUS.addDefault("inventory.editstoragetype.name", (ChatColor.BLUE + "%name%").replace("§", "&"));
		englishUS.addDefault("inventory.editstoragetype.import", (ChatColor.BLUE + "Import Items").replace("§", "&"));
		englishUS.addDefault("inventory.editstoragetype.max_import",
				Arrays.asList(
						(ChatColor.GOLD + "Max Import: " + ChatColor.DARK_AQUA + "%max_import%").replace("§", "&"),
						(ChatColor.WHITE + "Click to edit.").replace("§", "&")));
		englishUS.addDefault("inventory.editstoragetype.export", (ChatColor.BLUE + "Export Items").replace("§", "&"));
		englishUS.addDefault("inventory.editstoragetype.max_export",
				Arrays.asList(
						(ChatColor.GOLD + "Max Export: " + ChatColor.DARK_AQUA + "%max_export%").replace("§", "&"),
						(ChatColor.WHITE + "Click to edit.").replace("§", "&")));
		englishUS.addDefault("inventory.editstoragetype.vault_cost", (ChatColor.BLUE + "Vault Cost").replace("§", "&"));
		englishUS.addDefault("inventory.editstoragetype.get_vault_cost",
				Arrays.asList((ChatColor.GOLD + "Cost: " + ChatColor.DARK_AQUA + "%vault_cost%").replace("§", "&"),
						(ChatColor.WHITE + "Click to edit.").replace("§", "&")));
		englishUS.addDefault("inventory.editstoragetype.item_cost", (ChatColor.BLUE + "Item Cost").replace("§", "&"));
		englishUS.addDefault("inventory.editstoragetype.get_item_cost",
				Arrays.asList((ChatColor.GOLD + "Cost: " + ChatColor.DARK_AQUA + "%item_cost%").replace("§", "&"),
						(ChatColor.WHITE + "Click to edit.").replace("§", "&")));
		englishUS.addDefault("inventory.editstoragetype.max_amount", (ChatColor.BLUE + "Max Amount").replace("§", "&"));
		englishUS.addDefault("inventory.editstoragetype.get_max_amount",
				Arrays.asList((ChatColor.GOLD + "Amount: " + ChatColor.DARK_AQUA + "%max_amount%").replace("§", "&"),
						(ChatColor.WHITE + "Click to edit.").replace("§", "&")));
		englishUS.addDefault("inventory.editstoragetype.permissions",
				(ChatColor.BLUE + "Permissions").replace("§", "&"));

		englishUS.addDefault("inventory.editstoragelist.title", "Storage Type List");
		englishUS.addDefault("inventory.editstoragelist.title_paginated", "Storage Type List Page %page%");
		englishUS.addDefault("inventory.editstoragelist.storage_type",
				(ChatColor.GOLD + "Storage Type: " + ChatColor.DARK_AQUA + "%type%").replace("§", "&"));
		englishUS.addDefault("inventory.editstoragelist.storage_lore",
				Arrays.asList(
						(ChatColor.GOLD + "Max Storage Size: " + ChatColor.DARK_AQUA + "%size%").replace("§", "&"),
						(ChatColor.GOLD + "Price: " + ChatColor.DARK_AQUA + "%price%").replace("§", "&")));

		englishUS.addDefault("inventory.edittypepermissions.title", "Edit Permission");
		englishUS.addDefault("inventory.edittypepermissions.title_paginated", "Edit Permission Page %page%");
		englishUS.addDefault("inventory.edittypepermissions.permission",
				(ChatColor.GOLD + "Permission: " + ChatColor.DARK_AQUA + "%permission%").replace("§", "&"));
		englishUS.addDefault("inventory.edittypepermissions.permission_lore",
				Arrays.asList(
						(ChatColor.GOLD + "Check Order: " + ChatColor.DARK_AQUA + "%check_order%").replace("§", "&"),
						(ChatColor.GOLD + "Number of Storages: " + ChatColor.DARK_AQUA + "%num_storages%").replace("§",
								"&"),
						(ChatColor.GOLD + "Selected: " + ChatColor.DARK_AQUA + "%selected%").replace("§", "&")));

		englishUS.addDefault("inventory.listpermissions.title", "Edit Permission");
		englishUS.addDefault("inventory.listpermissions.title_paginated", "Edit Permission Page %page%");
		englishUS.addDefault("inventory.listpermissions.permission",
				(ChatColor.GOLD + "Permission: " + ChatColor.DARK_AQUA + "%permission%").replace("§", "&"));
		englishUS.addDefault("inventory.listpermissions.permission_lore", Arrays.asList(
				(ChatColor.GOLD + "Check Order: " + ChatColor.DARK_AQUA + "%check_order%").replace("§", "&"),
				(ChatColor.GOLD + "Number of Storages: " + ChatColor.DARK_AQUA + "%num_storages%").replace("§", "&")));

		englishUS.addDefault("inventory.viewpermission.title", "Edit Permission: %permission%");
		englishUS.addDefault("inventory.viewpermission.permission_string",
				(ChatColor.GOLD + "Permission String: " + ChatColor.DARK_AQUA + "%permission%").replace("§", "&"));
		englishUS.addDefault("inventory.viewpermission.check_order",
				(ChatColor.GOLD + "Check Order: " + ChatColor.DARK_AQUA + "%check_order%").replace("§", "&"));
		englishUS.addDefault("inventory.viewpermission.num_storages",
				(ChatColor.GOLD + "Number of Storages: " + ChatColor.DARK_AQUA + "%num_storages%").replace("§", "&"));

		englishUS.addDefault("inventory.listglobalpermissions.title", "Edit Global Permission");
		englishUS.addDefault("inventory.listglobalpermissions.title_paginated", "Edit Permission Page %page%");
		englishUS.addDefault("inventory.listglobalpermissions.permission",
				(ChatColor.GOLD + "Permission: " + ChatColor.DARK_AQUA + "%permission%").replace("§", "&"));
		englishUS.addDefault("inventory.listglobalpermissions.permission_lore", Arrays.asList(
				(ChatColor.GOLD + "Check Order: " + ChatColor.DARK_AQUA + "%check_order%").replace("§", "&"),
				(ChatColor.GOLD + "Number of Storages: " + ChatColor.DARK_AQUA + "%num_storages%").replace("§", "&")));

		englishUS.addDefault("inventory.viewglobalpermission.title", "Edit Global Permission: %permission%");
		englishUS.addDefault("inventory.viewglobalpermission.permission_string",
				(ChatColor.GOLD + "Permission String: " + ChatColor.DARK_AQUA + "%permission%").replace("§", "&"));
		englishUS.addDefault("inventory.viewglobalpermission.check_order",
				(ChatColor.GOLD + "Check Order: " + ChatColor.DARK_AQUA + "%check_order%").replace("§", "&"));
		englishUS.addDefault("inventory.viewglobalpermission.num_storages",
				(ChatColor.GOLD + "Number of Storages: " + ChatColor.DARK_AQUA + "%num_storages%").replace("§", "&"));
		
		englishUS.addDefault("inventory.draftlist.title", "%player%'s Drafts");
		englishUS.addDefault("inventory.draftlist.title_paginated", "%player%'s Drafts Page %page%");
		englishUS.addDefault("inventory.draftlist.name",
				(ChatColor.GOLD + "Name: " + ChatColor.DARK_AQUA + "%name%").replace("§", "&"));
		englishUS.addDefault("inventory.draftlist.max_amount",
				(ChatColor.GOLD + "Max Storage Size: " + ChatColor.DARK_AQUA + "%max_amount%").replace("§", "&"));
		englishUS.addDefault("inventory.draftlist.metadata", (ChatColor.GOLD + "Metadata: ").replace("§", "&"));
		englishUS.addDefault("inventory.draftlist.viewstorages",
				(ChatColor.LIGHT_PURPLE + "View Storages").replace("§", "&"));

		englishUS.addDefault("inventory.viewdraft.title", ("Cold Storage: %name%").replace("§", "&"));
		englishUS.addDefault("inventory.viewdraft.name",
				(ChatColor.GOLD + "Name: " + ChatColor.DARK_AQUA + "%name%").replace("§", "&"));
		englishUS.addDefault("inventory.viewdraft.name_lore",
				(ChatColor.WHITE + "Click to edit the name. Must have a name before finalizing.").replace("§", "&"));
		englishUS.addDefault("inventory.viewdraft.item_type",
				(ChatColor.GOLD + "Item Type: " + ChatColor.DARK_AQUA + "%item_type%").replace("§", "&"));
		englishUS.addDefault("inventory.viewdraft.meta_first",
				(ChatColor.GOLD + "Meta: " + ChatColor.DARK_AQUA + "%meta%").replace("§", "&"));
		englishUS.addDefault("inventory.viewdraft.meta", (ChatColor.DARK_AQUA + "%meta%").replace("§", "&"));
		englishUS.addDefault("inventory.viewdraft.storage_type",
				(ChatColor.GOLD + "Storage Type: " + ChatColor.DARK_AQUA + "%storage_type%").replace("§", "&"));
		englishUS.addDefault("inventory.viewdraft.exists",
				(ChatColor.GOLD + "Exists: " + ChatColor.DARK_AQUA + "%exists%").replace("§", "&"));
		englishUS.addDefault("inventory.viewdraft.max_storage",
				(ChatColor.GOLD + "Max Storage: " + ChatColor.DARK_AQUA + "%max_storage%").replace("§", "&"));
		englishUS.addDefault("inventory.viewdraft.price",
				(ChatColor.GOLD + "Price: " + ChatColor.DARK_AQUA + "%price%").replace("§", "&"));
		englishUS.addDefault("inventory.viewdraft.max_number",
				(ChatColor.GOLD + "Max Number: " + ChatColor.DARK_AQUA + "%max_number%").replace("§", "&"));
		englishUS.addDefault("inventory.viewdraft.max_import",
				(ChatColor.GOLD + "Max Import: " + ChatColor.DARK_AQUA + "%max_import%").replace("§", "&"));
		englishUS.addDefault("inventory.viewdraft.max_export",
				(ChatColor.GOLD + "Max Export: " + ChatColor.DARK_AQUA + "%max_export%").replace("§", "&"));
		englishUS.addDefault("inventory.viewdraft.buy_invalid", (ChatColor.DARK_RED + "Buy").replace("§", "&"));
		englishUS.addDefault("inventory.viewdraft.buy", (ChatColor.DARK_GREEN + "Buy").replace("§", "&"));
		englishUS.addDefault("inventory.viewdraft.buy_price",
				(ChatColor.DARK_GREEN + "Price: " + ChatColor.GREEN + "%price%").replace("§", "&"));
		englishUS.addDefault("inventory.viewdraft.issues", (ChatColor.RED + "%issue%").replace("§", "&"));

		englishUS.addDefault("inventory.storagelist.title", "%player%'s List");
		englishUS.addDefault("inventory.storagelist.title_paginated", "%player%'s List Page %page%");
		englishUS.addDefault("inventory.storagelist.name", (ChatColor.BLUE + "%name%").replace("§", "&"));
		englishUS.addDefault("inventory.storagelist.amount",
				(ChatColor.GOLD + "Stored Amount: " + ChatColor.DARK_AQUA + "%amount%").replace("§", "&"));
		englishUS.addDefault("inventory.storagelist.insert",
				(ChatColor.GOLD + "Insert All: " + ChatColor.DARK_AQUA + "%insert%").replace("§", "&"));
		englishUS.addDefault("inventory.storagelist.meta_first",
				(ChatColor.GOLD + "Metadata: " + ChatColor.DARK_AQUA + "%meta%").replace("§", "&"));
		englishUS.addDefault("inventory.storagelist.meta", (ChatColor.DARK_AQUA + "%meta%").replace("§", "&"));
		englishUS.addDefault("inventory.storagelist.insert_all",
				(ChatColor.BLUE + "Insert Items to Cold Storages").replace("§", "&"));
		englishUS.addDefault("inventory.storagelist.inserted",
				(ChatColor.WHITE + "Inserted %amount% of %material% into %storage%.").replace("§", "&"));

		englishUS.addDefault("inventory.viewstorage.title", "Cold Storage: %name%");
		englishUS.addDefault("inventory.viewstorage.take_stack", (ChatColor.BLUE + "Take a Stack").replace("§", "&"));
		englishUS.addDefault("inventory.viewstorage.type_meta", Arrays.asList(
				(ChatColor.GOLD + "Amount: " + ChatColor.DARK_AQUA + "%stored_amount%").replace("§", "&"),
				(ChatColor.GOLD + "Max Storage Size: " + ChatColor.DARK_AQUA + "%max_size%").replace("§", "&")));
		englishUS.addDefault("inventory.viewstorage.meta_first",
				(ChatColor.GOLD + "Metadata: " + ChatColor.DARK_AQUA + "%meta%").replace("§", "&"));
		englishUS.addDefault("inventory.viewstorage.meta", (ChatColor.DARK_AQUA + "%meta%").replace("§", "&"));
		englishUS.addDefault("inventory.viewstorage.fill_inventory",
				(ChatColor.BLUE + "Fill Inventory").replace("§", "&"));
		englishUS.addDefault("inventory.viewstorage.empty_inventory",
				(ChatColor.BLUE + "Empty Inventory").replace("§", "&"));
		englishUS.addDefault("inventory.viewstorage.empty_inventory_info",
				(ChatColor.WHITE + "Click items in your inventory to insert manually.").replace("§", "&"));
		englishUS.addDefault("inventory.viewstorage.edit_name",
				(ChatColor.BLUE + "Edit Name").replace("§", "&"));
		englishUS.addDefault("inventory.viewstorage.import_chest",
				(ChatColor.BLUE + "Import Chests").replace("§", "&"));
		englishUS.addDefault("inventory.viewstorage.edit_import_chest",
				Arrays.asList((ChatColor.GOLD + "Max Import Chests: " + ChatColor.DARK_AQUA + "%max_import%").replace("§", "&"), 
				(ChatColor.WHITE + "Click to edit.").replace("§", "&")));
		englishUS.addDefault("inventory.viewstorage.export_chest",
				(ChatColor.BLUE + "Export Chests").replace("§", "&"));
		englishUS.addDefault("inventory.viewstorage.edit_export_chest",
				Arrays.asList((ChatColor.GOLD + "Max Export Chests: " + ChatColor.DARK_AQUA + "%max_export%").replace("§", "&"), 
						(ChatColor.WHITE + "Click to edit.").replace("§", "&")));
		englishUS.addDefault("inventory.viewstorage.edit_display_order",
				(ChatColor.BLUE + "Edit Display Order").replace("§", "&"));
		englishUS.addDefault("inventory.viewstorage.display_order_meta",
				(ChatColor.GOLD + "Current: " + ChatColor.DARK_AQUA + "%order%").replace("§", "&"));
		englishUS.addDefault("inventory.viewstorage.toggle_insert_all",
				(ChatColor.BLUE + "Toggle Insert All").replace("§", "&"));
		englishUS.addDefault("inventory.viewstorage.insert_all_meta",
				(ChatColor.GOLD + "Current: " + ChatColor.DARK_AQUA + "%insert_all%").replace("§", "&"));
		englishUS.addDefault("inventory.viewstorage.set_amount",
				(ChatColor.BLUE + "Edit the Stored Amount").replace("§", "&"));

		englishUS.addDefault("inventory.storagetypelist.title", "Select Storage Type");
		englishUS.addDefault("inventory.storagetypelist.title_paginated", "Select Storage Type Page %page%");
		englishUS.addDefault("inventory.storagetypelist.storage_type",
				(ChatColor.GOLD + "Storage Type: " + ChatColor.DARK_AQUA + "%storage_type%").replace("§", "&"));
		englishUS.addDefault("inventory.storagetypelist.storage_meta",
				Arrays.asList(
						(ChatColor.GOLD + "Max Storage Size: " + ChatColor.DARK_AQUA + "%max_amount%").replace("§",
								"&"),
						(ChatColor.GOLD + "Price: " + ChatColor.DARK_AQUA + "%price%").replace("§", "&")));

		englishUS.addDefault("inventory.editchests.title", "%name% Edit %type% Chests");
		englishUS.addDefault("inventory.editchests.title_paginated", "%name% Edit %type% Chests Page %page%");
		englishUS.addDefault("inventory.editchests.different_type", (ChatColor.RED + "Cannot Toggle - Set to other chest type.").replace("§", "&"));
		englishUS.addDefault("inventory.editchests.chest_type", (ChatColor.BLUE + "Chest Type").replace("§", "&"));
		englishUS.addDefault("inventory.editchests.selected", (ChatColor.GOLD + "Selected: " + ChatColor.DARK_AQUA + "%selected%").replace("§", "&"));
		englishUS.addDefault("inventory.editchests.chest_type_locations", Arrays.asList(
				(ChatColor.GOLD + "Location: " + ChatColor.DARK_AQUA + "%location_one%").replace("§", "&"),
				(ChatColor.GOLD + "Second Location: " + ChatColor.DARK_AQUA + "%location_two%").replace("§", "&")
		));
		
		englishUS.addDefault("inventory.playerlist.title", "Modify Player Storages");
		englishUS.addDefault("inventory.playerlist.title_paginated", "Modify Player Storages Page %page%");
		englishUS.addDefault("inventory.playerlist.owning_player", (ChatColor.BLUE + "%owning_player%'s Storages").replace("§", "&"));
		englishUS.addDefault("inventory.playerlist.meta", Arrays.asList(
				(ChatColor.WHITE + "Click to edit.").replace("§", "&")
		));
		
		englishUS.addDefault("listeners.open_inventory", "Opened chest owned by %owned_player%.");
		englishUS.addDefault("listeners.break_chest", "Broke the chest! If this was a double chest, the second chest was removed as well.");
		englishUS.addDefault("listeners.new_chest", "Created new chest! Add to a storage using the edit menu.");
		englishUS.addDefault("listeners.use_chat", "Type in chat to set the value. Your inventory will not work until you do this.");
		
		englishUS.addDefault("reasons.player_null", "Player cannot be null.");
		englishUS.addDefault("reasons.unique_null", "Unique cannot be null.");
		englishUS.addDefault("reasons.material_null", "You must select a material.");
		englishUS.addDefault("reasons.storage_type_missing", "Storage type no longer exists: %storage_type%");
		englishUS.addDefault("reasons.storage_type_null", "You must select a storage type.");
		englishUS.addDefault("reasons.name_null", "You must give the storage a name.");
		englishUS.addDefault("reasons.no_permission", "You do not have permission for this storage type.");
		englishUS.addDefault("reasons.too_many_type", "You already have the maximum amount of storages for this type.");
		englishUS.addDefault("reasons.too_many_total", "You already have the maximum amount of storages.");

		englishUS.saveConfig();
	}
}
