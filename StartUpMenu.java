import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Icon;
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
	JButton cancel;
	boolean showStartUp = true;


	public StartUpMenu(){
		layer = new JPanel();
		layer.setLayout(new BorderLayout());

		if(instructions == null){
			instructions = FileHandler.getInstructions();
		}
		counter=0;
		label = new JLabel(instructions.get(0));
		frame = new JFrame("Getting Started");
		layer.add(label, BorderLayout.NORTH);

		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BorderLayout());


		JPanel previousNextPanel = new JPanel();
		previousNextPanel.setLayout(new BorderLayout());

		previous = new JButton(MenuOptions.previous);
		previous.setActionCommand(MenuOptions.previous);
		previous.addActionListener(this);
		previousNextPanel.add(previous, BorderLayout.WEST);
		previous.setVisible(false);

		cancel = new JButton(MenuOptions.Cancel);
		cancel.setActionCommand(MenuOptions.Cancel);
		cancel.addActionListener(this);
		previousNextPanel.add(cancel, BorderLayout.WEST);
		cancel.setVisible(true);

		next = new JButton(MenuOptions.next);
		next.setActionCommand(MenuOptions.next);
		next.addActionListener(this);
		previousNextPanel.add(next, BorderLayout.CENTER);
		next.setVisible(true);


		buttonPanel.add(previousNextPanel, BorderLayout.EAST);
		layer.add(buttonPanel, BorderLayout.SOUTH);
		frame.add(layer);
		frame.pack();
		frame.setVisible(false);

		/**
		 * {
		 * Image myImage;

			  public void setIcon(Icon icon) {
			        super.setIcon(icon);
			        if (icon instanceof ImageIcon)
			        {
			            myImage = ((ImageIcon) icon).getImage();
			        }
			    }

			    @Override
			    public void paint(Graphics g){
			        g.drawImage(myImage, 0, 0, this.getWidth(), this.getHeight(), null);
			    }
		};
		 */




	}


	public void showStartUp(boolean showStartUp){
		counter = 0;
		this.showStartUp = showStartUp;
		update(counter);
		frame.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		if(a.getActionCommand().equals(MenuOptions.finish)){
			if(noMore.isSelected()){
				FileHandler.propertySet(MenuOptions.startUp, "false");
			}
			frame.dispose();
			if(showStartUp){
				Driver.addScheduleGUI(Schedule.testSchedule());
			}
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
		if(a.getActionCommand().equals(MenuOptions.Cancel)){
			frame.dispose();
			if(showStartUp){
				Driver.addScheduleGUI(Schedule.testSchedule());
			}
		}

	}

	public void update(int n){
		next.setText("Next");
		next.setActionCommand(MenuOptions.next);

		next.setVisible(false);
		previous.setVisible(false);;
		if(noMore!=null){
			buttonPanel.remove(noMore);
		}


		layer.removeAll(); 
		label.setIcon(instructions.get(n));
		layer.add(label, BorderLayout.NORTH);
		if(n==0){
			next.setVisible(true);
		}
		if(n>0 && n<instructions.size()-1){
			next.setVisible(true);
			previous.setVisible(true);
		}
		if(n==instructions.size()-1){
			previous.setVisible(true);
			next.setText("Finish");
			next.setActionCommand(MenuOptions.finish);
			next.setVisible(true);
			noMore=new JCheckBox("Don't show this again");
			buttonPanel.add(noMore, BorderLayout.WEST);
		}

		buttonPanel.revalidate();
		buttonPanel.repaint();

		layer.add(buttonPanel, BorderLayout.CENTER);

		layer.revalidate();
		layer.repaint();

		frame.revalidate();
		frame.repaint();






	}
}
