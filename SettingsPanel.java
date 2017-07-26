import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

/**
 * 
 * Blurb written 7/26/2017
 * Last updated 7/26/2017
 * 
 * This is the panel that is shown when a user
 * wants to change his/her settings.
 * It is a series of tab panels with the first tab being 
 * whether or not the instructions manual is 
 * shown when the program is opened. 
 * The second tab allows the user to change his/her
 * start semester date. 
 * 
 * When the user chooses save settings
 * these settings are written into the Settings document in user
 * data via the properties class.  
 * 
 * Currently there are two settings the one that decides whether the user StartUp guide is displayed at the start of the program. 
 * All altering of that setting is done through this window. There is a direct line between this window and the file.
 * 
 * The currentSemester is alter through Driver's tryPickStartDate method. Lots of classes look at this piece of setting,
 * through the Properties object.  
 *
 */
public class SettingsPanel implements ActionListener {
	JCheckBox showStartUp; //If this is checked then the start up guide will be shown at the start of the program
	//This change will only be shown once the user shuts down and reopens this program. 
	SemesterDate originalSemesterStart; //This is the start semesterDate that was set when the user opened the settings panel
	JLabel currentStartSemesterLabel; //This is the label that alerts the user to the current semester start date, it updates as the
	//user changes it. 
	SemesterDate toChangeSemesterStart;//This is the start semesterDate that changes to the current one saved in the file,thus
	//if the user selects a null option, it does not change. 
	
	public SettingsPanel(){

		//Background of the whole settingPanel, the tabs are placed on this. 
		JPanel basePanel = new JPanel();
		basePanel.setPreferredSize(new Dimension(400, 200));
		basePanel.setLayout(new BorderLayout());
		JTabbedPane tabbedPane = new JTabbedPane();
		

		showStartUp = new JCheckBox(MenuOptions.startUp);
		if(FileHandler.propertyGet(MenuOptions.startUp).equals("true")){ //Sets the checkBox on the status of the current Settings doc. 
			showStartUp.setSelected(true);
		}

		
	
		//  We wanted this to be in the middle of the panel. This is 
		//  the best way we could figure out how to do this. By 
		//  creating a gridLayout and a series of dummy label. Will
		//  come back and try to fix, if time permits. 
		 
		JPanel layerOne = new JPanel();
		layerOne.setLayout(new GridLayout(3, 3, 1, 1));
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
	
		//Adds the startUp Pane, with singular check box to the first tab. 
		tabbedPane.addTab(MenuOptions.startUp, layerOne);
	
		//This all deals with the second tab, in which the user can change the start semesterDate. 
		JPanel startSemesterPanel= new JPanel();
		startSemesterPanel.setLayout(new BorderLayout());
		
		JPanel setSemesterButtonPanel = new JPanel(); //Used to resize button. 
		JButton startSemester = new JButton(FileHandler.startSemester);
		startSemester.addActionListener(this);
		setSemesterButtonPanel = new JPanel();
		setSemesterButtonPanel.add(startSemester);
		startSemesterPanel.add(setSemesterButtonPanel, BorderLayout.EAST);
		
		//This creates the SemesterDate that the setting document currently has set as the startSemester, this should
		//always be the same as the Schedule.defaultSemester. 
		if(FileHandler.propertyGet(FileHandler.startSemester) != null){
			originalSemesterStart = SemesterDate.readFrom(FileHandler.propertyGet(FileHandler.startSemester));
		}
		//This should never be called, however it is here just in case. 
		else{
			originalSemesterStart = Driver.tryPickStartDate();
		}
		//This sets the changed the same, so if a user clicks save changes without actually changing it sets to the correct value.
		toChangeSemesterStart = originalSemesterStart;
		
		//Tells the user their current choice. 
		currentStartSemesterLabel = new JLabel("Your current Start Semester is: " + originalSemesterStart.toString() );
		currentStartSemesterLabel.setHorizontalAlignment(JLabel.CENTER);

		
		startSemesterPanel.add(currentStartSemesterLabel, BorderLayout.CENTER);
		tabbedPane.addTab("Start Semester", startSemesterPanel);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		basePanel.add(tabbedPane);
		
		//If the user clicks "X" or cancel, it will revert, otherwise it will apply changes. 
		String[] Options = {"Apply Changes", "Cancel"};
		int n = JOptionPane.showOptionDialog(null,
						basePanel,
						"Change Settings",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						null,
						Options,
						Options[1]);
		
		
		
		if(n == -1 || n == 1){//Cancel, and exit options. 
			FileHandler.propertySet(FileHandler.startSemester, originalSemesterStart.saveString());
			
		}
		if(n == 0){
			//When user click save changes to setting, check all the user inputs and translates it to set the property accordingly. 
			checkUserInput();
			
		}

	}
	@Override
	//This ask the user to pick a start semester, and will only change the toChangeSemesterStart, if the user picks a valid option. 
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(FileHandler.startSemester)){
			SemesterDate check = Driver.tryPickStartDate();
			if(check != null){
				toChangeSemesterStart = check;
				currentStartSemesterLabel.setText("Your current Start Semester is: " + toChangeSemesterStart.toString());
				currentStartSemesterLabel.invalidate();
				currentStartSemesterLabel.repaint();
			}
		}

	}
	private void checkUserInput() {
		//Since the startSemester updates automatically, it reverts with cancel and does not need to be
		//updated if apply changes is chosen. 
		String s = null;
		if(showStartUp.isSelected()){
			s = "true";
		}
		else{
			s= "false";
		}
		//Changes show start up
		FileHandler.propertySet(MenuOptions.startUp, s);
	
	}
	
	 /**
	  *Testing:
	  *The main thing one should look for is that the settings are consistent in three places (before user has changed anything in this window):
	  * In the file, in this window, and as it is used in the ScheduleGUI. In this window the changes are only applied when a user specifically
	  *asks them to be. When one first opens this window, they should check that the information displays the 
	  *current state of the setting document folder. This can be assessed by either opening the document directly, 
	  *or looking at the GUI. If the program displayed the startUp instructions then the startUp check box should be
	  *selected. If one makes a blank schedule, the first semester should be the one listed as the current start semester.
	  *One should change both of these settings, and click apply changes and reopen the window to see if they were saved.
	  *They should also check the GUI to see if those changes were reflected and finally check the settings file. Conversely one should 
	  *change the options, but then click "x" or cancel. And they should check the window/file/GUI to see that the original choices
	  *remained. 
	  */
}
