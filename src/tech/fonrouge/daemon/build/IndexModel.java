package tech.fonrouge.daemon.build;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class IndexModel {
    private String name;
    private String keyField;
    private String masterKeyField;
    private boolean unique;
    private ArrayList<IndexFieldItem> indexFieldItems = new ArrayList<>();

    IndexModel(Node node) {

        Node node1;

        NamedNodeMap nodeMap = node.getAttributes();

        name = nodeMap.getNamedItem("name").getNodeValue();

        node1 = nodeMap.getNamedItem("keyField");
        keyField = node1 == null ? "" : node1.getNodeValue();

        node1 = nodeMap.getNamedItem("masterKeyField");
        masterKeyField = node1 == null ? "" : node1.getNodeValue();

        node1 = nodeMap.getNamedItem("unique");
        unique = node1 != null && node1.getNodeValue().equalsIgnoreCase("true");

        NodeList nodeList = node.getChildNodes();

        if (nodeList.getLength() > 0) {
            for (int i = 0; i < nodeList.getLength(); i++) {
                node1 = nodeList.item(i);
                /* partialFilter */
                if (node1.getNodeType() == Node.ELEMENT_NODE && node1.getLocalName().contentEquals("PartialFilter")) {
                    IndexFieldItem indexFieldItem = new IndexFieldItem();
                    indexFieldItems.add(indexFieldItem);
                    indexFieldItem.logicOperator = node1.getAttributes().item(0).getChildNodes().item(0).getNodeValue();
                    NodeList nodeListValidValues = node1.getChildNodes();
                    if (nodeListValidValues.getLength() > 0) {
                        for (int j = 0; j < nodeListValidValues.getLength(); j++) {
                            Node nodeKeyValue = nodeListValidValues.item(j);
                            if (nodeKeyValue.getNodeType() == Node.ELEMENT_NODE && nodeKeyValue.getLocalName().contentEquals("field")) {
                                String field = nodeKeyValue.getAttributes().getNamedItem("name").getNodeValue();
                                String operator = nodeKeyValue.getAttributes().getNamedItem("operator").getNodeValue();
                                String s = "Filters." + operator.substring(1) + "(\"" + field + "\"";
                                if (nodeKeyValue.getChildNodes().item(0) == null) {
                                    s += ")";
                                } else {
                                    s += ", " + nodeKeyValue.getChildNodes().item(0).getNodeValue() + ")";
                                }
                                indexFieldItem.partialFilterItems.add(s);
                            }
                        }
                    }
                }
            }
        }
    }

    public ArrayList<IndexFieldItem> getIndexFieldItems() {
        return indexFieldItems;
    }

    public String getName() {
        return name;
    }

    public String getKeyField() {
        return keyField;
    }

    public String getMasterKeyField() {
        return masterKeyField;
    }

    public boolean isUnique() {
        return unique;
    }

    class IndexFieldItem {
        private String logicOperator;
        private ArrayList<String> partialFilterItems = new ArrayList<>();

        String getLogicOperator() {
            return logicOperator;
        }

        ArrayList<String> getPartialFilterItems() {
            return partialFilterItems;
        }
    }
}
