
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
	ScheduleGUI d;


	public MainMenuBar(ScheduleGUI d) {
		super();
		this.d=d;
		JMenu menu, submenu;
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
		menu.add(menuItem);



		this.add(menu);



		//Build the file menu-> Add/Save Schedule
		menu = new JMenu(MenuOptions.file);
		this.add(menu);


		menuItem = new JMenuItem(MenuOptions.newSchedule,
				KeyEvent.VK_T);
		JPopupMenu newSched = new JPopupMenu(MenuOptions.newSchedule);
		menuItem.addActionListener(this);
		newSched.add(menuItem);
		menu.add(menuItem);


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

		
		//Unimplemented method will come back to 
		if(!FurmanOfficial.masterIsNotAround){
			menuItem = new JMenuItem("Compare");
			JPopupMenu compare = new JPopupMenu("Compare");
			menuItem.addActionListener(this);
			compare.add(menuItem);
			menu.add(menuItem);
		}
		
		
		//Build Edit menu in the menu bar.
		menu = new JMenu(MenuOptions.Edit);
		this.add(menu);
		menu.setMnemonic(KeyEvent.VK_N);
		menu.getAccessibleContext().setAccessibleDescription(
				"This allows edits to schedule");


		submenu = new JMenu(MenuOptions.Major);
		submenu.setMnemonic(KeyEvent.VK_S);
		menuItem = new JMenuItem(MenuOptions.addMajor);
		JPopupMenu majorPopup = new JPopupMenu();
		menuItem.addActionListener(this);
		majorPopup.add(menuItem);
		submenu.add(menuItem);



		menu.add(submenu);




		submenu = new JMenu(MenuOptions.Minor);
		menuItem = new JMenuItem(MenuOptions.addMinor);
		JPopupMenu minorPopup = new JPopupMenu();
		menuItem.addActionListener(this);
		minorPopup.add(menuItem);
		submenu.add(menuItem);


		menu.add(submenu);

		submenu = new JMenu(MenuOptions.Track);
		menuItem = new JMenuItem(MenuOptions.addTrack);
		JPopupMenu trackPopup = new JPopupMenu();
		menuItem.addActionListener(this);
		trackPopup.add(menuItem);
		submenu.add(menuItem);
		menu.add(submenu);



		menu = new JMenu(MenuOptions.settings);
		this.add(menu);

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







	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(MenuOptions.exploreMayX) || e.getActionCommand().equals(MenuOptions.exploreStudyAway) || e.getActionCommand().equals(MenuOptions.exploreInternship)){
			d.GUIOutsideLink(e.getActionCommand());
		}
		if(e.getActionCommand().equals(MenuOptions.addMayX)){
			d.GUIYearsPopUP(e.getActionCommand());
		}

		if(e.getActionCommand().equals(MenuOptions.addSummerClass)){
			d.GUIChooseSummerSession();
		}

		if(e.getActionCommand().equals(MenuOptions.addMajor) || e.getActionCommand().equals(MenuOptions.addMinor) || (e.getActionCommand().equals(MenuOptions.addTrack))){
			d.GUIPopUP(e.getActionCommand());
		}
		if(e.getActionCommand().equals(MenuOptions.newSchedule)){
			d.GUINewSchedule();
		}
		if(e.getActionCommand().equals(MenuOptions.openSchedule)){
			Driver.openSchedule();
		}
		if(e.getActionCommand().equals(MenuOptions.saveSchedule)){
			d.GUISaveSchedule();
		}
		if(e.getActionCommand().equals(MenuOptions.printSchedule)){

			d.GUIPrintSchedule();
		}

		if(e.getActionCommand().equals(MenuOptions.viewStartUp)){
			Driver.startUpMessage();
		}
		if(e.getActionCommand().equals(MenuOptions.checkAllErrors)){
			d.GUICheckAllErrors(true);
		}
		if(e.getActionCommand().equals(MenuOptions.deleteSchedule)){
			FileHandler.deleteSchedule();
		}
		if(e.getActionCommand().equals(MenuOptions.changeSettings)){
			FileHandler.showSetting();
		}
		if(e.getActionCommand().equals(MenuOptions.restoreDefault)){
			FileHandler.GUICalledRestoreDefaultSettings();
		}
		if(e.getActionCommand().equals(MenuOptions.examineRequirementHelp)){
			d.showExamineRequirementHelp();
		}
		if(e.getActionCommand().equals(MenuOptions.findACourse)){
			d.showFindACourseHelp();
		}
		if(e.getActionCommand().equals("Compare")){
			Driver.chooseSchedulesToCompare();
		}
	}


}


