package org.johnywith1n.models;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public class Link
{
	private String id;
	private String link;
	private String text;

	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	public String getLink()
	{
		return link;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	@JsonAnySetter
	public void handleUnknown(String key, Object value)
	{
		// do nothing
	}
}
