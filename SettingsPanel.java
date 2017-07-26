import java.awt.BorderLayout;
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


		JComponent panel1 = makeTextPanel("");
		JPanel checkBoxHolder = new JPanel();
		checkBoxHolder.setSize(200, 150);
		showStartUp = new JCheckBox(MenuOptions.startUp);
		if(FileHandler.propertyGet(MenuOptions.startUp).equals("true")){
			showStartUp.setSelected(true);
		}

		checkBoxHolder.add(showStartUp);
		panel1.add(checkBoxHolder);
		tabbedPane.addTab(MenuOptions.startUp, null, panel1,
				"Add/Remove Start Up Instructions");
		tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);



		layer.add(tabbedPane, BorderLayout.NORTH);
		layer.add(buttonPanel, BorderLayout.SOUTH);
		popUp.add(layer);
		popUp.pack();
		popUp.setVisible(true);

	}
	protected static JComponent makeTextPanel(String text) {
		JPanel panel = new JPanel(false);
		JLabel filler = new JLabel(text);
		filler.setHorizontalAlignment(JLabel.CENTER);
		panel.setLayout(new GridLayout(1, 1));
		panel.add(filler);
		return panel;
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
