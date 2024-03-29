package net.sourceforge.jetris;

import net.sourceforge.jetris.io.*;

import java.io.File;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JOptionPane;

public class TetrisGrid implements Serializable{
    
    static final String DAT_FILE = "JETRIS.DAT";
    
    LinkedList<int[]> gLines;
    private int lines;
    private int score;
    private int[] dropLines;
    public int level;
    private int opponentspeed = 0;
    HiScore[] hiScore;
    public int attack = 0;
    public int attacked = 0;
    private Thread it;
    protected boolean tetrisCleared;
    boolean destructionImpending = false;
    Random r = new Random();
    int specialEventType;
    
    TetrisGrid(Thread it) {
    	this.it = it;
        gLines = new LinkedList<int[]>();
        for (int i = 0; i < 20; i++) {
            gLines.add(new int[10]);
        }
        lines = score = 0;
        dropLines = new int[4];
        
        try{
            hiScore = HiScore.load(DAT_FILE);
        } catch (Exception e) {
            hiScore = new HiScore[3];
            for (int i = 0; i < hiScore.length; i++) {
                hiScore[i] = new HiScore();
                hiScore[i].name = "<empty>";
            }
            File f = new File(DAT_FILE);
            try {
                HiScore.write(hiScore, f);
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(null, "Could not load HiScore!", "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        
        tetrisCleared = false;
        specialEventType = 0;
    }
    
    boolean addFigure(Figure f) {
        for (int j = 0; j < f.arrX.length; j++) {
            if(f.arrY[j]+f.offsetY >= 20) {

                f.setOffset(f.offsetXLast,f.offsetYLast);
                                 addFiguretoGrid(f);
                eliminateLines();
                it.run();

                return true;
            }
            if(gLines.get(f.arrY[j]+f.offsetY)[f.arrX[j]+f.offsetX] != 0) {

                f.setOffset(f.offsetXLast,f.offsetYLast);
                                addFiguretoGrid(f);
                eliminateLines();
                it.run();  

                return true;
            }
        }
        return false;
    }
    
    boolean isNextMoveValid(Figure f, int xOffset, int yOffset) {
        boolean b = true;
        try {
            for (int j = 0; j < f.arrX.length; j++) {
                if(gLines.get(f.arrY[j]+yOffset)[f.arrX[j]+xOffset] != 0) {
                    b = false;
                } 
            }
            return b;
        } catch (Exception e) {
            return false;
        }
    }
    
    private void addFiguretoGrid(Figure f) {
        for (int j = 0; j < f.arrX.length; j++) {
            gLines.get(f.arrY[j]+f.offsetY)[f.arrX[j]+f.offsetX] = f.getGridVal();
        }
    }
    
    private void eliminateLines() {
        int lines = 0;
        int k = 0;
        
        for (Iterator iter = gLines.iterator(); iter.hasNext();) {
            int[] el = (int[]) iter.next();
            boolean isFull = true;
            destructionImpending = false;
            
            for (int j = 0; j < 10; j++) {
                if(el[j]==0) isFull = false;
                
                // If it's a destroyer block, set destructionImpending to true
                if (el[j] == 11) // 11 = destroyer 
                {
                    //System.out.println(".......test print.....");
                    destructionImpending = true;
                }
              
                if (el[j] == 12) { // 12 = bomb block
                    
                    if (k > 0) {
                        if (j != 0)  gLines.get(k - 1)[j - 1] = 0;
                        gLines.get(k - 1)[j] = 0;
                        if (j != 9)  gLines.get(k - 1)[j + 1] = 0;
                    }
                    
                    if (j != 0)  gLines.get(k)[j - 1] = 0;
                    gLines.get(k)[j] = 0;
                    if (j != 9)  gLines.get(k)[j + 1] = 0;      
                    
                    if (k <= 18){
                        if (j != 0) gLines.get(k + 1)[j - 1] = 0;
                        gLines.get(k + 1)[j] = 0;
                        if (j != 9) gLines.get(k + 1)[j + 1] = 0;
                    }
                }
            }
            if(isFull) {
                iter.remove();
                lines++;
                
                // if a line is cleared with the destroyer block, clear that player's grid
                if (destructionImpending){
                    //System.out.println("Destruction Impending");
                    for (int i = 0; i < 19; i++)
                         for (int j = 0; j < 10; j++)
                             gLines.get(i)[j] = 0;
                } 
            }
            
            k++;
        }
        
        //System.out.println("lines = " + lines);
        
        switch (lines) {
        case 1: score +=  100 +  5*level; break;
        case 2: score +=  400 + 20*level; break;
        case 3: score +=  900 + 45*level; break;
        case 4: score += 1600 + 80*level; break;
        }
        this.lines += lines;
        
        level = this.lines / 10;
        //level = 20;
        if(level > 20) level = 20;
        
        if (lines > 0) {
            dropLines[lines-1]++;
            opponentspeed += lines;
            if(lines == 3){
                attack = 1;
                //System.out.println("attack is set to " + attack);
				}
            else if (lines == 4){
                specialEventType = r.nextInt(7);
                
                if (specialEventType == 1) // probability of add 4 lines event is 1/7
                    attack = 4;
                else // otherwise, proceed to other possible tetris events in the interaction thread
                    tetrisCleared = true;
            }
        }

        for (int i = 0; i < lines; i++) {
            gLines.add(0,new int[10]);
        }
    }
    
    public void addLines() {
			
		//System.out.println("Add lines method is called, attacked = " + attacked);
                while(attacked > 0) {
			        	
			Random r = new Random();
			int[] el = new int[10];
			for(int i = 0; i < 10;i++)
				el[i] = r.nextInt(7);
			el[r.nextInt(9)] = 0;
			gLines.remove(0);
			gLines.add(el);
			attacked--;
		}
    }
    
    boolean isGameOver(Figure f) {
        return !isNextMoveValid(f, 4, 0);
    }
    
    int getLevel() { return level;}
    
    int getLines() { return lines;}
    
    int getScore() { return score;}
    
    int[] getDropLines() { return dropLines; }
    
    void resetStats() {
        lines = score = level = 0;
        for (int i = 0; i < dropLines.length; i++) {
            dropLines[i] = 0;
        }
    }
    
    int updateHiScore() {
        for (int i = 0; i < hiScore.length; i++) {
            HiScore s = hiScore[i];
            if((s.score < score) || 
              ((s.score == score) && (s.lines >= lines))) {
                //Stack the HiScore
                switch (i) {
                case 0:
                    s = hiScore[1];
                    hiScore[1] = hiScore[0];
                    hiScore[2] = s;
                    s = new HiScore();
                    hiScore[0] = s;
                    break;
                case 1:
                    hiScore[2] = s;
                    s = new HiScore();
                    hiScore[1] = s;
                    break;
                };
                s.score = score;
                s.lines = lines;
                return i;
            } 
        }
        return -1;
    }
    
    void saveHiScore(String Name, int pos) {
        File f = new File(DAT_FILE);
        try {
            hiScore[pos].name = Name;
            HiScore.write(hiScore, f);
        } catch (Exception e1) {
            JOptionPane.showMessageDialog(null, "Could not save HiScore!", "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    
    public String toString() {
        
        StringBuffer sb = new StringBuffer();
        for (int[] arr : gLines) {
            for (int j = 0; j < arr.length; j++) {
                sb.append(arr[j]);
            }
            sb.append('\n');
        }
        return sb.toString();
    }
    
    public int attackPlayer() {
        
        int tmp = attack;
    	attack = 0;
          // System.out.println("attack player method is called, " + tmp + " is returned.");
    	return tmp;
    }
    
    public void resetAttack(){
    	attack = 0;
    }

    public void playerAttacked(int lines) {

        attacked += lines;
     	//System.out.println("playerAttacked is called, attacked = " + attacked);
    }
    
    public int getOpponentSpeed(){
    	return opponentspeed;
    }

    public void setOpponentSpeed(int n){
    	opponentspeed = n;
    }

}
