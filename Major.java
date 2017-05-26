import java.util.ArrayList;


public class Major {
	String name;
	int groupNumber;
	ArrayList<Requirement> reqList;

	public static final int MajorDDNRange = 100;

	public Major(String name){
		this.name = name;
		this.reqList = new ArrayList<Requirement>();
	}

	public void addRequirement(Requirement r){
		int dipNumber = r.doubleDipNumber;
		addRequirement(r, dipNumber%MajorDDNRange);
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
		r.setDoubleDipNumber(groupNumber * MajorDDNRange + doubleDipNumber);
		this.reqList.add(r);
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
		7);
		electives.setName("MTH Electives");
		result.addRequirement(electives, 2);
		return result;
	}


	public String saveString(){
		StringBuilder result = new StringBuilder();
		result.append(name + "\n");
		for (Requirement r : this.reqList){
			result.append("REQ:" + r.saveString() + "\n");
		}
		return result.toString();
	}

	public static Major readFrom(String saveString){
		String[] lines = saveString.split("[\n]+");
		Major result = new Major(lines[0]);
		for(int i = 1; i < lines.length ; i ++){
			Requirement newRequirement = Requirement.readFrom(lines[i].substring(4));
			newRequirement.numFinished = 0;
			result.addRequirement(newRequirement);
		}
		return result;
	}


	public static void main(String[] args){
		Major t = Major.testMajor();
		System.out.println(t.saveString());
		Major x = Major.readFrom(t.saveString());
		System.out.println(x.saveString());
	}
}
