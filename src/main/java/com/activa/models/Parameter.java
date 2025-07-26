package com.activa.models;

import java.util.UUID;

public class Parameter {

    private UUID id;
    private String key;
    private String title; // Diubah dari 'name' menjadi 'title'
    private String content;

    /**
     * Konstruktor untuk membuat instance Parameter baru.
     * UUID baru akan dibuat secara otomatis.
     *
     * @param key     Kunci unik untuk parameter (misal: "CLUB_NAME").
     * @param title   Nama yang mudah dibaca untuk parameter (misal: "Nama Klub").
     * @param content Nilai atau isi dari parameter (misal: "Activa Club").
     */
    public Parameter(String key, String title, String content) {
        this.id = UUID.randomUUID();
        this.key = key;
        this.title = title;
        this.content = content;
    }

    /**
     * Konstruktor lengkap untuk membuat instance Parameter dari data yang sudah ada,
     * seperti dari database.
     *
     * @param id      UUID yang sudah ada dari parameter.
     * @param key     Kunci unik untuk parameter.
     * @param title   Nama yang mudah dibaca untuk parameter.
     * @param content Nilai atau isi dari parameter.
     */
    public Parameter(UUID id, String key, String title, String content) {
        this.id = id;
        this.key = key;
        this.title = title;
        this.content = content;
    }

    // --- Getters and Setters ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
