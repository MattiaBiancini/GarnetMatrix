package me.mbiancini.garnetmatrix.utils;

import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Leaves;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Utility class for detecting and breaking vanilla Minecraft trees.
 * <p>
 * This is a simplified, single-purpose version: it only recognizes vanilla logs
 * and their matching vanilla leaves (no custom/configurable tree definitions).
 */
public class TreeUtils {

	/** Safety cap on the number of log blocks a single tree flood-fill will collect. */
	private static final int MAX_LOGS = 1024;

	/** Safety cap on the number of leaf blocks a single tree flood-fill will collect. */
	private static final int MAX_LEAVES = MAX_LOGS * 4;

	/** Max blocks a leaf-only search will traverse while hunting for a nearby log to identify species. */
	private static final int MAX_LEAF_LOG_SEARCH = 200;

	/**
	 * Minimum number of matching, non-persistent leaf blocks a log cluster must have attached
	 * before it's treated as a real tree. This is the key safeguard against destroying player
	 * builds: unlike leaves, vanilla logs have NO "player-placed" flag, so a log cabin wall,
	 * pillar, or staircase built from plain (non-stripped) logs is otherwise indistinguishable
	 * from a real trunk. Real trees have a canopy; built structures generally don't. (Mirrors
	 * tree-feller's "required-leaves" config option.)
	 */
	private static final int MIN_REQUIRED_LEAVES = 5;

	/**
	 * Log-count threshold below which a tree is considered "small" for the purposes of
	 * {@link #SMALL_TREE_MAX_LEAF_DISTANCE} vs {@link #LARGE_TREE_MAX_LEAF_DISTANCE}.
	 */
	private static final int SMALL_TREE_LOG_THRESHOLD = 7;

	/**
	 * Max Chebyshev distance a leaf may be from the nearest log, for trees with fewer than
	 * {@link #SMALL_TREE_LOG_THRESHOLD} logs. Small trees (short oak/birch/spruce saplings
	 * that grew into modest trees, etc.) have tight canopies right around the trunk, so a
	 * tight cap here keeps the search from bleeding into a neighboring tree a couple blocks away.
	 */
	private static final int SMALL_TREE_MAX_LEAF_DISTANCE = 2;

	/**
	 * Max Chebyshev distance a leaf may be from the nearest log, for trees with
	 * {@link #SMALL_TREE_LOG_THRESHOLD} or more logs. Bigger trees (tall spruce, jungle,
	 * dark oak, big oak) have wider canopies, so this is a little more permissive.
	 */
	private static final int LARGE_TREE_MAX_LEAF_DISTANCE = 3;

	/**
	 * All 26 neighbor offsets. Used for BOTH log and leaf connectivity: acacia trunks can
	 * step diagonally (up + sideways in a single move), so two consecutive log blocks may
	 * only share an edge rather than a face - a purely 6-directional flood-fill would treat
	 * them as disconnected and only cut down part of the tree.
	 */
	private static final int[][] TWENTY_SIX_OFFSETS;

