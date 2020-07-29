package com.thechief.cafe;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

import jaco.mp3.player.MP3Player;

public class Console {

	public static final Color DARK_COLOR = new Color(15, 23, 15);

	public String directory = "C:\\";

	public ArrayList<String> cafeLines;
	public String lines[];
	public int starting[];

	public int currentLine = 0;
	public int caret = 1;
	public int fullCaretTime = 60, caretTime = fullCaretTime;

	public boolean awaitingExitConfirm = false;
	public boolean accessLocked = true;
	public boolean enterName = true; // Probably should have used a state system instead of this, but whatever.
	public boolean awaitingPassword = false;
	public boolean cafeSequence = false;
	public boolean showPassword = false;

	public boolean[] alreadyPressed = new boolean[10];

	public String passwordFolder = ""; // Folder for awaitingPassword
	public int cafeSequenceCurrentLine = 0;

	public int charWidth;

	public Color textColor = Color.WHITE;
	public Color caretColor = Color.WHITE;
	public Color backgroundColor = DARK_COLOR;

	public MP3Player player;

	public BufferedImage image; // The current image that you are looking at. null for no image.

	public Console() {
		lines = new String[166];
		starting = new int[166];
		cafeLines = new ArrayList<String>();

		currentLine = 10;

		lines[currentLine] = "Enter your first name.";
		lines[++currentLine] = "> ";
		starting[currentLine] = lines[currentLine].length();

		cafeLines.add("Hello, <Name>.");
		cafeLines.add("You've done excellently.");
		cafeLines.add("Have you made your choice?");
		cafeLines.add("...");
		cafeLines.add("Well, no matter what you choose, it will have the same outcome.");
		cafeLines.add("If you decide to destroy the documents, the super weapons will continue to be operational.");
		cafeLines.add("If you decide to leave the documents, it will only be a matter of time until they get past the security measures.");
		cafeLines.add("...");
		cafeLines.add("Ah...");
		cafeLines.add("I believe you have found your third option.");
		cafeLines.add("Hah.");
		cafeLines.add("Hahahah.");
		cafeLines.add("You're empty inside, huh?");
		cafeLines.add("> Launch codes inserted. Running...");
		cafeLines.add("Hahahahahahaha.");
		cafeLines.add("You hate humanity as much as I do.");
		cafeLines.add("...");
		cafeLines.add("Well.");
		cafeLines.add("Let's watch this world burn together!");
	}

