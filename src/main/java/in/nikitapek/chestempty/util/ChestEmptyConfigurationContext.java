package in.nikitapek.chestempty.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.amshulman.mbapi.MbapiPlugin;
import com.amshulman.mbapi.util.ConfigurationContext;

public final class ChestEmptyConfigurationContext extends ConfigurationContext {
    // Stores the players currently emptying chests.
    private final ArrayList<String> playersSelecting = new ArrayList<String>();
    // Stores the chests and their last inventories prior to deletion.
    private final HashMap<Location, ItemStack[]> chestBackups = new HashMap<Location, ItemStack[]>();

    public ChestEmptyConfigurationContext(final MbapiPlugin plugin) {
        super(plugin);
    }

    private void restoreChestBackup(final Chest chest) {
        for (final ItemStack itemStack : chestBackups.get(chest.getLocation())) {
            if (itemStack == null) {
                continue;
            }

            chest.getInventory().addItem(itemStack);
        }
    }

    public boolean isPlayerSelecting(final String playerName) {
        if (playerName == null) {
            throw new NullPointerException();
        }

        return playersSelecting.contains(playerName);
    }

    public void setPlayerSelecting(final String playerName, final boolean isSelecting) {
        if (!isSelecting) {
            playersSelecting.remove(playerName);
            return;
        }

        if (isPlayerSelecting(playerName)) {
            return;
        }

        playersSelecting.add(playerName);
    }

    public boolean isChestBackupsEmpty() {
        return chestBackups.isEmpty();
    }

    public boolean isCheckBackedUp(final Location location) {
        if (isChestBackupsEmpty()) {
            return false;
        }

        return chestBackups.containsKey(location);
    }

    public void addChestBackup(final Chest chest) {
        // Searches for, and removes any previously added inventory backups for that block.
        removeChestBackup(chest.getLocation());

        chestBackups.put(chest.getLocation(), chest.getInventory().getContents());
    }

    public void removeChestBackup(final Location location) {
        final Iterator<Entry<Location, ItemStack[]>> iter = chestBackups.entrySet().iterator();
        while (iter.hasNext()) {
            if (iter.next().getKey().equals(location)) {
                iter.remove();
            }
        }
    }

    public void restoreChestBackupForPlayer(final Player player) {
        // Checks every chest within the cache (this is probably a performance bottleneck).
        final Iterator<Entry<Location, ItemStack[]>> iter = chestBackups.entrySet().iterator();
        while (iter.hasNext()) {
            final Block block = iter.next().getKey().getBlock();

            // Checks if the player's metadata stores the same hashCode as the hashCode of the block (i.e. the same chest is being referenced).
            if (player.getMetadata("ChestBackupID").get(0).asInt() != block.getLocation().hashCode()) {
                continue;
            }

            // Removes the hashCode of the chest from the player's metadata.
            player.removeMetadata("ChestBackupID", plugin);

            // Checks to make sure the block has not been converted into another block since the chest was emptied.
            if (block.getType() != Material.CHEST) {
                player.sendMessage("Cannot restore inventory: block no longer a chest.");
                continue;
            }

            // Returns the deleted items into the chest. This should be tested for what happens when the chest is full and items get restored.
            restoreChestBackup((Chest) block.getState());

            // Removes any restored chests from the cache.
            iter.remove();
            player.sendMessage("Items successfully restored.");
        }
    }
}
