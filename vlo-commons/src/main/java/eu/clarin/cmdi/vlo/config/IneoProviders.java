package eu.clarin.cmdi.vlo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IneoProviders {
    protected final static Logger LOG = LoggerFactory.getLogger(IneoProviders.class);
    public List<IneoProvider> providers;
    public List<String> keys = new ArrayList<>();
    public Boolean defaultVal = false;

    public IneoProviders(String filePath) {
//        String filePath = "config/ineo-datasets.xml";
        providers = parseIneoConfig(filePath);
        providers.forEach(provider -> keys.add(provider.name));
        defaultVal = getDefaultVal(filePath);
//        providers.forEach(System.out::println);
    }

    @Override
    public String toString() {
        return "IneoProviders{" +
                ", keys=" + keys +
                ", defaultVal=" + defaultVal +
                providers.size() + " providers, first one is: " + providers.get(0) +
                '}';
    }

    public List<IneoProvider> fetchProvidersFromDocument(String tag, Document doc) {
        List<IneoProvider> providers = new ArrayList<>();
        NodeList nList = doc.getElementsByTagName(tag);

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                String name = eElement.getAttribute("name");

                Node profileNode = eElement.getElementsByTagName("profile").item(0);
                String profile = profileNode != null ? profileNode.getTextContent() : null;

                Node levelNode = eElement.getElementsByTagName("level").item(0);
                String level = levelNode != null ? levelNode.getTextContent() : null;

                Node defaultNode = eElement.getElementsByTagName("default").item(0);
                String defaultVal = defaultNode != null ? defaultNode.getTextContent() : null;

                providers.add(new IneoProvider(name, profile, level, defaultVal));
            }
        }

        return providers;
    }

    public Document getDocument(String filePath) {
        try {
            File inputFile = new File(filePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<IneoProvider> parseIneoConfig(String filePath) {
        List<IneoProvider> providers = new ArrayList<>();
        Document doc = getDocument(filePath);
        providers = fetchProvidersFromDocument("provider", doc);
        providers.addAll(fetchProvidersFromDocument("root", doc));
        return providers;
    }

    /**
     * Get the value of attribute named default from root element, whichh should be a boolean value
     */
    public Boolean getDefaultVal(String filePath) {
        Document doc = getDocument(filePath);
        Node root = doc.getDocumentElement();
        String defaultVal = root.getAttributes().getNamedItem("default").getNodeValue();
        return defaultVal.equals("true");
    }


}


