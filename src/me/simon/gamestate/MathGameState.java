/*
 * File: MathGameState.java
 * Name: Simon Ou
 * Date: 1/17/2018
 * Description: Math game state
 */

package me.simon.gamestate;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import me.simon.Main;
import me.simon.util.Audio;
import me.simon.util.Render;
import me.simon.util.StoreItems;

public class MathGameState implements GameState {
	private final int[] RELATIVE_X = new int[] { 28, 88, 148, 208 };
	private final int[] ITEM_Y = new int[] { 111, 170, 229 };
	private final int OPERATOR_RELATIVE_X = 280;
	private final int ITEM_2_RELATIVE_X = 330;

	private final int MOVEMENT_CONSTANT = 3;

	private BufferedImage CHECKOUT_COUNTER;
	private BufferedImage CONVEYOR_BELT_1;
	private BufferedImage CONVEYOR_BELT_2;
	private BufferedImage NEUTRAL_FACE;
	private BufferedImage HAPPY_FACE;
	private BufferedImage SAD_FACE;
	private BufferedImage CHECKMARK;
	private BufferedImage WRONG_CROSS;
	private BufferedImage GAME_OVER;

	private BufferedImage CRATE;

	private BufferedImage currentItem;

	private Audio RIGHT_ANSWER;
	private Audio WRONG_ANSWER;

	private final Color BACKGROUND_COLOUR = new Color(217, 244, 234);

	private final Font OPTION_FONT = new Font("PGTextje", Font.BOLD, 40);
	private final Font BIG_FONT = new Font("Ravie", Font.BOLD, 96);

	private int score = 0;
	private int streak = 0;
	private int lives = 3;
	private double multiplier = 1;

	private Operation operation = null;
	private boolean isMix = false;

	private int number1 = 0;
	private int number2 = 0;
	private int answer = 0;

	private String userAnswer = "";
	private boolean retry = false;

	private boolean questionDelay = false;
	private double delayTimer = 180;

	/*
	 * Substate of the math game– 0: Operation selection 1: Question animation 2:
	 * Prompt answer 3: Correct answer 4: Wrong answer 5: Finish question 6: Next
	 * question 7: Game over
	 */
	private int subState = 0;
	private int selectedOption = -1;
	private int conveyorBeltPos = 0;
	private int itemPos = 80 - Main.WIDTH;
	private int gameOverAnimation = 0;

	/*
	 * Generates a question of the given operation
	 */
	private void generateQuestion() {
		Random rand = new Random();
		if (isMix)
			operation = Operation.values()[rand.nextInt(3)];

		switch (operation) {
		case ADD:
			number1 = rand.nextInt(48) + 1;
			number2 = rand.nextInt(48) + 1;
			answer = number1 + number2;
			break;
		case SUBTRACT:
			number1 = rand.nextInt(48) + 1;
			number2 = rand.nextInt(number1) + 1;
			answer = number1 - number2;
			break;
		case MULTIPLY:
			number1 = rand.nextInt(12) + 1;
			number2 = rand.nextInt(12) + 1;
			answer = number1 * number2;
		}
		currentItem = StoreItems.getRandom(true);
		subState = 1;
	}

	/*
	 * Adds to score and applies powerup if correct
	 */
	private void correctAnswer() {
		int addition = (int) Math.round((0.2 * streak * streak + 1) * multiplier);
		score += addition >= 1 ? addition : 1;
		retry = false;
		RIGHT_ANSWER.play();
		if (StoreItems.isPowerup(currentItem)) // Checks powerup
			lives++;
		questionDelay = true;
	}

