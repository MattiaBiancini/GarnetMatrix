package me.mbiancini.garnetmatrix.registries.enchantments.implementations;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import me.mbiancini.garnetmatrix.constants.EnchantmentWeight;
import me.mbiancini.garnetmatrix.constants.GarnetMatrixColorTheme;
import me.mbiancini.garnetmatrix.constants.GarnetMatrixKeys;
import me.mbiancini.garnetmatrix.registries.enchantments.GMEnchant;
import me.mbiancini.garnetmatrix.utils.GMLogger;
import me.mbiancini.garnetmatrix.utils.TextComponentBuilder;
import me.mbiancini.garnetmatrix.utils.TreeUtils;
import net.kyori.adventure.key.Key;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;


public class GMTreeCapitator extends GMEnchant {

	public static final GMTreeCapitator INSTANCE = new GMTreeCapitator();

	private final GMLogger _logger = new GMLogger();

	public GMTreeCapitator() {
		super(
			"treecapitator",
			"Tree Capitator",
			ItemTypeTagKeys.AXES,
			EnchantmentRegistryEntry.EnchantmentCost.of(1, 1),
			EnchantmentRegistryEntry.EnchantmentCost.of(1, 1),
			EquipmentSlotGroup.MAINHAND,
			1,
			1,
			EnchantmentWeight.UNCOMMON,
			true,
			true
		);
	}

	@Override
	protected boolean _isEnchantActivating(Player player, ItemStack itemStack) {

		Enchantment treecapitatorEnchantment = RegistryAccess.registryAccess()
			.getRegistry(RegistryKey.ENCHANTMENT)
			.get(Key.key(GarnetMatrixKeys.NAMESPACE + ":" + this.getEnchantKey()));

		if(treecapitatorEnchantment == null) {
			_logger.sendAdminMessage(new TextComponentBuilder()
				.append(GarnetMatrixColorTheme.RED, "Impossible to find the enchantment: ")
				.append(GarnetMatrixColorTheme.ACCENT_RED, this.getEnchantKey())
				.append(GarnetMatrixColorTheme.RED, ".")
				.build()
			);

			return false;
		}

		return !player.isSneaking() && itemStack.hasItemMeta() && itemStack.getItemMeta().hasEnchant(treecapitatorEnchantment);
	}

	@EventHandler
	public void onPlayerBreakLogEvent(BlockBreakEvent e) {

		Player player = e.getPlayer();
		ItemStack mainHand = e.getPlayer().getInventory().getItemInMainHand();
		Block block = e.getBlock();

		if(_isEnchantActivating(player, mainHand)) {
			if(TreeUtils.breakTree(block, player)) {
				e.setCancelled(true);
			}
		}

	}
}
