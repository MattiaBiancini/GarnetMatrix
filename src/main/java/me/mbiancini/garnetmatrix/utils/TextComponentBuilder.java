package me.mbiancini.garnetmatrix.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;

public class TextComponentBuilder {

	private final TextComponent.Builder textComponent;

	public TextComponentBuilder() {
		this.textComponent = Component.text();
	}

	public TextComponentBuilder append(TextColor color, String text) {
		textComponent.append(Component.text(text).color(color));
		return this;
	}

	public TextComponent build() {
		return this.textComponent.style(Style.style().color(null).build()).build();
	}

}
