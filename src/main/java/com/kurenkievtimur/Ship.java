package com.kurenkievtimur;

public class Ship {
    private final String name;
    private final int length;
    private int[] coordinates;

    public Ship(String name, int length) {
        this.name = name;
        this.length = length;
        this.coordinates = new int[4];
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public int[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(int[] coordinates) {
        this.coordinates = coordinates;
    }
}
