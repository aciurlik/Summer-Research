

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class Driver {
	static ArrayList<ScheduleGUI> listOfScheduleGUIs; 
	static StartUpMenu startUP = null;



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
			if(FurmanOfficial.masterIsAround){
				result.reloadMajors();
			}

			Driver.addScheduleGUI(result);

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
		String chosenString = (String)JOptionPane.showInputDialog(null, message , title , JOptionPane.PLAIN_MESSAGE, null, displays, "Cats");
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

		SemesterDate result = GUIChooseStartTime(supportedSemesters);
		if(result == null){
			result = supportedSemesters.get(0);
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
			startUP = start;
		}

	}





	/**
	 * Opens up the StartUp help. 
	 */
	public static void startUpMessage() {
		if(startUP != null){
			startUP.showStartUp(false);
		}
		else{

			StartUpMenu start = new StartUpMenu();
			startUP =start;
			startUP.showStartUp(false);

		}
	}

	public static String getDisclaimer(){
		String instruct = " <center> <h1> THIS IS FOR PLANNING PURPOSES ONLY </h1> </center>" 
				+"<p style='width 100px;'>This tool does not officially enroll you in any courses. " 
				+ "There is no guarantee that the courses you select will be available in the semester you select them. "
				+ "There is no guarantee that the majors and requirements in this program are accurate."
				+ "It is your responsibility to ensure that you have met the graduation requirements. "
				+ "To officially enroll in courses, you must meet with your advisor  "
				+ " and go through the course registration process via MyFurman or Enrollment Services. </p> " ;

		return instruct;
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


	public static void main(String[] args){
		//This just loads FurmanOfficial into memory so that the UIManager
		// will be set before other static code gets run.		
		Color c = FurmanOfficial.grey;



		//SemesterDate start = tryPickStartDate();

		/*
		if(start == null){
			//this will close any running code, including the JOptionPanes which don't get collected by 
			// the garbage collector for some reason.
{			System.exit(0);
			return;
		}*/

		//Schedule.defaultFirstSemester = start;
		//new Driver();
		//Driver.importPriorCourses();
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


		JButton confirm = new JButton(MenuOptions.confirm);
		confirm.setActionCommand(MenuOptions.confirm);
		confirm.setEnabled(false);
		confirm.addActionListener(new ActionListener(){
			@Override 
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals(MenuOptions.confirm)){
					frame.dispose();
					PriorData data = FileHandler.getSavedStudentData();
					if(data != null){
						Driver.addScheduleGUI(new Schedule(data));
					}
					else{
						Driver.addScheduleGUI(new Schedule());
					}
					if(startUP != null){
						startUP.showStartUp(true);

					}
				}


			}
		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(confirm);
		frame.add(buttonPanel, BorderLayout.SOUTH);

		frame.add(editorHolder, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		editorPane.setText("<html><body>" + Driver.getDisclaimer() + "<br />  <br /> Establishing Settings <br />-           "+ "</p></body></html>");
		preScheduleLoading();
		listOfScheduleGUIs = new ArrayList<ScheduleGUI>();
		editorPane.setText("<html><body>" + Driver.getDisclaimer() + "<br />  <br /> Courses Loading <br />---             "+ "</p></body></html>");
		CourseList.loadAllCourses();
		editorPane.setText("<html><body>" + Driver.getDisclaimer() + "<br />  <br /> Majors Loading <br />-----        "+ "</p></body></html>");
		FileHandler.getMajorsList();
		editorPane.setText("<html><body>" + Driver.getDisclaimer() +  "<br />  <br /> Finished Loading <br />----------- 100%"+ "</p></body></html>");
		confirm.setEnabled(true);
	}



}
