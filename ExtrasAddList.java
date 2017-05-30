
import java.awt.Dimension;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;


public class ExtrasAddList implements ActionListener {
	JList <Major> addList;
	Driver d;
	Schedule s;
	String addMajor = new String("Add Major");
	String addMinor = new String("Add Minor");
	String addTrack = new String("Add Track");
	String addMayX = new String("Add MayX");
	String addSummerClass = new String("Add Summer Class");
	String addStudyAway = new String("Add Study Away");
	String addInternship = new String("Add Internship");
	String addResearch = new String("Add Research");
	JFrame frame = new JFrame();



	public ExtrasAddList(String type, Driver d, Schedule s){
		//Creates the PopUp Window
		
		frame = new JFrame(type);
		JPanel popUP = new JPanel();
		
		

		this.d=d;
		popUP.setBackground(FurmanOfficial.darkPurple);


		//Creates Add list
		
		
		if(type.equals(addMajor)){
			ListOfMajors majors = ListOfMajors.testList();
			ArrayList<Major> collectionOfMajors =  majors.getGUIMajors();
			
			ArrayList<Major> displayThings = d.sch. removeAlreadyChosenMajors(collectionOfMajors);
			System.out.println("Display" + displayThings);
			
			addList = new JList <Major>(displayThings.toArray(new Major[displayThings.size()]));
	

			
		}
		if(type.equals(addMinor)){
			ListOfMajors majors = ListOfMajors.testList();
			Major[] collectionOfMajors =  majors.getGUIMinor();
			addList = new JList <Major>(collectionOfMajors);
			
			
			}
		
		if(type.equals(addTrack)){
			ListOfMajors majors = ListOfMajors.testList();
			Major[] collectionOfMajors =  majors.getGUITrack();
			addList = new JList <Major>(collectionOfMajors);
		}


		addList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		addList.setFocusTraversalKeysEnabled(true);
		
		JScrollPane listScroller = new JScrollPane(addList);
		listScroller.setPreferredSize(new Dimension(200, 200));
		popUP.add(listScroller);


		//addList.addListSelectionListener(listSelectionListener);
		//Sets up location of popup
		
		Dimension screenSize= Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screenSize.getWidth();
		int screenHeight = (int) screenSize.getHeight();

		//Sets up Frame
		JPanel p = new JPanel();
		frame.setLocation((int)(screenWidth*.4),(int)(screenHeight*.4));
		frame.setPreferredSize(new Dimension(200, 300));
		p.add(popUP);
		
		JButton done = new JButton("DONE");
		done.addActionListener(this);
		
		p.add(done);
		frame.add(p);


		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		
		frame.setVisible(true);

	}


	@Override
	public void actionPerformed(ActionEvent e) {
		for(Major m :addList.getSelectedValuesList()){
			d.GUIAddMajor(m);
		
		}
		frame.dispose();
		
		
		
	}



}
