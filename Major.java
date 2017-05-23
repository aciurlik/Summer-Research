import java.util.ArrayList;


public class Major {
	String name;
	ArrayList<Requirement> reqList;
	
	public Major(String name){
		this.name = name;
		this.reqList = new ArrayList<Requirement>();
	}
	
	public void addRequirement(Requirement r){
		this.reqList.add(r);
	}
	
	public static Major testMajor(){
		Major result = new Major("Math");
		result.reqList.add(Requirement.testRequirement());
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
