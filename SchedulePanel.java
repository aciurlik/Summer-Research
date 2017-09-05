
import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;


/**
 * 
 * Blurb Written: 7/31/2017
 * Last Updated: 7/31/2017
 * 
 * This is the container that holds all of the semesterPanels it updates
 * based on the changed made to the schedule
 * @param schGUI
 */

public class SchedulePanel extends JPanel implements ActionListener{

	JPanel scrollPanel = new JPanel();
	JPanel addExtraSemesterButtonPanel = new JPanel();
	JButton addSemesterButton = new JButton("+");
	public ArrayList<SemesterPanel> allSemesterPanels;
	int insetsWidth = 5;
	ScheduleGUI schGUI;
	int Count=1;




	public SchedulePanel(ScheduleGUI schGUI) {

		super();
		this.schGUI = schGUI;
		this.setBackground(Color.white);
		this.setLayout(new BorderLayout());
		allSemesterPanels= new ArrayList<SemesterPanel>();
		scrollPanel.setBackground(Color.white);
		addExtraSemesterButtonPanel.setPreferredSize(new Dimension(100, 100)); //Arbitrary size smaller than scroll Panel set to same color
		addExtraSemesterButtonPanel.setBackground(FurmanOfficial.lightPurple(50));
		JButton addSemester = new JButton("+");
		addSemester.setPreferredSize(new Dimension(50, 50)); //Arbitrary size
		addExtraSemesterButtonPanel.add(addSemester);
		scrollPanel.add(addExtraSemesterButtonPanel);
		addSemester.addActionListener(this);
		addSemester.setToolTipText("Add Semester");




		JScrollPane scrollPane = new JScrollPane(scrollPanel);
		JScrollBar scrollBar = new JScrollBar();
		int scrollWidth = scrollBar.getPreferredSize().width;
		scrollPane.setSize(new Dimension(700,SemesterPanel.height+4*insetsWidth+scrollWidth));
		this.setPreferredSize(new Dimension(700,SemesterPanel.height+4*insetsWidth+scrollWidth));
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		this.add(scrollPane, BorderLayout.CENTER); 

	}


	@Override
	public void actionPerformed(ActionEvent e) {
		schGUI.addSemesterPanel();
	}

	public void dragStarted(ScheduleElement e){
		for(SemesterPanel s : this.allSemesterPanels){
			s.dragStarted(e);
		}
	}
	public void dragEnded(){
		for(SemesterPanel s : this.allSemesterPanels){
			s.dragEnded();
		}
	}


	int counter = 0;
	public void update(Schedule sch) {
		scrollPanel.removeAll();
		scrollPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.insets = new Insets(insetsWidth,insetsWidth,insetsWidth,insetsWidth);
		ArrayList<Semester> schSemesters = sch.getAllSemestersSorted();
		for(int i = 0; i < schSemesters.size() ; i ++){
			Semester s =  schSemesters.get(i);
			if(s.isTaken()){
				// TODO Makes it so you don't get errors from taken semesters
				//Since the semesters and created in tandem with the list, you
				//cannot set these limits when the semester is created.
				//They must be created when looking at the whole group
				//of them because their assignment depends on their position in the group.
				//Thus they exist in the update of schedulePanel because this is the class
				//responsible for putting these in order.
				s.setOverloadLimit(1000);

			}
			if(s.semesterDate.equals(sch.firstSemester) || s.semesterDate.equals(sch.firstSemester.nextSemester())){
				s.setOverloadLimit(16);
			}
			SemesterPanel semesterP = this.findPanelFor(s); //if you already have a panel for this semester,
			// don't make a new one.
			if(semesterP == null){
				semesterP =new SemesterPanel(s, this.schGUI);
				allSemesterPanels.add(semesterP);
			}
			else{
				semesterP.updatePanel(s);
			}
			
			
			//If it's the last semester, you can always delete it (except in the next case)
			if(i == schSemesters.size() - 1){
				semesterP.setDeletable(true);
			}
			//If it's one of the first two semesters, you can never delete it.
			// (even if it's the last semester)
			if( i <= 1){
				semesterP.setDeletable(false);
			}
			scrollPanel.add(semesterP, gbc);
			gbc.gridx ++;
		}


		gbc.fill = GridBagConstraints.NONE;
		scrollPanel.add(addExtraSemesterButtonPanel, gbc);
		
		
		//This code opens the secret admin window
		if(allSemesterPanels.size() == 15){
			
			allSemesterPanels.get(10).addMouseListener(new MouseAdapter(){
				@Override
				public void mousePressed(MouseEvent e){
					if(SwingUtilities.isRightMouseButton(e)){
						schGUI.askMasterPassword();
					}
				}
			});
		}

	}

	/**
	 * Finds and returns the Panel for that semester, if not
	 * then it alerts this class to make a new one. 
	 * @param s
	 * @return
	 */

	public SemesterPanel findPanelFor(Semester s){
		for(int i=0; i<allSemesterPanels.size(); i++ ){
			if(allSemesterPanels.get(i).sem.equals(s)){
				return(allSemesterPanels.get(i));
			}
		}
		return null;

	}




}


