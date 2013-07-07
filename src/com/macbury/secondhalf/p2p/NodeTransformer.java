package com.macbury.secondhalf.p2p;

import java.io.ByteArrayOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class NodeTransformer {
  public static String nodeToXml(Node node) {
    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder;
    try {
      docBuilder    = docFactory.newDocumentBuilder();
      Document doc  = docBuilder.newDocument();
      doc.setXmlStandalone(true);
      
      Element rootElement = doc.createElement(node.getName());
      doc.appendChild(rootElement);
      
      Attr attr = doc.createAttribute("id");
      attr.setValue(node.getId());
      rootElement.setAttributeNode(attr);
      
      if (Action.class.isInstance(node)) {
        Action action = (Action)node;
        attr = doc.createAttribute("type");
        attr.setValue(action.getType());
        rootElement.setAttributeNode(attr);
      }
      
      if (Response.class.isInstance(node)) {
        Response response = (Response)node;
        attr = doc.createAttribute("status");
        attr.setValue(response.getStatus());
        rootElement.setAttributeNode(attr);
        
        attr = doc.createAttribute("for");
        attr.setValue(response.getForType());
        rootElement.setAttributeNode(attr);
      } 
      
      for (Node child : node.getChildrens()) {
        Element childElement = doc.createElement(child.getName());
        childElement.setTextContent(child.getValue());
        rootElement.appendChild(childElement);
      }
      
      doc.normalizeDocument();
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer               = transformerFactory.newTransformer();
      DOMSource source                      = new DOMSource(doc);
      ByteArrayOutputStream output          = new ByteArrayOutputStream();
      StreamResult result                   = new StreamResult(output);
      
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      transformer.transform(source, result);
      return new String(output.toByteArray());
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (TransformerConfigurationException e) {
      e.printStackTrace();
    } catch (TransformerException e) {
      e.printStackTrace();
    }
    return null;
  }
}
