package gov.usgs.owi.nldi;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Feed;
import org.apache.abdera.protocol.Response;
import org.apache.abdera.protocol.client.AbderaClient;
import org.apache.abdera.protocol.client.ClientResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.tidy.Tidy;
import org.xml.sax.InputSource;

public class AtomReaderUtil {
	private static final Logger LOG = LoggerFactory.getLogger(AtomReaderUtil.class);

	public static final String GENERIC_ERROR_INNER = "<div><p>An error occured while loading the content</p></div>";
	public static final String GENERIC_ERROR = "<html><body><div>"+GENERIC_ERROR_INNER+"</div></body></html>";

	public static String getAtomFeedContentOnlyAsString(String atomUrl) {
		String content = GENERIC_ERROR_INNER;
		try {
			content = getXPathValue("/html/body/div[1]/div[1]", getAtomFeedAsString(atomUrl).trim());
		} catch (Exception e) {
			LOG.error("Error extracting inner <div> from RSS feed.",e);
		}
		return content;
	}

	public static String getAtomFeedAsString(String atomUrl) throws Exception {
		StringWriter writer = new StringWriter();
		if (getAtomFeed(atomUrl, writer)) {
			return writer.toString();
		} else {
			return GENERIC_ERROR;
		}
	}

	public static boolean getAtomFeed(String atomUrl, Writer output) throws Exception {
		// Default trust manager provider registered for port 443
		AbderaClient.registerTrustManager();
		Abdera abdera = new Abdera();
		AbderaClient client = new AbderaClient(abdera);
		ClientResponse resp = client.get(atomUrl);

		if (resp.getType() != Response.ResponseType.SUCCESS) {
			return false;
		}

		Document<Feed> doc = resp.getDocument();
		Object root = doc.getRoot();

		// if the Feed fails then it returns an empty FOMExtensibleElement instance -> ClassCastException
		String summary = GENERIC_ERROR;

		if (root instanceof Feed) {
			Feed feed = (Feed)root;
			summary = feed.getEntries().get(0).getSummary();
		} else {
			LOG.warn("Error accessing RSS Feed: " + atomUrl);
		}

		Tidy tidy = new Tidy();
		tidy.setXmlOut(true);
		AnchorTagWhiteSpaceRemoverWriterWrapper special = new AnchorTagWhiteSpaceRemoverWriterWrapper();
		tidy.parse(new StringReader(summary), special);

		output.write( special.toString() );
		return true;
	}

	public static String getXPathValue(String xpathExpression, String xmlDocument) throws Exception {
		org.w3c.dom.Document document = stringToDom(xmlDocument);
		XPath xPath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xPath.evaluate(xpathExpression, document, XPathConstants.NODE);
		String value = nodeToString(node);
		return value;
	}

	public static org.w3c.dom.Document stringToDom(String str) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource( new StringReader( str ) );
		org.w3c.dom.Document d = builder.parse(is);
		return d;
	}

	public static String nodeToString(Node node) throws TransformerException {
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(node), new StreamResult(sw));
		} catch (TransformerException te) {
			throw new TransformerException("RSS Feed nodeToString Transformer Exception", te);
		}
		return sw.toString();
	}

}
