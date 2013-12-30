//
// (c) Copyright 2009 Hewlett-Packard Development Company, L.P.
// Confidential computer software. Valid license from HP required for
// possession, use or copying.  Consistent with FAR 12.211 and 12.212,
// Commercial Computer Software, Computer Software Documentation, and
// Technical Data for Commercial Items are licensed to the U.S. Government
// under vendor's standard commercial license.

package androidx.xml;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.StringReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * A collection of XML-related utility methods.
 *
 * @author Luke Liu
 */
public class XMLUtil {
  private static DocumentBuilderFactory dbf;

  private XMLUtil() {
  }

  private static DocumentBuilderFactory getDocumentBuilderFactory() {
    if (dbf == null) {
      dbf = DocumentBuilderFactory.newInstance();
      // Optional: set various configuration options
      dbf.setNamespaceAware(true);
      dbf.setValidating(false);
    }
    return dbf;
  }


  public static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
    DocumentBuilderFactory dbf = getDocumentBuilderFactory();
    return dbf.newDocumentBuilder();
  }

  public static Document emptyDocument() throws ParserConfigurationException {
    DocumentBuilder db = getDocumentBuilder();
    return db.newDocument();
  }

  public static Document fileToDocument(File file)
      throws java.io.IOException, SAXException, ParserConfigurationException {
    DocumentBuilder dBuilder = getDocumentBuilder();
    return dBuilder.parse(file);
  }

  /**
   * Converts an XML string to a document
   * @param xmlstring
   * @return
   * @throws SAXException
   */
  public static Document stringToDocument(String xmlstring) throws SAXException {
    try {
      DocumentBuilder db = getDocumentBuilder();
      InputSource inputSource = new InputSource(new StringReader(xmlstring));
      return db.parse(inputSource);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e); // should never happen
    } catch (IOException e) {
      throw new RuntimeException(e); // should never happen
    }
  }

  public static Document URLToDocument(URL url)
      throws java.io.IOException, SAXException, ParserConfigurationException {
    URLConnection urlconn = url.openConnection();
    urlconn.setUseCaches(false);
    InputStream istr = urlconn.getInputStream();
    Document doc = null;
    try {
      doc = InputStreamToDocument(istr);
    }
    finally {
      istr.close();
    }
    return doc;
  }

  public static Document InputStreamToDocument(InputStream istr)
      throws java.io.IOException, SAXException, ParserConfigurationException {
    DocumentBuilderFactory dFactory = DocumentBuilderFactory.newInstance();
    dFactory.setNamespaceAware(true);
    DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
    Document xml = dBuilder.parse(istr);
    return xml;
  }

  public static Node[] getChildNodes(Node node, short type) {
    NodeList children = node.getChildNodes();
    if (children == null)
      return new Node[0];
    List<Node> elnodelist = new ArrayList<Node>();
    int i, n = children.getLength();
    Node childnode;
    for (i = 0; i < n; ++i) {
      childnode = children.item(i);
      if (childnode.getNodeType() == type)
        elnodelist.add(childnode);
    }
    Node[] elnodes = new Node[elnodelist.size()];
    elnodelist.toArray(elnodes);
    return elnodes;
  }

  public static String getNodeAttribute(Node node, String name) {
    return getNamedItemNodeValue(node.getAttributes(), name, null);
  }

  public static String getNodeAttribute(Node node, String name, String def) {
    return getNamedItemNodeValue(node.getAttributes(), name, def);
  }

  static String getNamedItemNodeValue(NamedNodeMap attributes, String name, String defvalue) {
    if (attributes == null) {
      return null;
    }
    Node namenode = attributes.getNamedItem(name);
    if (namenode == null)
      return defvalue;
    if (namenode.getNodeValue() == null)
      return defvalue;
    return namenode.getNodeValue();
  }

  public static String getChildContents(Node parentNode, String childName) {
    Node childNode = XMLUtil.findNamedElementNode(parentNode, childName);
    if (childNode == null) {
      return null;
    }
    return XMLUtil.getChildTextNodes(childNode);
  }

  public static String getChildTextNodes(Node node) {
    Node[] textnodes = XMLUtil.getChildNodes(node, Node.TEXT_NODE);
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < textnodes.length; ++i)
      sb.append(textnodes[i].getNodeValue());
    return sb.toString();
  }

  public static Node findNamedElementNode(Node doc, String name) {
    Node[] elnodes = getChildNodes(doc, Node.ELEMENT_NODE);
    int i;
    for (i = 0; i < elnodes.length; ++i) {
      if (elnodes[i].getNodeName().equals(name))
        return elnodes[i];
    }
    return null;
  }

  public static List<Node> findNamedElementNodes(Node doc, String name) {
    Node[] elnodes = getChildNodes(doc, Node.ELEMENT_NODE);
    List<Node> nodes = new ArrayList<Node>();
    int i;
    for (i = 0; i < elnodes.length; ++i) {
      if (elnodes[i].getNodeName().equals(name)) {
        nodes.add(elnodes[i]);
      }
    }
    return nodes;
  }

}
