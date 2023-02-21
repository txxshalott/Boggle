package FinalProject;

/*
 * Date Of Completion: June 20th, 2022
 * ICS4U Final Project - submitted ver
 * This program is written by: Andi Jingzhi Xie, Chantal Zhang, Oliver Zeng, Shalott Tam
 * This program requires bCLick.wav, dreams.wav, dictionary.txt to run
*/

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.sound.sampled.*;
import javax.swing.*;
import javax.swing.Timer;

public class Boggle extends JFrame implements ActionListener {
	// game variables
	private static String[][] dices = { { "AAAFRS", "AEEGMU", "CEIILT", "DHHNOT", "FIPRSY" },
			{ "AAEEEE", "AEGMNN", "CEILPT", "DHLNOR", "GORRVW" }, { "AAFIRS", "AFIRSY", "CEIPST", "EIIITT", "HIPRRY" },
			{ "ADENNN", "BJKQXZ", "DDLNOR", "EMOTTT", "NOOTUW" },
			{ "AEEEEM", "CCNSTW", "DHHLOR", "ENSSSU", "OOOTTU" } };
	// the dices given in the assignment sheet
	private int JButtonCoordinate[] = new int[2]; // Coordinate array of the pressed button
	private static ArrayList<String> wordsFoundInGrid = new ArrayList<String>();
	// all words found in grid, delete once pressed
	private static int minWordLength = 3; // defaulted
	private static String[] dictionary; // dictionary
	private static int[] AIButtonXCoordinate;
	// stores the coordinates of each character of the word found by AI on the grid
	private static int[] AIButtonYCoordinate;
	private int tournamentScore = 15; // defaulted
	private static String[][] dicesFacingUp = new String[5][5]; // letters visible to user
	private static String[][] dicesFacingUpCopy = new String[dicesFacingUp.length][dicesFacingUp[0].length];
	// for recursion method
	private int numButtonPressed = 0; // index of clickedButton
	private static int timeLimit = 15; // defaulted

	// checkers
	private boolean Player1Turn = true;
	// check which player's turn, false if it's Player 2's turn
	private boolean gamePaused = false; // boolean for the pause/start button
	private static int[][] clickedButton = new int[dicesFacingUp.length * dicesFacingUp[0].length][2];
	// similar to AIButtonXCoordinate, but for user
	private int sec = timeLimit; // decremented during countdown
	private static int totalPasses = 0; // total time the player has passed
	private int numTimesAITimerTriggerred = 0;
	private boolean AIWordShowedToUser = false;
	// creates a pause after AI word is shown on the grid
	public String wordGuess = ""; // current word on GUI
	// we want this variable to be seen outside of the class

	// user settings & scorekeeping
	private static String difficulty = "impossible";
	private boolean isSingle = false;
	private int Player1Score = 0;
	private int Player2Score = 0;

	// utilities
	private javax.swing.Timer timer = new Timer(1000, this); // triggers an event every second
	private javax.swing.Timer AITimer = new Timer(250, this);
	// update a character of AIWord every quarter of a second

	// ###################### GUI Components ###########################
	// custom colours
	private static Color darkTeal = new Color(8, 60, 95);
	private static Color white = new Color(255, 253, 250);
	private static Color lightBlue = new Color(157, 195, 239);

	// Menu variables
	JPanel welcomePanel;
	JLabel welcome;
	JPanel playerPanel;
	JButton singlePlayer;
	JButton multiPlayer;
	JLabel goalLabel;
	JTextField GoalPrompt;

	// instructions variables
	JButton rulesButton;
	JPanel rulePanel;
	JPanel messagePanel;
	JTextArea label;
	static String instructions;

	// board variables
	JPanel option;
	JPanel GoalTime;
	JPanel ScorePanel;
	JPanel grid;

	// user options in game
	JButton pause;
	JButton menu;
	JButton pass;
	JButton restart;
	JButton shuffle;

	JLabel Goal;
	JLabel Time;

	JLabel statusLabel;

	static JTextField Player1;
	JLabel Score;
	static JTextField Player2;

	static JLabel currentWord;
	static JButton[][] cells = new JButton[5][5];
	JButton check;
	JLabel winner;

	// settings:
	JPanel setting;
	JButton settingButton;
	static JProgressBar progressBar;
	JComboBox<String> difChoice; // drop down list
	JTextField timeF;
	JTextField minW;
	JComboBox<String> playerTurn;

