package com.gmail.andrewchouhs.utils.parser;

import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.gmail.andrewchouhs.model.DirInfo;
import com.gmail.andrewchouhs.storage.DataStorage;
import static com.gmail.andrewchouhs.storage.PropertyStorage.dirList;

public class DirParser
{
	private static final File file = new File(DataStorage.dirPathPath);
	
	public static void load()
	{
		if(!file.exists())
			return;
		
		//應修改 XML 讓其更簡短。
		try
		{
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			doc.getDocumentElement().normalize();
			
			dirList.clear();
			
			NodeList nodeList = doc.getElementsByTagName("path");
			
			for(int i = 0 ; i < nodeList.getLength() ; i++)
			{
				Node node = nodeList.item(i);
				
				if(node.getNodeType() == Node.ELEMENT_NODE)
					dirList.add(new DirInfo(((Element)node).getTextContent()));
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void save()
	{
		try
		{
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			Element rootElement = doc.createElement("dirpath");
			doc.appendChild(rootElement);
			
			for(DirInfo dirInfo : dirList)
			{
				Element path = doc.createElement("path");
				
				path.appendChild(doc.createTextNode(dirInfo.path.get()));
				rootElement.appendChild(path);
			}
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(new DOMSource(doc) , new StreamResult(file));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
