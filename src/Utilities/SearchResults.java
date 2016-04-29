package Utilities;

import java.util.ArrayList;

public class SearchResults {
	private ArrayList<Movie> movieList;
	private Boolean pageAvailable;
	
	
	
	public SearchResults(ArrayList<Movie> movieList, boolean pageAvailable) {
		this.movieList = movieList;
		this.pageAvailable = pageAvailable;
	}
	
	
	public ArrayList<Movie> getMovieList() {
		return movieList;
	}
	public void setMovieList(ArrayList<Movie> movieList) {
		this.movieList = movieList;
	}
	public Boolean getPageAvailable() {
		return pageAvailable;
	}
	public void setPageAvailable(Boolean pageAvailable) {
		this.pageAvailable = pageAvailable;
	}
}
