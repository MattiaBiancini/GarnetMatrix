package me.mbiancini.garnetmatrix.utils;

import me.mbiancini.garnetmatrix.constants.GMColorTheme;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.GameMode;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.Duration;
import java.util.function.BiFunction;

public class EnchantDamagerUtils {

	private static final BiFunction<Integer, Integer, Integer> ITEM_DAMAGE_FORMULA =
		(totalDamage, level) -> Math.max(1, totalDamage / (level + 1));

	private static final BiFunction<Integer, Integer, Integer> ARMOR_DAMAGE_FORMULA =
		(totalDamage, level) -> Math.max(1, (int) (0.6 * totalDamage + 0.4 * totalDamage / (level + 1)));

	private static final GMLogger _logger = new GMLogger();

	public static ItemStack damageItem(ItemStack item, Player player, int damagePreCalculus) {

		if (damagePreCalculus == 0 || player.getGameMode().equals(GameMode.CREATIVE)) {
			return item;
		}

		ItemMeta meta = item.getItemMeta();

		if (!(meta instanceof Damageable damageable)) {
			return item;
		}

		int damage = 0;
		int unbreakingLevel = getUnbreakingLevel(item);
		int maxDurability = item.getType().getMaxDurability();

		if(isArmor(item)) {
			damage = ARMOR_DAMAGE_FORMULA.apply(damagePreCalculus, unbreakingLevel);
		} else if(isTool(item)) {
			damage = ITEM_DAMAGE_FORMULA.apply(damagePreCalculus, unbreakingLevel);
		}


		if(damage == 0) {
			return item;
		}

		if((maxDurability - damageable.getDamage()) < damage) {
			if((maxDurability - damageable.getDamage()) == 0) {
				item.setAmount(0);
				return item;
			} else {
				damageable.setDamage(maxDurability);
				Title title = Title.title(
					Component.text("Damage exceed durability!").color(GMColorTheme.ACCENT_RED),
					Component.text("Change tool or next time it will break").color(GMColorTheme.RED),
					Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(2), Duration.ofMillis(500))
				);
				player.showTitle(title);
			}
		} else {
			damageable.setDamage(damageable.getDamage() + damage);
		}

		item.setItemMeta(damageable);
		return item;

	}

	public static int getUnbreakingLevel(ItemStack item) {
		if (item == null || !item.containsEnchantment(Enchantment.UNBREAKING)) {
			return 0;
		}

		return item.getEnchantmentLevel(Enchantment.UNBREAKING);
	}

	public static boolean isArmor(ItemStack item) {
		if (item == null) return false;
		EquipmentSlot slot = item.getType().getEquipmentSlot();
		return switch (slot) {
			case HEAD, CHEST, LEGS, FEET -> true;
			default -> false;
		};
	}

	public static boolean isTool(ItemStack item) {
		if (item == null) return false;
		String name = item.getType().name();
		return name.endsWith("_PICKAXE")
			|| name.endsWith("_AXE")
			|| name.endsWith("_SHOVEL")
			|| name.endsWith("_HOE")
			|| name.endsWith("_SWORD")
			|| name.equals("BOW")
			|| name.equals("CROSSBOW")
			|| name.equals("TRIDENT")
			|| name.equals("FISHING_ROD")
			|| name.equals("SHEARS")
			|| name.equals("FLINT_AND_STEEL");
	}

}
