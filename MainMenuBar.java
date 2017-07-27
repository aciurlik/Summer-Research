

import java.awt.event.*;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;

/**
 * Blurb written 7/24/2017
 * Last updated 7/27/2017
 * 
 *This is the main menu bar that is on the north section of the JFrame.  
 *The class is indented to mirror the look of the menubar, the more indented
 *the more menus the user must click through to reach the menuItem. 
 *
 */


public class MainMenuBar extends JMenuBar implements ActionListener {
	private static final long serialVersionUID = 1L;
	ScheduleGUI schGUI;
	static JMenu newSchedule; //The following are fields
	static JMenuItem importPriorSchedule; // because they can be updated 
	static JMenuItem blankSchedule;//based on the scheduleGUI's status

	
	static boolean testing = false;

	public MainMenuBar(ScheduleGUI schGUI) {
		super();
		this.schGUI=schGUI;
		JMenu menu, submenu, subTwomenu; //The length
		// of this name tells how deep in the menu tree this
		// menu is. A menu is visible without clicking anything,
		// a submenu is visible on one click, and a subTwoMenu requires
		// two or more clicks to be visible to the user. None of these
		// objects may be leaves in the menu tree.
		JMenuItem menuItem;//The leaf of the tree where the actionListener is

		
		
		//Build the "The Furman Advantage" menu
		menu = new JMenu(MenuOptions.FurmanAdvantage);
			submenu = new JMenu(MenuOptions.MayX);
				menuItem = new JMenuItem(MenuOptions.exploreMayX);
				menuItem.addActionListener(this);
			submenu.add(menuItem);
				menuItem = new JMenuItem(MenuOptions.addMayX);
				menuItem.addActionListener(this);
			submenu.add(menuItem);
		menu.add(submenu);
		
			menuItem = new JMenuItem(MenuOptions.exploreStudyAway);
			menuItem.addActionListener(this);
		menu.add(menuItem);

			menuItem = new JMenuItem(MenuOptions.addSummerClass);
			menuItem.addActionListener(this);
		menu.add(menuItem);

			menuItem = new JMenuItem(MenuOptions.exploreInternship);
			menuItem.addActionListener(this);
		menu.add(menuItem);
		this.add(menu);

		
		//Build the "File" menu-> Add/Save Schedule
		menu = new JMenu(MenuOptions.file);
			newSchedule = new JMenu(MenuOptions.newSchedule);
				blankSchedule = new JMenuItem(MenuOptions.newBlankSchedule);
				blankSchedule.addActionListener(this);
			newSchedule.add(blankSchedule);

				PriorData s = FileHandler.getSavedStudentData();

			importPriorSchedule = new JMenuItem(MenuOptions.newLoadedSchedule);
				importPriorSchedule.addActionListener(this);
			if(s != null){
			newSchedule.add(importPriorSchedule);
			}
		menu.add(newSchedule);

			menuItem = new JMenuItem(MenuOptions.openSchedule);
			menuItem.addActionListener(this);
		menu.add(menuItem);

			menuItem = new JMenuItem(MenuOptions.saveSchedule);
			menuItem.addActionListener(this);
		menu.add(menuItem);

			menuItem = new JMenuItem(MenuOptions.printSchedule);
			menuItem.addActionListener(this);
		menu.add(menuItem);

			menuItem = new JMenuItem(MenuOptions.deleteSchedule);
			menuItem.addActionListener(this);
		menu.add(menuItem);

			menuItem = new JMenuItem(MenuOptions.checkAllErrors);
			menuItem.addActionListener(this);
		menu.add(menuItem);

			submenu = new JMenu(MenuOptions.loadPriorCourses);
				subTwomenu = new JMenu(MenuOptions.student);
					menuItem = new JMenuItem(MenuOptions.dragAndDrop);
					menuItem.setActionCommand(MenuOptions.studentDnD);
					menuItem.addActionListener(this);
				subTwomenu.add(menuItem);
			submenu.add(subTwomenu);
		
				subTwomenu = new JMenu(MenuOptions.advisor);
					menuItem = new JMenuItem(MenuOptions.dragAndDrop);
					menuItem.setActionCommand(MenuOptions.advisorDnD);
					menuItem.addActionListener(this);
				subTwomenu.add(menuItem);
			submenu.add(subTwomenu);

		
					menuItem = new JMenuItem(MenuOptions.downloadcsv);
					menuItem.addActionListener(this);
				subTwomenu.add(menuItem);
			submenu.add(subTwomenu);
		menu.add(submenu);

		//This is a feature still in construction.
		//This will only appear if the programmer's view
		//is set to true. 
		if(FurmanOfficial.masterIsAround){
			menuItem = new JMenuItem("Compare");
			JPopupMenu compare = new JPopupMenu("Compare");
			menuItem.addActionListener(this);
			compare.add(menuItem);
			menu.add(menuItem);
		}
		this.add(menu);
		
		//Build the Edit menu
		menu = new JMenu(MenuOptions.Edit);
			menuItem = new JMenuItem(MenuOptions.addMajor);
			menuItem.addActionListener(this);
		menu.add(menuItem);
			menuItem = new JMenuItem(MenuOptions.addMinor);
			menuItem.addActionListener(this);
		menu.add(menuItem);
			menuItem = new JMenuItem(MenuOptions.addTrack);
			menuItem.addActionListener(this);
		menu.add(menuItem);
		this.add(menu);
		
		//Build the Settings menu
		menu = new JMenu(MenuOptions.settings);
			menuItem = new JMenuItem(MenuOptions.changeSettings);
			menuItem.addActionListener(this);
		menu.add(menuItem);
			menuItem = new JMenuItem(MenuOptions.restoreDefault);
			menuItem.addActionListener(this);
		menu.add(menuItem);
		this.add(menu);

		//Build the Help menu
		menu = new JMenu(MenuOptions.help);
			menuItem = new JMenuItem(MenuOptions.viewStartUp);
			menuItem.addActionListener(this);
		menu.add(menuItem);
			menuItem = new JMenuItem(MenuOptions.examineRequirementHelp);	
			menuItem.addActionListener(this);
		menu.add(menuItem);
			menuItem = new JMenuItem(MenuOptions.findACourse);
			menuItem.addActionListener(this);
		menu.add(menuItem);
		this.add(menu);
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
		String command = e.getActionCommand();
		if(testing){
			System.out.println("Clicked on " + command);
		}
		

		if(command.equals(MenuOptions.exploreMayX) || command.equals(MenuOptions.exploreStudyAway) ||command.equals(MenuOptions.exploreInternship)){
			schGUI.linkToWebpage(command);
		}
		else if(command.equals(MenuOptions.addMayX)){
			schGUI.addMayX();
		}
		else if(command.equals(MenuOptions.addSummerClass)){
			schGUI.addSummerSession();
		}
		else if(command.equals(MenuOptions.addMajor) || command.equals(MenuOptions.addMinor) || (command.equals(MenuOptions.addTrack))){
			schGUI.addMajor(e.getActionCommand());

		}
		else if(command.equals(MenuOptions.newBlankSchedule) || command.equals(MenuOptions.newLoadedSchedule)){
			schGUI.GUINewSchedule(command);
		}
		else if(command.equals(MenuOptions.openSchedule)){
			Driver.openSchedule();
		}
		else if(command.equals(MenuOptions.saveSchedule)){
			schGUI.GUISaveSchedule();
		}
		else if(command.equals(MenuOptions.printSchedule)){

			schGUI.GUIPrintSchedule();
		}
		else if(command.equals(MenuOptions.viewStartUp)){
			Driver.startUpMessage();
		}
		else if(command.equals(MenuOptions.checkAllErrors)){
			schGUI.GUICheckAllErrors();
		}
		else if(command.equals(MenuOptions.studentDnD) ){
			schGUI.importPriorCourses(true);
		}
		else if(command.equals(MenuOptions.advisorDnD) ){
			schGUI.importPriorCourses(false);
		}
		else if (command.equals(MenuOptions.downloadcsv)){
			schGUI.tryImportPriorCoursesViaFile();
		}
		else if(command.equals(MenuOptions.deleteSchedule)){
			FileHandler.deleteSchedule();
		}
		else if(command.equals(MenuOptions.changeSettings)){
			FileHandler.showSetting();
		}
		else if(command.equals(MenuOptions.restoreDefault)){
			FileHandler.requestRestoreDefaultSettings();
		}
		else if(command.equals(MenuOptions.examineRequirementHelp)){
			schGUI.showExamineRequirementHelp();
		}
		else if(command.equals(MenuOptions.findACourse)){
			schGUI.showFindACourseHelp();
		}
		else if(command.equals("Compare")){
			Driver.chooseSchedulesToCompare();
		}
	}


	/**
	 * Test
	 * Make sure every menuItem is activated when clicked. 
	 */
	public static void test(){
		testing = true;
	}
}



