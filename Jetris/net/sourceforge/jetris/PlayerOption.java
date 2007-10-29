package net.sourceforge.jetris;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.applet.AudioClip;
import java.applet.Applet;

public class PlayerOption extends JFrame {
	
	JButton onePlayer = new JButton("1 Player ");
	JButton twoPlayers = new JButton("2 Players");
	JLabel welcome = new JLabel("Welcome to Jetris, choose how many players");
	AudioClip clip;
	
	public PlayerOption() {
		super("Welcome!");
		setLayout(new FlowLayout());
		add(welcome);
		add(onePlayer);
		add(twoPlayers);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300,100);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width/2 - 150, screenSize.height/2 - 50);
		setResizable(false);
		setVisible(true);      
        //clip = Applet.newAudioClip(getClass().getResource("res/tetris_blast_ingame.mid"));
        //clip.loop();
		onePlayer.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent aEvt) {
					setVisible(false);
					//clip.stop();
					OnePlayerGame mf = new OnePlayerGame();
					dispose();
				}
			}
		);
		twoPlayers.addActionListener( new ActionListener() {
				public void actionPerformed(ActionEvent aEvt) {
					setVisible(false);
					//clip.stop();
					TwoPlayerGame MC = new TwoPlayerGame();
					dispose();
				}
			}
		);
	}
	
}