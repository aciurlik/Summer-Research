

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

	public static void chooseSchedulesToCompare() {

		Schedule one = FileHandler.openSchedule();
		Schedule two = FileHandler.openSchedule();
		compareSchedules(one, two);

	}

	private static void compareSchedules(Schedule one, Schedule two) {
		SemesterDate defaultPrior = new SemesterDate(1995, SemesterDate.OTHER);
		ArrayList<ScheduleElement> allOrderedElementsOne = new ArrayList<ScheduleElement>();
		ArrayList<SemesterDate> coorespondingDatesOne = new ArrayList<SemesterDate>();
		ArrayList<String> oneTotalString = new ArrayList<String>();
		ArrayList<String> twoTotalString = new ArrayList<String>();
		for(Semester s: one.getAllSemestersSorted()){
			for(ScheduleElement se: s.elements){
				allOrderedElementsOne.add(se);
				if(s.isAP){
					coorespondingDatesOne.add(defaultPrior);

				}
				else {
					coorespondingDatesOne.add(s.semesterDate);
				}
			}
		}
		for(int i=0; i<allOrderedElementsOne.size(); i++){
			String s = "";
			if(allOrderedElementsOne.get(i) instanceof ScheduleCourse){
				ScheduleCourse c =(ScheduleCourse) allOrderedElementsOne.get(i);
				 s = c.toString() + coorespondingDatesOne.get(i).toString();
			}
			else if(allOrderedElementsOne.get(i) instanceof Requirement){
				Requirement c =(Requirement) allOrderedElementsOne.get(i);
				 s = c.getDisplayString() + coorespondingDatesOne.get(i).toString();
			}
			
			oneTotalString.add(s);
		}
		ArrayList<ScheduleElement> allOrderedElementsTwo = new ArrayList<ScheduleElement>();
		ArrayList<SemesterDate> coorespondingDatesTwo = new ArrayList<SemesterDate>();
		for(Semester s: two.getAllSemestersSorted()){
			for(ScheduleElement se: s.elements){
				allOrderedElementsTwo.add(se);
				if(s.isAP){
					coorespondingDatesTwo.add(defaultPrior);

				}
				else {
					coorespondingDatesTwo.add(s.semesterDate);
				}
			}
		}
		for(int i=0; i<allOrderedElementsTwo.size(); i++){
			String s = "";
			if(allOrderedElementsTwo.get(i) instanceof ScheduleCourse){
				ScheduleCourse c =(ScheduleCourse) allOrderedElementsTwo.get(i);
				 s = c.toString() + coorespondingDatesTwo.get(i).toString();
			}
			else if(allOrderedElementsTwo.get(i) instanceof Requirement){
				Requirement c =(Requirement) allOrderedElementsTwo.get(i);
				 s = c.getDisplayString() + coorespondingDatesTwo.get(i).toString();
			
			}
			
			twoTotalString.add(s);
		}
		
		for(String oneS: oneTotalString){
			int counter = 0;
			System.out.println(oneS);
			for(String twoS: twoTotalString){
				if(oneS.equals(twoS)){
					counter++;
				}
			}
			if(counter>0){
				System.out.println("Same One");
			}
			else{
				System.out.println("different One");
			}



		}

		for(String twoS: twoTotalString){
			int counter = 0;
			System.out.println(twoS);
			for(String oneS: oneTotalString){
				if(twoS.equals(oneS)){
					counter++;
				}
			}
			if(counter>0){
				System.out.println("Same Two ");
			}
			else{
				System.out.println("different Two");
			}



		}




	}
}
