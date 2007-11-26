package net.sourceforge.jetris;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.KeyStroke;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.Box;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.border.EtchedBorder;
//import javax.swing.text.html.HTMLDocument.Iterator;
import javax.swing.Timer;
import javax.imageio.ImageIO;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Font;
import java.awt.Image;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.BufferedInputStream;
import java.applet.AudioClip;
import java.applet.Applet;
import res.ResClass;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;


public class TwoPlayerGame extends JFrame  {
     	private static final String NAME = "JETRIS 1.1";
    	private KeyListener keyHandler;
    	private Font font;
    	private HelpDialog helpDialog;
    	private JMenuItem jetrisRestart;
    	private JMenuItem jetrisKeyConfig;
    	private JMenuItem jetrisPause;
    	private JMenuItem jetrisNewGame;
    	private JMenuItem jetrisMusic;
    	private JMenuItem jetrisHiScore;
    	private JMenuItem jetrisExit;
    	private JMenuItem helpAbout;
    	private JMenuItem helpJetris;
 		private JPanel about;
 		private JPanel hiScorePanel;
    	private final Player mf;
    	private final Player mf2;
		private AudioClip[] clip = new AudioClip[3];
    	private int soundcycle = 0;
    	private boolean sound = true;
    	private InteractionThread it;
    	private GameOverThread got;
    	private boolean message;
    	private JButton pauseBut;
		//keys are stored in the order UP, LEFT, RIGHT, DOWN, DROP for 0, 1, 2, 3, 4 in the array
		private int[] onePlayerKeys = new int[] {KeyEvent.VK_W, KeyEvent.VK_A,
												KeyEvent.VK_D, KeyEvent.VK_S, KeyEvent.VK_Q};
		
		private int[] twoPlayerKeys = new int[] {KeyEvent.VK_UP, KeyEvent.VK_LEFT,
												KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN, KeyEvent.VK_SPACE};
		private KeyConfig KC;
		private Timer keyTimer;
		private Map pressedKeys = Collections.synchronizedMap(new HashMap());

                
        // game mode constants
        private final int FTL = 0;
        private final int FTS = 1;
        private final int FXL = 2;
        
        // stores current game mode
        private int gameMode;
        private boolean isDemoing;
	private int gameLimit;
        
        // for random special block generation
        private Random r = new Random();
        boolean helpful;
        

    public class InteractionThread extends Thread {
   
        public void run() {
        	
                if (gameMode == FTL || gameMode == FXL)
                {
                    System.out.println("thread runs");
                    
                    helpful = r.nextBoolean();

                    mf.setPlayerSpeed(mf2.tg.getOpponentSpeed());
                    mf2.setPlayerSpeed(mf.tg.getOpponentSpeed());

                    // this keeps the attacked value in the tetris grid updated
                    mf.tg.playerAttacked(mf2.tg.attackPlayer());
                    mf2.tg.playerAttacked(mf.tg.attackPlayer());
                    
                    // this controls special blocks
                    if (mf.tg.tetrisCleared == true){
                        System.out.println("helpful = " + helpful);
                        
                        if (helpful)
                            mf.helpfulBlock = true;
                        else
                            mf2.harmfulBlock = true;
                        
                        mf.tg.tetrisCleared = false;
                    }
                    if (mf2.tg.tetrisCleared == true){
                        System.out.println("helpful = " + helpful);
                        
                        if (helpful)
                            mf2.helpfulBlock = true;
                        else
                            mf.harmfulBlock = true;
                        
                        mf2.tg.tetrisCleared = false;
                    }
                   
                    if (mf.tg.attacked > 0 || mf.harmfulBlock == true) {
                        mf.playerLabel.setText("Player 1 - ATTACKED!");
                    }
                    else{
                        mf.playerLabel.setText("Player 1");
                    }
                    
                    // this uses the attacked value to add the appropriate lines to the other player's grid                   
                    mf.tg.addLines();
                    
                    if (mf2.tg.attacked > 0 || mf2.harmfulBlock == true){
                        mf2.playerLabel.setText("Player 2 - ATTACKED!");
                    }
                    else{
                        mf2.playerLabel.setText("Player 2");
                    }
                    
                    // this uses the attacked value to add the appropriate lines to the other player's grid 
                    mf2.tg.addLines();
                }
        }
    }
    
    private class GameOverThread extends Thread {
   
