package com.thechief.cafe;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.io.File;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.JFrame;

public class Game extends Canvas {

	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 1280, HEIGHT = 720;
	public static final String TITLE = "Cafe's Command";

	public static String name = "";

	public static Input input;

	public JFrame frame;

	public boolean running = true;

	public Font font;
	public Console console;
	public Intro intro;
	public ParticleSystem particleSystem;

	public static Game game;
	public static Pong pong;
	public static Furious furious;

	public static boolean isPlayingPong = false;
	public static boolean isPlayingFurious = false;

	public static boolean screenSwipe = false, isBobTransition = false;
	public int x = 0, frames = 120;
	public int haltFramesMax = 180, haltFrames = haltFramesMax;

	public static boolean introSequence = false;

	public Game() {
		Dimension d = new Dimension(WIDTH, HEIGHT);

		setPreferredSize(d);
		setMinimumSize(d);
		setMaximumSize(d);

		frame = new JFrame(TITLE);
		frame.setSize(d);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setVisible(true);
		frame.setLocationRelativeTo(null);
		frame.add(this);
		frame.pack();

		input = new Input();
		addKeyListener(input);

		requestFocus();

		font = new Font("Monospaced", Font.PLAIN, 18);

		pong = new Pong();
		furious = new Furious();
		console = new Console();
		intro = new Intro();

		particleSystem = new ParticleSystem();
	}

	public void run() {
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		final double ns = 1000000000.0 / 60.0;
		double delta = 0;
//		int fps = 0;

		while (running) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			while (delta >= 1) {
				update();
				render();
//				fps++;
				delta--;
			}

			if (System.currentTimeMillis() - timer > 1000) {
				timer += 1000;
//				System.out.println("FPS: " + fps);
//				fps = 0;
			}
		}
	}

	public void update() {
		if (screenSwipe) {
			x += getWidth() / 120;

			if (x >= getWidth()) {
				x = getWidth();
				haltFrames--;
				if (haltFrames > 0)
					return;
				
				particleSystem.update();

				if (isBobTransition) {
					furious.isBobSequence = true;
					furious.won = false;

					furious.update();
				}
			}
		} else {
			if (isPlayingPong) {
				pong.update();
			} else if (isPlayingFurious) {
				furious.update();
			} else if (introSequence) {
				intro.update();
			} else {
				console.update();
			}

		}
		input.update();
	}

	boolean startedPlaying = false;

	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(2);
			return;
		}

		Graphics2D g = (Graphics2D) bs.getDrawGraphics();

		g.setBackground(console.backgroundColor);
		g.clearRect(0, 0, getWidth(), getHeight());

		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		g.setColor(Color.white);
		g.setFont(font);

		if (introSequence) {
			intro.render(g);
		} else if (isPlayingPong) {
			pong.render(g);
		} else if (isPlayingFurious) {
			furious.render(g);
		} else {
			console.render(g);
		}

		if (screenSwipe) {
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, x, getHeight());

			if (x == getWidth() && !isBobTransition) {
				if (haltFrames > 0) {
					g.setColor(Color.WHITE);
					g.fillRect(0, 0, getWidth(), getHeight());
					return;
				}
				if (screenSwipe && x == getWidth() && !isBobTransition && !startedPlaying) {
					startedPlaying = true;
					console.playMusic("dead.mp3");
				}

				g.setColor(Color.BLACK);
				g.drawString("> Access to server \"CAFE\" aborted.", 20, getHeight() / 2 - 10);
				if (frames == 0) {
					g.drawString("A game by thechief. Thank you for playing.", 20, getHeight() / 2 + 30 - 10);
				} else {
					frames--;
				}

				particleSystem.render(g);
			} else if (frames < 120) { // This means that the player fullscreened at this point, so we just set x =
										// getWidth()
				x = getWidth();
			}

			if (isBobTransition && furious.isBobSequence) {
				furious.render(g);
			}
		}

		g.dispose();
		bs.show();
	}

	public static void main(String[] args) {
		Toolkit kit = Toolkit.getDefaultToolkit();
		Image img = kit.createImage("cafes_command.png");

		game = new Game();
		game.frame.setIconImage(img);
		game.run();
	}

	public static void playSound(String fileName) {
		File file = new File(fileName);

		try {
			final Clip clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));

			clip.addLineListener(new LineListener() {
				@Override
				public void update(LineEvent event) {
					if (event.getType() == LineEvent.Type.STOP)
						clip.close();
				}
			});

			clip.open(AudioSystem.getAudioInputStream(file));
			clip.start();
		} catch (Exception exc) {
			exc.printStackTrace(System.out);
		}
	}

}
