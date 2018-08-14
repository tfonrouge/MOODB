package tech.fonrouge.test;

import org.jetbrains.annotations.NotNull;
import tech.fonrouge.test.data.*;

import java.util.Date;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestMain {

    public static void main(String[] args) {

        Logger logger = Logger.getLogger("org.mongodb.driver");
        logger.setLevel(Level.SEVERE);

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

        if (!customer.field_customerId.find("BOEING")) {
            System.out.println("Error, no customer found.");
            return;
        }

        Invoice invoice = new Invoice();

        inventory.field_name.find();

        String[] strings = {"Mouse", "Keyboard", "Monitor", "Motherboard", "UTP Cable", "Intel CPU"};

        for (int i = 0; i < 10; i++) {
            if (invoice.insert()) {
                invoice.field_docNumber.setValue(i + 1);
                invoice.field_customer.setTable(customer);
                if (invoice.post()) {
                    InvoiceItem_XInvoice invoiceItem = new InvoiceItem_XInvoice(invoice);
                    Random random = new Random();
                    for (int j = 0; j < 3; j++) {
                        if (invoiceItem.insert()) {
                            inventory.field_name.find(strings[random.nextInt(strings.length)]);
                            invoiceItem.field_invItem.setTable(inventory);
                            invoiceItem.field_qty.setValue((double) (random.nextInt(10) + 1));
                            invoiceItem.field_unitPrice.setValue(inventory.field_unitPrice.value());
                            if (!invoiceItem.post()) {
                                System.out.println(invoiceItem.exception().getLocalizedMessage());
                                invoiceItem.cancel();
                            }
                        }
                    }
                } else {
                    System.out.println(invoice.exception().getLocalizedMessage());
                    invoice.cancel();
                }
            }
        }

        if (invoice.find()) {
            while (!invoice.eof()) {
                System.out.println("Invoice #" + invoice.field_docNumber.value());
                System.out.println("    Customer: " + invoice.field_customer.dataField().field_name.value());
                System.out.println("        Date: " + invoice.field_date.value());
                InvoiceItem_XInvoice invoiceItem = new InvoiceItem_XInvoice(invoice);
                if (invoiceItem.find()) {
                    int row = 0;
                    while (!invoiceItem.eof()) {
                        System.out.println("    * #" +
                                invoiceItem.field_invoice.dataField().field_docNumber.value() + " : " +
                                ++row + " = " +
                                invoiceItem.field_invItem.dataField().field_name.value() + " \t" +
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

    private static void createInventory(@NotNull Inventory inventory) {
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

    private static void createCustomers(@NotNull Customer customer) {

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

    private static void createUser(@NotNull User user) {
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
                System.out.println("Error: " + user.exception().getLocalizedMessage());
                user.cancel();
            }
        }
    }
}
