package mashup;


import static spark.Spark.*;
import static spark.Spark.port;

import java.util.List;

import com.google.gson.Gson;

import static spark.Spark.get;

import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.wrapper.spotify.*;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.CurrentUserRequest;
import com.wrapper.spotify.methods.PlaylistCreationRequest;
import com.wrapper.spotify.methods.Request;
import com.wrapper.spotify.methods.TrackSearchRequest;
import com.wrapper.spotify.methods.UserRequest;
import com.wrapper.spotify.methods.UserRequest.Builder;
import com.wrapper.spotify.methods.authentication.AuthorizationCodeGrantRequest;
import com.wrapper.spotify.methods.authentication.AuthorizationURLRequest;
import com.wrapper.spotify.methods.authentication.ClientCredentialsGrantRequest;
import com.wrapper.spotify.models.AuthorizationCodeCredentials;
import com.wrapper.spotify.models.ClientCredentials;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.Playlist;
import com.wrapper.spotify.models.Track;
import com.wrapper.spotify.models.User;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import spark.Route;
import spark.utils.Assert.*;



public class SpotifyAPI {

	//	private final String clientId = "2d9c135438be409bae156633ee90fe59";
	//	private final String clientSecret = "26c8c22966cf47a88bc0dbf86d3ba123";
	//	private final String redirectURI = "https%3A%2F%2Fexample.com%2Fcallback";
	private final String clientId = "8985e66f094e45819896ab978b198066";
	private final String clientSecret = "1e9240c78cae467cb2b2ea34b7b4f547";
	private final String redirectURI = "http://localhost:5000/callback/";
	private final String responseType = "code";
	private String cc;



	final Api api = Api.builder().clientId(clientId)
			.clientSecret(clientSecret)
			.redirectURI(redirectURI)
			.build();

	public SpotifyAPI(){
		final List<String> scopes = Arrays.asList("user-read-private", "user-read-email", "playlist-read-private");
		final String state = "someExpectedStateString";
		String authorizeURL = api.createAuthorizeURL(scopes, state);
		System.out.println(authorizeURL);

		//			String code = redirectURI;

	}
	//http%3A%2F%2Flocalhost%3A5000%2Fcallback%2F

	public ClientCredentials createRequestForToken() {
		final ClientCredentialsGrantRequest request = api.clientCredentialsGrant().basicAuthorizationHeader(clientId, clientSecret).build();
		ClientCredentials clientCredentialsResult = new ClientCredentials();
		try {
			clientCredentialsResult = request.get();
			System.out.println("I got " + 	clientCredentialsResult.getAccessToken().toString());
			cc = clientCredentialsResult.getAccessToken();
		} catch (Exception e){
			System.out.println("Something went wrong!" + e.getMessage());
		}
		return clientCredentialsResult;
	}

	public URL heavyTesting() throws IOException{
		final List<String> scopes = Arrays.asList("user-read-private", "user-read-email", "playlist-read-private");
		final String state = "someExpectedStateString";
		String authorizeURL = api.createAuthorizeURL(scopes, state);
		System.out.println(authorizeURL);



		URL url = new URL(authorizeURL);
		url.openStream().read();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		System.out.println(conn.getRequestProperties());
		conn.connect();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));
		String output;
		String info = "";
		URL code = null;

		while((output = br.readLine())!=null){
			info += output;
			if(conn.getURL() != url){
				code = conn.getURL();
			}
			System.out.println(info);
			System.out.println(code);

		}

		conn.disconnect();
		return code;
	}

	public AuthorizationCodeCredentials createRequestForAuthorizationToken() throws IOException{


		final AuthorizationCodeGrantRequest request = api.authorizationCodeGrant(cc).basicAuthorizationHeader(clientId, clientSecret).redirectUri(redirectURI).build();
		AuthorizationCodeCredentials authorizationCodeResult = new AuthorizationCodeCredentials();
		try {
			authorizationCodeResult = request.get();
			System.out.println("I got " + 	authorizationCodeResult.getAccessToken().toString());
			//			cc = authorizationCodeResult.getAccessToken();
		} catch (Exception e){
			System.out.println("Something went wrong!" + e.getMessage());
		}
		return authorizationCodeResult;
	}

	public String tryToPrint() throws IOException{

		String endpoint = "search/artists";
		String whatToReturn = "";
		try {

			//			URL url = new URL(baseUrlSetlist + endpoint);
			//			URL url = new URL("https://api.setlist.fm/rest/1.0/search/artists?artistName=" + artistname + "&p=1&sort=sortName");

			URL url = new URL("https://accounts.spotify.com/authorize" + clientId + responseType + redirectURI);
			System.out.println(url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			System.out.println(conn.getResponseMessage());

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				whatToReturn += output;
				//				System.out.println(output);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}		

		return whatToReturn;
	}









	//	api.authorizationCodeGrant(code)
	//	final List<String> scopes = Arrays.asList("user-read-private", "user-read-email", "playlist-read-private");
	//	final String state = "someExpectedStateString";
	//	String authorizeURL = api.createAuthorizeURL(scopes, state);
	//	System.out.println(authorizeURL);
	//
	//	return authorizeURL;


	public String autUrl(){

		final List<String> scopes = Arrays.asList("user-read-private", "user-read-email", "playlist-read-private");
		final String state = "someExpectedStateString";
		String authorizeURL = api.createAuthorizeURL(scopes, state);

		return authorizeURL;


	}

	public Playlist createNewPlaylist(){
		api.setAccessToken(cc);
		final PlaylistCreationRequest request = api.createPlaylist("lykkelewin", "APITest").publicAccess(true).build();
		Playlist playlist = new Playlist();
		try {
			playlist = request.get();
			System.out.println("You just created this playlist!");
			System.out.println("Its title is " + playlist.getName());
		} catch (Exception e){
			System.out.println("Something went wrong" + e.getMessage());
		}
		return playlist;

	}

	public void something(){
		try {

			URL url = new URL("https://accounts.spotify.com/api/token");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			String input = "/grant_type" + "/code" + "/redirectURI";

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			conn.disconnect();

		} catch (MalformedURLException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}
	}



	public User userRequest() {

		System.out.println(cc);

		final CurrentUserRequest request = api.getMe().accessToken(cc).build();
		User userRequestResult = new User();
		try{
			userRequestResult = request.get();
			System.out.println("I got " + userRequestResult.getDisplayName() + " results!");
		} catch (Exception e){
			System.out.println("Something went wrong!" + e.getMessage());
		}

		return userRequestResult;
	}

	public String testSearch(){
		api.setAccessToken(cc);
		final TrackSearchRequest request = api.searchTracks("Mr. Brightside").market("US").build();

		try {
		   final Page<Track> trackSearchResult = request.get();
//		   System.out.println("I got " + trackSearchResult.getTotal() + " results!");
		} catch (Exception e) {
		   System.out.println("Something went wrong!" + e.getMessage());
		}
		
		return "";
	}
	
	
	public Page<Track> trackSearch() throws IOException, WebApiException{
		api.setAccessToken(cc);
		final TrackSearchRequest request = api.searchTracks("Mr. Brightside").market("SE").build();
		Page<Track> trackSearchResult = new Page<Track>();

		try {
			trackSearchResult = request.get();
			System.out.println("I got " + trackSearchResult.getTotal() + " results!");
		} catch (Exception e) {
			System.out.println("Something went wrong!" + e.getMessage());
		}
		return trackSearchResult;

	}


}
