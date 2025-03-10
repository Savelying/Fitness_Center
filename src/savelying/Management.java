package savelying;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.Period;

public class Management {
    String dbName = "Fitness_Center";
    Frame frame = new Frame(600, 500);
    Container container = new Container();
    JPanel panel = new JPanel();

    //Метод выбора действия
    public void getChoice() {
        container.removeAll();
        container.setLayout(new GridLayout(5, 1));
        panel.removeAll();
        panel.setLayout(new GridLayout(2, 2));

        //Рисуем панель с кнопками выбора требуемого действия
        JButton buttonAdd = new JButton("Добавить члена");
        buttonAdd.addActionListener(_ -> addMember());
        panel.add(buttonAdd);
        JButton buttonInfo = new JButton("Узнать данные члена");
        buttonInfo.addActionListener(_ -> printMemberInfo());
        panel.add(buttonInfo);
        JButton buttonBonus = new JButton("Списать бонусы члена");
        buttonBonus.addActionListener(_ -> updateMemberPoints());
        panel.add(buttonBonus);
        JButton buttonDel = new JButton("Удалить члена");
        buttonDel.addActionListener(_ -> removeMember());
        panel.add(buttonDel);

        //Рисуем кнопку "Выход"
        JButton buttonExit = new JButton("ВЫХОД");
        buttonExit.addActionListener(_ -> System.exit(0));

        //Прорисовываем окно выбора требуемого действия полностью
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
    public void addMember() {
        container.removeAll();
        container.setLayout(new GridLayout(5, 1));
        panel.removeAll();
        panel.setLayout(new FlowLayout());

        //Рисуем панель с полями для ввода имени члена и выбора клуба
        panel.add(new JLabel("Имя нового клиента:"));
        JTextField nameField = new JTextField("", 30);
        panel.add(nameField);
        //Присваиваем клуб клиенту
        panel.add(new JLabel("Выберите желаемый клуб:"));
        JComboBox<String> clubBox = new JComboBox<>();
        clubBox.addItem("Мультиклуб");
        clubBox.addItem("Меркурий");
        clubBox.addItem("Венера");
        clubBox.addItem("Марс");
        panel.add(clubBox);
        //Фиксируем имя и клуб нового члена
        JButton buttonAdd = new JButton("ДОБАВИТЬ");
        panel.add(buttonAdd);
        buttonAdd.addActionListener(_ -> {
            Calculator<Integer> calculator;
            LocalDate date = LocalDate.now();
            int fees, points = 0;
            String type;

            String name = nameField.getText();
            String clubName = (String) clubBox.getSelectedItem();
            int clubId = clubBox.getSelectedIndex();

            //Присваиваем абонентскую плату
            if (clubId != 0) {
                calculator = (n) -> switch (n) {
                    case 1 -> 900;
                    case 2 -> 950;
                    case 3 -> 1000;
                    case 4 -> 1050;
                    default -> 0;
                };
                fees = calculator.calculateFees(clubId);
                type = "single";
            } else {
                calculator = (n) -> switch (n) {
                    case 0 -> 1500;
                    default -> 0;
                };
                fees = calculator.calculateFees(clubId);
                type = "multi";
            }
            //Добавляем нового члена в БД
            try (Connection connection = DBConnector.getServConnect()) {
                Statement statement = connection.createStatement();
                //Создаём схему БД, если ещё нет
                String dbNameSQL = "create database if not exists " + dbName;
                statement.executeUpdate(dbNameSQL);
                //Выбираем нужную БД для работы
                String dbUseSQL = "use " + dbName;
                statement.executeUpdate(dbUseSQL);
                //Создаём таблицу в БД, если ещё нет
                statement.executeUpdate("create table if not exists Members (id int not null auto_increment, name varchar(45), type varchar(6), clubname varchar(15), clubid int, fees int, date date, points int, primary key (id))");
                //Выводим окно с информацией добавляемого члена для верификации
                if (JOptionPane.showOptionDialog(null, "Имя члена: " + name + "\nКлуб: " + clubName + "(" + clubId + ")" + "\nЧленский взнос: " + fees + "\nБонусные баллы: " + points + "\nВсё верно?\nДОБАВЛЯЕМ?!", "Добавление нового члена сети клубов", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"да", "нет"}, "нет") == 0) {
                    //Вносим данные Нового члена в БД
                    String sql = "insert into Members set name = '" + name + "', type = '" + type + "', clubname = '" + clubName + "', clubid = " + clubId + ", fees = " + fees + ", date = '" + date + "', points = " + points;
                    statement.executeUpdate(sql);
                }
                //Выводим окно с информацией вновь добавленного члена
                ResultSet resultSet = statement.executeQuery("select id, fees from Members order by id desc limit 1;");
                if (resultSet.next()) {
                    JOptionPane.showMessageDialog(null, "Член сети клубов ID №" + resultSet.getInt("id") + " с взносом " + resultSet.getInt("fees") + "р. добавлен!", "Добавление нового члена сети клубов", JOptionPane.INFORMATION_MESSAGE);
                    getChoice();
                }

            } catch (SQLException | ClassNotFoundException f) {
                System.out.println(f.getMessage());
                JOptionPane.showMessageDialog(null, "ОШИБКА: Нет связи с базой данных!", "Добавление нового члена сети клубов", JOptionPane.ERROR_MESSAGE);
            }
        });

        //Рисуем кнопку "Назад"
        JButton buttonBack = new JButton("НАЗАД");
        buttonBack.addActionListener(_ -> getChoice());

        //Прорисовываем окно добавления нового члена полностью
        container.add(new Item());
        container.add(new JLabel("ДОБАВЛЕНИЕ НОВОГО ЧЛЕНА", SwingConstants.CENTER));
        container.add(panel);
        container.add(new JLabel("", SwingConstants.CENTER));
        container.add(buttonBack);
        frame.add(container);
        frame.repaint();
        frame.revalidate();
    }

