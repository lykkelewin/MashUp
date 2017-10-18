package mashup;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.SparkBase.port;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.sun.javafx.scene.paint.GradientUtils.Parser;




public class OurAPI {	
	public static void main(String[] args) throws Exception {
		port(5000);
		SetlistAPI setlist = new SetlistAPI();
		SpotifyAPI spotify = new SpotifyAPI();
		String endPointSpotify = "https://accounts.spotify.com";
		
		
//		setlist.getSetListFromArtist(setlist.setArtist("beyonce") + setlist.setCity("Rio de Janeiro"));

		get("/", (request, response) -> {
//			List<Unicorn> unicorns = storage.fetchUnicorns();
			response.type("application/json");
			response.header("Access-Control-Allow-Origin", "*");
			return "list";
//			return gson.toJson(unicorns);
		});


		get("/search/setlist/:para", (request, response) -> {
			response.type("application/json");
			response.header("Access-Control-Allow-Origin", "*");
			
			String para = request.params(":para");
			
			String buildSetListPara = "";
			for(int i = 1; i<=findTotalCount(para); i++){
				String type = "";
				String value = "";
				
				int andFirst = StringUtils.ordinalIndexOf(para, "&", i);
				int equalFirst = StringUtils.ordinalIndexOf(para, "=", i);
				int andSecond = StringUtils.ordinalIndexOf(para, "&", i+1);
				if(andSecond == -1){
					andSecond = para.length();
				}
				
				type = StringUtils.substring(para, andFirst+1, equalFirst);
				value = StringUtils.substring(para, equalFirst+1, andSecond);

				if(type.matches("artist")){
					buildSetListPara += setlist.setArtist(value);
				}else if(type.matches("city")){
					buildSetListPara += setlist.setCity(value);
				}else if(type.matches("date")){
					buildSetListPara += setlist.setDate(value);
				}
			}
			
			JsonParser parser = new JsonParser();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			
			//Calls setlist-API returns JSON
			JsonElement el = parser.parse(setlist.getMethod(buildSetListPara));
			String jsonString = gson.toJson(el); 
			
			//Create object from JSON
			Setlists setlistCollection = new Gson().fromJson(jsonString, Setlists.class);
			
			//String to return
			String returnToClient = "";
			
			//Used as test value in browser
			// http://localhost:5000/search/setlist/&date=14-10-2017&city=Richmond&artist=foo%20fighters
			System.out.println("---------------------------------");
			//Parse songs and create list of song names
			//Loops through setlist in setlists
			for(int i = 0; i<setlistCollection.setlist.size(); i++){
				//Loops through sets in setlist
				for(int j = 0; j<setlistCollection.setlist.get(i).sets.set.size(); j++){
					returnToClient += " ::--------------------- START \n";
					returnToClient += setlistCollection.setlist.get(i).eventDate + "\n";
					returnToClient += setlistCollection.setlist.get(i).artist.name + "\n";
					returnToClient += setlistCollection.setlist.get(i).venue.name + "\n";
					returnToClient += setlistCollection.setlist.get(i).venue.city.name + "\n";
					returnToClient += setlistCollection.setlist.get(i).venue.city.country.code + "\n";
					returnToClient += "------ Songs: ------ \n";
					//Loops through songs in set
					for(int k = 0; k<setlistCollection.setlist.get(i).sets.set.get(j).song.size(); k++){
						
						returnToClient += setlistCollection.setlist.get(i).artist.name + " - " + setlistCollection.setlist.get(i).sets.set.get(j).song.get(k).name + "\n";
						//Call to Spotify API
						//Artist name:
						System.out.println("::::::::::::::::::::::::::::::");
						System.out.println(setlistCollection.setlist.get(i).artist.name);
						//Song name:
						System.out.println(setlistCollection.setlist.get(i).sets.set.get(j).song.get(k).name);
						System.out.println("::::::::::::::::::::::::::::::");
					}
					returnToClient += " ::--------------------- END \n";
				}
			}
			
//			for(int i = 0; i<setlistCollection.setlist.size(); i++){
//				//Loops through sets in setlist
//				System.out.println("::::::::::::::::::::::::::::::");
//				System.out.println(setlistCollection.setlist.get(i).eventDate);
//				System.out.println(setlistCollection.setlist.get(i).artist.name);
//				System.out.println(setlistCollection.setlist.get(i).venue.name);
//				System.out.println(setlistCollection.setlist.get(i).venue.city.name);
//				System.out.println(setlistCollection.setlist.get(i).venue.city.country.code);
//				System.out.println("::::::::::::::::::::::::::::::");
//			}	
			
			return returnToClient;
		});
		

		/**
		 * Get access token, bearer 60 min
		 */
		get("/spotify/authorization", (request, response) -> {
			response.type("application/json");
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			//BQC1-kpTL_9sIApkRU01nbV2gYz482HvydfO07K9hKDjQgxqI04B1x9gvS9IIsYbmTmiTPBRAcABZFi4TAm-cg
			return gson.toJson(spotify.createRequestForToken());
		});
		/**
		 * Den här metoden funkar inte. Fel token?
		 */
		get("/spotify/", (request, response) -> {
			response.type("application/json");
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
//			return gson.toJson(spotify.userRequest());
			return gson.toJson(spotify.testSearch());

		});
		
		get("/callback/", (req, res) ->{
			
			
		    try {
		        // If you are using maven then your files
		        // will be in a folder called resources.
		        // getResource() gets that folder
		        // and any files you specify.
		        URL url = OurAPI.class.getResource("/callback.html");
		        System.out.println(url);
		        // Return a String which has all
		        // the contents of the file.
		        Path path = Paths.get(url.toURI());
		        return new String(Files.readAllBytes(path), Charset.defaultCharset());
		    } catch (IOException | URISyntaxException e) {
		        // Add your own exception handlers here.
		    }
		    return "";
		});

		

		
		
		
		
		get("/spotify/tracks", (request, response) -> {
			response.type("application/json");
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(spotify.trackSearch());
		});
		
		get("/spotify/test", (request, response) -> {
			response.type("code");
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(spotify.createRequestForAuthorizationToken());
		});
		
		get("/spotify/heavy", (request, response) -> {
			response.type("application/json");
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(spotify.heavyTesting());
		});
		
		get("/spotify/authorization/newPlaylist", (request, response) -> {
			response.type("application/json");
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			return gson.toJson(spotify.createNewPlaylist());
		});
		
//		post("/callback/", (request, response) -> {
//			response.type("application/json");
//			Gson gson = gson.fromJson(request.body(), classOfT)
//		});
		
//		post(endPointSpotify+"/api/token", (request, response) -> {
//			response.type("application/json");
//			Gson gson = gson.fromJson(request.body(), (Type) request.headers());
//			return gson;
//		});
		
		
//		
		
//		get("/spotify/browse", (request, response) -> {
//			response.type("application/json");
//			Gson gson = new GsonBuilder().setPrettyPrinting().create();
//			
////			return gson.toJson(spotify.test(spotify.getMethod(spotify.authorization())));
//			return gson.toJson(spotify.test(spotify.authorization()));
//			
////			JsonElement el = Parser.parseAngle(spotify.test(spotify.authorization()));
////			return gson.toJson(jsonElement)
//		});
//		
//		
//		post("/", (request, response) -> {
//			response.type("application/json");
//			response.header("Access-Control-Allow-Origin", "*");
//
////			Unicorn unicorn = new Gson().fromJson(request.body(), Unicorn.class);
////			storage.addUnicorn(unicorn);
//			return "Post, created a unicorn";
//		});
//		
//		put("/:id", (request, response) -> {
//			response.type("application/json");
//			response.header("Access-Control-Allow-Origin", "*");
//
////			Unicorn unicorn = new Gson().fromJson(request.body(), Unicorn.class);
////			storage.updateUnicorn(unicorn);
//			return "Put: " + request.params(":id");
//		});
//
//		delete("/:id", (request, response) -> {
//			response.type("application/json");
//			response.header("Access-Control-Allow-Origin", "*");
//
////			storage.deleteUnicorn(Integer.parseInt(request.params(":id")));
//			return "Deleted: " + request.params(":id");
//		});
//
	}
	
	public static int findTotalCount(String para){
		
        Pattern pattern = Pattern.compile("&");
	    Matcher  matcher = pattern.matcher(para);
	    
		int count = 0;		
		while (matcher.find())
		    count++;

		return count;
	}
	
}

