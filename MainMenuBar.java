

import java.awt.event.*;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;

/**
 * Blurb written 7/24/2017
 * Last updated 7/24/2017
 * 
 *This is the main menu bar that is on the north section of the JFrame.  
 *The class is structured to mirror the look of the menubar, the more indented
 *the more menus the user must click through to reach the menuItem. 
 *
 */


public class MainMenuBar extends JMenuBar implements ActionListener {
	private static final long serialVersionUID = 1L;
	ScheduleGUI schGUI;
	static JMenu newSchedule; //The following are fields
	static JMenuItem importPriorSchedule; // because they can be updated 
	static JMenuItem blankSchedule;//based on the scheduleGUI's status


	public MainMenuBar(ScheduleGUI schGUI) {
		super();
		this.schGUI=schGUI;
		JMenu menu, submenu, subTwomenu; //The branch of the tree,
		// the longer the name the more menues connect it with the menubar 
		JMenuItem menuItem;//The leaf of the tree where the actionListener is

		
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

		
		//Build the file menu-> Add/Save Schedule
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
		
		menu = new JMenu(MenuOptions.settings);
			menuItem = new JMenuItem(MenuOptions.changeSettings);
			menuItem.addActionListener(this);
		menu.add(menuItem);
			menuItem = new JMenuItem(MenuOptions.restoreDefault);
			menuItem.addActionListener(this);
		menu.add(menuItem);
		this.add(menu);

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
		if(e.getActionCommand().equals(MenuOptions.exploreMayX) || e.getActionCommand().equals(MenuOptions.exploreStudyAway) || e.getActionCommand().equals(MenuOptions.exploreInternship)){
			schGUI.GUIOutsideLink(e.getActionCommand());
		}
		else if(e.getActionCommand().equals(MenuOptions.addMayX)){
			schGUI.addMayX();
		}
		else if(e.getActionCommand().equals(MenuOptions.addSummerClass)){
			schGUI.addSummerSession();
		}
		else if(e.getActionCommand().equals(MenuOptions.addMajor) || e.getActionCommand().equals(MenuOptions.addMinor) || (e.getActionCommand().equals(MenuOptions.addTrack))){
			schGUI.GUIMajorPopUP(e.getActionCommand());
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
			schGUI.GUICheckAllErrors();
		}
		else if(e.getActionCommand().equals(MenuOptions.studentDnD) ){
			schGUI.importPriorCourses(true);
		}
		else if(e.getActionCommand().equals(MenuOptions.advisorDnD) ){
			schGUI.importPriorCourses(false);
		}
		else if (e.getActionCommand().equals(MenuOptions.downloadcsv)){
			schGUI.tryImportPriorCoursesViaFile();
		}
		else if(e.getActionCommand().equals(MenuOptions.deleteSchedule)){
			FileHandler.deleteSchedule();
		}
		else if(e.getActionCommand().equals(MenuOptions.changeSettings)){
			FileHandler.showSetting();
		}
		else if(e.getActionCommand().equals(MenuOptions.restoreDefault)){
			FileHandler.requestRestoreDefaultSettings();
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

/**
 * Test
 * Make sure every menuItem is activated when clicked. If it does act as expected problem
 * resides in other class. 
 */

