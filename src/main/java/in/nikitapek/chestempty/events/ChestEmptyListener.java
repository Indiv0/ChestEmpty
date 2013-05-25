package in.nikitapek.chestempty.events;

import in.nikitapek.chestempty.util.ChestEmptyConfigurationContext;

import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

public final class ChestEmptyListener implements Listener {
    private final ChestEmptyConfigurationContext configurationContext;

    public ChestEmptyListener(final ChestEmptyConfigurationContext configurationContext) {
        this.configurationContext = configurationContext;
    }

    @EventHandler
    public void onChestHit(final PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        // Makes sure the player is currently in selection mode.
        if (!configurationContext.isPlayerSelecting(event.getPlayer().getName())) {
            return;
        }

        // Makes sure that the clicked block is a chest.
        if (event.getClickedBlock().getType() == null || event.getClickedBlock().getType() != Material.CHEST) {
            return;
        }

        final Chest chest = (Chest) event.getClickedBlock().getState();
        final Player player = event.getPlayer();

        // Checks to make sure that the chest has items.
        if (chest.getBlockInventory().getSize() == 0) {
            player.sendMessage("This chest is empty. It cannot be cleared.");
            return;
        }

        // Adds the inventory of a chest into the cache, and stores its hashCode
        // in the player's metadata.
        player.setMetadata("ChestBackupID", new FixedMetadataValue(configurationContext.plugin, chest.getLocation().hashCode()));
        configurationContext.addChestBackup(chest);

        // Deleted the items, which are now backed up.
        chest.getInventory().clear();
        chest.update();

        player.sendMessage("Chest successfully emptied.");

        event.setCancelled(true);
    }
}
