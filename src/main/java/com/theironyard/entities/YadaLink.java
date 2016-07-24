package com.theironyard.entities;

/**
 * Created by will on 7/24/16.
 */

public class YadaLink {
    Yada yada;
    Link link;

    public YadaLink() {
    }

    public YadaLink(Yada yada, Link link) {
        this.yada = yada;
        this.link = link;
    }

    public Yada getYada() {
        return yada;
    }

    public void setYada(Yada yada) {
        this.yada = yada;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }
}
