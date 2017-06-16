

/*
 * A simple swing checkbox example with different constructors
 */

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;

public class simpleCheckBox {

	public static void main(String[] args) {
		// Create and set up a frame window
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Simple checkbox demo");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Define the panel to hold the checkbox	
		JPanel panel = new JPanel();
		
		// Create checkbox with different constructors
		JCheckBox checkbox1 = new JCheckBox("Apple", true);
		JCheckBox checkbox2 = new JCheckBox("Banana");		
		JCheckBox checkbox3 = new JCheckBox("Grape", true);
		JCheckBox checkbox4 = new JCheckBox("Orange");
		JCheckBox checkbox5 = new JCheckBox("Pear", true);
		
		// Set tooltip text
		checkbox2.setToolTipText("Select me");
		
		// Set up the title for the panel
		panel.setBorder(BorderFactory.createTitledBorder("Fruits"));
		
		// Add the checkbox into the panels 
		panel.add(checkbox1);
		panel.add(checkbox2);
		panel.add(checkbox3);
		panel.add(checkbox4);
		panel.add(checkbox5);
		
	//	JOptionPane.showInputDialog(frame, "",  "", JOptionPane.PLAIN_MESSAGE, null, panel, "cat" );
		// Add the panel into the frame
		frame.add(panel);
		
		// Set the window to be visible as the default to be false
		frame.pack();
		frame.setVisible(true);

	}

}
