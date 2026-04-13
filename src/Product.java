import java.io.IOException;
import java.io.RandomAccessFile;

public class Product
{
    private String name;
    private String description;
    private final String id;   // ID should never change once set
    private double cost;

    public static final int NAME_LEN = 35;
    public static final int DESC_LEN = 75;
    public static final int ID_LEN = 6;

    // each char = 2 bytes, double = 8 bytes
    public static final int RECORD_SIZE = (NAME_LEN + DESC_LEN + ID_LEN) * 2 + 8;

    public Product(String name, String description, String id, double cost)
    {
        this.name = name;
        this.description = description;
        this.id = id;
        this.cost = cost;
    }

    public Product(String name, String id, double cost)
    {
        this(name, "", id, cost);
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getId() { return id; }
    public double getCost() { return cost; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCost(double cost) { this.cost = cost; }

    public String toCSV()
    {
        return id + "," + name + "," + description + "," + cost;
    }

    public String toJSON()
    {
        return "{"
                + "\"id\":\"" + escapeJson(id) + "\","
                + "\"name\":\"" + escapeJson(name) + "\","
                + "\"description\":\"" + escapeJson(description) + "\","
                + "\"cost\":" + cost
                + "}";
    }

    public String toXML()
    {
        return "<product>"
                + "<id>" + escapeXml(id) + "</id>"
                + "<name>" + escapeXml(name) + "</name>"
                + "<description>" + escapeXml(description) + "</description>"
                + "<cost>" + cost + "</cost>"
                + "</product>";
    }

    @Override
    public String toString()
    {
        return "ID: " + id +
                " | Name: " + name +
                " | Description: " + description +
                " | Cost: $" + String.format("%.2f", cost);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Product other = (Product) obj;
        return id.equals(other.id);
    }

    private String escapeJson(String s)
    {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private String escapeXml(String s)
    {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private static String padString(String s, int length)
    {
        if (s == null)
        {
            s = "";
        }

        s = s.trim();

        if (s.length() > length)
        {
            return s.substring(0, length);
        }

        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < length)
        {
            sb.append(' ');
        }

        return sb.toString();
    }

    public void writeToFile(RandomAccessFile file) throws IOException
    {
        file.writeChars(padString(name, NAME_LEN));
        file.writeChars(padString(description, DESC_LEN));
        file.writeChars(padString(id, ID_LEN));
        file.writeDouble(cost);
    }

    public static Product readFromFile(RandomAccessFile file) throws IOException
    {
        char[] nameChars = new char[NAME_LEN];
        for (int i = 0; i < NAME_LEN; i++)
        {
            nameChars[i] = file.readChar();
        }
        String name = new String(nameChars).trim();

        char[] descChars = new char[DESC_LEN];
        for (int i = 0; i < DESC_LEN; i++)
        {
            descChars[i] = file.readChar();
        }
        String description = new String(descChars).trim();

        char[] idChars = new char[ID_LEN];
        for (int i = 0; i < ID_LEN; i++)
        {
            idChars[i] = file.readChar();
        }
        String id = new String(idChars).trim();

        double cost = file.readDouble();

        return new Product(name, description, id, cost);
    }
}