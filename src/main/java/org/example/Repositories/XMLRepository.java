package org.example.Repositories;

import org.example.Models.Album;
import org.example.Models.Artist;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLRepository implements Repository {
    private final List<Artist> artists = new ArrayList<>();
    private final List<Album> albums = new ArrayList<>();

    private int maxArtistId = 0;
    private int maxAlbumId = 0;

    public XMLRepository(String path) {
        DocumentBuilder builder;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new File(path));
            doc.getDocumentElement().normalize();

            NodeList elements = doc.getElementsByTagName(Artist.ARTIST);
            for (int i = 0; i < elements.getLength(); ++i) {
                Element element = (Element) elements.item(i);

                Artist artist = new Artist();
                parseArtist(element, artist);

                maxArtistId = Math.max(maxArtistId, artist.getId());
                artists.add(artist);
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int countArtists() {
        return artists.size();
    }

    @Override
    public int countAlbums() {
        return albums.size();
    }

    @Override
    public void insertArtist(Artist value) {
        maxArtistId++;
        value.setId(maxArtistId);
        artists.add(value);

        for (Album album: value.getAlbums()) {
            maxAlbumId++;
            album.setId(maxAlbumId);
            albums.add(album);
        }
    }

    @Override
    public void insertAlbum(int artistId, Album album) {
        Artist artist = getArtist(artistId);

        if (artist != null) {
            maxAlbumId++;
            album.setId(maxAlbumId);

            artist.addAlbum(album);
            albums.add(album);
        }
    }

    @Override
    public void deleteArtist(int id) {
        for (int i = 0; i < artists.size(); ++i) {
            if (artists.get(i).getId() == id) {
                albums.removeAll(artists.get(i).getAlbums());
                artists.remove(i);
                return;
            }
        }
    }

    @Override
    public void deleteAlbum(int id) {
        Album album = getAlbum(id);
        if (album != null) {
            for (Artist artist : artists) {
                if (artist.hasAlbum(album)) {
                    artist.removeAlbum(album);
                }
            }
            albums.remove(album);
        }
    }

    @Override
    public Artist getArtist(int id) {
        for (Artist item : artists) {
            if (item.getId() == id) {
                return item;
            }
        }

        return null;
    }

    @Override
    public Album getAlbum(int id) {
        for (Album item : albums) {
            if (item.getId() == id) {
                return item;
            }
        }

        return null;
    }

    @Override
    public List<Artist> getArtist() {
        return artists;
    }

    @Override
    public List<Album> getAlbums() {
        return albums;
    }

    public void save(String file) throws ParserConfigurationException, TransformerException {
        Document doc = createDocumentWithItems();

        Source source = new DOMSource(doc);
        Result result = new StreamResult(new File(file));

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.transform(source, result);
    }

    private void parseArtist(Element element, Artist item) {
        item.setId(Integer.parseInt(element.getAttribute(Artist.ID)));
        item.setName(element.getAttribute(Artist.NAME));
        item.setAge(Integer.parseInt(element.getAttribute(Artist.AGE)));

        NodeList children = element.getElementsByTagName(Album.ALBUM);
        for (int i = 0; i < children.getLength(); ++i) {
            Element child = (Element) children.item(i);

            Album album = new Album();
            album.setId(Integer.parseInt(child.getAttribute(Album.ID)));
            album.setName(child.getAttribute(Album.NAME));
            item.addAlbum(album);

            maxAlbumId = Math.max(maxAlbumId, album.getId());
            albums.add(album);
        }
    }

    private Document createDocumentWithItems() throws ParserConfigurationException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        Element artists = doc.createElement(Artist.ARTISTS);
        for (Artist item: this.artists) {
            Element artist = doc.createElement(Artist.ARTIST);

            artist.setAttribute(Artist.ID, String.valueOf(item.getId()));
            artist.setAttribute(Artist.NAME, item.getName());
            artist.setAttribute(Artist.AGE, String.valueOf(item.getAge()));

            Element albums = doc.createElement(Artist.ALBUMS);
            for (Album subitem: item.getAlbums()) {
                Element album = doc.createElement(Album.ALBUM);
                album.setAttribute(Album.ID, String.valueOf(subitem.getId()));
                album.setAttribute(Album.NAME, subitem.getName());
                albums.appendChild(album);
            }
            artist.appendChild(albums);

            artists.appendChild(artist);
        }

        doc.appendChild(artists);

        return doc;
    }
}
