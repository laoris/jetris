package net.sourceforge.jetris;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.awt.event.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/*
This class configures the keys for Jetris, it takes as a parameter whether the game is 2 player or one player
default values of up down left and right for the one player mode are the up down left and right arrows
for two player: default values for player 1 are up left down right = w a s d;  for player 2: up left down and right arrows
*/
public class KeyConfig extends JFrame implements ActionListener, MouseListener, FocusListener 
{	
	// holds the number of players this game has
	private int playNum;
	private KeyListener keyHandler;
	Container container = this.getContentPane();
	
	//These are the field the use will input the characters in
	private JTextField oneUpField = new JTextField(1); //use getKey function(0 -> up, 1-> left, 2-> right, 3->down)
	private JTextField oneLeftField = new JTextField(1);
	private JTextField oneRightField = new JTextField(1);
	private JTextField oneDownField = new JTextField(1);
	private JTextField oneDropField = new JTextField(1);
	private JTextField twoUpField = new JTextField(1);
	private JTextField twoLeftField = new JTextField(1);
	private JTextField twoRightField = new JTextField(1);
	private JTextField twoDownField = new JTextField(1);
	private JTextField twoDropField = new JTextField(1);
	
	//These are the descriptive Labels
	private JLabel oneUpLbl = new JLabel( "1P Rotate:");
	private JLabel oneLeftLbl = new JLabel( "1P Left:");
	private JLabel oneRightLbl = new JLabel( "1P Right:");
	private JLabel oneDownLbl = new JLabel( "1P Down:");
	private JLabel oneDropLbl = new JLabel( "1P Drop:");
	private JLabel twoUpLbl = new JLabel( "2P Rotate:");
	private JLabel twoLeftLbl = new JLabel( "2P Left:");
	private JLabel twoRightLbl = new JLabel( "2P Right:");
	private JLabel twoDownLbl = new JLabel( "2P Down:");
	private JLabel twoDropLbl = new JLabel( "2P Drop:");
	private JLabel spacer1 = new JLabel("");
	private JLabel spacer2 = new JLabel("");
	private JLabel spacer3 = new JLabel("");
	private JLabel spacer4 = new JLabel("");
	private JLabel errorLabel = new JLabel("");
	
	//These are the buttons
	private JButton okBtn = new JButton( "OK" );
	private JButton cancelBtn = new JButton( "Cancel" );
	private JButton defaultBtn = new JButton("Default");
	
	//These arrays will store the desired key configuration
	private int keyArraySize = 5; // there are 5 controls, up, left, right, down, drop
	private int[] onePlayerKeys;// = new int[keyArraySize]; //[0] gets up [1] gets left [2] gets right [3] gets down
	private int[] twoPlayerKeys;// = new int[keyArraySize];
	private int[] tempOnePlayerKeys = new int[keyArraySize]; //holds the Keys temporarily until ok is hit, then it commits
	private int[] tempTwoPlayerKeys = new int[keyArraySize]; 
	private int[] offLimitKeys = {KeyEvent.VK_N, KeyEvent.VK_K, KeyEvent.VK_R, KeyEvent.VK_P,KeyEvent.VK_M, KeyEvent.VK_H, KeyEvent.VK_X,KeyEvent.VK_J};
	private boolean noRepeatKeys = true;
	
