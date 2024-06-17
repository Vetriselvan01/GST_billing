import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;

public class App {
    private static String FILE_NAME;
    private static final String LOGO_PATH = "logo.png";
    static String buyer;
    public static int invoiceNumber;
    private static int sno = 1;
    private static float subtotal = 0;
    private static float totalDiscount = 0;
    private static float totalTax = 0;
    private static float totalTaxable = 0;
    private static Document document;
    private static PdfPTable tableheader;
    private static PdfPTable table;
    private static Font titleFont = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static Font titleFont2 = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static Font subFont = new Font(Font.FontFamily.HELVETICA, 12);
    public static String date;  
    static String tempFile = "documents\\temp.txt";
    public void appmain() {
        
        FILE_NAME = "INVOICE - " + invoiceNumber + ".pdf";

        // Get the directory where the JAR file is located
        Path jarDir = Paths.get(System.getProperty("user.dir"));

        // Create the invoice directory if it doesn't exist
        File invoiceDir = new File(jarDir.toString(), "invoice");
        if (!invoiceDir.exists()) {
            if (!invoiceDir.mkdir()) {
                System.err.println("Failed to create directory: " + invoiceDir.getAbsolutePath());
                return;
            }
        }

        // Path to the PDF file within the invoice directory
        File pdfFile = new File(invoiceDir, FILE_NAME);

        document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
            document.open();

            // Add logo
            InputStream logoStream = getClass().getResourceAsStream(LOGO_PATH);
            if (logoStream == null) {
                throw new FileNotFoundException("Resource not found: " + LOGO_PATH);
            }
            Image logo = Image.getInstance(ImageIO.read(logoStream), null);
            logo.setAbsolutePosition(400, 750);
            logo.scaleToFit(180, 60);
            document.add(logo);

            // Add company details
            addCompanyDetails();

            // Add invoice title
            Paragraph invoiceTitle = new Paragraph("Tax Invoice", new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD));
            invoiceTitle.setAlignment(Element.ALIGN_CENTER);
            invoiceTitle.setSpacingBefore(10);
            document.add(invoiceTitle);

            // Create table for Bill To and Invoice Details
            PdfPTable billToAndInvoiceDetailsTable = addBillToAndInvoice();
            document.add(billToAndInvoiceDetailsTable);
            LineSeparator lineSeparator = new LineSeparator();
            document.add(lineSeparator);

            // Add table headers
            addTableHeader();
            document.add(tableheader);
            // Add table rows
            document.add(lineSeparator);
            addTableRows();
            document.add(table);

            // Add totals
            addTotals();

            // Add terms and conditions
            addTermsAndConditions(document, subFont);

            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void addCompanyDetails() throws DocumentException {
        document.add(new Paragraph("SELLER", titleFont));
        document.add(new Paragraph("Address ..", subFont));
        document.add(new Paragraph("Address ..", subFont));
        document.add(new Paragraph("Phone no :", subFont));
        document.add(new Paragraph("GSTIN: ", subFont));
    }

    private PdfPTable addBillToAndInvoice() throws DocumentException {
        float[] topWidth = {7f, 2f};
        PdfPTable billToAndInvoiceDetailsTable = new PdfPTable(2);
        billToAndInvoiceDetailsTable.setWidthPercentage(100);
        billToAndInvoiceDetailsTable.setSpacingBefore(20f);
        billToAndInvoiceDetailsTable.setSpacingAfter(20f);
        billToAndInvoiceDetailsTable.setWidths(topWidth);

        // Add Bill To details to the left column
        PdfPCell billToCell = new PdfPCell();
        billToCell.setBorder(Rectangle.NO_BORDER);
        billToCell.addElement(new Paragraph("Bill To", titleFont));
        String[] address = buyer.split(",");
        for (String line : address) {
            billToCell.addElement(new Paragraph(line, subFont));
        }
        billToAndInvoiceDetailsTable.addCell(billToCell);

        // Add Invoice Details to the right column
        PdfPCell invoiceDetailsCell = new PdfPCell();
        invoiceDetailsCell.setBorder(Rectangle.NO_BORDER);
        invoiceDetailsCell.addElement(new Paragraph("Invoice Details", titleFont));
        invoiceDetailsCell.addElement(new Paragraph("Invoice No.: " + invoiceNumber, subFont));
        invoiceDetailsCell.addElement(new Paragraph("Date: " + date, subFont));
        billToAndInvoiceDetailsTable.addCell(invoiceDetailsCell);

        return billToAndInvoiceDetailsTable;
    }

    private static void addTableHeader() throws DocumentException {
        float[] colWidth = {2f, 10f, 6f, 6f, 6f, 6f, 6f, 6f, 6f};
        tableheader = new PdfPTable(9);
        tableheader.setWidthPercentage(100);
        tableheader.setSpacingBefore(10f);
        tableheader.setSpacingAfter(10f);
        tableheader.setWidths(colWidth);
        String[] head = {"S.N", "Item Name",  "MRP per \nUnit", "Quantity", "Rate", "Discount \n (20%)", "Taxable Amount" , "GST \n (18%)", "Total Amount"};
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        for (String field : head) {
            addCellToTable(field, titleFont, tableheader);
        }
    }

