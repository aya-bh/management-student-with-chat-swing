package view;

import controller.StudentController;
import model.Student;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class StudentView extends JFrame {
    private StudentController studentController;

    private JTextField idField;
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField sexeField;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton chatButton;
    private JTable studentTable;

    public StudentView(StudentController studentController) {
        this.studentController = studentController;

        setTitle("Student Management System");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        idField = new JTextField();
        nomField = new JTextField();
        prenomField = new JTextField();
        sexeField = new JTextField();
        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        chatButton = new JButton("Chat");

        // Create the table model with column names
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("ID");
        tableModel.addColumn("Nom");
        tableModel.addColumn("Prenom");
        tableModel.addColumn("Sexe");

        studentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(studentTable);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("ID:"));
        inputPanel.add(idField);
        inputPanel.add(new JLabel("Nom:"));
        inputPanel.add(nomField);
        inputPanel.add(new JLabel("Prenom:"));
        inputPanel.add(prenomField);
        inputPanel.add(new JLabel("Sexe:"));
        inputPanel.add(sexeField);
        inputPanel.add(addButton);
        inputPanel.add(updateButton);
        inputPanel.add(chatButton);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);
        buttonPanel.add(chatButton);

        add(buttonPanel, BorderLayout.SOUTH);
        updateStudentList(studentController.getAllStudents());

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStudent();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateStudent();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteStudent();
            }
        });

        chatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    // Run the server in a separate thread
                    new Thread(() -> {
                        new ChatServer().setVisible(true);
                    }).start();

                    // Run the client in the main thread
                    new ChatClient().setVisible(true);
                    new ChatClient().setVisible(true);
                });
            }
        });
    }

    public void updateStudentList(List<Student> students) {
        DefaultTableModel tableModel = (DefaultTableModel) studentTable.getModel();
        tableModel.setRowCount(0); // Clear existing rows

        for (Student student : students) {
            Object[] rowData = {student.getId(), student.getNom(), student.getPrenom(), student.getSexe()};
            tableModel.addRow(rowData);
        }
    }

    private void addStudent() {
        // Retrieve data from input fields
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String sexe = sexeField.getText();

        Student student = new Student(nom, prenom, sexe);

        studentController.addStudent(student);

        updateStudentList(studentController.getAllStudents());
        clearInputFields();
    }

    private void updateStudent() {
        int selectedRow = studentTable.getSelectedRow();

        // Check if a row is selected
        if (selectedRow != -1) {
            // Get data from the selected row
            int currentId = (int) studentTable.getValueAt(selectedRow, 0);
            String currentNom = (String) studentTable.getValueAt(selectedRow, 1);
            String currentPrenom = (String) studentTable.getValueAt(selectedRow, 2);
            String currentSexe = (String) studentTable.getValueAt(selectedRow, 3);


            // Update the data in the table model
            DefaultTableModel tableModel = (DefaultTableModel) studentTable.getModel();
            tableModel.setValueAt(currentId, selectedRow, 0);
            tableModel.setValueAt(currentNom, selectedRow, 1);
            tableModel.setValueAt(currentPrenom, selectedRow, 2);
            tableModel.setValueAt(currentSexe, selectedRow, 3);

            // Update the student in the database
            studentController.updateStudent(new Student(currentId, currentNom, currentPrenom, currentSexe));
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a student to update.",
                    "Update Student",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }


    private void clearInputFields() {
        idField.setText("");
        nomField.setText("");
        prenomField.setText("");
        sexeField.setText("");
    }

    private void deleteStudent() {
        int selectedRow = studentTable.getSelectedRow();

        if (selectedRow != -1) {
            // Ask for confirmation before deleting
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this student?",
                    "Delete Student",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                int studentIdToDelete = (int) studentTable.getValueAt(selectedRow, 0);
                studentController.deleteStudent(studentIdToDelete);

                // Update the UI with the latest student list
                updateStudentList(studentController.getAllStudents());
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a student to delete.",
                    "Delete Student",
                    JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
}
