package me.mbiancini.garnetmatrix.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TreeUtils {

	public static boolean isBlockPartOfTree(Block block) {
		return block.getType() == Material.OAK_LOG;
	}

	public static boolean breakTree(Block block, Player player) {
		if(isBlockPartOfTree(block)) {

			return true;
		}
		return false;
	}

	private static List<Block> _getTreeBlocks(Block block) {
		List<Block> blocks = new ArrayList<>();
		blocks.add(block);
		return blocks;
	}

}
