
import java.util.ArrayList;


public class Major {
	String name;
	int groupNumber;
	ArrayList<Requirement> reqList;
	
	public Major(){
		this.reqList = new ArrayList<Requirement>();
	}
	
	public void addRequirement(Requirement r){
		int dipNumber = r.doubleDipNumber;
		addRequirement(r, dipNumber%100);
		this.reqList.add(r);
	}
	/**
	 * Add this requirement with the specified doubleDipNumber
	 * 
	 * This method will automatically attempt to prevent clashing between
	 * doubleDipNumbers of requirements in different majors.
	 * To help with this, it is best if doubleDipNumber is less than 100.
	 * However, doubleDipNumber of 0 is the default (and will be treated as if there is
	 * no double dip number.)
	 * @param r
	 * @param doubleDipNumber
	 */
	public void addRequirement(Requirement r, int doubleDipNumber){
		r.setDoubleDipNumber(groupNumber * 100 + doubleDipNumber);
	}
	
	public static Major testMajor(){
		Major result = new Major("Math-BS");
		result.addRequirement(new Requirement(new Prefix[]{new Prefix("MTH", 250)}, 1));
		result.addRequirement(new Requirement(new Prefix[]{new Prefix("MTH", 260)}, 1));
		result.addRequirement(new Requirement(new Prefix[]{
				new Prefix("MTH", 350), new Prefix("MTH",450)}, 1));
		result.addRequirement(new Requirement(new Prefix[]{
				new Prefix("MTH", 360), new Prefix("MTH",460)}, 1));
		Requirement electives = new Requirement(new Prefix[]{
				new Prefix("MTH",151), 
				new Prefix("MTH",160),
				new Prefix("MTH",250),
				new Prefix("MTH",320),
				new Prefix("MTH",335),
				new Prefix("MTH",337),
				new Prefix("MTH",340),
				new Prefix("MTH",341),
				new Prefix("MTH",350),
				new Prefix("MTH",360),
				new Prefix("MTH",420),
				new Prefix("MTH",435),
				new Prefix("MTH",450),
				new Prefix("MTH",451),
				new Prefix("MTH",450),
				new Prefix("MTH",460),
				new Prefix("MTH",461),
				new Prefix("MTH",504)
				}, 		
				1);
		electives.setName("Electives");
		result.addRequirement(electives, 2);
		return result;
	}
}