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
        //Очищаем рабочую панель окна приложения
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
        //Очищаем рабочую панель окна приложения
        container.removeAll();
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
            String type;
            int fees;

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
                statement.executeUpdate("create table if not exists Members (id int not null auto_increment, name varchar(45), type varchar(6), clubid int, clubname varchar(15), fees int, points int, date date, primary key (id))");
                //Создаём объект класса члена
                Member member = new Member(0, name, type, clubId, clubName, fees, 0, LocalDate.now());
                //Выводим окно с информацией добавляемого члена для верификации
                if (JOptionPane.showOptionDialog(null, member + "\nВсё верно?\nДОБАВЛЯЕМ?!", "Добавление нового члена сети клубов", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"да", "нет"}, "нет") == 0) {
                    //Вносим данные Нового члена в БД
                    String sqlAdd = "insert into Members set name = '" + member.getName() + "', type = '" + member.getType() + "', clubid = " + member.getClubId() + ", clubname = '" + member.getClubName() + "', fees = " + member.getFees() + ", points = " + member.getPoints() + ", date = '" + member.getDate() + "'";
                    statement.executeUpdate(sqlAdd);
                }
                //Выводим окно с информацией вновь добавленного члена
                ResultSet resultSet = statement.executeQuery("select id, fees from Members order by id desc limit 1;");
                if (resultSet.next()) {
                    member.setId(resultSet.getInt("id"));
                    JOptionPane.showMessageDialog(null, "Член сети клубов ID №" + member.getId() + " с взносом " + member.getFees() + "р. добавлен!", "Добавление нового члена сети клубов", JOptionPane.INFORMATION_MESSAGE);
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
        //Очищаем рабочую панель окна приложения
        container.removeAll();
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
            try {
                int id = Integer.parseInt(idField.getText());
                //Считываем данные интересующего члена из БД
                try (Connection connection = DBConnector.getDbConnect(dbName)) {
                    String sql = "select * from Members where id = ?";
                    PreparedStatement statement = connection.prepareStatement(sql);
                    statement.setInt(1, id);
                    ResultSet resultSet = statement.executeQuery();
                    if (resultSet.next()) {
                        //Создаём объект класса члена
                        Member member = new Member(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("type"), resultSet.getInt("clubid"), resultSet.getString("clubname"), resultSet.getInt("fees"), resultSet.getInt("points"), resultSet.getDate("date").toLocalDate());
                        //Выводим окно с информацией интересующего члена
                        JOptionPane.showMessageDialog(null, member, "Информация члена ID №" + member.getId(), JOptionPane.INFORMATION_MESSAGE);
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
        //Очищаем рабочую панель окна приложения
        container.removeAll();
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
            try {
                int id = Integer.parseInt(idField.getText());
                int bonus = Integer.parseInt(bonusField.getText());
                //Считываем данные искомого члена из БД
                try (Connection connection = DBConnector.getDbConnect(dbName)) {
                    String readSQL = "select * from Members where id = ?";
                    PreparedStatement readStatement = connection.prepareStatement(readSQL);
                    readStatement.setInt(1, id);
                    ResultSet resultSet = readStatement.executeQuery();
                    if (resultSet.next()) {
                        //Создаём объект класса члена
                        Member member = new Member(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("type"), resultSet.getInt("clubid"), resultSet.getString("clubname"), resultSet.getInt("fees"), resultSet.getInt("points"), resultSet.getDate("date").toLocalDate());
                        if (member.getClubId() == 0) {
                            //Выводим окно с информацией интересующего члена для верификации
                            if (JOptionPane.showOptionDialog(null, member + "\nВсё верно?\nСПИСЫВАЕМ " + bonus + " БАЛЛОВ?!", "Списание бонусов члена ID №" + member.getId(), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"да", "нет"}, "нет") == 0) {
                                //Вносим обновлённое количество списанных бонусных баллов члена в БД
                                if (Period.between(member.getDate(), LocalDate.now()).toTotalMonths() * 100 >= member.getPoints() + bonus) {
                                    String writeSQL = "update Members set points = ? where id = ?";
                                    PreparedStatement writeStatement = connection.prepareStatement(writeSQL);
                                    writeStatement.setInt(1, member.getPoints() + bonus);
                                    writeStatement.setInt(2, member.getId());
                                    writeStatement.executeUpdate();
                                    //Выводим окно с информацией о списании бонусов члена
                                    JOptionPane.showMessageDialog(null, "С баланса члена №" + member.getId() + " списано " + bonus + " баллов", "Списание бонусов члена ID №" + member.getId(), JOptionPane.INFORMATION_MESSAGE);
                                    getChoice();
                                } else
                                    JOptionPane.showMessageDialog(null, "ОШИБКА: Бонусных баллов недостаточно!", "Списание бонусов члена ID №" + member.getId(), JOptionPane.ERROR_MESSAGE);
                            }
                        } else
                            JOptionPane.showMessageDialog(null, "ОШИБКА: Член не участвует в бонусной программе!", "Списание бонусов члена ID №" + member.getId(), JOptionPane.ERROR_MESSAGE);
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
        //Очищаем рабочую панель окна приложения
        container.removeAll();
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
            try {
                int id = Integer.parseInt(idField.getText());
                //Считываем данные искомого члена из базы данных
                try (Connection connection = DBConnector.getDbConnect(dbName)) {
                    String readSql = "select * from Members where id = ?";
                    PreparedStatement readStatement = connection.prepareStatement(readSql);
                    readStatement.setInt(1, id);
                    ResultSet resultSet = readStatement.executeQuery();
                    if (resultSet.next()) {
                        //Создаём объект класса члена
                        Member member = new Member(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("type"), resultSet.getInt("clubid"), resultSet.getString("clubname"), resultSet.getInt("fees"), resultSet.getInt("points"), resultSet.getDate("date").toLocalDate());
                        //Выводим окно с информацией интересующего члена для верификации
                        if (JOptionPane.showOptionDialog(null, member + "\nВсё верно?\nУДАЛЯЕМ?!", "Удаление члена ID №" + member.getId(), JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new Object[]{"да", "нет"}, "нет") == 0) {
                            //Удаляем члена из базы данных
                            String delSql = "delete from Members where id = ?";
                            PreparedStatement delStatement = connection.prepareStatement(delSql);
                            delStatement.setInt(1, id);
                            delStatement.executeUpdate();
                            //Выводим окно с информацией об удалении члена
                            JOptionPane.showMessageDialog(null, "Член сети клубов ID №" + member.getId() + " удалён!", "Удаление члена ID №" + member.getId(), JOptionPane.INFORMATION_MESSAGE);
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