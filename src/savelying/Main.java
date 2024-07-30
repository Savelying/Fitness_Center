package savelying;

public class Main {
	public static void main(String[] args) {
		String dbName = "Fitness_Center";
		Management manage = new Management();

		int choice = manage.getChoice();

		while (choice != -1) {
			switch (choice) {
				case 1 -> manage.addMember(dbName);
				case 2 -> manage.printMemberInfo(dbName);
				case 3 -> manage.updateMemberPoints(dbName);
				case 4 -> manage.removeMember(dbName);
				default -> System.out.print("\nОШИБКА: НЕПРАВИЛЬНЫЙ ВВОД. Попробуйте ещё раз:" + " ");
			}
			choice = manage.getChoice();
		}
		System.out.println("\nДО СВИДАНИЯ!");
	}
}