package savelying;

public class Member {
	private char type;
	private int id;
	private String name;
	private double fees;

	public Member(char type, int id, String name, double fees) {
		this.type = type;
		this.id = id;
		this.name = name;
		this.fees = fees;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getFees() {
		return fees;
	}

	public void setFees(double fees) {
		this.fees = fees;
	}

	@Override
	public String toString() {
		return type + ", " + id + ", " + name + ", " + fees;
	}
}
