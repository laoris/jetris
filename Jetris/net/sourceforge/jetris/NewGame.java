package net.sourceforge.jetris;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Component;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.applet.AudioClip;
import java.applet.Applet;
import res.ResClass;
import java.lang.*;

/*			/			/			/			/			/
/	This is a GUI to implement the New Game menu option.						/
/	NewGame allows the user to start a new game of their choice via radio buttons		/
/	This class is brGoen down into two sections VISUAL APPEARANCE and LOGIC		/
/	To alter the loGo of the window, Go to the VISUAL APPEARANCE section.			/	
/	To alter how the window works, Go to the LOGIC section						/
/			/			/			/			/		         */


public class NewGame extends JFrame implements ChangeListener, MouseListener, ActionListener //, FocusListener
{
	//1 player game selection radio buttons
	private JRadioButton normal = new JRadioButton("Normal", true);
	
	//2 player game selection radio buttons
	private JRadioButton FTL = new JRadioButton("First to Lose");		//First to Lose (normal play 2player)
	private JRadioButton FTS = new JRadioButton("First to Score");		//First to Score (play until a certain score 2player)
	private JRadioButton FXL = new JRadioButton("First to X Lines");	//First to get X Lines
	private JRadioButton DEMO = new JRadioButton("Demo Mode");			//Automated demo mode
	private JTextField scoreField = new JTextField("50000",8);
	private JTextField lineField = new JTextField("50",3);
	Container container = this.getContentPane();
	JButton Go = new JButton("Go");
	JButton Cancel = new JButton("Cancel");
	private final int LOSE = 0;
	private final int SCORE = 1;
	private final int LINES = 2;
	AudioClip clip;
	//public static boolean multi = false;
	//Integer lines = new Integer(0);
	//Integer score = new Integer(0);

	
	public NewGame()
	{
	
	//////////////////////////////////////////////////////////VISUAL APPEARANCE//////////////////////////////////////////////////////////
		super("Jetris - New Game");
		//lines = lineVal;
		//score = scoreVal;
		
		//create button group for the radio buttons, alter this if adding new buttons
		ButtonGroup gameModes = new ButtonGroup();	
		gameModes.add(normal);
		gameModes.add(FTL);
		gameModes.add(FTS);
		gameModes.add(FXL);
		gameModes.add(DEMO);
		
		//Text fields
		JPanel scorePanel = new JPanel();
		JPanel scoreSpace = new JPanel();
		JPanel lineSpace = new JPanel();
	
		scorePanel.setLayout(new GridLayout(1,2));
		scorePanel.setMaximumSize(new Dimension(250,20));

		scoreField.setEditable(false);
		scoreField.setMaximumSize(new Dimension(100,20));

		scorePanel.add(scoreSpace);
		scorePanel.add(scoreField);


		JPanel linePanel = new JPanel();
		linePanel.setLayout(new GridLayout(1,2));
		linePanel.setMaximumSize(new Dimension(250,20));


		lineField.setEditable(false);
		lineField.setMaximumSize(new Dimension(100,20));

		linePanel.add(lineSpace);
		linePanel.add(lineField);

		//create One Player Half of mainPanel 
		JPanel onePlayerPanel = new JPanel();
		JLabel onePlayer = new JLabel("1 Player");
		JPanel buttonPanel1 = new JPanel();
		buttonPanel1.setLayout(new BoxLayout(buttonPanel1, BoxLayout.PAGE_AXIS));
		buttonPanel1.add(normal);
		buttonPanel1.add(Box.createRigidArea(new Dimension(0,49))); //<-- used for Centering reasons, alter this if adding new buttons to buttonPanel1, change this value
		
		onePlayerPanel.setLayout(new BoxLayout(onePlayerPanel, BoxLayout.PAGE_AXIS));
		onePlayerPanel.add(onePlayer);
		onePlayerPanel.add(buttonPanel1);
		
		//create Two Player half of mainPanel
		JPanel twoPlayerPanel = new JPanel();
		JLabel twoPlayer = new JLabel("2 Player");
		JPanel buttonPanel2 = new JPanel();
		buttonPanel2.setLayout(new BoxLayout(buttonPanel2, BoxLayout.PAGE_AXIS));
		buttonPanel2.add(FTL);
		buttonPanel2.add(FTS);
		buttonPanel2.add(scorePanel);
		buttonPanel2.add(FXL);
		buttonPanel2.add(linePanel);
		buttonPanel2.add(DEMO);
		
		twoPlayerPanel.setLayout(new BoxLayout(twoPlayerPanel, BoxLayout.PAGE_AXIS));
		twoPlayerPanel.add(twoPlayer);
		twoPlayerPanel.add(buttonPanel2);
		
		//construct main panel
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.LINE_AXIS));
		mainPanel.add(Box.createHorizontalGlue());
		mainPanel.add(onePlayerPanel);
		mainPanel.add(Box.createRigidArea(new Dimension(20,0)));
		mainPanel.add(twoPlayerPanel);
		mainPanel.add(Box.createHorizontalGlue());
		
		//contsruct button panel (Go and Cancel)
		JPanel selectionPanel = new JPanel();
		selectionPanel.setLayout(new GridLayout(1,2));
		
		selectionPanel.add(Go);
		//selectionPanel.add(Cancel);

		//construct complete window
		JLabel newGameLabel = new JLabel("New Game:");
		newGameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
		JLabel label = new JLabel();
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
		Icon icon = new ImageIcon(TwoPlayerGame.loadImage("splash.png"));
		label.setIcon(icon);
		container.add(Box.createHorizontalGlue());
		container.add(label);
		container.add(newGameLabel);
		container.add(Box.createRigidArea(new Dimension(0,20)));
		container.add(mainPanel);
		container.add(Box.createRigidArea(new Dimension(0,20)));
		container.add(selectionPanel);
		container.add(Box.createVerticalGlue());
		
		setLocation(300,150); //<-- alter this to refer back to JetrisMainFrame's current screen position so it is centered
		setResizable(false);
		//setUndecorated(true);
		setSize(600, 460);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //consider disabling close operation on this GUI
		setVisible(true);
		
		//////////////////////////////////////////////////////////LOGIC//////////////////////////////////////////////////////////
		//addFocusListener( this );
		FXL.addChangeListener(this);
		FTL.addChangeListener(this);
		FTS.addChangeListener(this);
		DEMO.addChangeListener(this);
		normal.addChangeListener(this);
		Go.addMouseListener(this);
		Cancel.addActionListener(this);
		
		clip = Applet.newAudioClip(new ResClass().getClass().getResource("tetris_blast_ingame.mid"));
		clip.loop();
		
		//addComponentListener(this);
	}


	//This method contains the logic that drives which text field is editable
	public void stateChanged(ChangeEvent e)
	{
		if (FTS.isSelected())
		{
			scoreField.setEditable(true);
			lineField.setEditable(false);
		}
		else if (FXL.isSelected())
		{
			scoreField.setEditable(false);
			lineField.setEditable(true);
		}
		else if (FTL.isSelected() || normal.isSelected())
		{
			scoreField.setEditable(false);
			lineField.setEditable(false);
		}
	}

	//logic for Go button
	public void mouseClicked(MouseEvent e)
	{
		try
		{
			clip.stop();
			if (FTS.isSelected())
			{
				int score = Integer.parseInt(scoreField.getText());
				new TwoPlayerGame(SCORE, false, score);
			}
			else if (FXL.isSelected())
			{
				int lines = Integer.parseInt(lineField.getText());
				new TwoPlayerGame(LINES, false, lines);
			}
			else if (FTL.isSelected())
			{
				new TwoPlayerGame(LOSE, false, 0);
			}
			else if (DEMO.isSelected())
			{
				new TwoPlayerGame(LOSE, true, 0);
			}
			else
			{
				new OnePlayerGame();
			}

			this.dispose();
		}
		catch (NumberFormatException exc)
		{
			JOptionPane.showMessageDialog(null,"    Invalid Entry: Please type only numbers" ,"Error", JOptionPane.PLAIN_MESSAGE);
			System.out.println("Invalid Number Format. Create Warning.");
		}
	}
	
	public void actionPerformed(ActionEvent e)
	{
		this.dispose();
	}
	public void mouseEntered(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }


}