import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;


/**
 * Blurb written: 7/25/2017
 * Last updated: 7/26/2017
 * 
 * This class stores default colors, fonts, and other style information.
 * 
 * It is similar to MenuOptions, but this class holds program-wide
 * settings, while MenuOptions mainly holds strings.
 * 
 * Use the following website for the standards. If the website no longer exists,
 * google "Furman graphic design guide" and look for "Official univerisity colors"
 * and so on.
 * http://www.furman.edu/sites/university-communications/brand-standards/Pages/graphic-design-guide.aspx
 * 
 */
public class FurmanOfficial implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
	
	
	
	
	
	////////////////////////////
	////////////////////////////
	/////Colors 
	////////////////////////////
	////////////////////////////
	@SuppressWarnings("unused")
	private boolean ___Colors_________;
	
	//These are the official colors from Furman's website.
	private static final int[] officialLightPurpleRGB = {76, 42, 135}; // official Furman light purple
	private static final int[] officialDarkPurpleRGB = {62, 24, 94}; //official Furman dark purple
	private static final int[] officialGreyRGB = {91, 91, 91}; //official Furman grey
	
	
	//The Nimbus UI lightens and darkens buttons.
	// These two fields give the actual displayed color
	// of a nimbus button if the button's color is set to
	// officialDarkPurple.
	private static final Color nimbusPurple = new Color(93,45, 133); 
	private static final Color nimbusAlpha = new Color(93, 45, 133, 200);
	private static final Color bouzarthDarkPurple = darkPurple(230);
	
	
	
	// These fields are the main color fields used by other classes.
	// They are decided in static blocks at compile time, and might
	// be different combinations of the above colors.
	public static Color darkPurple;
	public static Color lightPurple;
	public static Color buttonPurple;
	public static final Color bouzarthGrey = grey(60);
	public static final Color officialGrey = new Color(
			officialGreyRGB[0],officialGreyRGB[1],officialGreyRGB[2]);
	
	
	
	////////////////////////////
	////////////////////////////
	/////Fonts
	////////////////////////////
	////////////////////////////
	@SuppressWarnings("unused")
	private boolean ___Fonts_________;
	
	public static final Font bigHeaderFont = getFont(18);
	public static final Font smallHeaderFont = getFont(14);
	public static final Font normalFont = getFont(12);
	public static final Font monospaced = new Font("MONOSPACED", Font.PLAIN, 12);
	
	
	public static boolean masterIsAround = false;
	
	
	
	
	
	////////////////////////////
	////////////////////////////
	///// Loading static block
	////////////////////////////
	////////////////////////////
	@SuppressWarnings("unused")
	private boolean ___LoadingBlock_________;
	
	
	/**
	 * This static block loads the UIManager, and should be called before
	 * any Swing objects are created.
	 */
	static{
		UIManager.put("OptionPane.messageFont", getFont(12));
		UIManager.put("OptionPane.font", getFont(12));
		UIManager.put("OptionPane.buttonFont", getFont(12));
		UIManager.put("ComboBox.font", monospaced);
		
		
		//Try to set the UIManager to Nimbus.
		// set darkPurple and lightPurple based on whether that's successful.
		try {
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
			
			MenuOptions.setNimbusLoaded(true);
		} 
		catch (Exception e) {
			try{UIManager.setLookAndFeel(
					UIManager.getCrossPlatformLookAndFeelClassName()
					);
			}catch(Exception e2){
				e2.printStackTrace();
			}
			MenuOptions.setNimbusLoaded(false);
		}
		if(MenuOptions.nimbusLoaded){
			darkPurple = nimbusPurple;
			lightPurple = nimbusAlpha;
		}
		else{
			darkPurple= bouzarthDarkPurple;
			lightPurple = bouzarthDarkWithAlpha(200);
		}
		buttonPurple = new Color(officialDarkPurpleRGB[0], officialDarkPurpleRGB[1], officialDarkPurpleRGB[2]);
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
		return(new Color(
				rescale(red, alpha), 
				rescale(green, alpha), 
				rescale(blue, alpha)
				));
	}
	
	public static int rescale(int originalValue, int alpha){
		double scalar = alpha / 255.0;
		int result = (int)(originalValue * scalar + 255 * (1- scalar));
		return result;
	}
	public static Color lightPurple(int alpha){
		return new Color(
				rescale(officialLightPurpleRGB[0], alpha), 
				rescale(officialLightPurpleRGB[1], alpha),
				rescale(officialLightPurpleRGB[2], alpha)
				);
	}
	public static Color darkPurple(int alpha){
		return new Color(
				rescale(officialDarkPurpleRGB[0], alpha),
				rescale(officialDarkPurpleRGB[1], alpha),
				rescale(officialDarkPurpleRGB[2], alpha)
				);
	}
	public static Color grey(int alpha){
		return new Color(
				rescale(officialGreyRGB[0], alpha),
				rescale(officialGreyRGB[1], alpha),
				rescale(officialGreyRGB[2], alpha)
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
