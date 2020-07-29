package com.thechief.cafe;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Random;

class Particle {
	double x, y;
	double vsp, hsp;

	public double time = 0.f;

	Random random = new Random();
	public boolean b = random.nextBoolean();

	public Particle(double x, double y) {
		this.x = x;
		this.y = y;

		hsp = 2d;
	}

	public void update() {
		double n = random.nextDouble() / 0.07d;
		if (n > 0.035)
			n = 0.035d;

		time += n;
	}
}

public class ParticleSystem {

	private ArrayList<Particle> particles = new ArrayList<Particle>();
	private Random random;

	private int maxFrames = 2, frames = maxFrames;

	private int delayStart = 120;

	public ParticleSystem() {
		random = new Random();
		random.setSeed(12345);
	}

	public void update() {
		delayStart--;
		if (delayStart > 0)
			return;

		frames--;
		if (frames == 0) {
			particles.add(new Particle(random.nextDouble() * Game.game.getWidth() * 2 - Game.game.getWidth(), 0));
			particles.get(particles.size() - 1).vsp = 2;
			frames = maxFrames;
		}

		for (Particle v : particles) {
			v.update();
			v.x += Math.sin(v.time / 1.5d) / 3.d * (v.b ? 1 : -1);

			v.vsp += 1.5d;
			if (v.vsp >= 3)
				v.vsp = 3;

			v.y += v.vsp;
			v.x += v.hsp;
		}
	}

	public void render(Graphics2D g) {
		if (delayStart > 0)
			return;

		g.setColor(new Color(180, 90, 90));

		for (Particle v : particles) {
			g.fillRect((int) v.x, (int) v.y, 4, 4);
		}
	}

}
