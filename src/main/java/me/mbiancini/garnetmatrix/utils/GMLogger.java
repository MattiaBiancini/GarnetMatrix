package me.mbiancini.garnetmatrix.utils;

import me.mbiancini.garnetmatrix.constants.GMColorTheme;
import me.mbiancini.garnetmatrix.constants.GarnetMatrixKeys;
import me.mbiancini.garnetmatrix.constants.GMPermission;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GMLogger {

	private static final TextComponent prefix = new TextComponentBuilder()
		.append(GMColorTheme.GRAY, "[")
		.append(GMColorTheme.RED, GarnetMatrixKeys.PREFIX)
		.append(GMColorTheme.GRAY, "] ")
		.build();

	public void sendConsoleMessage(TextComponent message) {
		Bukkit.getConsoleSender().sendMessage(prefix.append(message));
	}

	public void sendAdminMessage(TextComponent message) {
		sendConsoleMessage(message);

		for (Player player : Bukkit.getOnlinePlayers()) {
			if(player.hasPermission(GMPermission.PERMISSION_ADMIN_NOTIFICATION)) {
				player.sendMessage(prefix.append(message));
			}
		}
	}
}
