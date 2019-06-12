package com.group4.server.controller;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface ServerControllable {

    /**
     * Method to check if xml file is valid according
     * to internal dtd schema.
     *
     * @param xmlFile file, with connection parameters
     * @return true, if file is valid according to dtd schema
     * false, otherwise
     */
    default boolean validateWithDOM(String xmlFile){
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setValidating(true);

            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            documentBuilder.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    System.out.println("Warning: " + exception.getMessage());
                    throw exception;
                }

                @Override
                public void error(SAXParseException exception) throws SAXException {
                    System.out.println("Error: " + exception.getMessage());
                    throw exception;
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    System.out.println("Fatal error: " + exception.getMessage());
                    throw exception;
                }
            });
            documentBuilder.parse(xmlFile);
        } catch (SAXException | ParserConfigurationException | IOException ex) {
            return false;
        }
        return true;
    }

    /**
     * Method to get connection data elements from a xml file.
     *
     * @param inputXmlFile file with the connection data elements inside
     * @return list with the connection data elements inside.
     * @throws ParserConfigurationException in case of documentbuilder creation
     * @throws IOException in case if file cannot be read
     * @throws SAXException in case of any parsing errors from the file
     */
    default List<String> getConnectionData(String inputXmlFile) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(inputXmlFile);

        NodeList connectionParamsList = document.getElementsByTagName("connection-params");
        Node connectionParamsNode = connectionParamsList.item(0);
        Element connectionParamElement = (Element) connectionParamsNode;

        String ip = connectionParamElement.getElementsByTagName("ip").item(0).getTextContent();
        String port = connectionParamElement.getElementsByTagName("port").item(0).getTextContent();

        return new ArrayList<>(Arrays.asList(ip, port));
    }
}
