/*
 * File: EnglishGameState.java
 * Name: Simon Ou
 * Date: 1/21/2018
 * Description: English game state
 */

package me.simon.gamestate;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Random;

import me.simon.Main;
import me.simon.util.Audio;
import me.simon.util.Render;
import me.simon.util.StoreItems;

public class EnglishGameState implements GameState {
	private final LinkedHashMap<String, String> LEVEL_1_MAP = new LinkedHashMap<String, String>();
	private final LinkedHashMap<String, String> LEVEL_2_MAP = new LinkedHashMap<String, String>();
	private final LinkedHashMap<String, String> LEVEL_3_MAP = new LinkedHashMap<String, String>();
	private final LinkedHashMap<String, String> LEVEL_4_MAP = new LinkedHashMap<String, String>();
	private final LinkedHashMap<String, String> LEVEL_5_MAP = new LinkedHashMap<String, String>();
	private final LinkedHashMap<String, String> LEVEL_6_MAP = new LinkedHashMap<String, String>();
	private final LinkedHashMap<String, String> LEVEL_7_MAP = new LinkedHashMap<String, String>();
	private final LinkedHashMap<String, String> LEVEL_8_MAP = new LinkedHashMap<String, String>();
	private final LinkedHashMap<String, String> LEVEL_9_MAP = new LinkedHashMap<String, String>();
	private final LinkedHashMap<String, String> LEVEL_10_MAP = new LinkedHashMap<String, String>();

	private final int[] ITEM_X_RELATIVE = new int[] { 0, 80, 160, 240, 320, 400 };
	private final int[] ITEM_Y_RELATIVE = new int[] { 0, 97, 194 };
	private final int[] SUBITEM_RELATIVE = new int[] { 0, 0, 40, 0, 0, 40, 40,
			40 };
	private final int SHELF_X = 83;
	private final int SHELF_Y = 25;

	private BufferedImage RED_CURTAINS;
	private BufferedImage SHELVES;
	private BufferedImage BOX;
	private BufferedImage NEUTRAL_FACE;
	private BufferedImage HAPPY_FACE;
	private BufferedImage SAD_FACE;
	private BufferedImage CHECKMARK;
	private BufferedImage WRONG_CROSS;

	private final int numOfItems = 18;
	private BufferedImage[] SHELF_ITEMS = new BufferedImage[numOfItems];
	private BufferedImage[] WAITING_ITEMS = new BufferedImage[9];

	private Audio RIGHT_ANSWER;
	private Audio WRONG_ANSWER;

	private final Color BACKGROUND_COLOUR = new Color(249, 255, 211);

	private final Font OPTION_FONT = new Font("PGTextje", Font.BOLD, 32);
	private final Font BIG_FONT = new Font("Ravie", Font.BOLD, 96);
	private final Font DEFINITION_FONT = new Font("Arial", Font.BOLD, 14);
	private final Font WORD_FONT = new Font("Ubuntu", Font.PLAIN, 18);

	private int level = 1;
	private int score = 0;
	private int[] wordPositions = new int[18];
	private boolean[] wordsLeft = new boolean[9];
	private int currentWord = -1;
	private int currentWordPos = -1;
	private int waitingX = 400;
	private int waitingY = 365;
	private int targetX = 0;
	private int targetY = 0;
	private String userAnswer = "";

	private boolean questionDelay = false;
	private boolean rightAnswer = false;
	private int delayTimer = 180;
	private int finalDelay = 120;

	/*
	 * Substate of the English game– 0: Transition 1: Question 2: Correct answer
	 * 3: Wrong answer 4: Try again 5: Next question 6: Final 7: Victory
	 */
	private int subState = 0;
	private int selectedOption = -1;
	private int curtainPos = 0;
	private int waitingDist = 30;

	/*
	 * Creates a new level with randomized word locations
	 */
	private void generateLevel() {
		for (int i = 0; i < numOfItems; i++)
			wordPositions[i] = -1;
		Random rand = new Random();
		for (int i = 0; i < numOfItems; i++)
			SHELF_ITEMS[i] = StoreItems.getRandom(false);
		for (int i = 0; i < getMap(level).size(); i++) {
			int pos = rand.nextInt(numOfItems);
			if (SHELF_ITEMS[pos] != null) {
				WAITING_ITEMS[i] = StoreItems.getRandom(true);
				SHELF_ITEMS[pos] = null;
				wordPositions[pos] = i;
			} else {
				i--;
				continue;
			}
		}
		newWord();
	}

