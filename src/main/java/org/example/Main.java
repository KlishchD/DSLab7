package org.example;

import org.example.Models.Album;
import org.example.Models.Artist;
import org.example.Repositories.DBRepository;
import org.example.Repositories.Repository;
import org.example.Repositories.XMLRepository;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, TransformerException, SQLException, ClassNotFoundException {
        //XMLRepository repository = new XMLRepository("src/main/resources/music.xml");
        Repository repository = new DBRepository("jdbc:postgresql://localhost:5432/dev", "postgres", "postgres");
/*

        System.out.println("Artists count is " + repository.countArtists());
        System.out.println("Albums count is " + repository.countAlbums());
        System.out.println("Artists: " + repository.getArtist());

        System.out.println();

        Artist artist0 = repository.getArtist(0);
        System.out.println(artist0);

        Artist artist1 = repository.getArtist(1);
        System.out.println(artist1);

        System.out.println();

        repository.deleteArtist(24);
        System.out.println("Artists count is " + repository.countArtists());
        System.out.println("Albums count is " + repository.countAlbums());
        System.out.println("Artists: " + repository.getArtist());

        System.out.println("Album: " + repository.getAlbum(1));

        System.out.println("Artists count is " + repository.countArtists());
        System.out.println("Albums count is " + repository.countAlbums());
        System.out.println("Artists: " + repository.getArtist());

        System.out.println("Album: " + repository.getAlbum(10));
*/

//
//        Artist artist = new Artist();
//        artist.setId(3);
//        artist.setName("New one");
//        artist.setAge(17);
//        Album album = new Album();
//        album.setName("The end!");
//        artist.addAlbum(album);
//        repository.insertArtist(artist);

        repository.deleteAlbum(12);

        //repository.save("src/main/resources/music1.xml");
    }
}