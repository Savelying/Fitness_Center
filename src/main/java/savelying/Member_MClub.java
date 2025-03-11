package savelying;

import java.time.LocalDate;

public class Member_MClub extends Member {
	private int points;

	public Member_MClub(Integer id, String name, String type, Integer clubId, String clubName, Integer fees, Integer points, LocalDate date) {
		super(id, name, type, clubId, clubName, fees, points, date);
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
