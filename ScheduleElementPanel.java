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
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int updateCount = 0;
	private ScheduleElement s;
	public SemesterPanel container;
	private Dimension buttonSize = new Dimension(20, 20);
	private String removeButtonText = "x";
	public ScheduleElementPanel reference = this;
	int nimbusWidth = 40;
	int nimbusHeight = 20;
	JPanel remove = new JPanel();


	//	Driver coursesSatisfy = new Driver();
	JComboBox<ScheduleElement>  requirementDropDown;
	JButton addCourse = new JButton (MenuOptions.addCourseWithRequirement);




	public ScheduleElementPanel(ScheduleElement s, SemesterPanel container) {

		super();
		this.s=s;
		this.reference=this;
		this.container = container;
		this.setBackground(FurmanOfficial.grey(30));
		this.setLayout(new BorderLayout());




		if(s instanceof ScheduleCourse){
			if(((ScheduleCourse) s).isTaken()){
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
		container.d.GUIElementChanged(container, this, e);
	}



	public void updatePanel(){ //This can be taken out later
		String display = s.getDisplayString();
		if(display.length() > 100){
			display = s.shortString(100);
		}
		JLabel elementLabel = new JLabel(display);
		elementLabel.setFont(FurmanOfficial.normalFont);
		this.add(elementLabel, BorderLayout.WEST);
		if(s instanceof Requirement) {
			updateDropDown();
		}


		//Adds the remove Button
		
		remove.setOpaque(false);
		JButton toRemove = new JButton(removeButtonText);
		if(MenuOptions.UIType){
			toRemove.setPreferredSize(new Dimension(nimbusWidth, nimbusHeight));
			toRemove.setMargin(new Insets(1,1,1,1));
			

		}
		else{
			toRemove.setPreferredSize(buttonSize);
		}
		
		if(s instanceof ScheduleCourse){
			if (((ScheduleCourse) s).isTaken()){
				toRemove.setEnabled(false);
				elementLabel.setForeground(FurmanOfficial.grey(170));
			}
		}
		toRemove.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				removeSelf();
			}
		});

		remove.add(toRemove);
		this.add(remove, BorderLayout.EAST);
	}





	/**
	 * This should only be called if the schedule element is a requirement.
	 * It makes the button that either says "Add a course" or "No courses available."
	 */
	public void updateDropDown(){
		ArrayList<ScheduleCourse> listOfCourses = container.getSemester().getCoursesSatisfying((Requirement)s);
		ArrayList<ScheduleCourse> finallistOfCourses = container.sem.schedule.filterAlreadyChosenCourses(listOfCourses);
		final ScheduleCourse[] allPossibleCourses = finallistOfCourses.toArray(new ScheduleCourse[finallistOfCourses.size()]);
		if(finallistOfCourses.size()>0){
			addCourse.setActionCommand(MenuOptions.addCourseWithRequirement);
			addCourse.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					ScheduleCourse c = container.d.GUIChooseCourse(allPossibleCourses);
					if(c != null){
						container.d.GUIElementChanged(container, reference , c);
					}
				}
			});
			JPanel stack = new JPanel();
			stack.setBackground(this.getBackground());
			addCourse.setPreferredSize(new Dimension(130,20));
			remove.add(addCourse);
			this.add(stack, BorderLayout.CENTER);
		}	
		else{
			JLabel noCourse = new JLabel("No courses avaliable");
			noCourse.setFont(FurmanOfficial.normalFont);
			noCourse.setBackground(FurmanOfficial.official);
			noCourse.setOpaque(true);
			noCourse.setForeground(Color.white);
			remove.add(noCourse);

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
			container.d.dragStarted(s);
		}

		@Override
		public void afterDrop(Container source, JComponent dragged,
				boolean moveAction) {

			container.d.dragEnded();
		}

	}


	/*void dndDone(){
		System.out.println("hi");
	}
	 */



}
















