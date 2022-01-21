/*
 * File: GameStateManager.java
 * Name: Simon Ou
 * Date: 1/11/2018
 * Description: Contains all the "states" of the game (e.g. menu, level 1, etc.), and manages the current game state
 */

package me.simon.gamestate;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public final class GameStateManager {
	public static final GameState[] GAME_STATES = new GameState[] {new MainMenuGameState(), new MathGameState(), new EnglishGameState()};

	public static final int MAIN_MENU = 0;
	public static final int MATH_GAME = 1;
	public static final int ENGLISH_GAME = 2;

	private static int currentState = 0;
	
	public static void loadStates() {
		GAME_STATES[0].init();
		GAME_STATES[1].init();
		GAME_STATES[2].init();
	}

	public static void update(double delta) {
		GAME_STATES[currentState].update(delta);
	}

	public static void render(Graphics g) {
		GAME_STATES[currentState].render(g);
	}

	public static void keyPressed(KeyEvent e) {
		GAME_STATES[currentState].keyPressed(e);
	}

	public static void keyReleased(KeyEvent e) {
		GAME_STATES[currentState].keyReleased(e);
	}

	public static void keyTyped(KeyEvent e) {
		GAME_STATES[currentState].keyTyped(e);
	}

	public static void mouseClicked(MouseEvent e) {
		GAME_STATES[currentState].mouseClicked(e);
	}

	public static void mouseEntered(MouseEvent e) {
		GAME_STATES[currentState].mouseEntered(e);
	}

	public static void mouseExited(MouseEvent e) {
		GAME_STATES[currentState].mouseExited(e);
	}

	public static void mousePressed(MouseEvent e) {
		GAME_STATES[currentState].mousePressed(e);
	}

	public static void mouseReleased(MouseEvent e) {
		GAME_STATES[currentState].mouseReleased(e);
	}

	public static void mouseDragged(MouseEvent e) {
		GAME_STATES[currentState].mouseDragged(e);
	}

	public static void mouseMoved(MouseEvent e) {
		GAME_STATES[currentState].mouseMoved(e);
	}

	public static void setGameState(int state) {
		currentState = state;
	}
}
