import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MyPersonalTaskManager extends JFrame {
    private List<TaskDetails> tasks = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JTable taskTable;
    private JTextField nameTextField = new JTextField(20);
    private JTextArea descriptionTextArea = new JTextArea(5, 20);
    private JTextField dueDateTextField = new JTextField(10);

    public MyPersonalTaskManager() {
        setTitle("My Personal Task Manger");
        setSize(800, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(0.4);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(createInputPanel(), BorderLayout.CENTER);
        leftPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(createTaskTablePanel(), BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        getContentPane().add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);

        loadTasks();
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 10, 5, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("WELCOME"), gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Task"), gbc);
        gbc.gridx = 1;
        panel.add(nameTextField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Notes"), gbc);
        gbc.gridx = 1;
        panel.add(new JScrollPane(descriptionTextArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Due Date:"), gbc);
        gbc.gridx = 1;
        panel.add(dueDateTextField, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        
        JButton createButton = new JButton("Add New Task");
        createButton.addActionListener(e -> createTask());
        createButton.setBackground(Color.GREEN);

        JButton editButton = new JButton("Update");
        editButton.setBackground(Color.BLUE);
        editButton.addActionListener(e -> updateTask());

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBackground(Color.RED);
        deleteButton.addActionListener(e -> deleteTask());

        panel.add(createButton);
        panel.add(editButton);
        panel.add(deleteButton);

        return panel;
    }

    private JPanel createTaskTablePanel() {
    tableModel = new DefaultTableModel(new Object[]{"Task", "Notes", "Due On", "Status"}, 0);
    taskTable = new JTable(tableModel);
    taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    DefaultTableCellRenderer statusRowRenderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            TaskStatus status = (TaskStatus) table.getModel().getValueAt(row, 3); // Get status from the model

            if (isSelected) {
                component.setBackground(table.getSelectionBackground());
            } else {
                if (status == TaskStatus.COMPLETED) {
                    component.setBackground(Color.GREEN);
                } else if (status == TaskStatus.PENDING) {
                    component.setBackground(Color.YELLOW);
                
                } else {
                    component.setBackground(table.getBackground());
                }
            }

            return component;
        }
    };

    // Apply the custom renderer to all columns
    for (int i = 0; i < taskTable.getColumnCount(); i++) {
        taskTable.getColumnModel().getColumn(i).setCellRenderer(statusRowRenderer);
    }

    JPanel panel = new JPanel(new BorderLayout());
    panel.add(new JScrollPane(taskTable), BorderLayout.CENTER);
    return panel;
}


    private void createTask() {
        String name = nameTextField.getText();
        String description = descriptionTextArea.getText();
        String dueDate = dueDateTextField.getText();

        if (name.isEmpty() || description.isEmpty() || dueDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All field are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TaskDetails task = new TaskDetails(name, description, dueDate);
        tasks.add(task);

        Object[] rowData = {task.getName(), task.getDescription(), task.getDueDate(), task.getStatus()};
        tableModel.addRow(rowData);

        nameTextField.setText("");
        descriptionTextArea.setText("");
        dueDateTextField.setText("");

        saveTasks();
    }

    
    private void updateTask() {
        int selectedIndex = taskTable.getSelectedRow();
        if (selectedIndex >= 0) {
            TaskDetails selectedTask = tasks.get(selectedIndex);

            nameTextField.setText(selectedTask.getName());
            descriptionTextArea.setText(selectedTask.getDescription());
            dueDateTextField.setText(selectedTask.getDueDate());

            JComboBox<TaskStatus> statusComboBox = new JComboBox<>(TaskStatus.values());
            statusComboBox.setSelectedItem(selectedTask.getStatus());

            JPanel editPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();

            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(5, 10, 5, 10);

            

            gbc.gridx = 0;
            gbc.gridy = 0;
            editPanel.add(new JLabel("Task"), gbc);
            gbc.gridx = 1;
            editPanel.add(nameTextField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            editPanel.add(new JLabel("Update Notes"), gbc);
            gbc.gridx = 1;
            editPanel.add(new JScrollPane(descriptionTextArea), gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            editPanel.add(new JLabel("Due Date"), gbc);
            gbc.gridx = 1;
            editPanel.add(dueDateTextField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            editPanel.add(new JLabel("New Status"), gbc);
            gbc.gridx = 1;
            editPanel.add(statusComboBox, gbc);

            int result = JOptionPane.showConfirmDialog(this, editPanel,
                    "Update Task", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                selectedTask.setName(nameTextField.getText());
                selectedTask.setDescription(descriptionTextArea.getText());
                selectedTask.setDueDate(dueDateTextField.getText());
                selectedTask.setStatus((TaskStatus) statusComboBox.getSelectedItem());

                tableModel.setValueAt(selectedTask.getName(), selectedIndex, 0);
                tableModel.setValueAt(selectedTask.getDescription(), selectedIndex, 1);
                tableModel.setValueAt(selectedTask.getDueDate(), selectedIndex, 2);
                tableModel.setValueAt(selectedTask.getStatus(), selectedIndex, 3);

                saveTasks();
            }

            nameTextField.setText("");
            descriptionTextArea.setText("");
            dueDateTextField.setText("");
        }
    }

    private void loadTasks() {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("tasks.dat"))) {
            tasks = (List<TaskDetails>) inputStream.readObject();
            for (TaskDetails task : tasks) {
                Object[] rowData = {task.getName(), task.getDescription(), task.getDueDate(), task.getStatus()};
                tableModel.addRow(rowData);
            }
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private void saveTasks() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("tasks.dat"))) {
            outputStream.writeObject(tasks);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteTask() {
        int selectedIndex = taskTable.getSelectedRow();
        if (selectedIndex >= 0) {
            tasks.remove(selectedIndex);
            tableModel.removeRow(selectedIndex);
            saveTasks();
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MyPersonalTaskManager::new);
    }
}

class TaskDetails implements Serializable {
    private String name;
    private String description;
    private String dueDate;
    private TaskStatus status;

    public TaskDetails(String name, String description, String dueDate) {
        this.name = name;
        this.description = description;
        this.dueDate = dueDate;
        this.status = TaskStatus.PENDING; // Default status
    }

    // Getters and setters for name, description, dueDate, and status
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}


enum TaskStatus {
    PENDING, COMPLETED
}
