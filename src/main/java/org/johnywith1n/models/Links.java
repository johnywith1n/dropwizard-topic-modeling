package org.johnywith1n.models;

import java.util.List;
import java.util.Map;

public class Links
{
	private List<Link> data;
	private Map<String, String> paging;
	private static final String NEXT_URL_KEY = "next";

	public List<Link> getData()
	{
		return data;
	}

	public Map<String, String> getPaging()
	{
		return paging;
	}

	public void setData(List<Link> data)
	{
		this.data = data;
	}

	public void setPaging(Map<String, String> paging)
	{
		this.paging = paging;
	}

	public String getNextPageUrl()
	{
		if (paging != null)
			return paging.get(NEXT_URL_KEY);
		return null;
	}

}
