import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.RowSorter;
import javax.swing.SortOrder;
import javax.swing.table.DefaultTableModel;
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
		finishedChoosing = false;
		maxNumColumns = 0;
		for(ScheduleCourse c : courses){
			maxNumColumns = Math.max(dataFor(c).size(), maxNumColumns);
		}
		
		//GUI fields
		advancedSettingsVisible = false;
		filtersPanel = new FiltersPanel();
		
		
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
		sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
		sorter.setSortKeys(sortKeys);
		
		
		//TODO set column widths so you can see the titles
		
		
		JScrollPane scrollPane = new JScrollPane(visibleCoursesTable);
		visibleCoursesTable.setFillsViewportHeight(true);
		this.add(scrollPane, BorderLayout.CENTER);
		
		
		
		
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
		//Add column descriptors to advancesSettingsPanel.south.
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
		ArrayList<Requirement> reqsFulfilled = new ArrayList<Requirement>();
		for(Requirement r : reqs){
			if(r.isSatisfiedBy(c.getPrefix())){
				reqsFulfilled.add(r);
			}
		}
		
		Time startTime = null;
		if(c.c.meetingTime != null && c.c.meetingTime[0]!= null){
			startTime = c.c.meetingTime[0];
		}
		
		Prefix prefix = c.getPrefix();
		String professor = c.c.professor;
				
		
		if(startTime != null) 
			results.add(startTime.clockTime());
		results.add(prefix);
		results.add(professor);
		results.add(reqsFulfilled.size());
		results.addAll(reqsFulfilled);
		//results.add(Arrays.toString(c.c.meetingDays));
		return results;
	}
	public String[] columnNames(int numberColumns){
		String[] result = new String[numberColumns];
		result[0] = "Start Time";
		result[1] = "Subj-Num";
		result[2] = "Professor";
		result[3] = "Num new Reqs";
		for(int i = 4; i < numberColumns ; i ++){
			result[i] = "";
		}
		return result;
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
			
			//TODO Work on a rangeSlider for times
			
			
			
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
			
			
			//Professor names
			String profText = profNameField.getText();
			if(!profText.equals("")){
				result.add(c -> c.professor.contains(profText));
			}
			
			
			return result;
		}



		@Override
		public void actionPerformed(ActionEvent e) {
			recalcDisplayChoices();
			updateDisplay();
		}
	}
	
	
	
	
}
