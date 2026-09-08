package me.mbiancini.garnetmatrix.registries.enchantments;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.tag.TagKey;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.mbiancini.garnetmatrix.constants.EnchantmentWeight;
import me.mbiancini.garnetmatrix.constants.GarnetMatrixKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemType;

@Getter
@AllArgsConstructor
public abstract class GMEnchant implements PluginBootstrap {

	private String enchantKey;
	private String enchantName;
	private TagKey<ItemType> itemType;
	private EnchantmentRegistryEntry.EnchantmentCost minimumCost;
	private EnchantmentRegistryEntry.EnchantmentCost maximumCost;
	private EquipmentSlotGroup activeSlots;
	private int anvilCost;
	private int maxLevel;
	private EnchantmentWeight enchantmentWeight;


	@Override
	public void bootstrap(BootstrapContext context) {

		context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.compose().newHandler(event -> event.registry().register(
			EnchantmentKeys.create(Key.key(GarnetMatrixKeys.NAMESPACE + ":" + enchantKey)),
			b -> b.description(Component.text(enchantName))
				.supportedItems(event.getOrCreateTag(itemType))
				.anvilCost(anvilCost)
				.maxLevel(maxLevel)
				.weight(enchantmentWeight.getWeight())
				.minimumCost(minimumCost)
				.maximumCost(maximumCost)
				.activeSlots(activeSlots)
		)));

	}

}
