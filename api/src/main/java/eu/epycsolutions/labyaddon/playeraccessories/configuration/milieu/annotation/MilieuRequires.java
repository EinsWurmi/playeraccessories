package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MilieuRequires {

  String value();

  boolean invert() default false;

  String required() default "true";

}
