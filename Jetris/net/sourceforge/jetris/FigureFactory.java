package net.sourceforge.jetris;
import java.util.Random;

/* FigureFactory created on 14.09.2006 */

public class FigureFactory {
    
    Random r = new Random();
    private int[] counts;
    private int lastLastOne;
    private int lastOne;
    
    FigureFactory() {
    	counts = new int[8];
    }

    static Figure getFigure(int i) {
    	Figure f;
    	switch (i) {
        case Figure.I: f = new FigureI(); break;
        case Figure.T: f = new FigureT(); break;
        case Figure.O: f = new FigureO(); break;
        case Figure.L: f = new FigureL(); break;
        case Figure.J: f = new FigureJ(); break;
        case Figure.S: f = new FigureS(); break;
        case Figure.Stair: f = new FigureStair(); break;
        default /* Figure.Z */: f = new FigureZ(); break;
        }
    	return f;
    }
    
    Figure getRandomFigure() {
        int i = r.nextInt(8);
        while(lastLastOne == lastOne && lastOne == i+1) {
            i = r.nextInt(8);
        }
        Figure f = getFigure(i - 1);
        lastLastOne = lastOne;
        lastOne = i+1;
        counts[i]++;
         
        i = r.nextInt(4);

        for (int j = 0; j < i; j++) {
            f.rotationRight();
        }
        
        return f;
    }
    
    protected int[] getCounts() {
        return counts;
    }
     
    protected void resetCounts() {
        for (int i = 0; i < counts.length; i++) {
            counts[i] = 0;
        }
    }
}
