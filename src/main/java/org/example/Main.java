package org.example;

import org.example.Models.Album;
import org.example.Models.Artist;
import org.example.Repositories.DBRepository;
import org.example.Repositories.Repository;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, TransformerException, SQLException, ClassNotFoundException {
        //Repository repository = new XMLRepository("src/main/resources/music.xml");
        Repository repository = new DBRepository("jdbc:postgresql://localhost:5432/dev", "postgres", "postgres");

        System.out.println("Count is " + repository.count());
        System.out.println("Items: " + repository.get());

        System.out.println();

        Artist artist0 = repository.get(0);
        System.out.println(artist0);

        Artist artist1 = repository.get(1);
        System.out.println(artist1);

        System.out.println();

        repository.delete(1);
        System.out.println("Count is " + repository.count());
        System.out.println("Items: " + repository.get());

        Artist artist = new Artist();
        artist.setId(3);
        artist.setName("New one");
        artist.setAge(17);
        Album album = new Album();
        album.setName("The end!");
        artist.addAlbum(album);
        repository.insert(artist);

        //repository.save("src/main/resources/music1.xml");
    }
}