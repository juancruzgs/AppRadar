package com.mobilemakers.juansoler.appradar;

/**
 * SoundManager exception: it wasn't initialized.
 *
 * Created by ariel.cattaneo on 27/02/2015.
 */
public class SoundManagerNotInitializedException extends SoundManagerException {
    @Override
    public String getMessage() {
        return "The SoundManager object wasn't initialized.\nYou have to call SoundManager.init(context) before using it.";
    }
}
