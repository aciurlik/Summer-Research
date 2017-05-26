import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class FurmanOfficial {
	public static final Color lightPurple = new Color(76,42,135);
	public static final int[] lightPurpleRGB = {76, 42, 135};
	public static final Color darkPurple = new Color(62,24,94);
	public static final int[] darkPurpleRGB = {62, 24, 94};
	public static final Color grey = new Color(91,91,91);
	public static final int[] greyRGB = {91, 91, 91};
	
	
	public static Color lightPurple(int alpha){
		return new Color(lightPurpleRGB[0], lightPurpleRGB[1], lightPurpleRGB[2], alpha);
	}
	public static Color darkPurple(int alpha){
		return new Color(darkPurpleRGB[0], darkPurpleRGB[1], darkPurpleRGB[2], alpha);
	}
	public static Color grey(int alpha){
		return new Color(greyRGB[0], greyRGB[1], greyRGB[2], alpha);
	}
	
	public static final Font closeFont = new Font("Helvatica", Font.PLAIN, 12);

	public static void testColors(int numDivisions){
		JFrame f = new JFrame();
		JPanel main = new JPanel();
		int numTests = numDivisions;
		main.setLayout(new GridLayout(0,3));
		for(int i = 0; i < 255 ; i += 255/numTests){
			JPanel swath1 = new JPanel();
			swath1.setBackground(lightPurple(i));
			main.add(swath1);
			JPanel swath2 = new JPanel();
			swath2.setBackground(darkPurple(i));
			main.add(swath2);
			JPanel swath3 = new JPanel();
			swath3.setBackground(grey(i));
			main.add(swath3);
		}
		f.getContentPane().add(main);
		f.pack();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		
	}
	
	public static void main(String[] args){
		testColors(10);
	}
}
