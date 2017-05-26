import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class AdditionsPanel extends JPanel implements ActionListener{
		public String AdditionsHeader = new String("The Furman Advantage");
		public String classAddition = new String("MAJOR/MINOR");
		public int optionsNumber = 8;
		public int headerNumber = 2;
		public Color FurmanDarkPurple = new Color(43, 12, 86);
		public Color FurmanLightPurple = new Color(79, 33, 112);
		public Color FurmanGray = new Color(96, 96, 91);
	
	public AdditionsPanel(){
		
		super();
		this.setLayout((new GridLayout(optionsNumber+headerNumber+2, 1, 3, 3)));
		JLabel empty = new JLabel("          ");
		this.add(empty);
		this.setBackground(Color.white);
		JLabel header = new JLabel(AdditionsHeader);
		header.setHorizontalAlignment(JLabel.CENTER);
		this.add(header);
		this.addButton("Add MayX");
		this.addButton("Add Summer Class");
		this.addButton("Add Study Away");
		this.addButton("Add Internship");
		this.addButton("Add Research");
		JLabel classAdditions = new JLabel(classAddition);
		classAdditions.setHorizontalAlignment(JLabel.CENTER);
		this.add(classAdditions);
		this.addButton("Add Major");
		this.addButton("Add Minor");
		this.addButton("Add Track");
		this.add(empty);
		
	}
	
	public void addButton(String s){
		JPanel buttonPanel = new JPanel();
		buttonPanel.setBackground(Color.white);
		JButton button = new JButton(s);
		button.setForeground(Color.white);
		button.setHorizontalTextPosition(SwingConstants.LEFT);
		button.setBackground(FurmanDarkPurple);
		button.setBorderPainted(false);
		button.setPreferredSize(new Dimension(200, 20));
		button.setOpaque(true);
		button.addActionListener(this);
		
		buttonPanel.add(button);
		this.add(buttonPanel);
		
	
	}
	public static void main(String[] args){
	
		JFrame frame = new JFrame();
		AdditionsPanel add = new AdditionsPanel();
		frame.setSize(210, 300);
		frame.add(add);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		ExtrasAddList list = new ExtrasAddList(e.getActionCommand());
		
	}

}
