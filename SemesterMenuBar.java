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
			this.add(menu);
			
			menuItem = new JMenuItem(MenuOptions.supriseMe);
			JPopupMenu addSuprise = new JPopupMenu();
			menuItem.addActionListener(this);
			addSuprise.add(menuItem);
			menu.add(menuItem);
			this.add(menu);
			
		//	menuItem = new JMenuItem(MenuOptions.addInstruct);
		//	JPopupMenu addACoursePopup = new JPopupMenu();
		//	menuItem.addActionListener(this);
		//	addACoursePopup.add(menuItem);
		//	menu.add(menuItem);
		//	this.add(menu);
		}


			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals(MenuOptions.addInstruct)){
					System.out.println("I am doing what you want me to");
				}
				}
}