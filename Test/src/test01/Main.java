package test01;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.bson.types.ObjectId;
import test01.data.customer.Customer;
import test01.data.inventory.Inventory;
import test01.data.invoice.Invoice;
import test01.data.invoice.InvoiceCtrlList;
import test01.data.invoiceItem_Xinvoice.InvoiceItem_XInvoice;
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

        if (invoice.find()) {
            while (!invoice.getEof()) {
                System.out.println("Invoice #" + invoice.field_docNumber.value());
                System.out.println("    Customer: " + invoice.field_customer.syncLinkedTable().field_name.value());
                System.out.println("        DATE: " + invoice.field_date.value());
                InvoiceItem_XInvoice invoiceItem = new InvoiceItem_XInvoice(invoice);
                if (invoiceItem.find()) {
                    int row = 0;
                    while (!invoiceItem.getEof()) {
                        System.out.println("    * #" +
                                invoiceItem.field_invoice.syncLinkedTable().field_docNumber.value() + " : " +
                                ++row + " = " +
                                invoiceItem.field_invItem.syncLinkedTable().field_name.value() + " \t" +
                                invoiceItem.field_qty.value() + " \t" +
                                invoiceItem.field_unitPrice.value() + " total $" +
                                invoiceItem.field_total.value());
                        invoiceItem.next();
                    }
                }
                invoice.next();
            }
        }
    }

    private static void createInventory(Inventory inventory) {
        if (inventory.insert()) {
            inventory.field_itemId.setValue("9198798734");
            inventory.field_name.setValue("Mouse");
            inventory.field_stock.setValue(50.0);
            inventory.field_uom.setValue("pz");
            inventory.field_unitPrice.setValue(2.74);
            if (!inventory.post()) {
                inventory.cancel();
            }
        }
        if (inventory.insert()) {
            inventory.field_itemId.setValue("9182389798");
            inventory.field_name.setValue("Keyboard");
            inventory.field_stock.setValue(75.0);
            inventory.field_uom.setValue("pz");
            inventory.field_unitPrice.setValue(3.5);
            if (!inventory.post()) {
                inventory.cancel();
            }
        }
        if (inventory.insert()) {
            inventory.field_itemId.setValue("9362876234");
            inventory.field_name.setValue("Monitor");
            inventory.field_stock.setValue(90.0);
            inventory.field_uom.setValue("pz");
            inventory.field_unitPrice.setValue(110.2);
            if (!inventory.post()) {
                inventory.cancel();
            }
        }
        if (inventory.insert()) {
            inventory.field_itemId.setValue("1876238764");
            inventory.field_name.setValue("Motherboard");
            inventory.field_stock.setValue(70.0);
            inventory.field_uom.setValue("pz");
            inventory.field_unitPrice.setValue(275.3);
            if (!inventory.post()) {
                inventory.cancel();
            }
        }
        if (inventory.insert()) {
            inventory.field_itemId.setValue("1987238723");
            inventory.field_name.setValue("UTP Cable");
            inventory.field_stock.setValue(500.5);
            inventory.field_uom.setValue("mt");
            inventory.field_unitPrice.setValue(0.25);
            if (!inventory.post()) {
                inventory.cancel();
            }
        }
        if (inventory.insert()) {
            inventory.field_itemId.setValue("91987987293");
            inventory.field_name.setValue("Intel CPU");
            inventory.field_stock.setValue(25.0);
            inventory.field_uom.setValue("pz");
            inventory.field_unitPrice.setValue(210.0);
            if (!inventory.post()) {
                inventory.cancel();
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
                System.out.println("Error: " + user.getException().getLocalizedMessage());
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

        Inventory inventory = new Inventory();
        if (inventory.count() == 0) {
            createInventory(inventory);
        }

        //if (!customer.field_customerId.find("BOEING")) {
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

        inventory.field_name.find();

        String[] strings = {"Mouse", "Keyboard", "Monitor", "Motherboard", "UTP Cable", "Intel CPU"};

        for (int i = 0; i < 10; i++) {
            Random random = new Random();
            if (invoice.insert()) {
                invoice.field_docNumber.setValue(i + 1);
                invoice.field_customer.setValue(idCustomerList.get(random.nextInt(idCustomerList.size())));
                //invoice.field_customer.setValue(customer);
                if (invoice.post()) {
                    InvoiceItem_XInvoice invoiceItem = new InvoiceItem_XInvoice(invoice);
                    for (int j = 0; j < 3; j++) {
                        if (invoiceItem.insert()) {
                            inventory.field_name.find(strings[random.nextInt(strings.length)]);
                            invoiceItem.field_invItem.setValue(inventory);
                            invoiceItem.field_qty.setValue((double) (random.nextInt(10) + 1));
                            invoiceItem.field_unitPrice.setValue(inventory.field_unitPrice.value());
                            if (!invoiceItem.post()) {
                                System.out.println(invoiceItem.getException().getLocalizedMessage());
                                invoiceItem.cancel();
                            }
                        }
                    }
                } else {
                    System.out.println(invoice.getException().getLocalizedMessage());
                    invoice.cancel();
                }
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Test 01");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public void onActionButton(ActionEvent actionEvent) {
        Controller controller = new Controller();

        controller.showList(new InvoiceCtrlList());
    }
}
