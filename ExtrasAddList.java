import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class ExtrasAddList {

	
	public ExtrasAddList(String type){
		//Creates the PopUp Window
		JFrame frame = new JFrame(type);
		JPanel addMajor = new JPanel();
		addMajor.setPreferredSize(new Dimension( 1000, 1001));
		addMajor.setBackground(Color.MAGENTA);
		
		
		
		//Creates list
	//	DefaultListModel<>
		
	
		
		
		
		
		

		frame.add(addMajor);
		frame.pack();
		frame.setVisible(true);

	}

}