        public void run() {
        		if(mf.getGameOver()) {
        			mf2.isGameOver = mf.isGameOver;
				
        			if(!message){
					message = true;
					if (mf.isWinner){
						JOptionPane.showMessageDialog(null,"                          Player 1 wins" ,"WINNER", JOptionPane.PLAIN_MESSAGE);
					}
					else{
						JOptionPane.showMessageDialog(null,"                          Player 2 wins" ,"WINNER", JOptionPane.PLAIN_MESSAGE);
					}
				}
        		} else if(mf2.getGameOver()){
        			mf.isGameOver = mf2.isGameOver;
        			if(!message){
					message = true;
					if (mf2.isWinner){
						JOptionPane.showMessageDialog(null,"                          Player 2 wins" ,"WINNER", JOptionPane.PLAIN_MESSAGE);
					}
					else{
						JOptionPane.showMessageDialog(null,"                          Player 1 wins" ,"WINNER", JOptionPane.PLAIN_MESSAGE);
					}
				}
			}
	}
   		
   }    
    
    public TwoPlayerGame(int mode, boolean demo, int limit) {
        super(NAME);
   		initMenu();
   		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   		it = new InteractionThread();
   		got = new GameOverThread();

   		isDemoing = demo;
   		
   		mf = new Player(1,it, got, isDemoing, mode, limit);
        mf2 = new Player(2,it, got, isDemoing, mode, limit);

        gameMode = mode;
	gameLimit = limit;
	
        this.getContentPane().add(mf, BorderLayout.WEST);
        this.getContentPane().add(mf2, BorderLayout.EAST);
        this.getContentPane().add(getButtonPanel(), BorderLayout.CENTER);
        this.getContentPane().add(getCopyrightPanel(), BorderLayout.SOUTH);
   		pack();
   		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - getWidth() / 2, screenSize.height / 2 - getHeight() / 2);
   		setVisible(true);
   		this.setResizable(false);
                            


        clip[0] = Applet.newAudioClip(new ResClass().getClass().getResource("Tetrisa.mid"));
        clip[1] = Applet.newAudioClip(new ResClass().getClass().getResource("Tetrisb.mid"));
        clip[2] = Applet.newAudioClip(new ResClass().getClass().getResource("Tetrisc.mid"));  	            
		
        addWindowFocusListener(new WindowFocusListener(){

            public void windowGainedFocus(WindowEvent arg0) {}

            public void windowLostFocus(WindowEvent arg0) {
            	if (!mf.isPaused())
            		mf.pause();
            	if (!mf2.isPaused())
            		mf2.pause();
            }
        });

        // Would use Enums, but not allowed to instantiate them in a local context
        // These are fixed (final) objects and can be compared by reference 
        final Object newKey = new Object();
        final Object lagKey = new Object();
        final Object lagKeyAgain = new Object();
        final Object freeKey = new Object();
        
