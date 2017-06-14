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

public class SupriseMe extends JPanel implements ActionListener {
	Driver d;
	Course c;
	Semester s;
	JLabel sup = new JLabel();
	JFrame frame = new JFrame();
	JPanel newCourse;
	JPanel takeIt;
	JButton doIt;

	String delims = "[ ]+";
	String[] tokens;

	String whole = "";

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
			String string = c.toString();
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
				String part= new String (tokens[i]);
				whole= whole + "  " + part;
				sup.setText(whole);
				sup.setFont(FurmanOfficial.getFont(45));
				sup.setForeground(Color.white);
				newCourse.add(sup);
				try{
					Thread.sleep(300);
				}
				catch(InterruptedException p){
					p.printStackTrace();
				}
				
				frame.add(this);
				
				frame.pack();

				frame.setVisible(true);

			}
			takeIt.remove(doIt);
			this.add(newCourse);
			JButton want = new JButton("I Want it!");
			want.setActionCommand(MenuOptions.Want);
			want.addActionListener(this);
			takeIt.add(want);
		}
		if(e.getActionCommand().equals(MenuOptions.Want)){
			d.GUIChallengeExcepted(s, c);
			frame.dispose();
		}

	}
}


