import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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

public class SupriseMe extends JPanel implements ActionListener, Runnable {
	Driver d;
	Course c;
	Semester s;
	JLabel sup = new JLabel();
	JFrame frame = new JFrame();
	JPanel newCourse;
	JPanel takeIt;
	JButton doIt;

	String delims = "[,]";
	String[] tokens;
	Thread runner;
	String whole = "";
	ArrayList<JLabel> questions = new ArrayList<JLabel>();



	public SupriseMe(Schedule sch, Semester s, Driver d){
		super();
		this.d=d;
		this.s=s;

		frame = new JFrame();


		ArrayList<Course> getReady = sch.masterList.getCoursesIn(s);
		if(getReady.size()==0){
			ImageIcon icon = new ImageIcon("src/BellTower(T).png");
			JOptionPane.showMessageDialog(frame, "Classes have not yet been added to the "+ s.semesterDate.getSeason(s.semesterDate.sNumber)+ " "+ s.semesterDate.year + " semester", "No classes",JOptionPane.INFORMATION_MESSAGE,  icon  );

		}
		else{	
			Random rand = new Random();

			c = getReady.get(rand.nextInt(getReady.size()));
			ScheduleCourse cc = new ScheduleCourse(c, sch);
			String string = cc.supriseString();
			System.out.println(string);
			tokens = string.split(delims);


			this.setLayout(new BorderLayout());
			//this.setPreferredSize(new Dimension(500, 500));
			this.setBackground(FurmanOfficial.darkPurple);
			JLabel suprise = new JLabel("FEELING LUCKY?", JLabel.CENTER);

			suprise.setFont(FurmanOfficial.bigHeaderFont);
			suprise.setForeground(Color.white);
			this.add(suprise, BorderLayout.NORTH);

			newCourse = new JPanel();
			newCourse.setBackground(FurmanOfficial.grey);
			//	newCourse.setPreferredSize(new Dimension(300, 300));
			this.add(newCourse, BorderLayout.CENTER);



			takeIt = new JPanel();
			takeIt.setBackground(this.getBackground());
			doIt = new JButton("Take the Challenge!");
			doIt.setActionCommand(MenuOptions.Challenge);
			doIt.addActionListener(this);
			takeIt.add(doIt);
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

	}








	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals(MenuOptions.Challenge)){


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
			JButton want = new JButton("I Want it!");
			want.setActionCommand(MenuOptions.Want);
			want.addActionListener(this);
			takeIt.add(want);
		}
		frame.pack();

		frame.setVisible(true);


		if(e.getActionCommand().equals(MenuOptions.Want)){
			d.GUIChallengeExcepted(s, c);
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

			try { Thread.sleep(1000); }
			catch (InterruptedException e) { }
		}



		this.stop();


	}


}







