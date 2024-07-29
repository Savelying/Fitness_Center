package savelying;

import java.util.LinkedList;
import java.io.*;

public class FileHandler {

	public LinkedList<Member> readFile() {
		LinkedList<Member> members = new LinkedList<>();
		String lineRead;
		String[] splitLine;
		Member member;

		try (BufferedReader reader = new BufferedReader(new FileReader("Members.txt"))) {
			lineRead = reader.readLine();

			while (lineRead != null) {
				splitLine = lineRead.split(", ");

				if (splitLine[0].equals("S")) {
					member = new Member_SClub('S', Integer.parseInt(splitLine[1]), splitLine[2], Double.parseDouble(splitLine[3]), Integer.parseInt(splitLine[4]));
				} else {
					member = new Member_SClub('M', Integer.parseInt(splitLine[1]), splitLine[2], Double.parseDouble(splitLine[3]), Integer.parseInt(splitLine[4]));
				}

				members.add(member);
				lineRead = reader.readLine();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		return members;
	}

	public void appendFile(String memberInfo) {

		try (BufferedWriter writer = new BufferedWriter(new FileWriter("Members.txt", true))) {
			writer.write(memberInfo + "\n");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	public void overwriteFile(LinkedList<Member> member) throws IOException {
		String memberInfo;

		try (BufferedWriter writer = new BufferedWriter(new FileWriter("Members.temp", false))) {
			for (Member m : member) {
				memberInfo = m.toString();
				writer.write(memberInfo + "\n");
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

		File file = new File("Members.txt");
		File tempFile = new File("Members.temp");

		file.delete();
		tempFile.renameTo(file);
	}
}
