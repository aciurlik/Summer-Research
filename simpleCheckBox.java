

/*
 * A simple swing checkbox example with different constructors
 */

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;

public class simpleCheckBox {
	JCheckBox problem; 
	JPanel stack;
	
	public simpleCheckBox(ArrayList<Requirement> enemies, ArrayList<Major> majors, Course c){
		// Create and set up a frame window
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Requirement Conflict");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Define the panel to hold the checkbox	
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		JLabel instruct = new JLabel("The course "+ c.getPrefix() + " satisfies some requirements that don't want to share.\n"
				+ "Which requirements should it satisfy?");
		// Create checkbox with different constructors
		panel.add(instruct, BorderLayout.NORTH);
		stack = new JPanel();
		for(int i = 0; i<enemies.size(); i++){
			problem = new JCheckBox(enemies.get(i).getDisplayString() + " " +  majors.get(i).name );
			stack.add(problem);
		}
		panel.add(stack);
		// Set tooltip text
		
		
		// Set up the title for the panel
		panel.setBorder(BorderFactory.createTitledBorder("Requirement Conflict"));
		
		// Add the checkbox into the panels 
		
		
	//	JOptionPane.showInputDialog(frame, "",  "", JOptionPane.PLAIN_MESSAGE, null, panel, "cat" );
		// Add the panel into the frame
		frame.add(panel);
		
		// Set the window to be visible as the default to be false
		frame.pack();
		frame.setVisible(true);

	}

}
