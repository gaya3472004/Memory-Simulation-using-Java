import java.awt.*;
import javax.swing.*;

public class MemoryGUI {
    private static MemoryManager memoryManager;
    private static JFrame frame;
    private static JTextPane statusArea;
    private static JTextField processIdField;
    private static JTextField sizeField;
    private static JTextField strategyField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MemoryGUI::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        frame = new JFrame("Memory Simulation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(6, 2, 10, 10));

        JLabel totalSizeLabel = new JLabel("Total memory size:");
        JTextField totalSizeField = new JTextField();
        JButton initializeButton = new JButton("Initialize Memory");

        inputPanel.add(totalSizeLabel);
        inputPanel.add(totalSizeField);
        inputPanel.add(new JLabel("Process ID:"));
        processIdField = new JTextField();
        inputPanel.add(processIdField);
        inputPanel.add(new JLabel("Memory Size:"));
        sizeField = new JTextField();
        inputPanel.add(sizeField);
        inputPanel.add(new JLabel("Strategy (first, best, worst):"));
        strategyField = new JTextField();
        inputPanel.add(strategyField);
        inputPanel.add(initializeButton);

        frame.add(inputPanel, BorderLayout.NORTH);

        statusArea = new JTextPane(); // JTextPane supports HTML content
        statusArea.setContentType("text/html");
        statusArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(statusArea);
        frame.add(scrollPane, BorderLayout.CENTER);

        initializeButton.addActionListener(e -> {
            try {
                int totalSize = Integer.parseInt(totalSizeField.getText());
                memoryManager = new MemoryManager(totalSize);
                displayMessage("<b>Memory initialized with size " + totalSize + " units.</b>");
            } catch (NumberFormatException ex) {
                displayMessage("<font color='red'>Invalid total memory size.</font>");
            }
        });

        JButton allocateButton = new JButton("Allocate Memory");
        JButton deallocateButton = new JButton("Deallocate Memory");
        JButton displayButton = new JButton("Display Memory");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(allocateButton);
        buttonPanel.add(deallocateButton);
        buttonPanel.add(displayButton);

        frame.add(buttonPanel, BorderLayout.SOUTH);

        allocateButton.addActionListener(e -> {
            if (memoryManager == null) {
                displayMessage("<font color='red'>Memory not initialized.</font>");
                return;
            }
            try {
                int processId = Integer.parseInt(processIdField.getText());
                int size = Integer.parseInt(sizeField.getText());
                String strategy = strategyField.getText();
                boolean success = memoryManager.allocateMemory(processId, size, strategy);
                if (success) {
                    displayMessage("<font color='green'>Memory allocated successfully.</font>");
                } else {
                    displayMessage("<font color='red'>Memory allocation failed. Ensure sufficient memory and valid input.</font>");
                }
            } catch (NumberFormatException ex) {
                displayMessage("<font color='red'>Invalid input for process ID or size.</font>");
            }
        });

        deallocateButton.addActionListener(e -> {
            if (memoryManager == null) {
                displayMessage("<font color='red'>Memory not initialized.</font>");
                return;
            }
            try {
                int processId = Integer.parseInt(processIdField.getText());
                memoryManager.deallocateMemory(processId);
                displayMessage("<font color='green'>Memory deallocated successfully.</font>");
            } catch (NumberFormatException ex) {
                displayMessage("<font color='red'>Invalid process ID.</font>");
            }
        });

        displayButton.addActionListener(e -> {
            if (memoryManager == null) {
                displayMessage("<font color='red'>Memory not initialized.</font>");
                return;
            }
            String status = memoryManager.getMemoryStatus();
            statusArea.setText(status);
        });

        frame.setVisible(true);
    }

    private static void displayMessage(String message) {
        statusArea.setText("<html><body>" + message + "</body></html>");
    }
}
