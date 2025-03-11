package savelying;

import java.time.LocalDate;
import java.time.Period;

public class Member {
	private String name, type, clubName;
	private Integer id, clubId, fees, points;
	private LocalDate date;

	public Member(Integer id, String name, String type, Integer clubId, String clubName, Integer fees, Integer points, LocalDate date) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.clubId = clubId;
		this.clubName = clubName;
		this.fees = fees;
		this.points = points;
		this.date = date;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getClubName() {
		return clubName;
	}

	public void setClubName(String clubName) {
		this.clubName = clubName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getClubId() {
		return clubId;
	}

	public void setClubId(int clubId) {
		this.clubId = clubId;
	}

	public int getFees() {
		return fees;
	}

	public void setFees(int fees) {
		this.fees = fees;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	@Override
	public String toString() {
		return switch (clubId) {
			case 0 ->
					"Имя члена: " + name + "\nКлуб: " + clubName + "(" + clubId + ")" + "\nЧленский взнос: " + fees + "\nБонусные баллы: " + ((int) Period.between(date, LocalDate.now()).toTotalMonths() * 100 - points);
			case null, default ->
					"Имя члена: " + name + "\nКлуб: " + clubName + "(" + clubId + ")" + "\nЧленский взнос: " + fees + "\nБонусные баллы: " + points;
		};
	}
}