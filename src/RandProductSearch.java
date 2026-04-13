import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

public class RandProductSearch extends JFrame
{
    private JTextField searchField;
    private JTextArea resultArea;
    private JButton searchButton;
    private JButton quitButton;

    private final File dataFile;

    public RandProductSearch()
    {
        super("Rand Product Search");

        dataFile = new File("products.dat");

        buildGUI();

        setSize(650, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void buildGUI()
    {
        JPanel topPanel = new JPanel(new BorderLayout(8, 8));
        JPanel buttonPanel = new JPanel();

        searchField = new JTextField();
        searchButton = new JButton("Search");
        quitButton = new JButton("Quit");

        topPanel.add(new JLabel("Enter partial product name:"), BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);

        buttonPanel.add(searchButton);
        buttonPanel.add(quitButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        searchButton.addActionListener(e -> searchProducts());
        quitButton.addActionListener(e -> System.exit(0));
    }

    private void searchProducts()
    {
        String searchText = searchField.getText().trim().toLowerCase();

        if (searchText.isEmpty())
        {
            JOptionPane.showMessageDialog(this,
                    "Please enter part of a product name.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!dataFile.exists())
        {
            JOptionPane.showMessageDialog(this,
                    "The data file products.dat does not exist yet.",
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        resultArea.setText("");

        boolean found = false;

        try (RandomAccessFile raf = new RandomAccessFile(dataFile, "r"))
        {
            long numRecords = raf.length() / Product.RECORD_SIZE;

            for (int i = 0; i < numRecords; i++)
            {
                raf.seek((long) i * Product.RECORD_SIZE);
                Product product = Product.readFromFile(raf);

                if (product.getName().toLowerCase().contains(searchText))
                {
                    resultArea.append(product.toString() + "\n");
                    found = true;
                }
            }

            if (!found)
            {
                resultArea.setText("No matching products found.");
            }
        }
        catch (IOException e)
        {
            JOptionPane.showMessageDialog(this,
                    "Error searching file: " + e.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(RandProductSearch::new);
    }
}