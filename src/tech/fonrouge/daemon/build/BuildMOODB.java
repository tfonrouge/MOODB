package tech.fonrouge.daemon.build;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class BuildMOODB {

    public void buildClasses(Path pathXml) {
        Document document = validXmlOODBFile(pathXml);
        if (document != null) {
            String className = fileName(pathXml.getFileName());
            Path pathExtLess = convertPathExtensionLess(pathXml);
            System.out.println(pathExtLess);

            FileMaker fileMaker = new FileMaker(pathExtLess, document, className);
            fileMaker.run();
        }
    }

    private Path convertPathExtensionLess(Path path) {
        return Paths.get(
                path.getParent().toString() +
                        File.separator +
                        fileName(path.getFileName()));
    }

    private String fileName(Path path) {
        String fileName = path.getFileName().toString();
        int dot = fileName.lastIndexOf(".");
        return fileName.substring(0, dot);
    }

    private Document validXmlOODBFile(Path pathXml) {
        File file = new File(pathXml.toUri());
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            //builderFactory.setValidating(true);
            builderFactory.setNamespaceAware(true);
            //builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            //builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            builder = builderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        if (builder != null) {
            Document document = null;
            try {
                document = builder.parse(file);
            } catch (SAXException | IOException ignored) {
            }
            if (document != null && document.getDocumentElement().getLocalName().equals("Table")) {
                return document;
            }
        }
        return null;
    }
}
