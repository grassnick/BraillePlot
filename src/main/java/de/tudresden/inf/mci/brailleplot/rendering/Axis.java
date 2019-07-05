package de.tudresden.inf.mci.brailleplot.rendering;

public class Axis implements Renderable {

    private Type mType;
    private int mOrigin, mSpace, mResolution;

    private boolean mSetTicks;

    public Axis(final Type type, final int origin, final int space, final int resolution, final boolean setTicks) {
        this.mType = type;
        this.mOrigin = origin;
        this.mSpace = space;
        this.mResolution = resolution;
        this.mSetTicks = setTicks;
    }

    public Type getType() {
        return mType;
    }
    public void setType(Type Type) {
        this.mType = mType;
    }
    public int getOrigin() {
        return mOrigin;
    }
    public void setOrigin(int origin) {
        this.mOrigin = origin;
    }

    public int getSpace() {
        return mSpace;
    }
    public void setSpace(int space) {
        this.mSpace = space;
    }

    public int getResolution() {
        return mResolution;
    }
    public void setResolution(int resolution) {
        this.mResolution = resolution;
    }

    public boolean hasTicks() {
        return mSetTicks;
    }
    public void setTicks(boolean setTicks) {
        this.mSetTicks = setTicks;
    }

    enum Type {
        X_AXIS, Y_AXIS;
    }
}
