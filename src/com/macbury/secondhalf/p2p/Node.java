package com.macbury.secondhalf.p2p;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

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

import org.w3c.dom.Document;
import org.w3c.dom.Element;


public class Node {
  private String name;
  private String value = "";
  
  private String id;
  private ArrayList<Node> childrens;
  private Node parent;
  
  public Node(String tag) {
    setName(tag);
    childrens = new ArrayList<Node>();
  }
  
  public Node getParent() {
    return parent;
  }
  public void setParent(Node parent) {
    this.parent = parent;
  }
  public ArrayList<Node> getChildrens() {
    return childrens;
  }
  public void setChildrens(ArrayList<Node> childrens) {
    this.childrens = childrens;
  }
  
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  
  public Node addChild(Node node) {
    node.setParent(this);
    childrens.add(node);
    
    return node;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getValue() {
    return value;
  }
  public void setValue(String value) {
    this.value = value;
  }
  
  public String toString() {
    return NodeTransformer.nodeToXml(this);
  }
  
  public void addParam(String key, String value) {
    Node node = new Node(key);
    node.setValue(value);
    addChild(node);
  }
  
  public String getParam(String key) {
    for (Node node : this.childrens) {
      if (node.getName().equals(key)) {
        return node.getValue();
      }
    }
    return null;
  }
  
}
