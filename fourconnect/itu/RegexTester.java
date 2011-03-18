package andreas.fourconnect.itu;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class Regextester {

	/**
	 * @param args
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws TransformerException 
	 */
	public static void main(String[] args) throws IOException, ParserConfigurationException, TransformerException {

		loadRegularExpressions("");
	}

	private static void loadRegularExpressions(String filename) throws IOException, ParserConfigurationException, TransformerException 
	{

		FileInputStream fstream = new FileInputStream("C:\\regex_with_variables.txt");
		// Get the object of DataInputStream
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		StringBuilder sb = new StringBuilder();
		while((strLine = br.readLine())!= null ) {
			sb.append(strLine);
		}

		String[] list = sb.toString().split(":"); 
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder db = dbf.newDocumentBuilder();
		 DOMImplementation impl = db.getDOMImplementation();
		Document doc = impl.createDocument(null, null,null);
	
		Element root = doc.createElement("expressions");
		
		
		for (String s : list) {
			String[] vars = s.split(";");
			
			Element item = doc.createElement("expression");
			Node n = doc.createElement("description");
			n.appendChild(doc.createTextNode(vars[0].trim()));
			item.appendChild(n);
			n = doc.createElement("pattern");
			n.appendChild(doc.createTextNode(vars[1].trim()));
			System.out.println("item: " + vars[1]);
			item.appendChild(n);

			n = doc.createElement("offsetY");
			n.appendChild(doc.createTextNode(vars[2].trim()));
			item.appendChild(n);

			n = doc.createElement("offsetX");
			n.appendChild(doc.createTextNode(vars[3].trim()));
			item.appendChild(n);

			n = doc.createElement("modMin");
			n.appendChild(doc.createTextNode(vars[4].trim()));
			item.appendChild(n);

			n = doc.createElement("modMax");
			n.appendChild(doc.createTextNode(vars[5].trim()));
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
        
		
		FileWriter output = new FileWriter("C:\\regexexpressions.xml");
		BufferedWriter out = new BufferedWriter(output);
		//System.out.println(doc.toString());
		out.write(xml);
		out.flush();
		out.close();

	}
}