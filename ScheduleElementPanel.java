


import java.awt.Container;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;



public class ScheduleElementPanel extends JPanel {
	private int updateCount = 0;
	private ScheduleElement s;
	private SemesterPanel container;


	//	Driver coursesSatisfy = new Driver();
	JComboBox<ScheduleElement>  requirmentDropDown = new JComboBox<ScheduleElement>();

	public ScheduleElementPanel(ScheduleElement s, SemesterPanel container) {
		super();
		this.s=s;
		this.container = container;

		this.setTransferHandler(new SEPDragHandler());
		this.addMouseListener(ComponentDragHandler.getDragListener());

	}

	public ScheduleElement getElement(){
		return s;
	}




	public void updatePanel(){ //This can be taken out later
		JLabel elementLabel = new JLabel(s.getDisplayString());
		this.add(elementLabel);
		if(s instanceof Requirement) {
			updateDropDown();
		}
	}
	//If course is dropped then no dropDown Panel is needed




	/**
	 * This should only be called if the schedule element is a requirement.
	 */
	public void updateDropDown(){
		Requirement r = (Requirement)this.s;
		this.requirmentDropDown.removeAllItems();
		//Find the list of courses that might satisfy this requirement
		ArrayList<Course> listOfCourses = container.getSemester().getCoursesSatisfying(r);
		for( Course c : listOfCourses){
			requirmentDropDown.addItem(c);
		}
		this.add(requirmentDropDown);
	}


	public class SEPDragHandler extends ComponentDragHandler{

		@Override
		public void initiateDrag(JComponent toBeDragged) {

		}

		@Override
		public void afterDrop(Container source, JComponent dragged,
				boolean moveAction) {
			container.removeElement((ScheduleElementPanel) dragged);
			//container.d.reqs.update();
			//container.d.reqs.revalidate();
			//	container.d.reqs.repaint();

		}

	}



}










