package me.mbiancini.garnetmatrix.registries.enchantments.implementations;

import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import me.mbiancini.garnetmatrix.constants.EnchantmentWeight;
import me.mbiancini.garnetmatrix.registries.enchantments.GMEnchant;
import org.bukkit.inventory.EquipmentSlotGroup;

public class GMVeinMiner extends GMEnchant {

	public GMVeinMiner() {
		super(
			"veinminer",
			"Vein Miner",
			ItemTypeTagKeys.PICKAXES,
			EnchantmentRegistryEntry.EnchantmentCost.of(1, 1),
			EnchantmentRegistryEntry.EnchantmentCost.of(1, 1),
			EquipmentSlotGroup.MAINHAND,
			1,
			1,
			EnchantmentWeight.UNCOMMON
		);
	}
}
