import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StartUpMenu implements ActionListener {
	JFrame frame;
	JLabel label;//Holds the images. 
	ArrayList<ImageIcon> instructions;
	JPanel layer;//Holds all of the information, and buttons. 
	int counter = 0;//Holds the location of the current slide, updates as user click through them. 
	JPanel buttonPanel;
	
	//Options given at the bottom of the slides. 
	JButton next;
	JButton previous;
	JButton cancel;
	JButton finish;
	JCheckBox doNotShowAgain;



/**
 * 
 *  Blurb written: 7/28/2017
 *  Last updated: 7/28/2017
 *  
 * This is what gets displayed at the very first use of our program,
 * and when the user presses the Help 'View Start-Up Guide'. The user
 * can change if this gets popped up by altering the settings. This is just
 * a slide show of get Started Instructions. The slides are shown at the top,
 * and the user can navigate through them by using the buttons.  
 */
	public StartUpMenu(){
		counter=0;
		layer = new JPanel();
		layer.setLayout(new BorderLayout());
		if(instructions == null){
			instructions = FileHandler.getStartUpSlides();
		}
		
		label = new JLabel();
		label.setIcon(instructions.get(0));
		frame = new JFrame("Getting Started");
		layer.add(label, BorderLayout.NORTH);
		
		//Sets up the buttons at the bottom. 
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());
		
		JPanel previousNextPanel = new JPanel();
		previousNextPanel.setLayout(new BorderLayout());

		previous = new JButton(MenuOptions.previous);
		previous.addActionListener(this);
		previousNextPanel.add(previous, BorderLayout.CENTER);
		previous.setVisible(false);

		cancel = new JButton(MenuOptions.Cancel);
		cancel.addActionListener(this);
		previousNextPanel.add(cancel, BorderLayout.WEST);
		cancel.setVisible(true);

		next = new JButton(MenuOptions.next);
		next.addActionListener(this);
		previousNextPanel.add(next, BorderLayout.EAST);
		next.setVisible(true);


		buttonPanel.add(previousNextPanel, BorderLayout.EAST);
		layer.add(buttonPanel, BorderLayout.SOUTH);
		frame.add(layer);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(false);

	}


	public void showStartUp(){
		counter = 0;
		update(counter);
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		String command = a.getActionCommand();
		//Finish
		if(command.equals(MenuOptions.finish)){
			if(doNotShowAgain.isSelected()){
				FileHandler.propertySet(MenuOptions.startUp, "false");
			}
			frame.dispose();
		}
		//Next
		if(command.equals(MenuOptions.next)){
			counter++;
			update(counter);
		}
		//Previous
		if(command.equals(MenuOptions.previous)){
			counter--;
			update(counter);
		}
		//Cancel
		if(command.equals(MenuOptions.Cancel)){
			frame.dispose();		
		}
	}

	public void update(int n){
		next.setText("Next");
		next.setActionCommand(MenuOptions.next);
		next.setVisible(true);
		previous.setVisible(false);
		
		if(doNotShowAgain!=null){//If it has been created and the user has gone back. 
			buttonPanel.remove(doNotShowAgain);
		}
		layer.removeAll(); 
		label.setIcon(instructions.get(n));
		layer.add(label, BorderLayout.NORTH);
		if(n>0){//Previous option for all but the last page. 
			previous.setVisible(true);
		}
		if(n==instructions.size()-1){//Last slide has finish option. 
			next.setText("Finish");
			next.setActionCommand(MenuOptions.finish);
			doNotShowAgain=new JCheckBox("Don't show this again");
			if(FileHandler.propertyGet(MenuOptions.startUp).equals("false")){//This this always reflects the 
				//settings given by the user. 
				doNotShowAgain.setSelected(true);
			}
			buttonPanel.add(doNotShowAgain, BorderLayout.WEST);
		}

		buttonPanel.revalidate();
		buttonPanel.repaint();

		layer.add(buttonPanel, BorderLayout.CENTER);

		layer.revalidate();
		layer.repaint();

		frame.revalidate();
		frame.repaint();

	}
	
	
	/**
	 * Testing:
	 * The best way to test to flip through the slides and make sure all of them are displayed. 
	 * (Currently there are 7) To try going back when you reach the last slide, and then make sure both 
	 * the finish, and the cancel options close this StartUpMenu. Another thing to check would be the 
	 * don't show me this again option. Make sure that setting match up with what is given on the last slide,
	 * and if that is selected, then this should not be displayed when the program is closed and opened again. 
	 */
}
