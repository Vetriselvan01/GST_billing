import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
public class Main extends JFrame {
    private JTextField searchBar;
    private JList<String> searchResultsList;
    private DefaultListModel<String> searchResultsModel;
    private List<String> elements;
    private static String buyerFile = "documents\\buyers.txt";
    public Main() {
        setTitle("buyer Search");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        searchBar = new JTextField();
        searchBar.setPreferredSize(new Dimension(300, 30));

        searchResultsModel = new DefaultListModel<>();
        searchResultsList = new JList<>(searchResultsModel);
        JScrollPane scrollPane = new JScrollPane(searchResultsList);
        scrollPane.setPreferredSize(new Dimension(300, 200));

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
        JButton continueButton = new JButton("Next");
        searchPanel.add(continueButton);
        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                App.buyer = searchBar.getText();
                dispose();
                new BillDate();
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

    public static void main(String[] args) {
        Main app = new Main();
        app.elements = app.readElementsFromFile(buyerFile);
        app.setVisible(true);
    }
}
