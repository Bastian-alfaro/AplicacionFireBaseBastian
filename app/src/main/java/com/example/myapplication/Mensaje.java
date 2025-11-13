package com.example.myapplication;

public class Mensaje {

    public String id;
    public String texto;
    public long timestamp;

    public Mensaje() {}

    public Mensaje(String id, String texto, long timestamp) {
        this.id = id;
        this.texto = texto;
        this.timestamp = timestamp;
    }
}
