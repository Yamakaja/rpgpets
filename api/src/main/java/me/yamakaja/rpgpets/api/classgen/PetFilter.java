package me.yamakaja.rpgpets.api.classgen;

import me.yamakaja.rpgpets.api.entity.PetType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Yamakaja on 5/12/18.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
public @interface PetFilter {

    PetType[] value();

}
