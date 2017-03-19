package LightEngineer;

import java.util.ArrayList;

/**
 * Created by jackm on 07/11/2016.
 */

public class Colour implements ColourVariations{

    private int r;
    private int g;
    private int b;

    public Colour(int r, int g ,int b){
        this.colourName = "CUSTOM COLOUR";
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Colour(String colourName, int r, int g ,int b){
        this.colourName = colourName;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    private String colourName;

    public String getColourName() {
        return colourName;
    }

    public void setColourName(String colourName) {
        this.colourName = colourName;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
}
