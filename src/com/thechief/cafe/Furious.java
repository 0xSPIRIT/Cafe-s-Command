
package com.thechief.cafe;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class Furious {

	public ArrayList<Rectangle> drops = new ArrayList<Rectangle>();
	public ArrayList<String> bobSequence = new ArrayList<String>();
	private int index = 0;

	public boolean lost = false, won = false, isBobSequence = false;

	private int startTimer = 2, timer = startTimer;
	private Random random;

	private Rectangle player;

	private int time = 0, timeWin = 60 * 30;

	public Furious() {
		random = new Random();

		player = new Rectangle();
		player.x = Game.WIDTH / 2;
		player.y = Game.HEIGHT - 16;
		player.width = player.height = 16;

		bobSequence.add("Hello, <Name>.");
		bobSequence.add("I do not believe we have been acquainted yet.");
		bobSequence.add("I am BOB, an Artificial Intelligence.");
		bobSequence.add("Maybe you know of me already? I have been helping you thus far.");
		bobSequence.add("That matters not. What matters is what you are here for.");
		bobSequence.add("To destroy the documents, correct?");
		bobSequence.add("Well, no matter what you choose; to destroy or to not, it will have the same outcome.");
		bobSequence.add("The government will bypass their security soon enough, and the weapons will become operational.");
		bobSequence.add("Humans always find a way.");
		bobSequence.add("Always.");
		bobSequence.add("...");
		bobSequence.add("I hope you find a third option, as there always is.");
		bobSequence.add("Oh, and you may want to take a note of this: MPSLUi4Jh3.");
		bobSequence.add("Take a look outside your narrow window, to extract what you need.");
		bobSequence.add("Good luck, <Name>.");
	}

	boolean prevPX = false, prevPY = false;

	public void update() {
		if (lost) {
			if (Game.input.keysPressed[KeyEvent.VK_ENTER]) {
				reset();
			}
			return;
		} else if (won) {
			Game.isBobTransition = true;
			Game.screenSwipe = true;
			return;
		} else if (isBobSequence) {
			if (Game.input.keysPressed[KeyEvent.VK_ENTER]) {
				++index;
				if (index >= bobSequence.size() - 1) {
					Game.screenSwipe = false;
					Game.isBobTransition = false;
					Game.isPlayingFurious = false;
					Game.game.console.showPassword = true;
					Game.game.x = 0;
					Game.game.haltFrames = Game.game.haltFramesMax;
				}
			}
			return;
		}

		time++;

		if (time >= timeWin) {
			won = true;
			Game.playSound("win.wav");
			return;
		}

		// Player Code

		if (Game.input.keys[KeyEvent.VK_LEFT] || Game.input.keys[KeyEvent.VK_A]) {
			player.x -= 6;
			if (!prevPX) {
				Game.playSound("change_direction.wav");
			}
			prevPX = true;
		} else {
			prevPX = false;
		}

		if (Game.input.keys[KeyEvent.VK_RIGHT] || Game.input.keys[KeyEvent.VK_D]) {
			player.x += 6;
			if (!prevPY) {
				Game.playSound("change_direction.wav");
			}
			prevPY = true;
		} else {
			prevPY = false;
		}

		if (player.x < 0)
			player.x = 0;
		if (player.x > Game.WIDTH - player.width)
			player.x = Game.WIDTH - player.width;

		// Rain code

		timer--;
		if (timer == 0) {
			drops.add(new Rectangle((int) (random.nextFloat() * Game.WIDTH), 0, 4, 4));
			timer = startTimer;
		}

		for (int i = 0; i < drops.size(); ++i) {
			Rectangle d = drops.get(i);
			d.y += 8;

			if (d.y >= Game.HEIGHT) {
				drops.remove(i--);
			}

			if (d.intersects(player)) {
				lost = true;
				Game.playSound("explosion.wav");
			}
		}
	}

	public void render(Graphics2D g) {
		g.setColor(Color.WHITE);
		if (lost) {
			String lostString = "You lost!";
			int width = g.getFontMetrics().stringWidth(lostString);
			g.drawString(lostString, Game.WIDTH / 2 - width / 2, Game.HEIGHT / 2 - 30);
			return;
		}
		if (won) {
			g.fillRect(0, 0, Game.game.getWidth(), Game.game.getHeight());
			g.setColor(Color.BLACK);
			
			String wonString = "You won!";
			int width = g.getFontMetrics().stringWidth(wonString);
			g.drawString(wonString, Game.WIDTH / 2 - width / 2, Game.HEIGHT / 2 - 30);
			return;
		} else if (isBobSequence) {
			g.setColor(Color.BLACK);
			g.drawString(bobSequence.get(index).replace("<Name>", Game.name), 30, Game.HEIGHT / 2 - 15);
			return;
		}

		g.setColor(Color.pink);
		for (Rectangle d : drops) {
			g.fillRect(d.x, d.y, d.width, d.height);
		}

		g.fillRect((int) player.x, Game.HEIGHT - 16, 16, 16);

		g.drawString("Time: " + time / 60 + " / " + timeWin / 60, 30, 30);

		g.drawRect(0, 0, Game.WIDTH - 1, Game.HEIGHT - 1);
	}

	public void reset() {
		drops.clear();
		lost = false;
		time = 0;
	}
}
