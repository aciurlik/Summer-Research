import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;


public class FurmanOfficial implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final Color lightPurple = new Color(76,42,135);
	public static final int[] lightPurpleRGB = {76, 42, 135};
	public static final Color darkPurple = new Color(62,24,94);
	public static final int[] darkPurpleRGB = {62, 24, 94};
	public static final Color grey = new Color(91,91,91);
	public static final int[] greyRGB = {91, 91, 91};
	public static final Color nimbus = new Color(93,45, 133);
	public static final Color nimbusAlpha = new Color(93, 45, 133, 200);
	public static Color official;
	public static Color officialAlpha;
	
	public static final boolean masterIsNotAround = false;
	
	
	public static final Color bouzarthDarkPurple = darkPurple(230);
	
	public static final Color bouzarthGrey = grey(60);
	
	public static final Font bigHeaderFont = getFont(18);
	public static final Font smallHeaderFont = getFont(14);
	public static final Font normalFont = getFont(12);
	public static final Font monospaced = new Font("MONOSPACED", Font.PLAIN, 12);
	
	static{

		
		UIManager.put("OptionPane.messageFont", getFont(12));
		UIManager.put("OptionPane.font", getFont(12));
		UIManager.put("OptionPane.buttonFont", getFont(12));
		UIManager.put("ComboBox.font", monospaced);
	
	
	
		try {
		
			// Set cross-platform Java L&F (also called "Metal")
			 UIManager.setLookAndFeel(new NimbusLookAndFeel() {

			      @Override
			      public UIDefaults getDefaults() {
			       UIDefaults ret = super.getDefaults();
			       ret.put("List.font",
			    		   monospaced); // supersize me
			       ret.put("ComboBox.font",monospaced);
			  
			       return ret;
			      }

			     });
			
			MenuOptions.setUIType(true);
		} 
		catch (Exception e) {
			try{UIManager.setLookAndFeel(
					UIManager.getCrossPlatformLookAndFeelClassName()
					);
			}catch(Exception e2){
				e2.printStackTrace();
			}
			MenuOptions.setUIType(false);
		}
		if(MenuOptions.UIType){
			official = nimbus;
			officialAlpha = nimbusAlpha;
		}
		else{
			official= bouzarthDarkPurple;
			officialAlpha = bouzarthDarkWithAlpha(200);
		}
		
		
		UIManager.put("List.font",monospaced);
		UIManager.put("TextArea.font",monospaced);
		

	}
	
	
	
	/**
	 * Rescale this RGB value according to the given alpha
	 * as if it were placed on a white background.
	 * @param originalValue
	 * @param alpha
	 * @return
	 */
	
	public static Color bouzarthDarkWithAlpha(int alpha){
		int red=bouzarthDarkPurple.getRed();
		int blue=bouzarthDarkPurple.getBlue();
		int green=bouzarthDarkPurple.getGreen();
		return(new Color(red, green, blue, alpha));
		
	}
	
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
	
	/**
	 * Most calls should be replaced with one of :
	 * FurmanOfficial.bigHeaderFont
	 * FurmanOfficial.smallHeaderFont
	 * FurmanOfficial.normalFont
	 * 
	 * so that the presentation is standard across different buttons.
	 * 
	 * If your font size is between 10 and 24, use one of these fields instead.
	 * 
	 * 
	 * Calls to this method may be used for extreme cases, like 
	 * the surprise me panel (which has a font size 45). 
	 * 
	 * @param point
	 * @return
	 */
	public static Font getFont(int point){
		return new Font("Helvatica", Font.PLAIN, point);
	}
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
