package org.johnywith1n.resources;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.johnywith1n.util.HttpClientUtil;
import org.johnywith1n.util.HttpSessionUtil;
import org.johnywith1n.views.IndexView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.yammer.metrics.annotation.Timed;

@Path("/")
@Produces(MediaType.TEXT_HTML)
public class MainResource
{
	private final Logger LOGGER = LoggerFactory.getLogger(MainResource.class);
	private final String appId;
	private final String appSecret;
	private final HttpClient client;
	private final ObjectMapper mapper = new ObjectMapper();
	private static final String redirect_URL = "http://localhost:8080/service/facebook_login";

	@Context
	private UriInfo uriInfo;

	public MainResource(String appId, String appSecret, HttpClient client)
	{
		this.appId = appId;
		this.appSecret = appSecret;
		this.client = client;
	}

	@GET
	public IndexView getHomePage(@Context HttpServletRequest req)
	{
		HttpSession session = req.getSession(true);

		return new IndexView(uriInfo,
		        HttpSessionUtil.getFacebookProfileName(session));
	}

	public String facebookLogin(HttpSession session)
	        throws UnsupportedEncodingException
	{
		String facebook_state = Integer.toString((new Random()).nextInt());
		String dialogUrl = "https://www.facebook.com/dialog/oauth?client_id="
		        + appId + "&redirect_uri="
		        + URLEncoder.encode(redirect_URL, "UTF-8") + "&state="
		        + facebook_state + "&scope=read_stream";

		HttpSessionUtil.setCsrfState(session, facebook_state);
		return "<script> top.location.href='" + dialogUrl + "'</script>";
	}

	@Path("login")
	@Produces(MediaType.TEXT_HTML)
	@GET
	@Timed
	public String login(@Context HttpServletRequest req)
	        throws UnsupportedEncodingException
	{
		return facebookLogin(req.getSession(true));
	}

	@Path("logout")
	@Produces(MediaType.TEXT_HTML)
	@GET
	@Timed
	public Response logout(@Context HttpServletRequest req)
	        throws UnsupportedEncodingException
	{
		HttpSession session = req.getSession(true);

		HttpSessionUtil.setAccessToken(session, null);
		HttpSessionUtil.setFacebookProfileName(session, null);

		return Response.temporaryRedirect(
		        uriInfo.getBaseUriBuilder().path(MainResource.class).build())
		        .build();
	}

	@Path("facebook_login")
	@Produces(MediaType.TEXT_HTML)
	@GET
	@Timed
	public Response getAccessToken(
	        @QueryParam("error_reason") Optional<String> errorReason,
	        @QueryParam("state") Optional<String> state,
	        @QueryParam("code") Optional<String> code,
	        @Context HttpServletRequest req) throws ClientProtocolException,
	        IOException, URISyntaxException
	{
		HttpSession session = req.getSession(true);

		if (errorReason.isPresent())
			return getErrorResponse("Error retrieving code");
		else if (HttpSessionUtil.getCsrfState(session) == null
		        || !HttpSessionUtil.getCsrfState(session)
		                .equals(state.orNull()))
			return getErrorResponse("The state does not match. You may be a victim of CSRF.");

		String accessToken = getAccessToken(code.get());

		if (accessToken == null)
			return getErrorResponse("Unable to parse retrieved accessToken");

		Map<String, String> json = testAccessToken(accessToken);
		String name = json.get("name");
		if (name == null)
			return getErrorResponse("Unable to use token");
		else
		{
			HttpSessionUtil.setFacebookProfileName(session, name);
			HttpSessionUtil.setAccessToken(session, accessToken);

			return Response.temporaryRedirect(
			        uriInfo.getBaseUriBuilder().path(MainResource.class)
			                .build()).build();
		}
	}

	private Response getErrorResponse(String error)
	{
		return Response.status(401).entity(error).type(MediaType.TEXT_HTML)
		        .build();
	}

	private String getAccessToken(String code) throws IOException
	{
		String tokenUrl = "https://graph.facebook.com/oauth/access_token?"
		        + "client_id=" + this.appId + "&redirect_uri="
		        + URLEncoder.encode(redirect_URL, "UTF-8") + "&client_secret="
		        + appSecret + "&code=" + code;

		return parseAccessToken(HttpClientUtil.getResponseAsString(client,
		        tokenUrl));
	}

	private String parseAccessToken(String response)
	{
		Pattern pattern = Pattern.compile("access_token=(.+)&expires=(.*)");
		Matcher matcher = pattern.matcher(response);
		if (matcher.matches())
			return matcher.group(1);
		else
		{
			LOGGER.error("Unable to parse access token. Received: " + response);
			return null;
		}

	}

	@SuppressWarnings("unchecked")
	private Map<String, String> testAccessToken(String accessToken)
	        throws ClientProtocolException, IOException
	{
		String json = HttpClientUtil.getResponseAsString(client,
		        "https://graph.facebook.com/me?access_token=" + accessToken);
		return (Map<String, String>) mapper.readValue(json, Map.class);
	}

}
