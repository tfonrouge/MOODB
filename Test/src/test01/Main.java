package test01;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bson.types.ObjectId;
import test01.data.customer.Customer;
import test01.data.inventoryItem.InventoryItem;
import test01.data.invoice.Invoice;
import test01.data.invoiceItem.xInvoice.InvoiceItem_xInvoice;
import test01.data.user.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    public static void main(String[] args) {

        Logger logger = Logger.getLogger("org.mongodb.driver");
        logger.setLevel(Level.SEVERE);

        buildDataBaseTables();

        findInvoices();

        launch(args);

    }

    private static void findInvoices() {
        Invoice invoice = new Invoice();
        InvoiceItem_xInvoice invoiceItemXInvoice = new InvoiceItem_xInvoice(invoice);

        if (invoice.find()) {
            while (!invoice.getEof()) {
                System.out.println("Invoice #" + invoice.field_docNumber.value());
                System.out.println("    Customer: " + invoice.field_customer.syncedTable().field_name.value());
                System.out.println("        DATE: " + invoice.field_date.value());
                if (invoiceItemXInvoice.find()) {
                    int row = 0;
                    while (!invoiceItemXInvoice.getEof()) {
                        System.out.println("    * #" +
                                invoiceItemXInvoice.field_invoice.syncedTable().field_docNumber.value() + " : " +
                                ++row + " = " +
                                invoiceItemXInvoice.field_invItem.syncedTable().field_name.value() + " \t" +
                                invoiceItemXInvoice.field_qty.value() + " \t" +
                                invoiceItemXInvoice.field_unitPrice.value() + " total $" +
                                invoiceItemXInvoice.field_total.value());
                        invoiceItemXInvoice.next();
                    }
                }
                invoice.next();
            }
        }
    }

    private static void createInventory(InventoryItem inventoryItem) {
        if (inventoryItem.insert()) {
            inventoryItem.field_itemId.setValue("9198798734");
            inventoryItem.field_name.setValue("Mouse");
            inventoryItem.field_stock.setValue(50.0);
            inventoryItem.field_uom.setValue("pz");
            inventoryItem.field_unitPrice.setValue(2.74);
            if (!inventoryItem.post()) {
                inventoryItem.cancel();
            }
        }
        if (inventoryItem.insert()) {
            inventoryItem.field_itemId.setValue("9182389798");
            inventoryItem.field_name.setValue("Keyboard");
            inventoryItem.field_stock.setValue(75.0);
            inventoryItem.field_uom.setValue("pz");
            inventoryItem.field_unitPrice.setValue(3.5);
            if (!inventoryItem.post()) {
                inventoryItem.cancel();
            }
        }
        if (inventoryItem.insert()) {
            inventoryItem.field_itemId.setValue("9362876234");
            inventoryItem.field_name.setValue("Monitor");
            inventoryItem.field_stock.setValue(90.0);
            inventoryItem.field_uom.setValue("pz");
            inventoryItem.field_unitPrice.setValue(110.2);
            if (!inventoryItem.post()) {
                inventoryItem.cancel();
            }
        }
        if (inventoryItem.insert()) {
            inventoryItem.field_itemId.setValue("1876238764");
            inventoryItem.field_name.setValue("Motherboard");
            inventoryItem.field_stock.setValue(70.0);
            inventoryItem.field_uom.setValue("pz");
            inventoryItem.field_unitPrice.setValue(275.3);
            if (!inventoryItem.post()) {
                inventoryItem.cancel();
            }
        }
        if (inventoryItem.insert()) {
            inventoryItem.field_itemId.setValue("1987238723");
            inventoryItem.field_name.setValue("UTP Cable");
            inventoryItem.field_stock.setValue(500.5);
            inventoryItem.field_uom.setValue("mt");
            inventoryItem.field_unitPrice.setValue(0.25);
            if (!inventoryItem.post()) {
                inventoryItem.cancel();
            }
        }
        if (inventoryItem.insert()) {
            inventoryItem.field_itemId.setValue("91987987293");
            inventoryItem.field_name.setValue("Intel CPU");
            inventoryItem.field_stock.setValue(25.0);
            inventoryItem.field_uom.setValue("pz");
            inventoryItem.field_unitPrice.setValue(210.0);
            if (!inventoryItem.post()) {
                inventoryItem.cancel();
            }
        }
    }

    private static void createCustomers(Customer customer) {

        if (customer.insert()) {
            customer.field_customerId.setValue("BOEING");
            customer.field_name.setValue("Boeing");
            customer.field_address.setValue("Chicago, Illinois, United States");
            customer.field_country.setValue("US");
            customer.field_taxId.setValue("012938234876");
            if (!customer.post()) {
                customer.cancel();
            }
        }
        if (customer.insert()) {
            customer.field_customerId.setValue("ORACLE");
            customer.field_name.setValue("Oracle");
            customer.field_address.setValue("Redwood City, California, United States");
            customer.field_country.setValue("US");
            customer.field_taxId.setValue("982437651723");
            if (!customer.post()) {
                customer.cancel();
            }
        }
        if (customer.insert()) {
            customer.field_customerId.setValue("GDINAMICS");
            customer.field_name.setValue("General Dynamics");
            customer.field_address.setValue("Falls Church, Virginia, United States");
            customer.field_country.setValue("US");
            customer.field_taxId.setValue("645234982343");
            if (!customer.post()) {
                customer.cancel();
            }
        }
        if (customer.insert()) {
            customer.field_customerId.setValue("NOKIA");
            customer.field_name.setValue("Nokia");
            customer.field_address.setValue("Espoo, Finland");
            customer.field_country.setValue("FN");
            customer.field_taxId.setValue("98348765123675");
            if (!customer.post()) {
                customer.cancel();
            }
        }
        if (customer.insert()) {
            customer.field_customerId.setValue("GE");
            customer.field_name.setValue("General Electric");
            customer.field_address.setValue("Boston, Massachusetts, United States");
            customer.field_country.setValue("US");
            customer.field_taxId.setValue("7623487765234");
            if (!customer.post()) {
                customer.cancel();
            }
        }
    }

    private static void createUser(User user) {
        if (user.insert()) {
            user.field_name.setValue("Teo");
            user.field_firstName.setValue("Fonrouge");
            user.field_lastName.setValue("Orozco");
            user.field_bday.setValue(new Date());
            user.field_gender.setValue("M");
            user.field_userLevel.setValue("1");
            user.field_password.setValue("drowssap");
            user.field_userId.setValue("9999");
            if (!user.post()) {
                System.out.println("Error: " + user.getMessageWarning());
                user.cancel();
            }
        }
    }

    private static void buildDataBaseTables() {
        User user = new User();
        Customer customer = new Customer();

        if (!user.field_name.find("Teo")) {
            createUser(user);
        }

        if (user.edit()) {
            user.field_lastLogin.setValue(new Date());
            user.field_logCounter.setValue(1 + user.field_logCounter.value(0));
            if (!user.post()) {
                user.cancel();
            }
        }

        if (customer.count() == 0) {
            createCustomers(customer);
        }

        InventoryItem inventoryItem = new InventoryItem();
        if (inventoryItem.count() == 0) {
            createInventory(inventoryItem);
        }

        if (!customer.find()) {
            System.out.println("Error, no customer found.");
            return;
        }

        List<ObjectId> idCustomerList = new ArrayList<>();

        while (!customer.getEof()) {
            idCustomerList.add((ObjectId) customer.field__id.value());
            customer.next();
        }

        Invoice invoice = new Invoice();

        inventoryItem.field_name.find();

        String[] strings = {"Mouse", "Keyboard", "Monitor", "Motherboard", "UTP Cable", "Intel CPU"};

        if (invoice.count() == 0) {
            for (int i = 0; i < 100; i++) {
                Random random = new Random();
                if (invoice.insert()) {
                    invoice.field_customer.setValue(idCustomerList.get(random.nextInt(idCustomerList.size())));
                    invoice.field_daysOfCredit.setValue(random.nextInt(90));
                    if (invoice.post()) {
                        InvoiceItem_xInvoice invoiceItem = new InvoiceItem_xInvoice(invoice);
                        for (int j = 0; j < 3; j++) {
                            if (invoiceItem.insert()) {
                                inventoryItem.field_name.find(strings[random.nextInt(strings.length)]);
                                invoiceItem.field_invItem.setValue(inventoryItem);
                                invoiceItem.field_qty.setValue((double) (random.nextInt(10) + 1));
                                invoiceItem.field_unitPrice.setValue(inventoryItem.field_unitPrice.value());
                                if (!invoiceItem.post()) {
                                    System.out.println(invoiceItem.getMessageWarning());
                                    invoiceItem.cancel();
                                }
                            }
                        }
                    } else {
                        System.out.println(invoice.getMessageWarning());
                        invoice.cancel();
                    }
                }
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Test 01");
        primaryStage.setScene(new Scene(root, -1, -1));
        primaryStage.show();
    }
}
