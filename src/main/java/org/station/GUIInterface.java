package org.station;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.station.entity.*;
import org.station.service.*;

import java.time.LocalDate;
import java.util.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class GUIInterface {
    private final CarService carService;
    private final ClientService clientService;
    private final MasterService masterService;
    private final SpareService spareService;
    private final SpareOrderService spareOrderService;
    private final RepairService repairService;
    private final RepairTypeService repairTypeService;
    private final ServiceStationService serviceStationService;
    private final Scanner scanner = new Scanner(System.in);

    private JFrame mainFrame;
    private ServiceStation currentStation;

    public void run() {
        SwingUtilities.invokeLater(() -> {
            mainFrame = new JFrame("Сервіс Технічного Обслуговування");
            mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            mainFrame.setSize(600, 400);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setLayout(new BorderLayout());

            showStationSelectionScreen();

            mainFrame.setVisible(true);
        });
    }

    private void showStationSelectionScreen() {
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new GridLayout(4, 1, 10, 10));
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));

        JButton selectStationButton = new JButton("Вибрати станцію");
        JButton createStationButton = new JButton("Створити сервісну станцію");
        JButton showStationsButton = new JButton("Показати існуючі сервісні станції");
        JButton deleteStationButton = new JButton("Видалити сервісну станцію");
        JButton exitButton = new JButton("Вийти");

        selectStationButton.addActionListener(e -> selectServiceStation());
        createStationButton.addActionListener(e -> createServiceStation());
        showStationsButton.addActionListener(e -> showServiceStations());
        deleteStationButton.addActionListener(e -> deleteServiceStation());
        exitButton.addActionListener(e -> System.exit(0));

        selectionPanel.add(selectStationButton);
        selectionPanel.add(createStationButton);
        selectionPanel.add(showStationsButton);
        selectionPanel.add(deleteStationButton);
        selectionPanel.add(exitButton);

        switchPanel(selectionPanel);
    }

    private void selectServiceStation() {
        List<ServiceStation> stations = serviceStationService.getAllStations();

        if (stations.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Немає доступних сервісних станцій. Створіть нову.",
                    "Інформація", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] stationNames = new String[stations.size()];
        for (int i = 0; i < stations.size(); i++) {
            ServiceStation station = stations.get(i);
            stationNames[i] = station.getName() + " (" + station.getAddress() + ")";
        }

        String selectedName = (String) JOptionPane.showInputDialog(
                mainFrame,
                "Виберіть сервісну станцію:",
                "Вибір сервісної станції",
                JOptionPane.QUESTION_MESSAGE,
                null,
                stationNames,
                stationNames[0]
        );

        if (selectedName != null) {
            int index = -1;
            for (int i = 0; i < stationNames.length; i++) {
                if (stationNames[i].equals(selectedName)) {
                    index = i;
                    break;
                }
            }

            if (index >= 0) {
                currentStation = stations.get(index);
                JOptionPane.showMessageDialog(mainFrame,
                        "Вибрано станцію: " + currentStation.getName(),
                        "Інформація", JOptionPane.INFORMATION_MESSAGE);

                // Переход к основному меню после выбора станции
                showMainMenu();
            }
        }
    }

    private void createServiceStation() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JTextField nameField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField phoneField = new JTextField();

        panel.add(new JLabel("Назва станції:"));
        panel.add(nameField);
        panel.add(new JLabel("Адреса:"));
        panel.add(addressField);
        panel.add(new JLabel("Телефон:"));
        panel.add(phoneField);

        int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Створення сервісної станції", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String address = addressField.getText().trim();
            String phone = phoneField.getText().trim();

            // Валидация
            if (name.isEmpty() || address.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Всі поля повинні бути заповнені.", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!Pattern.matches("\\d{10}", phone)) {
                JOptionPane.showMessageDialog(mainFrame, "Невірний формат номера телефону. Використовуйте 10 цифр.", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ServiceStation newStation = new ServiceStation();
            newStation.setName(name);
            newStation.setAddress(address);
            newStation.setPhone(phone);

            serviceStationService.addStation(newStation);

            JOptionPane.showMessageDialog(mainFrame, "Сервісну станцію успішно створено!", "Успіх", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showServiceStations() {
        List<ServiceStation> stations = serviceStationService.getAllStations();

        if (stations.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Наразі немає жодної сервісної станції.", "Інформація", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] columnNames = {"ID", "Назва", "Адреса", "Телефон"};
        Object[][] data = new Object[stations.size()][4];

        for (int i = 0; i < stations.size(); i++) {
            ServiceStation station = stations.get(i);
            data[i][0] = station.getId();
            data[i][1] = station.getName();
            data[i][2] = station.getAddress();
            data[i][3] = station.getPhone();
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        JOptionPane.showMessageDialog(mainFrame, scrollPane, "Список сервісних станцій", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteServiceStation() {
        List<ServiceStation> stations = serviceStationService.getAllStations();

        if (stations.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Немає сервісних станцій для видалення.", "Інформація", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] stationNames = new String[stations.size()];
        for (int i = 0; i < stations.size(); i++) {
            ServiceStation station = stations.get(i);
            stationNames[i] = station.getName() + " (" + station.getAddress() + ")";
        }

        String selectedName = (String) JOptionPane.showInputDialog(
                mainFrame,
                "Виберіть сервісну станцію для видалення:",
                "Видалення станції",
                JOptionPane.QUESTION_MESSAGE,
                null,
                stationNames,
                stationNames[0]
        );

        if (selectedName != null) {
            int index = -1;
            for (int i = 0; i < stationNames.length; i++) {
                if (stationNames[i].equals(selectedName)) {
                    index = i;
                    break;
                }
            }

            if (index >= 0) {
                ServiceStation stationToDelete = stations.get(index);
                int confirm = JOptionPane.showConfirmDialog(mainFrame,
                        "Ви впевнені, що хочете видалити станцію \"" + stationToDelete.getName() + "\"?",
                        "Підтвердження",
                        JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean deleted = serviceStationService.deleteStation(stationToDelete.getId());
                    if (deleted) {
                        JOptionPane.showMessageDialog(mainFrame, "Станцію видалено успішно.", "Успіх", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(mainFrame, "Помилка під час видалення станції.", "Помилка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }

    private void showMainMenu() {
        if (currentStation == null) {
            JOptionPane.showMessageDialog(mainFrame, "Спочатку виберіть сервісну станцію.", "Попередження", JOptionPane.WARNING_MESSAGE);
            showStationSelectionScreen();
            return;
        }

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10)); // 6 кнопок вместо 5
        panel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));

        JLabel stationLabel = new JLabel("Поточна станція: " + currentStation.getName(), JLabel.CENTER);
        stationLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton addButton = new JButton("Додати об'єкт");
        JButton removeButton = new JButton("Видалити об'єкт");
        JButton viewButton = new JButton("Переглянути об'єкти");
        JButton otherOperationsButton = new JButton("Інші операції");
        JButton backButton = new JButton("Повернутися до вибору станції");
        JButton exitButton = new JButton("Вийти");

        addButton.addActionListener(e -> showAddMenu());
        removeButton.addActionListener(e -> showRemoveMenu());
        viewButton.addActionListener(e -> showViewMenu());
        otherOperationsButton.addActionListener(e -> showOtherOperationsMenu());
        backButton.addActionListener(e -> showStationSelectionScreen());
        exitButton.addActionListener(e -> System.exit(0));

        // Добавляем заголовок в верхней части панели
        mainFrame.getContentPane().removeAll();
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(stationLabel, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);

        panel.add(addButton);
        panel.add(removeButton);
        panel.add(viewButton);
        panel.add(otherOperationsButton);
        panel.add(backButton);
        panel.add(exitButton);

        mainFrame.add(mainPanel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private void showAddMenu() {
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new GridLayout(9, 1, 10, 10)); // Увеличено количество строк на 1
        addPanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));

        JButton addMasterButton = new JButton("Додати майстра");
        JButton addClientButton = new JButton("Додати клієнта");
        JButton addCarButton = new JButton("Додати авто");
        JButton addPartButton = new JButton("Замовити запчастину");
        JButton addPartOrderButton = new JButton("Додати замовлення на запчастину");
        JButton addRepairButton = new JButton("Додати ремонт");
        JButton addRepairTypeButton = new JButton("Додати тип ремонту");
        JButton backButton = new JButton("Повернутися до головного меню");


        addMasterButton.addActionListener(e -> addMaster());
        addClientButton.addActionListener(e -> addClient());
        addCarButton.addActionListener(e -> addCar());
        addPartButton.addActionListener(e -> addSpare());
        addPartOrderButton.addActionListener(e -> addSpareOrder());
        addRepairButton.addActionListener(e -> addRepair());
        addRepairTypeButton.addActionListener(e -> addRepairType());
        backButton.addActionListener(e -> showMainMenu());

        addPanel.add(addMasterButton);
        addPanel.add(addClientButton);
        addPanel.add(addCarButton);
        addPanel.add(addPartButton);
        addPanel.add(addPartOrderButton);
        addPanel.add(addRepairButton);
        addPanel.add(addRepairTypeButton);
        addPanel.add(backButton);

        switchPanel(addPanel);
    }

    private void showRemoveMenu() {
        JPanel removePanel = new JPanel();
        removePanel.setLayout(new GridLayout(10, 1, 10, 10)); // Оставляем 10 строк
        removePanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));

        JButton removeMasterButton = new JButton("Видалити майстра");
        JButton removeClientButton = new JButton("Видалити клієнта");
        JButton removeCarButton = new JButton("Видалити авто");
        JButton removePartButton = new JButton("Видалити запчастину");
        JButton removeRepairButton = new JButton("Видалити ремонт");
        JButton removeRepairTypeButton = new JButton("Видалити тип ремонту");
        JButton removeUserButton = new JButton("Видалити користувача");
        JButton removePartOrderButton = new JButton("Видалити замовлення запчастини");

        JButton backButton = new JButton("Повернутися до головного меню");

        removeMasterButton.addActionListener(e -> removeMaster());
        removeClientButton.addActionListener(e -> removeClient());
        removeCarButton.addActionListener(e -> removeCar());
        removePartButton.addActionListener(e -> removeSpare());
        removeRepairButton.addActionListener(e -> removeRepair());
        removeRepairTypeButton.addActionListener(e -> removeRepairType());
        removePartOrderButton.addActionListener(e -> removeSpareOrder());
        backButton.addActionListener(e -> showMainMenu());

        removePanel.add(removeMasterButton);
        removePanel.add(removeClientButton);
        removePanel.add(removeCarButton);
        removePanel.add(removePartButton);
        removePanel.add(removeRepairButton);
        removePanel.add(removeRepairTypeButton);
        removePanel.add(removeUserButton);
        removePanel.add(removePartOrderButton);
        removePanel.add(backButton);

        switchPanel(removePanel);
    }

    private void showViewMenu() {
        JPanel viewPanel = new JPanel();
        viewPanel.setLayout(new GridLayout(9, 1, 10, 10));
        viewPanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));

        JButton viewMastersButton = new JButton("Переглянути майстрів");
        JButton viewClientsButton = new JButton("Переглянути клієнтів");
        JButton viewCarsButton = new JButton("Переглянути авто");
        JButton viewPartsButton = new JButton("Переглянути запчастини");
        JButton viewPartOrdersButton = new JButton("Переглянути замовлення на запчастини");
        JButton viewRepairsButton = new JButton("Переглянути ремонти");
        JButton viewRepairTypesButton = new JButton("Переглянути типи ремонту");
        JButton backButton = new JButton("Повернутися до головного меню");

        viewMastersButton.addActionListener(e -> viewAllMasters());
        viewClientsButton.addActionListener(e -> viewAllClients());
        viewCarsButton.addActionListener(e -> viewAllCars());
        viewPartsButton.addActionListener(e -> listSpare());
        viewPartOrdersButton.addActionListener(e -> listSpareOrder());
        viewRepairsButton.addActionListener(e -> listRepairs());
        viewRepairTypesButton.addActionListener(e -> listRepairTypes());
        backButton.addActionListener(e -> showMainMenu()); // Изменено на showMainMenu

        viewPanel.add(viewMastersButton);
        viewPanel.add(viewClientsButton);
        viewPanel.add(viewCarsButton);
        viewPanel.add(viewPartsButton);
        viewPanel.add(viewPartOrdersButton);
        viewPanel.add(viewRepairsButton);
        viewPanel.add(viewRepairTypesButton);
        viewPanel.add(backButton);

        switchPanel(viewPanel);
    }

    private void showOtherOperationsMenu() {
        JPanel otherOperationsPanel = new JPanel();
        otherOperationsPanel.setLayout(new GridLayout(7, 1, 10, 10)); // Увеличено на 1 для кнопки возврата
        otherOperationsPanel.setBorder(BorderFactory.createEmptyBorder(30, 150, 30, 150));

        JButton avgRepairButton = new JButton("Середня тривалість ремонту");
        JButton findClientsButton = new JButton("Пошук клієнтів за проблемою");
        JButton findCarsButton = new JButton("Пошук авто за маркою");
        JButton findClientsByBrandButton = new JButton("Пошук клієнтів за маркою авто");
        JButton commonProblemButton = new JButton("Найпоширеніша проблема клієнтів");
        JButton numberOfClientsButton = new JButton("Загальна кількість клієнтів");
        JButton backButton = new JButton("Повернутися до головного меню");

        avgRepairButton.addActionListener(e -> showAverageRepairDuration());
        findClientsButton.addActionListener(e -> findClientsByProblem());
        findCarsButton.addActionListener(e -> findCarsByBrand());
        findClientsByBrandButton.addActionListener(e -> findClientsByCarBrand());
        commonProblemButton.addActionListener(e -> showCommonProblem());
        numberOfClientsButton.addActionListener(e -> showNumberOfClients());
        backButton.addActionListener(e -> showMainMenu()); // Изменено на showMainMenu

        otherOperationsPanel.add(avgRepairButton);
        otherOperationsPanel.add(findClientsButton);
        otherOperationsPanel.add(findCarsButton);
        otherOperationsPanel.add(findClientsByBrandButton);
        otherOperationsPanel.add(commonProblemButton);
        otherOperationsPanel.add(numberOfClientsButton);
        otherOperationsPanel.add(backButton);

        switchPanel(otherOperationsPanel);
    }

    private void backToMainMenu() {
        showMainMenu(); // Изменено для использования нового метода showMainMenu
    }

    private void switchPanel(JPanel newPanel) {
        mainFrame.getContentPane().removeAll();
        mainFrame.add(newPanel, BorderLayout.CENTER);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private void addMaster() {
        if (currentStation == null) {
            JOptionPane.showMessageDialog(mainFrame, "Спочатку виберіть сервісну станцію.", "Попередження", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10)); // Уменьшили до 4 строк, так как станция уже выбрана

        JTextField fullNameField = new JTextField();
        JTextField phoneNumberField = new JTextField();
        JTextField addressField = new JTextField();
        JTextField specializationField = new JTextField();

        panel.add(new JLabel("Ім'я майстра:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Номер телефону:"));
        panel.add(phoneNumberField);
        panel.add(new JLabel("Адреса:"));
        panel.add(addressField);
        panel.add(new JLabel("Спеціалізація:"));
        panel.add(specializationField);

        int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Додати Майстра", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String fullName = fullNameField.getText().trim();
            String phoneNumber = phoneNumberField.getText().trim();
            String address = addressField.getText().trim();
            String specialization = specializationField.getText().trim();

            // Валідація вводу
            if (fullName.isEmpty() || phoneNumber.isEmpty() || address.isEmpty() || specialization.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Всі поля повинні бути заповнені.", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!Pattern.matches("\\d{10}", phoneNumber)) {
                JOptionPane.showMessageDialog(mainFrame, "Невірний формат номера телефону. Використовуйте тільки цифри (10 цифр).", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Используем название текущей выбранной станции
            Master master = new Master(currentStation.getName(), fullName, phoneNumber, address, specialization);
            masterService.addMaster(master);
            JOptionPane.showMessageDialog(mainFrame, "Майстра додано!", "Успіх", JOptionPane.INFORMATION_MESSAGE);
            showMainMenu();
        }
    }

    private void addClient() {
        if (currentStation == null) {
            JOptionPane.showMessageDialog(mainFrame, "Спочатку виберіть сервісну станцію.", "Попередження", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10)); // Уменьшили до 4 строк

        JTextField fullNameField = new JTextField();
        JTextField phoneNumberField = new JTextField();
        JTextField problemField = new JTextField();
        JTextField carIdField = new JTextField();

        panel.add(new JLabel("Ім'я клієнта:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Номер телефону:"));
        panel.add(phoneNumberField);
        panel.add(new JLabel("Опис проблеми:"));
        panel.add(problemField);
        panel.add(new JLabel("ID Авто:"));
        panel.add(carIdField);

        int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Додати Клієнта", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String fullName = fullNameField.getText().trim();
            String phoneNumber = phoneNumberField.getText().trim();
            String problem = problemField.getText().trim();
            String carIdStr = carIdField.getText().trim();

            // Валідація вводу
            if (fullName.isEmpty() || phoneNumber.isEmpty() || problem.isEmpty() || carIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Всі поля повинні бути заповнені.", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!Pattern.matches("\\d{10}", phoneNumber)) {
                JOptionPane.showMessageDialog(mainFrame, "Невірний формат номера телефону. Використовуйте тільки цифри (10 цифр).", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            long carId;
            try {
                carId = Long.parseLong(carIdStr);
                if (carId <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame, "ID Авто повинен бути позитивним числом.", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Optional<Car> carOpt = carService.getCarById(carId);
            if (carOpt.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Такого авто не існує.", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Client client = new Client(problem, phoneNumber, currentStation.getName(), fullName, carOpt.get());
            clientService.addClient(client);
            JOptionPane.showMessageDialog(mainFrame, "Клієнта додано!", "Успіх", JOptionPane.INFORMATION_MESSAGE);
            showMainMenu();
        }
    }

    private void addCar() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));

        JTextField brandField = new JTextField();
        JTextField releaseYearField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField repairDurationField = new JTextField();

        panel.add(new JLabel("Марка авто:"));
        panel.add(brandField);
        panel.add(new JLabel("Рік випуску:"));
        panel.add(releaseYearField);
        panel.add(new JLabel("Ціна авто:"));
        panel.add(priceField);
        panel.add(new JLabel("Тривалість ремонту (у днях):"));
        panel.add(repairDurationField);

        int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Додати Авто", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String brand = brandField.getText().trim();
            String releaseYearStr = releaseYearField.getText().trim();
            String priceStr = priceField.getText().trim();
            String repairDurationStr = repairDurationField.getText().trim();

            // Валідація вводу
            if (brand.isEmpty() || releaseYearStr.isEmpty() || priceStr.isEmpty() || repairDurationStr.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Всі поля повинні бути заповнені.", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int releaseYear;
            double price;
            int repairDuration;
            try {
                releaseYear = Integer.parseInt(releaseYearStr);
                price = Double.parseDouble(priceStr);
                repairDuration = Integer.parseInt(repairDurationStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame, "Рік випуску, ціна та тривалість ремонту повинні бути числами.", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Car car = new Car(brand, releaseYear, price, repairDuration);
            carService.addCar(car);
            JOptionPane.showMessageDialog(mainFrame, "Авто додано!", "Успіх", JOptionPane.INFORMATION_MESSAGE);
            backToMainMenu();
        }
    }

    private void removeMaster() {
        String idStr = JOptionPane.showInputDialog(mainFrame, "Введіть ID майстра для видалення:", "Видалити Майстра", JOptionPane.PLAIN_MESSAGE);
        if (idStr != null) {
            long id;
            try {
                id = Long.parseLong(idStr);
                if (id <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame, "ID повинен бути позитивним числом.", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean removed = masterService.removeMaster(id);
            if (removed) {
                JOptionPane.showMessageDialog(mainFrame, "Майстра видалено.", "Успіх", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Майстра не знайдено.", "Помилка", JOptionPane.ERROR_MESSAGE);
            }
            backToMainMenu();
        }
    }

    private void removeClient() {
        String idStr = JOptionPane.showInputDialog(mainFrame, "Введіть ID клієнта для видалення:", "Видалити Клієнта", JOptionPane.PLAIN_MESSAGE);
        if (idStr != null) {
            long id;
            try {
                id = Long.parseLong(idStr);
                if (id <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame, "ID повинен бути позитивним числом.", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean removed = clientService.removeClient(id);
            if (removed) {
                JOptionPane.showMessageDialog(mainFrame, "Клієнта видалено.", "Успіх", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Клієнта не знайдено.", "Помилка", JOptionPane.ERROR_MESSAGE);
            }
            backToMainMenu();
        }
    }

    private void removeCar() {
        String idStr = JOptionPane.showInputDialog(mainFrame, "Введіть ID авто для видалення:", "Видалити Авто", JOptionPane.PLAIN_MESSAGE);
        if (idStr != null) {
            long id;
            try {
                id = Long.parseLong(idStr);
                if (id <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame, "ID повинен бути позитивним числом.", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean removed = carService.removeCar(id);
            if (removed) {
                JOptionPane.showMessageDialog(mainFrame, "Авто видалено.", "Успіх", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Авто не знайдено.", "Помилка", JOptionPane.ERROR_MESSAGE);
            }
            backToMainMenu();
        }
    }

    private void showAverageRepairDuration() {
        double averageDuration = carService.getAverageRepairDuration();
        JOptionPane.showMessageDialog(mainFrame, String.format("Середня тривалість ремонту: %.2f днів.", averageDuration), "Середня Тривалість Ремонту", JOptionPane.INFORMATION_MESSAGE);
        backToMainMenu();
    }

    private void findClientsByProblem() {
        String problem = JOptionPane.showInputDialog(mainFrame, "Введіть проблему для пошуку:", "Пошук Клієнтів за Проблемою", JOptionPane.PLAIN_MESSAGE);
        if (problem != null && !problem.trim().isEmpty()) {
            java.util.List<Client> clients = clientService.getClientsByProblem(problem.trim());

            if (clients.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Клієнтів з такою проблемою не знайдено.", "Результат Пошуку", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Створення таблиці для відображення результатів
                String[] columnNames = {"СТО", "Ім'я клієнта", "Телефон", "Проблема", "ID Авто"};
                Object[][] data = new Object[clients.size()][5];
                for (int i = 0; i < clients.size(); i++) {
                    Client client = clients.get(i);
                    data[i][0] = client.getServiceStationName();
                    data[i][1] = client.getFullName();
                    data[i][2] = client.getPhoneNumber();
                    data[i][3] = client.getProblem();
                    data[i][4] = (Object) client.getCar().getId();
                }

                JTable table = new JTable(data, columnNames);
                JScrollPane scrollPane = new JScrollPane(table);
                table.setFillsViewportHeight(true);

                JOptionPane.showMessageDialog(mainFrame, scrollPane, "Результати Пошуку Клієнтів", JOptionPane.INFORMATION_MESSAGE);
            }
            backToMainMenu();
        } else if (problem != null) { // Якщо користувач залишив поле порожнім
            JOptionPane.showMessageDialog(mainFrame, "Поле не повинно бути порожнім.", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void findCarsByBrand() {
        String brand = JOptionPane.showInputDialog(mainFrame, "Введіть марку авто:", "Пошук Авто за Маркою", JOptionPane.PLAIN_MESSAGE);
        if (brand != null && !brand.trim().isEmpty()) {
            List<Car> cars = carService.getCarsByBrand(brand.trim());

            if (cars.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Автомобілі з цією маркою не знайдено.", "Результат Пошуку", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String[] columnNames = {"ID Авто", "Марка", "Рік випуску", "Ціна", "Час ремонту (днів)"};
                Object[][] data = new Object[cars.size()][5];
                for (int i = 0; i < cars.size(); i++) {
                    Car car = cars.get(i);
                    data[i][0] = (Object) car.getId();
                    data[i][1] = car.getBrand();
                    data[i][2] = (Object) car.getReleaseYear();
                    data[i][3] = (Object) car.getPrice();
                    data[i][4] = (Object) car.getRepairDuration();
                }

                JTable table = new JTable(data, columnNames);
                JScrollPane scrollPane = new JScrollPane(table);
                table.setFillsViewportHeight(true);

                JOptionPane.showMessageDialog(mainFrame, scrollPane, "Результати Пошуку Авто", JOptionPane.INFORMATION_MESSAGE);
            }
            backToMainMenu();
        } else if (brand != null) { // Якщо користувач залишив поле порожнім
            JOptionPane.showMessageDialog(mainFrame, "Поле не повинно бути порожнім.", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void findClientsByCarBrand() {
        String brand = JOptionPane.showInputDialog(mainFrame, "Введіть марку авто для пошуку клієнтів:", "Пошук Клієнтів за Маркою Авто", JOptionPane.PLAIN_MESSAGE);
        if (brand != null && !brand.trim().isEmpty()) {
            List<Client> clients = clientService.getClientsByCarBrand(brand.trim());

            if (clients.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Клієнтів з такою маркою авто не знайдено.", "Результат Пошуку", JOptionPane.INFORMATION_MESSAGE);
            } else {
                String[] columnNames = {"СТО", "Ім'я клієнта", "Телефон", "Проблема", "ID Авто", "Марка авто"};
                Object[][] data = new Object[clients.size()][6];
                for (int i = 0; i < clients.size(); i++) {
                    Client client = clients.get(i);
                    data[i][0] = client.getServiceStationName();
                    data[i][1] = client.getFullName();
                    data[i][2] = client.getPhoneNumber();
                    data[i][3] = client.getProblem();
                    data[i][4] = (Object) client.getCar().getId();
                    data[i][5] = client.getCar().getBrand();
                }

                JTable table = new JTable(data, columnNames);
                JScrollPane scrollPane = new JScrollPane(table);
                table.setFillsViewportHeight(true);

                JOptionPane.showMessageDialog(mainFrame, scrollPane, "Результати Пошуку Клієнтів за Маркою Авто", JOptionPane.INFORMATION_MESSAGE);
            }
            backToMainMenu();
        } else if (brand != null) {
            JOptionPane.showMessageDialog(mainFrame, "Поле не повинно бути порожнім.", "Помилка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showCommonProblem() {
        String commonProblem = clientService.getCommonProblem();
        if (commonProblem == null || commonProblem.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Немає даних про проблеми клієнтів.", "Найпоширеніша Проблема", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Найпоширеніша проблема: " + commonProblem, "Найпоширеніша Проблема", JOptionPane.INFORMATION_MESSAGE);
        }
        backToMainMenu();
    }

    private void showNumberOfClients() {
        long count = clientService.getAllClients().size();
        JOptionPane.showMessageDialog(mainFrame, "Загальна кількість клієнтів: " + count, "Кількість Клієнтів", JOptionPane.INFORMATION_MESSAGE);
        backToMainMenu();
    }

    public void viewAllMasters() {
        List<Master> masters = masterService.getAllMasters();

        if (masters.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Майстрів нема.", "Перегляд Майстрів", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String[] columnNames = {"ID", "Ім'я", "СТО", "Телефон", "Спеціалізація"};
            Object[][] data = new Object[masters.size()][5];
            for (int i = 0; i < masters.size(); i++) {
                Master master = masters.get(i);
                data[i][0] = master.getId();
                data[i][1] = master.getFullName();
                data[i][2] = master.getServiceStationName();
                data[i][3] = master.getPhoneNumber();
                data[i][4] = master.getSpecialization();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            table.setFillsViewportHeight(true);

            JOptionPane.showMessageDialog(mainFrame, scrollPane, "Перегляд Всіх Майстрів", JOptionPane.INFORMATION_MESSAGE);
        }
        backToMainMenu();
    }

    public void viewAllClients() {
        List<Client> clients = clientService.getAllClients();

        if (clients.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Клієнтів нема.", "Перегляд Клієнтів", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String[] columnNames = {"ID", "Ім'я", "СТО", "Телефон", "Проблема", "Марка Авто"};
            Object[][] data = new Object[clients.size()][6];
            for (int i = 0; i < clients.size(); i++) {
                Client client = clients.get(i);
                data[i][0] = client.getId();
                data[i][1] = client.getFullName();
                data[i][2] = client.getServiceStationName();
                data[i][3] = client.getPhoneNumber();
                data[i][4] = client.getProblem();
                data[i][5] = client.getCar().getBrand();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            table.setFillsViewportHeight(true);

            JOptionPane.showMessageDialog(mainFrame, scrollPane, "Перегляд Всіх Клієнтів", JOptionPane.INFORMATION_MESSAGE);
        }
        backToMainMenu();
    }

    public void viewAllCars() {
        List<Car> cars = carService.getAllCars();

        if (cars.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Автомобілів нема.", "Перегляд Авто", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String[] columnNames = {"ID Авто", "Марка", "Рік випуску", "Ціна", "Тривалість ремонту (днів)"};
            Object[][] data = new Object[cars.size()][5];
            for (int i = 0; i < cars.size(); i++) {
                Car car = cars.get(i);
                data[i][0] = (Object) car.getId();
                data[i][1] = car.getBrand();
                data[i][2] = (Object) car.getReleaseYear();
                data[i][3] = (Object) car.getPrice();
                data[i][4] = (Object) car.getRepairDuration();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            table.setFillsViewportHeight(true);

            JOptionPane.showMessageDialog(mainFrame, scrollPane, "Перегляд Всіх Авто", JOptionPane.INFORMATION_MESSAGE);
        }
        backToMainMenu();
    }

    // ===== Методы GUI для Запчастин (Spare) =====
    private void addSpare() {
        List<Spare> spares = spareService.getAllParts();

        if (spares.stream().noneMatch(s -> s.getName().equalsIgnoreCase("Свеча"))) {
            spareService.addPart(new Spare("Свеча", 3));
        }
        if (spares.stream().noneMatch(s -> s.getName().equalsIgnoreCase("Блок живлення"))) {
            spareService.addPart(new Spare("Блок живлення", 2));
        }
        if (spares.stream().noneMatch(s -> s.getName().equalsIgnoreCase("Масло"))) {
            spareService.addPart(new Spare("Масло", 1));
        }

        spares = spareService.getAllParts();
        String[] options = new String[spares.size() + 1];
        for (int i = 0; i < spares.size(); i++) {
            Spare s = spares.get(i);
            options[i] = s.getId() + " | " + s.getName();
        }
        options[options.length - 1] = "Інше (додати вручну)";

        JComboBox<String> spareBox = new JComboBox<>(options);
        JTextField quantityField = new JTextField();
        JTextField nameField = new JTextField();
        JLabel availableLabel = new JLabel("Доступно: -");

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.add(new JLabel("Оберіть запчастину:"));
        panel.add(spareBox);
        panel.add(new JLabel("Назва (тільки якщо нова):"));
        panel.add(nameField);
        panel.add(new JLabel("Кількість (1–99):"));
        panel.add(quantityField);
        panel.add(new JLabel("Доступно:"));
        panel.add(availableLabel);

        spareBox.addActionListener(e -> {
            String selected = (String) spareBox.getSelectedItem();
            if (selected != null && !selected.equals("Інше (додати вручну)")) {
                Long spareId = Long.parseLong(selected.split(" \\| ")[0]);
                Spare spare = spareService.getPartById(spareId).orElse(null);
                if (spare != null) {
                    nameField.setText(spare.getName());
                    nameField.setEnabled(false);
                    availableLabel.setText(String.valueOf(spare.getQuantity()));
                }
            } else {
                nameField.setText("");
                nameField.setEnabled(true);
                availableLabel.setText("-");
            }
            quantityField.setText("");
        });

        int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Замовлення/додавання запчастини", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int quantity = Integer.parseInt(quantityField.getText().trim());
                if (quantity < 1 || quantity > 99) {
                    throw new IllegalArgumentException("Кількість має бути від 1 до 99");
                }

                String selected = (String) spareBox.getSelectedItem();
                if (selected.equals("Інше (додати вручну)")) {
                    String name = nameField.getText().trim();
                    if (name.isEmpty()) {
                        throw new IllegalArgumentException("Назва має бути заповнена");
                    }

                    Spare newSpare = new Spare(name, quantity);
                    spareService.addPart(newSpare);
                    JOptionPane.showMessageDialog(mainFrame, "Додано нову запчастину: " + name, "Успіх", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    Long spareId = Long.parseLong(selected.split(" \\| ")[0]);
                    Spare spare = spareService.getPartById(spareId).orElseThrow(() -> new RuntimeException("Запчастину не знайдено"));

                    if (quantity > spare.getQuantity()) {
                        throw new IllegalArgumentException("Запитана кількість перевищує доступну: " + spare.getQuantity());
                    }

                    spare.setQuantity(spare.getQuantity() - quantity);
                    spareService.updatePart(spare);
                    JOptionPane.showMessageDialog(mainFrame, "Замовлено " + quantity + " шт. запчастини: " + spare.getName(), "Успіх", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(mainFrame, "Помилка: " + e.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void listSpare() {
        System.out.println("Список запчастин:");
        spareService.getAllParts().forEach(spare ->
                System.out.println("ID: " + spare.getId() +
                        ", Назва: " + spare.getName() +
                        ", Кількість: " + spare.getQuantity()));
    }

    private void removeSpare() {
        String idStr = JOptionPane.showInputDialog(mainFrame, "Введіть ID запчастини:", "Видалити Запчастину", JOptionPane.PLAIN_MESSAGE);
        if (idStr != null) {
            try {
                long id = Long.parseLong(idStr);
                if (id <= 0) throw new NumberFormatException();

                boolean removed = spareService.removePart(id);
                if (removed) {
                    JOptionPane.showMessageDialog(mainFrame, "Запчастину видалено.", "Успіх", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Запчастину не знайдено.", "Помилка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame, "ID повинен бути позитивним числом.", "Помилка", JOptionPane.ERROR_MESSAGE);
            }
            backToMainMenu();
        }
    }



    // ===== Методы GUI для Замовлень на Запчастини (SpareOrder) =====
    private void addSpareOrder() {
        List<Repair> repairs = repairService.getAllRepairs();
        List<Spare> spares = spareService.getAllParts();

        if (repairs.isEmpty() || spares.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Немає доступних ремонтів або запчастин.", "Помилка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JComboBox<String> repairBox = new JComboBox<>(
                repairs.stream().map(r -> r.getId() + " - " + r.getDescription()).toArray(String[]::new)
        );

        JComboBox<String> spareBox = new JComboBox<>(
                spares.stream().map(s -> s.getId() + " | " + s.getName() + " (в наявності: " + s.getQuantity() + ")").toArray(String[]::new)
        );

        JTextField quantityField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Оберіть ремонт:"));
        panel.add(repairBox);
        panel.add(new JLabel("Оберіть запчастину:"));
        panel.add(spareBox);
        panel.add(new JLabel("Кількість:"));
        panel.add(quantityField);

        int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Замовлення запчастини", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Long repairId = Long.parseLong(((String) repairBox.getSelectedItem()).split(" - ")[0]);
                Long spareId = Long.parseLong(((String) spareBox.getSelectedItem()).split(" \\| ")[0]);
                int quantity = Integer.parseInt(quantityField.getText().trim());

                Spare spare = spareService.getPartById(spareId).orElse(null);
                Repair repair = repairService.getRepairById(repairId).orElse(null);

                if (spare == null || repair == null) {
                    JOptionPane.showMessageDialog(mainFrame, "Невірний вибір!", "Помилка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (quantity < 1 || quantity > spare.getQuantity()) {
                    JOptionPane.showMessageDialog(mainFrame, "Кількість має бути від 1 до " + spare.getQuantity(), "Помилка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                SpareOrder order = new SpareOrder();
                order.setRepair(repair);
                order.setSpare(spare);
                order.setQuantity(quantity);

                // Обновляем кількість на складі
                spare.setQuantity(spare.getQuantity() - quantity);
                spareService.updatePart(spare);
                spareOrderService.addPartOrder(order);

                JOptionPane.showMessageDialog(mainFrame, "Замовлення оформлено!", "Успіх", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(mainFrame, "Помилка: " + e.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void listSpareOrder() {
        System.out.println("Список замовлень на запчастини:");
        spareOrderService.getAllPartOrders().forEach(order -> {
            String partName = order.getSpare() != null ? order.getSpare().getName() : "Невідомо";
            Long repairId = order.getRepair() != null ? order.getRepair().getId() : null;

            System.out.println("ID: " + order.getId() +
                    ", Запчастина: " + partName +
                    ", Кількість: " + order.getQuantity() +
                    ", Ремонт ID: " + repairId);
        });
    }

    private void removeSpareOrder() {
        String idStr = JOptionPane.showInputDialog(mainFrame, "Введіть ID замовлення:", "Видалити Замовлення", JOptionPane.PLAIN_MESSAGE);
        if (idStr != null) {
            try {
                long id = Long.parseLong(idStr);
                if (id <= 0) throw new NumberFormatException();

                boolean removed = spareOrderService.removePartOrder(id);
                if (removed) {
                    JOptionPane.showMessageDialog(mainFrame, "Замовлення видалено.", "Успіх", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Замовлення не знайдено.", "Помилка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame, "ID повинен бути позитивним числом.", "Помилка", JOptionPane.ERROR_MESSAGE);
            }
            backToMainMenu();
        }
    }

    // ===== Методы GUI для Ремонтів (Repair) =====
    private void addRepair() {
        List<Car> cars = carService.getAllCars();
        List<Master> masters = masterService.getAllMasters();
        List<RepairType> repairTypes = repairTypeService.getAllRepairTypes();

        if (cars.isEmpty() || masters.isEmpty() || repairTypes.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame,
                    "Немає доступних авто, майстрів або типів ремонту.",
                    "Помилка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] carOptions = cars.stream()
                .map(car -> car.getId() + " | " + car.getBrand() + " (" + car.getReleaseYear() + ")")
                .toArray(String[]::new);

        String[] masterOptions = masters.stream()
                .map(master -> master.getId() + " | " + master.getFullName() + " (" + master.getSpecialization() + ")")
                .toArray(String[]::new);

        String[] typeOptions = repairTypes.stream()
                .map(type -> type.getId() + " | " + type.getName())
                .toArray(String[]::new);

        JComboBox<String> carBox = new JComboBox<>(carOptions);
        JComboBox<String> masterBox = new JComboBox<>(masterOptions);
        JComboBox<String> typeBox = new JComboBox<>(typeOptions);
        JTextField descField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.add(new JLabel("Виберіть авто:"));
        panel.add(carBox);
        panel.add(new JLabel("Виберіть майстра:"));
        panel.add(masterBox);
        panel.add(new JLabel("Тип ремонту:"));
        panel.add(typeBox);
        panel.add(new JLabel("Опис:"));
        panel.add(descField);

        int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Додати ремонт", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                Long carId = Long.parseLong(((String) carBox.getSelectedItem()).split(" \\| ")[0]);
                Long masterId = Long.parseLong(((String) masterBox.getSelectedItem()).split(" \\| ")[0]);
                Long repairTypeId = Long.parseLong(((String) typeBox.getSelectedItem()).split(" \\| ")[0]);
                String description = descField.getText().trim();

                Optional<Car> carOpt = carService.getCarById(carId);
                Optional<Master> masterOpt = masterService.getMasterById(masterId);
                Optional<RepairType> repairTypeOpt = repairTypeService.getRepairTypeById(repairTypeId);

                if (carOpt.isPresent() && masterOpt.isPresent() && repairTypeOpt.isPresent()) {
                    Repair repair = new Repair();
                    repair.setCar(carOpt.get());
                    repair.setMaster(masterOpt.get());
                    repair.setRepairType(repairTypeOpt.get());
                    repair.setStartDate(LocalDate.now());
                    repair.setEndDate(LocalDate.now().plusDays(3));
                    repair.setDescription(description);

                    repairService.addRepair(repair);

                    JOptionPane.showMessageDialog(mainFrame, "Ремонт успішно додано!", "Успіх", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Помилка при отриманні об'єктів з бази.", "Помилка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainFrame, "Помилка: " + ex.getMessage(), "Помилка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void listRepairs() {
        System.out.println("Список ремонтів:");
        repairService.getAllRepairs().forEach(repair -> {
            String carInfo = repair.getCar() != null ? "ID " + repair.getCar().getId() : "Невідомо";
            String masterInfo = repair.getMaster() != null ? repair.getMaster().getFullName() : "Невідомо";
            String typeInfo = repair.getRepairType() != null ? repair.getRepairType().getName() : "Невідомо";

            System.out.println("ID: " + repair.getId() +
                    ", Авто: " + carInfo +
                    ", Майстер: " + masterInfo +
                    ", Тип: " + typeInfo +
                    ", Початок: " + repair.getStartDate() +
                    ", Кінець: " + repair.getEndDate() +
                    ", Опис: " + repair.getDescription());

            List<SpareOrder> orders = spareOrderService.getOrdersByRepairId(repair.getId());
            if (!orders.isEmpty()) {
                System.out.println("  Замовлені запчастини:");
                orders.forEach(order -> {
                    System.out.println("    - " + order.getSpare().getName() +
                            ", Кількість: " + order.getQuantity() +
                            ", Загальна вартість: " + order.getTotalPrice() + " грн");
                });
            }
        });
    }


    private void getRepairById(Long id) {
        Optional<Repair> repair = repairService.getRepairById(id);
        repair.ifPresentOrElse(
                System.out::println,
                () -> System.out.println("Ремонт з ID " + id + " не знайдено")
        );
    }

    private void removeRepair() {
        String idStr = JOptionPane.showInputDialog(mainFrame, "Введіть ID ремонту для видалення:", "Видалити Ремонт", JOptionPane.PLAIN_MESSAGE);
        if (idStr != null) {
            try {
                long id = Long.parseLong(idStr);
                if (id <= 0) throw new NumberFormatException();

                boolean removed = repairService.removeRepair(id);
                if (removed) {
                    JOptionPane.showMessageDialog(mainFrame, "Ремонт видалено.", "Успіх", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Ремонт не знайдено.", "Помилка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame, "ID повинен бути позитивним числом.", "Помилка", JOptionPane.ERROR_MESSAGE);
            }
            backToMainMenu();
        }
    }

    // ===== Методы GUI для Типів ремонту (RepairType) =====
    private void addRepairType() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField nameField = new JTextField();
        JTextField descField = new JTextField();

        panel.add(new JLabel("Назва типу ремонту:"));
        panel.add(nameField);
        panel.add(new JLabel("Опис:"));
        panel.add(descField);

        int result = JOptionPane.showConfirmDialog(mainFrame, panel, "Додати тип ремонту", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText().trim();
            String desc = descField.getText().trim();

            if (name.isEmpty() || desc.isEmpty()) {
                JOptionPane.showMessageDialog(mainFrame, "Всі поля обов'язкові!", "Помилка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            RepairType newType = new RepairType();
            newType.setName(name);
            newType.setDescription(desc);

            repairTypeService.addRepairType(newType);

            JOptionPane.showMessageDialog(mainFrame, "Тип ремонту додано!", "Успіх", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    private void listRepairTypes() {
        System.out.println("Список типів ремонту:");
        repairTypeService.getAllRepairTypes().forEach(rt -> {
            System.out.println("ID: " + rt.getId() +
                    ", Назва: " + rt.getName() +
                    ", Опис: " + rt.getDescription());
        });
    }

    private void getRepairTypeById(Long id) {
        Optional<RepairType> repairType = repairTypeService.getRepairTypeById(id);
        repairType.ifPresentOrElse(
                rt -> System.out.println("ID: " + rt.getId() +
                        ", Назва: " + rt.getName() +
                        ", Опис: " + rt.getDescription()),
                () -> System.out.println("Тип ремонту з ID " + id + " не знайдено")
        );
    }

    private void removeRepairType() {
        String idStr = JOptionPane.showInputDialog(mainFrame, "Введіть ID типу ремонту:", "Видалити Тип Ремонту", JOptionPane.PLAIN_MESSAGE);
        if (idStr != null) {
            try {
                long id = Long.parseLong(idStr);
                if (id <= 0) throw new NumberFormatException();

                boolean removed = repairTypeService.removeRepairType(id);
                if (removed) {
                    JOptionPane.showMessageDialog(mainFrame, "Тип ремонту видалено.", "Успіх", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(mainFrame, "Тип ремонту не знайдено.", "Помилка", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(mainFrame, "ID повинен бути позитивним числом.", "Помилка", JOptionPane.ERROR_MESSAGE);
            }
            backToMainMenu();
        }
    }

}