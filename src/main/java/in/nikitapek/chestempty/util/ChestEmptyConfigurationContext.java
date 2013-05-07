package in.nikitapek.chestempty.util;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import com.amshulman.mbapi.MbapiPlugin;
import com.amshulman.mbapi.util.ConfigurationContext;

public class ChestEmptyConfigurationContext extends ConfigurationContext {
    // Stores the players currently emptying chests.
    public final ArrayList<String> playersSelecting;
    // Stores the chests and their last inventories prior to deletion.
    public final HashMap<Block, ItemStack[]> lastDeletedItems;

    public ChestEmptyConfigurationContext(MbapiPlugin plugin) {
        super(plugin);

        playersSelecting = new ArrayList<String>();
        lastDeletedItems = new HashMap<Block, ItemStack[]>();
    }
}
