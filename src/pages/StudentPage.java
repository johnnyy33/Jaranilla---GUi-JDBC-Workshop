package pages;

import dal.students.StudentDAO;
import models.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class StudentPage extends JFrame {
    private final StudentDAO studentDao = new StudentDAO();
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JTextField studentNumberField;
    private final JTextField firstNameField;
    private final JTextField lastNameField;
    private final JTextField programField;
    private final JSpinner levelSpinner;
    private final JComboBox<String> genderComboBox;
    private JButton addButton, updateButton, deleteButton;

    public StudentPage() {
        setTitle("Student CRUD");
        setSize(900, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        studentNumberField = new JTextField(15);
        firstNameField = new JTextField(15);
        lastNameField = new JTextField(15);
        programField = new JTextField(15);
        levelSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        genderComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});

        int row = 0;
        addFormRow(formPanel, gbc, row++, "Student Number:", studentNumberField);
        addFormRow(formPanel, gbc, row++, "First Name:", firstNameField);
        addFormRow(formPanel, gbc, row++, "Last Name:", lastNameField);
        addFormRow(formPanel, gbc, row++, "Program:", programField);
        addFormRow(formPanel, gbc, row++, "Level:", levelSpinner);
        addFormRow(formPanel, gbc, row++, "Gender:", genderComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");

        addButton.setBackground(new Color(76, 175, 80)); // green
        updateButton.setBackground(new Color(255, 235, 59)); // yellow
        deleteButton.setBackground(new Color(244, 67, 54)); // red

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(
                new String[]{"ID", "Student Number", "First Name", "Last Name", "Program", "Level", "Gender"}, 0
        );
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        loadStudents();

        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());

        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                studentNumberField.setText(tableModel.getValueAt(row, 1).toString());
                firstNameField.setText(tableModel.getValueAt(row, 2).toString());
                lastNameField.setText(tableModel.getValueAt(row, 3).toString());
                programField.setText(tableModel.getValueAt(row, 4).toString());
                levelSpinner.setValue((int) tableModel.getValueAt(row, 5));
                genderComboBox.setSelectedItem(tableModel.getValueAt(row, 6).toString());
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        panel.add(new JLabel(label), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        panel.add(field, gbc);
    }

    private void loadStudents() {
        tableModel.setRowCount(0);
        List<Student> students = studentDao.getAllStudents();
        for (Student student : students) {
            tableModel.addRow(new Object[]{
                    student.getId(),
                    student.getStudentNumber(),
                    student.getFirstName(),
                    student.getLastName(),
                    student.getProgram(),
                    student.getLevel(),
                    student.getGender()
            });
        }
    }

    private void addStudent() {
        String studentNumber = studentNumberField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String program = programField.getText();
        int level = (int) levelSpinner.getValue();
        String gender = genderComboBox.getSelectedItem().toString();

        if (!studentNumber.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !program.isEmpty()) {
            studentDao.addStudent(new Student(0, studentNumber, firstName, lastName, program, level, gender));
            loadStudents();
            clearFields();
        } else {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
        }
    }

    private void updateStudent() {
        int row = table.getSelectedRow();
        if (row != -1) {
            int id = (int) tableModel.getValueAt(row, 0);
            String studentNumber = studentNumberField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String program = programField.getText();
            int level = (int) levelSpinner.getValue();
            String gender = genderComboBox.getSelectedItem().toString();

            if (!studentNumber.isEmpty() && !firstName.isEmpty() && !lastName.isEmpty() && !program.isEmpty()) {
                studentDao.updateStudent(new Student(id, studentNumber, firstName, lastName, program, level, gender));
                loadStudents();
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            }
        }
    }

    private void deleteStudent() {
        int row = table.getSelectedRow();
        if (row != -1) {
            int id = (int) tableModel.getValueAt(row, 0);
            studentDao.deleteStudent(id);
            loadStudents();
            clearFields();
        }
    }

    private void clearFields() {
        studentNumberField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        programField.setText("");
        levelSpinner.setValue(1);
        genderComboBox.setSelectedIndex(0);
    }
}