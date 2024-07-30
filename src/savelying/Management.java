package savelying;

import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Management {
	final private Scanner reader = new Scanner(System.in);

	private void printClubOptions() {
		System.out.print("""
				                 \nСписок клубов:
				                 1) Клуб Меркурий
				                 2) Клуб Нептун
				                 3) Клуб Юпитер
				                 4) Мультиклубный
				                 Выберите нужный клуб:""" + " ");
	}

	private int getIntInput() {
		int choice = 0;

		while (choice == 0) {
			try {
				choice = reader.nextInt();

				if (choice == 0)
					throw new InputMismatchException();
				reader.nextLine();

			} catch (InputMismatchException e) {
				System.out.print("\nОШИБКА: НЕПРАВИЛЬНЫЙ ВВОД. Попробуйте ещё раз:" + " ");
			}
		}
		return choice;
	}

	public int getChoice() {

		System.out.print("""
				                 WELCOME TO OZONE FITNESS CENTER
				                 ================================
				                 Что хотим сделать?
				                 1) Добавить члена
				                 2) Удалить члена
				                 3) Узнать данные члена
				                 Выберите нужное действие (или введите "-1" для выхода):""" + " ");

		return getIntInput();
	}

	//Метод добавления нового клиента
	public void addMember(String dbName) {
		int fees;
		int points = 0;
		String name;
		String type;
		Calculator<Integer> calculator;
		LocalDate date = LocalDate.now();

		//Присваиваем имя клиенту
		System.out.print("\nВведите имя члена:" + " ");
		name = reader.nextLine();

		//Присваиваем клуб клиенту
		printClubOptions();
		int clubId = getIntInput();

		while (clubId < 1 || clubId > 4) {
			System.out.print("\nОШИБКА: НЕПРАВИЛЬНЫЙ ВВОД. Попробуйте ещё раз:" + " ");
			clubId = getIntInput();
		}

		//Вносим клиента в список с соответствующим клубом и тарифом
		if (clubId != 4) {
			calculator = (n) -> {
				return switch (n) {
					case 1 -> 900;
					case 2 -> 950;
					case 3 -> 1000;
					default -> -1;
				};
			};
			fees = calculator.calculateFees(clubId);
			type = "single";
//			System.out.println("\nЧлен отдельного клуба добавлен!\n");

		} else {
			calculator = (n) -> {
				return switch (n) {
					case 4 -> 1500;
					default -> -1;
				};
			};
			fees = calculator.calculateFees(clubId);
			type = "multi";
//			System.out.println("\nЧлен нескольких клубов добавлен!\n");
		}

		try (Connection connection = DBConnector.getServConnect()) {
			Statement statement = connection.createStatement();

			String dbNameSQL = "create database if not exists " + dbName;
			statement.executeUpdate(dbNameSQL);

			String dbUseSQL = "use " + dbName;
			statement.executeUpdate(dbUseSQL);

			statement.executeUpdate("create table if not exists Members (id int not null auto_increment, name varchar(45), type varchar(6), clubid int, fees int, date date, points int, primary key (id))");
			String sql = "insert into Members set name = '" + name + "', type = '" + type + "', clubid = " + clubId + ", fees = " + fees + ", date = '" + date + "', points = " + points;
			statement.executeUpdate(sql);

			ResultSet resultSet = statement.executeQuery("select id, fees from Members order by id desc limit 1;");
			if (resultSet.next())
				System.out.println("\nЧлен сети клубов №" + resultSet.getInt("id") + " с взносом " + resultSet.getInt("fees") + "р. добавлен!\n");

		} catch (SQLException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}

	//Метод удаления клиента
	public void removeMember(String dbName) {
		String readSql = "select id from Members where id = ?";
		String delSql = "delete from Members where id = ?";

		System.out.print("\nВведите номер члена:" + " ");
		int id = getIntInput();


		try (Connection connection = DBConnector.getDbConnect(dbName);) {
			PreparedStatement readStatement = connection.prepareStatement(readSql);

			readStatement.setInt(1, id);
			ResultSet resultSet = readStatement.executeQuery();
			if (resultSet.next()) {
				PreparedStatement delStatement = connection.prepareStatement(delSql);

				delStatement.setInt(1, id);
				delStatement.executeUpdate();

				System.out.println("\nЧлен №" + resultSet.getInt("id") + " сети клубов удалён!\n");

			} else System.out.println("\nОШИБКА: Члена с указанным номером нет в списках!\n");

		} catch (SQLException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}

	}

	//Метод вывода данных клиента
	public void printMemberInfo(String dbName) {
		String sql = "select * from Members where id = ?";

		System.out.print("\nВведите номер члена:" + " ");
		int id = getIntInput();

		try (Connection connection = DBConnector.getDbConnect(dbName);) {
			PreparedStatement statement = connection.prepareStatement(sql);

			statement.setInt(1, id);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				int points;

				if (resultSet.getInt("clubid") == 4) {
					LocalDate date = LocalDate.now();
					Period period = Period.between(resultSet.getDate("date").toLocalDate(), date);
					points = (int) period.toTotalMonths() * 100 - resultSet.getInt("points");

				} else points = resultSet.getInt("points");

				System.out.println("\nДанные интересующего Вас члена (№" + resultSet.getInt("id") + "):\nИмя члена = " + resultSet.getString("name") + "\nТип членства = " + resultSet.getString("type") + "\nНомер клуба = " + resultSet.getInt("clubid") + "\nЧленский взнос = " + resultSet.getInt("fees") + "\nБонусные баллы = " + points + "\n");

			} else System.out.println("\nОШИБКА: Члена с указанным номером нет в списках!\n");

		} catch (SQLException | ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
	}
}
