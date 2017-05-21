package com.gmail.andrewchouhs.utils;

import java.util.LinkedList;

public class TagMap extends LinkedList<String>
{
	private static final long serialVersionUID = 1L;	
    public TagStatus status;
	
    public enum TagStatus
    {
    	ENABLE,NONE,DISABLE
    }
}
