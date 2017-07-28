import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class ScheduleElementPanel extends JPanel implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	private int updateCount = 0;
	private ScheduleElement s;
	public SemesterPanel container;
	private Dimension buttonSize = new Dimension(20, 20);
	private String removeButtonText = "x";
	int nimbusWidth = 40;
	int nimbusHeight = 20;
	JPanel removePanel = new JPanel();
	ScheduleCourse[] coursesToChooseFrom; //used if this panel represents a requirement
	// and the user wants to choose a course satisfying it.


	//	Driver coursesSatisfy = new Driver();
	JComboBox<ScheduleElement>  requirementDropDown;
	JButton addCourse;




	public ScheduleElementPanel(ScheduleElement s, SemesterPanel container) {

		super();
		this.s=s;
		this.container = container;
		this.setBackground(FurmanOfficial.grey(30));
		this.setLayout(new BorderLayout());


		addCourse = new JButton (MenuOptions.addCourseWithRequirement);
		addCourse.setPreferredSize(new Dimension(130,20));
		addCourse.setActionCommand(MenuOptions.addCourseWithRequirement);
		addCourse.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				chooseACourse();
			}
		});
		

		if(s instanceof ScheduleCourse){
			if(((ScheduleCourse) s).getSemester().compareTo(container.schGUI.sch.currentSemester)<0){
				return;
			}
		}
		this.setTransferHandler(new SEPDragHandler());
		this.addMouseListener(ComponentDragHandler.getDragListener());
	}

	public ScheduleElement getElement(){
		return s;
	}

	/**
	 * This method should only be called if the user selected a course 
	 * from the dropdown.
	 */
	public void dropdownSelected(){
		ScheduleElement e = (ScheduleElement) this.requirementDropDown.getSelectedItem();
		container.schGUI.elementChanged(container, this, e);
	}



	public void updatePanel(){ //This can be taken out later
		String display = s.shortString(100);
		JLabel elementLabel = new JLabel(display);
		elementLabel.setFont(FurmanOfficial.normalFont);
		this.add(elementLabel, BorderLayout.WEST);
		if(s instanceof Requirement) {
			updateDropDown();
		}


		//Adds the remove Button
		
		removePanel.setOpaque(false);
		JButton toRemove = new JButton(removeButtonText);
		if(MenuOptions.nimbusLoaded){
			toRemove.setPreferredSize(new Dimension(nimbusWidth, nimbusHeight));
			toRemove.setMargin(new Insets(1,1,1,1));
		}
		else{
			toRemove.setPreferredSize(buttonSize);
		}
		
		//mark this element as taken if necessary
		if(s instanceof ScheduleCourse){
			if (((ScheduleCourse) s).getSemester().compareTo(container.schGUI.sch.currentSemester)<0){
				if(!FurmanOfficial.masterIsAround){
					toRemove.setEnabled(false);
				}
				elementLabel.setForeground(FurmanOfficial.grey(170));
			}
		}
		
		toRemove.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				removeSelf();
			}
		});

		removePanel.add(toRemove);
		this.add(removePanel, BorderLayout.EAST);
	}





	/**
	 * This should only be called if the schedule element is a requirement.
	 * It makes the button that either says "Add a course" or "No courses available."
	 */
	public void updateDropDown(){
		Semester thisSemester = container.getSemester();
		SemesterDate date = thisSemester.getDate();
		Requirement r = (Requirement)s;
		//collect all the courses that:
		// haven't been taken, 
		// are in this semester,
		// and satisfy this requirement.
		ArrayList<Course> listOfCourses = CourseList.getCoursesSatisfying(
				CourseList.inSemester(date)
				.and(CourseList.satisfiesRequirement(r))
				.and( CourseList.inSchedule(thisSemester.schedule).negate() )
				);
		ArrayList<ScheduleCourse> finalList = ScheduleCourse.toScheduleCourses(listOfCourses, thisSemester.schedule);
		//TODO is this necessary? finalList = thisSemester.schedule.filterAlreadyChosenCourses(finalList);
		coursesToChooseFrom = finalList.toArray(new ScheduleCourse[finalList.size()]);
		//Change what the user sees based on the size of coursesToChooseFrom
		//TODO do we ever take things out of removePanel?
		if(coursesToChooseFrom.length>0){
			//TODO what is stack doing?
			JPanel stack = new JPanel();
			stack.setBackground(this.getBackground());
			removePanel.add(addCourse);
			this.add(stack, BorderLayout.CENTER);
		}	
		else{
			JLabel noCourse = new JLabel("No courses avaliable");
			noCourse.setFont(FurmanOfficial.normalFont);
			noCourse.setBackground(FurmanOfficial.darkPurple);
			noCourse.setOpaque(true);
			noCourse.setForeground(Color.white);
			removePanel.add(noCourse);
		}
	}
	
	/**
	 * called when the user clicks 'choose a course'
	 */
	public void chooseACourse(){
		ScheduleCourse c = container.schGUI.chooseCourse(coursesToChooseFrom);
		if(c != null){
			container.schGUI.elementChanged(container,this, c);
		}
	}
	public void removeSelf(){
		container.removeElement(this);
	}
	


	public class SEPDragHandler extends ComponentDragHandler{

		@Override
		public void initiateDrag(JComponent toBeDragged) {

			//alert the driver of the change
			//container.d.dragStarted(toBeDragged);
			container.schGUI.dragStarted(s);
		}

		@Override
		public void afterDrop(Container source, JComponent dragged,
				boolean moveAction) {

			container.schGUI.dragEnded();
		}

	}





}
















