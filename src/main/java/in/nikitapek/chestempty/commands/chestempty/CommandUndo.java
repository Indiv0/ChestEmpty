package in.nikitapek.chestempty.commands.chestempty;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.amshulman.mbapi.commands.PlayerOnlyCommand;
import com.amshulman.typesafety.TypeSafeCollections;
import com.amshulman.typesafety.TypeSafeList;

import in.nikitapek.chestempty.util.ChestEmptyConfigurationContext;
import in.nikitapek.chestempty.util.Commands;

public class CommandUndo extends PlayerOnlyCommand {
    private final ChestEmptyConfigurationContext configurationContext;

    public CommandUndo(final ChestEmptyConfigurationContext configurationContext) {
        super(configurationContext, Commands.UNDO, 0, 0);

        this.configurationContext = configurationContext;
    }

    @Override
    protected boolean executeForPlayer(Player player, TypeSafeList<String> args) {
        // Undoes the previous chestempty operation for that user.
        if (configurationContext.playersSelecting.contains(player.getDisplayName())) {
            player.sendMessage("Please exit selection mode first.");
            return false;
        }

        // Checks to see if the cache has a backup for that user.
        if (!player.hasMetadata("ChestBackupID")) {
            player.sendMessage("Player has not emptied any chests.");
            return true;
        }

        if (configurationContext.lastDeletedItems.isEmpty()) {
            player.sendMessage("No chest backups exist.");
            player.removeMetadata("ChestBackupID", configurationContext.plugin);
            return true;
        }

        // Checks every chest within the cache (this is probably a performance bottleneck).
        Iterator<Entry<Block, ItemStack[]>> iter = configurationContext.lastDeletedItems.entrySet().iterator();
        while (iter.hasNext()) {
            Block block = iter.next().getKey();
            ArrayList<Block> blocksToRemove = new ArrayList<Block>();

            // Checks if the player's metadata stores the same hashCode as the hashCode of the block (i.e. the same chest is being referenced).
            if (player.getMetadata("ChestBackupID").get(0).asInt() != block.hashCode())
                continue;

            // Removes the hashcode of the chest from the player's metadata.
            player.removeMetadata("ChestBackupID", configurationContext.plugin);
            blocksToRemove.add(block);

            // Checks to make sure the block has not been converted into another block since the chest was emptied.
            if (block.getType() != Material.CHEST) {
                player.sendMessage("Cannot restore inventory: block no longer a chest.");
                continue;
            }

            Chest chest = (Chest) block.getState();

            // Returns the deleted items into the chest. This should be tested for what happens when the chest is full and items get restored.
            restore: for (ItemStack itemStack : configurationContext.lastDeletedItems.get(block)) {
                if (itemStack == null)
                    continue restore;

                chest.getInventory().addItem(itemStack);
            }

            // Removes any restored chests from the cache.
            iter.remove();
            player.sendMessage("Items successfully restored.");
        }

        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(final CommandSender sender, final TypeSafeList<String> args) {
        return TypeSafeCollections.emptyList();
    }
}
