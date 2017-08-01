import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class SemesterMenuBar extends JMenuBar implements ActionListener{


	/**
	 * 
	 * Blurb written 7/31/2017
	 * Last updated 7/31/2017
	 * When a user picks Options this is what is shown. It adjust based on the state of the 
	 * Semester concerning notes, and studyAway. 
	 */
	SemesterPanel semPanel;


	public SemesterMenuBar(SemesterPanel semesterPanel) {
		super();
		this.semPanel=semesterPanel;

		JMenu menu;
		JMenuItem menuItem; 


		//Create the menu bar.
		menu = new JMenu(MenuOptions.Options);
		menuItem = new JMenuItem(MenuOptions.addInstruct);//Add a course
		menuItem.addActionListener(this);
		menu.add(menuItem);


		menuItem = new JMenuItem(MenuOptions.supriseMe);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		
		JMenuItem studyAway = new JMenuItem();
		if(semPanel.sem.studyAway){
			studyAway.setText(MenuOptions.removeStudyAway);
			studyAway.setActionCommand(MenuOptions.removeStudyAway);
		}
		else{
			studyAway.setText(MenuOptions.addStudyAway);
			studyAway.setActionCommand(MenuOptions.addStudyAway);
		}
		studyAway.addActionListener(this);
		menu.add(studyAway);

		menuItem = new JMenuItem();
		if(semPanel.sem.hasNotes()){
			menuItem.setText(MenuOptions.removeNotes);
			menuItem.setActionCommand(MenuOptions.removeNotes);
		}
		if(!semPanel.sem.hasNotes()){
			menuItem.setText(MenuOptions.addNotes);
			menuItem.setActionCommand(MenuOptions.addNotes);
		}
		menuItem.addActionListener(this);
		menu.add(menuItem);

		
		this.add(menu);
		
	}


	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if(command.equals(MenuOptions.addInstruct)){
			semPanel.schGUI.addCourseTo(semPanel.sem);
		}
		if(command.equals(MenuOptions.supriseMe)){
			semPanel.schGUI.createSurpriseWindow(semPanel.sem);
		}
		if(command.equals(MenuOptions.addStudyAway)){
			semPanel.schGUI.makeSemesterStudyAway(semPanel.sem);

		}
		if(command.equals(MenuOptions.removeStudyAway)){
			semPanel.schGUI.removeSemesterStudyAway(semPanel.sem);

		}
		if(command.equals(MenuOptions.addNotes)){
			semPanel.schGUI.addNotesToSemester(semPanel.sem);
			semPanel.updatePanel(semPanel.sem);
			
			}
		if(command.equals(MenuOptions.removeNotes)){
			semPanel.schGUI.removeNotesFromSemester(semPanel.sem);
		}
	}
}