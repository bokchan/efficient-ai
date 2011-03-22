import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XMLHelper {
	private List<RegexExpression> patterns = new ArrayList<RegexExpression>();
	
	public static void main (String[] args) throws ParserConfigurationException, TransformerException, IOException {
		
		XMLHelper helper = new XMLHelper();
		helper.loadRegularExpressions("RegexKillerMoves.xml");
		
		RegexExpression[] ex = new RegexExpression[helper.patterns.size()];
		 helper.patterns.toArray(ex);
		 Arrays.sort(ex);
		 for (RegexExpression r : ex ) {
			 System.out.println(r.getPattern(1).pattern());
		 }
		 
		 helper.writeToFile(ex);	 
	}	
	
	private void loadRegularExpressions(String filename) {
		String dir = System.getProperty("user.dir") + System.getProperty("file.separator") + "trunk" +System.getProperty("file.separator") + "xml" + System.getProperty("file.separator");
		System.out.println(dir);
		File file = new File("C:\\Users\\Andreas\\workspace\\Efficient AI 2\\trunk\\xml\\" + filename);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setNamespaceAware(true);
		DocumentBuilder db = null;
		Document doc = null;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(file);

			XPathFactory factory = XPathFactory.newInstance();
			XPath xpath = factory.newXPath();
			XPathExpression expr = xpath.compile("//expression");
			NodeList expressions = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < ((NodeList) expressions).getLength(); i++) {
				// Now we got a regex expressions
				Node n = expressions.item(i);

				patterns.add(new RegexExpression(
						xpath.evaluate("description", n),
						xpath.evaluate("pattern", n),
						Integer.valueOf(xpath.evaluate("offsetY", n)),
						Integer.valueOf(xpath.evaluate("offsetX", n)),
						Integer.valueOf(xpath.evaluate("modMin", n)),
						Integer.valueOf(xpath.evaluate("modMax", n))));
			}


		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void writeToFile(RegexExpression[] list) throws ParserConfigurationException, TransformerException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		DocumentBuilder db = dbf.newDocumentBuilder();
		DOMImplementation impl = db.getDOMImplementation();
		Document doc = impl.createDocument(null, null,null);

		Element root = doc.createElement("expressions");

		for (RegexExpression ex : list) {
			
			Element item = doc.createElement("expression");
			Node n = doc.createElement("description");
			n.appendChild(doc.createTextNode(ex.getDescription()));
			item.appendChild(n);
			n = doc.createElement("pattern");
			n.appendChild(doc.createTextNode(ex.getPattern().trim()));
			
			item.appendChild(n);

			n = doc.createElement("offsetY");
			n.appendChild(doc.createTextNode(String.valueOf(ex.getOffsetY())));
			item.appendChild(n);

			n = doc.createElement("offsetX");
			n.appendChild(doc.createTextNode(String.valueOf(ex.getOffsetX())));
			item.appendChild(n);

			n = doc.createElement("modMin");
			n.appendChild(doc.createTextNode(String.valueOf(ex.getModMin())));
			item.appendChild(n);

			n = doc.createElement("modMax");
			n.appendChild(doc.createTextNode(String.valueOf(ex.getModMax())));
			item.appendChild(n);

			root.appendChild(item);
		}

		doc.appendChild(root);

		// transform the Document into a String
		DOMSource domSource = new DOMSource(doc);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.ENCODING,"ISO-8859-1");
		transformer.setOutputProperty
		("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		java.io.StringWriter sw = new java.io.StringWriter();
		StreamResult sr = new StreamResult(sw);
		transformer.transform(domSource, sr);
		String xml = sw.toString();


		FileWriter output = new FileWriter("C:\\regexexpressionssorted.xml");
		BufferedWriter out = new BufferedWriter(output);
		//System.out.println(doc.toString());
		out.write(xml);
		out.flush();
		out.close();
	}
}
