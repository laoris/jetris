package net.sourceforge.jetris;

import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.imageio.ImageIO;
import java.awt.Font;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import res.ResClass;
import java.applet.AudioClip;
import java.applet.Applet;

public class Player extends JPanel  {
	
    private static final int CELL_H = 24;
    private Font font;
    private JPanel playPanel;
    private JLabel score;
    private JLabel lines;
	private JLabel time;
    private JLabel[] statsF;
    private JLabel[] statsL;
    private JLabel levelLabel;
    private JLabel hiScoreLabel;
    private JLabel speedLabel;
    public JLabel playerLabel;
    private JPanel[][] cells;
    private JPanel[][] next;
    private int nextX;
    private int nextY;
    private Figure f;
    private Figure fNext;
    private FigureFactory ff;
    private boolean isNewFigureDroped;
    boolean isGameOver;
    private Color nextBg;
    private TimeThread tt;
    private int player;
    private JPanel hiScorePanel;
    private AudioClip gameover;
    protected TetrisGrid tg;
    private boolean isPause;
    private int playerspeed = 0;
    //private int difference = 0;
    private Thread got;
    private JButton pauseBut; 
    private boolean isOnePlayer;
    private boolean isDemoing;
    private DemoMode demoThread;
    private int pGameMode;
    private int pLimit;
    boolean isWinner;
    protected int speed;
    
    // special block variables
    boolean harmfulBlock;
    boolean helpfulBlock;
    
    // used to access the overall game frame
    
    private class GridThread extends Thread {
        
        private int count = 0;
   
        public void run() {
            try {
                while (true) {
                    if (isGameOver || isPause) {
                        Thread.sleep(50);
                        got.run();
                    } else {
                        if(isNewFigureDroped) {
                            isNewFigureDroped = false;
                            //System.out.println("Player " + player + " Fallrate = " + (1100-difference*50-50*tg.getLevel()) + " milliseconds");
                            count = 0;
                            nextMove();
                            continue;
                        } else {
                            Thread.sleep(50);
                        }
                        count += 50;
                        if(count + 50*speed + 50*tg.getLevel() >= 1100) {
                            count = 0;
                            nextY++;
                            nextMove();
                        }
                    } 
                }
            } catch (Exception e) {
                e.printStackTrace();
            } 
        }
    }
    
    public class TimeThread extends Thread {
        
        private int hours;
        private int min;
        private int sec;
        
        private int count;
        
        private void incSec() {
            sec++;
            if(sec == 60) {
                sec = 0;
                min++;
            }
            if(min == 60) {
                min = 0;
                hours++;
            } 
        }
        
        public void resetTime() {
            hours = min = sec = 0;
        }
        
