import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Driver {
	static ArrayList<ScheduleGUI> listOfScheduleGUIs; 
	
	public static void addScheduleGUI(Schedule s){
		ScheduleGUI schGUI = new ScheduleGUI(s);
		schGUI.addWindowListener(new WindowListener(){

			@Override
			public void windowActivated(WindowEvent arg0) {
			}
			@Override
			public void windowClosed(WindowEvent arg0) {
				removeScheduleGUI(schGUI);
				
			}
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				removeScheduleGUI(schGUI);
			}
			@Override
			public void windowDeactivated(WindowEvent arg0) {
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
			}	

			@Override
			public void windowIconified(WindowEvent arg0) {
		}

			@Override
			public void windowOpened(WindowEvent arg0) {
			}
			
		});
		listOfScheduleGUIs.add(schGUI);
		
	}
	
	public static void removeScheduleGUI(ScheduleGUI s){
		listOfScheduleGUIs.remove(s);
		if(listOfScheduleGUIs.isEmpty()){
			System.exit(0);
		}
	}
	
	public static void openSchedule() {

		Schedule result = FileHandler.openSchedule();
		if(result!=null){
			//TODO make sure nothing else needs to be set
			if(!FurmanOfficial.masterIsNotAround){
				result.reloadMajors();
			}

			ScheduleGUI d = new ScheduleGUI(result);
			d.setSchedule(result);
			d.update();

			//setSchedule(result);
			//this.update();
		}

	}
	
	
	/**
	 * Given these objects, and this list of strings,
	 * let the user pick one  
	 * @return
	 */
	public static int GUIChooseAmong(ArrayList<Object> choicesList, ArrayList<String> displaysList, String message, String title){
		Object[] choices = choicesList.toArray(new Object[choicesList.size()]);
		Object[] displays = displaysList.toArray(new String[displaysList.size()]);
		if(choices.length != displays.length){
			throw new RuntimeException("Wrong sizes for choices and displays in GUICHooseAmong" + choices.length + "," + displays.length);
		}
		String chosenString = (String)JOptionPane.showInputDialog(new JFrame(), message , title , JOptionPane.PLAIN_MESSAGE, null, displays, "Cats");
		int chosenIndex = 0;
		for(; chosenIndex < displays.length ; chosenIndex ++){
			if(displays[chosenIndex] == chosenString){
				break;
			}
		}
		if(chosenIndex >= choices.length){
			return -1;
		}
		return chosenIndex;
	}



	public static SemesterDate GUIChooseStartTime(ArrayList<SemesterDate> semesters){
		ArrayList<String> semesterStrings = new ArrayList<String>();
		ArrayList<Object> semesterObjects = new ArrayList<Object>();
		for(SemesterDate d : semesters){
			semesterStrings.add(d.getUserString());
			semesterObjects.add(d);
		}
		int index = GUIChooseAmong(semesterObjects, semesterStrings, "Which was your first semester at Furman? ", "Pick a semester");
		if(index != -1){
			return semesters.get(index);
		}
		return null;
	}

	
	
	public static SemesterDate tryPickStartDate(){
		ArrayList<SemesterDate> supportedSemesters = new ArrayList<SemesterDate>();
		//supportedSemesters.add( new SemesterDate(2012, SemesterDate.FALL ));
		//supportedSemesters.add( new SemesterDate(2013, SemesterDate.FALL ));
		supportedSemesters.add( new SemesterDate(2014, SemesterDate.FALL ));
		supportedSemesters.add( new SemesterDate(2015, SemesterDate.FALL ));
		supportedSemesters.add( new SemesterDate(2016, SemesterDate.FALL ));
		supportedSemesters.add( new SemesterDate(2017, SemesterDate.FALL ));

		return GUIChooseStartTime(supportedSemesters);

	}
	
	

	/**
	 * Reads settings from file and makes sure Schedule is matching those. 
	 */
	private static void establishSettings() {
		if(FileHandler.propertyGet(MenuOptions.startUp).equals("true")){
			startUpMessage();
		}

	}




/**
 * Opens up the StartUp help. 
 */
	public static void startUpMessage() {
		StartUpMenu start = new StartUpMenu();

	}

	
	
	public static void main(String[] args){
		listOfScheduleGUIs = new ArrayList<ScheduleGUI>();
		//This just loads FurmanOfficial into memory so that the UIManager
		// will be set before other static code gets run.
		Color c = FurmanOfficial.grey;
		SemesterDate start = tryPickStartDate();

		if(start == null){
			//this will close any running code, including the JOptionPanes which don't get collected by 
			// the garbage collector for some reason.
			System.exit(0);
			return;
		}
		else{
			Schedule.defaultFirstSemester = start;
			//new Driver();
			Driver.addScheduleGUI(Schedule.testSchedule());
			establishSettings();


		}
	}
}
