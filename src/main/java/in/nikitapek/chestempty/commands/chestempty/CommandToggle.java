package in.nikitapek.chestempty.commands.chestempty;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import in.nikitapek.chestempty.util.ChestEmptyConfigurationContext;
import in.nikitapek.chestempty.util.Commands;

import com.amshulman.mbapi.commands.PlayerOnlyCommand;
import com.amshulman.typesafety.TypeSafeCollections;
import com.amshulman.typesafety.TypeSafeList;

public class CommandToggle extends PlayerOnlyCommand {
    private final ChestEmptyConfigurationContext configurationContext;

    public CommandToggle(final ChestEmptyConfigurationContext configurationContext) {
        super(configurationContext, Commands.TOGGLE, 0, 0);

        this.configurationContext = configurationContext;
    }

    @Override
    protected boolean executeForPlayer(Player player, TypeSafeList<String> args) {
        // Toggles whether or not the player is currently in selection mode.
        if (configurationContext.playersSelecting.contains(player.getDisplayName())) {
            configurationContext.playersSelecting.remove(player.getDisplayName());
            player.sendMessage("Selection mode disabled.");
        }
        else {
            configurationContext.playersSelecting.add(player.getDisplayName());
            player.sendMessage("Selection mode activated.");
        }

        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(final CommandSender sender, final TypeSafeList<String> args) {
        return TypeSafeCollections.emptyList();
    }
}
