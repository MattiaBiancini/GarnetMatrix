package me.mbiancini.garnetmatrix;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import me.mbiancini.garnetmatrix.registries.enchantments.GMEnchantHolder;

public class GarnetMatrixBootstrap implements PluginBootstrap {

	@Override
	public void bootstrap(BootstrapContext context) {
		context.getLogger().info("GarnetMatrix bootstrap running...");
		GMEnchantHolder.bootstrapEnchants(context);
	}

}
