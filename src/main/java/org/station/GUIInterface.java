package org.station;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.station.entity.*;
import org.station.service.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GUIInterface {
    private final CarService carService;
    private final ClientService clientService;
    private final MasterService masterService;
    private final SpareService spareService;
    private final RepairService repairService;
    private final RepairTypeService repairTypeService;
    private final ServiceStationService serviceStationService;
    private final CarBrandService carBrandService;
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

        JPanel menuPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));

        JLabel stationLabel = new JLabel("Поточна станція: " + currentStation.getName(), JLabel.CENTER);
        stationLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JButton masterButton = new JButton("Майстер");
        JButton repairButton = new JButton("Ремонт");
        JButton continueButton = new JButton("Далі");
        JButton backButton = new JButton("Повернутися до вибору станції");

        masterButton.addActionListener(e -> showMasterMenu());
        repairButton.addActionListener(e -> showRepairMenu());
        continueButton.addActionListener(e -> showFullMainMenu());
        backButton.addActionListener(e -> showStationSelectionScreen());

        menuPanel.add(masterButton);
        menuPanel.add(repairButton);
        menuPanel.add(continueButton);
        menuPanel.add(backButton);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(stationLabel, BorderLayout.NORTH);
        mainPanel.add(menuPanel, BorderLayout.CENTER);

        mainFrame.getContentPane().removeAll();
        mainFrame.add(mainPanel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }

    private void showMasterMenu() {
        JPanel masterPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        masterPanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));

        JButton addMasterButton = new JButton("Додати майстра");
        JButton viewMastersButton = new JButton("Переглянути майстрів");
        JButton deleteMasterButton = new JButton("Видалити майстра");
        JButton selectRepairButton = new JButton("Обрати ремонт");
        JButton backButton = new JButton("Назад");

        addMasterButton.addActionListener(e -> addMaster());
        viewMastersButton.addActionListener(e -> viewAllMasters());
        deleteMasterButton.addActionListener(e -> removeMaster());
        selectRepairButton.addActionListener(e -> handleRepairsInterface());
        backButton.addActionListener(e -> showMainMenu());

        masterPanel.add(addMasterButton);
        masterPanel.add(viewMastersButton);
        masterPanel.add(deleteMasterButton);
        masterPanel.add(selectRepairButton);
        masterPanel.add(backButton);

        switchPanel(masterPanel);
    }

    private void showRepairMenu() {
        JPanel repairPanel = new JPanel(new GridLayout(6, 1, 10, 10));
        repairPanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));

        JButton viewRepairsButton = new JButton("Переглянути ремонти");
        //JButton addRepairButton = new JButton("Додати ремонт");
        JButton historyButton = new JButton("Список ремонтів");
        JButton deleteRepairButton = new JButton("Видалити ремонт");
        JButton requestRepairButton = new JButton("Створити заявку на ремонт");
        JButton viewPartsButton = new JButton("Переглянути запчастини");
        JButton backButton = new JButton("Назад");

        viewRepairsButton.addActionListener(e -> listRepairs());
        //addRepairButton.addActionListener(e -> addRepair());
        historyButton.addActionListener(e -> showFullRepairHistory());
        deleteRepairButton.addActionListener(e -> removeRepair());
        requestRepairButton.addActionListener(e -> addRepair()); // можно создать отдельный метод
        viewPartsButton.addActionListener(e -> listSpare());
        backButton.addActionListener(e -> showMainMenu());

        repairPanel.add(viewRepairsButton);
        //repairPanel.add(addRepairButton);
        repairPanel.add(historyButton);
        repairPanel.add(deleteRepairButton);
        repairPanel.add(requestRepairButton);
        repairPanel.add(viewPartsButton);
        repairPanel.add(backButton);

        switchPanel(repairPanel);
    }

    private void showFullMainMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10));
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

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(stationLabel, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);

        panel.add(addButton);
        panel.add(removeButton);
        panel.add(viewButton);
        panel.add(otherOperationsButton);
        panel.add(backButton);
        panel.add(exitButton);

        mainFrame.getContentPane().removeAll();
        mainFrame.add(mainPanel);
        mainFrame.revalidate();
        mainFrame.repaint();
    }


    private void showAddMenu() {
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new GridLayout(9, 1, 10, 10)); // Увеличено количество строк на 1
        addPanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));

        //JButton addMasterButton = new JButton("Додати майстра");
        JButton addPartButton = new JButton("Замовити запчастину");
        //JButton addRepairButton = new JButton("Додати ремонт");
        JButton addRepairTypeButton = new JButton("Додати тип ремонту");
        JButton backButton = new JButton("Повернутися до головного меню");


        //addMasterButton.addActionListener(e -> addMaster());
        addPartButton.addActionListener(e -> addSpare());
        //addRepairButton.addActionListener(e -> addRepair());
        addRepairTypeButton.addActionListener(e -> addRepairType());
        backButton.addActionListener(e -> showMainMenu());

        //addPanel.add(addMasterButton);
        addPanel.add(addPartButton);
        //addPanel.add(addRepairButton);
        addPanel.add(addRepairTypeButton);
        addPanel.add(backButton);

        switchPanel(addPanel);
    }

    private void showRemoveMenu() {
        JPanel removePanel = new JPanel();
        removePanel.setLayout(new GridLayout(10, 1, 10, 10)); // Оставляем 10 строк
        removePanel.setBorder(BorderFactory.createEmptyBorder(50, 150, 50, 150));

        //JButton removeMasterButton = new JButton("Видалити майстра");
        JButton removeClientButton = new JButton("Видалити клієнта");
        JButton removeCarButton = new JButton("Видалити авто");
        JButton removePartButton = new JButton("Видалити запчастину");
        //JButton removeRepairButton = new JButton("Видалити ремонт");
        JButton removeRepairTypeButton = new JButton("Видалити тип ремонту");
        JButton removeUserButton = new JButton("Видалити користувача");
        JButton removePartOrderButton = new JButton("Видалити замовлення запчастини");

        JButton backButton = new JButton("Повернутися до головного меню");

        //removeMasterButton.addActionListener(e -> removeMaster());
        removeClientButton.addActionListener(e -> removeClient());
        removeCarButton.addActionListener(e -> removeCar());
        removePartButton.addActionListener(e -> removeSpare());
        //removeRepairButton.addActionListener(e -> removeRepair());
        removeRepairTypeButton.addActionListener(e -> removeRepairType());
        backButton.addActionListener(e -> showMainMenu());

        //removePanel.add(removeMasterButton);
        removePanel.add(removeClientButton);
        removePanel.add(removeCarButton);
        removePanel.add(removePartButton);
        //removePanel.add(removeRepairButton);
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

        //JButton viewMastersButton = new JButton("Переглянути майстрів");
        JButton viewClientsButton = new JButton("Переглянути клієнтів");
        JButton viewCarsButton = new JButton("Переглянути авто");
        JButton viewPartsButton = new JButton("Переглянути запчастини");
        //JButton manageRepairsButton = new JButton("Обробити ремонти");
        JButton viewRepairsButton = new JButton("Переглянути ремонти");
        JButton viewRepairTypesButton = new JButton("Переглянути типи ремонту");
        JButton backButton = new JButton("Повернутися до головного меню");

        //viewMastersButton.addActionListener(e -> viewAllMasters());
        viewClientsButton.addActionListener(e -> viewAllClients());
        viewCarsButton.addActionListener(e -> viewAllCars());
        viewPartsButton.addActionListener(e -> listSpare());
        //manageRepairsButton.addActionListener(e -> handleRepairsInterface());
        viewRepairsButton.addActionListener(e -> listRepairs());
        viewRepairTypesButton.addActionListener(e -> listRepairTypes());
        backButton.addActionListener(e -> showMainMenu()); // Изменено на showMainMenu

        //viewPanel.add(viewMastersButton);
        viewPanel.add(viewClientsButton);
        viewPanel.add(viewCarsButton);
        viewPanel.add(viewPartsButton);
        //viewPanel.add(manageRepairsButton);
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
        backButton.addActionListener(e -> showMainMenu());

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

            Master master = new Master(currentStation.getName(), fullName, phoneNumber, address, specialization);
            masterService.addMaster(master);
            JOptionPane.showMessageDialog(mainFrame, "Майстра додано!", "Успіх", JOptionPane.INFORMATION_MESSAGE);
            showMainMenu();
        }
    }

    private void handleRepairsInterface() {
        List<Repair> pendingRepairs = repairService.getAllRepairs().stream()
                .filter(r -> !"COMPLETED".equalsIgnoreCase(r.getStatus()))
                .collect(Collectors.toList());

        if (pendingRepairs.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Немає активних ремонтів для обробки.", "Ремонти", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] repairOptions = pendingRepairs.stream()
                .map(r -> "ID: " + r.getId() + " | " +
                        "Клієнт: " + (r.getClient() != null ? r.getClient().getFullName() : "Невідомо") +
                        ", Машина: " + (r.getCar() != null ? r.getCar().getBrand() : "Невідомо") +
                        ", Тип: " + (r.getRepairType() != null ? r.getRepairType().getName() : "Невідомо") +
                        ", Майстер: " + (r.getMaster() != null ? r.getMaster().getFullName() : "Невідомо") +
                        ", Статус: " + r.getStatus())
                .toArray(String[]::new);

        JComboBox<String> repairBox = new JComboBox<>(repairOptions);
        Object[] options = {"Прийняти", "Завершити", "Відхилити", "Вихід"};
        int action = JOptionPane.showOptionDialog(mainFrame, repairBox, "Обробка ремонтів", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (action >= 0 && action <= 2) {
            Long repairId = pendingRepairs.get(repairBox.getSelectedIndex()).getId();
            switch (action) {
                case 0 -> repairService.acceptRepair(repairId, LocalDateTime.now());
                case 1 -> repairService.completeRepair(repairId, LocalDateTime.now());
                case 2 -> repairService.rejectRepair(repairId);
            }
            JOptionPane.showMessageDialog(mainFrame, "Операція виконана!", "Успіх", JOptionPane.INFORMATION_MESSAGE);
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
        JLabel availableLabel = new JLabel("Доступно у наявності: -");

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
        List<Spare> spares = spareService.getAllParts();

        if (spares.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Запчастин не знайдено.", "Перегляд Запчастин", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String[] columnNames = {"ID", "Назва", "Кількість"};
            Object[][] data = new Object[spares.size()][3];

            for (int i = 0; i < spares.size(); i++) {
                Spare spare = spares.get(i);
                data[i][0] = spare.getId();
                data[i][1] = spare.getName();
                data[i][2] = spare.getQuantity();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            table.setFillsViewportHeight(true);

            JOptionPane.showMessageDialog(mainFrame, scrollPane, "Перегляд Запчастин", JOptionPane.INFORMATION_MESSAGE);
        }

        backToMainMenu();
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

    // ===== Методы GUI для Ремонтів (Repair) =====
    private void addRepair() {
        JTextField fullNameField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField problemField = new JTextField();

        JPanel clientPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        clientPanel.add(new JLabel("Ім'я клієнта:"));
        clientPanel.add(fullNameField);
        clientPanel.add(new JLabel("Телефон:"));
        clientPanel.add(phoneField);
        clientPanel.add(new JLabel("Проблема:"));
        clientPanel.add(problemField);

        int clientResult = JOptionPane.showConfirmDialog(mainFrame, clientPanel, "Крок 1: Дані клієнта", JOptionPane.OK_CANCEL_OPTION);
        if (clientResult != JOptionPane.OK_OPTION) return;

        List<CarBrand> brands = carBrandService.getAllBrands();
        String[] brandNames = brands.stream().map(CarBrand::getName).toArray(String[]::new);
        JComboBox<String> brandBox = new JComboBox<>(brandNames);
        JTextField yearField = new JTextField();
        JTextField durationField = new JTextField();

        JPanel carPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        carPanel.add(new JLabel("Марка авто:"));
        carPanel.add(brandBox);
        carPanel.add(new JLabel("Рік випуску:"));
        carPanel.add(yearField);
        carPanel.add(new JLabel("Тривалість ремонту:"));
        carPanel.add(durationField);

        int carResult = JOptionPane.showConfirmDialog(mainFrame, carPanel, "Крок 2: Дані авто", JOptionPane.OK_CANCEL_OPTION);
        if (carResult != JOptionPane.OK_OPTION) return;

        List<Master> masters = masterService.getAllMasters();
        List<RepairType> types = repairTypeService.getAllRepairTypes();
        String[] masterOptions = masters.stream().map(m -> m.getId() + " - " + m.getFullName()).toArray(String[]::new);
        String[] typeOptions = types.stream().map(t -> t.getId() + " - " + t.getName()).toArray(String[]::new);

        JComboBox<String> masterBox = new JComboBox<>(masterOptions);
        JComboBox<String> typeBox = new JComboBox<>(typeOptions);
        JTextField descField = new JTextField();

        JPanel confirmPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        confirmPanel.add(new JLabel("Майстер:"));
        confirmPanel.add(masterBox);
        confirmPanel.add(new JLabel("Тип ремонту:"));
        confirmPanel.add(typeBox);
        confirmPanel.add(new JLabel("Опис:"));
        confirmPanel.add(descField);

        int confirmResult = JOptionPane.showConfirmDialog(mainFrame, confirmPanel, "Крок 3: Підтвердження", JOptionPane.OK_CANCEL_OPTION);
        if (confirmResult != JOptionPane.OK_OPTION) return;

        Car car = new Car((String) brandBox.getSelectedItem(),
                Integer.parseInt(yearField.getText().trim()),
                0.0,
                Integer.parseInt(durationField.getText().trim()));
        carService.addCar(car);

        Client client = new Client(problemField.getText().trim(),
                phoneField.getText().trim(),
                currentStation.getName(),
                fullNameField.getText().trim(),
                car);
        clientService.addClient(client);

        Long masterId = Long.parseLong(((String) masterBox.getSelectedItem()).split(" - ")[0]);
        Long typeId = Long.parseLong(((String) typeBox.getSelectedItem()).split(" - ")[0]);

        Repair repair = new Repair();
        repair.setCar(car);
        repair.setMaster(masterService.getMasterById(masterId).orElseThrow());
        repair.setRepairType(repairTypeService.getRepairTypeById(typeId).orElseThrow());
        repair.setStartDate(LocalDate.now());
        repair.setEndDate(LocalDate.now().plusDays(3));
        repair.setDescription(descField.getText().trim());

        repairService.addRepair(repair);

        List<Spare> availableSpares = spareService.getAllParts();
        boolean noAvailableSpare = availableSpares.stream().noneMatch(s -> s.getQuantity() > 0);

        if (noAvailableSpare) {
            int choice = JOptionPane.showOptionDialog(mainFrame,
                    "Немає доступних запчастин.\nБажаєте скасувати ремонт чи додати запчастини?",
                    "Увага",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    new Object[]{"Скасувати ремонт", "Додати запчастини"},
                    "Додати запчастини");

            if (choice == JOptionPane.YES_OPTION) {
                repairService.removeRepair(repair.getId());
                carService.removeCar(car.getId());
                clientService.removeClient(client.getId());
                JOptionPane.showMessageDialog(mainFrame, "Ремонт скасовано.", "Інформація", JOptionPane.INFORMATION_MESSAGE);
            } else {
                addSpare();
            }
        } else {
            JOptionPane.showMessageDialog(mainFrame, "Ремонт успішно створено!", "Успіх", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showFullRepairHistory() {
        List<Repair> repairs = repairService.getAllRepairs();

        if (repairs.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Ремонтів не знайдено.", "Список Ремонтів", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String[] columnNames = {
                "ID", "Майстер", "Клієнт", "Автомобіль", "Тип", "Початок", "Кінець", "Статус", "Опис"
        };

        Object[][] data = new Object[repairs.size()][columnNames.length];

        for (int i = 0; i < repairs.size(); i++) {
            Repair repair = repairs.get(i);
            Master master = repair.getMaster();
            Car car = repair.getCar();
            Client client = clientService.getClientByCarId(car.getId()).orElse(null);

            data[i][0] = repair.getId();
            data[i][1] = master != null ? master.getFullName() : "Невідомо";
            data[i][2] = client != null ? client.getFullName() : "Невідомо";
            data[i][3] = car != null ? car.getBrand() + " (" + car.getReleaseYear() + ")" : "Невідомо";
            data[i][4] = repair.getRepairType() != null ? repair.getRepairType().getName() : "Невідомо";
            data[i][5] = repair.getStartDate() != null ? repair.getStartDate().toString() : "-";
            data[i][6] = repair.getEndDate() != null ? repair.getEndDate().toString() : "-";
            data[i][7] = repair.getStatus();
            data[i][8] = repair.getDescription();
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);

        JOptionPane.showMessageDialog(mainFrame, scrollPane, "Історія ремонтів", JOptionPane.INFORMATION_MESSAGE);
    }


    private void listRepairs() {
        List<Repair> repairs = repairService.getAllRepairs();

        if (repairs.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Ремонтів не знайдено.", "Перегляд Ремонтів", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String[] columnNames = {"ID", "Авто", "Майстер", "Тип", "Початок", "Кінець", "Опис", "Запчастини"};
            Object[][] data = new Object[repairs.size()][8];

            for (int i = 0; i < repairs.size(); i++) {
                Repair repair = repairs.get(i);

                String carInfo = repair.getCar() != null ? "ID " + repair.getCar().getId() : "Невідомо";
                String masterInfo = repair.getMaster() != null ? repair.getMaster().getFullName() : "Невідомо";
                String typeInfo = repair.getRepairType() != null ? repair.getRepairType().getName() : "Невідомо";
                String startDate = repair.getStartDate() != null ? repair.getStartDate().toString() : "-";
                String endDate = repair.getEndDate() != null ? repair.getEndDate().toString() : "-";
                String description = repair.getDescription() != null ? repair.getDescription() : "-";

                StringBuilder spareList = new StringBuilder();
                if (repair.getRepairType() != null && repair.getRepairType().getSpares() != null) {
                    repair.getRepairType().getSpares().forEach(spare -> {
                        spareList.append(spare.getName()).append(" (x").append(spare.getQuantity()).append("), ");
                    });
                    if (spareList.length() > 0) {
                        spareList.setLength(spareList.length() - 2);
                    } else {
                        spareList.append("Немає");
                    }
                } else {
                    spareList.append("Немає");
                }

                data[i][0] = repair.getId();
                data[i][1] = carInfo;
                data[i][2] = masterInfo;
                data[i][3] = typeInfo;
                data[i][4] = startDate;
                data[i][5] = endDate;
                data[i][6] = description;
                data[i][7] = spareList.toString();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            table.setFillsViewportHeight(true);

            JOptionPane.showMessageDialog(mainFrame, scrollPane, "Перегляд Ремонтів", JOptionPane.INFORMATION_MESSAGE);
        }

        backToMainMenu();
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
        List<RepairType> repairTypes = repairTypeService.getAllRepairTypes();

        if (repairTypes.isEmpty()) {
            JOptionPane.showMessageDialog(mainFrame, "Типів ремонту не знайдено.", "Перегляд Типів Ремонту", JOptionPane.INFORMATION_MESSAGE);
        } else {
            String[] columnNames = {"ID", "Назва", "Опис"};
            Object[][] data = new Object[repairTypes.size()][3];

            for (int i = 0; i < repairTypes.size(); i++) {
                RepairType type = repairTypes.get(i);
                data[i][0] = type.getId();
                data[i][1] = type.getName();
                data[i][2] = type.getDescription();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            table.setFillsViewportHeight(true);

            JOptionPane.showMessageDialog(mainFrame, scrollPane, "Перегляд Типів Ремонту", JOptionPane.INFORMATION_MESSAGE);
        }

        backToMainMenu();
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