	private void showMenu() { // construct menu
		getContentPane().removeAll();
		getContentPane().repaint();

		// creating large welcome message at top of GUI
		JLabel spaceLabel = new JLabel();
		JLabel boggleTitle = new JLabel("BOGGLE", JLabel.CENTER);
		boggleTitle.setForeground(darkTeal);
		spaceLabel.setPreferredSize(new Dimension(600, 90));

		boggleTitle.setPreferredSize(new Dimension(600, 150));
		boggleTitle.setFont(new Font("Helvetica", Font.BOLD, 130));

		welcomePanel = new JPanel();

		welcome = new JLabel("Welcome to Boggle!", JLabel.CENTER);
		welcome.setPreferredSize(new Dimension(600, 30));
		welcome.setFont(new Font("Helvetica", Font.BOLD, 15));
		welcome.setHorizontalAlignment(JLabel.CENTER);
		welcome.setVerticalAlignment(JLabel.CENTER);

		welcomePanel.add(spaceLabel);
		welcomePanel.add(boggleTitle);
		add(welcomePanel);

		Font baseFont = new Font("Helvetica", Font.PLAIN, 15); // used for everything

		/*
		 * instructions
		 */
		instructions = "The goal is to find words by connecting a series of letters on a five-by-five grid. "
				+ "The word must be a valid word as defined by the English dictionary; "
				+ "You can connect letters horizontally, vertically, or diagonally."
				+ "\nYou can play with a friend or against the computer! "
				+ "\n\nScoring for each word is provided by word length. " + "\n length \t points"
				+ "\n3 \t 1 \n4 \t 1 \n5 \t 2 \n6 \t 3 \n7 \t 5 \n8 \t 11" + "\n \nHappy word finding!";

		JLabel newSpaceLabel = new JLabel();
		newSpaceLabel.setPreferredSize(new Dimension(800, 50)); // pushing down the instructions button on the interface
		rulesButton = new JButton("RULES");
		rulesButton.addActionListener(this);
		rulesButton.setBackground(Color.WHITE);
		rulesButton.setPreferredSize(new Dimension(100, 50));
		rulesButton.setFont(baseFont);

		// centering the instructions button
		rulePanel = new JPanel();
		rulePanel.add(newSpaceLabel);
		rulePanel.add(rulesButton);
		add(rulePanel);

		// creating instructions JOptionPane
		messagePanel = new JPanel();
		messagePanel.setPreferredSize(new Dimension(400, 300));
		label = new JTextArea(instructions);
		label.setLineWrap(true);
		label.setWrapStyleWord(true);
		label.setFont(new Font("Century", Font.PLAIN, 15));
		label.setEditable(false);

		// panel in which instructions will appear on
		messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
		messagePanel.add(label);

		// creating the non-instructions parts of menu
		GridLayout gridLayout = new GridLayout(1, 2);
		gridLayout.setHgap(10);
		playerPanel = new JPanel(gridLayout);

		singlePlayer = new JButton("Start Single Player");
		singlePlayer.addActionListener(this);
		singlePlayer.setBackground(Color.WHITE);
		singlePlayer.setBorder(BorderFactory.createLineBorder(darkTeal, 3));
		singlePlayer.setFont(new Font("Helvetica", Font.PLAIN, 30));

		multiPlayer = new JButton("Start Multi Player");
		multiPlayer.addActionListener(this);
		multiPlayer.setBackground(Color.WHITE);
		multiPlayer.setBorder(BorderFactory.createLineBorder(darkTeal, 3));
		multiPlayer.setFont(new Font("Helvetica", Font.PLAIN, 30));

		playerPanel.add(singlePlayer);
		playerPanel.add(multiPlayer);

		add(playerPanel);

		// creating goal point section of GUI
		restoreArray(clickedButton);

		// the settings menu
		GoalPrompt = new JTextField("15");
		GoalPrompt.setPreferredSize(new Dimension(100, 20));
		int textFieldSize = 10;

		setting = new JPanel();
		goalLabel = new JLabel("Enter the goal point: ");
		goalLabel.setFont(baseFont);
		goalLabel.setPreferredSize(new Dimension(200, 20));

		JPanel settingButtonPanel = new JPanel();
		settingButton = new JButton("GAME SETTINGS");
		settingButton.setPreferredSize(new Dimension(200, 50));
		settingButtonPanel.add(settingButton);
		add(settingButtonPanel);

		GridLayout secondGridLayout = new GridLayout(5, 2);
		secondGridLayout.setHgap(5);
		setting.setLayout(secondGridLayout);
		settingButton.addActionListener(this);
		settingButton.setBackground(Color.WHITE);
		settingButton.setFont(baseFont);
		setting.add(goalLabel);
		setting.add(GoalPrompt);

		String[] difList = { "impossible", "hard", "medium", "easy" };
		difChoice = new JComboBox<>(difList);
		difChoice.setBounds(80, 50, 140, 20);
		difChoice.setFont(baseFont);

		JLabel setDifficulty = new JLabel("Please select a difficulty");
		setDifficulty.setFont(baseFont);
		setting.add(setDifficulty);
		setting.add(difChoice);

		JLabel enterTime = new JLabel("Please input your time limit");
		enterTime.setFont(baseFont);
		timeF = new JTextField("15", textFieldSize);
		setting.add(enterTime);
		setting.add(timeF);

		JLabel enterWordLength = new JLabel("Please input the minimum word length");
		enterWordLength.setFont(baseFont);
		minW = new JTextField("3", textFieldSize);
		setting.add(enterWordLength);
		setting.add(minW);

		JLabel pTurn = new JLabel("Please enter player turn");
		String[] pList = { "Player 1", "Player 2", "Random" };
		playerTurn = new JComboBox<>(pList);
		playerTurn.setBounds(80, 50, 140, 20);
		playerTurn.setFont(baseFont);
		pTurn.setFont(baseFont);
		setting.add(pTurn);
		setting.add(playerTurn);

		setVisible(true);
	}

