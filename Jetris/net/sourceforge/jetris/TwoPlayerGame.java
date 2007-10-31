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
		private int gameType;
		private final int LOSE = 0;
		private final int SCORE = 1;
		private final int LINES = 2;
    
    public TwoPlayerGame(int gameType) {

        super(NAME);
        this.gameType = gameType;
   		initMenu();
   		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mf = new Player(1, this);
        mf2 = new Player(2, this);
        this.getContentPane().add(mf, BorderLayout.WEST);
        this.getContentPane().add(mf2, BorderLayout.EAST);
        this.getContentPane().add(getCopyrightPanel(), BorderLayout.SOUTH);
   		pack();
   		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - getWidth() / 2, screenSize.height / 2 - getHeight() / 2);
   		setVisible(true);
   		this.setResizable(false);
        //clip[0] = Applet.newAudioClip(getClass().getResource("Tetrisa.mid"));
        //clip[1] = Applet.newAudioClip(getClass().getResource("Tetrisb.mid"));
        //clip[2] = Applet.newAudioClip(getClass().getResource("Tetrisc.mid"));             
        //clip[0].loop();
		
        addWindowFocusListener(new WindowFocusListener(){

            public void windowGainedFocus(WindowEvent arg0) {}

            public void windowLostFocus(WindowEvent arg0) {
                mf.isPause = true;
                mf2.isPause = true;
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
                    mf.playerspeed = mf2.tg.opponentspeed;
                    mf.attacked += mf2.tg.attack;
                    mf2.tg.attack = 0;
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
                    mf2.playerspeed = mf.tg.opponentspeed;
                    mf2.attacked += mf.tg.attack;
                    mf.tg.attack = 0;
                } else if(code == KeyEvent.VK_Q) {
                    mf.moveDrop();
                }
            }
        };
        addKeyListener(keyHandler);
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
            jetrisMusic.setMnemonic('S');
            
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
    	if(mf.isPause != mf2.isPause){
    		mf.isPause = true;
    		mf2.isPause = true;
    	} else {
        	mf.isPause = !mf.isPause;
        	mf2.isPause = !mf2.isPause;
    	}
    }

    private void restart() {
        mf.restart();
        mf2.restart();
    }

	private void sound() {
		if(sound){
			//clip[soundcycle].stop();
			soundcycle++;
			if(soundcycle > 2)
				soundcycle = 0;
		} else
			//clip[soundcycle].loop();	
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