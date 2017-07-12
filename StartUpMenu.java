import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StartUpMenu implements ActionListener {
	JFrame frame;
	JLabel label;
	ArrayList<ImageIcon> instructions;
	JPanel layer;
	int counter = 0;
	JPanel buttonPanel;
	JButton next;
	JButton previous;
	JButton finish;
	JCheckBox noMore;
	public StartUpMenu(){
		layer = new JPanel();
		layer.setLayout(new BorderLayout());
		instructions = FileHandler.getInstructions(new File(MenuOptions.resourcesFolder + "StartUpSlides"));
		JLabel label = new JLabel(instructions.get(0));
		frame = new JFrame("Getting Started");

		layer.add(label, BorderLayout.NORTH);

		buttonPanel = new JPanel();

		previous = new JButton(MenuOptions.previous);
		previous.setActionCommand(MenuOptions.previous);
		previous.addActionListener(this);

		next = new JButton(MenuOptions.next);
		next.setActionCommand(MenuOptions.next);
		next.addActionListener(this);
		buttonPanel.add(next);
		
		finish = new JButton(MenuOptions.finish);
		finish.setActionCommand(MenuOptions.finish);
		finish.addActionListener(this);

		layer.add(buttonPanel, BorderLayout.SOUTH);
		frame.add(layer);
		frame.pack();
		frame.setVisible(true);


	}

	@Override
	public void actionPerformed(ActionEvent a) {
		if(a.getActionCommand().equals(MenuOptions.finish)){
			if(noMore.isSelected()){
				FileHandler.propertySet(MenuOptions.startUp, "false");
			}
			frame.dispose();
		}

		//Next
		if(a.getActionCommand().equals(MenuOptions.next)){
			counter++;
			update(counter);
		}


		//Previous
		if(a.getActionCommand().equals(MenuOptions.previous)){
			
				counter--;
				update(counter);
		}

	}

	public void update(int n){
		
		layer.removeAll();
		layer.add(new JLabel(instructions.get(n)), BorderLayout.NORTH);
		buttonPanel.removeAll();
		if(n==0){
			buttonPanel.add(next);
		}
		if(n>0 && n<instructions.size()-2){
			buttonPanel.add(previous);
			buttonPanel.add(next);
		}
		if(n==instructions.size()-2){
			buttonPanel.add(previous);
			buttonPanel.add(finish);
			noMore=new JCheckBox("Don't show this again");
			buttonPanel.add(noMore);
		}
		layer.add(buttonPanel, BorderLayout.CENTER);
		layer.revalidate();
		layer.repaint();
		
		frame.revalidate();
		frame.repaint();
		
		
		
		
		

	}
}
