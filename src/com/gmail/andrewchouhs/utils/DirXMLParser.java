package com.gmail.andrewchouhs.utils;

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
import com.gmail.andrewchouhs.Storage;
import com.gmail.andrewchouhs.model.DirInfo;

public class DirXMLParser
{
	private static File file = new File(System.getenv("APPDATA") + "\\SJCMusicPlayer\\DirectoryPath.xml");
	
	public static void load()
	{
		if(!file.exists())
			return;
		try
		{
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
			doc.getDocumentElement().normalize();
			Storage.dirList.clear();
			NodeList nodeList = doc.getElementsByTagName("path");
			for(int i = 0 ; i < nodeList.getLength() ; i++)
			{
				Node node = nodeList.item(i);
				if(node.getNodeType() == Node.ELEMENT_NODE)
					Storage.dirList.add(new DirInfo(((Element)node).getTextContent()));
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
			Element dirPath = doc.createElement("dirpath");
			doc.appendChild(dirPath);
			for(DirInfo dirInfo : Storage.dirList)
			{
				Element path = doc.createElement("path");
				path.appendChild(doc.createTextNode(dirInfo.path.get()));
				dirPath.appendChild(path);
			}
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(new DOMSource(doc), new StreamResult(file));
		}
		catch(Exception e)
		{
		}
	}
}
