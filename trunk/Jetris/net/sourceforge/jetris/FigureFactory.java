package net.sourceforge.jetris;
import java.util.Random;

/* FigureFactory created on 14.09.2006 */

public class FigureFactory {
    
    Random r = new Random();
    private int[] counts;
    private int lastLastOne;
    private int lastOne;
    
    protected final static int Helpful = 0;
    protected final static int Harmful = 1;
    
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
       
        switch (i) {
            
            // harmful: i = 8 through 10
            case Figure.Stair: f = new FigureStair(); break;
            case Figure.Corners: f = new FigureCorners(); break;
            case Figure.Rand: f = new FigureRandom(); break;
            
            // helpful: i = 11 and 12
            case Figure.Bomb: f = new FigureBomb(); break;
            case Figure.Destroyer: f = new FigureDestroyer(); break;

            default: f = new FigureZ(); System.out.print("error selecting special block");  break;
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
		if(i==0) i=7;   //<-- needed to make sure that the array never goes <0
        counts[i-1]++;  //<-- there seems to be an incosistency when the stats label is made in the jetrisMain class
				        //all the stats seem to be shifted down 1, by incrementing counts[i-1] this fixes the inconsistency
					    //without altering the GUI
         
        i = r.nextInt(4);

        for (int j = 0; j < i; j++) {
            f.rotationRight();
        }
        
        return f;
    }
    
    Figure getRandomSpecialFigure(boolean helpful) {
        
        int i;
        
        if (helpful)
            i = r.nextInt(2) + 11; // indices for helpful blocks start at 11
        else
            i = r.nextInt(3) + 8;  // indices for harmful blocks start at 8
        
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
