package me.mbiancini.garnetmatrix;

import me.mbiancini.garnetmatrix.constants.GMColorTheme;
import me.mbiancini.garnetmatrix.registries.enchantments.GMEnchantHolder;
import me.mbiancini.garnetmatrix.registries.listeners.GMChatListener;
import me.mbiancini.garnetmatrix.utils.GMLogger;
import me.mbiancini.garnetmatrix.utils.TextComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class GarnetMatrixPlugin extends JavaPlugin {

	public static Plugin INSTANCE;

	private final GMLogger logger = new GMLogger();
	private final TextComponentBuilder textComponentBuilder = new TextComponentBuilder();

	@Override
	public void onEnable() {

		GarnetMatrixPlugin.INSTANCE = this;

		_printLoadInfo();
		_loadEnchantments();
		_loadEvents();
		_printEndLoad();


	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
	}


	private void _printLoadInfo() {
		textComponentBuilder.append(GMColorTheme.YELLOW, "Starting ");
		textComponentBuilder.append(GMColorTheme.ACCENT_YELLOW, "Garnet Matrix");
		textComponentBuilder.append(GMColorTheme.YELLOW, " Plugin.");

		logger.sendConsoleMessage(textComponentBuilder.build());
		textComponentBuilder.reset();

		logger.sendConsoleMessage(textComponentBuilder.build());
	}

	private void _loadEvents() {

		textComponentBuilder.append(GMColorTheme.YELLOW, "Loading Events");
		logger.sendConsoleMessage(textComponentBuilder.build());
		textComponentBuilder.reset();

		Bukkit.getPluginManager().registerEvents(new GMChatListener(), this);

		textComponentBuilder.append(GMColorTheme.GREEN, "Events loaded ");
		textComponentBuilder.append(GMColorTheme.ACCENT_GREEN, "successfully");

		logger.sendConsoleMessage(textComponentBuilder.build());
		textComponentBuilder.reset();

		logger.sendConsoleMessage(textComponentBuilder.build());
		textComponentBuilder.reset();

	}

	private void _loadEnchantments() {

		textComponentBuilder.append(GMColorTheme.YELLOW, "Loading Enchantments");
		logger.sendConsoleMessage(textComponentBuilder.build());
		textComponentBuilder.reset();

		GMEnchantHolder.registerEnchants(this);

		textComponentBuilder.append(GMColorTheme.GREEN, "Enchantments loaded ");
		textComponentBuilder.append(GMColorTheme.ACCENT_GREEN, "successfully");

		logger.sendConsoleMessage(textComponentBuilder.build());
		textComponentBuilder.reset();

		logger.sendConsoleMessage(textComponentBuilder.build());
		textComponentBuilder.reset();

	}

	private void _printEndLoad() {
		textComponentBuilder.append(GMColorTheme.YELLOW, "Garnet Matrix loaded complete! ");
		logger.sendConsoleMessage(textComponentBuilder.build());
		textComponentBuilder.reset();
	}

}
