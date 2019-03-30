package tech.fonrouge.daemon.build;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ChildTableModel {
    private String name;
    private String className;

    ChildTableModel(Node node) {

        NamedNodeMap nodeMap = node.getAttributes();

        name = nodeMap.getNamedItem("name").getNodeValue();

        className = nodeMap.getNamedItem("class").getNodeValue();
    }

    public String getName() {
        return name;
    }

    public String getClassName() {
        return className;
    }
}