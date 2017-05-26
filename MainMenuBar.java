
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



public class MainMenuBar implements ActionListener {
	JTextArea output;
	JScrollPane scrollPane;

	public JMenuBar createMenuBar() {
		JMenuBar menuBar;
		JMenu menu, submenu;
		JMenuItem menuItem;
		JRadioButtonMenuItem rbMenuItem;
		JCheckBoxMenuItem cbMenuItem;

		//Create the menu bar.
		menuBar = new JMenuBar();

		menu = new JMenu("The Furman Advantage");
		submenu = new JMenu("MayX");
		menuItem = new JMenuItem("Explore MayX Opportunities");
		submenu.add(menuItem);
		menuItem = new JMenuItem("Add a MayX");
		submenu.add(menuItem);
		menu.add(submenu);


		submenu = new JMenu("Study Abroad");
		menuItem = new JMenuItem("Explore Study Abroad Opportunities");
		submenu.add(menuItem);
		menuItem = new JMenuItem("Add Study Abroad");
		submenu.add(menuItem);
		menu.add(submenu);

		menuItem = new JMenuItem("Add a Summer Course");
		menu.add(menuItem);
		menuItem = new JMenuItem("Explore Intership Opportunities");
		menu.add(menuItem);
		menuBar.add(menu);



		//Build the file menu-> Add/Save Schedule
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
				"The only menu in this program that has menu items");
		menuBar.add(menu);
		menuItem = new JMenuItem("New Schedule",
				KeyEvent.VK_T);
		menuItem.setMnemonic(KeyEvent.VK_T); //used constructor instead
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_1, ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription(
				"Add a new Schedule");
		menu.add(menuItem);
		menuItem = new JMenuItem("Save Schedule");
		menuItem.setMnemonic(KeyEvent.VK_B);
		menu.add(menuItem);
		menuItem.setMnemonic(KeyEvent.VK_D);
		menu.add(menuItem);

		menuItem = new JMenuItem("Do another new fangly thing");
		menu.add(menuItem);





		//Build Edit menu in the menu bar.
		menu = new JMenu("Edit");
		menu.setMnemonic(KeyEvent.VK_N);
		menu.getAccessibleContext().setAccessibleDescription(
				"This allows edits to schedule");
		menuBar.add(menu);


		submenu = new JMenu("Major");
		submenu.setMnemonic(KeyEvent.VK_S);
		menuItem = new JMenuItem("Add Major");
		JPopupMenu majorPopup = new JPopupMenu();
		menuItem.addActionListener(this);
		majorPopup.add(menuItem);
		submenu.add(menuItem);
		menuItem = new JMenuItem("Remove Major");
		submenu.add(menuItem);
		menu.add(submenu);




		submenu = new JMenu("Minor");
		menuItem = new JMenuItem("Add Minor");
		JPopupMenu minorPopup = new JPopupMenu();
		menuItem.addActionListener(this);
		minorPopup.add(menuItem);
		submenu.add(menuItem);
		menuItem = new JMenuItem("Remove Minor");
		submenu.add(menuItem);
		menu.add(submenu);




		//Add Help
		menu = new JMenu("Help");
		menu.setMnemonic(KeyEvent.VK_N);
		menuBar.add(menu);




		return menuBar;
	}



	public Container createContentPane() {
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);
		return contentPane;
	}







	@Override
	public void actionPerformed(ActionEvent e) {
		String addMajor = "Add Major";
		ExtrasAddList list = new ExtrasAddList(e.getActionCommand());

	}


}


