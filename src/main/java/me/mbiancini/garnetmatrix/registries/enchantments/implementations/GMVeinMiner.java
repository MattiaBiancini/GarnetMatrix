package me.mbiancini.garnetmatrix.registries.enchantments.implementations;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import me.mbiancini.garnetmatrix.constants.GMEnchantmentWeight;
import me.mbiancini.garnetmatrix.constants.GMColorTheme;
import me.mbiancini.garnetmatrix.constants.GarnetMatrixKeys;
import me.mbiancini.garnetmatrix.registries.enchantments.GMEnchant;
import me.mbiancini.garnetmatrix.utils.GMLogger;
import me.mbiancini.garnetmatrix.utils.TextComponentBuilder;
import me.mbiancini.garnetmatrix.utils.VeinUtils;
import net.kyori.adventure.key.Key;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;

public class GMVeinMiner extends GMEnchant {

	public static final GMVeinMiner INSTANCE = new GMVeinMiner();

	private final GMLogger _logger = new GMLogger();

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
			GMEnchantmentWeight.UNCOMMON,
			true,
			true
		);
	}

	@Override
	protected boolean _isEnchantActivating(Player player, ItemStack itemStack) {
		Enchantment veinminerEnchantment = RegistryAccess.registryAccess()
			.getRegistry(RegistryKey.ENCHANTMENT)
			.get(Key.key(GarnetMatrixKeys.NAMESPACE + ":" + this.getEnchantKey()));

		if(veinminerEnchantment == null) {
			_logger.sendAdminMessage(new TextComponentBuilder()
				.append(GMColorTheme.RED, "Impossible to find the enchantment: ")
				.append(GMColorTheme.ACCENT_RED, this.getEnchantKey())
				.append(GMColorTheme.RED, ".")
				.build()
			);

			return false;
		}

		return !player.isSneaking() && itemStack.hasItemMeta() && itemStack.getItemMeta().hasEnchant(veinminerEnchantment);
}

@EventHandler
public void onPlayerBreakLogEvent(BlockBreakEvent e) {

	Player player = e.getPlayer();
	ItemStack mainHand = e.getPlayer().getInventory().getItemInMainHand();
	Block block = e.getBlock();

	if(_isEnchantActivating(player, mainHand)) {
		e.setCancelled(true);
		VeinUtils.breakVein(block, player, e.getExpToDrop());
	}

}
}
