package savelying;

import java.time.LocalDate;

public class Member_SClub extends Member {
	private int club;

	public Member_SClub(Integer id, String name, String type, Integer clubId, String clubName, Integer fees, Integer points, LocalDate date) {
		super(id, name, type, clubId, clubName, fees, points, date);
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
