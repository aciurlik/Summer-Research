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
 * This class displays a list of courses for the user to choose from,
 * and offers options to sort or filter these courses to find
 * the ones you want.
 *
 */
public class CourseChooser extends JPanel implements FocusListener, ActionListener{
	private static final long serialVersionUID = 1L;
	ScheduleCourse[] choices;
	int maxNumColumns;
	ArrayList<Requirement> reqs;
	ArrayList<ScheduleCourse> displayChoices;
	FiltersPanel filtersPanel;
	
	boolean advancedSettingsVisible; //tells whether the user has clicked the
	// button to open advanced settings.
	public static final String showText = "Show Filters";
	public static final String hideText = "Hide Filters";
	JPanel advancedSettingsPanel;
	JButton advancedSettingsButton;
	JPanel coursesPanel;
	JTable visibleCoursesTable;
	
	boolean finishedChoosing;
	
	
	//Used in filterPanel
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
		//We only care about unfulfilled requirements
		// However, if we're choosing a course for a requirement that is fulfilled,
		// we still want to see that the courses all satisfy that requirement, 
		// so I'm taking this code out.
				/*new ArrayList<Requirement>();
		for(Requirement r : reqs){
			if(!r.storedIsComplete()){
				this.reqs.add(r);
			}
		}*/
		finishedChoosing = false;
		maxNumColumns = 0;
		for(ScheduleCourse c : courses){
			maxNumColumns = Math.max(dataFor(c).size(), maxNumColumns);
		}
		
		//GUI fields
		advancedSettingsVisible = false;
		filtersPanel = new FiltersPanel();
		//this.setPreferredSize(new Dimension(700,200));
		
		
		//Construction of the GUI
		this.setLayout(new BorderLayout());
		
		advancedSettingsPanel = new JPanel();
		advancedSettingsPanel.setLayout(new BorderLayout());
		
		advancedSettingsButton = new JButton(showText);
		advancedSettingsButton.addActionListener(this);
		
		
		
