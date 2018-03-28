import edu.cmu.cs.cs214.hw4.core.GameController;
import edu.cmu.cs.cs214.hw4.gui.StartGameClient;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

/**
 * Carcassonne Game.
 */
public class Carcassonne {
    /**
     * Main method.
     * @param args input strings
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() ->{
            JFrame frame = new JFrame("Start a new Carcassonne Game");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            frame.add(new StartGameClient(frame));
            // display the frame
            frame.pack();
            frame.setResizable(true);
            frame.setVisible(true);
        });
    }
}
