package com.example.mjb.musicplayer.model;

import java.io.Serializable;

public class Music implements Serializable {

        private String path;
        private String title;
        private String album;
        private String artist;
        private String coverPath;

    public Music(String path, String title, String album, String artist,String coverPath) {
        this.path = path;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.coverPath = coverPath;
    }

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public String getPath() {
        return path;

    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
