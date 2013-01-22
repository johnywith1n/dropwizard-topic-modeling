package org.johnywith1n.util;

import javax.servlet.http.HttpSession;

public class HttpSessionUtil
{
	private static final String accessTokenSessionAttribute = "session attribute: access token";
	private static final String csrfStateSessionAttribute = "session attribute: csrf state";
	private static final String nameSessionAttribute = "session attribute: name";

	public static String getAccessToken(HttpSession session)
	{
		return (String) session.getAttribute(accessTokenSessionAttribute);
	}

	public static void setAccessToken(HttpSession session, String accessToken)
	{
		session.setAttribute(accessTokenSessionAttribute, accessToken);
	}

	public static String getFacebookProfileName(HttpSession session)
	{
		return (String) session.getAttribute(nameSessionAttribute);
	}

	public static void setFacebookProfileName(HttpSession session, String name)
	{
		session.setAttribute(nameSessionAttribute, name);
	}

	public static String getCsrfState(HttpSession session)
	{
		return (String) session.getAttribute(csrfStateSessionAttribute);
	}

	public static void setCsrfState(HttpSession session, String state)
	{
		session.setAttribute(csrfStateSessionAttribute, state);
	}
}
