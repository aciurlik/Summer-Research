
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

		//Build the first menu.
		menu = new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
				"The only menu in this program that has menu items");
		menuBar.add(menu);

		//a group of JMenuItems
		menuItem = new JMenuItem("New Schedule",
				KeyEvent.VK_T);
		//menuItem.setMnemonic(KeyEvent.VK_T); //used constructor instead
		menuItem.setAccelerator(KeyStroke.getKeyStroke(
				KeyEvent.VK_1, ActionEvent.ALT_MASK));
		//   menuItem.getAccessibleContext().setAccessibleDescription(
		//      "This doesn't really do anything");
		menu.add(menuItem);

		// ImageIcon icon = createImageIcon("images/middle.gif");
		//menuItem = new JMenuItem("Both text and icon", icon);
		menuItem = new JMenuItem("Save Schedule");
		//  menuItem.setMnemonic(KeyEvent.VK_B);
		menu.add(menuItem);

		// menuItem = new JMenuItem(icon);
		menuItem.setMnemonic(KeyEvent.VK_D);
		menu.add(menuItem);

		menuItem = new JMenuItem("Do another new fangly thing");
		menu.add(menuItem);





		//Build second menu in the menu bar.
		menu = new JMenu("Edit");
		menu.setMnemonic(KeyEvent.VK_N);
		// menu.getAccessibleContext().setAccessibleDescription(
		//       "This menu does nothing");
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

		//  menu.getAccessibleContext().setAccessibleDescription("Add a Major");



		return menuBar;
	}



	public Container createContentPane() {
		//Create the content-pane-to-be.
		JPanel contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);

		//Create a scrolled text area.
		//  output = new JTextArea(5, 30);
		//output.setEditable(false);
		// scrollPane = new JScrollPane(output);

		//Add the text area to the content pane.
		//  contentPane.add(scrollPane, BorderLayout.CENTER);

		return contentPane;
	}



	/**
	 * Create the GUI and show it.  For thread safety,
	 * this method should be invoked from the
	 * event-dispatching thread.
	 */
	private static void createAndShowGUI() {
		//Create and set up the window.
		JFrame frame = new JFrame("MenuLookDemo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		MainMenuBar demo = new MainMenuBar();
		frame.setJMenuBar(demo.createMenuBar());
		frame.setContentPane(demo.createContentPane());

		//Display the window.
		frame.setSize(450, 260);
		frame.setVisible(true);
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		ExtrasAddList list = new ExtrasAddList();

	}





}

