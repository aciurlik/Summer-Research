import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Driver {

	Schedule sch;
	SchedulePanel schP;
	RequirementListPanel reqs;




	public Driver(){
		
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
		System.out.println("works");
		this.update();
		
	}
	
	public void GUIPopUP(String s){
		ExtrasAddList list = new ExtrasAddList(s, this, sch);
	}
	
	public ArrayList<Major> GUIRemoveDuplicates(ArrayList<Major> collectionOfMajors) {
		return sch.removeAlreadyChosenMajors(collectionOfMajors);
		
	
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


	public static void main(String[] args){
		new Driver();


	}



	

	








}



