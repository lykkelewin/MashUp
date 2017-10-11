package mashup;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;
import static spark.SparkBase.port;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class OurAPI {
	Gson gson = new Gson();

	public static void main(String[] args) throws Exception {
		port(5000);
		SetlistAPI setlist = new SetlistAPI();
		SpotifyAPI spotify = new SpotifyAPI();
		
//		setlist.getSetListFromArtist(setlist.setArtist("beyonce") + setlist.setCity("Rio de Janeiro"));

		get("/", (request, response) -> {
//			List<Unicorn> unicorns = storage.fetchUnicorns();
			response.type("application/json");
			response.header("Access-Control-Allow-Origin", "*");
			return "list";
//			return gson.toJson(unicorns);
		});

		get("/getSetlist/:name", (request, response) -> {
			response.type("application/json");
			response.header("Access-Control-Allow-Origin", "*");
			
			
			JsonParser parser = new JsonParser();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();

			JsonElement el = parser.parse(setlist.getMethod(setlist.setArtist(request.params(":name"))));
			String jsonString = gson.toJson(el); // done
			if(request.params(":name") == "")
				return "Didn't search.";
			else
				return jsonString;
		});

		get("/spotify/authorization", (request, response) -> {
//			List<Unicorn> unicorns = storage.fetchUnicorns();
			response.type("application/json");
//			response.header("Access-Control-Allow-Origin", "*");
			
			JsonParser parser = new JsonParser();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();

			JsonElement el = parser.parse(spotify.getMethod(spotify.getMethod(spotify.authorization())));
			String jsonString = gson.toJson(el); // done
			
			return jsonString;
//			return gson.toJson(unicorns);
		});
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

	}
	
}
