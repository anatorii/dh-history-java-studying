import org.jdatepicker.impl.DateComponentFormatter;
import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

public class DayHistory extends JFrame {
    static int width = 800;
    static int height = 600;
    JDatePickerImpl datePicker;
    JTextArea textArea;

    public DayHistory () {
        super("DayHistory");

        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(DayHistory.width, DayHistory.height);
        this.setLocation(d.width / 2 - DayHistory.width / 2, d.height / 2 - DayHistory.height / 2);
        this.getContentPane().setBackground(Color.orange);
        this.setLocationRelativeTo(null);
        this.setLayout(new BorderLayout());

        JPanel panelUp = new JPanel(new GridLayout());
        this.add(panelUp, BorderLayout.NORTH);

        JPanel panelLeft = new JPanel(new FlowLayout());
        panelUp.add(panelLeft);

        JPanel panelDown = new JPanel(new GridBagLayout());
        this.add(panelDown);

        Properties properties = new Properties();
        properties.setProperty("text.today", "Сегодня");
        UtilDateModel model = new UtilDateModel();
        JDatePanelImpl datePanel = new JDatePanelImpl(model, properties);
        datePicker = new JDatePickerImpl(datePanel, new DateComponentFormatter());
        datePicker.getModel().setSelected(true);

        JButton button = new JButton("Получить факт");
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    buttonActionPerformed(e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        panelLeft.add(datePicker);
        panelLeft.add(button);

        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Areal", Font.PLAIN, 20));
        textArea.setMargin(new Insets(10,10,10,10));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 0);
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weighty = 1;
        constraints.weightx = 1;
        panelDown.add(scrollPane, constraints);

        setVisible(true);
    }

    void buttonActionPerformed(ActionEvent e) throws IOException {
        textArea.setText(
                getResponse(
                        datePicker.getModel().getDay(),
                        datePicker.getModel().getMonth() + 1
                ).orElse("No response")
        );
    }

    Optional<String> getResponse(int day, int month) throws IOException {
        String uri = "http://numbersapi.com";
        URL url = new URL(uri + "/" + month + "/" + day + "/date");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(3000);
        connection.setRequestMethod("GET");

        InputStream responseStream = connection.getInputStream();
        InputStreamReader reader = new InputStreamReader(responseStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(reader);
        return Optional.of(bufferedReader.lines().collect(Collectors.joining(System.lineSeparator())));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DayHistory frame = new DayHistory();
            }
        });
    }
}
