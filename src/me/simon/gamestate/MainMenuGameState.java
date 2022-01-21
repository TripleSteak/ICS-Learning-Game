/*
 * File: MainMenuGameState.java
 * Name: Simon Ou
 * Date: 1/11/2018
 * Description: First game state, includes animated intro as well as main menu and instructions
 */

package me.simon.gamestate;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import me.simon.Main;
import me.simon.util.Audio;
import me.simon.util.Render;

public class MainMenuGameState implements GameState {
	private BufferedImage SUPERMARKET;
	private BufferedImage BACKGROUND;
	private BufferedImage MATH_OPTION;
	private BufferedImage ENG_OPTION;

	private BufferedImage SOUND_ON;
	private BufferedImage SOUND_OFF;

	private Audio SUPERMARKET_FALL;
	private Audio MUSIC;

	private final String SIMON_STRING = "Simon's";
	private final String SUPERMARKET_STRING = "Supermarket";

	private final Font TITLE_FONT = new Font("PGTextje", Font.PLAIN, 48);
	private final Font OPTION_FONT = new Font("PGTextje", Font.PLAIN, 24);
	private final Font INSTRUCTION_FONT = new Font("Ubuntu", Font.PLAIN, 16);

	private final Color OPTION_COLOUR = new Color(186, 85, 211);

	/*
	 * Substate of the main menu, or where the user is currently at– 0: Intro
	 * animation 1: Name input 2: Menu selection 3: Instructions 5: Gamemode
	 * Selection
	 */
	public int subState = 0;
	private int animationState = 0;
	private int selectedOption = -1;

	private int supermarketY = -150;
	private int string1X = -100;
	private int string2X = 800;

	public void init() {
		SUPERMARKET = Render.loadImage("Supermarket");
		BACKGROUND = Render.loadImage("Menu Background");
		MATH_OPTION = Render.loadImage("Math Option");
		ENG_OPTION = Render.loadImage("English Option");

		SOUND_ON = Render.loadImage("Sound On");
		SOUND_OFF = Render.loadImage("Sound Off");

		SUPERMARKET_FALL = new Audio("Supermarket Fall");
		MUSIC = new Audio("Spazzmatica_Polka");
	}

	/*
	 * Updates object positions for introductory animation
	 */
	public void update(double delta) {
		if (subState == 0) {
			if (animationState == 113)
				SUPERMARKET_FALL.play();
			if (animationState >= 60 && animationState < 113)
				supermarketY += 8;
			if (animationState >= 150 && animationState < 180)
				string1X += 8;
			if (animationState >= 215 && animationState < 258)
				string2X -= 8;
			if (animationState == 300)
				subState = 1;
			animationState++;
		}
	}

