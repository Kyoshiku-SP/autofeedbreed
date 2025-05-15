package com.kyoshiku.autofeedbreed.state;

public interface AnimalFeedBreedState {
    boolean hasEaten();
    void setHasEaten(boolean value);

    boolean hasBred();
    void setHasBred(boolean value);

    long getCooldownEnd();
    void setCooldownEnd(long time);

    long getEatingCooldownEnd();
    void setEatingCooldownEnd(long time);

    long getBreedingCooldownEnd();
    void setBreedingCooldownEnd(long time);
}