	/*
	 * Loads images and sound files
	 */
	public void init() {
		CHECKOUT_COUNTER = Render.loadImage("Checkout Counter");
		CONVEYOR_BELT_1 = Render.loadImage("Conveyor Belt");
		CONVEYOR_BELT_2 = Render.loadImage("Conveyor Belt");

		NEUTRAL_FACE = Render.loadImage("Neutral Face");
		HAPPY_FACE = Render.loadImage("Happy Face");
		SAD_FACE = Render.loadImage("Sad Face");

		CHECKMARK = Render.loadImage("Checkmark");
		WRONG_CROSS = Render.loadImage("Wrong Cross");
		GAME_OVER = Render.loadImage("Math Game Over");

		CRATE = Render.loadImage("Crate");

		RIGHT_ANSWER = new Audio("Correct Answer");
		WRONG_ANSWER = new Audio("Wrong Answer");
	}

	/*
	 * Updates movement of conveyor belt and items
	 */
	public void update(double delta) {
		if (questionDelay) { // Delay for correct/wrong answer screens
			if (delayTimer == 0) {
				delayTimer = 180;
				questionDelay = false;
				userAnswer = "";
				subState = 5;
			} else
				delayTimer--;
		}
		if (subState == 1) { // Moves conveyor belt and items (before question)
			conveyorBeltPos += MOVEMENT_CONSTANT;
			conveyorBeltPos %= Main.WIDTH;
			itemPos += MOVEMENT_CONSTANT;
			if (itemPos >= 31)
				subState = 2;
		} else if (subState == 5) { // Moves conveyor belt and items (after
									// question)
			conveyorBeltPos += MOVEMENT_CONSTANT;
			conveyorBeltPos %= Main.WIDTH;
			itemPos += MOVEMENT_CONSTANT;
			if (itemPos > Main.WIDTH + 20) {
				itemPos = 80 - Main.WIDTH;
				subState = 6;
			}
		} else if (subState == 7) { // Game over animation
			gameOverAnimation++;
			if (gameOverAnimation > 300) {
				gameOverAnimation = 0;
				GameStateManager.setGameState(GameStateManager.MAIN_MENU);
				subState = 0;
			}
		}
	}

	/*
	 * Renders all images to screen, including situational buttons and items
	 */
	public void render(Graphics g) {
		// Draws background, score, lives, and conveyor belt
		g.setColor(BACKGROUND_COLOUR);
		g.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);
		Render.render("Score", g, Main.HEADING_FONT, 20, 60, Color.BLACK);
		Render.render(String.valueOf(score), g, Main.BASE_FONT, 90, 60, Color.BLACK);
		Render.render("Lives", g, Main.HEADING_FONT, 220, 60, Color.BLACK);
		Render.render(String.valueOf(lives), g, Main.BASE_FONT, 290, 60, Color.BLACK);
		Render.render(CONVEYOR_BELT_1, g, conveyorBeltPos, 80);
		Render.render(CONVEYOR_BELT_2, g, conveyorBeltPos - Main.WIDTH, 80);

		// Draws different expression on person based on answer validity
		if (subState == 3)
			Render.renderCentre(HAPPY_FACE, g, 170, 310);
		else if (subState == 4)
			Render.renderCentre(SAD_FACE, g, 170, 310);
		else
			Render.renderCentre(NEUTRAL_FACE, g, 170, 310);

		Render.render(CHECKOUT_COUNTER, g, 0, Main.HEIGHT - 200);