	/*
	 * Renders buttons based on sub state
	 */
	public void render(Graphics g) {
		// Draw background and "Simon's Supermarket"
		Render.render(BACKGROUND, g, 0, 0);
		Render.renderCentre(SIMON_STRING, g, TITLE_FONT, string1X, 150,
				Color.BLACK);
		Render.renderCentre(SUPERMARKET_STRING, g, TITLE_FONT, string2X, 150,
				Color.BLACK);

		if (subState != 3 && subState != 5) // Draws supermarket building
			Render.renderCentre(SUPERMARKET, g, Main.WIDTH / 2, supermarketY);

		if (subState == 1) { // Draws name prompt
			Render.renderCentre("My name is ", g, OPTION_FONT, 200, 450,
					Color.BLACK);
			g.setColor(Color.WHITE);
			g.fillRect(270, 415, 200, 35);
			g.setColor(Color.BLACK);
			g.drawRect(270, 415, 200, 35);
			Render.render(Main.username, g, Main.BASE_FONT, 275, 440,
					Color.BLACK);
		} else if (subState == 2) { // Draws menu buttons
			g.setColor(Color.WHITE);
			switch (selectedOption) {
			case 0:
				g.fillRect(24, 404, 135, 67);
				break;
			case 1:
				g.fillRect(177, 404, 135, 67);
				break;
			case 2:
				g.fillRect(330, 404, 135, 67);
				break;
			case 3:
				g.fillRect(483, 404, 135, 67);
			}
			g.setColor(OPTION_COLOUR);
			g.fillRect(30, 410, 123, 55);
			g.fillRect(183, 410, 123, 55);
			g.fillRect(336, 410, 123, 55);
			g.fillRect(489, 410, 123, 55);
			g.setColor(Color.BLACK);
			g.drawRect(30, 410, 123, 55);
			g.drawRect(183, 410, 123, 55);
			g.drawRect(336, 410, 123, 55);
			g.drawRect(489, 410, 123, 55);
			Render.renderCentre("Play", g, OPTION_FONT, 91, 455, Color.WHITE);
			Render.renderCentre("Help", g, OPTION_FONT, 244, 455, Color.WHITE);
			if (Audio.enableAudio)
				Render.renderCentre(SOUND_ON, g, 397, 437);
			else
				Render.renderCentre(SOUND_OFF, g, 397, 437);
			Render.renderCentre("Quit", g, OPTION_FONT, 550, 455, Color.WHITE);
		} else if (subState == 3) { // Displays instructions
			g.setColor(Color.WHITE);
			g.fillRect(50, 150, Main.WIDTH - 100, Main.HEIGHT - 230);
			g.setColor(Color.BLACK);
			g.drawRect(50, 150, Main.WIDTH - 100, Main.HEIGHT - 230);
			Render.render("Hello " + Main.username + "! Welcome to Simon's Supermarket!", g, INSTRUCTION_FONT,
					60, 180, Color.BLACK);
			Render.render("Business is growing fast, and Simon needs help! Use your math and English", g, INSTRUCTION_FONT,
					60, 200, Color.BLACK);
			Render.render("skills to keep the business running and customers happy.", g, INSTRUCTION_FONT,
					60, 220, Color.BLACK);
			Render.render("Take your math skills and help operate the cashier! Different items will", g, INSTRUCTION_FONT,
					60, 250, Color.BLACK);
			Render.render("come in, and you're job is to solve the math question and find the total", g, INSTRUCTION_FONT,
					60, 270, Color.BLACK);
			Render.render("quantity!", g, INSTRUCTION_FONT,
					60, 290, Color.BLACK);
			Render.render("Or, you could try your hand in the aisles. Match the definition with the", g, INSTRUCTION_FONT,
					60, 320, Color.BLACK);
			Render.render("appropriate word to help Simon reorganize the shelves.", g, INSTRUCTION_FONT,
					60, 340, Color.BLACK);
			Render.render("Come across a cake? You're in luck, cake slices grant you one extra life", g, INSTRUCTION_FONT,
					60, 370, Color.BLACK);
			Render.render("in the math mode and a 3x score multiplier in the English mode. Good luck!", g, INSTRUCTION_FONT,
					60, 390, Color.BLACK);
		} else if (subState == 5) { // Displays gamemode selection prompt
			g.setColor(Color.WHITE);
			switch (selectedOption) {
			case 1:
				g.fillRect(Main.WIDTH / 2 - 256, 164, 212, 212);
				break;
			case 2:
				g.fillRect(Main.WIDTH / 2 + 44, 164, 212, 212);
			}
			g.setColor(Color.RED);
			g.fillRect(Main.WIDTH / 2 - 250, 170, 200, 200);
			g.setColor(Color.BLUE);
			g.fillRect(Main.WIDTH / 2 + 50, 170, 200, 200);
			Render.renderCentre(MATH_OPTION, g, Main.WIDTH / 2 - 150, 270);
			Render.renderCentre(ENG_OPTION, g, Main.WIDTH / 2 + 150, 270);
			Render.renderCentre("Math", g, OPTION_FONT, Main.WIDTH / 2 - 150,
					410, Color.BLACK);
			Render.renderCentre("English", g, OPTION_FONT,
					Main.WIDTH / 2 + 150, 410, Color.BLACK);
			Render.renderCentre("Highscore: " + Main.mathHighScore, g,
					Main.BASE_FONT, Main.WIDTH / 2 - 150, 170, Color.BLACK);
			Render.renderCentre("Highscore: " + Main.englishHighScore, g,
					Main.BASE_FONT, Main.WIDTH / 2 + 150, 170, Color.BLACK);
		}

		if (subState >= 3) { // Draws back button when necessary
			g.setColor(Color.WHITE);
			if (selectedOption == 0)
				g.fillRect(Main.WIDTH / 2 - 67, 404, 135, 67);
			g.setColor(OPTION_COLOUR);
			g.fillRect(Main.WIDTH / 2 - 61, 410, 123, 55);
			g.setColor(Color.BLACK);
			g.drawRect(Main.WIDTH / 2 - 61, 410, 123, 55);
			Render.renderCentre("Back", g, OPTION_FONT, Main.WIDTH / 2, 455,
					Color.WHITE);
		}
	}

