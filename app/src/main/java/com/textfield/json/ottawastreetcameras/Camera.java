package com.textfield.json.ottawastreetcameras;

/**
 * Created by Jason on 25/04/2016.
 */
public class Camera {
    private String name, id;

    public Camera(String name, String id){
        this.name = name;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
