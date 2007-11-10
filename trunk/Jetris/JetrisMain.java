import net.sourceforge.jetris.NewGame;
import javax.swing.*;

public class JetrisMain {
    public static void main(String[] args) {
		NewGame game = new NewGame();
		game.setSize(400, 245);  //was(300,200)
		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.setVisible(true);
		game.setResizable(false);
                
        
    }

}
