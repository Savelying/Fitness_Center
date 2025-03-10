package savelying;

public class Member_MClub extends Member {
	private int points;

	public Member_MClub(char type, int id, String name, double fees, int points) {
		super(type, id, name, fees);
		this.points = points;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	@Override
	public String toString() {
		return super.toString() + ", " + points;
	}
}