	public KeyConfig( int playerNumber, int[] onePKeys, int[] twoPKeys)
	{
		super( "Key Configuration");
		playNum = playerNumber;
		onePlayerKeys = onePKeys;
		
		setOnePlayerKeys(onePKeys);
		//copy values from onePlayerKeys to tempOnePlayerKeys
		for(int i =0; i< keyArraySize; i++)
		{
			tempOnePlayerKeys[i] = onePlayerKeys[i];
		}

		if(playNum ==2)
		{

			twoPlayerKeys = twoPKeys;
			setTwoPlayerKeys(twoPKeys);
			for(int i =0; i< keyArraySize; i++)
			{
				tempTwoPlayerKeys[i] = twoPlayerKeys[i];
			}

		}
		
		JPanel masterPanel = new JPanel( new GridLayout(5, 4)); //contains the textfields and labels
		JPanel buttonPanel = new JPanel(new GridLayout(1,5)); //contains the buttons
		
		//This disables the two player fields if we are in a one player game.
		if( playerNumber == 1)
		{
			twoUpField.setEnabled(false);
			twoLeftField.setEnabled(false);
			twoRightField.setEnabled(false);
			twoDownField.setEnabled(false);
			twoDropField.setEnabled(false);
			
			twoUpField.setText("Disabled for one player mode");
			twoLeftField.setText("Disabled for one player mode");
			twoRightField.setText("Disabled for one player mode");
			twoDownField.setText("Disabled for one player mode");
			twoDropField.setText("Disabled for one player mode");
			
		}
		//construct the GUI
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
		masterPanel.add(oneDropLbl);
		masterPanel.add(oneDropField);
		masterPanel.add(twoDropLbl);
		masterPanel.add(twoDropField);
		
		//construct buttonPanel and set its maximum size so it looks prettier
		buttonPanel.add(defaultBtn);
		buttonPanel.add(spacer4);
		buttonPanel.add(okBtn);
		buttonPanel.add(cancelBtn);
		buttonPanel.setMaximumSize(new Dimension(320,10));
		
		//construct the full window
		container.setLayout(new BoxLayout(container, BoxLayout.PAGE_AXIS));
		container.add(masterPanel);
		container.add(Box.createRigidArea(new Dimension(0,20)));
		errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		container.add(errorLabel);
		container.add(Box.createRigidArea(new Dimension(0,20)));
		container.add(buttonPanel);
		
		this.setSize(400,300);
		this.setVisible(true);
		this.setResizable(false);
		
//////////////////////////////////////start Logic////////////////////////////////////////////////////		
		
		//add action listeners to the buttons
		okBtn.addActionListener(this);
		defaultBtn.addActionListener(this);
		cancelBtn.addActionListener(this);
		
		//make all textfields uneditable
		oneUpField.setEditable(false);
		oneLeftField.setEditable(false);
		oneRightField.setEditable(false);
		oneDownField.setEditable(false);
		oneDropField.setEditable(false);
		twoUpField.setEditable(false);
		twoLeftField.setEditable(false);
		twoRightField.setEditable(false);
		twoDownField.setEditable(false);
		twoDropField.setEditable(false);
		oneUpField.requestFocusInWindow();
		oneUpField.setBackground(Color.WHITE);
		
		keyHandler = new KeyAdapter()
		{
			public void keyPressed(KeyEvent e) //fills in the arrays and sets the text boxes when the user types keys
			{
				
				int code = e.getKeyCode();
				
				for( int i = 0; i < offLimitKeys.length; i++ )
				{
					if( code == offLimitKeys[i])
					{
						errorLabel.setText("The key " + e.getKeyText(code) + " is reserved for a hot key, please choose another key");
						return;
					}
					
				}
				errorLabel.setText("");
				String text = e.getKeyText(code);
				
				if(oneUpField.isFocusOwner())
				{
					oneUpField.setText(text);
					tempOnePlayerKeys[0] = code;
					oneLeftField.requestFocusInWindow();
					//oneLeftField.setBackground(Color.WHITE);
				}
				else if(oneLeftField.isFocusOwner())
				{
					oneLeftField.setText(text);
					tempOnePlayerKeys[1] = code;
					oneRightField.requestFocusInWindow();
					//oneRightField.setBackground(Color.WHITE);
				}
				else if(oneRightField.isFocusOwner())
				{
					oneRightField.setText(text);
					tempOnePlayerKeys[2] = code;
					oneDownField.requestFocusInWindow();
					//oneDownField.setBackground(Color.WHITE);
				}
				else if(oneDownField.isFocusOwner())
				{
					oneDownField.setText(text);
					tempOnePlayerKeys[3] = code;
					oneDropField.requestFocusInWindow();
					//oneDropField.setBackground(Color.WHITE);
				}
				else if(oneDropField.isFocusOwner())
				{
					oneDropField.setText(text);
					tempOnePlayerKeys[4] = code;
					
					if( playNum == 1)
					{
						oneUpField.requestFocusInWindow();
						//oneUpField.setBackground(Color.WHITE);
					}
					else
					{
						twoUpField.requestFocusInWindow();
					}
				}
				else if(twoUpField.isFocusOwner())
				{
					twoUpField.setText(text);
					tempTwoPlayerKeys[0] = code;
					twoLeftField.requestFocusInWindow();
				}
				else if(twoLeftField.isFocusOwner())
				{
					twoLeftField.setText(text);
					tempTwoPlayerKeys[1] = code;
					twoRightField.requestFocusInWindow();
				}
				else if(twoRightField.isFocusOwner())
				{
					twoRightField.setText(text);
					tempTwoPlayerKeys[2] = code;
					twoDownField.requestFocusInWindow();
				}
				else if(twoDownField.isFocusOwner())
				{
					twoDownField.setText(text);
					tempTwoPlayerKeys[3] = code;
					twoDropField.requestFocusInWindow();
				}
				else if(twoDropField.isFocusOwner())
				{
					twoDropField.setText(text);
					tempTwoPlayerKeys[4] = code;
					oneUpField.requestFocusInWindow();
				}
			}
		};
		oneUpField.addKeyListener(keyHandler);
		oneLeftField.addKeyListener(keyHandler);
		oneRightField.addKeyListener(keyHandler);
		oneDownField.addKeyListener(keyHandler);
		oneDropField.addKeyListener(keyHandler);
		
		twoUpField.addKeyListener(keyHandler);
		twoLeftField.addKeyListener(keyHandler);
		twoRightField.addKeyListener(keyHandler);
		twoDownField.addKeyListener(keyHandler);
		twoDropField.addKeyListener(keyHandler);
		
		oneUpField.addFocusListener(this);
		oneDownField.addFocusListener(this);
		oneLeftField.addFocusListener(this);
		oneRightField.addFocusListener(this);
		oneDropField.addFocusListener(this);
		
		twoUpField.addFocusListener(this);
		twoDownField.addFocusListener(this);
		twoLeftField.addFocusListener(this);
		twoRightField.addFocusListener(this);
		twoDropField.addFocusListener(this);
		

	}
    
