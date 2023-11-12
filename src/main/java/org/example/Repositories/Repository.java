package org.example.Repositories;

import org.example.Models.Album;
import org.example.Models.Artist;

import java.util.List;

public interface Repository {
    int countArtists();
    int countAlbums();

    void insertArtist(Artist artist);
    void insertAlbum(int artistId, Album album);

    void deleteArtist(int id);
    void deleteAlbum(int id);

    Artist getArtist(int id);
    Album getAlbum(int id);

    List<Artist> getArtist();
    List<Album> getAlbums();
}
