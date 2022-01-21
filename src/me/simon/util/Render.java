/*
 * File: Render.java
 * Name: Simon Ou
 * Date: 1/12/2018
 * Description: Final class that makes rendering more convenient (centered, formatting, etc.)
 */

package me.simon.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import me.simon.Main;

public final class Render {
	/*
	 * Loads image from file
	 */
	public static BufferedImage loadImage(String imageName) {
		try {
			return ImageIO.read(Main.class
					.getResourceAsStream("/assets/textures/" + imageName
							+ ".png"));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * Renders an image at given coordinates
	 */
	public static void render(BufferedImage img, Graphics g, int x, int y) {
		g.drawImage(img, x, y, null);
	}
	
	/*
	 * Renders a string at given coordinates
	 */
	public static void render(String s, Graphics g, Font f, int x, int y,
			Color c) {
		g.setColor(c);
		g.setFont(f);
		g.drawString(s, x, y);
	}

	/*
	 * Renders an image centered at given coordinates
	 */
	public static void renderCentre(BufferedImage img, Graphics g, int x, int y) {
		g.drawImage(img, x - img.getWidth() / 2, y - img.getHeight() / 2, null);
	}

	/*
	 * Renders a string centered at given coordinates
	 */
	public static void renderCentre(String s, Graphics g, Font f, int x, int y,
			Color c) {
		FontMetrics fm = g.getFontMetrics(f);

		g.setColor(c);
		g.setFont(f);
		g.drawString(s, x - fm.stringWidth(s) / 2, y - fm.getHeight() / 2 + 14);
	}
}
