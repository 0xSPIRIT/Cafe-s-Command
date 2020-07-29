package com.thechief.cafe;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;

public class Pong {

	public int userScore = 0, aiScore = 0;
	public int winningScore = 6;

	public Rectangle paddlea;
	public Rectangle paddleb;

	public Rectangle ball;
	public float vx, vy;
	public float ballSpd = 6;

	public boolean lost = false;
	public boolean won = false;

	public boolean intersected = false;
	public int intersectedTimer = 18;

	public float spd = 10;

	public int startDelay = 30;

	public Pong() {
		paddlea = new Rectangle();
		paddleb = new Rectangle();
		ball = new Rectangle();

		reset();
	}

	public void update() {
		if (startDelay > 0) {
			startDelay--;
			return;
		}

		if (lost) {
			if (Game.input.keysPressed[KeyEvent.VK_ENTER]) {
				userScore = 0;
				aiScore = 0;
				reset();
				lost = false;
				startDelay = 30;
			} else if (Game.input.keysPressed[KeyEvent.VK_ESCAPE]) {
				Game.isPlayingPong = false;
			}
			return;
		}
		if (won) {
			if (Game.input.keysPressed[KeyEvent.VK_ENTER]) {
				Game.isPlayingPong = false;
			}
			return;
		}

		if (intersected) {
			intersectedTimer--;
			if (intersectedTimer == 0) {
				intersectedTimer = 18;
				intersected = false;
			}
		}

		if (Game.input.keys[KeyEvent.VK_UP] || Game.input.keys[KeyEvent.VK_W]) {
			paddlea.y -= spd;
		}
		if (Game.input.keys[KeyEvent.VK_DOWN] || Game.input.keys[KeyEvent.VK_S]) {
			paddlea.y += spd;
		}

		paddleb.y = ball.y - paddleb.height / 2 - ball.height / 2;

		if (paddlea.y > Game.HEIGHT - paddlea.height) {
			paddlea.y = Game.HEIGHT - paddlea.height;
		}
		if (paddlea.y < 0) {
			paddlea.y = 0;
		}

		if (paddleb.y > Game.HEIGHT - paddleb.height) {
			paddleb.y = Game.HEIGHT - paddleb.height;
		}
		if (paddleb.y < 0) {
			paddleb.y = 0;
		}

		ball.x += vx;
		ball.y += vy;

		if (ball.y >= Game.HEIGHT - ball.height / 2 || ball.y <= 0) {
			vy *= -1;
			Game.playSound("hit.wav");
		}
		if (ball.intersects(paddlea) && !intersected) {
			vx *= -1;
			intersected = true;
			userScore++;
			Game.playSound("hit.wav");
		}
		if (ball.intersects(paddleb) && !intersected) {
			vx *= -1;
			intersected = true;
			aiScore++;
			Game.playSound("hit.wav");
		}

		if (ball.x >= Game.WIDTH) { // Impossible condition.
			userScore++; 
			reset();
		}

		if (ball.x <= 0) {
			Game.playSound("lost.wav");
			lost = true;
			return;
		}

		if (aiScore == winningScore) {
			lost = true;
		}
		if (userScore == winningScore) {
			won = true;
			Game.playSound("win.wav");
		}
	}

	public void render(Graphics2D g) {
		if (lost) {
			String lostString = "You lost! (Enter to try again, or escape to exit)";
			int width = g.getFontMetrics().stringWidth(lostString);
			g.drawString(lostString, Game.WIDTH / 2 - width / 2, Game.HEIGHT / 2 - 30);
			return;
		}
		if (won) {
			String wonString = "You won! (password for C:\\\\music\\\\ = 'cadence')";
			int width = g.getFontMetrics().stringWidth(wonString);
			g.drawString(wonString, Game.WIDTH / 2 - width / 2, Game.HEIGHT / 2 - 30);
			return;
		}

		g.setColor(Color.WHITE);
		g.fillRect(paddlea.x, paddlea.y, paddlea.width, paddlea.height);

		g.fillRect(paddleb.x, paddleb.y, paddleb.width, paddleb.height);

		g.fillRect(ball.x, ball.y, ball.width, ball.height);

		g.drawString(userScore + "/" + winningScore + " | " + aiScore + "/" + winningScore, Game.WIDTH / 2 - 80, 20);
		
		g.drawRect(0, 0, Game.WIDTH - 1, Game.HEIGHT - 1);
	}

	public void reset() {
		paddlea.height = 50;
		paddlea.width = 10;
		paddlea.x = 30;
		paddlea.y = Game.HEIGHT / 2 - paddlea.height / 2;

		paddleb.height = 50;
		paddleb.width = 10;
		paddleb.x = Game.WIDTH - 30 - paddleb.width;
		paddleb.y = Game.HEIGHT / 2 - paddlea.height / 2;

		ball.width = 5;
		ball.height = 5;
		ball.x = Game.WIDTH / 2 - ball.width / 2;
		ball.y = Game.HEIGHT / 2 - ball.height / 2;

		vx = -ballSpd;
		vy = ballSpd;
	}

}