	/*
	 * Changes word
	 */
	private void newWord() {
		boolean remaining = false;
		for (int i = 0; i < getMap(level).size(); i++)
			if (wordsLeft[i]) {
				remaining = true;
				break;
			}
		while (remaining) {
			currentWord = (new Random()).nextInt(getMap(level).size());
			if (wordsLeft[currentWord])
				break;
		}
		if (!remaining) {
			subState = 6;
			for (int i = 0; i < 9; i++)
				wordsLeft[currentWord] = true;
			return;
		} else {
			wordsLeft[currentWord] = false;
			for (int i = 0; i < numOfItems; i++) {
				if (wordPositions[i] == currentWord)
					currentWordPos = i;
			}
		}
	}

	/*
	 * Adds to score and applies powerup if correct
	 */
	private void correctAnswer() {
		score += level * 10;
		RIGHT_ANSWER.play();
		if (StoreItems.isPowerup(WAITING_ITEMS[currentWord])) // Checks powerup
			score += level * 20;
		questionDelay = true;
		rightAnswer = true;
	}

	/*
	 * Locates empty shelf spot and sets waiting item's target
	 */
	private void setTarget() {
		targetX = SHELF_X + ITEM_X_RELATIVE[currentWordPos % 6];
		targetY = SHELF_Y + ITEM_Y_RELATIVE[currentWordPos / 6];
	}

	/*
	 * Loads images and sound files, as well as all words and respective
	 * definitions
	 */
	public void init() {
		RED_CURTAINS = Render.loadImage("Red Curtains");
		SHELVES = Render.loadImage("Shelves");
		BOX = Render.loadImage("Box");

		NEUTRAL_FACE = Render.loadImage("Neutral Face");
		HAPPY_FACE = Render.loadImage("Happy Face");
		SAD_FACE = Render.loadImage("Sad Face");

		CHECKMARK = Render.loadImage("Checkmark");
		WRONG_CROSS = Render.loadImage("Wrong Cross");

		RIGHT_ANSWER = new Audio("Correct Answer");
		WRONG_ANSWER = new Audio("Wrong Answer");

		generateMap();
	}

	/*
	 * Updates movement of conveyor belt and items
	 */
	public void update(double delta) {
		if (questionDelay) { // Delay for checkmark and wrong answer
			if (delayTimer == 0) {
				delayTimer = 180;
				questionDelay = false;
				userAnswer = "";
				if (rightAnswer) {
					subState = 5;
					setTarget();
				} else
					subState = 4;
			} else
				delayTimer--;
		}
		if (subState == 0) { // Creates level
			curtainPos++;
			if (curtainPos >= 24 && level > 10) {
				subState = 7;
			} else if (curtainPos >= 276) {
				curtainPos = 24;
				subState = 1;
				for (int i = 0; i < 9; i++)
					wordsLeft[i] = true;
				generateLevel();
				((MainMenuGameState) GameStateManager.GAME_STATES[GameStateManager.MAIN_MENU]).subState = 2;
			}
		} else if (subState == 1) { // Updates curtain animatino
			if (curtainPos > 0)
				curtainPos--;
		} else if (subState == 5) { // Sets shelf item to original waiting item
									// after answer
			if (waitingDist <= 0) {
				SHELF_ITEMS[currentWordPos] = WAITING_ITEMS[currentWord];
				subState = 1;
				newWord();
				waitingDist = 30;
			} else
				waitingDist--;
		} else if (subState == 6) {
			if (finalDelay <= 0) {
				finalDelay = 120;
				level++;
				subState = 0;
			} else
				finalDelay--;
		} else if (subState == 7) { // Finish game, update high score
			if (finalDelay <= 0) {
				finalDelay = 120;
				if (score > Main.englishHighScore)
					Main.englishHighScore = score;
				subState = 0;
				level = 1;
				score = 0;
				GameStateManager.setGameState(GameStateManager.MAIN_MENU);
			} else
				finalDelay--;
		}
	}