    //Метод вывода данных клиента
    public void printMemberInfo() {
        container.removeAll();
        container.setLayout(new GridLayout(5, 1));
        panel.removeAll();
        panel.setLayout(new FlowLayout());

        //Рисуем панель с полем для ввода номера члена
        panel.add(new JLabel("Идентификационный номер:"));
        JTextField idField = new JTextField("", 30);
        panel.add(idField);
        //Фиксируем имя искомого члена
        JButton buttonInfo = new JButton("ПОКАЗАТЬ");
        panel.add(buttonInfo);
        buttonInfo.addActionListener(_ -> {
            int id;
            try {
                id = Integer.parseInt(idField.getText());
                //Считываем данные интересующего члена из БД
                try (Connection connection = DBConnector.getDbConnect(dbName)) {
                    String sql = "select * from Members where id = ?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, id);
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        int points;
                        //Считаем количество доступных бонусов
                        if (resultSet.getInt("clubid") == 0) {
                            LocalDate date = LocalDate.now();
                            Period period = Period.between(resultSet.getDate("date").toLocalDate(), date);
                            points = (int) period.toTotalMonths() * 100 - resultSet.getInt("points");
                        } else points = resultSet.getInt("points");
                        //Выводим окно с информацией интересующего члена
                        JOptionPane.showMessageDialog(null, "Имя члена: " + resultSet.getString("name") + "\nКлуб: " + resultSet.getString("clubname") + "(" + resultSet.getInt("clubid") + ")" + "\nЧленский взнос: " + resultSet.getInt("fees") + "\nБонусные баллы: " + points, "Информация члена ID №" + resultSet.getInt("id"), JOptionPane.INFORMATION_MESSAGE);
                        getChoice();
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
        });

        //Рисуем кнопку "Назад"
        JButton buttonBack = new JButton("НАЗАД");
        buttonBack.addActionListener(_ -> getChoice());

        //Прорисовываем окно вывода информации нужного члена полностью
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
    public void updateMemberPoints() {
        container.removeAll();
        container.setLayout(new GridLayout(5, 1));
        panel.removeAll();
        panel.setLayout(new FlowLayout());

        //Рисуем панель с полями для ввода номера члена и списываемых бонусов
        panel.add(new JLabel("Идентификационный номер:"));
        JTextField idField = new JTextField("", 30);
        panel.add(idField);
        panel.add(new JLabel("Сколько списать баллов:"));
        JTextField bonusField = new JTextField("", 30);
        panel.add(bonusField);
        //Фиксируем номер члена и количество списываемых баллов
        JButton buttonBonus = new JButton("СПИСАТЬ");
        panel.add(buttonBonus);
        buttonBonus.addActionListener(_ -> {
            int id, bonus;
            try {
                id = Integer.parseInt(idField.getText());
                bonus = Integer.parseInt(bonusField.getText());
                //Считываем данные искомого члена из БД
                try (Connection connection = DBConnector.getDbConnect(dbName)) {
                    String readSQL = "select * from Members where id = ?";
                    PreparedStatement readStatement = connection.prepareStatement(readSQL);
                    readStatement.setInt(1, id);
                    ResultSet resultSet = readStatement.executeQuery();
                    if (resultSet.next()) {
                        int points;
                        //Считаем количество доступных бонусов
                        if (resultSet.getInt("clubid") == 0) {
                            LocalDate date = LocalDate.now();
                            Period period = Period.between(resultSet.getDate("date").toLocalDate(), date);
                            points = (int) period.toTotalMonths() * 100 - resultSet.getInt("points");
                            int updatePoints = resultSet.getInt("points") + bonus;
                            //Выводим окно с информацией интересующего члена для верификации
                            if (JOptionPane.showOptionDialog(null, "Имя члена: " + resultSet.getString("name") + "\nКлуб: " + resultSet.getString("clubname") + "(" + resultSet.getInt("clubid") + ")" + "\nЧленский взнос: " + resultSet.getInt("fees") + "\nБонусные баллы: " + points + "\nВсё верно?\nСПИСЫВАЕМ " + bonus + " БАЛЛОВ?!", "Информация члена ID №" + resultSet.getInt("id"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"да", "нет"}, "нет") == 0) {
                                //Вносим обновлённое количество списанных бонусных баллов члена в БД
                                if (period.toTotalMonths() * 100 - updatePoints >= 0) {
                                    String writeSQL = "update Members set points = ? where id = ?";
                                    PreparedStatement writeStatement = connection.prepareStatement(writeSQL);
                                    writeStatement.setInt(1, updatePoints);
                                    writeStatement.setInt(2, id);
                                    writeStatement.executeUpdate();
                                    //Выводим окно с информацией о списании бонусов члена
                                    JOptionPane.showMessageDialog(null, "С баланса члена №" + id + " списано " + bonus + " баллов", "Списание бонусов члена ID №" + resultSet.getInt("id"), JOptionPane.INFORMATION_MESSAGE);
                                    getChoice();
                                } else
                                    JOptionPane.showMessageDialog(null, "ОШИБКА: Бонусных баллов недостаточно!", "Списание бонусов члена ID №" + resultSet.getInt("id"), JOptionPane.ERROR_MESSAGE);
                            }
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
        });

        //Рисуем кнопку "Назад"
        JButton buttonBack = new JButton("НАЗАД");
        buttonBack.addActionListener(_ -> getChoice());

        //Прорисовываем окно списания бонусных баллов члена полностью
        container.add(new Item());
        container.add(new JLabel("СПИСАТЬ БОНУСЫ ЧЛЕНА №", SwingConstants.CENTER));
        container.add(panel);
        container.add(new JLabel("", SwingConstants.CENTER));
        container.add(buttonBack);
        frame.add(container);
        frame.repaint();
        frame.revalidate();
    }

    //Метод удаления клиента
    public void removeMember() {
        container.removeAll();
        container.setLayout(new GridLayout(5, 1));
        panel.removeAll();
        panel.setLayout(new FlowLayout());

        //Рисуем панель с полем для ввода номера клиента
        panel.add(new JLabel("Идентификационный номер:"));
        JTextField idField = new JTextField("", 30);
        panel.add(idField);
        //Фиксируем номер удаляемого члена
        JButton buttonDel = new JButton("УДАЛИТЬ");
        panel.add(buttonDel);
        buttonDel.addActionListener(_ -> {
            int id;
            try {
                id = Integer.parseInt(idField.getText());
                //Считываем данные искомого члена из базы данных
                try (Connection connection = DBConnector.getDbConnect(dbName)) {
                    String readSql = "select * from Members where id = ?";
                    PreparedStatement readStatement = connection.prepareStatement(readSql);
                    readStatement.setInt(1, id);
                    ResultSet resultSet = readStatement.executeQuery();

                    if (resultSet.next()) {
                        int points;
                        //Считаем количество доступных бонусов
                        if (resultSet.getInt("clubid") == 0) {
                            LocalDate date = LocalDate.now();
                            Period period = Period.between(resultSet.getDate("date").toLocalDate(), date);
                            points = (int) period.toTotalMonths() * 100 - resultSet.getInt("points");
                        } else points = resultSet.getInt("points");
                        //Выводим окно с информацией интересующего члена для верификации
                        if (JOptionPane.showOptionDialog(null, "Имя члена: " + resultSet.getString("name") + "\nКлуб: " + resultSet.getString("clubname") + "(" + resultSet.getInt("clubid") + ")" + "\nЧленский взнос: " + resultSet.getInt("fees") + "\nБонусные баллы: " + points + "\nВсё верно?\nУДАЛЯЕМ?!", "Удаление члена ID №" + resultSet.getInt("id"), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"да", "нет"}, "нет") == 0) {
                            //Удаляем члена из базы данных
                            String delSql = "delete from Members where id = ?";
                            PreparedStatement delStatement = connection.prepareStatement(delSql);
                            delStatement.setInt(1, id);
                            delStatement.executeUpdate();
                            //Выводим окно с информацией об удалении члена
                            JOptionPane.showMessageDialog(null, "Член сети клубов ID №" + resultSet.getInt("id") + " удалён!", "Удаление члена ID №" + resultSet.getInt("id"), JOptionPane.INFORMATION_MESSAGE);
                            getChoice();
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
        });

        //Рисуем кнопку "Назад"
        JButton buttonBack = new JButton("НАЗАД");
        buttonBack.addActionListener(_ -> getChoice());

        //Прорисовываем окно удаления члена полностью
        container.add(new Item());
        container.add(new JLabel("УДАЛЕНИЕ ЧЛЕНА №", SwingConstants.CENTER));
        container.add(panel);
        container.add(new JLabel("", SwingConstants.CENTER));
        container.add(buttonBack);
        frame.add(container);
        frame.repaint();
        frame.revalidate();
    }
}