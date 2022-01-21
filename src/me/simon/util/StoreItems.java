/*
 * File: StoreItems.java
 * Name: Simon Ou
 * Date: 1/21/2018
 * Description: Small class that holds all the different store item images
 */

package me.simon.util;

import java.awt.image.BufferedImage;
import java.util.Random;

public final class StoreItems {
	private static final int NUM_OF_ITEMS = 39;
	private static final int NUM_OF_POWERUPS = 1;
	private static final BufferedImage[] STORE_ITEMS = new BufferedImage[NUM_OF_ITEMS];
	public static final BufferedImage[] POWERUPS = new BufferedImage[NUM_OF_POWERUPS];

	public static void init() {
		STORE_ITEMS[0] = Render.loadImage("Item Apple");
		STORE_ITEMS[1] = Render.loadImage("Item Baguette");
		STORE_ITEMS[2] = Render.loadImage("Item Banana");
		STORE_ITEMS[3] = Render.loadImage("Item Beef");
		STORE_ITEMS[4] = Render.loadImage("Item Blueberry");
		STORE_ITEMS[5] = Render.loadImage("Item Bread");
		STORE_ITEMS[6] = Render.loadImage("Item Broccoli");
		STORE_ITEMS[7] = Render.loadImage("Item Butter");
		STORE_ITEMS[8] = Render.loadImage("Item Carrot");
		STORE_ITEMS[9] = Render.loadImage("Item Cheese");
		STORE_ITEMS[10] = Render.loadImage("Item Cherry");
		STORE_ITEMS[11] = Render.loadImage("Item Chicken");
		STORE_ITEMS[12] = Render.loadImage("Item Coconut");
		STORE_ITEMS[13] = Render.loadImage("Item Corn");
		STORE_ITEMS[14] = Render.loadImage("Item Croissant");
		STORE_ITEMS[15] = Render.loadImage("Item Cucumber");
		STORE_ITEMS[16] = Render.loadImage("Item Eggplant");
		STORE_ITEMS[17] = Render.loadImage("Item Grapes");
		STORE_ITEMS[18] = Render.loadImage("Item Juice");
		STORE_ITEMS[19] = Render.loadImage("Item Lemon");
		STORE_ITEMS[20] = Render.loadImage("Item Mango");
		STORE_ITEMS[21] = Render.loadImage("Item Milk");
		STORE_ITEMS[22] = Render.loadImage("Item Mushroom");
		STORE_ITEMS[23] = Render.loadImage("Item Onion");
		STORE_ITEMS[24] = Render.loadImage("Item Orange");
		STORE_ITEMS[25] = Render.loadImage("Item Pasta");
		STORE_ITEMS[26] = Render.loadImage("Item Peach");
		STORE_ITEMS[27] = Render.loadImage("Item Pear");
		STORE_ITEMS[28] = Render.loadImage("Item Pepper");
		STORE_ITEMS[29] = Render.loadImage("Item Pineapple");
		STORE_ITEMS[30] = Render.loadImage("Item Pork");
		STORE_ITEMS[31] = Render.loadImage("Item Potato");
		STORE_ITEMS[32] = Render.loadImage("Item Raspberry");
		STORE_ITEMS[33] = Render.loadImage("Item Rice");
		STORE_ITEMS[34] = Render.loadImage("Item Sausage");
		STORE_ITEMS[35] = Render.loadImage("Item Strawberry");
		STORE_ITEMS[36] = Render.loadImage("Item Tomato");
		STORE_ITEMS[37] = Render.loadImage("Item Watermelon");
		STORE_ITEMS[38] = Render.loadImage("Item Yogurt");

		POWERUPS[0] = Render.loadImage("Powerup Cake");
	}

	public static boolean isPowerup(BufferedImage image) {
		for (BufferedImage check : POWERUPS) {
			if (image == check)
				return true;
		}
		return false;
	}

	public static BufferedImage getRandom(boolean hasPowerup) {
		if (hasPowerup && (new Random()).nextInt(20) == 0)
			return POWERUPS[(new Random()).nextInt(NUM_OF_POWERUPS)];
		return STORE_ITEMS[(new Random()).nextInt(NUM_OF_ITEMS)];
	}
}
