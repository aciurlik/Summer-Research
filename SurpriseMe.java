import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;



public class SurpriseMe extends JPanel implements ActionListener, Runnable {

	

	
	String showGEROption;
	ScheduleGUI schGUI;
	Course c;
	Semester s;
	JFrame frame = new JFrame();
	JPanel newCourse;
	JPanel confirmationOption;
	JButton startAnimation;
	JButton want = new JButton("I Want it!");
	String[] tokens;
	Thread runner;
	ArrayList<JLabel> questions = new ArrayList<JLabel>();
	JButton tryAgain = new JButton("Try Again");
	JButton cancel;
	




/**
 * 
 * Blurb written: 7/28/2017
 * Last updated: 7/28/2017
 * 
 * 
 * This class was made as an extraneous project, thus this code is a bit 
 * of a surprise. (And by a bit, I mean a lot) If it presents problems the best thing to do would be to 
 * take it out because it is not an important/imperative feature. If one want
 * to dedicate more time to this giant time-suck (but fun time-suck) it has been
 * requested that the background be made into a slot machine, and that the question marks
 * should seems to spin around (or better yet be replaced with other pieces of data) before
 * resting on the chosen course. (Like slot machines do) Also sound effects, good ones for good courses
 * bad ones for bad courses. (Although good luck trying to define good vs. bad course)
 * Adding in these features would get you on the good side of the prof that teaches MTH-OR. But once you
 * implemented these changes, you will probably be persuaded to add something else. So beware of this surpriseMe
 * treadmill. 
 * @param s, semester. 
 * @param schGUI
 * @param gerChoice
 */
	public SurpriseMe( Semester s, ScheduleGUI schGUI, String gerChoice){
		super();
		this.showGEROption=gerChoice;
		this.schGUI=schGUI;
		this.s=s;
		
		frame = new JFrame();
		if(CourseList.getCoursesIn(s).size()==0){ //If semester that is chosen has no classes, alert User, can't do anymore. 
			ImageIcon icon = FileHandler.getDialogBoxImage();
			JOptionPane.showMessageDialog(null, "Classes are not avaliable in the "+ SemesterDate.getSeason(s.semesterDate.sNumber)+ " "+ s.semesterDate.year + " semester", "No classes",JOptionPane.INFORMATION_MESSAGE,  icon  );
			return;
		}
		if(gerChoice == null){
			ArrayList<String> gerNames = new ArrayList<String>();
			///Add these to the array. 
			for(Requirement r: schGUI.sch.getGER().reqList){
				gerNames.add(r.getName());
			}
			//Puts the names into a format the optionPane will take. 
			String[] toShow = new String[gerNames.size()];
			for(int i=0; i<toShow.length; i++){
				toShow[i]=gerNames.get(i);
			}
			gerChoice = (String)JOptionPane.showInputDialog(
					null,
					"Which GER would you like to satisfy?",
					"How can I surprise you?",
					JOptionPane.PLAIN_MESSAGE,
					null,
					toShow,
					null);
			this.showGEROption= gerChoice;
			//If user does not have a valid choice, it stops immediately
			if(gerChoice == null){
				frame.dispose();
				return;
			}
		}
		if(gerChoice != null){
			ArrayList<Course> coursesInSemester = CourseList.getCoursesIn(s);
			Random rand = new Random();
			if(s.semesterDate.sNumber != (SemesterDate.MAYX)){
				ArrayList<Course> coursesThatSatisfyGER = CourseList.onlyThoseSatisfying(coursesInSemester, schGUI.sch.getGER().getRequirement(gerChoice));
				if(coursesThatSatisfyGER.size()>0){
					c = coursesThatSatisfyGER.get(rand.nextInt(coursesThatSatisfyGER.size()));
				}
				else{
					ImageIcon icon = FileHandler.getDialogBoxImage();
					JOptionPane.showMessageDialog(null, "There are no classes in this semester that satisfy the " + gerChoice + " requirement ", "No classes",JOptionPane.INFORMATION_MESSAGE,  icon  );
					return;
				}
			}
			else{
				c=coursesInSemester.get(rand.nextInt(coursesInSemester.size()));
			}

			ScheduleCourse schCourse = new ScheduleCourse(c, schGUI.sch);
			tokens = schCourse.supriseMePieces();
		}

		//Formats the look of the surpriseMe Window. 
		//Top Header Part
		this.setLayout(new BorderLayout());
		this.setBackground(FurmanOfficial.darkPurple);
		JLabel supriseHeader = new JLabel("FEELING LUCKY?", JLabel.CENTER);
		supriseHeader.setFont(FurmanOfficial.bigHeaderFont);
		supriseHeader.setForeground(Color.white);
		this.add(supriseHeader, BorderLayout.NORTH);
		//Middle Part where the course is displayed. 
		newCourse = new JPanel();
		newCourse.setBackground(FurmanOfficial.officialGrey);
		this.add(newCourse, BorderLayout.CENTER);
		//Sets up bottom, that does Animation and confirmation.
		confirmationOption = new JPanel();
		confirmationOption.setBackground(this.getBackground());
		startAnimation = new JButton("Take the Challenge!");
		startAnimation.setActionCommand(MenuOptions.Challenge);
		startAnimation.addActionListener(this);
		confirmationOption.add(startAnimation);
		cancel = new JButton("Cancel");
		cancel.setActionCommand(MenuOptions.Cancel);
		cancel.addActionListener(this);
		want.setActionCommand(MenuOptions.Want);
		want.addActionListener(this);
		tryAgain.setActionCommand(MenuOptions.tryAgain);
		tryAgain.addActionListener(this);
		this.add(confirmationOption, BorderLayout.SOUTH);
		//displays Window.
		frame.add(this);
		frame.pack();
		frame.setVisible(true);
	}

