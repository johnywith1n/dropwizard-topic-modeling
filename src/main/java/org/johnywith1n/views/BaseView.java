package org.johnywith1n.views;

import javax.ws.rs.core.UriInfo;

import org.johnywith1n.resources.MainResource;

import com.yammer.dropwizard.views.View;

public abstract class BaseView extends View
{
	private final String homeURL;

	private final String topicModelingURL;

	private final String aboutURL;

	private final String contactURL;

	private final String loginSection;

	public BaseView(String templateName, UriInfo uriInfo, String username)
	{
		super(templateName);

		homeURL = uriInfo.getBaseUriBuilder().path(MainResource.class).build()
		        .toString();
		topicModelingURL = "#";
		aboutURL = "#";
		contactURL = "#";
		if (username == null)
		{
			String login = uriInfo.getBaseUriBuilder()
			        .path(MainResource.class, "login").build().toString();
			loginSection = "<a href=\"" + login + "\"> Login </a>";
		}
		else
		{
			String logout = uriInfo.getBaseUriBuilder()
			        .path(MainResource.class, "logout").build().toString();
			loginSection = "Not " + username + "? <a href =\"" + logout
			        + "\">Log out</a>";
		}
	}

	public String getHomeURL()
	{
		return homeURL;
	}

	public String getTopicModelingURL()
	{
		return topicModelingURL;
	}

	public String getAboutURL()
	{
		return aboutURL;
	}

	public String getContactURL()
	{
		return contactURL;
	}

	public String getLoginSection()
	{
		return loginSection;
	}

}
