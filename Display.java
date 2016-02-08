import java.awt.Canvas;
import java.awt.Dimension;
import javax.swing.JFrame;

public class Display {

	public JFrame frame;
	public Canvas canvas;
	
	public Display(){
		frame = new JFrame("Battle-Snake Lab");
		frame.setSize(mainSnake.windowSize.width, mainSnake.windowSize.height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(mainSnake.windowSize.width, mainSnake.windowSize.height));
		canvas.setMaximumSize(new Dimension(mainSnake.windowSize.width, mainSnake.windowSize.height));
		canvas.setMinimumSize(new Dimension(mainSnake.windowSize.width, mainSnake.windowSize.height));
		canvas.setFocusable(false);
		
		frame.add(canvas);
		frame.pack();
		frame.setFocusTraversalKeysEnabled(false);
	}
	
	public Canvas getCanvas(){
		return canvas;		
	}
	
	public JFrame getFrame(){
		return frame;
	}
	
}