	private void constructGame() { // construct boggle game
		// clear the board
		getContentPane().removeAll();
		getContentPane().repaint();

		// pause, exit, restart buttons
		JPanel option = new JPanel();
		option.setLayout(new FlowLayout());
		Font buttonFont = new Font("Dialog", Font.BOLD, 13);

		pause = new JButton("Pause");
		pause.addActionListener(this);
		pause.setBackground(white);
		pause.setFont(buttonFont);

		menu = new JButton("< Menu");
		menu.addActionListener(this);
		menu.setBackground(white);
		menu.setFont(buttonFont);

		pass = new JButton("Pass");
		pass.addActionListener(this);
		pass.setBackground(white);
		pass.setFont(buttonFont);

		shuffle = new JButton("Shuffle");
		shuffle.addActionListener(this);
		shuffle.setEnabled(false);
		shuffle.setBackground(white);
		shuffle.setFont(buttonFont);

		restart = new JButton("Restart");
		restart.addActionListener(this);
		restart.setBackground(white);
		restart.setFont(buttonFont);

		option.add(menu);
		option.add(pause);
		option.add(pass);
		option.add(shuffle);
		option.add(restart);
		add(option);

		// Goal and Time panel
		GoalTime = new JPanel();
		GoalTime.setLayout(new FlowLayout());
		Goal = new JLabel("Goal: " + tournamentScore);
		GoalTime.add(Goal);
		add(GoalTime);

		// Status label
		JPanel statusPanel = new JPanel();
		statusLabel = new JLabel(" ");
		statusPanel.add(statusLabel);

		// Player/Score Panel
		ScorePanel = new JPanel();
		ScorePanel.setLayout(new FlowLayout());
		Player1 = new JTextField("Player 1", 7);
		Player1.setHorizontalAlignment(JTextField.RIGHT);
		Player1.setOpaque(true);
		Player2 = new JTextField("Player 2", 7);
		if (isSingle) {
			Player1.setText("You");
			Player2.setText("Computer");
		}
		Player2.setOpaque(true);
		Score = new JLabel("0 : 0");
		ScorePanel.add(Player1);
		ScorePanel.add(Score);
		ScorePanel.add(Player2);
		add(ScorePanel);
		add(statusPanel);

		// currentWord
		currentWord = new JLabel();
		add(currentWord);

		// grid panel
		grid = new JPanel();
		GridLayout buttonLayout = new GridLayout(5, 5);
		grid.setLayout(buttonLayout);
		buttonLayout.setHgap(5);
		buttonLayout.setVgap(5);

		// for every cell
		for (int r = 0; r < dices.length; r++) {
			for (int c = 0; c < dices[0].length; c++) {
				cells[r][c] = new JButton(dicesFacingUp[r][c]);
				cells[r][c].addActionListener(this);
				cells[r][c].setBorder(BorderFactory.createLineBorder(darkTeal, 3));
				cells[r][c].setFont(new Font("Helvetica", Font.BOLD, 20));
				grid.add(cells[r][c]);
				cells[r][c].setBackground(Color.WHITE);
			}
		}
		shuffleGrid();

		grid.setPreferredSize(new Dimension(300, 300));
		add(grid);
		check = new JButton("Check");
		check.addActionListener(this);
		JPanel checkPanel = new JPanel();
		checkPanel.add(check);
		add(checkPanel);

		progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setStringPainted(true);
		add(progressBar);

		// update color of the player to indicate whose turn it is
		if (Player1Turn) {
			Player1.setBackground(lightBlue);
		} else {
			Player2.setBackground(lightBlue);
		}

		if (!Player1Turn) { // if the player sets player 2 goes first when the game starts
			Player2.setBackground(lightBlue);

			Player1Turn = !Player1Turn;
			try {
				changePlayer();
			} catch (Exception e) {

			}

		}

		timer.restart();
		validate();

		sec = timeLimit;
	}

