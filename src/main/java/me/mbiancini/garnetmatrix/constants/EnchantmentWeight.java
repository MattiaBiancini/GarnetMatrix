package me.mbiancini.garnetmatrix.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum EnchantmentWeight {

	COMMON(10),
	UNCOMMON(5),
	RARE(2),
	LEGENDARY(1);

	private final int weight;

}
