package io.quillo.quillo.interfaces;

import io.quillo.quillo.data.Person;

/**
 * Created by Stickells on 15/01/2018.
 */

public interface PersonListener {
    void onPersonLoaded(Person person);
}
