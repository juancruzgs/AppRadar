package com.mobilemakers.juansoler.appradar;

/**
 * SoundManager exception: the sound wasn't set.
 *
 * Created by ariel.cattaneo on 27/02/2015.
 */
public class SoundNotSetException extends SoundManagerException {
    @Override
    public String getMessage() {
        return "The sound wasn't set.";
    }
}
