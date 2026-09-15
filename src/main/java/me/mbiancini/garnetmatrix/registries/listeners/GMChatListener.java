package me.mbiancini.garnetmatrix.registries.listeners;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.mbiancini.garnetmatrix.constants.GMColorTheme;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GMChatListener implements Listener {

	@EventHandler
	public void onChat(AsyncChatEvent e) {

		List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

		Component message = e.message();
		List<Player> mentioned = _findMentionedPlayers(message, players);

		Component messageFormatted = _formatMessage(message, mentioned);

		e.message(messageFormatted);

		mentioned.forEach(player -> player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f));
	}

	private Component _formatMessage(Component message, List<Player> mentionedPlayers) {
		if (mentionedPlayers.isEmpty()) {
			return message;
		}

		String combinedRegex = mentionedPlayers.stream()
			.map(p -> "\\b" + Pattern.quote(p.getName()) + "\\b")
			.collect(Collectors.joining("|"));

		Pattern pattern = Pattern.compile(combinedRegex, Pattern.CASE_INSENSITIVE);

		TextReplacementConfig replacementConfig = TextReplacementConfig.builder()
			.match(pattern)
			.replacement((matchResult, builder) -> builder
				.content("@" + matchResult.group())
				.color(GMColorTheme.LIGHT_BLUE))
			.build();

		return message.replaceText(replacementConfig);
	}

	private List<Player> _findMentionedPlayers(Component message, List<Player> onlinePlayers) {
		return onlinePlayers.stream().filter(x -> _messageContainsPlayerName(message, x.getName())).toList();
	}

	private boolean _messageContainsPlayerName(Component message, String playerName) {
		String regex = "\\b" + Pattern.quote(playerName) + "\\b";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		return pattern.matcher(PlainTextComponentSerializer.plainText().serialize(message)).find();
	}

}