	public void actionPerformed( ActionEvent e) //button actions
	{
			if((JButton)e.getSource() == cancelBtn) // if cancel is clicked
			{
				this.hide();
			}
			else if((JButton)e.getSource() == defaultBtn) //if default is clicked
			{
				//set the keys to the default values
				if(playNum == 1)
				{
					tempOnePlayerKeys[0] = KeyEvent.VK_UP;//up
					oneUpField.setText(KeyEvent.getKeyText(KeyEvent.VK_UP));
					
					tempOnePlayerKeys[1] = KeyEvent.VK_LEFT;
					oneLeftField.setText(KeyEvent.getKeyText(KeyEvent.VK_LEFT));
					
					tempOnePlayerKeys[2] = KeyEvent.VK_RIGHT;
					oneRightField.setText(KeyEvent.getKeyText(KeyEvent.VK_RIGHT));
					
					tempOnePlayerKeys[3] = KeyEvent.VK_DOWN;
					oneDownField.setText(KeyEvent.getKeyText(KeyEvent.VK_DOWN));
					
					tempOnePlayerKeys[4] = KeyEvent.VK_SPACE;
					oneDropField.setText(KeyEvent.getKeyText(KeyEvent.VK_SPACE));
				}
				//set the keys to default values, 2 player option
				else if(playNum == 2)
				{
					tempOnePlayerKeys[0] = KeyEvent.VK_W;//up
					oneUpField.setText(KeyEvent.getKeyText(KeyEvent.VK_W));
					
					tempOnePlayerKeys[1] = KeyEvent.VK_A;//left
					oneLeftField.setText(KeyEvent.getKeyText(KeyEvent.VK_A));
					
					tempOnePlayerKeys[2] = KeyEvent.VK_D;//right
					oneRightField.setText(KeyEvent.getKeyText(KeyEvent.VK_D));
					
					tempOnePlayerKeys[3] = KeyEvent.VK_S;//down
					oneDownField.setText(KeyEvent.getKeyText(KeyEvent.VK_S));
					
					tempOnePlayerKeys[4] = KeyEvent.VK_Q;//drop
					oneDropField.setText(KeyEvent.getKeyText(KeyEvent.VK_Q));
					
					tempTwoPlayerKeys[0] = KeyEvent.VK_UP;
					twoUpField.setText(KeyEvent.getKeyText(KeyEvent.VK_UP));
					
					tempTwoPlayerKeys[1] = KeyEvent.VK_LEFT;
					twoLeftField.setText(KeyEvent.getKeyText(KeyEvent.VK_LEFT));
					
					tempTwoPlayerKeys[2] = KeyEvent.VK_RIGHT;
					twoRightField.setText(KeyEvent.getKeyText(KeyEvent.VK_RIGHT));
					
					tempTwoPlayerKeys[3] = KeyEvent.VK_DOWN;
					twoDownField.setText(KeyEvent.getKeyText(KeyEvent.VK_DOWN));
					
					tempTwoPlayerKeys[4] = KeyEvent.VK_SPACE;
					twoDropField.setText(KeyEvent.getKeyText(KeyEvent.VK_SPACE));
				}
			}
			else if((JButton)e.getSource() == okBtn) //if ok is clicked
			{
				
				if(playNum == 1)
				{
					
					
					//check for repeats within onePlayerKeys
					for(int i= 0; i< keyArraySize; i++)
					{
						int temp = tempOnePlayerKeys[i];
						for( int j=i+1; j<keyArraySize; j++)
						{
							if(temp == tempOnePlayerKeys[j])
							{
								noRepeatKeys = false;
								
							}
						}
					}
					if(noRepeatKeys == true)
					{
						System.out.println("there are no repeat keys");
						errorLabel.setText("");
						//no repeats, change the keys
						for (int i=0; i< keyArraySize; i++)
						{
							onePlayerKeys[i] = tempOnePlayerKeys[i];
						}										

						this.hide();
						
					}
					else
					{
						errorLabel.setText("Error! more than one of the same key is used");
						noRepeatKeys = true;

					}
				}
				//player 2 option
				else if(playNum == 2)
				{
					
					int temp;
					int temp2;
					int j;
					
					//check for repeats within onePlayerKeys, twoPlayer keys, and between both 
					for(int i= 0; i< keyArraySize; i++)
					{
						temp = tempOnePlayerKeys[i];
						temp2 = tempTwoPlayerKeys[i];
						for( j=i+1; j<keyArraySize; j++)
						{
							if(temp == tempOnePlayerKeys[j] || temp2 == tempTwoPlayerKeys[j])
							{
								//System.out.println(" one player similar keys!");
								noRepeatKeys = false;
							}
						}
						for( j =0; j<keyArraySize; j++) //check onePlayerKeys against twoPlayerKeys
						{
							if(temp == tempTwoPlayerKeys[j])
							{
								//System.out.println(" one and two player similar keys!");
								noRepeatKeys = false;
							}
						}

					}
					if(noRepeatKeys == true)
					{
						System.out.println("there are no repeat keys");
						errorLabel.setText("");
						//onePlayerKeys = tempOnePlayerKeys; //no repeats, change the keys
						for (int i=0; i< keyArraySize; i++)
						{
							onePlayerKeys[i] = tempOnePlayerKeys[i];
							twoPlayerKeys[i] = tempTwoPlayerKeys[i];
						}										

						//twoPlayerKeys = tempTwoPlayerKeys;
						this.hide(); //or hide?
						
					}
					else
					{
						errorLabel.setText("error! more than one of the same key is used");
						noRepeatKeys = true;
					}
				}
				
				
			}
			
	}
	public int[] getOnePlayerKeys()
	{
		return onePlayerKeys;
	}
	public int[] getTwoPlayerKeys()
	{
		return twoPlayerKeys;
	}
	public void setOnePlayerKeys(int [] oneKeys)
	{
		oneUpField.setText(KeyEvent.getKeyText(oneKeys[0]));
		oneLeftField.setText(KeyEvent.getKeyText(oneKeys[1]));
		oneRightField.setText(KeyEvent.getKeyText(oneKeys[2]));
		oneDownField.setText(KeyEvent.getKeyText(oneKeys[3]));
		oneDropField.setText(KeyEvent.getKeyText(oneKeys[4]));
		//set array to have the given codes
		for(int i =0; i< keyArraySize; i++)
		{
			onePlayerKeys[i] = oneKeys[i];
		}
	}
	public void setTwoPlayerKeys(int [] twoKeys)
	{
		twoUpField.setText(KeyEvent.getKeyText(twoKeys[0]));
		twoLeftField.setText(KeyEvent.getKeyText(twoKeys[1]));
		twoRightField.setText(KeyEvent.getKeyText(twoKeys[2]));
		twoDownField.setText(KeyEvent.getKeyText(twoKeys[3]));
		twoDropField.setText(KeyEvent.getKeyText(twoKeys[4]));
		//set array to have the given codes
		for(int i =0; i< keyArraySize; i++)
		{
			twoPlayerKeys[i] = twoKeys[i];
		}
	}
	public void focusGained( FocusEvent e )
	{
		if( e.getComponent().equals(oneUpField))
			oneUpField.setBackground(Color.WHITE);
		
		if( e.getComponent().equals(oneDownField))
			oneDownField.setBackground(Color.WHITE);
		
		if( e.getComponent().equals(oneLeftField))
			oneLeftField.setBackground(Color.WHITE);
		
		if( e.getComponent().equals(oneRightField))
			oneRightField.setBackground(Color.WHITE);
		
		if( e.getComponent().equals(oneDropField))
			oneDropField.setBackground(Color.WHITE);
		
		if( e.getComponent().equals(twoUpField))
			twoUpField.setBackground(Color.WHITE);
		
		if( e.getComponent().equals(twoDownField))
			twoDownField.setBackground(Color.WHITE);
		
		if( e.getComponent().equals(twoLeftField))
			twoLeftField.setBackground(Color.WHITE);
		
		if( e.getComponent().equals(twoRightField))
			twoRightField.setBackground(Color.WHITE);
		
		if( e.getComponent().equals(twoDropField))
			twoDownField.setBackground(Color.WHITE);
	}
	
