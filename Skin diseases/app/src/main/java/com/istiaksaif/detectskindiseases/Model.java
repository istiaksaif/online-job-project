package com.istiaksaif.detectskindiseases;

public class Model {
    private int id;
    private byte[] image;
    private String disease;

    public Model() {
    }

    public Model(int id, byte[] image, String disease) {
        this.id = id;
        this.image = image;
        this.disease = disease;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }
}