	/*
	 * Renders all images to screen, including situational buttons and items
	 */
	public void render(Graphics g) {
		if (subState != 0) { // Draws background, level, and score
			g.setColor(BACKGROUND_COLOUR);
			g.fillRect(0, 0, Main.WIDTH, Main.HEIGHT);
			Render.renderCentre(SHELVES, g, Main.WIDTH / 2, 200);

			Render.render("Level", g, Main.HEADING_FONT, 80, 340, Color.BLACK);
			Render.render(String.valueOf(level), g, Main.BASE_FONT, 160, 340,
					Color.BLACK);
			Render.render("Score", g, Main.HEADING_FONT, 220, 340, Color.BLACK);
			Render.render(String.valueOf(score), g, Main.BASE_FONT, 300, 340,
					Color.BLACK);

			// Draws different expression based on answer validity
			if (subState == 2)
				Render.renderCentre(HAPPY_FACE, g, 500, 360);
			else if (subState == 3)
				Render.renderCentre(SAD_FACE, g, 500, 360);
			else
				Render.renderCentre(NEUTRAL_FACE, g, 500, 360);

			// Draws shelf items
			for (int i = 0; i < 3; i++) {
				for (int j = 0; j < numOfItems / 3; j++) {
					if (SHELF_ITEMS[i * 6 + j] != null) {
						for (int k = 0; k < 4; k++)
							Render.render(SHELF_ITEMS[i * 6 + j], g, SHELF_X
									+ ITEM_X_RELATIVE[j]
									+ SUBITEM_RELATIVE[k * 2], SHELF_Y
									+ ITEM_Y_RELATIVE[i]
									+ SUBITEM_RELATIVE[k * 2 + 1]);
					} else {
						if((i * 6 + j) % 2 == 0) Render.renderCentre(
								getMap(level)
										.get(getMap(level).keySet().toArray()[wordPositions[i
												* 6 + j]]), g, WORD_FONT,
								SHELF_X + ITEM_X_RELATIVE[j] + 41, SHELF_Y
										+ ITEM_Y_RELATIVE[i] + 25, Color.BLACK);
						else Render.renderCentre(
								getMap(level)
								.get(getMap(level).keySet().toArray()[wordPositions[i
										* 6 + j]]), g, WORD_FONT,
						SHELF_X + ITEM_X_RELATIVE[j] + 41, SHELF_Y
								+ ITEM_Y_RELATIVE[i] + 65, Color.BLACK);
					}
				}
			}
			if (subState == 1) // Displays definition
				Render.render(
						(String) getMap(level).keySet().toArray()[currentWord],
						g, DEFINITION_FONT, 15, 450, Color.BLACK);
			else if (subState == 2) {
				Render.renderCentre(CHECKMARK, g, 200, 380);
			} else if (subState == 3) {
				Render.renderCentre(WRONG_CROSS, g, 200, 380);
			} else if (subState == 4) { // Draws buttons for try again or
										// continue
				g.setColor(Color.WHITE);
				switch (selectedOption) {
				case 0:
					g.fillRect(14, 394, 162, 72);
					break;
				case 1:
					g.fillRect(174, 394, 162, 72);
				}
				g.setColor(Color.ORANGE);
				g.fillRect(20, 400, 150, 60);
				g.fillRect(180, 400, 150, 60);
				g.setColor(Color.BLACK);
				g.drawRect(20, 400, 150, 60);
				g.drawRect(180, 400, 150, 60);
				Render.renderCentre("Try Again", g, OPTION_FONT, 95, 450,
						Color.BLACK);
				Render.renderCentre("Continue", g, OPTION_FONT, 255, 450,
						Color.BLACK);
			}

			// Draws boxes and textfield
			Render.render(BOX, g, 520, 400);
			Render.render(BOX, g, 370, 400);
			g.setColor(Color.WHITE);
			g.fillRect(420, 430, 200, 40);
			g.setColor(Color.BLACK);
			g.drawRect(420, 430, 200, 40);

			if (subState <= 4) { // Draws waiting item and user answer in text
									// field
				Render.render(WAITING_ITEMS[currentWord], g, waitingX, waitingY);
				Render.renderCentre(userAnswer, g, Main.BASE_FONT, 520, 460,
						Color.BLACK);
			} else if (subState == 5) { // Draws waiting item moving onto shelf
				Render.render(WAITING_ITEMS[currentWord], g, targetX
						+ waitingDist * (waitingX - targetX) / 30, targetY
						+ waitingDist * (waitingY - targetY) / 30);
			}
		}

		if (subState == 7) { // Draws curtains at end of game
			Render.render(RED_CURTAINS, g, 0, 0);
			Render.renderCentre("Congratulations!", g, OPTION_FONT,
					Main.WIDTH / 2, Main.HEIGHT / 2 + 100, Color.YELLOW);
		} else { // Draws curtains and level transition
			if (curtainPos > 0 && curtainPos < 24)
				Render.render(RED_CURTAINS, g, 0, curtainPos * 20 - Main.HEIGHT);
			else if (curtainPos > 24) {
				Render.render(RED_CURTAINS, g, 0, 0);
				Render.renderCentre("Level " + level, g, BIG_FONT,
						Main.WIDTH / 2, Main.HEIGHT / 2 + 100, Color.YELLOW);
			}
		}
	}