	public void keyPressed(KeyEvent e) {
		if (subState == 1) {
			/*
			 * Checks for backspaces to remove characters or enter to continue
			 */
			if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				if (Main.username.length() > 0)
					Main.username = Main.username.substring(0,
							Main.username.length() - 1);
			} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				MUSIC.loop();
				subState = 2;
			}
		}
	}

	public void keyReleased(KeyEvent e) {

	}

	public void keyTyped(KeyEvent e) {
		if (subState == 1) {
			/*
			 * Checks whether key is printable before printing, or if max. name
			 * length was reached
			 */
			char c;
			try {
				c = e.getKeyChar();
				Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
				if (!Character.isISOControl(c) && c != KeyEvent.CHAR_UNDEFINED
						&& block != null
						&& block != Character.UnicodeBlock.SPECIALS
						&& Main.username.length() <= 12)
					Main.username += String.valueOf(c);
			} catch (Exception ex) {

			}
		}
	}

	/*
	 * Changes gamestate or substate based on chosen option
	 */
	public void mouseClicked(MouseEvent e) {
		if (subState == 2) {
			if (selectedOption == 0) {
				subState = 5;
				selectedOption = -1;
			} else if (selectedOption == 1) {
				subState = 3;
				selectedOption = -1;
			} else if (selectedOption == 2) {
				if (Audio.enableAudio) {
					Audio.enableAudio = false;
					Audio.stopAll();
				}
				else {
					Audio.enableAudio = true;
					MUSIC.loop();
				}
			} else if (selectedOption == 3)
				System.exit(0);
		} else if (subState == 5) {
			if (selectedOption == 1) {
				subState = 2;
				GameStateManager.setGameState(GameStateManager.MATH_GAME);
			} else if (selectedOption == 2)
				GameStateManager.setGameState(GameStateManager.ENGLISH_GAME);
		}
		if (subState >= 3 && selectedOption == 0) {
			subState = 2;
			selectedOption = -1;
		}
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	public void mousePressed(MouseEvent e) {

	}

	public void mouseReleased(MouseEvent e) {

	}

	public void mouseDragged(MouseEvent e) {

	}

	/*
	 * Detects if mouse hovers over any buttons
	 */
	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (subState == 2) {
			if (y >= 410 && y <= 465) {
				if (x >= 30 && x <= 163) {
					selectedOption = 0;
					return;
				} else if (x >= 183 && x <= 306) {
					selectedOption = 1;
					return;
				} else if (x >= 336 && x <= 459) {
					selectedOption = 2;
					return;
				} else if (x >= 489 && x <= 612) {
					selectedOption = 3;
					return;
				}
			}
		} else if (subState == 5) {
			if (y >= 170 && y <= 370) {
				if (x >= Main.WIDTH / 2 - 250 && x <= Main.WIDTH / 2 - 50) {
					selectedOption = 1;
					return;
				} else if (x >= Main.WIDTH / 2 + 50
						&& x <= Main.WIDTH / 2 + 250) {
					selectedOption = 2;
					return;
				}
			}
		}
		if (subState >= 3 && y >= 410 && y <= 465 && x >= Main.WIDTH / 2 - 61
				&& x <= Main.WIDTH / 2 + 61) {
			selectedOption = 0;
			return;
		}
		selectedOption = -1;
	}
}
