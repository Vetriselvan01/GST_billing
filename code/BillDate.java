import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BillDate {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = sdf.format(new Date());
        public BillDate(){
            JFrame frame = new JFrame();
            frame.setTitle("Set Bill Date");
            frame.setSize(600,400);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());
            JLabel label = new JLabel("Date");
            JTextField date = new JTextField(20);
            JLabel invoice = new JLabel("INVOICE NUMBER");
            JTextField invoiceNO = new JTextField(20);
            JPanel panel1 = new JPanel();
            panel1.setLayout(new FlowLayout());
            panel1.add(label);
            panel1.add(date);
            JPanel panel2 = new JPanel();
            panel2.setLayout(new FlowLayout());
            panel2.add(invoice);
            panel2.add(invoiceNO);
            frame.add(panel1,BorderLayout.NORTH);
            frame.add(panel2,BorderLayout.CENTER);
            date.setText(currentDate);

            JButton next = new JButton("Next");
            next.setPreferredSize(new Dimension(100,50));
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(next,BorderLayout.EAST);
            frame.add(buttonPanel,BorderLayout.SOUTH);
            frame.setVisible(true);
            next.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    App.date = date.getText();
                    App.invoiceNumber = Integer.parseInt(invoiceNO.getText());
                    frame.dispose();
                    Add obj = new Add();
                    obj.addmain();
                }
            });
        }
}
