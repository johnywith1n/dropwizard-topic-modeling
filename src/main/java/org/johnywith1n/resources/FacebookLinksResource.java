package org.johnywith1n.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.johnywith1n.articleExtractor.LinkContentExtractor;
import org.johnywith1n.models.Link;
import org.johnywith1n.models.Links;
import org.johnywith1n.util.FacebookLinksRetriever;
import org.johnywith1n.util.HtmlUtil;
import org.johnywith1n.util.HttpClientUtil;
import org.johnywith1n.util.HttpSessionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.metrics.annotation.Timed;

@Path("/facebook_links")
@Produces(MediaType.TEXT_HTML + "; charset=UTF-8")
public class FacebookLinksResource
{

	private final Logger LOGGER = LoggerFactory
	        .getLogger(FacebookLinksResource.class);
	private final HttpClient client;

	@Context
	private UriInfo uriInfo;

	public FacebookLinksResource(HttpClient client)
	{
		this.client = client;
	}

	@GET
	@Timed
	public Response getLinks(@Context HttpServletRequest req)
	        throws ClientProtocolException, IOException
	{
		HttpSession session = req.getSession(true);

		if (HttpSessionUtil.getAccessToken(session) == null)
			return Response.temporaryRedirect(
			        uriInfo.getBaseUriBuilder().path(MainResource.class)
			                .build()).build();
		// String prettyLinksJson = getLinksHTML((String) session
		// .getAttribute(SessionAttributeKeys.accessTokenSessionAttribute));
		String prettyLinks = printLinkArticleContent(getLinks(
		        HttpSessionUtil.getAccessToken(session), 3));

		return Response.ok().entity(prettyLinks).build();
	}

	private List<Links> getLinks(String accessToken, int numberLinksToGet)
	{
		return FacebookLinksRetriever.getLinks(client,
		        "https://graph.facebook.com/me/links?access_token="
		                + accessToken, numberLinksToGet);
	}

	private String printLinkArticleContent(List<Links> linksList)
	{
		List<Link> link = new ArrayList<Link>();
		link.add(new Link());
		link.get(0).setId("0");
		link.get(0).setLink(
		        "http://blog.kissmetrics.com/google-products-that-failed/");
		linksList.get(0).setData(link);

		StringBuilder builder = new StringBuilder();
		Map<String, String> articleMap = LinkContentExtractor.extractArticles(
		        client, linksList);
		int i = 0;
		for (String url : articleMap.keySet())
		{
			builder.append(url + "<br /><br />");
			builder.append(articleMap.get(url) + "<br /><br />");
		}
		return builder.toString();
	}

	private String prettyPrintLinks(List<Links> linksList)
	{
		StringBuilder builder = new StringBuilder();
		for (Links links : linksList)
		{
			for (Link link : links.getData())
			{
				builder.append(link.getLink() + "<br />");
			}
		}
		return builder.toString();
	}

	private String getLinksHTML(String accessToken)
	        throws ClientProtocolException, IOException
	{
		String url = "https://graph.facebook.com/me/links?access_token="
		        + accessToken;

		return HtmlUtil.prettyPrintJsonForHtml(HttpClientUtil
		        .getResponseAsString(client, url));
	}

}
