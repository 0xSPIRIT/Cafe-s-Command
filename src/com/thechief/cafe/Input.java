package com.thechief.cafe;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Input implements KeyListener {

	public char newTyped = '\0';

	public boolean keysPressed[] = new boolean[255];
	public boolean keys[] = new boolean[255];

	public void update() {
		newTyped = '\0';
		for (int i = 0; i < keysPressed.length; ++i) {
			keysPressed[i] = false;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() < 255) {
			keysPressed[e.getKeyCode()] = true;
			keys[e.getKeyCode()] = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() < 255) {
			keys[e.getKeyCode()] = false;
			keysPressed[e.getKeyCode()] = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		newTyped = e.getKeyChar();
	}

}