        public void run() {
            try {
                while (true) {
                    Thread.sleep(50);
                    if (isGameOver) {
                        Graphics g = playPanel.getGraphics();
                        Font font = new Font(g.getFont().getFontName(), Font.BOLD, 30);
                        g.setFont(font);
						g.setColor(Color.BLACK);
                        g.drawString("GAME OVER", 33, 250);

                    } else if(isPause) {
						Graphics g = playPanel.getGraphics();
                        Font font = new Font(g.getFont().getFontName(), Font.BOLD, 30);
                        g.setFont(font);
						g.setColor(Color.BLACK);
                        g.drawString("PAUSED", 47, 250);
                    } else if(count >= 1000) {
                        count = 0;
                        incSec();
                        time.setText(this.toString());
                    } else {
                        count+=50;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } 
        }
        
        public String toString() {
            StringBuffer sb = new StringBuffer();
            if(hours < 10) {
                sb.append('0');
            }
            sb.append(hours);
            
            sb.append(':');
            
            if(min < 10) {
                sb.append('0');
            }
            sb.append(min);
            
            sb.append(':');
            
            if(sec < 10) {
                sb.append('0');
            }
            sb.append(sec);
            
            return sb.toString();
        }
    }
    
    public Player(int player, Thread it, Thread got, boolean demoMode, int mode, int limit) {
        this.player = player;
        this.got = got;
        this.isDemoing = demoMode;
	pGameMode = mode;
        speed = 0;
        helpfulBlock = false;
        harmfulBlock = false;
	pLimit = limit;
	isWinner = false;
        font = new Font("Dialog", Font.PLAIN, 12);        
        gameover = Applet.newAudioClip(new ResClass().getClass().getResource("Tetrisgo.mid"));
        tg = new TetrisGrid(it);
        ff = new FigureFactory();
        nextBg = new Color(238,238,238);
        JPanel all = new JPanel(new BorderLayout());
        if(player != 0){
        	JPanel labelPanel = new JPanel();
        	playerLabel = new JLabel("Player " + player);
        	playerLabel.setFont(new Font("SansSerif",Font.BOLD,20));
        	labelPanel.add(playerLabel, BorderLayout.CENTER);
        	all.add(labelPanel, BorderLayout.NORTH);
        }
        else
        	isOnePlayer = true;
        all.add(getStatPanel(), BorderLayout.WEST);
        all.add(getPlayPanel(), BorderLayout.CENTER);
        all.add(getMenuPanel(), BorderLayout.EAST);
        add(all, BorderLayout.CENTER);

        if (isDemoing) {
        	demoThread = new DemoMode(this);
        	fNext = demoThread.getNextFigure();
        	demoThread.start();
        }
        // normal mode
        else {
        	fNext = ff.getRandomFigure();
        }
        dropNext();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - getWidth() / 2, screenSize.height / 2 - getHeight() / 2);
        setVisible(true);

        GridThread gt = new GridThread();
        tt = new TimeThread();
        gt.start();
        tt.start();
    }

    public int getSpeed(){
        return speed;
    }
    
