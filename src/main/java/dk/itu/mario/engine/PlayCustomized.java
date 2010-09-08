package dk.itu.mario.engine;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;

public class PlayCustomized {

	public static void main(String[] args)
     {
				JFrame frame = new JFrame("Infinite Adaptive Mario");
		    	MarioComponent mario = new MarioComponent(640, 480,true);

		    	frame.setContentPane(mario);
		    	frame.setResizable(false);
		        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		        frame.pack();

		        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		        frame.setLocation((screenSize.width-frame.getWidth())/2, (screenSize.height-frame.getHeight())/2);

		        frame.setVisible(true);

		        mario.start();   
	}	

}
