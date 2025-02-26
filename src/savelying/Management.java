package savelying;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Management {
    final private Scanner reader = new Scanner(System.in);
    String dbName = "Fitness_Center";
    Frame frame = new Frame(600, 500);
    Container container = new Container();
    JPanel panel = new JPanel();

    private void printClubOptions() {
        System.out.print("""
                
                Список клубов:
                0) Мультиклубный
                1) Клуб Меркурий
                2) Клуб Нептун
                3) Клуб Юпитер
                Выберите нужный клуб:\s""");
    }

    private int getIntInput() {
        int choice = -5;

        while (choice == -5) {
            try {
                choice = reader.nextInt();

                if (choice == -5)
                    throw new InputMismatchException();
                reader.nextLine();

            } catch (InputMismatchException e) {
                System.out.print("\nОШИБКА: НЕПРАВИЛЬНЫЙ ВВОД. Попробуйте ещё раз:" + " ");
            }
        }
        return choice;
    }

    //Метод выбора действия
    public void getChoice() {
        container.removeAll();
        container.setLayout(new GridLayout(5, 1));

        panel.removeAll();
        panel.setLayout(new GridLayout(2, 2));

        JButton buttonAdd = new JButton("Добавить члена");
        buttonAdd.addActionListener(e -> addMember(dbName));
        panel.add(buttonAdd);
        JButton buttonInfo = new JButton("Узнать данные члена");
        buttonInfo.addActionListener(e -> printMemberInfo(dbName));
        panel.add(buttonInfo);
        JButton buttonBonus = new JButton("Списать бонусы члена");
        buttonBonus.addActionListener(e -> updateMemberPoints(dbName));
        panel.add(buttonBonus);
        JButton buttonDel = new JButton("Удалить члена");
        buttonDel.addActionListener(e -> removeMember(dbName));
        panel.add(buttonDel);

        JButton buttonExit = new JButton("ВЫХОД");
        buttonExit.addActionListener(e -> System.exit(0));

        container.add(new Item());
        container.add(new JLabel("ВЫБЕРИТЕ НУЖНОЕ ДЙСТВИЕ", SwingConstants.CENTER));
        container.add(panel);
        container.add(new JLabel("", SwingConstants.CENTER));
        container.add(buttonExit);
        frame.add(container);
        frame.repaint();
        frame.revalidate();
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

        while (clubId < 0 || clubId > 3) {
            System.out.print("\nОШИБКА: НЕПРАВИЛЬНЫЙ ВВОД. Попробуйте ещё раз:" + " ");
            clubId = getIntInput();
        }

        //Вносим клиента в список с соответствующим клубом и тарифом
        if (clubId != 0) {
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

        } else {
            calculator = (n) -> {
                return switch (n) {
                    case 0 -> 1500;
                    default -> -1;
                };
            };
            fees = calculator.calculateFees(clubId);
            type = "multi";
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
        String readSql = "select * from Members where id = ?";
        String delSql = "delete from Members where id = ?";

        container.removeAll();
        container.setLayout(new GridLayout(5, 1));

        panel.removeAll();
        panel.setLayout(new FlowLayout());

        panel.add(new JLabel("Идентификационный номер:"));
        JTextField idField = new JTextField("", 30);
        panel.add(idField);
        JButton buttonDel = new JButton("УДАЛИТЬ");
        buttonDel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id;
                try {
                    id = Integer.parseInt(idField.getText());

                    try (Connection connection = DBConnector.getDbConnect(dbName)) {
                        PreparedStatement readStatement = connection.prepareStatement(readSql);

                        readStatement.setInt(1, id);
                        ResultSet resultSet = readStatement.executeQuery();

                        if (resultSet.next()) {
                            int points;
                            if (resultSet.getInt("clubid") == 0) {
                                LocalDate date = LocalDate.now();
                                Period period = Period.between(resultSet.getDate("date").toLocalDate(), date);
                                points = (int) period.toTotalMonths() * 100 - resultSet.getInt("points");
                            } else points = resultSet.getInt("points");

                            if (JOptionPane.showOptionDialog(null, "Имя члена: " + resultSet.getString("name") + "\nТип членства: " + resultSet.getString("type") + "\nНомер клуба: " + resultSet.getInt("clubid") + "\nЧленский взнос: " + resultSet.getInt("fees") + "\nБонусные баллы: " + points + "\nВсё верно?\nУДАЛЯЕМ?!", "Удаление члена ID №" + resultSet.getInt("id"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"да", "нет"}, "нет") == 0) {
                                PreparedStatement delStatement = connection.prepareStatement(delSql);
                                delStatement.setInt(1, id);
                                delStatement.executeUpdate();
                                JOptionPane.showMessageDialog(null, "Член сети клубов ID №" + id + " удалён!", "Удаление члена ID №" + resultSet.getInt("id"), JOptionPane.INFORMATION_MESSAGE);
                            }

                        } else
                            JOptionPane.showMessageDialog(null, "ОШИБКА: Члена с указанным номером нет в списках!", "Удаление члена ID №" + id, JOptionPane.ERROR_MESSAGE);

                    } catch (SQLException | ClassNotFoundException f) {
                        System.out.println(f.getMessage());
                        JOptionPane.showMessageDialog(null, "ОШИБКА: Нет связи с базой данных!", "Удаление члена ID №" + id, JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception m) {
                    System.out.println(m.getMessage());
                    JOptionPane.showMessageDialog(null, "ОШИБКА: Введено не числовое значение!", "Удаление члена ID №", JOptionPane.ERROR_MESSAGE);
                }
                ;
            }
        });

        panel.add(buttonDel);
        JButton buttonBack = new JButton("НАЗАД");
        buttonBack.addActionListener(e -> getChoice());

        container.add(new Item());
        container.add(new JLabel("УДАЛЕНИЕ ЧЛЕНА №", SwingConstants.CENTER));
        container.add(panel);
        container.add(new JLabel("", SwingConstants.CENTER));
        container.add(buttonBack);


        frame.add(container);
        frame.repaint();
        frame.revalidate();
    }

    //Метод вывода данных клиента
    public void printMemberInfo(String dbName) {
        String sql = "select * from Members where id = ?";

        container.removeAll();
        container.setLayout(new GridLayout(5, 1));

        panel.removeAll();
        panel.setLayout(new FlowLayout());

        panel.add(new JLabel("Идентификационный номер:"));
        JTextField idField = new JTextField("", 30);
        panel.add(idField);
        JButton buttonInfo = new JButton("ПОКАЗАТЬ");
        buttonInfo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id;
                try {
                    id = Integer.parseInt(idField.getText());

                    try (Connection connection = DBConnector.getDbConnect(dbName)) {
                        PreparedStatement statement = connection.prepareStatement(sql);

                        statement.setInt(1, id);
                        ResultSet resultSet = statement.executeQuery();

                        if (resultSet.next()) {
                            int points;
                            if (resultSet.getInt("clubid") == 0) {
                                LocalDate date = LocalDate.now();
                                Period period = Period.between(resultSet.getDate("date").toLocalDate(), date);
                                points = (int) period.toTotalMonths() * 100 - resultSet.getInt("points");
                            } else points = resultSet.getInt("points");

                            JOptionPane.showMessageDialog(null, "Имя члена: " + resultSet.getString("name") + "\nТип членства: " + resultSet.getString("type") + "\nНомер клуба: " + resultSet.getInt("clubid") + "\nЧленский взнос: " + resultSet.getInt("fees") + "\nБонусные баллы: " + points, "Информация члена ID №" + resultSet.getInt("id"), JOptionPane.INFORMATION_MESSAGE);

                        } else
                            JOptionPane.showMessageDialog(null, "ОШИБКА: Члена с указанным номером нет в списках!", "Информация члена ID №" + id, JOptionPane.ERROR_MESSAGE);

                    } catch (SQLException | ClassNotFoundException f) {
                        System.out.println(f.getMessage());
                        JOptionPane.showMessageDialog(null, "ОШИБКА: Нет связи с базой данных!", "Информация члена ID №" + id, JOptionPane.ERROR_MESSAGE);
                    }

                } catch (Exception m) {
                    System.out.println(m.getMessage());
                    JOptionPane.showMessageDialog(null, "ОШИБКА: Введено не числовое значение!", "Информация члена ID №", JOptionPane.ERROR_MESSAGE);
                }
                ;
            }
        });

        panel.add(buttonInfo);
        JButton buttonBack = new JButton("НАЗАД");
        buttonBack.addActionListener(e -> getChoice());

        container.add(new Item());
        container.add(new JLabel("ПОКАЗАТЬ ДАННЫЕ ЧЛЕНА №", SwingConstants.CENTER));
        container.add(panel);
        container.add(new JLabel("", SwingConstants.CENTER));
        container.add(buttonBack);


        frame.add(container);
        frame.repaint();
        frame.revalidate();
    }

    //Метод расчёта бонусов клиента
    public void updateMemberPoints(String dbName) {
        String readSQL = "select * from Members where id = ?";
        String writeSQL = "update Members set points = ? where id = ?";

        container.removeAll();
        container.setLayout(new GridLayout(5, 1));

        panel.removeAll();
        panel.setLayout(new FlowLayout());

        panel.add(new JLabel("Идентификационный номер:"));
        JTextField idField = new JTextField("", 30);
        panel.add(idField);
        panel.add(new JLabel("Сколько списать баллов:"));
        JTextField bonusField = new JTextField("", 30);
        panel.add(bonusField);
        JButton buttonBonus = new JButton("СПИСАТЬ");
        buttonBonus.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int id, bonus;
                try {
                    id = Integer.parseInt(idField.getText());
                    bonus = Integer.parseInt(bonusField.getText());

                    try (Connection connection = DBConnector.getDbConnect(dbName)) {
                        PreparedStatement readStatement = connection.prepareStatement(readSQL);

                        readStatement.setInt(1, id);
                        ResultSet resultSet = readStatement.executeQuery();

                        if (resultSet.next()) {
                            int points;
                            if (resultSet.getInt("clubid") == 0) {
                                LocalDate date = LocalDate.now();
                                Period period = Period.between(resultSet.getDate("date").toLocalDate(), date);
                                points = (int) period.toTotalMonths() * 100 - resultSet.getInt("points");
                                int updatePoints = resultSet.getInt("points") + bonus;

                                if (JOptionPane.showOptionDialog(null, "Имя члена: " + resultSet.getString("name") + "\nТип членства: " + resultSet.getString("type") + "\nНомер клуба: " + resultSet.getInt("clubid") + "\nЧленский взнос: " + resultSet.getInt("fees") + "\nБонусные баллы: " + points + "\nВсё верно?\nСПИСЫВАЕМ " + bonus + " БАЛЛОВ?!", "Информация члена ID №" + resultSet.getInt("id"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"да", "нет"}, "нет") == 0) {

                                    if (period.toTotalMonths() * 100 - updatePoints >= 0) {
                                        PreparedStatement writeStatement = connection.prepareStatement(writeSQL);
                                        writeStatement.setInt(1, updatePoints);
                                        writeStatement.setInt(2, id);
                                        writeStatement.executeUpdate();
                                        JOptionPane.showMessageDialog(null, "С баланса члена №" + id + " списано " + bonus + " баллов", "Списание бонусов члена ID №" + resultSet.getInt("id"), JOptionPane.INFORMATION_MESSAGE);
                                    } else
                                        JOptionPane.showMessageDialog(null, "ОШИБКА: Бонусных баллов недостаточно!", "Списание бонусов члена ID №" + resultSet.getInt("id"), JOptionPane.ERROR_MESSAGE);
                                }
                                ;
                            } else
                                JOptionPane.showMessageDialog(null, "ОШИБКА: Член не участвует в бонусной программе!", "Списание бонусов члена ID №" + resultSet.getInt("id"), JOptionPane.ERROR_MESSAGE);

                        } else
                            JOptionPane.showMessageDialog(null, "ОШИБКА: Члена с указанным номером нет в списках!", "Списание бонусов члена ID №" + id, JOptionPane.ERROR_MESSAGE);

                    } catch (SQLException | ClassNotFoundException f) {
                        System.out.println(f.getMessage());
                        JOptionPane.showMessageDialog(null, "ОШИБКА: Нет связи с базой данных!", "Списание бонусов члена ID №" + id, JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception m) {
                    System.out.println(m.getMessage());
                    JOptionPane.showMessageDialog(null, "ОШИБКА: Введено не числовое значение!", "Списание бонусов члена ID №", JOptionPane.ERROR_MESSAGE);
                }
                ;
            }
        });

        panel.add(buttonBonus);
        JButton buttonBack = new JButton("НАЗАД");
        buttonBack.addActionListener(e -> getChoice());

        container.add(new Item());
        container.add(new JLabel("СПИСАТЬ БОНУСЫ ЧЛЕНА №", SwingConstants.CENTER));
        container.add(panel);
        container.add(new JLabel("", SwingConstants.CENTER));
        container.add(buttonBack);


        frame.add(container);
        frame.repaint();
        frame.revalidate();
    }
}