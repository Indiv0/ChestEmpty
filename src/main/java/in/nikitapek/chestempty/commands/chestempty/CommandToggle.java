package in.nikitapek.chestempty.commands.chestempty;

import in.nikitapek.chestempty.commands.CommandChestEmpty.ChestEmptyCommands;
import in.nikitapek.chestempty.util.ChestEmptyConfigurationContext;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.amshulman.mbapi.commands.PlayerOnlyCommand;
import com.amshulman.typesafety.TypeSafeCollections;
import com.amshulman.typesafety.TypeSafeList;

public final class CommandToggle extends PlayerOnlyCommand {
    private final ChestEmptyConfigurationContext configurationContext;

    public CommandToggle(final ChestEmptyConfigurationContext configurationContext) {
        super(configurationContext, ChestEmptyCommands.TOGGLE, 0, 0);

        this.configurationContext = configurationContext;
    }

    @Override
    protected boolean executeForPlayer(final Player player, final TypeSafeList<String> args) {
        // Toggles whether or not the player is currently in selection mode.
        configurationContext.setPlayerSelecting(player.getName(), !configurationContext.isPlayerSelecting(player.getName()));
        player.sendMessage(configurationContext.isPlayerSelecting(player.getName()) ? "Selection mode activated." : "Selection mode disabled.");

        return true;
    }

    @Override
    public TypeSafeList<String> onTabComplete(final CommandSender sender, final TypeSafeList<String> args) {
        return TypeSafeCollections.emptyList();
    }
}
