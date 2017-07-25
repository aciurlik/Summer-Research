import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class SettingsPanel implements ActionListener {
	JFrame popUp;
	JCheckBox showStartUp;

	public SettingsPanel(){
		popUp = new JFrame();


		JPanel layer = new JPanel();
		layer.setPreferredSize(new Dimension(400, 200));
		
		
		layer.setLayout(new BorderLayout());
		JPanel buttonPanel = new JPanel();
		JButton saveChanges = new JButton(MenuOptions.saveChanges);
		JButton cancel = new JButton(MenuOptions.Cancel);
		cancel.setActionCommand(MenuOptions.Cancel);
		cancel.addActionListener(this);
		saveChanges.setActionCommand(MenuOptions.saveChanges);
		saveChanges.addActionListener(this);
		buttonPanel.add(saveChanges);
		buttonPanel.add(cancel);

		JTabbedPane tabbedPane = new JTabbedPane();
		JPanel layerOne = new JPanel();
		layerOne.setLayout(new GridLayout(3, 3, 1, 1));
	
		
		showStartUp = new JCheckBox(MenuOptions.startUp);
		if(FileHandler.propertyGet(MenuOptions.startUp).equals("true")){
			showStartUp.setSelected(true);
		}
		
		layerOne.setLayout(new GridLayout(3, 3, 0, 0));
		layerOne.add(new JLabel(""));
		layerOne.add(new JLabel(""));
		layerOne.add(new JLabel (""));
		layerOne.add(new JLabel(""));
		layerOne.add(showStartUp);
		layerOne.add(new JLabel(""));
		layerOne.add(new JLabel (""));
		layerOne.add(new JLabel(""));
		layerOne.add(new JLabel(""));
	

		tabbedPane.addTab(MenuOptions.startUp, layerOne);
	
		
		JPanel comingSoon = new JPanel();
		comingSoon.setLayout(new BorderLayout());
		JLabel label = new JLabel("Coming Soon");
		comingSoon.add(label, BorderLayout.NORTH);
	
		tabbedPane.addTab("Other Settings", label);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		
		layer.add(tabbedPane);
		
	
		layer.add(buttonPanel, BorderLayout.SOUTH);
		popUp.add(layer);
		popUp.pack();
		popUp.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(MenuOptions.Cancel)){
			popUp.dispose();

		}
		if(e.getActionCommand().equals(MenuOptions.saveChanges)){
			//When user click save changes to setting, check all the user inputs and translates it to set the property accordingly. 
			checkUserInput();
			popUp.dispose();
		}

	}
	private void checkUserInput() {
		String s = null;
		if(showStartUp.isSelected()){
			s = "true";
		}
		else{
			s= "false";
		}
		FileHandler.propertySet(MenuOptions.startUp, s);

	}
}
