import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SemesterMenuBar extends JMenuBar implements ActionListener{


	SemesterPanel semPanel;


	public SemesterMenuBar(SemesterPanel semesterPanel) {
		
		super();
		this.removeAll();
		
		this.semPanel=semesterPanel;

		JMenu menu;
		JMenuItem menuItem; 


		//Create the menu bar.


		menu = new JMenu("Options");



		menuItem = new JMenuItem(MenuOptions.addInstruct);
		JPopupMenu addACoursePopup = new JPopupMenu();
		menuItem.addActionListener(this);
		addACoursePopup.add(menuItem);
		menu.add(menuItem);


		menuItem = new JMenuItem(MenuOptions.supriseMe);
		JPopupMenu addSuprise = new JPopupMenu();
		menuItem.addActionListener(this);
		addSuprise.add(menuItem);
		menu.add(menuItem);

		
		JMenuItem studyAway = new JMenuItem();
		if(semPanel.sem.studyAway){
		//System.out.println("REMOVE");
			studyAway.setText(MenuOptions.removeStudyAway);
			studyAway.setActionCommand(MenuOptions.removeStudyAway);
		}
		else{
		//	System.out.println("ADD");
			studyAway.setText(MenuOptions.addStudyAway);
			studyAway.setActionCommand(MenuOptions.addStudyAway);
		}
		
		JPopupMenu addStudyAway = new JPopupMenu();
		studyAway.addActionListener(this);
		addStudyAway.add(studyAway);
		menu.add(studyAway);



		menuItem = new JMenuItem();
		if(semPanel.sem.hasNotes){
			menuItem.setText(MenuOptions.removeNotes);
			menuItem.setActionCommand(MenuOptions.removeNotes);
		}
		if(!semPanel.sem.hasNotes){
			menuItem.setText(MenuOptions.addNotes);
			menuItem.setActionCommand(MenuOptions.addNotes);
		}
		
		JPopupMenu notes = new JPopupMenu();
		menuItem.addActionListener(this);
		notes.add(menuItem);
		menu.add(menuItem);

		
		this.add(menu);
		
	}


	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(MenuOptions.addInstruct)){
			semPanel.d.addCourseDialogBox(semPanel.sem);
		}
		if(e.getActionCommand().equals(MenuOptions.supriseMe)){
			semPanel.d.GUISupriseWindow(semPanel.sem);
		}
		if(e.getActionCommand().equals(MenuOptions.addStudyAway)){
			semPanel.d.GUImakeSemesterStudyAway(semPanel.sem);

		}
		if(e.getActionCommand().equals(MenuOptions.removeStudyAway)){
			semPanel.d.GUIremoveSemesterStudyAway(semPanel.sem);

		}
		if(e.getActionCommand().equals(MenuOptions.addNotes)){
			semPanel.d.GUIaddNotes(semPanel.sem);
			semPanel.updatePanel(semPanel.sem);
			
			}
		if(e.getActionCommand().equals(MenuOptions.removeNotes)){
			semPanel.d.GUIremoveNotes(semPanel.sem);
		}
	}
}