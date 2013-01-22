package org.johnywith1n.util;

import com.fasterxml.jackson.core.JsonProcessingException;

public class HtmlUtil
{
	public static String prettyPrintJsonForHtml(String json)
	        throws JsonProcessingException
	{
		return "<pre><script> var obj =" + json + ";"
		        + " var string = JSON.stringify(obj,undefined,4);"
		        + "document.write(string);" + "</script></pre>";
	}
}
