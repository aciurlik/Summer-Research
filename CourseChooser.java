import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.function.Predicate;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;



/**
 * blurb written: 7/23/2017
 * last updated: 7/23/2017
 * 
 * This class displays a list of courses for the user to choose from,
 * and offers options to sort or filter these courses to find
 * the ones you want.
 * 
 * it is in the GUI group of classes.
 *
 */
public class CourseChooser extends JPanel implements FocusListener, ActionListener{
	private static final long serialVersionUID = 1L;
	ScheduleCourse[] choices;
	//the courses the user can choose from. Does not change as the user filters.
	ArrayList<ScheduleCourse> displayChoices;
	//the courses that the user is shown - changes based on filtering.
	ArrayList<Requirement> reqs; //the set of requirements 
	// loaded in the schedule when the courseChooser was created.

	int maxNumColumns;
	// used to determine how far out the courses should display 
	// (the num cols changes based on the number of reqs a course satisfies)
	// so that the table doesn't throw errors.
	
	JPanel northPanel;
	JButton toggleFiltersButton;
	boolean filtersVisible;
	FiltersPanel filtersPanel;
	
	JPanel coursesPanel;
	JTable visibleCoursesTable;
	
	
	
	//Used in filterPanel
	// times between 5:00AM and 9:00 PM into the time filter choices.
	public static final Time[] timesToChooseFromWhenFiltering;
	static{
		ArrayList<Time> times = new ArrayList<Time>();
		int minBetweenChoices = 15;
		int numChoices = 4 * 18;//18 hours
		Time currentTime = Time.tryRead("5:00AM");
		for(int i = 0; i < numChoices ; i ++ ){
			times.add(currentTime);
			currentTime = currentTime.addMinutes(minBetweenChoices);
		}
		timesToChooseFromWhenFiltering = times.toArray(new Time[times.size()]);
	}
	
	
	
	
	
	
	


	/**
	 * Convenience method to do the course choosing without creating a CourseChooser
	 * object.
	 * 
	 * May return null.
	 * 
	 * @param courses: the set of courses to choose from
	 * @param reqs: the set of requirements currently loaded into the schedule
	 * @return the course, or else null if none was chosen.
	 */
	public static ScheduleCourse chooseCourse(ScheduleCourse[] courses, ArrayList<Requirement> reqs){
		CourseChooser c = new CourseChooser(courses, reqs);
		
		int chosen = JOptionPane.showConfirmDialog(null,
				c,
				"Choose a course",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		
		
		if(chosen == JOptionPane.OK_OPTION){
			return c.getChosenCourse();
		}
		else{
			return null;
		}
	}

	
	public CourseChooser(ScheduleCourse[] courses, ArrayList<Requirement> reqs){
		super(); //JPanel constructor
		
		//Data fields
		this.choices = courses;
		this.reqs = reqs;
		//note - once, we removed all reqs that were fulfilled.
		// however, if you drag a req in the schedule and make the req fulfilled 
		// on that move, then when you go to choose a course for that req
		// you won't get to see the req you're choosing for in the table.
		// This is very bad for NW/NWL, because you can't tell any more
		// which things are NW and which are NWL.
		
		maxNumColumns = 0;
		for(ScheduleCourse c : courses){
			maxNumColumns = Math.max(dataFor(c).size(), maxNumColumns);
		}
		
		//GUI fields
		filtersVisible = false;
		filtersPanel = new FiltersPanel();
		
		
		//Construction of the GUI
		this.setLayout(new BorderLayout());
		
		northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		
		toggleFiltersButton = new JButton(MenuOptions.showCourseFiltersText);
		toggleFiltersButton.addActionListener(this);
		
		
		recalcDisplayChoices();
		updateDisplay();
	}	
	
	/////////////////////////
	/////////////////////////
	//// GUI Updates
	/////////////////////////
	/////////////////////////
	@SuppressWarnings("unused")
	private boolean ___GUIUpdates_________;
	
	/**
	 * use displayChoices and filtersVisible to recalculate all other
	 *  values necessary and update the display.
	 */
	public void updateDisplay(){
		this.removeAll();
		
		
		//Collect the data to be displayed in the table
		Object[][] data = new Object[displayChoices.size()][maxNumColumns];
		for(int i = 0; i < displayChoices.size() ; i ++){
			ArrayList<Object> dataList = dataFor(displayChoices.get(i));
			Object[] fullList = new Object[maxNumColumns];
			for(int j  = 0; j < dataList.size(); j ++){
				fullList[j] = dataList.get(j);
			}
			data[i] = fullList;
		}
		
		//make the table
		visibleCoursesTable = new JTable(data, columnNames(maxNumColumns));
		
		
		//This section of code ensures that the table's values are not editable
		DefaultTableModel tableModel = new DefaultTableModel(data, columnNames(maxNumColumns)) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       return false;
		    }
		};
		visibleCoursesTable.setModel(tableModel);
		
