package in.nikitapek.chestempty.commands.chestempty;

import in.nikitapek.chestempty.commands.CommandChestEmpty.ChestEmptyCommands;
import in.nikitapek.chestempty.util.ChestEmptyConfigurationContext;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.amshulman.mbapi.commands.PlayerOnlyCommand;
import com.amshulman.typesafety.TypeSafeCollections;
import com.amshulman.typesafety.TypeSafeList;

public final class CommandUndo extends PlayerOnlyCommand {
    private final ChestEmptyConfigurationContext configurationContext;

    public CommandUndo(final ChestEmptyConfigurationContext configurationContext) {
        super(configurationContext, ChestEmptyCommands.UNDO, 0, 0);

        this.configurationContext = configurationContext;
    }

    @Override
    protected boolean executeForPlayer(final Player player, final TypeSafeList<String> args) {
        // Cancels the operation if the player is in selection mode.
        if (configurationContext.isPlayerSelecting(player.getName())) {
            player.sendMessage("Please exit selection mode first.");
            return false;
        }

        // Cancels the operation of there is no backup for that user.
        if (!player.hasMetadata("ChestBackupID")) {
            player.sendMessage("You have not emptied any chests.");
            return true;
        }

        // Cancels the operation is no backups are present.
        if (configurationContext.isChestBackupsEmpty()) {
            player.sendMessage("No chest backups exist.");
            player.removeMetadata("ChestBackupID", configurationContext.plugin);
            return true;
        }

        configurationContext.restoreChestBackupForPlayer(player);

        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(final CommandSender sender, final TypeSafeList<String> args) {
        return TypeSafeCollections.emptyList();
    }
}