		if (subState == 0) { // Draws select mode buttons
			g.setColor(Color.WHITE);
			switch (selectedOption) {
			case 0:
				g.fillRect(394, 274, 92, 92);
				break;
			case 1:
				g.fillRect(494, 274, 92, 92);
				break;
			case 2:
				g.fillRect(394, 374, 92, 92);
				break;
			case 3:
				g.fillRect(494, 374, 92, 92);
			}
			g.setColor(Color.RED);
			g.fillRect(400, 280, 80, 80);
			g.setColor(Color.GREEN);
			g.fillRect(500, 280, 80, 80);
			g.setColor(Color.BLUE);
			g.fillRect(400, 380, 80, 80);
			g.setColor(Color.YELLOW);
			g.fillRect(500, 380, 80, 80);
			g.setColor(Color.BLACK);
			g.drawRect(400, 280, 80, 80);
			g.drawRect(500, 280, 80, 80);
			g.drawRect(400, 380, 80, 80);
			g.drawRect(500, 380, 80, 80);
			Render.renderCentre("+", g, OPTION_FONT, 440, 345, Color.BLACK);
			Render.renderCentre("-", g, OPTION_FONT, 540, 345, Color.BLACK);
			Render.renderCentre("x", g, OPTION_FONT, 440, 445, Color.BLACK);
			Render.renderCentre("MIX", g, OPTION_FONT, 540, 445, Color.BLACK);
		} else if (subState >= 1) { // Draws items on conveyor belt
			int value1 = number1;
			int value2 = number2;
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < 4; j++) {
					if (value1 >= 10) {
						Render.renderCentre(CRATE, g, itemPos + RELATIVE_X[j], ITEM_Y[i]);
						Render.renderCentre(currentItem, g, itemPos + RELATIVE_X[j], ITEM_Y[i]);
						value1 -= 10;
					} else if (value1 > 0) {
						Render.renderCentre(currentItem, g, itemPos + RELATIVE_X[j], ITEM_Y[i]);
						value1--;
					}
					if (operation != Operation.MULTIPLY) {
						if (value2 >= 10) {
							Render.renderCentre(CRATE, g, itemPos + ITEM_2_RELATIVE_X + RELATIVE_X[j], ITEM_Y[i]);
							Render.renderCentre(currentItem, g, itemPos + ITEM_2_RELATIVE_X + RELATIVE_X[j], ITEM_Y[i]);
							value2 -= 10;
						} else if (value2 > 0) {
							Render.renderCentre(currentItem, g, itemPos + ITEM_2_RELATIVE_X + RELATIVE_X[j], ITEM_Y[i]);
							value2--;
						}
					} else {
						Render.renderCentre(String.valueOf(number2), g, BIG_FONT, itemPos + 450, 245, Color.WHITE);
					}
					Render.renderCentre(operation.symbol, g, BIG_FONT, itemPos + OPERATOR_RELATIVE_X, 245, Color.WHITE);
				}
			}

			if (subState == 2) { // Draws answer prompt
				Render.renderCentre(number1 + " " + operation.symbol + " " + number2 + " = ?", g, OPTION_FONT, 500, 330,
						Color.BLACK);
				g.setColor(Color.WHITE);
				g.fillRect(430, 350, 140, 40);
				g.setColor(Color.BLACK);
				g.drawRect(430, 350, 140, 40);
				Render.renderCentre(userAnswer, g, Main.BASE_FONT, 500, 380, Color.BLACK);
				if (retry)
					Render.renderCentre("Try again!", g, OPTION_FONT, 500, 440, Color.BLACK);
			} else if (subState == 3)
				Render.renderCentre(CHECKMARK, g, 500, 350);
			else if (subState == 4)
				Render.renderCentre(WRONG_CROSS, g, 500, 350);
			else if (subState == 6) { // Draws buttons for continue or change mode
				g.setColor(Color.WHITE);
				switch (selectedOption) {
				case 0:
					g.fillRect(394, 274, 192, 92);
					break;
				case 1:
					g.fillRect(394, 374, 192, 92);
				}
				g.setColor(Color.ORANGE);
				g.fillRect(400, 280, 180, 80);
				g.fillRect(400, 380, 180, 80);
				g.setColor(Color.BLACK);
				g.drawRect(400, 280, 180, 80);
				g.drawRect(400, 380, 180, 80);
				Render.renderCentre("Continue", g, OPTION_FONT, 490, 345, Color.BLACK);
				Render.renderCentre("Change Mode", g, OPTION_FONT, 490, 445, Color.BLACK);
			} else if (subState == 7) { // Draws game over animation
				if (gameOverAnimation > 24) {
					Render.render(GAME_OVER, g, 0, 0);
					Render.renderCentre("Game Over!", g, OPTION_FONT, Main.WIDTH / 2, 200, Color.BLACK);
					Render.renderCentre("Highscore: " + Main.mathHighScore, g, Main.BASE_FONT, Main.WIDTH / 2, 300,
							Color.BLACK);
				} else
					Render.render(GAME_OVER, g, 0, gameOverAnimation * 20 - Main.HEIGHT);
			}
		}
	}

	public void keyPressed(KeyEvent e) {
		if (subState == 2) {
			/*
			 * Checks for backspaces to remove characters or enter to validate the answer
			 */
			if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				if (userAnswer.length() > 0)
					userAnswer = userAnswer.substring(0, userAnswer.length() - 1);
			} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (userAnswer.length() == 0)
					return;
				if (Integer.parseInt(userAnswer) == answer) {
					streak++;
					correctAnswer();
					subState = 3;
				} else {
					if (!retry) {
						retry = true;
						multiplier = 0.5;
						userAnswer = "";
					} else {
						WRONG_ANSWER.play();
						retry = false;
						streak = 0;
						lives--;
						if (lives < 1) {
							if (score > Main.mathHighScore)
								Main.mathHighScore = score;
							score = 0;
							lives = 3;
							userAnswer = "";
							subState = 7;
						} else {
							subState = 4;
							questionDelay = true;
						}
					}
				}
			}
		}
	}

	public void keyReleased(KeyEvent e) {

	}

	public void keyTyped(KeyEvent e) {
		if (subState == 2) {
			/*
			 * Checks if key is printable and is a number
			 */
			char c;
			try {
				c = e.getKeyChar();
				Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
				if (!Character.isISOControl(c) && c != KeyEvent.CHAR_UNDEFINED && block != null
						&& block != Character.UnicodeBlock.SPECIALS && userAnswer.length() <= 8 && c - '0' >= 0
						&& c - '0' < 10)
					userAnswer += c;
			} catch (Exception ex) {

			}
		}
	}

	/*
	 * Changes gamestate or substate based on chosen option, or checks for correct
	 * answer
	 */
	public void mouseClicked(MouseEvent e) {
		if (subState == 0) {
			if (selectedOption == 0) {
				isMix = false;
				operation = Operation.ADD;
				selectedOption = -1;
				multiplier = 1;
				generateQuestion();
			} else if (selectedOption == 1) {
				isMix = false;
				operation = Operation.SUBTRACT;
				selectedOption = -1;
				multiplier = 1;
				generateQuestion();
			} else if (selectedOption == 2) {
				isMix = false;
				operation = Operation.MULTIPLY;
				selectedOption = -1;
				multiplier = 1;
				generateQuestion();
			} else if (selectedOption == 3) {
				isMix = true;
				selectedOption = -1;
				multiplier = 1.5;
				generateQuestion();
			}
		} else if (subState == 6) {
			if (selectedOption == 0) {
				multiplier = isMix ? 1.5 : 1.0;
				generateQuestion();
			} else if (selectedOption == 1) {
				subState = 0;
			}
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

	public void mouseMoved(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (subState == 0) {
			if (y >= 280 && y <= 360) {
				if (x >= 400 && x <= 480) {
					selectedOption = 0;
					return;
				}
				if (x >= 500 && x <= 580) {
					selectedOption = 1;
					return;
				}
			}
			if (y >= 380 && y <= 460) {
				if (x >= 400 && x <= 480) {
					selectedOption = 2;
					return;
				}
				if (x >= 500 && x <= 580) {
					selectedOption = 3;
					return;
				}
			}
		} else if (subState == 6) {
			if (x >= 400 && x <= 580) {
				if (y >= 280 && y <= 360) {
					selectedOption = 0;
					return;
				}
				if (y >= 380 && y <= 460) {
					selectedOption = 1;
					return;
				}
			}
		}
		selectedOption = -1;
	}

	/*
	 * Current operation
	 */
	public enum Operation {
		ADD("+"), SUBTRACT("-"), MULTIPLY("x");

		String symbol;

		Operation(String symbol) {
			this.symbol = symbol;
		}
	}
}
