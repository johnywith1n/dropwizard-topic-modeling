package org.johnywith1n.views;

import javax.ws.rs.core.UriInfo;

public class IndexView extends BaseView
{
	private static final String templatePath = "index.ftl";

	public IndexView(UriInfo uriInfo, String username)
	{
		super(templatePath, uriInfo, username);
	}

}
