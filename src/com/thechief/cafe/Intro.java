package com.thechief.cafe;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Intro {

	public ArrayList<String> str = new ArrayList<String>();
	
	public int index = 0;
	public float alpha = 0;
	
	public int transitionFrames = 35;
	
	public Intro() {
		str.add("It is 1989.");
		str.add("The U.S. Government has received note of a break-in at one of their servers.");
		str.add("It contains vital confidetial documents.");
		str.add("We have traced the hacker's signal to a server owned by CAFE, a coffee company situated in New York.");
		str.add("We trust you to enter their server and destroy the stolen documents.");
		str.add("Good luck, <Name>.");
		str.add("Opening remote server connection...");
		str.add("Connected.");
	}
	
	public void update() {
		if (Game.input.keysPressed[KeyEvent.VK_ENTER]) {
			if (index < str.size() - 1) {
				index++;
				alpha = 0;
			} else {
				transitionFrames--;
 			}
		}
		if (transitionFrames < 35) transitionFrames--;
		
		if (transitionFrames == 0) {
			Game.introSequence = false;
		}
	}
	
	public void render(Graphics2D g) {
		if (transitionFrames < 20) return;
		
		alpha += 0.05;
		if (alpha > 1) alpha = 1;
		
		g.setColor(new Color(alpha, alpha, alpha));
		
		g.drawString(str.get(index).replace("<Name>", Game.name), 40, Game.game.getHeight() / 2 - 30);
	}
	
}
