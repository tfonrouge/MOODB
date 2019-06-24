package tech.fonrouge.daemon.build;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;

class FieldModel {
    String fieldName;
    String type;
    String description;
    String label;
    String className;
    String newValue;
    String defaultValue;
    String onAfterChangeValue;
    HashMap<String, String> keyValueItems;
    boolean calculated;
    boolean newFinal;
    boolean validate;
    boolean notNull;
    boolean autoInc;
    boolean notEmpty;
    private boolean valid = true;

    FieldModel(Node node) {
        Node node1;
        keyValueItems = new HashMap<>();

        try {
            fieldName = node.getAttributes().getNamedItem("name").getNodeValue();
            type = node.getLocalName().substring(5);

            node1 = node.getAttributes().getNamedItem("calculated");
            calculated = node1 != null && node1.getNodeValue().equalsIgnoreCase("true");

            node1 = node.getAttributes().getNamedItem("final");
            newFinal = node1 != null && node1.getNodeValue().equalsIgnoreCase("true");

            node1 = node.getAttributes().getNamedItem("validate");
            validate = node1 != null && node1.getNodeValue().equalsIgnoreCase("true");

            node1 = node.getAttributes().getNamedItem("notNull");
            notNull = node1 != null && node1.getNodeValue().equalsIgnoreCase("true");

            node1 = node.getAttributes().getNamedItem("autoInc");
            autoInc = node1 != null && node1.getNodeValue().equalsIgnoreCase("true");

            node1 = node.getAttributes().getNamedItem("notEmpty");
            notEmpty = node1 != null && node1.getNodeValue().equalsIgnoreCase("true");

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
                                    String value;
                                    if (nodeKeyValue.getChildNodes().item(0) != null) {
                                        value = nodeKeyValue.getChildNodes().item(0).getNodeValue();
                                    } else {
                                        value = "\"" + label + "\"";
                                    }
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
                    /* default value */
                    if (node1.getNodeType() == Node.ELEMENT_NODE && node1.getLocalName().contentEquals("defaultValue")) {
                        NodeList nodeNewValue = node1.getChildNodes();
                        if (nodeNewValue.getLength() > 0) {
                            defaultValue = nodeNewValue.item(0).getNodeValue();
                        }
                    }
                    /* onAfterChangeValue */
                    if (node1.getNodeType() == Node.ELEMENT_NODE && node1.getLocalName().contentEquals("onAfterChangeValue")) {
                        NodeList childNodes = node1.getChildNodes();
                        if (childNodes.getLength() > 0) {
                            onAfterChangeValue = childNodes.item(0).getNodeValue();
                        }
                    }
                }
            }

        } catch (NullPointerException e) {
            valid = false;
        }
    }

    String getCast() {
        String cast;
        if (type.contentEquals("TableField")) {
            cast = "<" + className + ">";
        } else {
            cast = "";
        }
        return cast;
    }

    boolean valid() {
        return valid;
    }
}
