package com.example.kelimebulmaca;

/**
 * Bu bolumdeki kodlar asagida verilen siteden alinarak uzerinde degisiklik yapilmistir
 * http://stephenpengilley.blogspot.com/2013/01/android-custom-spinner-tutorial.html
 */

public class CompleteTick {
    private String name;
    private int resourceId;

    CompleteTick(String name, int resourceId) {
        this.name = name;
        this.resourceId = resourceId;
    }

    public String getName() {
        return name;
    }

    public int getResourceId() {
        return resourceId;
    }

    @Override
    public String toString() {
        return getName();
    }
}
