
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.ButtonGroup;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.ImageIcon;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JFrame;
import javax.swing.JList;

/* 
 * 
 * http://docs.oracle.com/javase/tutorial/uiswing/examples/components/MenuLookDemoProject/src/components/MenuLookDemo.java
 */



public class MainMenuBar extends JMenuBar implements ActionListener, java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextArea output;
	JScrollPane scrollPane;
	ScheduleGUI schGUI;
	static JMenu newSchedule;
	static JMenuItem importPriorSchedule;
	static JMenuItem blankSchedule;


	public MainMenuBar(ScheduleGUI schGUI) {
		super();
		this.schGUI=schGUI;
		JMenu menu, submenu, subTwomenu;
		JMenuItem menuItem;




		//Create the menu bar.


		menu = new JMenu(MenuOptions.FurmanAdvantage);
		
		

		submenu = new JMenu(MenuOptions.MayX);
		menuItem = new JMenuItem(MenuOptions.exploreMayX);
		JPopupMenu exploreMayXPopup = new JPopupMenu();
		menuItem.addActionListener(this);
		exploreMayXPopup.add(menuItem);
		submenu.add(menuItem);


		menuItem = new JMenuItem(MenuOptions.addMayX);
		JPopupMenu addMayX = new JPopupMenu();
		menuItem.addActionListener(this);
		addMayX.add(menuItem);
		submenu.add(menuItem);

		menu.add(submenu);


		submenu = new JMenu(MenuOptions.StudyAbroad);

		menuItem = new JMenuItem(MenuOptions.exploreStudyAway);
		JPopupMenu exploreStudyAway = new JPopupMenu();
		menuItem.addActionListener(this);
		exploreStudyAway.add(menuItem);
		submenu.add(menuItem);

		menu.add(submenu);


		menuItem = new JMenuItem(MenuOptions.addSummerClass);
		JPopupMenu addSummer = new JPopupMenu();
		menuItem.addActionListener(this);
		addSummer.add(menuItem);
		menu.add(menuItem);



		menuItem = new JMenuItem(MenuOptions.exploreInternship);
		JPopupMenu exploreInternship = new JPopupMenu();
		menuItem.addActionListener(this);
		exploreInternship.add(menuItem);
		System.out.println(menu.getName());
		
		menu.add(menuItem);
		
		this.add(menu);


		
		
		

		//Build the file menu-> Add/Save Schedule
		menu = new JMenu(MenuOptions.file);
		


		newSchedule = new JMenu(MenuOptions.newSchedule);
		newSchedule.setMnemonic(KeyEvent.VK_S);
		blankSchedule = new JMenuItem(MenuOptions.newBlankSchedule);
		JPopupMenu newBlank = new JPopupMenu();
		blankSchedule.addActionListener(this);
		newBlank.add(blankSchedule);
		newSchedule.add(blankSchedule);



		String s = FileHandler.getSavedStudentData();

		importPriorSchedule = new JMenuItem(MenuOptions.newLoadedSchedule);
		JPopupMenu newLoaded = new JPopupMenu();
		importPriorSchedule.addActionListener(this);
		newLoaded.add(importPriorSchedule);
		if(s != null){
			newSchedule.add(importPriorSchedule);

		}
		
		menu.add(newSchedule);




		menuItem = new JMenuItem(MenuOptions.openSchedule);
		JPopupMenu openSched = new JPopupMenu(MenuOptions.newSchedule);
		menuItem.addActionListener(this);
		openSched.add(menuItem);
		menu.add(menuItem);





		menuItem = new JMenuItem(MenuOptions.saveSchedule);
		JPopupMenu	saveSched = new JPopupMenu(MenuOptions.saveSchedule);
		menuItem.addActionListener(this);
		saveSched.add(menuItem);
		menu.add(menuItem);



		menuItem = new JMenuItem(MenuOptions.printSchedule);
		JPopupMenu	printSched = new JPopupMenu(MenuOptions.printSchedule);
		menuItem.addActionListener(this);
		printSched.add(menuItem);
		menu.add(menuItem);

		menuItem = new JMenuItem(MenuOptions.deleteSchedule);
		JPopupMenu deleteSched = new JPopupMenu(MenuOptions.deleteSchedule);
		menuItem.addActionListener(this);
		deleteSched.add(menuItem);
		menu.add(menuItem);


		menuItem = new JMenuItem(MenuOptions.checkAllErrors);
		JPopupMenu allErrors = new JPopupMenu(MenuOptions.checkAllErrors);
		menuItem.addActionListener(this);
		allErrors.add(menuItem);
		menu.add(menuItem);

		submenu = new JMenu(MenuOptions.loadPriorCourses);
		
		
		subTwomenu = new JMenu(MenuOptions.student);
		
		
		
		menuItem = new JMenuItem(MenuOptions.dragAndDrop);
		menuItem.setActionCommand(MenuOptions.studentDnD);
		JPopupMenu loadPrior = new JPopupMenu();
		menuItem.addActionListener(this);
		loadPrior.add(menuItem);
		subTwomenu.add(menuItem);
		
		submenu.add(subTwomenu);
		
		subTwomenu = new JMenu(MenuOptions.advisor);
		menuItem = new JMenuItem(MenuOptions.dragAndDrop);
		menuItem.setActionCommand(MenuOptions.advisorDnD);
		JPopupMenu loadPriorAdvisor = new JPopupMenu();
		menuItem.addActionListener(this);
		loadPriorAdvisor.add(menuItem);
		subTwomenu.add(menuItem);
		submenu.add(subTwomenu);

		
		menuItem = new JMenuItem(MenuOptions.downloadcsv);
		JPopupMenu csv = new JPopupMenu();
		menuItem.addActionListener(this);
		csv.add(menuItem);
		subTwomenu.add(menuItem);
		
		
		submenu.add(subTwomenu);
		
		
		
		
	
		
		
		
		menu.add(submenu);

		//Unimplemented method will come back to 
		if(FurmanOfficial.masterIsAround){
			menuItem = new JMenuItem("Compare");
			JPopupMenu compare = new JPopupMenu("Compare");
			menuItem.addActionListener(this);
			compare.add(menuItem);
			menu.add(menuItem);
		}

		this.add(menu);
		
		
		//Build Edit menu in the menu bar.
		menu = new JMenu(MenuOptions.Edit);
		
		menu.setMnemonic(KeyEvent.VK_N);
		menu.getAccessibleContext().setAccessibleDescription(
				"This allows edits to schedule");


		menuItem = new JMenuItem(MenuOptions.addMajor);
		JPopupMenu majorPopup = new JPopupMenu();
		menuItem.addActionListener(this);
		majorPopup.add(menuItem);
		



		menu.add(menuItem);




		
		menuItem = new JMenuItem(MenuOptions.addMinor);
		JPopupMenu minorPopup = new JPopupMenu();
		menuItem.addActionListener(this);
		minorPopup.add(menuItem);
		

		menu.add(menuItem);

	
		menuItem = new JMenuItem(MenuOptions.addTrack);
		JPopupMenu trackPopup = new JPopupMenu();
		menuItem.addActionListener(this);
		trackPopup.add(menuItem);
		
		menu.add(menuItem);


		this.add(menu);
		
		
		menu = new JMenu(MenuOptions.settings);
		

		menuItem = new JMenuItem(MenuOptions.changeSettings);
		menuItem.setActionCommand(MenuOptions.changeSettings);
		JPopupMenu settings =new JPopupMenu();
		settings.add(menuItem);
		menuItem.addActionListener(this);
		menu.add(menuItem);



		menuItem = new JMenuItem(MenuOptions.restoreDefault);
		menuItem.setActionCommand(MenuOptions.restoreDefault);
		JPopupMenu restoreDefault = new JPopupMenu();
		restoreDefault.add(menuItem);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		this.add(menu);


		menu = new JMenu(MenuOptions.help);
		menuItem = new JMenuItem(MenuOptions.viewStartUp);
		menuItem.setActionCommand(MenuOptions.viewStartUp);
		//JPopupMenu viewStartUp = new JPopupMenu(MenuOptions.viewStartUp);
		menuItem.addActionListener(this);
		//viewStartUp.add(menuItem);
		menu.add(menuItem);

		menuItem = new JMenuItem(MenuOptions.examineRequirementHelp);
		menuItem.setActionCommand(MenuOptions.examineRequirementHelp);
		//new JPopupMenu().add(menuItem);
		menuItem.addActionListener(this);
		menu.add(menuItem);

		menuItem = new JMenuItem(MenuOptions.findACourse);
		menuItem.setActionCommand(MenuOptions.findACourse);
		menuItem.addActionListener(this);
		menu.add(menuItem);


		this.add(menu);





	}



	public Container createContentPane() {
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);
		return contentPane;
	}


	public static void addImportScheduleOption(){
		if(newSchedule != null){
			newSchedule.removeAll();
			newSchedule.add(blankSchedule);
			newSchedule.add(importPriorSchedule);

		}
	}




	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(MenuOptions.exploreMayX) || e.getActionCommand().equals(MenuOptions.exploreStudyAway) || e.getActionCommand().equals(MenuOptions.exploreInternship)){
			schGUI.GUIOutsideLink(e.getActionCommand());
		}
		else if(e.getActionCommand().equals(MenuOptions.addMayX)){
			schGUI.GUIYearsPopUP(e.getActionCommand());
		}

		else if(e.getActionCommand().equals(MenuOptions.addSummerClass)){
			schGUI.GUIChooseSummerSession();
		}

		else if(e.getActionCommand().equals(MenuOptions.addMajor) || e.getActionCommand().equals(MenuOptions.addMinor) || (e.getActionCommand().equals(MenuOptions.addTrack))){
			schGUI.GUIPopUP(e.getActionCommand());
		}
		else if(e.getActionCommand().equals(MenuOptions.newBlankSchedule) || e.getActionCommand().equals(MenuOptions.newLoadedSchedule)){
			schGUI.GUINewSchedule(e.getActionCommand());
		}
		else if(e.getActionCommand().equals(MenuOptions.openSchedule)){
			Driver.openSchedule();
		}
		else if(e.getActionCommand().equals(MenuOptions.saveSchedule)){
			schGUI.GUISaveSchedule();
		}
		else if(e.getActionCommand().equals(MenuOptions.printSchedule)){

			schGUI.GUIPrintSchedule();
		}
		else if(e.getActionCommand().equals(MenuOptions.viewStartUp)){
			Driver.startUpMessage();
		}
		else if(e.getActionCommand().equals(MenuOptions.checkAllErrors)){
			schGUI.GUICheckAllErrors(true);
		}
		else if(e.getActionCommand().equals(MenuOptions.studentDnD) ){
			schGUI.importPriorCourses(true);
		}
		else if(e.getActionCommand().equals(MenuOptions.advisorDnD) ){
			schGUI.importPriorCourses(false);
		}
		else if(e.getActionCommand().equals(MenuOptions.deleteSchedule)){
			FileHandler.deleteSchedule();
		}
		else if(e.getActionCommand().equals(MenuOptions.changeSettings)){
			FileHandler.showSetting();
		}
		else if(e.getActionCommand().equals(MenuOptions.restoreDefault)){
			FileHandler.GUICalledRestoreDefaultSettings();
		}
		else if(e.getActionCommand().equals(MenuOptions.examineRequirementHelp)){
			schGUI.showExamineRequirementHelp();
		}
		else if(e.getActionCommand().equals(MenuOptions.findACourse)){
			schGUI.showFindACourseHelp();
		}
		else if(e.getActionCommand().equals("Compare")){
			Driver.chooseSchedulesToCompare();
		}
	}


}