	static {
		List<int[]> offsets = new ArrayList<>();
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					if (x == 0 && y == 0 && z == 0) continue;
					offsets.add(new int[]{x, y, z});
				}
			}
		}
		TWENTY_SIX_OFFSETS = offsets.toArray(new int[0][]);
	}

	/**
	 * Returns true if the given block is a log or leaves block that is part of
	 * a valid, connected vanilla tree (i.e. it belongs to a tree structure that
	 * contains at least one log).
	 */
	public boolean isBlockPartOfTree(Block block) {
		Material type = block.getType();
		if (!isLog(type) && !isLeaves(type)) {
			return false;
		}

		List<Block> treeBlocks = _getTreeBlocks(block);
		if (!treeBlocks.contains(block)) {
			return false;
		}

		int logCount = 0;
		int leafCount = 0;
		for (Block treeBlock : treeBlocks) {
			if (isLog(treeBlock.getType())) {
				logCount++;
			} else if (isLeaves(treeBlock.getType())) {
				leafCount++;
			}
		}

		return logCount > 0 && leafCount >= MIN_REQUIRED_LEAVES;
	}

	/**
	 * Breaks the entire tree that the given block belongs to, dropping items as if
	 * broken naturally by the given player's held tool.
	 *
	 * @return true if a tree was found and broken, false otherwise
	 */
	public boolean breakTree(Block block, Player player) {
		if (!isBlockPartOfTree(block)) {
			return false;
		}

		List<Block> treeBlocks = _getTreeBlocks(block);
		ItemStack tool = player.getInventory().getItemInMainHand();

		for (Block treeBlock : treeBlocks) {
			treeBlock.breakNaturally(tool);
		}
		return true;
	}

	/**
	 * Collects all log and leaves blocks belonging to the tree that {@code block} is part of.
	 * <p>
	 * Algorithm:
	 * 1. Identify the log species. If {@code block} is a log, use it directly. If it's leaves,
	 *    search nearby (through other leaves) for a connected log to identify the species.
	 * 2. Flood-fill (26-directional) through logs of that exact species to find the whole trunk/branches.
	 * 3. Flood-fill (26-directional) outward from those logs through matching, non-player-placed
	 *    leaves to find the canopy.
	 */
	private static List<Block> _getTreeBlocks(Block block) {
		List<Block> result = new ArrayList<>();
		Material type = block.getType();

		Block startLog;
		if (isLog(type)) {
			startLog = block;
		} else if (isLeaves(type)) {
			startLog = findNearbyLog(block);
			if (startLog == null) {
				// Leaves not connected to any log nearby - not treated as a tree.
				return result;
			}
		} else {
			return result;
		}

		Material logType = startLog.getType();
		Material leafType = getMatchingLeafType(logType);

		// Step 1: flood-fill connected logs of the same exact type.
		Set<Block> logs = new HashSet<>();
		Queue<Block> logQueue = new ArrayDeque<>();
		logs.add(startLog);
		logQueue.add(startLog);

		while (!logQueue.isEmpty() && logs.size() <= MAX_LOGS) {
			Block current = logQueue.poll();
			for (int[] offset : TWENTY_SIX_OFFSETS) {
				Block neighbor = current.getRelative(offset[0], offset[1], offset[2]);
				if (logs.contains(neighbor)) continue;
				if (neighbor.getType() == logType) {
					logs.add(neighbor);
					logQueue.add(neighbor);
				}
			}
		}
		result.addAll(logs);

		// Step 2: flood-fill connected, non-player-placed leaves of the matching type.
		if (leafType != null) {
			int maxLeafDistance = logs.size() < SMALL_TREE_LOG_THRESHOLD
				? SMALL_TREE_MAX_LEAF_DISTANCE
				: LARGE_TREE_MAX_LEAF_DISTANCE;

			Set<Block> leaves = new HashSet<>();
			Queue<Block> leafQueue = new ArrayDeque<>();

			for (Block log : logs) {
				for (int[] offset : TWENTY_SIX_OFFSETS) {
					Block neighbor = log.getRelative(offset[0], offset[1], offset[2]);
					if (leaves.contains(neighbor)) continue;
					if (isMatchingLeaves(neighbor, leafType)
						&& distanceToNearestLog(neighbor, logs) <= maxLeafDistance) {
						leaves.add(neighbor);
						leafQueue.add(neighbor);
					}
				}
			}

			while (!leafQueue.isEmpty() && leaves.size() <= MAX_LEAVES) {
				Block current = leafQueue.poll();
				for (int[] offset : TWENTY_SIX_OFFSETS) {
					Block neighbor = current.getRelative(offset[0], offset[1], offset[2]);
					if (leaves.contains(neighbor)) continue;
					if (isMatchingLeaves(neighbor, leafType)
						&& distanceToNearestLog(neighbor, logs) <= maxLeafDistance) {
						leaves.add(neighbor);
						leafQueue.add(neighbor);
					}
				}
			}
			result.addAll(leaves);
		}

		return result;
	}

	/** True for vanilla, non-stripped log/wood blocks (stripped logs are treated as player-made, not tree parts). */
	private static boolean isLog(Material type) {
		return Tag.LOGS.isTagged(type) && !type.name().startsWith("STRIPPED_");
	}

	/** True for any vanilla leaves block, regardless of persistence. */
	private static boolean isLeaves(Material type) {
		return Tag.LEAVES.isTagged(type);
	}

	/**
	 * A leaf block "matches" a tree if it's the correct leaf type for the log species
	 * and is not player-placed (i.e. not persistent - natural leaves generated by tree
	 * growth are non-persistent and can decay; player-placed leaves are persistent).
	 */
	private static boolean isMatchingLeaves(Block block, Material leafType) {
		if (block.getType() != leafType) {
			return false;
		}
		if (block.getBlockData() instanceof Leaves leavesData) {
			return !leavesData.isPersistent();
		}
		return true;
	}

	/** Chebyshev distance from a block to the closest block in the given set of log blocks. */
	private static int distanceToNearestLog(Block block, Set<Block> logs) {
		int min = Integer.MAX_VALUE;
		for (Block log : logs) {
			int dx = Math.abs(block.getX() - log.getX());
			int dy = Math.abs(block.getY() - log.getY());
			int dz = Math.abs(block.getZ() - log.getZ());
			int dist = Math.max(dx, Math.max(dy, dz));
			if (dist < min) {
				min = dist;
				if (min == 0) break;
			}
		}
		return min;
	}

	/** Derives the matching vanilla leaves material for a given log material (e.g. OAK_LOG -> OAK_LEAVES). */
	private static Material getMatchingLeafType(Material logType) {
		String name = logType.name();
		if (!name.endsWith("_LOG")) {
			return null;
		}
		String species = name.substring(0, name.length() - "_LOG".length());
		return Material.matchMaterial(species + "_LEAVES");
	}

	/**
	 * Searches outward through connected leaves (26-directional) for a nearby log block,
	 * used to identify a tree's species when starting the search from a leaf block.
	 */
	private static Block findNearbyLog(Block start) {
		Set<Block> visited = new HashSet<>();
		Queue<Block> queue = new ArrayDeque<>();
		visited.add(start);
		queue.add(start);

		while (!queue.isEmpty() && visited.size() < MAX_LEAF_LOG_SEARCH) {
			Block current = queue.poll();
			for (int[] offset : TWENTY_SIX_OFFSETS) {
				Block neighbor = current.getRelative(offset[0], offset[1], offset[2]);
				if (visited.contains(neighbor)) continue;
				visited.add(neighbor);

				if (isLog(neighbor.getType())) {
					return neighbor;
				}
				if (isLeaves(neighbor.getType())) {
					queue.add(neighbor);
				}
			}
		}
		return null;
	}
}