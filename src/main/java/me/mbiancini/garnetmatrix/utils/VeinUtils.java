package me.mbiancini.garnetmatrix.utils;

import me.mbiancini.garnetmatrix.GarnetMatrixPlugin;
import me.mbiancini.garnetmatrix.constants.GarnetMatrixColorTheme;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class VeinUtils {

	private static final int BLOCK_CAP = 128;

	private static final List<Material> veinableMaterials = List.of(
		Material.COAL_ORE,
		Material.DEEPSLATE_COAL_ORE,
		Material.COPPER_ORE,
		Material.DEEPSLATE_COPPER_ORE,
		Material.IRON_ORE,
		Material.DEEPSLATE_IRON_ORE,
		Material.GOLD_ORE,
		Material.DEEPSLATE_GOLD_ORE,
		Material.REDSTONE_ORE,
		Material.DEEPSLATE_REDSTONE_ORE,
		Material.EMERALD_ORE,
		Material.DEEPSLATE_EMERALD_ORE,
		Material.LAPIS_ORE,
		Material.DEEPSLATE_LAPIS_ORE,
		Material.DIAMOND_ORE,
		Material.DEEPSLATE_DIAMOND_ORE,
		Material.NETHER_GOLD_ORE,
		Material.NETHER_QUARTZ_ORE,
		Material.AMETHYST_CLUSTER
	);

	public static boolean isVeinable(Material material) {
		return veinableMaterials.contains(material);
	}

	public static boolean breakVein(Block block, Player player, int experience) {

		if(isVeinable(block.getType())) {

			try {
				new BukkitRunnable() {

					@Override
					public void run() {

						List<Block> blocks = getVein(block);
						Location playerLocation = player.getLocation();
						int experienceToDrop = experience;

						if (blocks != null && !blocks.isEmpty()) {

							experienceToDrop = experience * blocks.size();

							List<ItemStack> loot = blocks.stream()
								.flatMap(x -> x.getDrops(player.getInventory().getItemInMainHand()).stream())
								.toList();

							blocks.forEach(block -> block.setType(Material.AIR));
							loot.forEach(x -> playerLocation.getWorld().dropItemNaturally(playerLocation, x));

						}

						ExperienceOrb orb = (ExperienceOrb) playerLocation.getWorld().spawnEntity(playerLocation, EntityType.EXPERIENCE_ORB);
						orb.setExperience(experienceToDrop);
					}

				}.runTask(GarnetMatrixPlugin.INSTANCE);
			} catch (Exception e) {
				e.printStackTrace();
			}

			return true;
		}

		return false;
	}

	public static List<Block> getVein(Block block) {

		Material material = block.getType();

		if(isVeinable(material)) {

			List<Block> vein = new ArrayList<>();
			Set<Block> visited = new HashSet<>();
			Deque<Block> queue = new ArrayDeque<>();

			visited.add(block);
			queue.add(block);

			while (!queue.isEmpty() && vein.size() < BLOCK_CAP) {
				Block current = queue.poll();
				vein.add(current);

				for (int dx = -1; dx <= 1; dx++) {
					for (int dy = -1; dy <= 1; dy++) {
						for (int dz = -1; dz <= 1; dz++) {
							if (dx == 0 && dy == 0 && dz == 0) continue;

							Block neighbor = current.getRelative(dx, dy, dz);

							if (visited.contains(neighbor)) continue;
							visited.add(neighbor);

							if (neighbor.getType() == material) {
								queue.add(neighbor);
							}
						}
					}
				}
			}

			return vein;

		}

		return null;

	}

}
