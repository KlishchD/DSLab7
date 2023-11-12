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
    private final List<Artist> data = new ArrayList<>();

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
                data.add(artist);
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int count() {
        return data.size();
    }

    @Override
    public void insert(Artist value) {
        int maxId = 0;
        for (Artist item: data) {
            maxId = Math.max(maxId, item.getId());
        }

        value.setId(maxId + 1);
        data.add(value);
    }

    @Override
    public void delete(int id) {
        for (int i = 0; i < data.size(); ++i) {
            if (data.get(i).getId() == id) {
                data.remove(i);
                return;
            }
        }
    }

    @Override
    public Artist get(int id) {
        for (Artist item : data) {
            if (item.getId() == id) {
                return item;
            }
        }

        return null;
    }

    @Override
    public List<Artist> get() {
        return data;
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
        }
    }

    private Document createDocumentWithItems() throws ParserConfigurationException {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();

        Element artists = doc.createElement(Artist.ARTISTS);
        for (Artist item: data) {
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
