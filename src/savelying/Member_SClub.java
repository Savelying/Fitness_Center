package savelying;

public class Member_SClub extends Member {
	private int club;

	public Member_SClub(char type, int id, String name, double fees, int club) {
		super(type, id, name, fees);
		this.club = club;
	}

	public int getClub() {
		return club;
	}

	public void setClub(int club) {
		this.club = club;
	}

	@Override
	public String toString() {
		return super.toString() + ", " + club;
	}
}
