import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class ExtrasAddList {

	public ExtrasAddList(){

		JFrame frame = new JFrame();
		JPanel addMajor = new JPanel();
		addMajor.setPreferredSize(new Dimension( 1000, 1001));
		addMajor.setBackground(Color.BLACK);

		JList listOfMajor = new JList();
		listOfMajor.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		listOfMajor.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		addMajor.add(listOfMajor);

		JScrollPane listScroller = new JScrollPane(addMajor);
		listScroller.setPreferredSize(new Dimension (200, 100));





		frame.add(addMajor);
		frame.pack();
		frame.setVisible(true);

	}

}
