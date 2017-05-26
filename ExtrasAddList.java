import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ExtrasAddList {


	public ExtrasAddList(String type){
		//Creates the PopUp Window
		JFrame frame = new JFrame(type);
		JPanel popUP = new JPanel();
		String addMajor = new String("Add Major");
		String addMinor = new String("Add Minor");
		String addTrack = new String("Add Track");
		String addMayX = new String("Add MayX");
		String addSummerClass = new String("Add Summer Class");
		String addStudyAway = new String("Add Study Away");
		String addInternship = new String("Add Internship");
		String addResearch = new String("Add Research");


		popUP.setBackground(FurmanOfficial.darkPurple);

		System.out.println(type);

		//Creates Add list
		DefaultListModel<String> addList = new DefaultListModel<>();

		if(type.equals(addMajor)){
			ListOfMajors majors = ListOfMajors.testList();
			for(int i=0; i<majors.size(); i++){
				addList.addElement(majors.get(i).name);

			}
		}
		if(type.equals(addMinor)){
			System.out.println("I am doing this");
			String[] minor = {"cat", "dog", "pig"};
			for(int i=0; i<minor.length; i++){
				addList.addElement(minor[i]);
			}
		}
		if(type.equals(addTrack)){
			System.out.println("I am doing this Track");
		}


		JList<String> addCourses = new JList<>(addList);
		addCourses.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
	
		
		
		JScrollPane listScroller = new JScrollPane(addCourses);
		listScroller.setPreferredSize(new Dimension(200, 200));
		popUP.add(listScroller);


		//Sets up location of popup
		Dimension screenSize= Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int) screenSize.getWidth();
		int screenHeight = (int) screenSize.getHeight();

		//Sets up Frame
		frame.setLocation((int)(screenWidth*.4),(int)(screenHeight*.4));
		frame.setPreferredSize(new Dimension(200, 300));
		frame.add(popUP);


		//frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}



}