    private static void addCellToTable(String text, Font font, PdfPTable table) {
        PdfPCell temp = new PdfPCell(new Phrase(text, font));
        temp.setBorder(Rectangle.NO_BORDER);
        temp.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(temp);
    }

    private static void addTableRows() throws DocumentException {
        
        float[] colWidth = {2f, 10f, 6f, 6f, 6f, 6f, 6f, 6f, 6f};
        table = new PdfPTable(9);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(20f);
        table.setWidths(colWidth);
        try (BufferedReader br = new BufferedReader(new FileReader(tempFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] itemDetail = line.split(",");
                float rate = ((float) (Integer.parseInt(itemDetail[1]) * Integer.parseInt(itemDetail[2]) * 100) / 118.00f);
                float discount = 0.2f * rate;
                float taxable = rate - discount ; 
                float gst = (taxable) * 0.18f;
                float total = rate + gst - discount;
                addCellToTable(String.valueOf(sno), subFont, table);
                addCellToTable(itemDetail[0], subFont, table);
                addCellToTable(itemDetail[1], subFont, table);
                addCellToTable(itemDetail[2], subFont, table);
                addCellToTable(String.format("%.2f", rate), subFont, table);
                addCellToTable(String.format("%.2f", discount), subFont, table);
                addCellToTable(String.format("%.2f", taxable), subFont, table);
                addCellToTable(String.format("%.2f", gst), subFont, table);
                addCellToTable(String.format("%.2f", total), subFont, table);
                subtotal += rate;
                totalDiscount += discount;
                totalTaxable +=taxable;
                totalTax += gst;
                sno++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        addCellToTable(" ", titleFont2, table);
        addCellToTable("Total", titleFont2, table);
        addCellToTable(" ", titleFont2, table);
        addCellToTable(" ", titleFont2, table);
        addCellToTable(String.format("%.2f", subtotal), titleFont2, table);
        addCellToTable(String.format("%.2f", totalDiscount), titleFont2, table);
        addCellToTable(String.format("%.2f", totalTaxable), titleFont2, table);
        addCellToTable(String.format("%.2f", totalTax), titleFont2, table);
        addCellToTable(String.format("%.2f", subtotal + totalTax - totalDiscount), titleFont2, table);
    }

    private static void addTotals() throws DocumentException {
        PdfPTable totalTable = new PdfPTable(3);
        totalTable.setWidthPercentage(100);
        float[] w = {2f, 1f, 1f};
        totalTable.setWidths(w);
        PdfPCell bank = new PdfPCell();
        bank.setBorder(Rectangle.NO_BORDER);
        bank.addElement(new Paragraph("Pay To", titleFont));
        bank.addElement(new Paragraph("name", subFont));
        bank.addElement(new Paragraph("Bank", subFont));
        bank.addElement(new Paragraph("Acc No: ", subFont));
        bank.addElement(new Paragraph("IFSC code: ", subFont));
        totalTable.addCell(bank);

        int grandTotal = (int) (subtotal + totalTax - totalDiscount);
        float roundOff = (subtotal + totalTax - totalDiscount) - grandTotal;
        if(Math.round(roundOff*100.00f)/100.00f==1){
            grandTotal++;
            roundOff=0;
        }
        String[][] totals = {
                {"Sub Total", String.format("%.2f", subtotal)},
                {"Discount", String.format("%.2f", totalDiscount)},
                {"Taxable Amount", String.format("%.2f", totalTaxable)},
                {"SGST @ 9%", String.format("%.2f", totalTax / 2)},
                {"CGST @ 9%", String.format("%.2f", totalTax / 2)},
                {"Round off", "- " + String.format("%.2f", roundOff)}
        };
        PdfPCell descriptionCell = new PdfPCell();
        PdfPCell amountCell = new PdfPCell();
        descriptionCell.setBorder(Rectangle.NO_BORDER);
        amountCell.setBorder(Rectangle.NO_BORDER);
        for (String[] total : totals) {
            descriptionCell.addElement(new Paragraph(total[0], subFont));
            amountCell.addElement(new Paragraph(total[1], subFont));
        }
        descriptionCell.addElement(new Paragraph("Total", titleFont));
        amountCell.addElement(new Paragraph(String.valueOf(grandTotal), titleFont));
        totalTable.addCell(descriptionCell);
        totalTable.addCell(amountCell);

        document.add(totalTable);
    }

    private static void addTermsAndConditions(Document document, Font subFont) throws DocumentException {
        Paragraph sign = new Paragraph("Authorized Signature", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD));
        sign.setSpacingBefore(50);
        document.add(sign);

        Paragraph terms = new Paragraph("Terms and Conditions", titleFont);
        terms.setSpacingBefore(20);
        document.add(terms);
        document.add(new Paragraph("Thanks for doing business with us!", subFont));
    }
}