	public void update() {
		if (cafeSequence) {
			if (Game.input.keysPressed[KeyEvent.VK_ENTER]) {
				cafeSequenceCurrentLine++;
				currentLine = cafeSequenceCurrentLine;

				System.out.println(currentLine + ", " + cafeLines.size());

				if (currentLine == cafeLines.size()) {
					Game.screenSwipe = true;
					playMusic("system_error.mp3");
					System.out.println(player.isStopped());
					return;
				}
			}

			lines[cafeSequenceCurrentLine] = cafeLines.get(cafeSequenceCurrentLine).replace("<Name>", Game.name);
			return;
		}

		if (!enterName) {
			if (image != null) {
				if (Game.input.keysPressed[KeyEvent.VK_ENTER]) {
					image = null;
				}
				return;
			}

			if (player != null && player.isStopped()) {
				stopMusic();
			}
		} else {
			if (lines[currentLine].length() > 1) {
				String s = new String(lines[currentLine]).substring(0, 2);
				lines[currentLine] = lines[currentLine].substring(2).replace(s, "");
				lines[currentLine] = "> " + lines[currentLine].replace(" ", "");
			}
		}

		if (Game.input.newTyped != '\0') {
			caretTime = fullCaretTime / 2 - 1;

			if (Game.input.keys[KeyEvent.VK_CONTROL] && Game.input.keysPressed[KeyEvent.VK_BACK_SPACE]) {

			} else if (Game.input.keys[KeyEvent.VK_CONTROL] && Game.input.keysPressed[KeyEvent.VK_C]) {

			} else if (Game.input.keys[KeyEvent.VK_CONTROL] && Game.input.keysPressed[KeyEvent.VK_V]) {

			} else if (Game.input.keysPressed[KeyEvent.VK_ESCAPE]) {
				if (enterName) {
					lines[currentLine] = "> ";
				} else {
					lines[currentLine] = directory + "> ";
				}
			} else if (Game.input.keysPressed[KeyEvent.VK_ENTER] && !enterName && !awaitingPassword) {
				parse(lines[currentLine].substring(starting[currentLine], lines[currentLine].length()));

				if (!awaitingExitConfirm && !awaitingPassword) {
					lines[++currentLine] = directory + "> ";
				} else {
					lines[++currentLine] = "> ";
				}
				starting[currentLine] = lines[currentLine].length();
			} else if (Game.input.keysPressed[KeyEvent.VK_ENTER] && enterName && !awaitingPassword) {
				String name = lines[currentLine].replace("> ", "");
				if (name.equals(""))
					return;

				for (int i = 0; i < name.length(); ++i) {
					if (i == 0) {
						Game.name += Character.toUpperCase(name.charAt(i));
					} else {
						Game.name += Character.toLowerCase(name.charAt(i));
					}
				}
				enterName = false;
				currentLine = -1;
				for (int i = 0; i < lines.length; ++i) {
					lines[i] = "";
				}

				lines[++currentLine] = "CAFE OS [Version 2.1.1368.959]";
				lines[++currentLine] = "(c) 1989 CAFE Corporation.";
				lines[++currentLine] = directory + "> ";
				starting[currentLine] = lines[currentLine].length();

				Game.introSequence = true;
			} else if (Game.input.keysPressed[KeyEvent.VK_ENTER] && awaitingPassword) {
				awaitingPassword = false;
				String passwordEntered = lines[currentLine].replace("> ", "");

				if (passwordFolder.equals("C:\\CAFE\\cafe_documents")) {
					if (passwordEntered.equals("1138")) {
						lines[++currentLine] = "Access granted.";
						directory = passwordFolder + "\\";
						alreadyPressed[0] = true;
					} else {
						lines[++currentLine] = "Wrong password.";
					}
				} else if (passwordFolder.equals("C:\\CAFE\\sec_document")) {
					if (passwordEntered.toLowerCase().equals("coffee")) {
						lines[++currentLine] = "Access granted.";
						directory = passwordFolder + "\\";
						alreadyPressed[1] = true;
					} else {
						lines[++currentLine] = "Wrong password.";
					}
				} else if (passwordFolder.equals("C:\\music")) {
					if (passwordEntered.toLowerCase().equals("cadence")) {
						lines[++currentLine] = "Access granted.";
						directory = passwordFolder + "\\";
						alreadyPressed[2] = true;
					} else {
						lines[++currentLine] = "Wrong password.";
					}
				} else if (passwordFolder.equals("C:\\games")) {
					if (passwordEntered.toLowerCase().equals("conversion")) {
						lines[++currentLine] = "Access granted.";
						directory = passwordFolder + "\\";
						alreadyPressed[3] = true;
					} else {
						lines[++currentLine] = "Wrong password.";
					}
				} else if (passwordFolder.equals("C:\\games\\furious_game")) {
					if (passwordEntered.toLowerCase().equals("bob")) {
						lines[++currentLine] = "Access granted.";
						directory = passwordFolder + "\\";
						alreadyPressed[4] = true;
					} else {
						lines[++currentLine] = "Wrong password.";
					}
				}

				lines[++currentLine] = directory + "> ";
				starting[currentLine] = lines[currentLine].length();
			} else {
				switch (Game.input.newTyped) {
				case '\b': {
					if (lines[currentLine].length() > starting[currentLine]) {
						lines[currentLine] = lines[currentLine].substring(0, lines[currentLine].length() - 1);
					}
					break;
				}
				default:
					lines[currentLine] += Game.input.newTyped;
				}
			}
		}

		if (awaitingExitConfirm || enterName)
			return;

		if (currentLine > (int) Math.floor(Game.game.getHeight() / 30) - 1) {
			currentLine = -1;
			for (int i = 0; i < lines.length; ++i) {
				lines[i] = "";
			}

			lines[++currentLine] = directory + "> ";
			starting[currentLine] = lines[currentLine].length();
		}
	}