	public void keyPressed(KeyEvent e) {
		if (subState == 1) {
			/*
			 * Checks for backspaces to remove characters or enter to validate
			 * the answer
			 */
			if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				if (userAnswer.length() > 0)
					userAnswer = userAnswer.substring(0,
							userAnswer.length() - 1);
			} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				if (userAnswer
						.equals(getMap(level).values().toArray()[currentWord])) {
					correctAnswer();
					subState = 2;
				} else {
					score -= level * 6;
					if (score < 0)
						score = 0;
					WRONG_ANSWER.play();
					subState = 3;
					questionDelay = true;
					rightAnswer = false;
				}
			}
		}
	}

	public void keyReleased(KeyEvent e) {

	}

	public void keyTyped(KeyEvent e) {
		if (subState == 1) {
			/*
			 * Checks if key is printable and is a number
			 */
			char c;
			try {
				c = e.getKeyChar();
				Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
				if (!Character.isISOControl(c) && c != KeyEvent.CHAR_UNDEFINED
						&& block != null
						&& block != Character.UnicodeBlock.SPECIALS
						&& userAnswer.length() <= 12 && c - '0' >= 0) {
					if (c - 'a' >= 0 && c - 'a' < 26)
						userAnswer += c;
					else if (c - 'A' >= 0 && c - 'Z' < 26)
						userAnswer += (char) ((c - 'A') + 'a');
				}
			} catch (Exception ex) {

			}
		}
	}

	/*
	 * Changes gamestate or substate based on chosen option, or checks for
	 * correct answer
	 */
	public void mouseClicked(MouseEvent e) {
		if (subState == 4) {
			if (selectedOption == 0) {
				userAnswer = "";
				subState = 1;
			} else if (selectedOption == 1) {
				userAnswer = "";
				subState = 5;
				setTarget();
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
		if (subState == 4) {
			if (y >= 400 && y <= 550) {
				if (x >= 20 && x <= 170) {
					selectedOption = 0;
					return;
				}
				if (x >= 180 && x <= 330) {
					selectedOption = 1;
					return;
				}
			}
		}
		selectedOption = -1;
	}

	/*
	 * Returns the words and definitions for the given level
	 */
	private LinkedHashMap<String, String> getMap(int num) {
		switch (num) {
		case 1:
			return LEVEL_1_MAP;
		case 2:
			return LEVEL_2_MAP;
		case 3:
			return LEVEL_3_MAP;
		case 4:
			return LEVEL_4_MAP;
		case 5:
			return LEVEL_5_MAP;
		case 6:
			return LEVEL_6_MAP;
		case 7:
			return LEVEL_7_MAP;
		case 8:
			return LEVEL_8_MAP;
		case 9:
			return LEVEL_9_MAP;
		case 10:
			return LEVEL_10_MAP;
		}
		return null;
	}

	/*
	 * Generates all words and definitions
	 */
	private void generateMap() {
		LEVEL_1_MAP.put("To do something", "act");
		LEVEL_1_MAP.put("Where oceans touch land", "coast");
		LEVEL_1_MAP.put("Someone who supports a singer", "fan");
		LEVEL_1_MAP.put("The point when you can't go farther", "limit");
		LEVEL_1_MAP.put("Taking a chance", "risk");

		LEVEL_2_MAP.put("Something true or current", "actual");
		LEVEL_2_MAP.put("Unwanted coldness", "chill");
		LEVEL_2_MAP.put("The Earth", "globe");
		LEVEL_2_MAP.put("A break from doing something", "pause");
		LEVEL_2_MAP.put("How much something is worth", "value");

		LEVEL_3_MAP.put("How someone acts towards something", "attitude");
		LEVEL_3_MAP.put("Short and quick", "brief");
		LEVEL_3_MAP.put("Mean and unwelcoming", "harsh");
		LEVEL_3_MAP.put("Going on an adventure", "journey");
		LEVEL_3_MAP.put("To look at", "observe");
		LEVEL_3_MAP
				.put("To tell someone to do something with a sign", "signal");

		LEVEL_4_MAP.put("A thing made to do something", "device");
		LEVEL_4_MAP.put("To look around a new area", "explore");
		LEVEL_4_MAP.put("To plan to do something", "intend");
		LEVEL_4_MAP.put("A way of doing something", "method");
		LEVEL_4_MAP.put("A country", "nation");
		LEVEL_4_MAP.put("A valuable reward pirates look for", "treasure");

		LEVEL_5_MAP.put("The skills to do something", "ability");
		LEVEL_5_MAP.put("100% sure", "certain");
		LEVEL_5_MAP.put("Strong and powerful", "fierce");
		LEVEL_5_MAP.put("Joy and happiness", "grace");
		LEVEL_5_MAP.put("One person", "individual");
		LEVEL_5_MAP.put("To guess what might happen", "predict");
		LEVEL_5_MAP.put("Going backwards", "reverse");

		LEVEL_6_MAP.put("The edge between two countries", "border");
		LEVEL_6_MAP.put("Someone who's afraid", "coward");
		LEVEL_6_MAP.put("Very detailed, but easily broken", "delicate");
		LEVEL_6_MAP.put("To hold on", "grasp");
		LEVEL_6_MAP.put("Someone who stays by your side", "loyal");
		LEVEL_6_MAP.put("Where something comes from", "origin");
		LEVEL_6_MAP.put("Knows what to do at the right time", "wisdom");

		LEVEL_7_MAP.put("To put in order", "arrange");
		LEVEL_7_MAP.put("To give away the truth", "confess");
		LEVEL_7_MAP.put("To look closely at something for details", "examine");
		LEVEL_7_MAP.put("Amazing and beautiful", "magnificent");
		LEVEL_7_MAP.put("An animal that eats other animals", "predator");
		LEVEL_7_MAP.put("To remember something", "recall");
		LEVEL_7_MAP.put("To control the direction of a car", "steer");
		LEVEL_7_MAP.put("Being naturally good at something", "talent");

		LEVEL_8_MAP.put("Raise someone else's child", "adopt");
		LEVEL_8_MAP.put("To take by force", "capture");
		LEVEL_8_MAP.put("Something done on purpose", "deed");
		LEVEL_8_MAP.put("Easily broken", "frail");
		LEVEL_8_MAP.put("Smart", "intelligent");
		LEVEL_8_MAP.put("Sadness and regret", "misery");
		LEVEL_8_MAP.put("Collected by bees to make honey", "nectar");
		LEVEL_8_MAP.put("The result", "outcome");

		LEVEL_9_MAP.put("To blame someone for doing something", "accuse");
		LEVEL_9_MAP.put("Smart way of doing something", "clever");
		LEVEL_9_MAP.put("Be carried by wind or water", "drift");
		LEVEL_9_MAP.put("Light and beautiful in appearance", "elegant");
		LEVEL_9_MAP.put("To rapidly flap wings", "flutter");
		LEVEL_9_MAP.put("To confuse someone", "mystify");
		LEVEL_9_MAP.put("Completely different", "opposite");
		LEVEL_9_MAP.put("Something done for fun", "pastime");
		LEVEL_9_MAP.put("To have great difficulty", "struggle");

		LEVEL_10_MAP.put("A hope to do something", "ambition");
		LEVEL_10_MAP.put("The weather and temperature of a place", "climate");
		LEVEL_10_MAP.put("To sleep lightly", "doze");
		LEVEL_10_MAP.put("Causing death", "fatal");
		LEVEL_10_MAP.put("Art painted on a wall", "mural");
		LEVEL_10_MAP.put("A painful time of trouble", "ordeal");
		LEVEL_10_MAP
				.put("A special right given to certain people", "privilege");
		LEVEL_10_MAP.put("To win or be successful", "triumph");
		LEVEL_10_MAP.put("Great sadness or loss", "woe");
	}
}
