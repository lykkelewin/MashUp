package mashup;

import static spark.Spark.*;
import static spark.Spark.port;

import java.util.List;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SpotifyAPI {
//	private final String clientID     = "8985e66f094e45819896ab978b198066";
//	private final String clientSecret = "1e9240c78cae467cb2b2ea34b7b4f547";
//	
	private final String clientID     = "2d9c135438be409bae156633ee90fe59";
	private final String clientSecret = "26c8c22966cf47a88bc0dbf86d3ba123";
	
	private final String redirecURI   = "https%3A%2F%2Fexample.com%2Fcallback";
	
	private final String baseUrlSetlist = "https://api.setlist.fm/rest/1.0/";
	
	
	
	public String setClientId (){
		return "client_id=" + clientID;
	}
	
	public String setResponseType (){

			return "&response_type=code";
	}
	
	public String setRedirectUri (){
		return "&redirect_uri=" + redirecURI;
	}
	
	public String setCity(String city){
		return "&cityName=" + replaceSpace(city);
	}
	
	
	public String replaceSpace(String string){
		return string.replace(" ", "%20");	
	}
	
	public boolean isValidDate(String dateString) {
	    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
	    try {
	        df.parse(dateString);
	        return true;
	    } catch (ParseException e) {
	        return false;
	    }
	}
	
	public String authorization(){
		return setClientId() + setResponseType() + setRedirectUri();
	}
	
	public String getMethod(String para) {
		
		
		String endpoint = "search/artists";
		String whatToReturn = "";
		try {
			

//			URL url = new URL(baseUrlSetlist + endpoint);
//			URL url = new URL("https://api.setlist.fm/rest/1.0/search/artists?artistName=" + artistname + "&p=1&sort=sortName");
			URL url = new URL("https://accounts.spotify.com/authorize/?" + para);
			System.out.println(url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
//			conn.addRequestProperty("x-api-key", apikey);
			
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

}
