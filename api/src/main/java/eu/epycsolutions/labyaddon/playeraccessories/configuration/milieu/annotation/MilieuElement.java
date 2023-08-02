package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.SwappableHandler;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface MilieuElement {

  boolean extended() default false;

  Class<? extends SwappableHandler> swappable() default SwappableHandler.class;

}