	public void focusLost( FocusEvent e)
	{
		if( e.getComponent().equals(oneUpField))
			oneUpField.setBackground(Color.LIGHT_GRAY);
		
		if( e.getComponent().equals(oneDownField))
			oneDownField.setBackground(Color.LIGHT_GRAY);
		
		if( e.getComponent().equals(oneLeftField))
			oneLeftField.setBackground(Color.LIGHT_GRAY);
		
		if( e.getComponent().equals(oneRightField))
			oneRightField.setBackground(Color.LIGHT_GRAY);
		
		if( e.getComponent().equals(oneDropField))
			oneDropField.setBackground(Color.LIGHT_GRAY);
		
		if( e.getComponent().equals(twoUpField))
			twoUpField.setBackground(Color.LIGHT_GRAY);
		
		if( e.getComponent().equals(twoDownField))
			twoDownField.setBackground(Color.LIGHT_GRAY);
		
		if( e.getComponent().equals(twoLeftField))
			twoLeftField.setBackground(Color.LIGHT_GRAY);
		
		if( e.getComponent().equals(twoRightField))
			twoRightField.setBackground(Color.LIGHT_GRAY);
		
		if( e.getComponent().equals(twoDropField))
			twoDownField.setBackground(Color.LIGHT_GRAY);
	}
	public void mouseClicked( MouseEvent e) {}
	public void mousePressed( MouseEvent e) {}
	public void mouseReleased( MouseEvent e) {}
	public void mouseEntered( MouseEvent e) {}
	public void mouseExited( MouseEvent e) {}
	/* used for testing purposes
	public static void main(String args[])
	{

		//k.setSize(500,500);
		
		int[] oneKeys = new int[] {KeyEvent.VK_A, KeyEvent.VK_B,
								KeyEvent.VK_C, KeyEvent.VK_D, KeyEvent.VK_X};
		int[] twoKeys = new int[] {KeyEvent.VK_E, KeyEvent.VK_F,
								KeyEvent.VK_G, KeyEvent.VK_H, KeyEvent.VK_I};
		KeyConfig k = new KeyConfig(2, oneKeys, twoKeys);
		k.setVisible(true);
		//k.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	*/
}


