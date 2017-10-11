package mashup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SetlistAPI {
	private final String apikey  = "2b417377-7108-44f6-8bb0-9c3a42419c0c";
	private final String baseUrlSetlist = "https://api.setlist.fm/rest/1.0/";
		
	public String setVenue(String venue){
		return "&venueName=" + replaceSpace(venue);
	}
	
	public String setDate(String date){
		if(isValidDate(date))
			return "&date=" + date;
		else
			return "";
	}
	
	public String setArtist(String artist){
		return "&artistName=" + replaceSpace(artist);
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
	
	public String getMethod(String para) {
		String endpoint = "search/artists";
		String whatToReturn = "";
		try {
			
//			URL url = new URL(baseUrlSetlist + endpoint);
//			URL url = new URL("https://api.setlist.fm/rest/1.0/search/artists?artistName=" + artistname + "&p=1&sort=sortName");
			
			URL url = new URL("https://api.setlist.fm/rest/1.0/search/setlists?p=1" + para);
			System.out.println(url);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.addRequestProperty("x-api-key", apikey);
			
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
