package net.sourceforge.jetris;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.awt.event.*;

public class KeyConfig extends JFrame implements ActionListener, MouseListener {
	
	// holds the number of players this game has
	private int playNum;
	
	Container container = this.getContentPane();
	
	//These are the field the use will input the characters in
	private JTextField oneUpField = new JTextField(1);
	private JTextField oneLeftField = new JTextField(1);
	private JTextField oneRightField = new JTextField(1);
	private JTextField oneDownField = new JTextField(1);
	private JTextField twoUpField = new JTextField(1);
	private JTextField twoLeftField = new JTextField(1);
	private JTextField twoRightField = new JTextField(1);
	private JTextField twoDownField = new JTextField(1);
	
	//These are the descriptive Labels
	private JLabel oneUpLbl = new JLabel( "1P Rotate:");
	private JLabel oneLeftLbl = new JLabel( "1P Left:");
	private JLabel oneRightLbl = new JLabel( "1P Right:");
	private JLabel oneDownLbl = new JLabel( "1P Fast Fall:");
	private JLabel twoUpLbl = new JLabel( "2P Rotate:");
	private JLabel twoLeftLbl = new JLabel( "2P Left:");
	private JLabel twoRightLbl = new JLabel( "2P Right:");
	private JLabel twoDownLbl = new JLabel( "2P Fast Fall:");
	private JLabel space = new JLabel("");
	
	//These are the buttons
	private JButton okBtn = new JButton( "OK" );
	private JButton cancelBtn = new JButton( "Cancel" );
	
	public KeyConfig( int playerNumber )
	{
		super( "Key Configuration");
		playNum = playerNumber;
		
		JPanel masterPanel = new JPanel( new GridLayout(5, 4));
		
		//This disables the two player fields if we are in a one player game.
		if( playerNumber == 2)
		{
			twoUpField.setEnabled(false);
			twoLeftField.setEnabled(false);
			twoRightField.setEnabled(false);
			twoDownField.setEnabled(false);
		}
		
		masterPanel.add(oneUpLbl);
		masterPanel.add(oneUpField);
		masterPanel.add(twoUpLbl);
		masterPanel.add(twoUpField);	
		masterPanel.add(oneLeftLbl);
		masterPanel.add(oneLeftField);
		masterPanel.add(twoLeftLbl);
		masterPanel.add(twoLeftField);
		masterPanel.add(oneRightLbl);
		masterPanel.add(oneRightField);
		masterPanel.add(twoRightLbl);
		masterPanel.add(twoRightField);
		masterPanel.add(oneDownLbl);
		masterPanel.add(oneDownField);
		masterPanel.add(twoDownLbl);
		masterPanel.add(twoDownField);
		masterPanel.add(space);
		masterPanel.add(okBtn);
		masterPanel.add(cancelBtn);
		
		container.add(masterPanel);
	}
    
	public void actionPerformed( ActionEvent e) {}
	public void mouseClicked( MouseEvent e) {}
	public void mousePressed( MouseEvent e) {}
	public void mouseReleased( MouseEvent e) {}
	public void mouseEntered( MouseEvent e) {}
	public void mouseExited( MouseEvent e) {}
	
}