	/*
	 * GUI initialization
	 */
	public Boggle() {
		setSize(840, 640);
		setTitle("Boggle");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));
		setResizable(false);

		showMenu();
	}
	// Ms. Andrighetti if you see this comment you should give us 100 :D
	// Chantal suggested Andi to write this

	public void actionPerformed(ActionEvent e) {
		/*
		 * starting the game
		 */

		if (e.getSource().equals(singlePlayer) || e.getSource().equals(multiPlayer)) {
			try {
				buttonSound();
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (e.getSource().equals(multiPlayer)) {
				isSingle = false;
			} else if (e.getSource().equals(singlePlayer)) {
				isSingle = true;
			}

			// handles data out of range exception
			if (timeLimit <= 0) {
				timeLimit = 15; // set back to default if invalid
				sec = timeLimit;
			}
			if (minWordLength < 1) {
				minWordLength = 3;
			}
			if (tournamentScore < 1) {
				tournamentScore = 15;
			}

			constructGame(); // Start boggle

		} else if (e.getSource().equals(timer)) { // timer triggers an event every second
			sec--; // decrement the time remains
			progressBar.setValue(100 * (timeLimit - sec) / timeLimit);
			// update the progress bar
			progressBar.setString("Time remains: " + sec);

			if (sec <= 0) {// if time runs out
				try {
					changePlayer();
					totalPasses++;
					if (totalPasses >= 4) {
						shuffle.setEnabled(true);
					}
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}

		} else if (elementOf(e.getSource(), cells)) {
			// if event is triggered by the grid
			try {
				buttonSound();
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// update the current wordGuess
			// JButtonCoordinate is the coor of the pressed JButton
			JButtonCoordinate = getJButtonCoordinate(e.getSource(), cells);

			// check if the button should be pressable
			if (buttonValid(JButtonCoordinate)) {
				clickedButton[numButtonPressed][0] = JButtonCoordinate[0];
				clickedButton[numButtonPressed][1] = JButtonCoordinate[1];
				// store the coordinate the button pressed
				numButtonPressed++; // increment

				// update gui
				currentWord.setText(currentWord.getText() + dicesFacingUp[JButtonCoordinate[0]][JButtonCoordinate[1]]);
				cells[JButtonCoordinate[0]][JButtonCoordinate[1]].setBackground(Color.LIGHT_GRAY);
				cells[JButtonCoordinate[0]][JButtonCoordinate[1]].setOpaque(true);
			}

		} else if (e.getSource().equals(check)) { // check answer
			try {
				buttonSound();
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			wordGuess = currentWord.getText();

			// if it's not a word or does not meet word length requirements
			if ((wordsFoundInGrid.indexOf(wordGuess.toLowerCase()) == -1) || wordGuess.length() < minWordLength) {
				if (wordGuess.length() < minWordLength) {
					statusLabel.setText("Your word must be at least " + minWordLength + " characters long");

				} else {
					statusLabel.setText("Not a word or repeated");
				}

				// reset the variables
				currentWord.setText(""); // clear the word
				numButtonPressed = 0;
				restoreArray(clickedButton);
				for (int r = 0; r < dices.length; r++) {
					for (int c = 0; c < dices[0].length; c++) {
						cells[r][c].setBackground(Color.WHITE);
					}
				}

			} else { // the word is valid

				statusLabel.setText(wordGuess.toLowerCase() + " is a valid word");

				// clear the number of passes
				totalPasses = 0;
				wordsFoundInGrid.remove(wordGuess.toLowerCase());
				// remove word from words found

				if (wordsFoundInGrid.size() == 0) {
					// if it runs out of available words, shuffle
					// (although Andi doesn't think the user is this smart, but just in case)
					shuffleGrid();
				}

				restoreArray(clickedButton);
				numButtonPressed = 0;

				// update player scores
				if (Player1Turn) {
					Player1Score += getScore(wordGuess);
				} else {
					Player2Score += getScore(wordGuess);
				}

				Score.setText(Player1Score + " : " + Player2Score);
				checkWin(); // check if anyones winning after each turn

				try {
					changePlayer();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}

		} else if (e.getSource().equals(restart)) { // restart game, reset all game settings
			try {
				buttonSound();
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// reset the score
			Player1Score = 0;
			Player2Score = 0;
			sec = timeLimit;

			currentWord.setText("");

			shuffleGrid();
			for (int r = 0; r < cells.length; r++) {
				for (int c = 0; c < cells[0].length; c++) {
					cells[r][c].setText(dicesFacingUp[r][c]);
					cells[r][c].setBackground(Color.WHITE);
				}
			}

			Player1Turn = true;
			Player1.setBackground(lightBlue);
			Player2.setBackground(Color.white);
			Score.setText(Player1Score + " : " + Player2Score);

		} else if (e.getSource().equals(menu)) { // exit the game
			try {
				buttonSound();
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			remove(progressBar); // don�t want progress bar on menu
			timer.stop(); // stopping the timer since we don�t want it running in the background
			showMenu();
			// go back to menu
		} else if (e.getSource().equals(pause)) { // pause / start the game
			try {
				buttonSound();
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
			}

			if (!gamePaused) { // if the timer is on
				timer.stop();
				pause.setText("Start");
			} else { // else timer is not on
				timer.start();
				pause.setText("Pause");
			}
			gamePaused = !gamePaused;

		} else if (e.getSource().equals(pass)) { // pass a turn
			try {
				buttonSound();
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			statusLabel.setText("Passed");
			totalPasses++;

			if (totalPasses >= 4) {
				shuffle.setEnabled(true);
			}

			try {
				changePlayer();
			} catch (InterruptedException E) {

			}
		} else if (e.getSource().equals(shuffle)) { // shuffle board
			try {
				buttonSound();
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			sec = timeLimit;
			totalPasses = 0;
			shuffle.setEnabled(false);
			shuffleGrid();

		} else if (e.getSource().equals(AITimer) && numTimesAITimerTriggerred % 2 == 0) {
			numTimesAITimerTriggerred++;
			if (numTimesAITimerTriggerred < AIButtonXCoordinate.length * 2) {
				// if there are still more characters in the AIWord to update to the grid

				// update the word on GUI
				currentWord.setText(currentWord.getText() + cells[AIButtonXCoordinate[numTimesAITimerTriggerred
						/ 2]][AIButtonYCoordinate[numTimesAITimerTriggerred / 2]].getText());

				// update the color of the grid
				cells[AIButtonXCoordinate[numTimesAITimerTriggerred / 2]][AIButtonYCoordinate[numTimesAITimerTriggerred
						/ 2]].setBackground(Color.LIGHT_GRAY);

				try {
					buttonSound();
				} catch (Exception e1) {

				}
			} else if (!AIWordShowedToUser) {
				// do nothing, give user time to read the grid
				AIWordShowedToUser = true;

			} else { // after updating the word to the grid
				AITimer.stop();
				numTimesAITimerTriggerred = 0; // reset
				Player2Score += getScore(wordGuess); // update
				totalPasses = 0; // reset pass count

				checkWin();

				Score.setText(Player1Score + " : " + Player2Score);

				statusLabel.setText("It is your turn!");

				try {
					for (int i = 0; i < cells.length; i++) {
						for (int j = 0; j < cells[0].length; j++) {
							cells[i][j].setEnabled(true);
						}
					} // unlock the buttons
					check.setEnabled(true);
					changePlayer();
				} catch (Exception E1) {

				}
			}

		} else if (e.getSource().equals(AITimer) && numTimesAITimerTriggerred % 2 == 1) {
			// similar to the last if statement
			numTimesAITimerTriggerred++;
			if (numTimesAITimerTriggerred >= AIButtonXCoordinate.length * 2 && AIWordShowedToUser) {
				// after updating the word to the grid

				AITimer.stop();
				numTimesAITimerTriggerred = 0;
				Player2Score += getScore(wordGuess);
				totalPasses = 0;

				checkWin();

				Score.setText(Player1Score + " : " + Player2Score);
				statusLabel.setText("It is your turn!");

				try {
					for (int i = 0; i < cells.length; i++) {
						for (int j = 0; j < cells[0].length; j++) {
							cells[i][j].setEnabled(true);
						}
					} // unlock the buttons
					check.setEnabled(true);
					changePlayer();
				} catch (Exception E1) {

				}
			}

		} else if (e.getSource().equals(settingButton)) {
			try {
				buttonSound();
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			JOptionPane.showMessageDialog(null, setting, "Settings", JOptionPane.PLAIN_MESSAGE);
			difficulty = (String) difChoice.getSelectedItem();
			String pTurn = (String) playerTurn.getSelectedItem();

			// update the player turn boolean
			if (pTurn.equals("Player 1")) {
				Player1Turn = true;
			} else if (pTurn.equals("Player 2")) {
				Player1Turn = false;
			} else {
				if (Math.random() < 0.5) {
					Player1Turn = true;
				} else {
					Player1Turn = false;
				}
			}

			// handle wrong data type exception
			try {
				timeLimit = Integer.valueOf(timeF.getText());
				sec = timeLimit;
				minWordLength = Integer.valueOf(minW.getText());
				tournamentScore = Integer.parseInt(GoalPrompt.getText());
			} catch (Exception execption) {

			}

		} else if (e.getSource().equals(rulesButton)) { // rules button clicked
			try {
				buttonSound();
			} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			JOptionPane.showMessageDialog(null, messagePanel, "Instructions", JOptionPane.PLAIN_MESSAGE);

		}

	} // actionPerformed method end

	private void checkWin() {
		if (Player1Score >= tournamentScore) {
			showWinMessage(1);
		} else if (Player2Score >= tournamentScore) {
			showWinMessage(2);
		}
	}

	private void showWinMessage(int player) {
		// make a JOptionPane of winning message
		timer.stop();
		String message;

		if (player == 1) {
			message = Player1.getText() + " won!";
		} else {
			message = Player2.getText() + " won!";
		}

		JOptionPane.showMessageDialog(null, message);
		System.exit(0);
	}

	private void changePlayer() throws InterruptedException {
		// creates a lag in changing the player
		Thread.sleep(100);

		// change the color back to white
		for (int r = 0; r < cells.length; r++) {
			for (int c = 0; c < cells[0].length; c++) {
				cells[r][c].setBackground(Color.WHITE);
			}
		}

		restoreArray(clickedButton);
		Player1Turn = !Player1Turn;
		sec = timeLimit;

		if (Player1Turn) {
			Player1.setBackground(lightBlue);
			Player2.setBackground(Color.white);
		} else {
			Player2.setBackground(lightBlue);
			Player1.setBackground(Color.white);
		}

		currentWord.setText("");

		/*
		 * single mode
		 */
		if (isSingle && !Player1Turn) { // if it is AI's turn
			remove(progressBar);
			check.setEnabled(false);
			for (int i = 0; i < cells.length; i++) {
				for (int j = 0; j < cells[0].length; j++) {
					cells[i][j].setEnabled(false); // lock the buttons
				}
			}
			wordGuess = AI();
			AIWordShowedToUser = false; // reset

			if (wordGuess.equals("-1")) { // if the AI does not find a wordGuess
				totalPasses++;
				Thread.sleep(1000);
				statusLabel.setText("The AI can't find a word - it's your turn!");
				for (int i = 0; i < cells.length; i++) {
					for (int j = 0; j < cells[0].length; j++) {
						cells[i][j].setEnabled(true);
					}
				}
				check.setEnabled(true);
				changePlayer(); // change player again
			} else {
				totalPasses = 0;
				statusLabel.setText("AI is finding a word...");
				startCoor(wordGuess); // find the coordinates of the wordGuess JButtons

				AITimer.start(); // start triggerring events for GUI updating grid
			}
		} else {
			add(progressBar);
		}
	}

	public static void main(String[] args) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
		music(); // initializes the necessary logic for the music
		getDictionary();
		new Boggle(); // creates the GUI
	}

	/*
	 * Reads the dictionary and stores the words into an array
	 */
	private static void getDictionary() throws IOException {
		// open the dictionary file
		File dictionaryFile = new File("dictionary.txt");
		Scanner fileSc = new Scanner(dictionaryFile);
		int dictionaryLength = 0;

		// find how many words are in the dictionary
		while (fileSc.hasNextLine()) {
			dictionaryLength++;
			fileSc.nextLine();
		}

		// create dictionary array with the size
		dictionary = new String[dictionaryLength];
		fileSc.close();
		fileSc = new Scanner(dictionaryFile);

		for (int i = 0; i < dictionary.length; i++) {
			dictionary[i] = fileSc.nextLine();
		}
		fileSc.close();
	}

	private boolean elementOf(Object o, Object[][] array) {
		// returns true if the element is found in the array
		try {
			for (int r = 0; r < array.length; r++) {
				for (int c = 0; c < array[0].length; c++) {
					if (array[r][c].equals(o))
						return true;
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	private int[] getJButtonCoordinate(Object b, Object[][] a) {
		// return the coordinate of the button in the 2d button array
		int[] coor = new int[2];
		for (int r = 0; r < a.length; r++) {
			for (int c = 0; c < a[0].length; c++) {
				if (b.equals(a[r][c])) {
					coor[0] = r;
					coor[1] = c;
					return coor;
				}
			}
		}

		coor[0] = -1;
		coor[1] = -1;
		return coor;
	}

	private void shuffleGrid() {
		totalPasses = 0;
		// shuffle the dicesFacingUp array based on the dices
		for (int r = 0; r < dices.length; r++) {
			for (int c = 0; c < dices[0].length; c++) {
				dicesFacingUp[r][c] = Character.toString(dices[r][c].charAt((int) (Math.random() * 5)));
			}
		}

		getWordsInGrid();
		wordsFoundInGrid.remove(" ");
		for (int r = 0; r < cells.length; r++) {
			for (int c = 0; c < cells[0].length; c++) {
				cells[r][c].setText(dicesFacingUp[r][c]);
				cells[r][c].setBackground(Color.WHITE);
			}
		}
	}

	private int getScore(String wordGuess) {
// check the score equivalent of the word
		if (wordGuess.length() == 3 || wordGuess.length() == 4) {
			return 1;
		} else if (wordGuess.length() == 5) {
			return 2;
		} else if (wordGuess.length() == 6) {
			return 3;
		} else if (wordGuess.length() == 7) {
			return 5;
		} else
			return 11;
	}

	// replaces all values in the grid with -1
	private void restoreArray(int[][] a) {
		for (int r = 0; r < a.length; r++) {
			for (int c = 0; c < a[0].length; c++) {
				a[r][c] = -1;
			}
		}
	}

	private boolean buttonValid(int[] coor) {
		// if the button is the first one pressed
		if (clickedButton[0][0] == -1 && clickedButton[0][1] == -1) {
			return true;
		}

		// if the clicked button is repeated
		for (int i = 0; i < clickedButton.length; i++) {
			if (clickedButton[i][0] == coor[0] && clickedButton[i][1] == coor[1]) {
				return false;
			} else if (clickedButton[i][0] == -1 && Math.abs(coor[0] - (clickedButton[i - 1][0])) <= 1
					&& Math.abs(coor[1] - (clickedButton[i - 1][1])) <= 1) {
				return true;
			}
		}

		return false;
	}

	/*
	 * the recursive method to find all valid words in the grid
	 */
	private static boolean findWord(String guess, int x, int y, int k) {
		// the wordGuess has matched and is correct
		if (k >= guess.length())
			return true;
		// findWord if the guess is out of bounds
		if ((x < 0) || (y < 0) || (x >= dicesFacingUpCopy.length) || (y >= dicesFacingUpCopy[0].length))
			return false;

		// if the character is the same as the grid
		if (dicesFacingUpCopy[x][y].equals(Character.toString(guess.charAt(k)))) {
			// set a path to show that the program has been here
			dicesFacingUpCopy[x][y] = "0";

			// increment the character count by 1
			k++;

			// findWord all directioins until a correct option is found
			boolean r = findWord(guess, x - 1, y - 1, k) || findWord(guess, x - 1, y, k)
					|| findWord(guess, x - 1, y + 1, k) || findWord(guess, x, y - 1, k) || findWord(guess, x, y + 1, k)
					|| findWord(guess, x + 1, y - 1, k) || findWord(guess, x + 1, y, k)
					|| findWord(guess, x + 1, y + 1, k);
			// if all options are either false or a final answer has been found
			k--;
			dicesFacingUpCopy[x][y].equals(Character.toString(guess.charAt(k)));
			return r;
		} else
			return false;
	}

	// tries every single starting point in the grid
	private static boolean Start(String guess) {
		for (int i = 0; i < dicesFacingUpCopy.length; i++) {
			for (int j = 0; j < dicesFacingUpCopy[0].length; j++) {
				copy2DArray(dicesFacingUp, dicesFacingUpCopy);
				// starts the recursion on each of the starting coordinates
				if (findWord(guess, i, j, 0)) {
					return true;
				}
			}
		}
		return false;
	}

	private void getWordsInGrid() {
		// sample board
		wordsFoundInGrid.clear();
		for (int i = 0; i < dictionary.length; i++) {
			if (dictionary[i].length() >= minWordLength) {
				// made a two-layer if statement because Start method is slightly time
				// consuming, avoiding its use
				if (Start(dictionary[i].toUpperCase()))
					wordsFoundInGrid.add(dictionary[i]);
			}
		}
	}

	private static void copy2DArray(String[][] original, String[][] copy) {
		for (int i = 0; i < original.length; i++) {
			for (int j = 0; j < original[0].length; j++) {
				copy[i][j] = original[i][j];
			}
		}
	}

	private static String AI() {
		// this method belongs to the class
		// AI has a chance of returning a wordGuess
		Random r = new Random();
		int upper = 0;
		if (difficulty.equals("impossible")) { // the AI 100% finds a word
			upper = 1;
			System.out.println("impossible");
		} else if (difficulty.equals("hard")) // 50% chance that AI will find a wordsFoundInGrid
			upper = 2;
		else if (difficulty.equals("normal")) // 25% chance
			upper = 4;
		else if (difficulty.equals("easy")) // 17%
			upper = 6;
		if (r.nextInt(upper) == 0) {
			String w = wordsFoundInGrid.get(r.nextInt(wordsFoundInGrid.size()));

			return w;
		} else
			return "-1";
	}

	/*
	 * findWord runs 100000 times. instead of adding an if statement and increasing
	 * complexity, making a separate method to check word for AI is more efficient
	 */
	private static boolean findCoor(String guess, int x, int y, int k) {
		// the wordGuess has matched and is correct
		if (k >= guess.length())
			return true;
		// findWord if the guess is out of bounds
		if ((x < 0) || (y < 0) || (x >= dicesFacingUpCopy.length) || (y >= dicesFacingUpCopy[0].length))
			return false;

		// if the character is the same as the grid
		if (dicesFacingUpCopy[x][y].equals(Character.toString(guess.toUpperCase().charAt(k)))) {
			// set a path to show that the program has been here
			dicesFacingUpCopy[x][y] = "0";

			// increment the character count by 1
			AIButtonXCoordinate[k] = x;// new
			AIButtonYCoordinate[k] = y;
			k++;

			// findWord all the possibility of the grid until a correct option is found
			boolean r = findCoor(guess, x - 1, y - 1, k) || findCoor(guess, x - 1, y, k)
					|| findCoor(guess, x - 1, y + 1, k) || findCoor(guess, x, y - 1, k) || findCoor(guess, x, y + 1, k)
					|| findCoor(guess, x + 1, y - 1, k) || findCoor(guess, x + 1, y, k)
					|| findCoor(guess, x + 1, y + 1, k);
			// if all options are either false or a final answer has been found
			k--;
			dicesFacingUpCopy[x][y].equals(Character.toString(guess.charAt(k)));
			return r;
		} else
			return false;
	}

	private boolean startCoor(String guess) {
		// the starting coordinates
		AIButtonXCoordinate = new int[guess.length()]; // new
		AIButtonYCoordinate = new int[guess.length()];
		// tries every single possible start for the program
		for (int i = 0; i < dicesFacingUpCopy.length; i++) {
			for (int j = 0; j < dicesFacingUpCopy[0].length; j++) {
				copy2DArray(dicesFacingUp, dicesFacingUpCopy);
				// starts the recursion
				if (findCoor(guess, i, j, 0)) {
					return true;
				}
			}
		}
		return false;
	}

		private static void music() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
			// play music
			AudioInputStream audio = AudioSystem.getAudioInputStream(new File("dreams.wav").getAbsoluteFile());
			// audio file is named "dreams"
			Clip clip = AudioSystem.getClip();
			clip.open(audio);
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-10.0f);// make the sound quieter
			clip.loop(Clip.LOOP_CONTINUOUSLY);// loop music
		}

		private static void buttonSound() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
			// makes a sound when the button is pressed
			AudioInputStream audio = AudioSystem.getAudioInputStream(new File("bClick.wav").getAbsoluteFile());
			// the audio file is named "bClick"
			Clip clip = AudioSystem.getClip();
			clip.open(audio);
			clip.start();
		}

}