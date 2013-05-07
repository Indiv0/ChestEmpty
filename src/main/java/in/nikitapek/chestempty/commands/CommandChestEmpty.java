package in.nikitapek.chestempty.commands;

import in.nikitapek.chestempty.commands.chestempty.CommandToggle;
import in.nikitapek.chestempty.commands.chestempty.CommandUndo;
import in.nikitapek.chestempty.util.ChestEmptyConfigurationContext;
import in.nikitapek.chestempty.util.Commands;

import com.amshulman.mbapi.commands.DelegatingCommand;

public class CommandChestEmpty extends DelegatingCommand {
    public CommandChestEmpty(final ChestEmptyConfigurationContext configurationContext) {
        super(configurationContext, Commands.CHESTEMPTY, 1, 1);
        registerSubcommand(new CommandToggle(configurationContext));
        registerSubcommand(new CommandUndo(configurationContext));
    }
}
