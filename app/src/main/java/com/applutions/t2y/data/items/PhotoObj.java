package com.applutions.t2y.data.items;

import java.io.Serializable;

public class PhotoObj implements Serializable {
    String contentType;
    String data;

    public String getContentType() {
        return contentType;
    }

    public String getData() {
        return data;
    }
}
