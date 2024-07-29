package savelying;

import java.util.InputMismatchException;
import java.util.LinkedList;
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

		int choice = getIntInput();
		return choice;
	}

	//Метод добавления нового клиента
	public String addMember(LinkedList<Member> members) {
		int clubId, id;
		double fees;
		String name, info;
		Member member;
		Calculator<Integer> calculator;

		//Присваиваем имя клиенту
		System.out.print("\nВведите имя члена:" + " ");
		name = reader.nextLine();

		//Присваиваем клуб клиенту
		printClubOptions();
		clubId = getIntInput();

		while (clubId < 1 || clubId > 4) {
			System.out.print("\nОШИБКА: НЕПРАВИЛЬНЫЙ ВВОД. Попробуйте ещё раз:" + " ");
			clubId = getIntInput();
		}

		//Присваиваем индекс клиенту
		if (members.size() > 0) {
			id = members.getLast().getId() + 1;
		} else id = 1;

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
			member = new Member_SClub('S', id, name, fees, clubId);
			members.add(member);
			info = member.toString();
			System.out.println("\nЧлен отдельного клуба добавлен!\n");
		} else {
			calculator = (n) -> {
				return switch (n) {
					case 4 -> 1500;
					default -> -1;
				};
			};
			fees = calculator.calculateFees(clubId);
			member = new Member_MClub('M', id, name, fees, 100);
			members.add(member);
			info = member.toString();
			System.out.println("\nЧлен нескольких клубов добавлен!\n");
		}
		return info;
	}

	//Метод удаления клиента
	public void removeMember(LinkedList<Member> members) {
		int id;

		System.out.print("\nВведите номер члена:" + " ");
		id = getIntInput();
		for (Member m : members) {
			if (id == m.getId()) {
				members.remove(m);
				System.out.println("\nЧлен сети клубов удалён!\n");
				return;
			}
		}
		System.out.println("\nОШИБКА: Члена с указанным номером нет в списках!\n");
	}

	//Метод вывода данных клиента
	public void printMemberInfo(LinkedList<Member> members) {

		System.out.print("\nВведите номер члена:" + " ");
		int id = getIntInput();

		for (Member m : members) {
			if (id == m.getId()) {
				String[] info = m.toString().split(", ");
				System.out.println("\nДанные интересующего Вас члена:\n" +
				                   "Тип члена = " + info[0] +"\n" +
				                   "Номер члена = " + info[1] +"\n" +
				                   "Имя члена = " + info[2] +"\n" +
				                   "Членский взнос = " + info[3]);
				if (info[0].equals("S")) {
					System.out.println("Номер клуба = " + info[4] + "\n");
				} else System.out.println("Кол-во бонусов = " + info[4] + "\n");
				return;
			}
		}
		System.out.println("\nОШИБКА: Члена с указанным номером нет в списках!\n");
	}
}
