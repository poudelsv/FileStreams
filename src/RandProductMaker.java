import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandProductMaker extends JFrame
{
    private JTextField nameField;
    private JTextField descriptionField;
    private JTextField idField;
    private JTextField costField;
    private JTextField recordCountField;

    private JButton addButton;
    private JButton quitButton;

    private final File dataFile;

    public RandProductMaker()
    {
        super("Rand Product Maker");

        dataFile = new File("products.dat");

        buildGUI();
        updateRecordCount();

        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void buildGUI()
    {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 8, 8));

        formPanel.add(new JLabel("Product Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        formPanel.add(descriptionField);

        formPanel.add(new JLabel("Product ID:"));
        idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(new JLabel("Cost:"));
        costField = new JTextField();
        formPanel.add(costField);

        formPanel.add(new JLabel("Record Count:"));
        recordCountField = new JTextField();
        recordCountField.setEditable(false);
        formPanel.add(recordCountField);

        JPanel buttonPanel = new JPanel();

        addButton = new JButton("Add");
        quitButton = new JButton("Quit");

        buttonPanel.add(addButton);
        buttonPanel.add(quitButton);

        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        addButton.addActionListener(e -> addRecord());
        quitButton.addActionListener(e -> System.exit(0));
    }

    private void updateRecordCount()
    {
        try (RandomAccessFile raf = new RandomAccessFile(dataFile, "rw"))
        {
            long count = raf.length() / Product.RECORD_SIZE;
            recordCountField.setText(String.valueOf(count + 1));
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this,
                    "Error reading record count: " + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addRecord()
    {
        String name = nameField.getText().trim();
        String description = descriptionField.getText().trim();
        String id = idField.getText().trim();
        String costText = costField.getText().trim();

        if (name.isEmpty() || description.isEmpty() || id.isEmpty() || costText.isEmpty())
        {
            JOptionPane.showMessageDialog(this,
                    "All fields must be filled in.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (name.length() > Product.NAME_LEN)
        {
            JOptionPane.showMessageDialog(this,
                    "Name must be at most " + Product.NAME_LEN + " characters.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (description.length() > Product.DESC_LEN)
        {
            JOptionPane.showMessageDialog(this,
                    "Description must be at most " + Product.DESC_LEN + " characters.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (id.length() > Product.ID_LEN)
        {
            JOptionPane.showMessageDialog(this,
                    "ID must be at most " + Product.ID_LEN + " characters.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        double cost;
        try
        {
            cost = Double.parseDouble(costText);
        }
        catch (NumberFormatException e)
        {
            JOptionPane.showMessageDialog(this,
                    "Cost must be a valid number.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Product product = new Product(name, description, id, cost);

        try (RandomAccessFile raf = new RandomAccessFile(dataFile, "rw"))
        {
            raf.seek(raf.length());
            product.writeToFile(raf);

            JOptionPane.showMessageDialog(this,
                    "Record added successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            clearFields();
            updateRecordCount();
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this,
                    "Error writing record: " + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields()
    {
        nameField.setText("");
        descriptionField.setText("");
        idField.setText("");
        costField.setText("");
        nameField.requestFocus();
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(RandProductMaker::new);
    }
}