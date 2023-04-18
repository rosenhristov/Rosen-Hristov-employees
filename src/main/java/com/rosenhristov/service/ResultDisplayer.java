package com.rosenhristov.service;

import com.rosenhristov.model.ProjectCollaboration;
import javax.swing.*;
import java.util.List;

public class ResultDisplayer {

    List<ProjectCollaboration> collaborationsToDisplay;

    public ResultDisplayer(List<ProjectCollaboration> collaborationsToDisplay) {
        this.collaborationsToDisplay = collaborationsToDisplay;
    }

    public static ResultDisplayer of(List<ProjectCollaboration> collaborationsToDisplay) {
        return new ResultDisplayer(collaborationsToDisplay);
    }

    public void displayCollaborationsAsDataGrid() {
        String[] columnHeaders = {"Employee ID #1", "Employee ID #2", "Project ID", "Days worked"};
        Object[][] data = prepareDataGrid();

        JTable table = new JTable(data, columnHeaders);

        JScrollPane scrollPane = new JScrollPane(table);

        JPanel panel = new JPanel();
        panel.add(scrollPane);

        JFrame frame = new JFrame("Longest collaborations in common projects");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private Integer[][] prepareDataGrid() {
        int rows = collaborationsToDisplay.size();
        int cols = 4;
        Integer[][] data = new Integer[rows][4];
        for (int row = 0; row < data.length; row++) {
            ProjectCollaboration collaboration = collaborationsToDisplay.get(row);
            for (int col = 0; col < cols; col++) {
                data[row][col] = getRequiredData(col, collaboration);
            }
        }
        return data;
    }

    private Integer getRequiredData(int column, ProjectCollaboration collaboration) {
        Integer data;
        switch(column) {
            case 0:
                data = collaboration.getEmployee1();
                break;
            case 1:
                data = collaboration.getEmployee2();
                break;
            case 2:
                data = collaboration.getProjectID();
                break;
            case 3:
                data = collaboration.getDaysWorked();
                break;

            default: data = 0;
        }
        return data;
    }
}
