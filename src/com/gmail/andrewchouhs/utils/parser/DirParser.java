package com.gmail.andrewchouhs.utils.parser;

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
import java.io.File;

public class DirParser
{
	public static void load()
	{
		File dirPathFile = new File(DataStorage.dirPathPath);
		
		if(!dirPathFile.exists())
			return;
		
		try
		{
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(dirPathFile);
			doc.getDocumentElement().normalize();
			
			dirList.clear();
			
			NodeList nodeList = doc.getElementsByTagName("path");
			
			for(int i = 0 ; i < nodeList.getLength() ; i++)
			{
				Node node = nodeList.item(i);
				
				if(node.getNodeType() == Node.ELEMENT_NODE)
					dirList.add(new DirInfo(((Element)node).getAttribute("path")));
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
			File dirPathFile = new File(DataStorage.dirPathPath);
			
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			Element rootElement = doc.createElement("dirpath");
			doc.appendChild(rootElement);
			
			for(DirInfo dirInfo : dirList)
			{
				Element path = doc.createElement("path");
				
				path.setAttribute("path" , dirInfo.path.get());
				rootElement.appendChild(path);
			}
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(new DOMSource(doc) , new StreamResult(dirPathFile));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