	public void render(Graphics2D g) {
		if (image != null) {
			g.drawImage(image, null, 100, 100);
			return;
		}

		g.setColor(textColor);

		charWidth = g.getFontMetrics().stringWidth(" ");

		for (int i = 0; i < lines.length; ++i) {
			String line = lines[i];
			if (line == null)
				continue;

			g.drawString(line, 20, 30 + i * 30);
		}

		int width = g.getFontMetrics().stringWidth(lines[currentLine]);

		caretTime--;
		if (caretTime == 0) {
			caretTime = fullCaretTime;
		}

		g.setColor(caretColor);
		if (caretTime < fullCaretTime / 2) {
			g.fillRect(width + caret * charWidth + charWidth, 15 + currentLine * 30, 10, 20);
		}

		if (showPassword) {
			g.setColor(Color.white);
			g.drawString("MPSLUi4Jh3", Game.game.getWidth() - 125, Game.game.getHeight() - 15);
		}
	}

	public void parse(String command) {
		if (awaitingExitConfirm) {
			if (command.startsWith("y")) {
				System.exit(0);
			} else {
				awaitingExitConfirm = false;
			}
			return;
		}

		String[] parts = command.split(" ");
		// Removing \n, in case it slipped by.
		for (int i = 0; i < parts.length; ++i) {
			parts[i] = parts[i].replace("\n", "");
		}

		if (parts.length == 1) {
			if (parts[0].equals("clear") || parts[0].equals("cls")) {
				currentLine = -1;
				for (int i = 0; i < lines.length; ++i) {
					lines[i] = "";
				}
				return;
			} else if (parts[0].equals("ls") || parts[0].equals("list") || parts[0].equals("dir")) {
				File path = new File(directory.replace(":", ""));
				File[] files = path.listFiles();

				Arrays.sort(files);

				for (File f : files) {
					lines[++currentLine] = "- " + f.getName() + (f.isDirectory() ? " (Folder)" : " (File)");
				}
				return;
			} else if (parts[0].equals("help")) {
				help();
				if (currentLine > (int) Math.floor(Game.game.getHeight() / 30) - 1) {
					currentLine = -1;
					for (int i = 0; i < lines.length; ++i) {
						lines[i] = "";
					}
					help(false);
				}
				return;
			} else if (parts[0].equals("stop")) {
				stopMusic();
				return;
			} else if (parts[0].equals("exit")) {
				lines[++currentLine] = "Are you sure you want to exit? There is no save feature. (y/n): ";
				awaitingExitConfirm = true;
				return;
			}
		} else if (parts.length == 2) {
			File path = new File(directory.replace(":", ""));
			File[] files = path.listFiles();

			Arrays.sort(files);

			if (parts[0].equals("cd")) {
				if (parts[1].equals("..")) {
					if (!directory.equals("C:\\")) {
						int c = directory.length() - 2; // -2 to skip over the first \\
						while (directory.charAt(c) != '\\') {
							c--;
						}
						directory = directory.substring(0, c + 1);
						return;
					} else {
						lines[++currentLine] = "Cannot go back a folder further.";
						return;
					}
				}

				boolean exists = false;
				for (File f : files) {
					if (f.getName().equals(parts[1]) && f.isDirectory()) {
						exists = true;
						break;
					}
				}

				if (!exists) {
					lines[++currentLine] = "Folder \"" + parts[1] + "\" does not exist.";
					return;
				} else {
					if (parts[1].equals("access_key") && directory.equals("C:\\CAFE\\") && accessLocked) {
						lines[++currentLine] = "Folder Locked. Access not granted. (Use folder_cracker to open the folder.)";
					} else {
						// Hardcoded folders that need a password. Also, no switch statements because
						// we're using .equals() not ==
						if (parts[1].equals("cafe_documents") && directory.equals("C:\\CAFE\\") && !alreadyPressed[0]) {
							awaitPassword(parts[1]);
							return;
						} else if (parts[1].equals("sec_document") && directory.equals("C:\\CAFE\\") && !alreadyPressed[1]) {
							awaitPassword(parts[1]);
							return;
						} else if (parts[1].equals("music") && directory.equals("C:\\") && !alreadyPressed[2]) {
							awaitPassword(parts[1]);
							return;
						} else if (parts[1].equals("games") && directory.equals("C:\\") && !alreadyPressed[3]) {
							awaitPassword(parts[1]);
							return;
						} else if (parts[1].equals("furious_game") && directory.equals("C:\\games\\") && !alreadyPressed[4]) {
							awaitPassword(parts[1]);
							return;
						}

						directory += parts[1] + "\\";
					}
					return;
				}
			} else if (parts[0].equals("open")) {
				boolean exists = false;

				for (File f : files) {
					System.out.println(f.getName().equals(parts[1]));
					if (f.getName().equals(parts[1])) {
						exists = true;
						break;
					}
				}

				if (!exists) {
					lines[++currentLine] = "File \"" + parts[1] + "\" does not exist.";
				} else {
					if (parts[1].endsWith(".txt")) {
						currentLine++;

						try {
							BufferedReader br = new BufferedReader(new FileReader(directory.replace(":", "") + parts[1]));

							ArrayList<String> lns = new ArrayList<String>();

							String line;
							while ((line = br.readLine()) != null) {
								System.out.println(line);
								lns.add(line);
							}

							if (currentLine + lns.size() > (int) Math.floor(Game.game.getHeight() / 30) - 1) {
								currentLine = -1;
								for (int i = 0; i < lines.length; ++i) {
									lines[i] = "";
								}
							}

							for (int i = 0; i < lns.size(); ++i) {
								lines[++currentLine] = lns.get(i).replace("<Name>", Game.name);
							}
							lines[++currentLine] = "";

							br.close();
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					} else if (parts[1].endsWith(".jpg")) {
						File f = new File(directory.replace(":", "") + parts[1]);
						try {
							image = ImageIO.read(f);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else if (parts[1].equals("project_cafe")) {
						cafeSequence = true;

						currentLine = -1;
						for (int i = 0; i < lines.length; ++i) {
							lines[i] = "";
						}
						return;
					}
				}
				return;
			} else if (parts[0].equals("run")) {
				boolean exists = false;

				for (File f : files) {
					if (f.getName().equals(parts[1])) {
						exists = true;
						break;
					}
				}

				if (!exists) {
					lines[++currentLine] = "File \"" + parts[1] + "\" does not exist.";
				} else {
					if (parts[1].equals("pong_game.o") && directory.equals("C:\\games\\")) {
						Game.isPlayingPong = true;
						return;
					} else if (parts[1].equals("furious_game.o") && directory.equals("C:\\games\\furious_game\\")) {
						Game.isPlayingFurious = true;
						return;
					}
				}
			} else if (parts[0].equals("bgcol")) {
				Color pb = backgroundColor;

				if (getColorFromName(parts[1]) != null) {
					backgroundColor = getColorFromName(parts[1]);
				}

				if (backgroundColor == textColor) {
					backgroundColor = pb;
					lines[++currentLine] = "Do not set your background colour to the same thing as your text colour.";
				}

				return;
			} else if (parts[0].equals("txtcol")) {
				Color pb = textColor;

				if (getColorFromName(parts[1]) != null) {
					textColor = getColorFromName(parts[1]);
				}

				if (backgroundColor == textColor) {
					textColor = pb;
					lines[++currentLine] = "Do not set your text colour to the same thing as your background colour.";
				}

				return;
			} else if (parts[0].equals("crtcol")) {
				Color pb = caretColor;

				if (getColorFromName(parts[1]) != null) {
					caretColor = getColorFromName(parts[1]);
				}

				if (backgroundColor == caretColor) {
					caretColor = pb;
					lines[++currentLine] = "Do not set your caret colour to the same thing as your background colour.";
				}

				return;
			} else if (parts[0].equals("play")) {
				if (player != null) {
					lines[++currentLine] = "Unable to play right now. Type 'stop' to stop currently playing music.";
					return;
				}
				if (player != null && !player.isStopped()) {
					lines[++currentLine] = "Unable to play right now. Type 'stop' to stop currently playing music.";
					return;
				}

				boolean exists = false;

				for (File f : files) {
					if (f.getName().equals(parts[1])) {
						exists = true;
						break;
					}
				}

				if (!exists) {
					lines[++currentLine] = "File \"" + parts[1] + "\" does not exist.";
				} else {
					playMusic(directory.replace(":", "") + parts[1]);
				}
				return;
			} else if (parts[0].equals("folder_cracker")) {
				if (parts[1].equals("access_key") && directory.equals("C:\\CAFE\\") && accessLocked) {
					accessLocked = false;
					lines[++currentLine] = "Folder \"" + parts[1] + "\" unlocked.";
					return;
				} else {
					lines[++currentLine] = "Folder already opened / folder does not exist.";
					return;
				}
			}
		}

		lines[++currentLine] = "Unknown command. Type 'help' to get a list of commands.";
	}

	public void listColors() {
		lines[++currentLine] = "";
		lines[++currentLine] = "The following is a list of all available colours:";
		lines[++currentLine] = "white";
		lines[++currentLine] = "black";
		lines[++currentLine] = "dark";
		lines[++currentLine] = "red";
		lines[++currentLine] = "yellow";
		lines[++currentLine] = "magenta";
		lines[++currentLine] = "green";
		lines[++currentLine] = "blue";
		lines[++currentLine] = "";
	}

	private void stopMusic() {
		if (player != null) {
			if (!player.isStopped()) {
				player.stop();
				player = null;
			}
			player = null;
		} else {
			lines[++currentLine] = "No music currently playing.";
		}
	}

	private Color getColorFromName(String nameColor) {
		switch (nameColor) {
		case "white":
			return Color.WHITE;
		case "black":
			return Color.BLACK;
		case "dark":
			return DARK_COLOR;
		case "red":
			return Color.RED;
		case "yellow":
			return Color.YELLOW;
		case "magenta":
			return Color.MAGENTA;
		case "green":
			return Color.GREEN;
		case "blue":
			return Color.BLUE;
		default: {
			lines[++currentLine] = "Unknown color.";
			listColors();
		}
		}

		return null;
	}

	private void awaitPassword(String folderName) {
		awaitingPassword = true;
		passwordFolder = directory + folderName;

		lines[++currentLine] = "Enter password: ";
		starting[currentLine] = lines[currentLine].length();
	}

	private void help(boolean isLine) {
		if (isLine) {
			lines[++currentLine] = "";
		}
		lines[++currentLine] = "Commands: ";
		lines[++currentLine] = "help                    Shows this text.";
		lines[++currentLine] = "cd folder               Goes into a specified folder. \"cd ..\" to go back a folder.";
		lines[++currentLine] = "list                    Lists all files and folders in the current folder.";
		lines[++currentLine] = "clear                   Clears the screen.";
		lines[++currentLine] = "folder_cracker folder   An program injected by you. Unlocks the specified folder.";
		lines[++currentLine] = "open file.txt           Opens the specified text file.";
		lines[++currentLine] = "open file.jpg           Opens the specified image file.";
		lines[++currentLine] = "run game.o              Runs the specified game.";
		lines[++currentLine] = "bgcol colour            Changes the background colour to the specified colour.";
		lines[++currentLine] = "txtcol colour           Changes the text colour to the specified colour.";
		lines[++currentLine] = "crtcol colour           Changes the text colour of the caret to the specified colour.";
		lines[++currentLine] = "play file.mp3           Plays the specified music file.";
		lines[++currentLine] = "stop                    Stops any currently playing music.";
		lines[++currentLine] = "exit                    Exits the terminal.";
		lines[++currentLine] = "";
	}
	
	private void help() {
		help(true);
	}

	public void playMusic(String fp) {
		player = new MP3Player(new File(fp));
		player.play();
	}
}
