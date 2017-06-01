import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class MajorPanel extends JPanel {
	public  Major major;
	public Driver d;


	public MajorPanel(Major m, Driver d){
		super();
		this.d=d;
		this.major = m;
		update(m);
	}




	public void update (Major m){
		ArrayList<Requirement> reqList = new ArrayList<Requirement>(m.reqList);
		Collections.sort(reqList);

		//Calculte the requirements left in this major
		int reqsLeft = 0;
		for(Requirement r : reqList){
			if(r.numFinished < r.numToChoose){
				reqsLeft += r.numToChoose - r.numFinished;
			}
		}


		//Make this major's panel

		this.setLayout(new BorderLayout());
		JPanel header = new JPanel();
		header.setLayout(new FlowLayout(FlowLayout.LEFT));
		JPanel top = new JPanel ();


		top.setLayout(new BorderLayout());
		top.setBorder(new CompoundBorder(new EmptyBorder(4, 4, 4, 4), new MatteBorder(0, 0, 1, 0, Color.BLACK)));
		JLabel topLabel =new JLabel(m.name + "         " + reqsLeft + " Unscheduled"); 
		topLabel.setFont(FurmanOfficial.getFont(16));
		header.add(topLabel);
		top.add(header, BorderLayout.WEST);

		JPanel removeP = new JPanel();
		removeP.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton remove = new JButton("x");
		remove.setBackground(FurmanOfficial.darkPurple);
		remove.setPreferredSize(new Dimension (15, 15));
		remove.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				removeSelf();


			}
		});
		removeP.add(remove);
		top.add(removeP, BorderLayout.EAST);


		this.add(top, BorderLayout.NORTH);


		JPanel bottom = new JPanel();
		for(Requirement r : reqList){
			bottom.add(new RequirementPanel(r));
		}
		this.add(bottom, BorderLayout.CENTER);

		JPanel red = new JPanel();
		red.setBackground(Color.red);
		red.setSize(300, 2000);




	}

	public void removeSelf(){
		d.GUIRemoveMajor(this);

	}



}
