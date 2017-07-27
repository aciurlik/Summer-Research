

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.time.LocalDate;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * 
 * Blurb written 7/26/2017
 * Last updated 7/26/2017
 * 
 * This is the highest level of code.
 * It holds all of the scheduleGUIs 
 * that the program has made. It is 
 * also holds the main method run by the
 * program. If any action is not specific 
 * to a schedule/schedule, or an entity that is smaller
 * than either of those it should reside in this class. 
 * 
 *
 */
public class Driver {

	static ArrayList<ScheduleGUI> listOfScheduleGUIs; //This allows multiple schedules to be opened 
	//without them interacting with each other.
	static StartUpMenu startUP = null;//This stores the startup object, because inital loading is time 
	//consuming. 

	
	
	

	/**
	 * When one creates a new Schedule through initial loading of the program,
	 * or when they open a program this method is called. It adds that program to
	 * an ArrayList list of scheduleGUI's and displays it in a separate window. When 
	 * a schedule is created from scratch it replaces the current schedule, and
	 * thus this method is not called. 
	 * @param s Schedule that one wants to open. 
	 */
	public static void addScheduleGUI(Schedule s){
		
		ScheduleGUI schGUI = new ScheduleGUI(s);
		//The window listeners keep the closure of one window from causing the others to close. 
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

	/**
	 * This displays a dialog box that reminds the user that 
	 * they have the option to save
	 * @return int cooresponding to user choice. 
	 */
	public static int saveScheduleReminder(){
		String[] Options = {"Yes", "No"};

		int n = JOptionPane.showOptionDialog(null,
				"Would you like to save your schedule",
				"Save Schedule",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE,
				null,
				Options,Options[1]);
		return n;
	}
	/**
	 * This closes the schedule, and if it is the last one proceeds to 
	 * close the whole program. 
	 * @param schGUI ScheduleGUI that one wants to close. 
	 */
	public static void removeScheduleGUI(ScheduleGUI schGUI){
		listOfScheduleGUIs.remove(schGUI);
		//Asks user to save schedule
		int n = saveScheduleReminder();
		if(n==0){
			schGUI.GUISaveSchedule();
		}
		//If there are no other schedules loaded, it will proceeded to 
		//shut down the whole program. 
		if(listOfScheduleGUIs.isEmpty()){
			System.exit(0);

		}

	}

	/**
	 * When one opens a schedule it defers to FileHandler to pick the 
	 * schedule, once the user selects the schedule, as long as it is not null
	 * the schGUI is created and added to the list this class holds. If master is around
	 * the majors that were saved will be updated to their most updated status. Otherwise if
	 * a major file was updated after a schedule was saved, when this saved schedule was opened
	 * it would not reflect that change. 
	 */
	public static void openSchedule() {
		Schedule result = FileHandler.openSchedule();
		if(result!=null){
			if(FurmanOfficial.masterIsAround){
				result.reloadMajors();
			}
			Driver.addScheduleGUI(result);
		}
	}


	/**
	 * This displays a list of string to the user, and lets the user choose among them.
	 * This returns the index of the chosen string, -1 if none chosen. Strings should be unique, if not the first
	 * will be chosen. 
	 * @return the integer pointing to the user selection. 
	 */
	public static int GUIChooseAmong(ArrayList<String> displaysList, String message, String title){
		Object[] displays = displaysList.toArray(new String[displaysList.size()]);
		String chosenString = (String)JOptionPane.showInputDialog(null, message , title , JOptionPane.PLAIN_MESSAGE, null, displays, "Cats");
		int chosenIndex = 0;
		for(; chosenIndex < displays.length ; chosenIndex ++){
			if(displays[chosenIndex].equals(chosenString)){
				break;
			}
		}
		if(chosenIndex >= displays.length){
			return -1;
		}
		return chosenIndex;
	}


	/**
	 * This allows the user to pick a start semester at the start of the program. (The first blank schedule 
	 * creates this option for the user). The program then stores this answers and if another blank schedule is
	 * loaded its start date will be but what the user selected. If the user shuts down the program and then reopens it
	 * they must pick the start date again for a blank schedule. 
	 * @param semesters
	 * @return SemesterDate that will start the schedule. Null if user does not select  valid answers. 
	 */
	public static SemesterDate GUIChooseStartTime(ArrayList<SemesterDate> semesters){
		ArrayList<String> semesterStrings = new ArrayList<String>();
		ArrayList<Object> semesterObjects = new ArrayList<Object>();
		for(SemesterDate d : semesters){
			semesterStrings.add(d.getUserString());
			semesterObjects.add(d);
		}
		int index = GUIChooseAmong(semesterStrings, "Which was your first semester at Furman? ", "Pick a semester");
		if(index != -1){
			return semesters.get(index);
		}
		return null;
	}


	/**
	 * This provides six starting date options.
	 * @return The defaultFirstSemester. 
	 */
	public static SemesterDate tryPickStartDate(){
		int currentYearFall = LocalDate.now().getYear(); 
		ArrayList<SemesterDate> supportedSemesters = new ArrayList<SemesterDate>();
		for(int i=5; i>=0; i--){
			supportedSemesters.add(new SemesterDate(currentYearFall-i, SemesterDate.FALL));
		}
		//Ask the user to pick start time. 
		SemesterDate result = GUIChooseStartTime(supportedSemesters);
		
		//Sets the stored default semester to the user choice. If it is not null. 
		if(result != null){
			FileHandler.propertySet(FileHandler.startSemester, result.saveString());
		}
		return result;
	}



	/**
	 * Load anything that is needed before a schedule is made
	 */
	private static void preScheduleLoading() {
		if(FileHandler.propertyGet(MenuOptions.startUp).equals("true")){
			//This creates slides in the load. Waits to be displayed. 
			StartUpMenu start = new StartUpMenu();
			//Saves the start up because it takes time to load. 
			startUP = start;
		}

	}

	/**
	 * Opens up the StartUp help. 
	 */
	public static void startUpMessage() {
		if(startUP != null){
			startUP.showStartUp();
		}
		else{
			StartUpMenu start = new StartUpMenu();
			startUP =start;
			startUP.showStartUp();
		}
	}


	/**
	 * This disclaimer is shown at the start of the program. It is also displayed on the printout. 
	 * @return Disclaimer
	 */
	public static String getDisclaimer(){
		String instruct = " <center> <h1> THIS IS FOR PLANNING PURPOSES ONLY </h1> </center>" 
				+"<p style='width 100px;'>This tool does not officially enroll you in any courses. " 
				+ "There is no guarantee that the courses you select will be available in the semester you select them. "
				+ "There is no guarantee that the majors and requirements in this program are accurate. "
				+ "It is your responsibility to ensure that you have met the graduation requirements. "
				+ "To officially enroll in courses, you must meet with your advisor  "
				+ " and go through the course registration process via MyFurman or Enrollment Services. </p> " ;

		return instruct;
	}



	/**
	 * This the following is a work in progress it is not linked to the schGUI, the only
	 * way one can access this is by turning on master. 
	 */
	///////////////////////////////////////////////////////////////////////////
	//////////////////////UNUSED CODE/   |  ///////////////////////////////////
	//////////////////////////////////   v  /////////////////////////////////////
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
				if(s.isPriorSemester){
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
				if(s.isPriorSemester){
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
	///////////////////////////////////  ^  ///////////////////////////////////
	//////////////////////UNUSED CODE/   |  ///////////////////////////////////
	///////////////////////////////////////////////////////////////////////////

	/**
	 * This is what initiates the whole program.  
	 * It displays the disclaimer/load screen
	 * And then creates and displays the first
	 * scheduleGUI. If the setting say 
	 * to display the startUp guide it will
	 * also do that. 
	 *
	 * 
	 */
	public static void main(String[] args){
		/**
		 * This is an attempt to set the apple controlled menubar 
		 * to something other than the class name. It does not 
		 * work because we are using Java 8. On the list of things 
		 * to look into. 
		 */
		// take the menu bar off the jframe
		//System.setProperty("apple.laf.useScreenMenuBar", "true");
		// set the name of the application menu item
		//System.setProperty("com.apple.mrj.application.apple.menu.about.name", "PlanIt!");




		//This just loads FurmanOfficial into memory so that the UIManager
		// will be set before other static code gets run.		
		// If you omit this code, you'll get a nullPointerException before
		// a schedule can display.
		Color c = FurmanOfficial.lightPurple;




		//Sets up the disclaimer. 
		JPanel editorHolder = new JPanel();
		JEditorPane editorPane = new JEditorPane();
		editorPane.setPreferredSize(new Dimension(500, 250));
		editorPane.setContentType("text/html");
		editorPane.setText("<html><body>" + Driver.getDisclaimer() + "</body></html>");
		editorPane.setEditable(false);
		editorHolder.add(editorPane);

		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//This is what the user clicks to show they understand 
		//What is stated on the disclaimer. 
		JButton confirm = new JButton(MenuOptions.confirm);
		confirm.setActionCommand(MenuOptions.confirm);
		//Stays unenabled until all the loading is done.
		confirm.setEnabled(false);
		confirm.addActionListener(new ActionListener(){
			@Override 
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals(MenuOptions.confirm)){
					//Actions that occur when user is ready to start program
					frame.dispose();
					//This gets any import they had already done, and 
					//loads that schedule, or ask the user for a startDate
					//and then opens a blank schedule. 
					PriorData data = FileHandler.getSavedStudentData();
					if(data != null){
						Driver.addScheduleGUI(new Schedule(data));
					}
					else{
						if(FileHandler.propertyGet(FileHandler.startSemester) ==null &&  Driver.tryPickStartDate() == null){
							return;
						}
						else{
							Driver.addScheduleGUI(new Schedule());
						}
					}
					//Displays the startUp after the schedule is already shown,
					//hopefully enticing the user to play with the program behind it
					//while going through the tutorial. 
					if(startUP != null){
						startUP.showStartUp();
					}
				}
			}
		});

		//Finish setting up the confirm button
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(confirm);
		frame.add(buttonPanel, BorderLayout.SOUTH);

		//This adds the disclaimer to the frame.
		frame.add(editorHolder, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		//This gives the user an update on the status of loading the backround files of the program
		editorPane.setText("<html><body>" + Driver.getDisclaimer() + "<br />  <br /> Establishing Settings <br />-           "+ "</p></body></html>");
		preScheduleLoading();
		listOfScheduleGUIs = new ArrayList<ScheduleGUI>();
		editorPane.setText("<html><body>" + Driver.getDisclaimer() + "<br />  <br /> Courses Loading <br />---             "+ "</p></body></html>");
		CourseList.loadAllCourses();
		editorPane.setText("<html><body>" + Driver.getDisclaimer() + "<br />  <br /> Majors Loading <br />-----        "+ "</p></body></html>");
		FileHandler.getMajorsList();
		editorPane.setText("<html><body>" + Driver.getDisclaimer() +  "<br />  <br /> Finished Loading <br />----------- 100%"+ "</p></body></html>");
		//User can continue once it is finished loading. 
		confirm.setEnabled(true);
	}



}
