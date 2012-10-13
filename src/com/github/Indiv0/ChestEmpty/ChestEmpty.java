package com.github.Indiv0.ChestEmpty;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestEmpty extends JavaPlugin {
    // Stores whether or not chests are currently being selected for emptying.
    private ArrayList<String> playersSelecting = new ArrayList<String>();
    // Stores the list of inventories of the chests emptied last time.
    private HashMap<Block, ItemStack[]> lastDeletedItems = new HashMap<Block, ItemStack[]>();

    // Initializes an ItemCraftListener.
    public final BlockSelectionListener blockListener = new BlockSelectionListener(this);

    public void onEnable() {
        // Retrieves an instance of the PluginManager.
        PluginManager pm = getServer().getPluginManager();

        // Registers the blockListener with the PluginManager.
        pm.registerEvents(this.blockListener, this);
        
        // Prints a message to the server confirming successful initialization of the plugin.
        getLogger().info("ChestEmpty has initialized successfully.");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Checks to see if the command is the "/chestempty" command.
        if(!cmd.getName().equalsIgnoreCase("chestempty")) return false;

        // Checks to make sure a player is performing the command.
        if(!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return false;
        }

        if(!sender.hasPermission("chestempty.use"))
            return false;
        
        // Makes sure an argument has been provided.
        if(args.length == 0) {
            sender.sendMessage("To use ChestEmpty, type \"/chestempty\" followed by: toggle or undo.");
            return false;
        }

        // Makes sure the appropriate amount of arguments has been provided.
        if(args.length > 1) {
            sender.sendMessage("Too many arguments. Valid arguments are: toggle or undo.");
            return false;
        }
        
        if(!args[0].equals("toggle") && !args[0].equals("undo")) {
            sender.sendMessage("Invalid argument. Valid arguments are: toggle or undo");
            return false;
        }
        
        String playerName = ((Player)sender).getDisplayName();
        
        if(args[0].equals("toggle")) {
            toggleSelecting(playerName, sender);
            return true;
        }
        if(args[0].equals("undo")) {
            if(isSelecting(playerName)) {
                sender.sendMessage("Please exit selection mode first.");
                return false;
            }
            
            undoDeleteChestContents(sender);
            return true;
        }
        
        return false;
    }

    private void undoDeleteChestContents(CommandSender sender) {
        Player player = (Player) sender;
        
        if(lastDeletedItems.isEmpty() || player.hasMetadata("ChestBackupID") == false) {
            sender.sendMessage("No items to restore.");
            return;
        }
        
        for(Block block : lastDeletedItems.keySet()) {
            if(player.getMetadata("ChestBackupID").get(0).asInt() == block.hashCode()) {
                if(!(block.getType() == Material.CHEST)) {
                    sender.sendMessage("An item could not be restored.");
                }
                else {
                    Chest chest = (Chest) block.getState();;
                    
                    for(ItemStack itemStack : lastDeletedItems.get(block))
                        if(itemStack != null)
                            chest.getInventory().addItem(itemStack);
                }
                
                player.removeMetadata("ChestBackupID", this);
                lastDeletedItems.remove(block);
            }
        }

        lastDeletedItems.clear();

        sender.sendMessage("Items successfully restored.");
    }

    public void addItemBackup(Chest chest, Player player)
    {
        Block block = (Block) chest.getBlock();
        
        player.setMetadata("ChestBackupID", new FixedMetadataValue(this, block.hashCode()));
        lastDeletedItems.put(block, chest.getInventory().getContents());
    }

    public void toggleSelecting(String playerName, CommandSender sender)
    {
        if(isSelecting(playerName)) {
            playersSelecting.remove(playerName);
            sender.sendMessage("Selection mode disabled.");
        }
        else {
            playersSelecting.add(playerName);
            sender.sendMessage("Selection mode activated.");
        }
    }
   
    public boolean isSelecting(String playerName) {
        return playersSelecting.contains(playerName);
    }
}
