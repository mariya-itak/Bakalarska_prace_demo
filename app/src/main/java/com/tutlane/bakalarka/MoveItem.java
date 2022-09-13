package com.tutlane.bakalarka;

public class MoveItem {
    private String name;
    private int intensity;
    private int smoothness;
    private int repeating;
    private String animationName;

    public MoveItem(String name, int intensity, int smoothness, int repeating, String animationName) {
        this.name = name;
        this.intensity = intensity;
        this.smoothness = smoothness;
        this.repeating = repeating;
        this.animationName = animationName;
    }


    public String getName() {
        return name;
    }

    public int getIntensity() {
        return intensity;
    }

    public int getSmoothness() {
        return smoothness;
    }

    public int getRepeating() {
        return repeating;
    }

    public String getAnimationName() {
        return animationName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public void setSmoothness(int smoothness) {
        this.smoothness = smoothness;
    }

    public void setRepeating(int repeating) {
        this.repeating = repeating;
    }

    public void setAnimationName(String animationName) {
        this.animationName = animationName;
    }
}
