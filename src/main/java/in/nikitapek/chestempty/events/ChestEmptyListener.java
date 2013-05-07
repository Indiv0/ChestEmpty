package in.nikitapek.chestempty.events;

import java.util.Iterator;
import java.util.Map.Entry;

import in.nikitapek.chestempty.util.ChestEmptyConfigurationContext;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class ChestEmptyListener implements Listener {
    private ChestEmptyConfigurationContext configurationContext;

    public ChestEmptyListener(final ChestEmptyConfigurationContext configurationContext) {
        this.configurationContext = configurationContext;
    }

    @EventHandler
    public void onChestHit(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK)
            return;

        // Makes sure the player is currently in selection mode.
        if (!configurationContext.playersSelecting.contains(event.getPlayer().getDisplayName()))
            return;

        // Makes sure that the clicked block is a chest.
        if (event.getClickedBlock().getType() == null || event.getClickedBlock().getType() != Material.CHEST)
            return;

        Chest chest = (Chest) event.getClickedBlock().getState();
        Player player = event.getPlayer();

        // Checks to make sure that the chest has items.
        if (chest.getBlockInventory().getSize() == 0) {
            player.sendMessage("This chest is empty. It cannot be cleared.");
            return;
        }

        // Searches for, and removes any previously added inventory backups for that block.
        Iterator<Entry<Block, ItemStack[]>> iter = configurationContext.lastDeletedItems.entrySet().iterator();
        while (iter.hasNext())
            // If the chest has already been backed up, remove the iterator.
            if (iter.next().getKey().hashCode() == event.getClickedBlock().hashCode())
                iter.remove();

        // Adds the inventory of a chest into the cache, and stores its hashCode
        // in the player's metadata.
        player.setMetadata("ChestBackupID", new FixedMetadataValue(configurationContext.plugin, event.getClickedBlock().hashCode()));
        configurationContext.lastDeletedItems.put(event.getClickedBlock(), chest.getInventory().getContents());

        // Deleted the items, which are now backed up.
        chest.getInventory().clear();
        chest.update();

        player.sendMessage("Chest successfully emptied.");

        event.setCancelled(true);
    }
}
