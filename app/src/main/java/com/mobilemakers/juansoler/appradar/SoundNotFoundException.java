package com.mobilemakers.juansoler.appradar;

/**
 * SoundManager exception: the sound file wasn't found.
 *
 * Created by ariel.cattaneo on 27/02/2015.
 */
public class SoundNotFoundException extends SoundManagerException {
    @Override
    public String getMessage() {
        return "The sound file wasn't found.";
    }
}
