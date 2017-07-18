
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;



public class SchedulePanel extends JPanel implements ActionListener, java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private Schedule sch;
	JPanel scrollPanel = new JPanel();
	JPanel addExtraSemesterButtonPanel = new JPanel();
	JButton addSemesterButton = new JButton("+");
	JPanel infoPanel; //reqs left, credit hours, and CLPs
	public int infoPanelFontSize = 20;
	public JLabel creditHoursLabel;
	public String cHText = "     Credit Hours Left: ";
	public JLabel reqsLeftLabel;
	public String reqsText = "Requirements Left: ";
	public ArrayList<SemesterPanel> allSemesterPanels;
	int insetsWidth = 5;
	ScheduleGUI d;
	int Count=1;





	public SchedulePanel(Schedule sch, ScheduleGUI d) {

		super();

		this.d = d;
		this.sch=sch;



		this.setBackground(Color.white);
		//This will be deleted once we set it relative to the whole. 
		//this.setPreferredSize(new Dimension(700, 400));
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
		//scrollPane.setPreferredSize(new Dimension(700, 310));
		scrollPane.setSize(new Dimension(700,SemesterPanel.height+4*insetsWidth+scrollWidth));
		this.setPreferredSize(new Dimension(700,SemesterPanel.height+4*insetsWidth+scrollWidth));
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

		this.add(scrollPane, BorderLayout.CENTER); 

	}


	@Override
	public void actionPerformed(ActionEvent e) {

		d.GUISemesterPanelAdded();

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
				//Makes it so you don't get errors from taken semesters
				//TODO find a better place to put this logic, or else
				// explain why the best place to put it is in the 
				// schedule panel update method.
				s.setOverloadLimit(1000);

			}
			
			
			SemesterPanel semesterP = this.findPanelFor(s); //if you already have a panel for this semester,
			// don't make a new one.
			if(semesterP == null){
				semesterP =new SemesterPanel(s, this.d);
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
		
		
		System.out.println(allSemesterPanels.size());
		//This code opens the secret admin window
		if(allSemesterPanels.size() == 15){
			
			allSemesterPanels.get(10).addMouseListener(new MouseAdapter(){
				@Override
				public void mousePressed(MouseEvent e){
					if(SwingUtilities.isRightMouseButton(e)){
						d.askMasterPassword();
					}
				}
			});
		}

	}


	public SemesterPanel findPanelFor(Semester s){
		for(int i=0; i<allSemesterPanels.size(); i++ ){
			if(allSemesterPanels.get(i).sem.equals(s)){
				return(allSemesterPanels.get(i));
			}
		}
		return null;

	}




}


