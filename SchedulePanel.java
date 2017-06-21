
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
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;



public class SchedulePanel extends JPanel implements ActionListener{
	/**
	 * 
	 */
	private Schedule sch;
	private int numberOfRegularSemesters=12;
	private int spaceConstant=5;
	private int buttonPress=0;
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
	Driver d;
	int Count=1;





	public SchedulePanel(Schedule sch, Driver d) {

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

		long startTime = System.currentTimeMillis();
		scrollPanel.removeAll();
		scrollPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = gbc.VERTICAL;
		gbc.insets = new Insets(insetsWidth,insetsWidth,insetsWidth,insetsWidth);


		for(Semester s: sch.getAllSemesters()){
			SemesterPanel foundp = this.findPanelFor(s);
			s.setLastSemester(false);
			if(s.equals(sch.getAllSemesters().get(sch.getAllSemesters().size()-1))){
				s.setLastSemester(true);
			}
			if(s.equals(sch.getAllSemesters().get(1))){
				s.setUndeletableSemester(true);
			}
			if(foundp != null){
				foundp.updatePanel(s);
				scrollPanel.add(foundp, gbc);
				gbc.gridx++;
			}
			else{
				SemesterPanel semester = new SemesterPanel(s, this.d);
				if(semester.equals(sch.getAllSemesters().get(sch.getAllSemesters().size()-1))){
					s.setLastSemester(true);
				}
				else{
					s.setLastSemester(false);
				}
				if(semester.equals(sch.getAllSemesters().get(1))){
					s.setUndeletableSemester(true);
				}
				else{
					s.setUndeletableSemester(false);
				}
				allSemesterPanels.add(semester);
				scrollPanel.add(semester, gbc);
				gbc.gridx ++;
			}



		}


		gbc.fill = gbc.NONE;
		scrollPanel.add(addExtraSemesterButtonPanel, gbc);


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


