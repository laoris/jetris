package net.sourceforge.jetris;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.Timer;

public class DemoMode implements ActionListener {
	private static final int TIMEOUT = 300; // milliseconds between each move
	private static Random random = new Random();
	private static final int LEFT   = 0;
	private static final int RIGHT  = 1;
	private static final int DOWN   = 2;	/* soft drop (like hitting the down arrow) */
	private static final int ROTATE = 3;
	private static final int DROP   = 4;	/* hard drop (like hitting space bar) */
	
	/*
	 * This is an array of move sets.
	 * Each set is an array of pieces.
	 * Each piece array has the type of piece you want to drop as the first (zeroth element)
	 * of the array.  The rest of the array is a list of moves for that particular piece.
	 * 
	 * For instance:
	 * 		{Figure.I,	LEFT,	ROTATE,	DROP}
	 * means to make a long I-figure, move it left once, rotate it once, and then do a
	 * hard drop.
	 * 
	 * You should typically end each piece's move list with a DROP, otherwise the demo gets
	 * boring while waiting for the piece to fall.
	 */	
	private static final int[][][] moveSets = {
		// move set 0
		{	// piece	move 1	move 2	move 3	etc
			{Figure.I,	LEFT,	LEFT,	LEFT,	LEFT,	DROP},			// piece 0
			{Figure.T,	LEFT,	ROTATE,	LEFT,	ROTATE,	LEFT,	DROP},	// piece 1
			{Figure.Z,	LEFT,	DROP},									// etc.
			{Figure.O,	RIGHT,	RIGHT,	RIGHT,	DROP},
			{Figure.Z,	ROTATE, LEFT,	LEFT,	LEFT,	DROP},
			{Figure.J,	ROTATE,	ROTATE,	RIGHT,	RIGHT,	DROP},
			{Figure.L,	LEFT,	DROP},
			{Figure.T,	LEFT,	LEFT,	LEFT,	LEFT,	DROP},
			{Figure.L,	ROTATE,	ROTATE,	DROP},
			{Figure.S,	ROTATE,	RIGHT,	RIGHT,	RIGHT,	DROP},
			{Figure.S,	ROTATE,	RIGHT,	DROP},
			{Figure.O,	LEFT,	LEFT,	LEFT,	LEFT,	DROP},
			{Figure.J,	ROTATE,	ROTATE,	ROTATE,	LEFT,	LEFT,	DROP},
			{Figure.I,	RIGHT,	RIGHT,	RIGHT,	RIGHT,	RIGHT,	DROP},
			{Figure.S,	RIGHT,	RIGHT,	ROTATE, RIGHT,	DROP},
			{Figure.J,	ROTATE,	ROTATE,	ROTATE,	DROP},
			{Figure.L,	ROTATE,	LEFT,	DROP},
			{Figure.J,	LEFT,	ROTATE,	LEFT,	ROTATE,	LEFT,	ROTATE,	LEFT,	DROP},
			{Figure.J,	ROTATE,	RIGHT,	ROTATE,	RIGHT,	ROTATE, DROP},
			{Figure.I,	RIGHT,	RIGHT,	RIGHT,	RIGHT,	RIGHT,	DROP},
		},
		// move set 1
		{
			{Figure.L,	ROTATE,	ROTATE,	ROTATE,	RIGHT,	RIGHT,	DROP},	// piece 1
			{Figure.L,	LEFT,	LEFT,	LEFT,	DROP},			// piece 2
			{Figure.Z,	LEFT,	LEFT,	DROP},					// etc
			{Figure.T,	DROP},
			{Figure.J,	ROTATE,	ROTATE,	ROTATE,	RIGHT,	DROP},
			{Figure.S,	LEFT,	DROP},
			{Figure.L,	RIGHT,	ROTATE,	RIGHT,	RIGHT,	ROTATE,	RIGHT,	DROP},
			{Figure.I,	ROTATE,	RIGHT,	RIGHT,	DROP},
			{Figure.T,	LEFT,	LEFT,	LEFT,	DROP},
			{Figure.I,	LEFT,	LEFT,	LEFT,	LEFT,	DROP},
		},
		
	};
	
	private final Timer timer;
	private final Player player;
	private int currentMoveSet;
	private int currentPiece;
	private int currentMove;
	private int nextMoveSet;
	private int nextPiece;
	
	public DemoMode(Player p) {
		timer = new Timer(TIMEOUT, this);
		player = p;
		nextMoveSet = 0;
		nextPiece = moveSets[nextMoveSet].length;	// an invalid array index, forcing getNextFigure to pick a new move set
	}
	
	/*
	 * This is sometimes necessary to force getNextFigure to pick a new moveset.
	 */
	
	public void reset() {
		nextPiece = moveSets[nextMoveSet].length;
	}
	
	/*
	 * getNextFigure should replace all calls to FigureFactory.getRandomFigure when
	 * in demo mode.
	 */
	public Figure getNextFigure() {
		// Make the current piece (the one acted upon by the timer in the actionPerformed
		// method below) the last piece returned by getNextFigure
		currentPiece = nextPiece;
		currentMoveSet = nextMoveSet;
		currentMove = 1;
		// Go to the next piece in the move set
		++nextPiece;
		// If we've reached the end of the moveSet, pick a new one
		if (nextPiece >= moveSets[nextMoveSet].length) {
			nextMoveSet = random.nextInt(moveSets.length);
			nextPiece = 0;
		}
		return FigureFactory.getFigure(moveSets[nextMoveSet][nextPiece][0]); 
	}
	
	public void actionPerformed(ActionEvent e) {
		// Some checks just in case stuff got screwy somewhere
		// We wouldn't want to throw any IndexOutOfBoundsExceptions...
		if (currentPiece >= moveSets[currentMoveSet].length)
			return;
		if (currentMove >= moveSets[currentMoveSet][currentPiece].length)
			return;
		switch (moveSets[currentMoveSet][currentPiece][currentMove]) {
		case LEFT:
			player.moveLeft();
			break;
		case RIGHT:
			player.moveRight();
			break;
		case DOWN:
			player.moveDown();
			break;
		case ROTATE:
			player.rotation();
			break;
		case DROP:
			player.moveDrop();
			// Decrement currentMove here because moveDrop() implicitly calls Player.dropNext
			// which resets currentMove to 1, which is where we want it to be for the next move
			// (not 2).
			--currentMove;
			break;
		}
		++currentMove;
	}

	public boolean isRunning() {
		return timer.isRunning();
	}

	public void restart() {
		timer.restart();
	}

	public void start() {
		timer.start();
	}

	public void stop() {
		timer.stop();
	}
	
	
}
