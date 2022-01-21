/*
 * File: Main.java
 * Name: Simon Ou
 * Date: 1/11/2018
 * Description: Main class, creates the game window, runs the game loop, updates and renders the game, and listens 
 * 		for key and mouse events.
 */

package me.simon;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;

import me.simon.gamestate.GameStateManager;
import me.simon.util.StoreItems;

public class Main {
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	public static final String TITLE = "";
	public static final Font BASE_FONT = new Font("Ubuntu", Font.PLAIN, 24);
	public static final Font HEADING_FONT = new Font("PGTextje", Font.PLAIN, 26);

	private static JFrame frame;
	private static BufferedImage buffer;

	private static boolean isRunning = false;
	private static final int TARGET_FPS = 60;
	private static final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
	
	public static String username = "";
	public static int mathHighScore = 0;
	public static int englishHighScore = 0;

	private static void init() {
		StoreItems.init();
		
		GameStateManager.loadStates();
		GameStateManager.setGameState(GameStateManager.MAIN_MENU);

		buffer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	}

	private static void run() {
		long lastLoopTime = System.nanoTime();
		long lastFPSTime = 0;

		isRunning = true;

		while (isRunning) {
			long now = System.nanoTime();
			long updateLength = now - lastLoopTime;
			lastLoopTime = now;
			double delta = updateLength / ((double) OPTIMAL_TIME);

			lastFPSTime += updateLength;

			if (lastFPSTime > 1000000000) {
				lastFPSTime = 0;
			}

			update(delta);
			render();

			try {
				Thread.sleep((lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000);
			} catch (Exception e) {

			}
		}
	}

	/*
	 * Performs game updates
	 */
	private static void update(double delta) {
		GameStateManager.update(delta);
	}

	/*
	 * Draws to screen with double buffer
	 */
	private static void render() {
		Graphics g = frame.getGraphics();
		Graphics bufferGraphics = buffer.getGraphics();

		GameStateManager.render(bufferGraphics);

		g.drawImage(buffer, 0, 0, frame);
	}

	/*
	 * Creates and displays the window, as well as listeners for mouse and keyboard events
	 */
	public static void main(String[] args) {
		init();
		frame = new JFrame(TITLE);
		frame.setSize(WIDTH, HEIGHT);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setFocusable(true);
		frame.setVisible(true);
		frame.requestFocus();

		frame.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				GameStateManager.keyPressed(e);
			}

			public void keyReleased(KeyEvent e) {
				GameStateManager.keyReleased(e);
			}

			public void keyTyped(KeyEvent e) {
				GameStateManager.keyTyped(e);
			}
		});

		frame.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				GameStateManager.mouseClicked(e);
			}

			public void mouseEntered(MouseEvent e) {
				GameStateManager.mouseEntered(e);
			}

			public void mouseExited(MouseEvent e) {
				GameStateManager.mouseExited(e);
			}

			public void mousePressed(MouseEvent e) {
				GameStateManager.mousePressed(e);
			}

			public void mouseReleased(MouseEvent e) {
				GameStateManager.mouseReleased(e);
			}
		});

		frame.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				GameStateManager.mouseDragged(e);
			}

			public void mouseMoved(MouseEvent e) {
				GameStateManager.mouseMoved(e);
			}
		});
		run();
	}
}
