package tech.fonrouge.daemon.build;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class FieldFilterModel {

    String fieldName;
    String filterValue;

    FieldFilterModel(Node node) {
        fieldName = node.getAttributes().getNamedItem("field").getNodeValue();
        NodeList childNodes = node.getChildNodes();
        if (childNodes != null && childNodes.getLength() > 0) {
            filterValue = childNodes.item(0).getTextContent();
        }
    }
}
