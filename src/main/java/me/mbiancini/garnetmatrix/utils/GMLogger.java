package me.mbiancini.garnetmatrix.utils;

import me.mbiancini.garnetmatrix.constants.GarnetMatrixColorTheme;
import me.mbiancini.garnetmatrix.constants.GarnetMatrixKeys;
import me.mbiancini.garnetmatrix.constants.GarnetMatrixPermission;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GMLogger {

	private static final TextComponent prefix = new TextComponentBuilder()
		.append(GarnetMatrixColorTheme.GRAY, "[")
		.append(GarnetMatrixColorTheme.RED, GarnetMatrixKeys.PREFIX)
		.append(GarnetMatrixColorTheme.GRAY, "] ")
		.build();

	public void sendConsoleMessage(TextComponent message) {
		Bukkit.getConsoleSender().sendMessage(prefix.append(message));
	}

	public void sendAdminMessage(TextComponent message) {
		sendConsoleMessage(message);

		for (Player player : Bukkit.getOnlinePlayers()) {
			if(player.hasPermission(GarnetMatrixPermission.PERMISSION_ADMIN_NOTIFICATION)) {
				player.sendMessage(prefix.append(message));
			}
		}
	}
}
