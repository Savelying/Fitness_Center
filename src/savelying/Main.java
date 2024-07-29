package savelying;

import java.io.IOException;
import java.util.LinkedList;

public class Main {
	public static void main(String[] args) throws IOException {
		String member;
		Management manage = new Management();
		FileHandler file = new FileHandler();
		LinkedList<Member> members = file.readFile();

		int choice = manage.getChoice();

		while (choice != -1) {
			switch (choice) {
				case 1:
					member = manage.addMember(members);
					file.appendFile(member);
					break;
				case 2:
					manage.removeMember(members);
					file.overwriteFile(members);
					break;
				case 3:
					manage.printMemberInfo(members);
					break;
				default:
					System.out.print("\nОШИБКА: НЕПРАВИЛЬНЫЙ ВВОД. Попробуйте ещё раз:" + " ");
					break;
			}
			choice = manage.getChoice();
		}
		System.out.println("\nДО СВИДАНИЯ!");
	}
}