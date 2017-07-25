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



public class SupriseMe extends JPanel implements ActionListener, Runnable, java.io.Serializable {

	/**

	 * 

	 */

	private static final long serialVersionUID = 1L;

	String gerChoice;

	ScheduleGUI d;

	Course c;

	Semester s;

	JLabel sup = new JLabel();

	JFrame frame = new JFrame();

	JPanel newCourse;

	JPanel takeIt;

	JButton doIt;

	JButton want = new JButton("I Want it!");

	String delims = "[,]";

	String[] tokens;

	Thread runner;

	String whole = "";

	ArrayList<JLabel> questions = new ArrayList<JLabel>();

	JButton tryAgain = new JButton("Try Again");

	Schedule sch;
	JButton cancel;

	ArrayList<Course> almostReady;
	ArrayList<Course>  getReady;





	public SupriseMe(Schedule sch, Semester s, ScheduleGUI d, String gerChoice){


		super();
		this.gerChoice=gerChoice;

		this.d=d;
		this.s=s;
		this.sch=sch;
		frame = new JFrame();

		if(CourseList.getCoursesIn(s).size()==0){

			ImageIcon icon = FileHandler.getBelltowerImage();

			JOptionPane.showMessageDialog(frame, "Classes have not yet been added to the "+ s.semesterDate.getSeason(s.semesterDate.sNumber)+ " "+ s.semesterDate.year + " semester", "No classes",JOptionPane.INFORMATION_MESSAGE,  icon  );

			return;
		}


		if(gerChoice == null){
			ArrayList<String> gerNames = new ArrayList<String>();
			///Add these to the array. 
			for(Requirement r: sch.getGER().reqList){
				gerNames.add(r.getName());
			}
			String[] toShow = new String[gerNames.size()];
			for(int i=0; i<toShow.length; i++){
				toShow[i]=gerNames.get(i);
			}
			gerChoice = (String)JOptionPane.showInputDialog(
					frame,
					"Which GER would you like to satisfy?",
					"How can I suprise you?",
					JOptionPane.PLAIN_MESSAGE,
					null,
					toShow,
					null);
			this.gerChoice= gerChoice;

			if(gerChoice == null){
				frame.dispose();
				return;

			}


		}
		if(gerChoice != null){
			almostReady = CourseList.getCoursesIn(s);



			Random rand = new Random();

			if(s.semesterDate.sNumber != (SemesterDate.MAYX)){

				getReady = CourseList.onlyThoseSatisfying(almostReady, sch.getGER().getRequirement(gerChoice));
				if(getReady.size()>0){
					c = getReady.get(rand.nextInt(getReady.size()));
				}
				else{
					ImageIcon icon = FileHandler.getBelltowerImage();

					JOptionPane.showMessageDialog(frame, "There are no classes in this semester that satisfy the " + gerChoice + " requirement ", "No classes",JOptionPane.INFORMATION_MESSAGE,  icon  );

					return;
				}


			}
			else{

				c=almostReady.get(rand.nextInt(almostReady.size()));

			}

			ScheduleCourse cc = new ScheduleCourse(c, sch);

			String string = cc.supriseString();


			tokens = string.split(delims);
		}






		this.setLayout(new BorderLayout());

		//this.setPreferredSize(new Dimension(500, 500));

		this.setBackground(FurmanOfficial.darkPurple);

		JLabel suprise = new JLabel("FEELING LUCKY?", JLabel.CENTER);



		suprise.setFont(FurmanOfficial.bigHeaderFont);

		suprise.setForeground(Color.white);

		this.add(suprise, BorderLayout.NORTH);



		newCourse = new JPanel();

		newCourse.setBackground(FurmanOfficial.officialGrey);

		//	newCourse.setPreferredSize(new Dimension(300, 300));

		this.add(newCourse, BorderLayout.CENTER);







		takeIt = new JPanel();

		takeIt.setBackground(this.getBackground());

		doIt = new JButton("Take the Challenge!");

		doIt.setActionCommand(MenuOptions.Challenge);

		doIt.addActionListener(this);

		takeIt.add(doIt);


		cancel = new JButton("Cancel");
		cancel.setActionCommand(MenuOptions.Cancel);
		cancel.addActionListener(this);


		tryAgain.setActionCommand(MenuOptions.tryAgain);

		tryAgain.addActionListener(this);





		this.add(takeIt, BorderLayout.SOUTH);

		//sup = new JLabel(c.toString());







		frame.add(this);

		frame.pack();

		this.revalidate();

		this.repaint();

		frame.revalidate();

		frame.repaint();

		frame.setVisible(true);



	}






















	@Override

	public void actionPerformed(ActionEvent e) {





		if(e.getActionCommand().equals(MenuOptions.Challenge)){

			newCourse.removeAll();



			takeIt.remove(tryAgain);

			takeIt.remove(want);

			for(int i=0; i<tokens.length; i++){

				JPanel stack = new JPanel();

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



			start();

			//this.add(newCourse);

			takeIt.remove(doIt);



			want.setActionCommand(MenuOptions.Want);

			want.addActionListener(this);






			frame.pack();



			frame.setVisible(true);







		}

		if(e.getActionCommand().equals(MenuOptions.Want)){

			d.GUIChallengeExcepted(s, c);

			frame.dispose();

		}

		if(e.getActionCommand().equals(MenuOptions.tryAgain)){

			SupriseMe s = new SupriseMe(this.sch, this.s, this.d, gerChoice);

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

				part= new String (tokens[i] + "   ");

			}

			else{

				part= new String (tokens[i]);

			}

			questions.get(i).setText(part);





			frame.repaint();

			frame.revalidate();

			frame.pack();

			frame.setVisible(true);



			try { Thread.sleep(500); }



			catch (InterruptedException e) { }

		}
		takeIt.add(cancel);
		takeIt.add(tryAgain);
		takeIt.add(want);

		frame.repaint();

		frame.revalidate();

		frame.pack();

		frame.setVisible(true);

		this.stop();





	}





}












