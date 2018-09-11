package tech.fonrouge.daemon.build;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;

class FieldModel {
    boolean notNullable;
    boolean newDate;
    String fieldName;
    String type;
    boolean calculated;
    boolean validate;
    String description;
    String label;
    boolean required;
    String className;
    HashMap<String, String> keyValueItems;
    String newValue = "";
    private boolean valid = true;

    FieldModel(Node node) {
        Node node1;
        keyValueItems = new HashMap<>();

        try {
            fieldName = node.getAttributes().getNamedItem("name").getNodeValue();
            type = node.getLocalName().substring(5);

            node1 = node.getAttributes().getNamedItem("calculated");
            calculated = node1 != null && node1.getNodeValue().equalsIgnoreCase("true");

            node1 = node.getAttributes().getNamedItem("validate");
            validate = node1 != null && node1.getNodeValue().equalsIgnoreCase("true");

            node1 = node.getAttributes().getNamedItem("required");
            required = node1 != null && node1.getNodeValue().equalsIgnoreCase("true");

            node1 = node.getAttributes().getNamedItem("notNullable");
            notNullable = node1 != null && node1.getNodeValue().equalsIgnoreCase("true");

            node1 = node.getAttributes().getNamedItem("newDate");
            newDate = node1 != null && node1.getNodeValue().equalsIgnoreCase("true");

            node1 = node.getAttributes().getNamedItem("class");
            if (node1 != null) {
                className = node1.getNodeValue();
            }

            node1 = node.getAttributes().getNamedItem("description");
            if (node1 != null) {
                description = node1.getNodeValue();
            }

            node1 = node.getAttributes().getNamedItem("label");
            if (node1 != null) {
                label = node1.getNodeValue();
            }

            NodeList nodeList = node.getChildNodes();

            if (nodeList.getLength() > 0) {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    node1 = nodeList.item(i);

                    /* valid values */
                    if (node1.getNodeType() == Node.ELEMENT_NODE && node1.getLocalName().contentEquals("validValues")) {
                        NodeList nodeListValidValues = node1.getChildNodes();
                        if (nodeListValidValues.getLength() > 0) {
                            for (int j = 0; j < nodeListValidValues.getLength(); j++) {
                                Node nodeKeyValue = nodeListValidValues.item(j);
                                if (nodeKeyValue.getNodeType() == Node.ELEMENT_NODE && nodeKeyValue.getLocalName().contentEquals("value")) {
                                    String label = nodeKeyValue.getAttributes().getNamedItem("label").getNodeValue();
                                    String value = nodeKeyValue.getChildNodes().item(0).getNodeValue();
                                    keyValueItems.put(value, label);
                                }
                            }
                        }
                    }
                    /* new value */
                    if (node1.getNodeType() == Node.ELEMENT_NODE && node1.getLocalName().contentEquals("newValue")) {
                        NodeList nodeNewValue = node1.getChildNodes();
                        if (nodeNewValue.getLength() > 0) {
                            newValue = nodeNewValue.item(0).getNodeValue();
                        }
                    }
                }
            }

        } catch (NullPointerException e) {
            valid = false;
        }
    }

    boolean valid() {
        return valid;
    }
}
