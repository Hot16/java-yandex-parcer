/**
 * Created by webmaster on 13.01.16.
 */
package parcer.yandex;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.*;
import java.net.*;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for parsing Yandex websearch results using Yandex.XML
 */
public class YandexParser {
    private final String username;
    private final String password;
    private final String lr;
    private final String filter;
    private final String groupBy;
    private final String page;
    private List<Result> results;


    /**
     * Constructs Parser with login information
     * @param username Username on Yandex.XML
     * @param password Password on Yandex.XML
     */
    public YandexParser(String username, String password, String lr, String filter, String groupBy, String page){
        this.username = username;
        this.password = password;
        this.lr = lr;
        this.filter = filter;
        this.groupBy = groupBy;
        this.page = page;

    }


    /**
     * Make a query to yandex search engine. Should be before any calls to getResults()
     * @param queryText text of query to yandex search engine
     * @throws XMLQueryResultsException if having parsing problems or xml format changes
     * @throws IOException if connectivity problems
     */
    public void query(String queryText)  throws XMLQueryResultsException, IOException {
        String xml = getQueryXML(queryText);
        results = parseXMLResults(xml);
    }

    /**
     * This method returns the list of actual yandex-generated results
     * @return Yandex search results
     */
    public List<Result> getResults() {
        return results;
    }

    private String getQueryXML(String query) throws IOException {
        /*
        // FIXME: switch to online.
        StringBuilder xml = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(
                    new FileReader(new File("myquery.xml")));
            String line;
            while ((line = reader.readLine()) != null) {
                xml.append(line);
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("IO error while reading the file");
            e.printStackTrace();
        }
        return new String(xml);
         */

        try {
                URL url = new URL("https://yandex.ru/search/xml?" +
                        "user=" + username + "&" +
                        "key=" + password + "&" +
                        "lr=" + lr + "&" +
                        "query=" + URLEncoder.encode(query, "UTF-8") + "&" +
                        "filter=" + filter + "&" +
                        "groupby=" + groupBy + "&" +
                        "page=" + page);

                return getPageAnswer(url);

        } catch (MalformedURLException e) {
            System.err.println("Incorrect url! Should be impossible");
            System.exit(0x10);
        } catch (UnsupportedEncodingException e) {
            System.err.println("Utf-8 not supported! Should be impossible.");
            System.exit(0x11);
        }
        return null;
    }

    private String getPageAnswer(URL url) throws IOException {
        URLConnection cnx = url.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(cnx.getInputStream(), Charset.forName("UTF-8")));
        StringBuilder pageHTML = new StringBuilder();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            pageHTML.append(inputLine);
        }
        return new String(pageHTML);
    }

    private List<Result> parseXMLResults(String xml) throws XMLQueryResultsException {
        System.out.println("work -- YandexParser 107");
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document xmldoc = dBuilder.parse(new InputSource(
                    new StringReader(xml)));
            xmldoc.getDocumentElement().normalize();

            // test if erroneus result returned
            NodeList errorNodeList = xmldoc.getElementsByTagName("error");
            if (errorNodeList.getLength() != 0) {
                Element errorElement = (Element) errorNodeList.item(0);
                int errorCode = Integer.parseInt(errorElement.getAttribute("code"));
                String errorString = errorElement.getTextContent();
                throw new XMLQueryResultsException(errorCode, errorString);
            }

            List<Result> results = new ArrayList<>();

            NodeList groupNodeList = xmldoc.getElementsByTagName("group");
            for (int groupId = 0; groupId < groupNodeList.getLength(); ++groupId) {
                Element groupElement = (Element) groupNodeList.item(groupId);
                NodeList docNodeList = groupElement.getElementsByTagName("doc");
                for (int docId = 0; docId < docNodeList.getLength(); ++docId) {
                    Element docElement = (Element) docNodeList.item(docId);

                    // get url
                    Element urlElement = (Element) docElement
                            .getElementsByTagName("url")
                            .item(0);
                    String url = URLDecoder.decode(urlElement.getTextContent(), "utf-8");

                    // get title
                    Element titleElement = (Element) docElement
                            .getElementsByTagName("title")
                            .item(0);
                    String title = titleElement.getTextContent();

                    // get annotation from headline and passages
                    StringBuilder annotationBuilder = new StringBuilder();
                    NodeList headlineNodeList = docElement.getElementsByTagName("headline");
                    if (headlineNodeList.getLength() == 1) {
                        Element headlineElement = (Element) headlineNodeList.item(0);
                        annotationBuilder.append(headlineElement.getTextContent());
                    }

                    NodeList passagesNodeList = docElement.getElementsByTagName("passages");
                    for (int passageId = 0; passageId < passagesNodeList.getLength(); ++passageId) {
                        Element passageElement = (Element) passagesNodeList.item(passageId);
                        annotationBuilder.append(passageElement.getTextContent());
                    }
                    String annotation = new String(annotationBuilder);

                    // get green line
                    Element domainElement = (Element) docElement
                            .getElementsByTagName("domain")
                            .item(0);
                    String domain = domainElement.getTextContent();

                    // remove www
                    String greenLine = domain;
                    if (domain.startsWith("www.")) {
                        greenLine = domain.substring(4);
                    }
                    Result result = new Result(url, title, annotation, greenLine);
                    results.add(result);
                }
            }
            return results;
        } catch (Exception e) {
            throw new XMLQueryResultsException(
                    XMLQueryResultsException.ParseErrorCode, "Cannot parse XML file");
        }
    }


}
