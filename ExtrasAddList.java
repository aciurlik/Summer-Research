import java.awt.Color;
import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class ExtrasAddList {

	public ExtrasAddList(String type){
		//Creates the PopUp Window
		JFrame frame = new JFrame(type);
		JPanel popUP = new JPanel();
		popUP.setBackground(Color.MAGENTA);



		
		//Creates Add list
		DefaultListModel<String> addList = new DefaultListModel<>();
	
	
		
		
		
		
		
	//	for(int i=0; i<list.getSize(); i++){
	//		addList.addElement(list.get(i).name);
	//	}
	
		JList<String> addCourses = new JList<>(addList);
		addCourses.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		addCourses.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		
		JScrollPane listScroller = new JScrollPane(addCourses);
		listScroller.setPreferredSize(new Dimension(250, 100));
		
		popUP.add(addCourses);
		
		
		
	
		frame.add(popUP);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}

}