        keyHandler = new KeyAdapter(){
            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == twoPlayerKeys[4])
                    mf2.moveDrop();
                else if (code == onePlayerKeys[4])
                    mf.moveDrop();
                else
                	pressedKeys.put(new Integer(code), newKey);
            }
            public void keyReleased(KeyEvent e) {
                int code = e.getKeyCode();
                pressedKeys.remove(new Integer(code));
            }	
        };
        
        keyTimer = new Timer(80, new ActionListener() {
        	private Map changes = new HashMap();
        	public void actionPerformed(ActionEvent e) {
    			//keys in arrays are stored in the order UP LEFT RIGHT DOWN DROP
				//0	1	2	 3	    4
        		Iterator i = pressedKeys.keySet().iterator();
        		while (i.hasNext()) {
        			Integer key = (Integer) i.next();
        			int code = key.intValue();
        			if (pressedKeys.get(key) == lagKey) {
        				changes.put(key, lagKeyAgain);
        				continue;
        			}
        			else if (pressedKeys.get(key) == lagKeyAgain) {
        				changes.put(key, freeKey);
        				continue;
        			}
        			if (code == twoPlayerKeys[1])
                        mf2.moveLeft();
                    else if (code == twoPlayerKeys[2])
                        mf2.moveRight();
                    else if (code == twoPlayerKeys[3])
                    	mf2.moveDown();
                    else if (code == twoPlayerKeys[0])
                        mf2.rotation();
                    else if (code == onePlayerKeys[1])
                        mf.moveLeft();
                    else if (code == onePlayerKeys[2])
                        mf.moveRight();
                    else if (code == onePlayerKeys[3])
                        mf.moveDown();
                    else if (code == onePlayerKeys[0])
                        mf.rotation();
        			if (pressedKeys.get(key) == newKey)
        				changes.put(key, lagKey);
        	    }
        		i = changes.keySet().iterator();
        		while (i.hasNext()) {
        			Object key = i.next();
        			pressedKeys.put(key, changes.get(key));
        		}
        		changes.clear();
        	}
        });
        if (!isDemoing) {
            addKeyListener(keyHandler);
        	keyTimer.start();
        }
        
        pause();
        
    }

    public JPanel getButtonPanel() {
        JPanel r = new JPanel();
        BoxLayout rL = new BoxLayout(r,BoxLayout.Y_AXIS);
        r.setLayout(rL);
        Dimension ra = new Dimension(5, 0);
        Dimension d = new Dimension(4*18, 4*18);

        JPanel jp = new JPanel();

        //BUTTONS


		//JetrisIcon

		r.add(Box.createRigidArea(new Dimension(0, 41)));
		
		jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(Box.createRigidArea(new Dimension(21, 0)));
        JLabel label = new JLabel();
        Icon icon = new ImageIcon(loadImage("jetris.png"));
        label.setIcon(icon);
        jp.add(label);
        jp.add(Box.createHorizontalGlue());
        r.add(jp);
        
       r.add(Box.createRigidArea(new Dimension(0, 158)));
				
				//Display game mode
				
				jp = new JPanel();
				jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));

				/*
				String gameModeString = new String("Demo Mode");
					JLabel gameModeLabel = new JLabel(gameModeString);
					jp.add(gameModeLabel);
					jp.add(Box.createHorizontalGlue());
					r.add(jp);
				*/
				
				
				String gameModeString = new String("");
				
				if (isDemoing){
					gameModeString = new String("     Demo Mode");
				} else if (gameMode == 0) {
					gameModeString = new String("    First To Lose");
				} else if (gameMode == 1) {
					gameModeString = new String("First To "+gameLimit+" Points");
				} else if (gameMode == 2) {
					gameModeString = new String("First To "+gameLimit+" Lines");
				}
				
				JLabel gameModeLabel = new JLabel(gameModeString);
					jp.add(gameModeLabel);
					jp.add(Box.createHorizontalGlue());
					r.add(jp);
				
				
				
				
				
				/*
				//Display Winning Parameter
				jp = new JPanel();
				jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
				jp.add(Box.createRigidArea(new Dimension(31, 0)));
				String gameModeParameterString = ""+gameLimit;
				JLabel gameModeParameterLabel = new JLabel(gameModeParameterString);
				jp.add(gameModeParameterLabel);
				jp.add(Box.createHorizontalGlue());
        r.add(jp);
				*/
				
			
				
				r.add(Box.createRigidArea(new Dimension(0, 160)));
        
        //Restart Button
        
        jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(Box.createRigidArea(ra));
        JButton restartBut = new JButton("Restart");
        restartBut.setToolTipText("Press 'R'");
        restartBut.setFocusable(false);
        restartBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                	restart();
            }
        });
        d = new Dimension(90, 30);
        restartBut.setMinimumSize(d);
        restartBut.setPreferredSize(d);
        restartBut.setMaximumSize(d);
        jp.add(restartBut);
        jp.add(Box.createHorizontalGlue());
        r.add(jp);
        
        r.add(Box.createRigidArea(new Dimension(0, 5)));
        
        
        
        //Pause Button
        jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(Box.createRigidArea(ra));
        pauseBut = new JButton("Pause");
        pauseBut.setToolTipText("Press 'P'");
        pauseBut.setFocusable(false);
        pauseBut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                pause();
            }
        });
        pauseBut.setMinimumSize(d);
        pauseBut.setPreferredSize(d);
        pauseBut.setMaximumSize(d);
        jp.add(pauseBut);
        jp.add(Box.createHorizontalGlue());
        r.add(jp);
        
        
        
        

        /*
        r.add(Box.createRigidArea(new Dimension(0, 5)));   
        
        
        //PlayerInteraction
        jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));
        //jp.add(Box.createRigidArea(new Dimension(1, 0)));
        JLabel linelabel = new JLabel("Player 1 lines");
        JLabel lines = new JLabel("0");
        JLabel linelabel2 = new JLabel("Player 2 lines");
        JLabel lines2 = new JLabel("0");        
        
        jp.add(linelabel);
        jp.add(lines);
        jp.add(linelabel2);
        jp.add(lines2);

        jp.add(Box.createHorizontalGlue());
        r.add(jp);*/
        return r;
    }

    private void initMenu() {
        
        MenuHandler mH = new MenuHandler();
        
        JMenuBar menu = new JMenuBar(); 
        setJMenuBar(menu);
        
        JMenu mJetris = new JMenu();
        menu.add(mJetris);
        mJetris.setText("Jetris");
        mJetris.setMnemonic('J');
        {
            jetrisNewGame = new JMenuItem("New Game");
            mJetris.add(jetrisNewGame);
            setKeyAcceleratorMenu(jetrisNewGame, 'N',0);
            jetrisNewGame.addActionListener(mH);
            jetrisNewGame.setMnemonic('N');

            jetrisKeyConfig = new JMenuItem("Key Config");
            mJetris.add(jetrisKeyConfig);
            setKeyAcceleratorMenu(jetrisKeyConfig, 'K', 0);
            jetrisKeyConfig.addActionListener(mH);
            jetrisKeyConfig.setMnemonic('K');
            
            jetrisRestart = new JMenuItem("Restart");
            mJetris.add(jetrisRestart);
            setKeyAcceleratorMenu(jetrisRestart, 'R',0);
            jetrisRestart.addActionListener(mH);
            jetrisRestart.setMnemonic('R');
            
            jetrisPause = new JMenuItem("Pause");
            mJetris.add(jetrisPause);
            setKeyAcceleratorMenu(jetrisPause, 'P',0);
            jetrisPause.addActionListener(mH);
            jetrisPause.setMnemonic('P');
            
            jetrisMusic = new JMenuItem("Music ON/OFF");
            mJetris.add(jetrisMusic);
            setKeyAcceleratorMenu(jetrisMusic, 'M',0);
            jetrisMusic.addActionListener(mH);
            jetrisMusic.setMnemonic('M');
            
            mJetris.addSeparator();
            
            jetrisHiScore = new JMenuItem("HiScore...");
            mJetris.add(jetrisHiScore);
            setKeyAcceleratorMenu(jetrisHiScore, 'H',0);
            jetrisHiScore.addActionListener(mH);
            jetrisHiScore.setMnemonic('H');
            
            mJetris.addSeparator();
            
            jetrisExit = new JMenuItem("Exit");
            mJetris.add(jetrisExit);
            setKeyAcceleratorMenu(jetrisExit, KeyEvent.VK_ESCAPE, 0);
            jetrisExit.addActionListener(mH);
            jetrisExit.setMnemonic('X');
        }
        JMenu mHelp = new JMenu();
        menu.add(mHelp);
        mHelp.setText("Help");
        mHelp.setMnemonic('H');
        {
            helpJetris = new JMenuItem("Jetris Help");
            mHelp.add(helpJetris);
            setKeyAcceleratorMenu(helpJetris, KeyEvent.VK_F1 ,0);
            helpJetris.addActionListener(mH);
            helpJetris.setMnemonic('J');
            
            helpAbout = new JMenuItem("About");
            mHelp.add(helpAbout);
            helpAbout.addActionListener(mH);
            helpAbout.setMnemonic('A');
        }
    }
    
    private void setKeyAcceleratorMenu(JMenuItem mi, int keyCode, int mask) {
        KeyStroke ks = KeyStroke.getKeyStroke(keyCode, mask);
        mi.setAccelerator(ks);
    }
    
    private void doAbout() {
        if(about == null) setAboutPanel();
        JOptionPane.showMessageDialog(this,about,"ABOUT", 
                JOptionPane.PLAIN_MESSAGE, 
                new ImageIcon(loadImage("jetris.png")));
    }
    
    private void setAboutPanel() {
        about = new JPanel();
        about.setLayout(new BoxLayout(about, BoxLayout.Y_AXIS));
        JLabel jl = new JLabel("<HTML><B>"+NAME+"</B> Copyright (c) 2006 Nikolay G. Georgiev</HTML>");
        jl.setFont(font);
        about.add(jl);
        about.add(Box.createVerticalStrut(10));
        
        jl = new JLabel("WEB PAGE:");
        jl.setFont(font);
        about.add(jl);
        HTMLLink hl = new HTMLLink("http://jetris.sf.net", false);
        hl.setFont(font);
        about.add(hl);
        
        about.add(Box.createVerticalStrut(20));
        
        jl = new JLabel("<HTML>This program is released under the Mozilla Public License 1.1 .<BR> A copy of this is included with your copy of JETRIS<BR>and can also be found at:</HTML>");
        jl.setFont(font);
        about.add(jl);
        about.add(jl);
        hl = new HTMLLink("http://www.opensource.org/licenses/mozilla1.1.php", false);
        hl.setFont(font);
        about.add(hl);
    }

    static Image loadImage(String imageName) {
        try {
            Image im = ImageIO.read(new BufferedInputStream(
                    new ResClass().getClass().getResourceAsStream(imageName)));
            return im;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return null;
        }
    }

    private void doHelp() {
        if(helpDialog == null) helpDialog = new HelpDialog(this);
        helpDialog.show();
    }

    private void showHiScore() {
        setHiScorePanel();
        
        JOptionPane.showMessageDialog(this,hiScorePanel,"HI SCORE", 
                JOptionPane.PLAIN_MESSAGE, 
                new ImageIcon(loadImage("jetris32x32.png")));
        
        hiScorePanel = null;
    }

    private void setHiScorePanel() {
        hiScorePanel = new JPanel(new BorderLayout());
        
        String[] colNames = {"Place", "Points", "Lines", "Name"};
        String[][] data = new String[mf.tg.hiScore.length+1][colNames.length];
        data[0] = colNames;
        for (int i = 0; i < mf.tg.hiScore.length; i++) {
            data[i+1] = new String[colNames.length];
            data[i+1][0] = (i+1)+".";
            data[i+1][1] = (""+mf.tg.hiScore[i].score);
            data[i+1][2] = (""+mf.tg.hiScore[i].lines);
            data[i+1][3] = (""+mf.tg.hiScore[i].name);
        }
        
        JTable table = new JTable(data, colNames);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setBackground(new Color(230,255,255));
        table.setEnabled(false);
        
        hiScorePanel.add(table,BorderLayout.CENTER);
        JButton jb = new JButton("Publish HiScore Online");
        //jb.addActionListener(pH);
        
        hiScorePanel.add(jb, BorderLayout.SOUTH);
    }
    
    private synchronized void pause() {
        mf2.pause();
        mf.pause();
        pressedKeys.clear();
        if(mf.isPaused())
        	pauseBut.setText("Unpause");
        else
        	pauseBut.setText("Pause");
        if(!sound) return;
        if(mf.isPaused()) {
			clip[soundcycle].stop();
			soundcycle++;
			if(soundcycle > 2)
				soundcycle = 0;
		} else {
			clip[soundcycle].loop();
		}
    }

    public void restart() {
    	if (/*mf.isGameOver || mf2.isGameOver || */JOptionPane.showConfirmDialog(null, "Are you sure you want to restart the game?", "Restart Game", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
    		mf.restart();
        	mf2.restart();
        	message = false;
        	pause();
    	}
    }

	private void sound() {
		if(mf.isPaused()) return;
		if(sound){
			clip[soundcycle].stop();
			soundcycle++;
			if(soundcycle > 2)
				soundcycle = 0;
		} else
			clip[soundcycle].loop();	
		sound = !sound;
		
	}
    
    private JPanel getCopyrightPanel() {
        JPanel r = new JPanel(new BorderLayout());
        BoxLayout rL = new BoxLayout(r,BoxLayout.X_AXIS);
        r.setLayout(rL);
        r.setBorder(new EtchedBorder());
        r.add(Box.createRigidArea(new Dimension(32,0)));
        
        JLabel jL = new JLabel("Copyright (c) 2006 Nikolay G. Georgiev ");
        jL.setFont(font);
        HTMLLink email = new HTMLLink("ngg@users.sourceforge.net", true);
        email.setFont(font);
        
        r.add(jL);
        r.add(email);
        
        return r;
    }

    private class MenuHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            try {
                JMenuItem tmp = (JMenuItem) e.getSource();
                if (tmp == jetrisRestart) {
                    restart();
                } else if (tmp == jetrisPause) {
                    pause();
                } else if (tmp == jetrisNewGame) {
                	if(JOptionPane.showConfirmDialog(null,"Are you sure you wish to quit this game?", "New Game", JOptionPane.YES_NO_OPTION) == 0){
                		clip[soundcycle].stop();
                		//OnePlayerGame mf = new OnePlayerGame();
						new NewGame();
						mf.stopTimeThread();
                		mf2.stopTimeThread();
						dispose();
					}
                } else if (tmp == jetrisMusic) {
                	sound();               	
                } else if (tmp == jetrisHiScore) {
                    showHiScore();
                } else if (tmp == jetrisExit) {
                    System.exit(0);
                } else if (tmp == helpJetris) {
                    doHelp();
                } else if (tmp == helpAbout) {
                    doAbout();
                } else if(tmp == jetrisKeyConfig){
					KC= new KeyConfig(2, onePlayerKeys, twoPlayerKeys); //paramaters are number of players, int array 1, int array 2
                }
            } catch (Exception exc) {
                exc.printStackTrace(System.out);
            }
        }
    }
}