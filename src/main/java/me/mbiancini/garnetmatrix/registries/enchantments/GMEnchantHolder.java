package me.mbiancini.garnetmatrix.registries.enchantments;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.mbiancini.garnetmatrix.constants.GarnetMatrixKeys;
import me.mbiancini.garnetmatrix.registries.enchantments.implementations.GMTreeCapitator;
import me.mbiancini.garnetmatrix.registries.enchantments.implementations.GMVeinMiner;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public class GMEnchantHolder {

	private static final List<GMEnchant> ENCHANTS = List.of(
		GMTreeCapitator.INSTANCE,
		GMVeinMiner.INSTANCE
	);


	public static void bootstrapEnchants(BootstrapContext context) {
		ENCHANTS.forEach(enchant -> {
			enchant.bootstrap(context);
		});
	}

	public static void registerEnchants(Plugin plugin) {

		ENCHANTS.forEach(enchant -> Bukkit.getPluginManager().registerEvents(enchant, plugin));
	}

}
