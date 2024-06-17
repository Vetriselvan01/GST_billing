import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Product {
    String tempFile = "documents\\temp.txt";
    static String amountFile = "documents\\amount.txt";
    public Product(String name, int quantity) {
        try (FileWriter fw = new FileWriter(tempFile, true)) {
            fw.write(String.valueOf(name + "," +readProductCosts(name)+","+quantity));
            fw.write("\n");
        }catch (Exception e) {
        }
    }
    private static int  readProductCosts(String productName) {
        
        try (BufferedReader br = new BufferedReader(new FileReader(amountFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                String product = parts[0].trim();
                int cost = Integer.parseInt(parts[1].trim());
                if(product.equals(productName))
                return cost;
            } 
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
        }
}
