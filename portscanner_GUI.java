import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

class PortScannerGUI {
    private JFrame frame;
    private JTextField targetHostTextField;
    private JTextField startPortTextField;
    private JTextField endPortTextField;
    private JTextArea resultTextArea;

    public PortScannerGUI() {
        frame = new JFrame("Port Scanner");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel();
        JLabel targetHostLabel = new JLabel("Target Host:");
        JLabel startPortLabel = new JLabel("Starting Port:");
        JLabel endPortLabel = new JLabel("Ending Port:");
        targetHostTextField = new JTextField(20);
        startPortTextField = new JTextField(5);
        endPortTextField = new JTextField(5);
        inputPanel.add(targetHostLabel);
        inputPanel.add(targetHostTextField);
        inputPanel.add(startPortLabel);
        inputPanel.add(startPortTextField);
        inputPanel.add(endPortLabel);
        inputPanel.add(endPortTextField);

        JPanel buttonPanel = new JPanel();
        JButton scanButton = new JButton("Scan Ports");
        scanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String targetHost = targetHostTextField.getText();
                int startPort = Integer.parseInt(startPortTextField.getText());
                int endPort = Integer.parseInt(endPortTextField.getText());

                scanPorts(targetHost, startPort, endPort);
            }
        });
        buttonPanel.add(scanButton);

        JPanel resultPanel = new JPanel(new BorderLayout());
        JLabel resultLabel = new JLabel("Scan Results:");
        resultPanel.add(resultLabel, BorderLayout.NORTH);
        resultTextArea = new JTextArea(10, 30);
        resultTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultTextArea);
        resultPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel outputButtonPanel = new JPanel();
        JButton clearButton = new JButton("Clear Output");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resultTextArea.setText("");
            }
        });
        outputButtonPanel.add(clearButton);

        JButton exportButton = new JButton("Export Results");
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportResults();
            }
        });
        outputButtonPanel.add(exportButton);

        resultPanel.add(outputButtonPanel, BorderLayout.SOUTH);

        mainPanel.add(inputPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        mainPanel.add(resultPanel, BorderLayout.NORTH);

        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

    public void scanPorts(String targetHost, int startPort, int endPort) {
        try {
            String targetIp = java.net.InetAddress.getByName(targetHost).getHostAddress();

            StringBuilder result = new StringBuilder();

            for (int port = startPort; port <= endPort; port++) {
                try (Socket socket = new Socket(targetIp, port)) {
                    result.append("Port ").append(port).append(": Open\n");
                } catch (IOException e) {
                    result.append("Port ").append(port).append(": Closed\n");
                }
            }

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    resultTextArea.setText(result.toString());
                }
            });
        } catch (java.net.UnknownHostException e) {
            resultTextArea.setText("Hostname could not be resolved.");
        }
    }

    public void exportResults() {
        String targetHost = targetHostTextField.getText();
        int startPort = Integer.parseInt(startPortTextField.getText());
        int endPort = Integer.parseInt(endPortTextField.getText());

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = fileChooser.showSaveDialog(frame);

        if (result == JFileChooser.APPROVE_OPTION) {
            String selectedDirectory = fileChooser.getSelectedFile().getAbsolutePath();
            String fileName = selectedDirectory + "/" + targetHost + "_scan_results.txt";

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
                writer.write("Target Host: " + targetHost);
                writer.newLine();

                for (int port = startPort; port <= endPort; port++) {
                    try (Socket socket = new Socket(targetHost, port)) {
                        writer.write("Port " + port + ": Open");
                        writer.newLine();
                    } catch (IOException e) {
                        writer.write("Port " + port + ": Closed");
                        writer.newLine();
                    }
                }

                writer.flush();
                writer.close();

                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        resultTextArea.append("\nScan results exported to " + fileName);
                    }
                });
            } catch (IOException e) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        resultTextArea.append("\nError exporting results: " + e.getMessage());
                    }
                });
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PortScannerGUI();
            }
        });
    }
}
