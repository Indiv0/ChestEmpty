package com.github.Indiv0.ChestEmpty;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockSelectionListener implements Listener {
    public static ChestEmpty plugin;

    public BlockSelectionListener(ChestEmpty instance) {
        plugin = instance;
    }

    // Create a method to handle/interact with clicking events.
    @EventHandler
    public void onChestHit(PlayerInteractEvent event) {
        if(!plugin.isSelecting(event.getPlayer().getDisplayName())) return;

        Block clickedBlock = event.getClickedBlock();
        
        try {
            if(clickedBlock.getType() != Material.CHEST)
                return;
        } catch (NullPointerException ex) {
            return;
        }
        
        if(event.getAction() != org.bukkit.event.block.Action.LEFT_CLICK_BLOCK)
            return;

        Chest chest = (Chest)clickedBlock.getState();

        if(chest.getBlockInventory().getSize() == 0) return;

        plugin.addItemBackup(chest, event.getPlayer());
        chest.getInventory().clear();
        chest.update();

        event.getPlayer().sendMessage("Chest successfully emptied.");
    }
}
