package me.mbiancini.garnetmatrix.registries.enchantments;

import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.TagEntry;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.mbiancini.garnetmatrix.constants.EnchantmentWeight;
import me.mbiancini.garnetmatrix.constants.GarnetMatrixKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;

import java.util.Collections;
import java.util.Set;

@Getter
@AllArgsConstructor
public abstract class GMEnchant implements PluginBootstrap, Listener {

	private String enchantKey;
	private String enchantName;
	private TagKey<ItemType> itemType;
	private EnchantmentRegistryEntry.EnchantmentCost minimumCost;
	private EnchantmentRegistryEntry.EnchantmentCost maximumCost;
	private EquipmentSlotGroup activeSlots;
	private int anvilCost;
	private int maxLevel;
	private EnchantmentWeight enchantmentWeight;
	private boolean findInEnchantingTable;
	private boolean findInVillagerTrade;


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

		context.getLifecycleManager().registerEventHandler(
			LifecycleEvents.TAGS.preFlatten(RegistryKey.ENCHANTMENT),
			event -> {
				var key = EnchantmentKeys.create(Key.key(GarnetMatrixKeys.NAMESPACE + ":" + enchantKey));
				if (findInEnchantingTable) {
					event.registrar().addToTag(EnchantmentTagKeys.IN_ENCHANTING_TABLE, Collections.singleton(TagEntry.valueEntry(key)));
				}
				if (findInVillagerTrade) {
					event.registrar().addToTag(EnchantmentTagKeys.TRADEABLE, Collections.singleton(TagEntry.valueEntry(key)));
				}
			}
		);

	}

	protected abstract boolean _isEnchantActivating(Player player, ItemStack itemStack);

}
