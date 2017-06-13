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


	public SemesterMenuBar(SemesterPanel semesterPanel, JPanel j) {
		super();
		this.semPanel=semesterPanel;

		JMenu menu, submenu;
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

		
		menuItem = new JMenuItem();
		if(!semPanel.sem.studyAway){
			menuItem.setText(MenuOptions.addStudyAway);
			menuItem.setActionCommand(MenuOptions.addStudyAway);
		}
		if(semPanel.sem.studyAway){
			menuItem.setText(MenuOptions.removeStudyAway);
			menuItem.setActionCommand(MenuOptions.removeStudyAway);
		}
		JPopupMenu addStudyAway = new JPopupMenu();
		menuItem.addActionListener(this);
		addStudyAway.add(menuItem);
		menu.add(menuItem);


		menuItem = new JMenuItem(MenuOptions.addNotes);
		JPopupMenu addNotes = new JPopupMenu();
		menuItem.addActionListener(this);
		addNotes.add(menuItem);
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
			//JTextField notes = new JTextField();
			//semPanel.defaultPanel.add(notes);		
			}
	}
}