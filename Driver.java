import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class Driver implements ActionListener{ 

	Schedule sch;
	SchedulePanel schP;
	RequirementListPanel reqs;
	int season;
	JFrame popUP = new JFrame();
	JList<Integer> pickYears;
	String seasonName;
	Integer[] yearsDialog;
	ImageIcon icon = new ImageIcon("src/Furman-logo.png");
	String instruct = "Please pick year(s) you would like to add a ";
	String headInstruct = "Please pick a course";





	public Driver() {

		//Belltower icon and scaling
		ImageIcon icon = new ImageIcon("src/bellTower.jpg");
		Image image = icon.getImage();
		Image newImage = image.getScaledInstance(100,400 , java.awt.Image.SCALE_SMOOTH);
		icon = new ImageIcon(newImage);
		JLabel belltowerLabel = new JLabel(icon);


		//Make data
		Schedule test = Schedule.testSchedule();		
		sch=test;


		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());

		//Adds the menu bar
		MainMenuBar menu = new MainMenuBar(this);
		menu.setFont(FurmanOfficial.getFont(12));
		frame.setJMenuBar(menu);
		frame.setContentPane(menu.createContentPane());

		//Adds Additions Panel and belltower
		AdditionsPanel extras = new AdditionsPanel(this);
		JPanel left = new JPanel();
		left.add(belltowerLabel);
		left.add(extras);
		frame.add(left, BorderLayout.WEST);

		schP = new SchedulePanel(test, this);
		frame.add(schP, BorderLayout.CENTER);
		reqs = new RequirementListPanel(test);
		frame.add(reqs, BorderLayout.SOUTH);

		this.update();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}

	public void GUIRequirementPanelDropped(RequirementPanel r, SemesterPanel semesterP) {
		sch.addRequirementElement(r.req, semesterP.sem);
		this.update();

	}


	public void GUIScheduleElementPanelDropped(ScheduleElementPanel p, SemesterPanel semesterPanel) {
		sch.addScheduleElement(p.getElement(), semesterPanel.sem);
		this.update();
	}


	public void GUISemesterPanelAdded(){
		sch.addNewSemester();
		this.update();

	}

	public void GUIRemoveElement(ScheduleElementPanel e, SemesterPanel semesterPanel) {
		sch.remove(e.getElement(), semesterPanel.sem);
		this.update();

	}

	public void GUIElementChanged(SemesterPanel container, ScheduleElementPanel toChange, ScheduleElement newValue){
		Semester s = container.sem;
		ScheduleElement old = toChange.getElement();
		sch.replaceElement(s, old, newValue);
		update();
	}

	public void GUIAddMajor(Major m) {
		sch.addMajor(m);
		this.update();

	}

	public void GUIPopUP(String s){
		ExtrasAddList list = new ExtrasAddList(s, this, sch);
	}

	public ArrayList<Major> GUIRemoveDuplicates(ArrayList<Major> collectionOfMajors) {
		return sch.removeAlreadyChosenMajors(collectionOfMajors);


	}
	public void GUIOutsideLink(String actionCommand) {
		try{
			if(actionCommand.equals(MenuOptions.addInternship)){

				Desktop.getDesktop().browse(new URL("http://www.furman.edu/sites/internship/FindingInternships/Pages/default.aspx").toURI());
			}
			if(actionCommand.equals(MenuOptions.addStudyAway)){
				Desktop.getDesktop().browse(new URL("https://studyaway.furman.edu/index.cfm?FuseAction=Programs.SimpleSearch").toURI());
			}
			if(actionCommand.equals(MenuOptions.addResearch)){
				Desktop.getDesktop().browse(new URL("http://www.furman.edu/sites/ur/Pages/default.aspx").toURI());
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}


	public void GUIYearsPopUP(String actionCommand){
		if(actionCommand.equals(MenuOptions.addSummerClass)){
			season= SemesterDate.SUMMER;
			seasonName = "Summer Class";
		}

		if(actionCommand.equals(MenuOptions.addMayX)){
			season= SemesterDate.MAYX;
			seasonName = "May X";
		}


		//Gets available years
		ArrayList<Integer> availableYears = new ArrayList<Integer>();
		Collections.sort(sch.semesters);

		int last = (sch.semesters.size()-1);
		int end = sch.semesters.get(last).semesterDate.year;

		for(int i=  sch.semesters.get(1).semesterDate.year; i<= end; i++){
			if ((!sch.SemesterAlreadyExists(new SemesterDate(i, season)))){

				availableYears.add(i);
			}

		}
		yearsDialog = new Integer[availableYears.size()];
		for(int i=0; i<availableYears.size(); i++){
			yearsDialog[i]= availableYears.get(i);
		}
		createDialogBox(instruct + seasonName);



	}

	public void createDialogBox(String s){
		Integer y = (Integer)JOptionPane.showInputDialog(popUP, s,  headInstruct, JOptionPane.PLAIN_MESSAGE, icon, yearsDialog, "cat" );
		if((y != null) && (y !=0)){
			sch.addNewSemesterInsideSch(y,season);
			this.update();
		}
	}

	public void updateAll(){
		schP.update(sch);
		reqs.update(sch);
	}

	public void repaintAll(){
		schP.revalidate();
		schP.repaint();

		reqs.revalidate();
		reqs.repaint();

	}



	public void update() {
		updateAll();
		repaintAll(); 

	}




	@Override
	public void actionPerformed(ActionEvent e) {
		for(int i: pickYears.getSelectedValuesList()){
			sch.addNewSemesterInsideSch(i,season);
		}
		this.update();
		popUP.dispose();
	}



	public static void main(String[] args){
		new Driver();


	}


}






















