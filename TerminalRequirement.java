import java.util.Stack;


public class TerminalRequirement implements RequirementInterface {
	public Prefix p;
	public int number;
	
	
	
	public TerminalRequirement(String s){
		//If this is a terminal requirement string, read correctly.
		// If not, throw a runtime exception.
		System.out.println(s);
		
	}
	public TerminalRequirement(Prefix p){
		this.p = p;
	}
	public TerminalRequirement(Prefix p, int number){
		this.p = p;
		this.number = number;
	}
	
	
	
	
	
	
	@Override
	public RequirementInterface cloneRequirement(){
		return new TerminalRequirement(new Prefix(p.getSubject(), p.getNumber()), number);
	}
	
}
