import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ComboBoxDemo implements ActionListener{
	
	  public static void main(String[] args) {
		  String[] petStrings = { "Bird", "Cat", "Dog", "Rabbit", "Pig" };

		//Create the combo box, select item at index 4.
		//Indices start at 0, so 4 specifies the pig.
		JComboBox petList = new JComboBox(petStrings);
		petList.setSelectedIndex(4);
		
		JPanel test = new JPanel();
		test.add(petList);
		
		JFrame frame = new JFrame();
		frame.add(test);
		
		petList.setVisible(true);
		test.setBackground(Color.pink);
		test.setVisible(true);
		frame.setVisible(true);

	      
	    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
	}

}