    private JPanel getPlayPanel() {
        playPanel = new JPanel();
        playPanel.setLayout(new GridLayout(20,10));
        playPanel.setPreferredSize(new Dimension(10*CELL_H, 20*CELL_H));

        cells = new JPanel[20][10];
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                cells[i][j] = new JPanel();
                cells[i][j].setBackground(Color.WHITE);
                cells[i][j].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                playPanel.add(cells[i][j]);
            }
        }
        return playPanel;
    }
    
    public JPanel getMenuPanel() {
        JPanel r = new JPanel();
        BoxLayout rL = new BoxLayout(r,BoxLayout.Y_AXIS);
        r.setLayout(rL);
        r.setBorder(new EtchedBorder());
        Dimension ra = new Dimension(5, 0);
        next = new JPanel[4][4];
        JPanel nextP = new JPanel();
        nextP.setLayout(new GridLayout(4,4));
        Dimension d = new Dimension(4*18, 4*18);
        nextP.setMinimumSize(d);
        nextP.setPreferredSize(d);
        nextP.setMaximumSize(d);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                next[i][j] = new JPanel();
                nextP.add(next[i][j]);
            }
        }
        
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(Box.createRigidArea(ra));
        jp.add(new JLabel("NEXT:"));
        jp.add(Box.createHorizontalGlue());
        r.add(jp);
        r.add(nextP);
        
        r.add(Box.createRigidArea(new Dimension(100, 10)));
        
        jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(Box.createRigidArea(ra));
        jp.add(new JLabel("HI-SCORE:"));
        jp.add(Box.createHorizontalGlue());
        r.add(jp);
        
        hiScoreLabel = new JLabel(""+tg.hiScore[0].score);
        hiScoreLabel.setForeground(Color.RED);
        
        
        jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(Box.createRigidArea(ra));
        jp.add(hiScoreLabel);
        jp.add(Box.createHorizontalGlue());
        r.add(jp);
        
        r.add(Box.createVerticalStrut(5));
        
        jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(Box.createRigidArea(ra));
        jp.add(new JLabel("SCORE:"));
        jp.add(Box.createHorizontalGlue());
        r.add(jp);
        
        score = new JLabel("0");
        score.setForeground(Color.BLUE);
        
        jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(Box.createRigidArea(ra));
        jp.add(score);
        jp.add(Box.createHorizontalGlue());
        r.add(jp);
        
        jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(Box.createRigidArea(ra));
        jp.add(new JLabel("LINES:"));
        jp.add(Box.createHorizontalGlue());
        r.add(jp);
        
        lines = new JLabel("0");
        lines.setForeground(Color.BLUE);
        
        jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(Box.createRigidArea(ra));
        jp.add(lines);
        jp.add(Box.createHorizontalGlue());
        r.add(jp);
        
        jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(Box.createRigidArea(ra));
        jp.add(new JLabel("LEVEL:"));
        jp.add(Box.createHorizontalGlue());
        r.add(jp);
        
        levelLabel = new JLabel("1");
        levelLabel.setForeground(Color.BLUE);
        
        jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(Box.createRigidArea(ra));
        jp.add(levelLabel);
        jp.add(Box.createHorizontalGlue());
        r.add(jp);
        
        // speed statistic:
        jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(Box.createRigidArea(ra));
        jp.add(new JLabel("SPEED:"));
        jp.add(Box.createHorizontalGlue());
        r.add(jp);
        
        speedLabel = new JLabel("1");
        speedLabel.setForeground(Color.BLUE);
        
        jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(Box.createRigidArea(ra));
        jp.add(speedLabel);
        jp.add(Box.createHorizontalGlue());
        r.add(jp);
        
        jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(Box.createRigidArea(ra));
        jp.add(new JLabel("TIME:"));
        jp.add(Box.createHorizontalGlue());
        r.add(jp);
        
        time = new JLabel("00:00:00");
        time.setForeground(Color.BLUE);
        
        jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(Box.createRigidArea(ra));
        jp.add(time);
        jp.add(Box.createHorizontalGlue());
        r.add(jp);
        
        r.add(Box.createVerticalGlue());
        
        if(player == 1){
        	r.add(addHelpPanel("A - Left"));
        	r.add(addHelpPanel("D - Right"));
        	r.add(addHelpPanel("W - Rotate"));
        	r.add(addHelpPanel("S - Down"));
        	r.add(addHelpPanel("Q - Drop"));
        } else if (player == 2){
        	r.add(addHelpPanel("\u2190 - Left"));
        	r.add(addHelpPanel("\u2192 - Right"));
        	r.add(addHelpPanel("\u2191 - Rotate"));
        	r.add(addHelpPanel("\u2193 - Down"));
        	r.add(addHelpPanel("Space - Drop"));
        } else {
        	r.add(addHelpPanel("A or \u2190 - Left"));
        	r.add(addHelpPanel("D or \u2192 - Right"));
        	r.add(addHelpPanel("W or \u2191 - Rotate"));
        	r.add(addHelpPanel("S or \u2193 - Down"));
        	r.add(addHelpPanel("Space - Drop"));
        }
        	
        
        //BUTTONS
        if(player == 0)
        {
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
        }
        return r;
    }
    
    private JPanel addHelpPanel(String help) {
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(Box.createRigidArea(new Dimension(5,0)));
        JLabel jL = new JLabel(help);
        jL.setFont(font);
        jL.setForeground(Color.GRAY);
        jp.add(jL);
        jp.add(Box.createHorizontalGlue());
        return jp;
    }
    
    private JPanel getStatPanel() {
        int h = 12;
        JPanel r = new JPanel();
        BoxLayout rL = new BoxLayout(r,BoxLayout.Y_AXIS);
        r.setLayout(rL);
        r.setBorder(new EtchedBorder());
        
        JPanel[][] fig;
        JPanel figP, statFP;
        Dimension d = new Dimension(4*h, 4*h);
        Figure f;
        statsF = new JLabel[7];
        statsL = new JLabel[4];
        
        JPanel jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.LINE_AXIS));
        jp.add(Box.createRigidArea(new Dimension(5,0)));
        jp.add(new JLabel("STATISTICS: "));
        jp.add(Box.createHorizontalGlue());
        r.add(jp);
        
        r.add(Box.createRigidArea(new Dimension(0, 5)));
        
        for (int k = 0; k < 7; k++) {
            fig = new JPanel[4][4];
            figP = new JPanel();
            statFP = new JPanel();
            statFP.setLayout(new BoxLayout(statFP, BoxLayout.LINE_AXIS));
            figP.setLayout(new GridLayout(4,4));
            figP.setMinimumSize(d);
            figP.setPreferredSize(d);
            figP.setMaximumSize(d);
            
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    fig[i][j] = new JPanel();
                    fig[i][j].setBackground(nextBg);
                    figP.add(fig[i][j]);
                }
            }
 
            switch (k+1) {
            case Figure.I: f = new FigureI(); f.setOffset(2,0); break;
            case Figure.T: f = new FigureT(); f.setOffset(1,1); break;
            case Figure.O: f = new FigureO(); f.setOffset(1,1); break;
            case Figure.J: f = new FigureJ(); f.setOffset(1,1); break;
            case Figure.L: f = new FigureL(); f.setOffset(1,1); break;
            case Figure.S: f = new FigureS(); f.setOffset(1,1); break;
                case Figure.Stair: f = new FigureStair(); f.setOffset(0,0); break;
            default: f = new FigureZ(); f.setOffset(1,1); break;
            }
            
            for (int i = 0; i < 4; i++) {
                fig[f.arrY[i]+f.offsetY][f.arrX[i]+f.offsetX].setBackground(f.getGolor());
                fig[f.arrY[i]+f.offsetY][f.arrX[i]+f.offsetX].setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            }
            statFP.add(figP);
            statFP.add(new JLabel("  X  "));
            
            statsF[k] = new JLabel("0");
            statsF[k].setForeground(Color.BLUE);
            statFP.add(statsF[k]);
            r.add(statFP);
        }

        r.add(Box.createRigidArea(new Dimension(100, 15)));
        
        for (int i = 0; i < statsL.length; i++) {
            statFP = new JPanel();
            statFP.setLayout(new BoxLayout(statFP, BoxLayout.LINE_AXIS));
            switch (i) {
            case 0: statFP.add(new JLabel ("  Single  X  ")); break;
            case 1: statFP.add(new JLabel   ("Double  X  ")); break;
            case 2: statFP.add(new JLabel ("  Triple  X  ")); break;
            default: statFP.add(new JLabel("  Tetris  X  ")); break;
            }
            statsL[i] = new JLabel("0");
            statsL[i].setForeground(Color.BLUE);
            statFP.add(statsL[i]);
            r.add(statFP);
            r.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        return r;
    }
    
    public synchronized void nextMove() {
        f.setOffset(nextX, nextY);
        
        
        if(tg.addFigure(f)) {
            dropNext();
            f.setOffset(nextX, nextY);
            paintTG();
        } else {
            clearOldPosition();
        }
        paintNewPosition();
        
        if(isGameOver) {
        	gameover.play();
            int tmp = tg.updateHiScore();
            if(tmp >= 0 && player == 0) {
                
                String  s;
                
                do {
                    s = JOptionPane.showInputDialog(this,"Enter Your Name...\nMust be between 1 and 10 charachters long","New HiScore "+(tmp+1)+". Place", JOptionPane.PLAIN_MESSAGE);
                } while (s != null && (s.length() < 1 || s.length() > 10));
                
                if(s == null) {
                    s = "<empty>";
                }
                
                tg.saveHiScore(s,tmp);
                
                if(tmp == 0)
                    hiScoreLabel.setText(""+tg.hiScore[0].score);
            } 
        } 
    }
    
    private void clearOldPosition() {
        for (int j = 0; j < 4; j++) {
            cells[f.arrY[j]+f.offsetYLast][f.arrX[j]+f.offsetXLast].setBackground(Color.WHITE);
            cells[f.arrY[j]+f.offsetYLast][f.arrX[j]+f.offsetXLast].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }
    }
    
    private void paintNewPosition() {
        for (int j = 0; j < 4; j++) {
            cells[f.arrY[j]+f.offsetY][f.arrX[j]+f.offsetX].setBackground(f.getGolor());
            cells[f.arrY[j]+f.offsetY][f.arrX[j]+f.offsetX].setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        } 
    }
    
    private void paintTG() {
        int i = 0;
        Color c;
        for (int[] arr : tg.gLines) {
            for (int j = 0; j < arr.length; j++) {
                if(arr[j]!= 0) {
                    switch (arr[j]) {
                    case Figure.I: c = Figure.COL_I; break;
                    case Figure.T: c = Figure.COL_T; break;
                    case Figure.O: c = Figure.COL_O; break;
                    case Figure.J: c = Figure.COL_J; break;
                    case Figure.L: c = Figure.COL_L; break;
                    case Figure.S: c = Figure.COL_S; break;
                    case Figure.Z: c = Figure.COL_Z; break;
                    default: c = Figure.COL_Special; break;
                    }
                    cells[i][j].setBackground(c);
                    cells[i][j].setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                } else {
                    cells[i][j].setBackground(Color.WHITE);
                    cells[i][j].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                } 
            }
            i++;
        }
    }
    
    private void showNext(Figure f) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                next[i][j].setBackground(nextBg);
                next[i][j].setBorder(BorderFactory.createEmptyBorder());
            }
        }
        
        for (int j = 0; j < f.arrX.length; j++) {
            next[f.arrY[j]][f.arrX[j]].setBackground(f.getGolor());
            next[f.arrY[j]][f.arrX[j]].setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        }
    }
    
    public void dropNext() {
        if(isGameOver) return;
        nextX = 4;
        nextY = 0;

        score.setText(""+tg.getScore());
        lines.setText(""+tg.getLines());
        levelLabel.setText(tg.getLevel()+" / 20");
        speedLabel.setText(String.valueOf(tg.getLevel() + speed));

        f = fNext;
        if (isDemoing)
        	fNext = demoThread.getNextFigure();
        else
        	if (harmfulBlock){
                    fNext = ff.getRandomSpecialFigure(false);  // false signifies harmful block
                    harmfulBlock = false;
                }
                else if (helpfulBlock){
                    fNext = ff.getRandomSpecialFigure(true); // true signifies helpful block
                    helpfulBlock = false;
                }
                else{
                    fNext = ff.getRandomFigure();
                }
        showNext(fNext);

	if (pGameMode == 0){
		isGameOver = tg.isGameOver(f);
	}
	else if (pGameMode == 1){
		if (pLimit <= tg.getScore()){
			isGameOver = true;
			isWinner = true;
		}else{
			isGameOver = tg.isGameOver(f);
		}
	}
	else if (pGameMode == 2){
		if (pLimit <= tg.getLines()){
			isGameOver = true;
			isWinner = true;
		}else{
			isGameOver = tg.isGameOver(f);
		}
	}

        isNewFigureDroped = true;
        updateStats();
    }
    
    public void moveLeft() {
        if(isGameOver || isPause) return;
        if(nextX-1 >= 0) {
            if (tg.isNextMoveValid(f,f.offsetX-1,f.offsetY)) {
                nextX--;
                nextMove();
            }
        }
    }
    
    public void moveRight() {
        if(isGameOver || isPause) return;
        if(f.getMaxRightOffset()+1 < 10) {
            if (tg.isNextMoveValid(f,f.offsetX+1,f.offsetY)) {
                nextX++;
                nextMove();
            }
        }
    }
    
    public synchronized void moveDown() {
        if(isGameOver || isPause) return;
        nextY++;
        nextMove();
    }
    
    public synchronized void moveDrop() {
        if(isGameOver || isPause) return;
        
        f.offsetYLast = f.offsetY;
        f.offsetXLast = f.offsetX;
        clearOldPosition();
        
        while(tg.isNextMoveValid(f, f.offsetX, f.offsetY)) {
            f.setOffset(f.offsetX, f.offsetY+1);
        }

        
        tg.addFigure(f);
        paintTG();
        dropNext();
        nextMove();   
    }
    
    public synchronized void rotation() {
        if(isGameOver || isPause) return;
        for (int j = 0; j < f.arrX.length; j++) {
            cells[f.arrY[j]+f.offsetY][f.arrX[j]+f.offsetX].setBackground(Color.WHITE);
            cells[f.arrY[j]+f.offsetY][f.arrX[j]+f.offsetX].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }
        f.rotationRight();
        if(!tg.isNextMoveValid(f,f.offsetX,f.offsetY)) {
            f.rotationLeft();
        }
        nextMove();
    }
    
    public synchronized void pause() {
        isPause = !isPause;
		if (!isGameOver && !isPause) {
			paintTG();
			paintNewPosition();
		}
		if (pauseBut != null) {
			if (isPause)
				pauseBut.setText("Unpause");
			else
				pauseBut.setText("Pause");
		}
		if (isDemoing) {
			if (isPause)
				demoThread.stop();
			else
				demoThread.start();
		}
			
    }

    public void restart() {
    	//if (isOnePlayer && !isGameOver && (JOptionPane.showConfirmDialog(this, "Are you sure you want to restart the game?", "Restart Game", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION))
    	//	return;
    	
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                tg.gLines.get(i)[j] = 0;
                cells[i][j].setBackground(Color.WHITE);
                cells[i][j].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
            }
        } 
        ff.resetCounts();

        tg.setOpponentSpeed(0);
        tg.resetAttack();
        playerspeed = 0;
        speed = 0;
        isGameOver = false;
        isPause = false;
		isWinner = false;
        if (isDemoing) {
        	demoThread.reset();
        	fNext = demoThread.getNextFigure();
        }
        else
        	fNext = ff.getRandomFigure();
        tt.resetTime();
        time.setText("00:00:00");
        tg.resetStats();
        dropNext();
        nextMove();
        //pause();
    }
    
    private void updateStats() {
        for (int i = 0; i < statsF.length; i++) {
            statsF[i].setText(""+ff.getCounts()[i]);
        }
        
        for (int i = 0; i < statsL.length; i++) {
            statsL[i].setText(""+tg.getDropLines()[i]);
        }
    }
    
    private void showHiScore() {
        setHiScorePanel();
        try {
        JOptionPane.showMessageDialog(this,hiScorePanel,"HI SCORE", 
                JOptionPane.PLAIN_MESSAGE, 
                new ImageIcon(ImageIO.read(new BufferedInputStream(
                    new ResClass().getClass().getResourceAsStream("C:\\Users\\Eric\\Desktop\\Jetris-src-11\\Jetris\\splash.png")))));
        } catch (Exception e) {System.out.println("Oopsy");}       	
                	//loadImage("C:\\Users\\Eric\\Desktop\\Jetris-src-11\\Jetris\\splash.png")));
        
        hiScorePanel = null;
    }

    private void setHiScorePanel() {
        hiScorePanel = new JPanel(new BorderLayout());
        
        String[] colNames = {"Place", "Points", "Lines", "Name"};
        String[][] data = new String[tg.hiScore.length+1][colNames.length];
        data[0] = colNames;
        for (int i = 0; i < tg.hiScore.length; i++) {
            data[i+1] = new String[colNames.length];
            data[i+1][0] = (i+1)+".";
            data[i+1][1] = (""+tg.hiScore[i].score);
            data[i+1][2] = (""+tg.hiScore[i].lines);
            data[i+1][3] = (""+tg.hiScore[i].name);
        }
        
        JTable table = new JTable(data, colNames);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setBackground(new Color(230,255,255));
        table.setEnabled(false);
        
        hiScorePanel.add(table,BorderLayout.CENTER);
        //JButton jb = new JButton("Publish HiScore Online");
        //jb.addActionListener(pH);
        
        //hiScorePanel.add(jb, BorderLayout.SOUTH);
    }
    
    public void stopTimeThread() {
    	tt.stop();
    }
    
    public void setPlayerSpeed(int amount) {
    	playerspeed = amount;
    }
    
    public boolean isPaused(){
    	return isPause;
    }
    public void setPaused() {
    	isPause = true;
    }
    
    public boolean getGameOver() {
    	return isGameOver;
    }
    

    
}
