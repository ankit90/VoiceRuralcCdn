

import java.io.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MessageParser{

	Message m;
	Document dom;


	public MessageParser(){
		//create a list to hold the employee objects
		m = null;
	}

	public Message runExample(InputStream is) {

		parseXmlFile(is);
		parseDocument();
		return m;
		
	}
	
	
	private void parseXmlFile(InputStream is){
		//get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			
			//Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();
			//parse using builder to get DOM representation of the XML file
                        System.out.println("here");
			dom = db.parse(is);
                        System.out.println("here1");
		}catch(ParserConfigurationException pce) {
			pce.printStackTrace();
		}catch(SAXException se) {
			se.printStackTrace();
		}catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}

	
	private void parseDocument(){
		//get the root elememt
		Element docEle = dom.getDocumentElement();
		
		NodeList nl = docEle.getElementsByTagName("Message");
		if(nl != null && nl.getLength() > 0) {

				Element ml = (Element)nl.item(0);

				m = getMessage(ml);
			}
		else
			System.out.println("Invalid XML Format !!");
	}


	/**
	 * I take an employee element and read the values in, create
	 * an Employee object and return it
	 * @param empEl
	 * @return
	 */
	private Message getMessage(Element ml) {
		int size = getIntValue(ml,"size");
		System.out.println("size"+size);
		String fileName = getTextValue(ml,"fileName");
		System.out.println("file"+fileName);
		String title = getTextValue(ml,"Title");
		System.out.println("title"+title);
		String desc = getTextValue(ml,"Desc");
		String tags = getTextValue(ml,"Tags");
		String time_stamp = getTextValue(ml,"Time_stamp");
		String conference_stamp = getTextValue(ml,"Conference_stamp");
                String function = getTextValue(ml,"Function");

		Message mm = new Message(size,fileName,title,desc,tags,time_stamp,conference_stamp,function);
		
		return mm;
	}


	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if(nl != null && nl.getLength() > 0) {
			Element el = (Element)nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

	private int getIntValue(Element ele, String tagName) {
		//in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele,tagName));
	}
	
	
	/**
	 * Iterate through the list and print the 
	 * content to console
	 */
	private String printData(){
		
		return m.toString();
	}
}