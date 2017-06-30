
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
	Driver d;


	public MainMenuBar(Driver d) {
		super();
		this.d=d;
		JMenu menu, submenu;
		JMenuItem menuItem;




		//Create the menu bar.


		menu = new JMenu("The Furman Advantage");
		submenu = new JMenu("MayX");


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


		submenu = new JMenu("Study Abroad");

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



		menuItem = new JMenuItem(MenuOptions.addInternship);
		JPopupMenu exploreInternship = new JPopupMenu();
		menuItem.addActionListener(this);
		exploreInternship.add(menuItem);
		menu.add(menuItem);



		this.add(menu);



		//Build the file menu-> Add/Save Schedule
		menu = new JMenu("File");
		this.add(menu);
	
		
		menuItem = new JMenuItem("New Schedule",
				KeyEvent.VK_T);
		JPopupMenu newSched = new JPopupMenu(MenuOptions.newSchedule);
		menuItem.addActionListener(this);
		newSched.add(menuItem);
		menu.add(menuItem);
		
		/**
		 *
		 * 
		 * 
		 * 
		 * 	menuItem = new JMenuItem("Save Schedule");
		JPopupMenu	saveSched = new JPopupMenu(MenuOptions.saveSchedule);
		menuItem.addActionListener(this);
		saveSched.add(menuItem);
		menu.add(menuItem);
		
	
		

		menuItem = new JMenuItem("Print Schedule");
		JPopupMenu	printSched = new JPopupMenu(MenuOptions.printSchedule);
		menuItem.addActionListener(this);
		saveSched.add(menuItem);
		menu.add(menuItem);
		 */
		
		
		 menuItem = new JMenuItem(MenuOptions.openSchedule);
			JPopupMenu openSched = new JPopupMenu(MenuOptions.newSchedule);
			menuItem.addActionListener(this);
			openSched.add(menuItem);
			menu.add(menuItem);
		
		
		
		menuItem = new JMenuItem("Save Schedule");
		JPopupMenu	saveSched = new JPopupMenu(MenuOptions.saveSchedule);
		menuItem.addActionListener(this);
		saveSched.add(menuItem);
		menu.add(menuItem);
		
		
		
		menuItem = new JMenuItem("Print Schedule");
		JPopupMenu	printSched = new JPopupMenu(MenuOptions.printSchedule);
		menuItem.addActionListener(this);
		printSched.add(menuItem);
		menu.add(menuItem);
		
		
		
		menuItem = new JMenuItem("Check all Errors");
		JPopupMenu allErrors = new JPopupMenu(MenuOptions.checkAllErrors);
		menuItem.addActionListener(this);
		allErrors.add(menuItem);
		menu.add(menuItem);




		//Build Edit menu in the menu bar.
		menu = new JMenu("Edit");
		menu.setMnemonic(KeyEvent.VK_N);
		menu.getAccessibleContext().setAccessibleDescription(
				"This allows edits to schedule");
		this.add(menu);


		submenu = new JMenu("Major");
		submenu.setMnemonic(KeyEvent.VK_S);
		menuItem = new JMenuItem(MenuOptions.addMajor);
		JPopupMenu majorPopup = new JPopupMenu();
		menuItem.addActionListener(this);
		majorPopup.add(menuItem);
		submenu.add(menuItem);



		menu.add(submenu);




		submenu = new JMenu("Minor");
		menuItem = new JMenuItem(MenuOptions.addMinor);
		JPopupMenu minorPopup = new JPopupMenu();
		menuItem.addActionListener(this);
		minorPopup.add(menuItem);
		submenu.add(menuItem);


		menu.add(submenu);

		submenu = new JMenu("Track");
		menuItem = new JMenuItem(MenuOptions.addTrack);
		JPopupMenu trackPopup = new JPopupMenu();
		menuItem.addActionListener(this);
		trackPopup.add(menuItem);
		submenu.add(menuItem);

	

		menu.add(submenu);

/**
 * //Add Help
		menu = new JMenu("Help");
		JPopupMenu helpMePopup = new JPopupMenu(MenuOptions.help);
		menu.addActionListener(this);
		helpMePopup.add(menu);
		this.add(menu);

 * 
 * 
 * 
 */
		




	}



	public Container createContentPane() {
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);
		return contentPane;
	}







	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(MenuOptions.exploreMayX) || e.getActionCommand().equals(MenuOptions.exploreStudyAway) || e.getActionCommand().equals(MenuOptions.addInternship)){
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
			d.openSchedule();
		}
		if(e.getActionCommand().equals(MenuOptions.saveSchedule)){
			d.GUISaveSchedule();
		}
		if(e.getActionCommand().equals(MenuOptions.printSchedule)){
		
				d.GUIPrintSchedule();
		}
		
		if(e.getActionCommand().equals(MenuOptions.help)){
			System.out.println("HElp yourself");
		}
		if(e.getActionCommand().equals(MenuOptions.checkAllErrors)){
			d.GUICheckAllErrors(true);
		}
	}


	}


