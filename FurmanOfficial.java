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
	
	/**
	 * Rescale this RGB value according to the given alpha
	 * as if it were placed on a white background.
	 * @param originalValue
	 * @param alpha
	 * @return
	 */
	public static int rescale(int originalValue, int alpha){
		double scalar = alpha / 255.0;
		int result = (int)(originalValue * scalar + 255 * (1- scalar));
		return result;
	}
	public static Color lightPurple(int alpha){
		return new Color(
				rescale(lightPurpleRGB[0], alpha), 
				rescale(lightPurpleRGB[1], alpha),
				rescale(lightPurpleRGB[2], alpha)
				);
	}
	public static Color darkPurple(int alpha){
		return new Color(
				rescale(darkPurpleRGB[0], alpha),
				rescale(darkPurpleRGB[1], alpha),
				rescale(darkPurpleRGB[2], alpha)
				);
	}
	public static Color grey(int alpha){
		return new Color(
				rescale(greyRGB[0], alpha),
				rescale(greyRGB[1], alpha),
				rescale(greyRGB[2], alpha)
				);
	}
	
	public static final Font closeFont = new Font("Helvatica", Font.PLAIN, 12);

	
	/**
	 * Make a window showing all the different colors with different
	 * alpha values.
	 * 
	 * numDivisions tells how many swaths of each color should be made, and
	 * it will split the range 0-255 into numDivisions pieces to make the panels.
	 * @param numDivisions
	 */
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
