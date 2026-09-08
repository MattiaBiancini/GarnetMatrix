package me.mbiancini.garnetmatrix;

import me.mbiancini.garnetmatrix.registries.enchantments.GMEnchantHolder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class GarnetMatrixPlugin extends JavaPlugin {

	public static Plugin INSTANCE;

	@Override
	public void onEnable() {

		GarnetMatrixPlugin.INSTANCE = this;

		GMEnchantHolder.registerEnchants(this);

	}

	@Override
	public void onLoad() {
		GarnetMatrixPlugin.INSTANCE = this;

		GMEnchantHolder.registerEnchants(this);
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
	}

}
