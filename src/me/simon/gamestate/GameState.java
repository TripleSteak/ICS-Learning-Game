/*
 * File: GameState.java
 * Name: Simon Ou
 * Date: 1/11/2018
 * Description: game state superclass
 */

package me.simon.gamestate;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface GameState {
	public void init();

	public void update(double delta);

	public void render(Graphics g);

	public void keyPressed(KeyEvent e);

	public void keyReleased(KeyEvent e);

	public void keyTyped(KeyEvent e);

	public void mouseClicked(MouseEvent e);

	public void mouseEntered(MouseEvent e);

	public void mouseExited(MouseEvent e);

	public void mousePressed(MouseEvent e);

	public void mouseReleased(MouseEvent e);
	
	public void mouseDragged(MouseEvent e);

	public void mouseMoved(MouseEvent e);
}
