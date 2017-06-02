
import java.awt.*;
import java.awt.event.*;
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



public class MainMenuBar extends JMenuBar implements ActionListener {
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

		menuItem = new JMenuItem(MenuOptions.addStudyAway);
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
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
				"The only menu in this program that has menu items");
		this.add(menu);
		
		menuItem = new JMenuItem("New Schedule",
				KeyEvent.VK_T);
		menuItem.setMnemonic(KeyEvent.VK_T); //used constructor instead
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Add a new Schedule");
		JPopupMenu newSched = new JPopupMenu(MenuOptions.newSchedule);
		menuItem.addActionListener(this);
		newSched.add(menuItem);
		menu.add(menuItem);
		
		
		
		
		menuItem = new JMenuItem("Save Schedule");
		menuItem.setMnemonic(KeyEvent.VK_B);
		menu.add(menuItem);
		menuItem.setMnemonic(KeyEvent.VK_D);
		menu.add(menuItem);

		menuItem = new JMenuItem("Print Schedule");
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


		menuItem = new JMenuItem(MenuOptions.removeMajor);
		JPopupMenu removeMajorPopup = new JPopupMenu(MenuOptions.removeMajor);
		menuItem.addActionListener(this);
		removeMajorPopup.add(menuItem);
		submenu.add(menuItem);

		menu.add(submenu);




		submenu = new JMenu("Minor");
		menuItem = new JMenuItem(MenuOptions.addMinor);
		JPopupMenu minorPopup = new JPopupMenu();
		menuItem.addActionListener(this);
		minorPopup.add(menuItem);
		submenu.add(menuItem);


		menuItem = new JMenuItem(MenuOptions.removeMinor);
		JPopupMenu minorRemovePopup = new JPopupMenu();
		menuItem.addActionListener(this);
		minorRemovePopup.add(menuItem);
		submenu.add(menuItem);

		menu.add(submenu);

		submenu = new JMenu("Track");
		menuItem = new JMenuItem(MenuOptions.addTrack);
		JPopupMenu trackPopup = new JPopupMenu();
		menuItem.addActionListener(this);
		trackPopup.add(menuItem);
		submenu.add(menuItem);

		menuItem = new JMenuItem(MenuOptions.removeTrack);
		JPopupMenu trackRemovePopup = new JPopupMenu();
		menuItem.addActionListener(this);
		trackRemovePopup.add(menuItem);
		submenu.add(menuItem);

		menu.add(submenu);


		//Add Help
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_N);
		this.add(menu);





	}



	public Container createContentPane() {
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);
		return contentPane;
	}







	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(MenuOptions.exploreMayX) || e.getActionCommand().equals(MenuOptions.addStudyAway) || e.getActionCommand().equals(MenuOptions.addInternship)){
			d.GUIOutsideLink(e.getActionCommand());
		}
		if(e.getActionCommand().equals(MenuOptions.addMayX) || e.getActionCommand().equals(MenuOptions.addSummerClass)){
			d.GUIYearsPopUP(e.getActionCommand());
		}
		if(e.getActionCommand().equals(MenuOptions.removeMajor) || e.getActionCommand().equals(MenuOptions.removeMinor) || (e.getActionCommand().equals(MenuOptions.removeTrack))){
			d.GUIRemoveMajorDialogBox(e.getActionCommand());
		}
		if(e.getActionCommand().equals(MenuOptions.addMajor) || e.getActionCommand().equals(MenuOptions.addMinor) || (e.getActionCommand().equals(MenuOptions.addTrack))){
			d.GUIPopUP(e.getActionCommand());
		}
		if(e.getActionCommand().equals(MenuOptions.newSchedule)){
			d.GUINewSchedule();
		}

	}


}


