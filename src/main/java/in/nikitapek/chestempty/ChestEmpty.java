package in.nikitapek.chestempty;

import in.nikitapek.chestempty.events.ChestEmptyListener;
import in.nikitapek.chestempty.util.ChestEmptyConfigurationContext;

import org.bukkit.Bukkit;

import com.amshulman.mbapi.MbapiPlugin;

public class ChestEmpty extends MbapiPlugin {
    @Override
    public void onEnable() {
        ChestEmptyConfigurationContext configurationContext = new ChestEmptyConfigurationContext(this);

        registerEventHandler(new ChestEmptyListener(configurationContext));

        super.onEnable();
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }
}
