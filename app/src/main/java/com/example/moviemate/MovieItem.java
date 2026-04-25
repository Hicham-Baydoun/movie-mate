package com.example.moviemate;

public class MovieItem {
    private int movie_id;
    private String title;
    private String imageUrl; // Changed from imageResId to imageUrl string
    private String duration;
    private String genre;
    private String releaseYear;

    public MovieItem(int movie_id, String title, String imageUrl, String duration, String genre, String releaseYear) {
        this.movie_id = movie_id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.duration = duration;
        this.genre = genre;
        this.releaseYear = releaseYear;
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

    public int getDurationMinutes() {
        try {
            return Integer.parseInt(duration);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String getGenre() {
        return genre;
    }

    public String getReleaseYear() {
        return releaseYear;
    }
}
