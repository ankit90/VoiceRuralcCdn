import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

//For jdk1.5 with built in xerces parser
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.io.DataOutputStream;
import java.net.Socket;

//For JDK 1.3 or JDK 1.4  with xerces 2.7.1
//import org.apache.xml.serialize.XMLSerializer;
//import org.apache.xml.serialize.OutputFormat;


public class MessageCreator{

	//No generics
	String message;
	Document dom;

	public MessageCreator(String msg) {
		message = msg;
		loadData();
		//Get a DOM object
		createDocument();
	}


	public void runExample(DataOutputStream out){
		System.out.println("Started .. ");
		createDOMTree();
		printToFile(out);
		System.out.println("Generated file successfully.");
	}

	private void loadData(){
		this.message = "Message sent succesfully";
	}

	/**
	 * Using JAXP in implementation independent manner create a document object
	 * using which we create a xml tree in memory
	 */
	private void createDocument() {

		//get an instance of factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
		//get an instance of builder
		DocumentBuilder db = dbf.newDocumentBuilder();

		//create an instance of DOM
		dom = db.newDocument();

		}catch(ParserConfigurationException pce) {
			//dump it
			System.out.println("Error while trying to instantiate DocumentBuilder " + pce);
			System.exit(1);
		}

	}

	/**
	 * The real workhorse which creates the XML structure
	 */
	private void createDOMTree(){

		//create the root element <Books>
		Element rootEle = dom.createElement("Temp");
		dom.appendChild(rootEle);

		StatusMessage sm = new StatusMessage(this.message);

			Element smEle = createSMElement(sm);
			rootEle.appendChild(smEle);

	}

	/**
	 * Helper method which creates a XML element <Book>
	 * @param b The book for which we need to create an xml representation
	 * @return XML element snippet representing a book
	 */
	private Element createSMElement(StatusMessage sm){

		Element smEle = dom.createElement("Message");
		Text smText = dom.createTextNode(sm.getStatus());

		smEle.appendChild(smText);

		return smEle;

	}

	/**
	 * This method uses Xerces specific classes
	 * prints the XML document to file.
     */
	private void printToFile(DataOutputStream out){

		try
		{
			//print
			OutputFormat format = new OutputFormat(dom);
			format.setIndenting(true);
			//to generate output to console use this serializer
			XMLSerializer serializer = new XMLSerializer(out, format);
                        serializer.serialize(dom);
                        out.flush();
                        out.close();

			//to generate a file output use fileoutputstream instead of system.out
			//XMLSerializer serializer = new XMLSerializer(
			//new FileOutputStream(new File("sm.xml")), format);

			

		} catch(IOException ie) {
		    ie.printStackTrace();
		}
	}
}

