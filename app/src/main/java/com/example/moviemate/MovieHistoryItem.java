package com.example.moviemate;

public class MovieHistoryItem {
    private int history_id;
    private int movie_id;
    private String title;
    private String imageUrl; // Changed from imageResId to imageUrl string
    private String duration;
    private String genre;
    private String releaseYear;
    private int rating;
    private String watchDate;

    public MovieHistoryItem(int history_id, int movie_id, String title, String imageUrl, String duration,
                            String genre, String releaseYear, int rating, String watchDate) {
        this.history_id = history_id;
        this.movie_id = movie_id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.duration = duration;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.rating = rating;
        this.watchDate = watchDate;
    }

    public int getHistory_id() {
        return history_id;
    }

    public int getMovie_id() {
        return movie_id;
    }

    public String getTitle() {
        return title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDuration() {
        return duration;
    }

    public String getGenre() {
        return genre;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public int getRating() {
        return rating;
    }

    public String getWatchDate() {
        return watchDate;
    }
}
