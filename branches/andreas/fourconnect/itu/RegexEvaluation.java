package andreas.fourconnect.itu;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class RegexEvaluation {

	private List<RegexExpression> patterns;

	private Matcher matcher;
	int cols = 7;

	public RegexEvaluation(String xmlFileName) {
		patterns = new ArrayList<RegexExpression>();
		loadRegularExpressions(xmlFileName);
	}

	/***
	 * Returns -1 if no match was found else the column index, 
	 * zero based of the next move    
	 * else returns the column  
	 * @param input
	 * @return
	 */
	public int[] match(String input, int playerid) {
		for (RegexExpression r : patterns) {
			matcher =r.getPattern(playerid).matcher(input); 
			while (matcher.find()) {
				/**** A little hack. The patterns are not independent of the place of occurence
				 * However they can be evaluated based on mod
				 */
				if (matcher.start()%cols  >=r.getModMin() && matcher.start()%cols <= r.getModMax()) {
					return new int[] {matcher.start(), r.getyOffset(), r.getXOffset()};
				}
			}
		}
		return null;
	}

	private void loadRegularExpressions(String filename) {

		File file = new File(filename);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} 
}