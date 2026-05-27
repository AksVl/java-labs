package gui;

import threadpool.Task;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class FactoryGUI extends JFrame {
    private JSlider body_supplier_speed;
    private JSlider motor_supplier_speed;
    private JSlider accessory_supplier_speed;
    private JSlider dealer_speed;

    private JLabel body_speed_label;
    private JLabel motor_speed_label;
    private JLabel accessory_speed_label;
    private JLabel dealer_speed_label;

    private JLabel body_storage_label;
    private JLabel motor_storage_label;
    private JLabel accessory_storage_label;
    private JLabel car_storage_label;

    private JLabel sold_cars_label;

    private final int body_storage_capacity;
    private final int motor_storage_capacity;
    private final int accessory_storage_capacity;
    private final int car_storage_capacity;

    // Экземпляры задач
    private final Task supplyBodies;
    private final Task supplyMotos;
    private final Task supplyAccessories;
    private final Task orderSell;

    public FactoryGUI(Task supplyBodies, Task supplyMotos, Task supplyAccessories, Task orderSell,
                      int body_storage_capacity, int motor_storage_capacity,
                      int accessory_storage_capacity, int car_storage_capacity,
                      int body_supplier_delay, int motor_supplier_delay,
                      int accessory_supplier_delay, int dealer_delay) {

        this.body_storage_capacity = body_storage_capacity;
        this.motor_storage_capacity = motor_storage_capacity;
        this.accessory_storage_capacity = accessory_storage_capacity;
        this.car_storage_capacity = car_storage_capacity;

        this.supplyBodies = supplyBodies;
        this.supplyMotos = supplyMotos;
        this.supplyAccessories = supplyAccessories;
        this.orderSell = orderSell;

        setTitle("Factory Control Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setMinimumSize(new Dimension(800, 600));
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(240, 240, 245));

        // Панель статистики
        JPanel stats_panel = createStatsPanel();

        // Панель управления
        JPanel control_panel = createControlPanel(body_supplier_delay, motor_supplier_delay,
                accessory_supplier_delay, dealer_delay);

        // Компоненты с отступами
        add(stats_panel, BorderLayout.CENTER);
        add(control_panel, BorderLayout.SOUTH);

        // Логотип в верхнюю часть
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 70, 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        add(headerPanel, BorderLayout.NORTH);
        // Убедитесь, что путь к иконке корректен
        //setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/resources/icon.jpg")));
        // Центрирование окна
        setLocationRelativeTo(null);
    }

    private JPanel createStatsPanel() {
        JPanel stats_panel = new JPanel();
        stats_panel.setLayout(new GridLayout(6, 1, 10, 10));
        stats_panel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(
                        new LineBorder(new Color(70, 70, 70), 2, true),
                        "stats",
                        TitledBorder.CENTER, TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 16),
                        new Color(70, 70, 70)
                ),
                new EmptyBorder(10, 15, 15, 15)
        ));
        stats_panel.setBackground(Color.WHITE);

        // Стилизация меток
        Font labelFont = new Font("Arial", Font.PLAIN, 14);
        Color labelColor = new Color(60, 60, 60);

        body_storage_label = createStyledLabel("Body storage: 0/" + body_storage_capacity, labelFont, labelColor);
        motor_storage_label = createStyledLabel("Motor storage: 0/" + motor_storage_capacity, labelFont, labelColor);
        accessory_storage_label = createStyledLabel("Accessory storage: 0/" + accessory_storage_capacity, labelFont, labelColor);
        car_storage_label = createStyledLabel("Car storage: 0/" + car_storage_capacity, labelFont, labelColor);

        sold_cars_label = createStyledLabel("Sold cars: 0", labelFont, labelColor);

        stats_panel.add(body_storage_label);
        stats_panel.add(motor_storage_label);
        stats_panel.add(accessory_storage_label);
        stats_panel.add(car_storage_label);
        stats_panel.add(sold_cars_label);

        return stats_panel;
    }

    private JPanel createControlPanel(int body_delay, int motor_delay, int accessory_delay, int dealer_delay) {
        JPanel control_panel = new JPanel();
        control_panel.setLayout(new GridLayout(4, 2, 15, 15));
        control_panel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(
                        new LineBorder(new Color(70, 70, 70), 2, true),
                        "controls",
                        TitledBorder.CENTER, TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 16),
                        new Color(70, 70, 70)
                ),
                new EmptyBorder(15, 15, 15, 15)
        ));
        control_panel.setBackground(Color.WHITE);

        // Создание слайдеров
        body_speed_label = createStyledLabel("Body supplier delay (ms): " + body_delay,
                new Font("Arial", Font.BOLD, 13),
                new Color(70, 70, 70));
        body_supplier_speed = createSlider(100, 10000, body_delay, body_speed_label);

        motor_speed_label = createStyledLabel("Motor supplier delay (ms): " + motor_delay,
                new Font("Arial", Font.BOLD, 13),
                new Color(70, 70, 70));
        motor_supplier_speed = createSlider(100, 10000, motor_delay, motor_speed_label);

        accessory_speed_label = createStyledLabel("Accessory supplier delay (ms): " + accessory_delay,
                new Font("Arial", Font.BOLD, 13),
                new Color(70, 70, 70));
        accessory_supplier_speed = createSlider(100, 10000, accessory_delay, accessory_speed_label);

        dealer_speed_label = createStyledLabel("Dealer delay (ms): " + dealer_delay,
                new Font("Arial", Font.BOLD, 13),
                new Color(70, 70, 70));
        dealer_speed = createSlider(100, 10000, dealer_delay, dealer_speed_label);

        // Добавление компонентов
        control_panel.add(body_speed_label);
        control_panel.add(body_supplier_speed);
        control_panel.add(motor_speed_label);
        control_panel.add(motor_supplier_speed);
        control_panel.add(accessory_speed_label);
        control_panel.add(accessory_supplier_speed);
        control_panel.add(dealer_speed_label);
        control_panel.add(dealer_speed);

        return control_panel;
    }

    private JLabel createStyledLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text, JLabel.LEFT);
        label.setFont(font);
        label.setForeground(color);
        label.setBorder(new EmptyBorder(5, 10, 5, 10));
        return label;
    }

    private JSlider createSlider(int min, int max, int value, JLabel label) {
        JSlider slider = new JSlider(min, max, value);

        slider.setMajorTickSpacing((max - min) / 10);
        slider.setMinorTickSpacing((max - min) / 20);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setBackground(Color.WHITE);

        slider.setUI(new javax.swing.plaf.basic.BasicSliderUI(slider) {
            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Rectangle trackBounds = trackRect;
                int trackHeight = 8;

                g2d.setColor(new Color(220, 220, 220));
                g2d.fillRoundRect(trackBounds.x, trackBounds.y + (trackBounds.height - trackHeight)/2,
                        trackBounds.width, trackHeight, trackHeight, trackHeight);

                int fillWidth = thumbRect.x + thumbRect.width/2 - trackBounds.x;
                g2d.setColor(new Color(250, 170, 0));
                g2d.fillRoundRect(trackBounds.x, trackBounds.y + (trackBounds.height - trackHeight)/2,
                        fillWidth, trackHeight, trackHeight, trackHeight);
            }

            @Override
            protected Dimension getThumbSize() {
                return new Dimension(16, 16);
            }

            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(70, 70, 70));
                g2d.fillOval(thumbRect.x, thumbRect.y, thumbRect.width, thumbRect.height);

                g2d.setColor(Color.WHITE);
                g2d.fillOval(thumbRect.x + 3, thumbRect.y + 3, thumbRect.width - 6, thumbRect.height - 6);
            }
        });

        slider.addChangeListener(e -> {
            int slider_value = slider.getValue();
            label.setText(label.getText().split(":")[0] + ": " + slider_value);

            // Обновляем параметр задержки для соответствующей задачи
            if (e.getSource() == body_supplier_speed) {
                supplyBodies.setParameters(slider_value);
            } else if (e.getSource() == motor_supplier_speed) {
                supplyMotos.setParameters(slider_value);
            } else if (e.getSource() == accessory_supplier_speed) {
                supplyAccessories.setParameters(slider_value);
            } else if (e.getSource() == dealer_speed) {
                orderSell.setParameters(slider_value);
            }
        });

        return slider;
    }

    public void updateStats(int body_storage, int motor_storage, int accessory_storage,
                            int car_storage, int sold_cars) {
        SwingUtilities.invokeLater(() -> {
            body_storage_label.setText("Body storage: " + body_storage + "/" + body_storage_capacity);
            motor_storage_label.setText("Motor storage: " + motor_storage + "/" + motor_storage_capacity);
            accessory_storage_label.setText("Accessory storage: " + accessory_storage + "/" + accessory_storage_capacity);
            car_storage_label.setText("Car storage: " + car_storage + "/" + car_storage_capacity);
            sold_cars_label.setText("Sold cars: " + sold_cars);
        });
    }
}