	@Override

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(MenuOptions.Challenge)){
			newCourse.removeAll();
			confirmationOption.remove(tryAgain);
			confirmationOption.remove(want);
			for(int i=0; i<tokens.length; i++){
				//These are the dummy variables that are switched out when class is revealed.
				JLabel question = new JLabel("????? ");
				question.setFont(FurmanOfficial.getFont(45));
				question.setForeground(Color.white);
				questions.add(question);
			}
			for(int p =0; p<questions.size(); p++){
				newCourse.add(questions.get(p));
			}
			frame.add(this);
			frame.pack();
			frame.setVisible(true);
			start();//Start the animation of revealing the chosen class. 
			confirmationOption.remove(startAnimation);//Makes it so the user cannot accept or reject before the animation is done. 
			frame.pack();
			frame.setVisible(true);
		}

		if(e.getActionCommand().equals(MenuOptions.Want)){
			schGUI.addCourseToSchedule(s, c);
			frame.dispose();
		}

		if(e.getActionCommand().equals(MenuOptions.tryAgain)){
			new SurpriseMe(this.s, this.schGUI, showGEROption); //Creates a new object and gets rid of this one. 
			frame.dispose();
		}
		if(e.getActionCommand().equals(MenuOptions.Cancel)){
			frame.dispose();
		}
	}


	public void start() {
		if (runner == null) {
			runner = new Thread(this);
			runner.start();
		}
	}


	@SuppressWarnings("deprecation")
	public void stop() {
		if (runner != null) {
			runner.stop();
			runner = null;
		}
	}

	@Override
	public void run() {
		for(int i=0; i<questions.size(); i++){
			String part;
			if(i>1){
				part= new String (tokens[i] + "   "); //Gives a space btw parts. 

			}
			else{
				part= new String (tokens[i]);
			}
			questions.get(i).setText(part);
			frame.repaint();
			frame.revalidate();
			frame.pack();
			frame.setVisible(true);
			try { Thread.sleep(500); }//Adds the delay
			catch (InterruptedException e) { }
		}
		confirmationOption.add(cancel);
		confirmationOption.add(tryAgain);
		confirmationOption.add(want);
		frame.repaint();
		frame.revalidate();
		frame.pack();
		frame.setVisible(true);
		this.stop();
	}

}












