
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Add extends JFrame {
    private JTextField searchBar;
    private JList<String> searchResultsList;
    private DefaultListModel<String> searchResultsModel;
    public List<String> elements;
    private JTextField quantityField;
    private String tempFile = "documents\\temp.txt";
    private String elementFile= "documents\\elements.txt";
    public Add() {
        setTitle("Add Products");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        elements = new ArrayList<>();
        searchBar = new JTextField();
        searchBar.setPreferredSize(new Dimension(300, 30));

        searchResultsModel = new DefaultListModel<>();
        searchResultsList = new JList<>(searchResultsModel);
        JScrollPane scrollPane = new JScrollPane(searchResultsList);
        scrollPane.setPreferredSize(new Dimension(300, 100));

        searchBar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchElements(searchBar.getText().toLowerCase());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchElements(searchBar.getText().toLowerCase());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchElements(searchBar.getText().toLowerCase());
            }
        });

        searchResultsList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    String selectedValue = searchResultsList.getSelectedValue();
                    if (selectedValue != null) {
                        searchBar.setText(selectedValue);
                    }
                }
            }
        });

        JPanel searchPanel = new JPanel();
        searchPanel.add(searchBar);

        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(searchPanel, BorderLayout.NORTH);
        container.add(scrollPane, BorderLayout.CENTER);

        JLabel quantityLabel = new JLabel("Quantity:");
        quantityField = new JTextField(20);
        JButton continueButton = new JButton("Add");
        JButton doneButton = new JButton("Done");

        JPanel inputPanel = new JPanel();
        inputPanel.add(quantityLabel);
        inputPanel.add(quantityField);
        inputPanel.add(continueButton);
        inputPanel.add(doneButton);
        container.add(inputPanel, BorderLayout.SOUTH);

        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addProductToList();
            }
        });

        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                finishAndProcessList();
            }
        });
    }

    private void searchElements(String query) {
        searchResultsModel.clear();
        for (String element : elements) {
            if (element.toLowerCase().startsWith(query) || element.toLowerCase().contains(query)) {
                searchResultsModel.addElement(element);
            }
        }
    }

    private List<String> readElementsFromFile(String filePath) {
        List<String> elements = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                elements.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return elements;
    }

    private void addProductToList() {
        String name = searchBar.getText();
        int quantity;
        try {
            quantity = Integer.parseInt(quantityField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        new Product(name, quantity);
        searchBar.setText("");
        quantityField.setText("");
        searchResultsModel.clear();
    }

    private void finishAndProcessList() {
        dispose();
        printProductList();
    }

    private void printProductList() {
        App app = new App();
        app.appmain();
    }

    public   void addmain() {
        Add app = new Add();
        try (FileWriter fw = new FileWriter(tempFile, false)) {
        }catch (Exception e) {
        }
        app.elements = app.readElementsFromFile(elementFile);
        app.setVisible(true);
}
}
