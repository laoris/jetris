package net.sourceforge.jetris;
import java.util.Random;

/* FigureFactory created on 14.09.2006 */

public class FigureFactory {
    
    Random r = new Random();
    private int[] counts;
    private int lastLastOne;
    private int lastOne;
    
    FigureFactory() {
    	counts = new int[7];
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
        default /* Figure.Z */: f = new FigureZ(); break;
        }
    	return f;
    }
    
    static Figure getSpecialFigure(int i) {
        Figure f;
        i += 8; // add 8 because special blocks are 8 and up
        switch (i) {
            case Figure.Stair: f = new FigureStair(); break;
            case Figure.Corners: f = new FigureCorners(); break;

            default: f = new FigureStair(); break;
        }
        
        return f;  
    }
    
    Figure getRandomFigure() {
        int i = r.nextInt(7);
        while(lastLastOne == lastOne && lastOne == i+1) {
            i = r.nextInt(7);

        }
        Figure f = getFigure(i);
        lastLastOne = lastOne;
        lastOne = i+1;
        counts[i]++;
         
        i = r.nextInt(4);

        for (int j = 0; j < i; j++) {
            f.rotationRight();
        }
        
        return f;
    }
    
    Figure getRandomSpecialFigure() {
        
        int i = r.nextInt(2);
        Figure f = getSpecialFigure(i);
        
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
