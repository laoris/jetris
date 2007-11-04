package net.sourceforge.jetris;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.KeyStroke;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.Box;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.border.EtchedBorder;
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

public class TwoPlayerGame extends JFrame  {
     	private static final String NAME = "JETRIS 1.1";
    	private KeyListener keyHandler;
    	private Font font;
    	private HelpDialog helpDialog;
    	private JMenuItem jetrisRestart;
    	private JMenuItem jetrisPause;
    	private JMenuItem jetrisGame;
    	private JMenuItem jetrisMusic;
    	private JMenuItem jetrisHiScore;
    	private JMenuItem jetrisExit;
    	private JMenuItem helpAbout;
    	private JMenuItem helpJetris;
    	private JMenuItem jetrisGM1;
    	private JMenuItem jetrisGM2;
    	private JMenuItem jetrisGM3;	
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
    	private TwoPlayerGame frame;

    private class InteractionThread extends Thread {
   
        public void run() {
        	
        	mf.setPlayerSpeed(mf2.tg.getOpponentSpeed());
        	mf2.setPlayerSpeed(mf.tg.getOpponentSpeed());
        	mf.tg.playerAttacked(mf2.tg.attackPlayer());
        	mf2.tg.playerAttacked(mf.tg.attackPlayer());
        }
    }
    
    private class GameOverThread extends Thread {
   
        public void run() {
        		if(mf.getGameOver()) {
        			mf2.isGameOver = mf.isGameOver;
        			if(!message){
        				message = true;
        				JOptionPane.showMessageDialog(frame,"                          Player 2 wins" ,"WINNER", JOptionPane.PLAIN_MESSAGE);
        			}
        		} else if(mf2.getGameOver()){
        			mf.isGameOver = mf2.isGameOver;
        			if(!message){
        				message = true;
        				JOptionPane.showMessageDialog(frame,"                          Player 1 wins" ,"WINNER", JOptionPane.PLAIN_MESSAGE);
        			}
        		}
   		}
   		
    }    
    
    public TwoPlayerGame() {
        super(NAME);
   		initMenu();
   		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   		it = new InteractionThread();
   		got = new GameOverThread();
   		frame = this;
        mf = new Player(1,it, got);
        mf2 = new Player(2,it, got);


        this.getContentPane().add(mf, BorderLayout.WEST);
        this.getContentPane().add(mf2, BorderLayout.EAST);
        this.getContentPane().add(getButtonPanel(), BorderLayout.CENTER);
        this.getContentPane().add(getCopyrightPanel(), BorderLayout.SOUTH);
   		pack();
   		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - getWidth() / 2, screenSize.height / 2 - getHeight() / 2);
   		setVisible(true);
   		this.setResizable(false);
        clip[0] = Applet.newAudioClip(getClass().getResource("Tetrisa.mid"));
        clip[1] = Applet.newAudioClip(getClass().getResource("Tetrisb.mid"));
        clip[2] = Applet.newAudioClip(getClass().getResource("Tetrisc.mid"));             
        clip[0].loop();
		
        addWindowFocusListener(new WindowFocusListener(){

            public void windowGainedFocus(WindowEvent arg0) {}

            public void windowLostFocus(WindowEvent arg0) {
                mf.setPaused();
                mf2.setPaused();
            }
        });

        keyHandler = new KeyAdapter(){

            public void keyPressed(KeyEvent e) {
                int code = e.getKeyCode();
                if(code == KeyEvent.VK_LEFT) {
                    mf2.moveLeft();
                } else if(code == KeyEvent.VK_RIGHT) {
                    mf2.moveRight();
                } else if(code == KeyEvent.VK_DOWN) {
                    mf2.moveDown();
                } else if(code == KeyEvent.VK_UP) {
                    mf2.rotation();
                } else if(code == KeyEvent.VK_SPACE) {
                    mf2.moveDrop();
                } else if(code == KeyEvent.VK_A) {
                    mf.moveLeft();
                } else if(code == KeyEvent.VK_D) {
                    mf.moveRight();
                } else if(code == KeyEvent.VK_S) {
                    mf.moveDown();
                } else if(code == KeyEvent.VK_W) {
                    mf.rotation();
                } else if(code == KeyEvent.VK_Q) {
                    mf.moveDrop();
                }
            }
        };
        addKeyListener(keyHandler);

    }
    
    public JPanel getButtonPanel() {
        JPanel r = new JPanel();
        BoxLayout rL = new BoxLayout(r,BoxLayout.Y_AXIS);
        r.setLayout(rL);
        Dimension ra = new Dimension(5, 0);
        Dimension d = new Dimension(4*18, 4*18);

        JPanel jp = new JPanel();

        //BUTTONS
        r.add(Box.createRigidArea(new Dimension(0, 10)));
        
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
        
        jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(Box.createRigidArea(ra));
        JButton pauseBut = new JButton("Pause");
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
            jetrisGame = new JMenuItem("1 Player Game");
            mJetris.add(jetrisGame);
            setKeyAcceleratorMenu(jetrisGame, 'G',0);
            jetrisGame.addActionListener(mH);
            jetrisGame.setMnemonic('G');

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
        
        JMenu mGameMode = new JMenu();
        menu.add(mGameMode);
        mGameMode.setText("Game Mode");
        mGameMode.setMnemonic('G');
        {
            jetrisGM1 = new JMenuItem("Blood Sacrifice");
            mGameMode.add(jetrisGM1);
            setKeyAcceleratorMenu(jetrisGM1, KeyEvent.VK_F5,0);
            jetrisGM1.addActionListener(mH);
            jetrisGM1.setMnemonic('J');
            
            jetrisGM2 = new JMenuItem("Orgy of Gore");
            mGameMode.add(jetrisGM2);
            setKeyAcceleratorMenu(jetrisGM2, KeyEvent.VK_F6,0);
            jetrisGM2.addActionListener(mH);
            jetrisGM2.setMnemonic('J');
            
            jetrisGM3 = new JMenuItem("Entrails Eater");
            mGameMode.add(jetrisGM3);
            setKeyAcceleratorMenu(jetrisGM3, KeyEvent.VK_F7,0);
            jetrisGM3.addActionListener(mH);
            jetrisGM3.setMnemonic('J');
            	        	
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
    	if(mf.isPaused() != mf2.isPaused()){
    		mf.setPaused();
    		mf2.setPaused();
    	} else {
        	mf.pause();
        	mf2.pause();
    	}
    }

    public void restart() {
        mf.restart();
        mf2.restart();
        message = false;
    }

	private void sound() {
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
                } else if (tmp == jetrisGame) {
                	if(JOptionPane.showConfirmDialog(frame,"Are you sure?", "Uhh", JOptionPane.YES_NO_OPTION) == 0){
                		setVisible(false);
                		clip[soundcycle].stop();
                		//clip.dispose();
                		mf.stopTimeThread();
                		mf.dispose();
                		mf2.stopTimeThread();
                		OnePlayerGame mf = new OnePlayerGame();
                		dispose();
                	}
                } else if (tmp == jetrisMusic) {
                	sound();               	
                } else if (tmp == jetrisHiScore) {
                    //showHiScore();
                } else if (tmp == jetrisExit) {
                    System.exit(0);
                } else if (tmp == helpJetris) {
                    //doHelp();
                } else if (tmp == helpAbout) {
                    //doAbout();
                }
            } catch (Exception exc) {
                exc.printStackTrace(System.out);
            }
        }
    }
}