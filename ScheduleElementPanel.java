import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * 
 * Blurb Written: 7/31/2017
 * Last Updated: 7/31/2017
 * 
 * This is the panel that is dragged and dropped into a SemesterPanel
 * it allows the user to change it into a scheduledCourse. This has the dragHandler attached
 * that allows it to me moved once placed. 
 *
 */

public class ScheduleElementPanel extends JPanel{
	

	private ScheduleElement s;
	private Dimension buttonSize = new Dimension(20, 20);
	public SemesterPanel container;
	int nimbusWidth = 40;
	int nimbusHeight = 20;
	JPanel removePanel = new JPanel();
	ScheduleCourse[] coursesToChooseFrom; //used if this panel represents a requirement
	// and the user wants to choose a course satisfying it.
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
		addCourse.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				chooseACourse(); //User clicking this will open the Choose Course Menu
			}
		});
		if(s instanceof ScheduleCourse){
			if(((ScheduleCourse) s).getSemester().compareTo(container.schGUI.sch.currentSemester)<0){
				return; //If this scheduleCourse is before the current semester it should be made
				//so it can't be moved because the drag handler is not set up. 
			}
		}
		this.setTransferHandler(new SEPDragHandler()); //Allows the panel to be dragged. 
		this.addMouseListener(ComponentDragHandler.getDragListener());
	}

	/**
	 * This gives the data that is associated with the scheduleElement. 
	 * @return
	 */
	public ScheduleElement getElement(){
		return s; 
	}

	/**
	 * This method should only be called if the user selected a course 
	 * from the dropdown. Since this was changed from dropDown to chooser this is no longer used. 
	 * But I am keeping it in case I am mistaken. 
	 */
	//public void dropdownSelected(){
	//	ScheduleElement e = (ScheduleElement) this.requirementDropDown.getSelectedItem();
	//	container.schGUI.elementChanged(container, this, e);
	// }


	private String removeButtonText = "x";
	public void updatePanel(){ //This can be taken out later
		String display = s.shortString(100);
		JLabel elementLabel = new JLabel(display);
		elementLabel.setFont(FurmanOfficial.normalFont);
		this.add(elementLabel, BorderLayout.WEST);
		if(s instanceof Requirement) {
			updateCourseOptions();
		}
		//Adds the remove Button
		removePanel.setOpaque(false);
		JButton toRemove = new JButton(removeButtonText);
		if(MenuOptions.nimbusLoaded){ //If this is not in place the Nimbus cuts off the text of a button
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
					toRemove.setEnabled(false); //Not deletable. 
				}
				elementLabel.setForeground(FurmanOfficial.grey(170));
			}
		}
		toRemove.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				removeSelf(); //Deletes from GUI. 
			}
		});
		removePanel.add(toRemove);
		this.add(removePanel, BorderLayout.EAST);
	}





	/**
	 * This should only be called if the schedule element is a requirement.
	 * It makes the button that either says "Add a course" or "No courses available."
	 */
	public void updateCourseOptions(){
		removePanel.removeAll();
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
		//Is this necessary? finalList = thisSemester.schedule.filterAlreadyChosenCourses(finalList); --> This does the same as the inSchedule(negate) kept 
		// in case we switch back to use the filter
		coursesToChooseFrom = finalList.toArray(new ScheduleCourse[finalList.size()]);
		//Change what the user sees based on the size of coursesToChooseFrom
		if(coursesToChooseFrom.length>0){
			removePanel.add(addCourse);
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
	
	
	/**
	 * removes this scheduleElement from the semester
	 */
	public void removeSelf(){
		container.removeElement(this);
	}
	

	/**
	 * This is given to the scheduleElement to allow it to be dgragged. 
	 * 
	 *
	 */
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
















