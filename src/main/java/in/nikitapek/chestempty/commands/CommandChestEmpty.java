package in.nikitapek.chestempty.commands;

import in.nikitapek.chestempty.commands.chestempty.CommandToggle;
import in.nikitapek.chestempty.commands.chestempty.CommandUndo;
import in.nikitapek.chestempty.util.ChestEmptyConfigurationContext;
import in.nikitapek.chestempty.util.Commands;

import com.amshulman.mbapi.commands.DelegatingCommand;
import com.amshulman.mbapi.util.PermissionsEnum;

public final class CommandChestEmpty extends DelegatingCommand {
    public CommandChestEmpty(final ChestEmptyConfigurationContext configurationContext) {
        super(configurationContext, Commands.CHESTEMPTY, 1, 1);
        registerSubcommand(new CommandToggle(configurationContext));
        registerSubcommand(new CommandUndo(configurationContext));
    }

    public enum ChestEmptyCommands implements PermissionsEnum {
        TOGGLE, UNDO;

        private static final String PREFIX;

        static {
            PREFIX = Commands.CHESTEMPTY.getPrefix() + Commands.CHESTEMPTY.name() + ".";
        }

        @Override
        public String getPrefix() {
            return PREFIX;
        }
    }
}
