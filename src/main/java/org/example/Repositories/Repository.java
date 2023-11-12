package org.example.Repositories;

import org.example.Models.Artist;

import java.util.List;

public interface Repository {
    int count();

    void insert(Artist artist);

    void delete(int id);

    Artist get(int id);

    List<Artist> get();
}
