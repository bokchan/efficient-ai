package fourconnect;

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
	public static enum MATCH_RESULT_STATE {PLAYER1WON, PLAYER2WON, PLAYER1THREEINAROW, PLAYER2THREEINAROW, PLAYER1KILLERMOVE, PLAYER2KILLERMOVE, TIE, UNDEFINED, PLAYER1PREKILLER, PLAYER2PREKILLER};
	public static enum MATCH_TYPE {FOUR_IN_A_ROW, THREE_IN_A_ROW, KILLER_MOVE, BASIC};
	private List<RegexExpression> patterns;
	private Matcher matcher;
	int cols = 7;
	private MATCH_TYPE matchtype;

	public enum Winner {

		PLAYER1, PLAYER2, TIE, NOT_FINISHED
	}

	public RegexEvaluation(String xmlFileName, MATCH_TYPE type) {
		patterns = new ArrayList<RegexExpression>();
		loadRegularExpressions(xmlFileName);
		this.matchtype = type;
	}

	/***
	 * Returns -1 if no match was found else the column index,
	 * zero based of the next move
	 * else returns the column
	 * @param input
	 * @return
	 */
	public RegexResult match(String input, int playerid) {
		for (RegexExpression r : patterns) {
			matcher = r.getPattern(playerid).matcher(input);
			while (matcher.find()) {
				/**** A little hack. The patterns are not independent of the place of occurence
				 * However they can be evaluated based on mod value of the start index against the number of columns
				 * 8%7 = 1, 
				 * 789
				 * 0123456
				 */
				if (matcher.start() % cols >= r.getModMin() && matcher.start() % cols <= r.getModMax()) {
					//GameHelper.Trace(true, "Matchtype in regex: " + matchtype.name());
					return new RegexResult(matcher.start(), r.getOffsetY(), r.getOffsetX(),getMatchResultState(playerid), playerid);
				}
			}
		}
		return null;
	}

	private void loadRegularExpressions(String filename) {
		//GameHelper.Trace(false, "Loading regex patterns");
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

	private MATCH_RESULT_STATE getMatchResultState(int playerid) {
		MATCH_RESULT_STATE resultState = MATCH_RESULT_STATE.UNDEFINED;
		switch (matchtype) {
		case FOUR_IN_A_ROW:
			resultState = (playerid == 1) ? MATCH_RESULT_STATE.PLAYER1WON : MATCH_RESULT_STATE.PLAYER2WON;
			break;
		case THREE_IN_A_ROW : 
			resultState = (playerid == 1) ? MATCH_RESULT_STATE.PLAYER1THREEINAROW : MATCH_RESULT_STATE.PLAYER2THREEINAROW;
			break;
		case KILLER_MOVE : 
			resultState  = (playerid == 1) ? MATCH_RESULT_STATE.PLAYER1KILLERMOVE : MATCH_RESULT_STATE.PLAYER2KILLERMOVE;
			break;
		case BASIC : 
			resultState  = (playerid == 1) ? MATCH_RESULT_STATE.PLAYER1PREKILLER : MATCH_RESULT_STATE.PLAYER2PREKILLER;
			break;
			default : 
			 resultState = MATCH_RESULT_STATE.UNDEFINED;
			break;
		}
		
		return resultState;
	}
}