		//This section of code lets the table sort when necessary.
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(visibleCoursesTable.getModel());
		visibleCoursesTable.setRowSorter(sorter);
		ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<RowSorter.SortKey>();
		  //initial sort based on column 1
		sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		
		//Set column widths.
		// Must occur after the above code that sets the columnModel.
		visibleCoursesTable.getColumnModel().getColumn(2).setPreferredWidth(200); //title
		visibleCoursesTable.getColumnModel().getColumn(3).setPreferredWidth(150); //professor
		//visibleCoursesTable.getColumnModel().getColumn(5).setPreferredWidth(50);
		
		
		
		
		
		JScrollPane scrollPane = new JScrollPane(visibleCoursesTable);
		visibleCoursesTable.setFillsViewportHeight(true);
		this.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setPreferredSize(new Dimension(
				visibleCoursesTable.getPreferredSize().width,
				300));
		
		
		showFilters(filtersVisible);
		this.add(northPanel, BorderLayout.NORTH);
		
		this.revalidate();
		this.repaint();
	}
	
	public void showFilters(boolean show){
		northPanel.removeAll();
		northPanel.add(toggleFiltersButton, BorderLayout.NORTH);
		if(show){
			northPanel.add(filtersPanel, BorderLayout.CENTER);
		}
	}
	
	/////////////////////////
	/////////////////////////
	//// Data Updates
	/////////////////////////
	/////////////////////////
	@SuppressWarnings("unused")
	private boolean ___DataUpdates_________;
	
	/**
	 * Make one row of data for this course
	 * 
	 *
	 * @param c
	 * @return
	 */
	public ArrayList<Object> dataFor(ScheduleCourse c){
		//If you change this method, be sure to update the method
		// getColumnNames to reflect the change.
		ArrayList<Object> results = new ArrayList<Object>();
		
		
		//Reqs that this course fulfills
		ArrayList<Requirement> reqsFulfilled = c.filterEnemyRequirements(reqs, false);
		
		//Special case for NW and NWL
		for(int i = 0; i < reqsFulfilled.size() ; i ++){
			Requirement r = reqsFulfilled.get(i);
			if("NW/NWL".equals(r.name)){
				Prefix p = c.getPrefix();
				Requirement newReq = new Requirement();
				
				if(CourseList.isNWL(p)){
					newReq.setName("NWL");
					reqsFulfilled.set(i,newReq);
				}
				else{
					newReq.setName("NW");
					reqsFulfilled.set(i,newReq);
				}
			}
		}
		
		//start time, prefix, professor
		Time startTime = c.c.getStartTime();
		Prefix prefix = c.getPrefix();
		String professor = c.c.professor;
				
		
		if(startTime != null) 
			results.add(startTime.clockTime());
		else
			results.add(null);
		results.add(prefix);
		results.add(c.c.getName());
		results.add(professor);
		results.add(c.c.creditHours);
		results.add(reqsFulfilled.size());
		for(Requirement r: reqsFulfilled){
			results.add(r.shortString(10));
		}
		//results.add(Arrays.toString(c.c.meetingDays));
		return results;
	}
	/**
	 * Get the column names.
	 * This method should reflect the return values from
	 *  the method dataFor(Course)
	 *  
	 * For the last few columns, which are requirements that satisfy the course,
	 * just use blank column names.
	 * 
	 * @param numberColumns
	 * @return
	 */
	public String[] columnNames(int numberColumns){
		String[] result = new String[numberColumns];
		String[] known = new String[]{
				"Start Time","Course","Title","Professor","Credit Hours","Num new Reqs"};
		for(int i = 0; i < known.length ; i ++){
			result[i] = known[i];
		}
		for(int i = known.length; i < numberColumns ; i ++){
			result[i] = "";
		}
		return result;
	}
	
	
	

	
	
	public void recalcDisplayChoices(){
		ArrayList<ScheduleCourse> result = new ArrayList<ScheduleCourse>();
		
		//Filter the list of choices
		ArrayList<Predicate<Course>> filters = filtersPanel.getFilters();
		for(ScheduleCourse c : choices){
			if(allApply(filters, c)){
				result.add(c);
			}
		}
		displayChoices = result;
	}

	
	
	
	
	
	
	
	
	
	public ScheduleCourse getChosenCourse(){
		int index = visibleCoursesTable.getSelectedRow();
		if(index == -1){
			return null;
		}
		else{
			int actualIndex = visibleCoursesTable.convertRowIndexToModel(index);
			return displayChoices.get(actualIndex);
		}
	}
	
	

	

	

	
	
	
	
	
	
	
	
	/////////////////////////
	/////////////////////////
	////Prevent focus being lost
	/////////////////////////
	/////////////////////////
	@SuppressWarnings("unused")
	private boolean ___keepFocus_________;
	

	@Override
	public void focusGained(FocusEvent e) {
	}
	
	/*
	 * 
	 * Keep the focus until you're closed.
	 */
	@Override
	public void focusLost(FocusEvent e) {
		if(this.isVisible()){
			this.requestFocus();
		}
	}
	
	
	
	/////////////////////////
	/////////////////////////
	////MISC
	/////////////////////////
	/////////////////////////
	@SuppressWarnings("unused")
	private boolean ___miscellaneous_________;
	
	



	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == toggleFiltersButton){
			filtersVisible = !filtersVisible;
			if(filtersVisible){
				toggleFiltersButton.setText(MenuOptions.hideCourseFiltersText);
			}
			else{
				toggleFiltersButton.setText(MenuOptions.showCourseFiltersText);
			}
			updateDisplay();
		}
	}
	
	
	/**
	 * Check whether each predicate evaluates to true on c.
	 * 
	 * Essentially the AND of a bunch of predicates.
	 * 
	 * Used for filtering.
	 * @param filters
	 * @param c
	 * @return
	 */
	public boolean allApply(ArrayList<Predicate<Course>> filters, ScheduleCourse c){
		for(Predicate<Course> p : filters){
			if(!p.test(c.c)){
				return false;
			}
		}
		return true;
	}
	
	
	
	/**
	 * 
	 * This class represents the panel that can be shown or hidden in courseChooser, 
	 * which the user can use to create filters on the list of courses.
	 * 
	 * When other classes are ready to get the list of filters, 
	 * use the getFilters() method. The result will be a
	 * list of Predicate<Course> objects (a Predicate<Course> is a function
	 * that takes in a course and returns a boolean) and a course c
	 * can pass through the filters if each filter returns true when
	 * evaluated on c. You can then convenience method allApply(filters, course)
	 * to see if a course can pass through a list of filters 
	 * 
	 */
	private class FiltersPanel extends JPanel implements ActionListener{
	
		
		private static final long serialVersionUID = 1L;
		JToggleButton[] meetingDaysButtons;
		JTextField profNameField;
		JTextField subjField;
		
		JComboBox<String> startOfTimeRange; //filter all courses who's start times
		// are after startOfTimeRange.
		JComboBox<String> endOfTimeRange;// filter all courses who's start times
		// are after endOfTimeRange
		
		JButton applyButton; //the user can force an update if they need to, like
		// for giving data to the professor name textField.
		
	
		
		
		
		
		public FiltersPanel(){
			super();
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			
			//Toggle buttons for the meeting days
			meetingDaysButtons = new JToggleButton[5];
			int index = 0;
			JPanel toggleButtonsPanel = new JPanel();
			toggleButtonsPanel.add(new JLabel("Meets on"));
			for(int i = Time.MONDAY; i < Time.SATURDAY ; i ++){
				JToggleButton b = new JToggleButton(Time.dayCode(i));
				toggleButtonsPanel.add(b);
				meetingDaysButtons[index] = b;
				b.addActionListener(this); //revalidate when pressed
				index ++;
			}
			this.add(toggleButtonsPanel);
			
			
			//Professor names
			JPanel professorNamePanel = new JPanel();
			profNameField = new JTextField();
			profNameField.setColumns(5);
			profNameField.setText("");
			profNameField.addActionListener(this);//revalidate when typed
			professorNamePanel.add(new JLabel("Professor name contains"));
			professorNamePanel.add(profNameField);
			//this.add(professorNamePanel);
			
			//subject 
			JPanel subjFieldPanel = new JPanel();
			subjField = new JTextField();
			subjField.setColumns(4);
			subjField.setText("");
			subjField.addActionListener(this);
			subjFieldPanel.add(new JLabel("Subject (3 letters)"));
			subjFieldPanel.add(subjField);
			//this.add(subjFieldPanel);
			
			JPanel fieldsPanel = new JPanel();
			fieldsPanel.add(professorNamePanel);
			fieldsPanel.add(subjFieldPanel);
			this.add(fieldsPanel);
			
			
			//Times
			JPanel timesPanel = new JPanel();
			String[] timeStrings = new String[timesToChooseFromWhenFiltering.length];
			for(int i = 0; i < timesToChooseFromWhenFiltering.length ; i ++){
				timeStrings[i] = timesToChooseFromWhenFiltering[i].clockTime();
			}
			startOfTimeRange = new JComboBox<String>(timeStrings);
			endOfTimeRange = new JComboBox<String>(timeStrings);
			
			int last = timeStrings.length - 1;
			endOfTimeRange.setSelectedIndex(last);
			
			startOfTimeRange.addActionListener(this);
			endOfTimeRange.addActionListener(this);
			timesPanel.add(new JLabel("Starts after:"));
			timesPanel.add(startOfTimeRange);
			timesPanel.add(new JLabel("Starts before:"));
			timesPanel.add(endOfTimeRange);
			this.add(timesPanel);
			
			
			//Not sure why, but wrapping the button in a panel helped the
			// BoxLayout to center the button (it was skewed to the right before).
			JPanel applyPanel = new JPanel();
			applyButton = new JButton("Apply");
			applyButton.addActionListener(this); //revalidate when pressed
			applyPanel.add(applyButton);
			this.add(applyPanel);
		}
		
		
		
		/**
		 * Collect the list of filters from this panel.
		 * Each filter is a Predicate<Course>, which is a function
		 * that takes a course and returns a boolean. A course can pass through
		 * the filter f if the f(Course) == true.
		 * @return
		 */
		public ArrayList<Predicate<Course>> getFilters(){
			
			ArrayList<Predicate<Course>> result = new ArrayList<Predicate<Course>>();
			
			//meeting days filters
			int i = Time.MONDAY;
			for(JToggleButton b : meetingDaysButtons){
				final int day = i;
				if(b.isSelected()){
					result.add(
						//The predicate that checks if c meets on 'day'.
						new Predicate<Course>(){
						@Override
						public boolean test(Course c) {
							for(int j : c.meetingDays){
								if(j == day){
									return true;
								}
							}
							return false;
						}
						
					});
				}
				i++;
			}
			
			
			//Professor name filter
			String profText = profNameField.getText().toUpperCase();
			if(!profText.equals("")){
				result.add(c -> c.professor.toUpperCase().contains(profText));
			}
			
			//subject filter
			
			String subjFilter = subjField.getText().toUpperCase();
			if(!subjFilter.equals("")){
				result.add(c -> c.coursePrefix.getSubject().contains(subjFilter));
			}
			
			
			//Times filter
			Time startTime = timesToChooseFromWhenFiltering[startOfTimeRange.getSelectedIndex()];
			Time endTime = timesToChooseFromWhenFiltering[endOfTimeRange.getSelectedIndex()];
			final Interval<Time> validStartInterval = new Interval<Time>(startTime, endTime);
			result.add(c -> c.getStartTime() == null || validStartInterval.contains(c.getStartTime(), true));
			
			
			return result;
		}



		@Override
		/**
		 * if the user does an action on any of the filter GUI things, 
		 * do an update of the course table.
		 */
		public void actionPerformed(ActionEvent e) {
			recalcDisplayChoices();
			updateDisplay();
		}
	}
	
	
	
	
}