		recalcDisplayChoices();
		updateDisplay();
		
		
	}	
	
	
	
	/**
	 * Assuming that displayChoices and advancedSettingsVisible are
	 * both up to date, recalculate all other values necessary 
	 * and update the display.
	 */
	public void updateDisplay(){
		this.removeAll();
		
		
		Object[][] data = new Object[displayChoices.size()][maxNumColumns];
		for(int i = 0; i < displayChoices.size() ; i ++){
			ArrayList<Object> dataList = dataFor(displayChoices.get(i));
			Object[] fullList = new Object[maxNumColumns];
			for(int j  = 0; j < dataList.size(); j ++){
				fullList[j] = dataList.get(j);
			}
			data[i] = fullList;
		}
		
		
		visibleCoursesTable = new JTable(data, columnNames(maxNumColumns));
		
		//set table's column width
		CourseChooser.setJTableColumnsWidth(visibleCoursesTable, 
				visibleCoursesTable.getPreferredSize().width,
				columnSizes(maxNumColumns));
		
		
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
		sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		
		
		
		JScrollPane scrollPane = new JScrollPane(visibleCoursesTable);
		visibleCoursesTable.setFillsViewportHeight(true);
		this.add(scrollPane, BorderLayout.CENTER);
		scrollPane.setPreferredSize(new Dimension(
				visibleCoursesTable.getPreferredSize().width,
				300));
		
		
		
		
		showAdvancedSettings(advancedSettingsVisible);
		this.add(advancedSettingsPanel, BorderLayout.NORTH);
		
		this.revalidate();
		this.repaint();
	}
	public void showAdvancedSettings(boolean show){
		advancedSettingsPanel.removeAll();
		advancedSettingsPanel.add(advancedSettingsButton, BorderLayout.NORTH);
		if(show){
			advancedSettingsPanel.add(filtersPanel, BorderLayout.CENTER);
		}
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
	
	
	public ArrayList<Object> dataFor(ScheduleCourse c){
		ArrayList<Object> results = new ArrayList<Object>();
		
		ArrayList<Requirement> reqsFulfilled = c.getRequirementsFulfilled(reqs, false);
		
		//Filter out reqs that are already complete
		// This code made it so that if you pick "choose a course"
		// from a req that is complete, it won't show you which courses
		// fulfill that req (meaning you can't see which ones are NW and
		// NWL for that particular requirement.)
		/*
		for(int i = 0; i < reqsFulfilled.size() ; i ++){
			Requirement r = reqsFulfilled.get(i);
			if(r.storedIsComplete()){
				reqsFulfilled.remove(i);
				i--;
			}
		}*/
		
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
		results.add(reqsFulfilled.size());
		results.addAll(reqsFulfilled);
		//results.add(Arrays.toString(c.c.meetingDays));
		return results;
	}
	public String[] columnNames(int numberColumns){
		String[] result = new String[numberColumns];
		String[] known = new String[]{
				"Start Time","Course","Title","Professor","Num new Reqs"};
		for(int i = 0; i < known.length ; i ++){
			result[i] = known[i];
		}
		for(int i = known.length; i < numberColumns ; i ++){
			result[i] = "";
		}
		return result;
	}
	
	/**
	 * Find column sizes as percent of total
	 */
	public double[] columnSizes(int numColumns){
		double[] result = new double[numColumns];
		double[] known = new double[]{5,5,20,10,2};
		for(int i = 0; i < known.length ; i ++){
			result[i] = known[i];
		}
		for(int i = known.length ; i < numColumns ; i ++){
			result[i] = 5;
		}
		return rescale(result);
	}
	public double[] rescale(double[] input){
		double sum = 0;
		for(double d : input){
			sum += d;
		}
		double[] result = new double[input.length];
		for(int i =0 ; i < result.length ; i ++){
			result[i] = input[i] / sum * 100;
		}
		return result;
	}
	
	
	/**
	 * Sets a JTable's column widths 
	 * Found on 
	 * http://www.codejava.net/java-se/swing/setting-column-width-and-row-height-for-jtable
	 * @param table
	 * @param tablePreferredWidth
	 * @param percentages
	 */
	public static void setJTableColumnsWidth(JTable table, int tablePreferredWidth,
	        double[] percentages) {
	    double total = 0;
	    for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
	        total += percentages[i];
	    }
	 
	    for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
	        TableColumn column = table.getColumnModel().getColumn(i);
	        column.setPreferredWidth((int)
	                (tablePreferredWidth * (percentages[i] / total)));
	    }
	}

	
	
	
	
	
	
	
	
	public boolean allApply(ArrayList<Predicate<Course>> filters, ScheduleCourse c){
		for(Predicate<Course> p : filters){
			if(!p.test(c.c)){
				return false;
			}
		}
		return true;
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



	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if(source == advancedSettingsButton){
			advancedSettingsVisible = !advancedSettingsVisible;
			if(advancedSettingsVisible){
				advancedSettingsButton.setText(hideText);
			}
			else{
				advancedSettingsButton.setText(showText);
			}
			updateDisplay();
		}
	}
	
	private class FiltersPanel extends JPanel implements ActionListener{
	
		
		private static final long serialVersionUID = 1L;
		String professorStartString;
		Time startTime;
		Time endTime;
		JToggleButton[] meetingDaysButtons;
		JButton applyButton;
		JTextField profNameField;
		JComboBox<String> startTimeRange;
		JComboBox<String> endTimeRange;
		
	
		
		
		
		
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
			this.add(professorNamePanel);
			
			//Times
			JPanel timesPanel = new JPanel();
			String[] timeStrings = new String[timesToChooseFromWhenFiltering.length];
			for(int i = 0; i < timesToChooseFromWhenFiltering.length ; i ++){
				timeStrings[i] = timesToChooseFromWhenFiltering[i].clockTime();
			}
			startTimeRange = new JComboBox<String>(timeStrings);
			endTimeRange = new JComboBox<String>(timeStrings);
			
			int last = timeStrings.length - 1;
			endTimeRange.setSelectedIndex(last);
			
			startTimeRange.addActionListener(this);
			endTimeRange.addActionListener(this);
			timesPanel.add(new JLabel("Starts after:"));
			timesPanel.add(startTimeRange);
			timesPanel.add(new JLabel("Starts before:"));
			timesPanel.add(endTimeRange);
			this.add(timesPanel);
			
			
			//Not sure why, but wrapping the button in a panel helped the
			// BoxLayout to center the button (it was skewed to the right before).
			JPanel applyPanel = new JPanel();
			applyButton = new JButton("Apply");
			applyButton.addActionListener(this); //revalidate when pressed
			applyPanel.add(applyButton);
			this.add(applyPanel);
			
		}
		
		
		
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
			String profText = profNameField.getText();
			if(!profText.equals("")){
				result.add(c -> c.professor.contains(profText));
			}
			
			
			//Times filter
			Time startTime = timesToChooseFromWhenFiltering[startTimeRange.getSelectedIndex()];
			Time endTime = timesToChooseFromWhenFiltering[endTimeRange.getSelectedIndex()];
			final Interval<Time> validStartInterval = new Interval<Time>(startTime, endTime);
			result.add(c -> c.getStartTime() == null || validStartInterval.contains(c.getStartTime(), true));
			
			
			return result;
		}



		@Override
		public void actionPerformed(ActionEvent e) {
			recalcDisplayChoices();
			updateDisplay();
		}
	}
	
	
	
	
}
