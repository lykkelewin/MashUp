package mashup;

import java.util.List;

public class Setlists {
	public String type = "";
	public String page = "";
	public List<Setlist> setlist;
	
	public Setlists() {
	}
	
	public class Setlist {
		public String id = "";
		public String info = "";
		public String eventDate = "";
		
		public Artist artist = new Artist();
		public Tour tour = new Tour();
		public Sets sets = new Sets();
		public Venue venue = new Venue();
		public Setlist() {

		}
	}
	
	public class Venue{
		public String name = "";
		public City city = new City();
		public Venue() {
		}
	}
	
	public class City{
		public String name = "";
		public Country country = new Country();
		public City() {
		}
	}
	
	public class Country{
		public String code = "";
		public Country() {
		}
	}
	
	public class Artist{
		public String name = "";
		public Artist() {
		}
	}
	
	public class Tour{
		public String name = "";
		public Tour() {
		}
	}

	public class Sets{
		public List<Set> set;
		public Sets() {
		}
	}

	public class Set{
		public int encore = 0;
		public List<Song> song;
		public Set() {
		}
	}
	
	public class Song{
		public String name = "";
		public Song() {
		}
	}
}