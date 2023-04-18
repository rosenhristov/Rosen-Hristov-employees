package com.rosenhristov.service;

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.rosenhristov.utils.Utils.isCSVFilePath;

public class UserInputReaderGUI extends BaseUserInputReader implements ActionListener {

    private String filePath;

    private JFrame frame;
    private JDialog dialog;
    private JPanel panel;
    private JTextField filePathTextField;
    private JLabel filePathLabel;
    private JButton submitButton;

    private UserInputReaderGUI() {
    }

    public static UserInputReaderGUI create() {
        return new UserInputReaderGUI();
    }

    @Override
    public void getUserInput() {
        frame = new JFrame();

        dialog = new JDialog(frame);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setTitle("User Input");
        dialog.setSize(550, 200);
        dialog.setResizable(true);
        dialog.setLocationRelativeTo(null);

        panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        panel.setLayout(null);

        filePathLabel = new JLabel("Enter the CSV file path:");
        filePathLabel.setBounds(10,20,500,25);
        panel.add(filePathLabel);

        filePathTextField = new JTextField();
        filePathTextField.setBounds(10, 50, 500, 25);
        panel.add(filePathTextField);

        submitButton = new JButton("Submit");
        submitButton.setBounds(10, 80, 78, 30);
        submitButton.addActionListener(this);
        panel.add(submitButton);

        dialog.add(panel);
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String error = StringUtils.EMPTY;
        filePath = filePathTextField.getText();
        if (!isCSVFilePath(filePath)) {
            filePathTextField.setText(null);
            error = "This is not a path to '.csv' file. The file name should have a '.csv' extension";
        }

        if (StringUtils.isNotEmpty(error)) {
            JOptionPane.showMessageDialog(
                    null, error, "Error!", JOptionPane.ERROR_MESSAGE);
        }

        if (StringUtils.isEmpty(error)) {
            dialog.dispose();
        }
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getDateFormat() {
        return this.dateFormat;
    }
}
