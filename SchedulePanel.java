/*
 * 
 * https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/layout/GridBagLayoutDemoProject/src/layout/GridBagLayoutDemo.java
 * https://docs.oracle.com/javase/tutorial/uiswing/layout/gridbag.html
 * 
package scheduler;
/*
 * GridBagLayoutDemo.java requires no other files.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
	Driver d;
	


	public SchedulePanel(Schedule sch, Driver d) {

		super();

		this.d = d;
		this.sch=sch;

		
		
		this.setBackground(Color.white);
		//This will be deleted once we set it relative to the whole. 
		this.setPreferredSize(new Dimension(700, 400));
		this.setLayout(new BorderLayout());

		this.infoPanel = new JPanel();
		creditHoursLabel = new JLabel();
		creditHoursLabel.setFont(FurmanOfficial.getFont(this.infoPanelFontSize));
		reqsLeftLabel = new JLabel();
		reqsLeftLabel.setFont(FurmanOfficial.getFont(this.infoPanelFontSize));
		this.infoPanel.add(reqsLeftLabel);
		this.infoPanel.add(creditHoursLabel);
		this.infoPanel.setBackground(this.getBackground());
		
		this.add(infoPanel, BorderLayout.NORTH);

		scrollPanel.setBackground(Color.white);
		
		
		addExtraSemesterButtonPanel.setPreferredSize(new Dimension(100, 100)); //Arbitrary size smaller than scroll Panel set to same color
		addExtraSemesterButtonPanel.setBackground(FurmanOfficial.lightPurple(50));
		JButton addSemester = new JButton("+");
		addSemester.setPreferredSize(new Dimension(50, 50)); //Arbitrary size
		addExtraSemesterButtonPanel.add(addSemester);
		scrollPanel.add(addExtraSemesterButtonPanel);
		addSemester.addActionListener(this);




		JScrollPane scrollPane = new JScrollPane(scrollPanel);
		scrollPane.setPreferredSize(new Dimension(700, 300));
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		this.add(scrollPane, BorderLayout.CENTER); 

	}


	@Override
	public void actionPerformed(ActionEvent e) {

		d.GUISemesterPanelAdded();



	}



	public void update(Schedule sch) {
		scrollPanel.removeAll();
		scrollPanel.setLayout(new GridLayout(1, sch.semesters.size()+1, 5, 5));
		for(Semester s: sch.semesters){
			SemesterPanel semester = new SemesterPanel(s, this.d);
			scrollPanel.add(semester);
		}
		scrollPanel.add(addExtraSemesterButtonPanel);
		
		this.creditHoursLabel.setText(this.cHText + (230 - sch.getCreditHoursComplete()));
		this.reqsLeftLabel.setText(this.reqsText + sch.totalRequirementsLeft());
	}







}


