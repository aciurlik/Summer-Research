import java.io.File;

import javax.swing.JButton;


//https://stackoverflow.com/questions/16267562/making-a-jpanel-into-a-joptionpane-ok-option



public class MenuOptions {
	//Both Additions and Menu
	public static final  String addMajor = "Add Major";
	public static final  String addMinor = "Add Minor";
	public static final  String addTrack = "Add Track";
	public static final  String addInternship = "Explore Internships";
	public static final  String addResearch = "Explore Research";
	public static final  String exploreStudyAway = "Explore Study Away";

	public static final  String addMayX = "Add MayX";
	public static final  String addSummerClass = "Add Summer Class";
	public static final  String summerSessionOne = "Summer Session One";
	public static final  String summerSessionTwo = "Summer Session Two";
	public static final  String mayX = "May X";

	//Semester Panel
	public static final  String addInstruct = "Add a Course";
	public static final  String supriseMe = "Surprise Me!";
	public static final  String changeInstruct = "Change Course";
	public static final  String addStudyAway = "Add Study Away";
	public static final  String addNotes = "Add Note";
	public static final  String removeStudyAway = "Remove Study Away";
	public static final  String removeNotes = "Remove Note";
	
	public static final  String deleteSemester = "x";
	public static final  String hideSemester = "-";
	public static final  String showSemester = "+";
	
	public static final String resourcesFolder = "Resources" + File.separator;
	
	
	
	
	//Schedule Element Panel
	public static final  String addCourseWithRequirement = "Choose a course";


	//Suprise Me 
	public static final  String Want = "I want it";
	public static final  String Challenge = "Take the Challenge";
	public static final  String tryAgain = "Try Again";

	//Only Menu Bar 
	public static final  String openSchedule = "Open Schedule";
	public static final  String newSchedule = "New Schedule";
	public static final  String saveSchedule = "Save Schedule";
	public static final  String printSchedule = "Print Schedule";
	public static final  String removeMajor = "Remove Major";
	public static final  String removeTrack = "Remove Track";
	public static final  String removeMinor = "Remove Minor";
	public static final  String exploreMayX = "Explore MayX Opportunities";
	public static final  String help ="Help";
	public static String checkAllErrors = "Check all Errors";
	
	
	public static boolean UIType;


	public boolean isUIType() {
		return UIType;
	}


	public static void setUIType(boolean uIType) {
		UIType = uIType;
	}
	
}




