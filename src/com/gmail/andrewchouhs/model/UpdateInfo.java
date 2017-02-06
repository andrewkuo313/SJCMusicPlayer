package com.gmail.andrewchouhs.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

@XmlRootElement(name = "update")
public class UpdateInfo
{
	private final StringProperty versionProperty = new SimpleStringProperty();
	private final StringProperty dateProperty = new SimpleStringProperty();
	private final StringProperty articleProperty = new SimpleStringProperty();
	
	public UpdateInfo(String version , String date , String article)
	{
		setVersion(version);
		setDate(date);
		setArticle(article);
	}
	
	@XmlAttribute
	public String getVersion()
	{
		return versionProperty.get();
	}
	
	public void setVersion(String version)
	{
		versionProperty.set(version);
	}
	
	public StringProperty getVersionProperty()
	{
		return versionProperty;
	}
	
	@XmlAttribute
	public String getDate()
	{
		return dateProperty.get();
	}
	
	public void setDate(String date)
	{
		dateProperty.set(date);
	}
	
	public StringProperty getDateProperty()
	{
		return dateProperty;
	}
	
	@XmlValue
	public String getArticle()
	{
		return articleProperty.get();
	}
	
	public void setArticle(String article)
	{
		articleProperty.set(article);
	}
	
	public StringProperty getArticleProperty()
	{
		return articleProperty;
	}
	
	public UpdateInfo()
	{
	}
}
