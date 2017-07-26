

import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;




/**
 * Blurb written 7/24/2017
 * Last updated 7/24/2017
 * This class lets the user choose a Major (or Minors or Track, they
 * all operate in a similar way) from a list of majors.
 * 
 *
 */
public class MajorPopUpChooser {
	ScheduleGUI schGUI;
	Schedule s;
	JFrame frame = new JFrame();
	ArrayList <Major> displayThings;
	ImageIcon icon = FileHandler.getDialogImage();



	public MajorPopUpChooser(String type, ScheduleGUI schGUI, Schedule s){
		//Creates the PopUp Window
		frame = new JFrame(type);
		this.schGUI=schGUI;

		//Retrieves correct list to display on dialog box, and calls diaolog box method
		if(type.equals(MenuOptions.addMajor)){
			ListOfMajors majors = schGUI.l; //Links to test Major
			ArrayList<Major> collectionOfMajors =  majors.getGUIMajors();
			displayThings = schGUI.sch.filterAlreadyChosenMajors(collectionOfMajors);
			createDiaologBox(type);
		}

		if(type.equals(MenuOptions.addMinor)){
			ListOfMajors majors = schGUI.l;//Links to test List
			ArrayList<Major> collectionOfMinors =  majors.getGUIMinor();
			displayThings = schGUI.sch.filterAlreadyChosenMajors(collectionOfMinors);
			createDiaologBox(type);
		}

		if(type.equals(MenuOptions.addTrack)){
			ListOfMajors majors = schGUI.l;//Links to test List
			ArrayList<Major> collectionOfTrack =  majors.getGUITrack();
			displayThings = schGUI.sch.filterAlreadyChosenMajors(collectionOfTrack);
			createDiaologBox(type);

		}
	}



	/**
	 * Create the dialog box and get the user's choice of major.
	 * @param s, The instructions that are added to the dialog box. 
	 */
	public void createDiaologBox(String s){
		Major[] dialogList = new Major[displayThings.size()];
		//Puts in a form the JOptionPane will take. 
		for(int i=0; i<displayThings.size(); i++){
			dialogList[i]=displayThings.get(i);
		}
		Major m = (Major)JOptionPane.showInputDialog(frame, "Please " + s,  s, JOptionPane.PLAIN_MESSAGE, icon, dialogList, "cat" );
		if((m != null) && (m instanceof Major)){
			schGUI.GUIAddMajor(m);
		}
	}
}




