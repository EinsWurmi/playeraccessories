package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface MilieuOrder {

  byte value();

  class Order {
    public static final byte FIRST = -127;
    public static final byte SOON = -64;
    public static final byte NORMAL = 0;
    public static final byte LATE = 64;
    public static final byte LAST = 126;
  }

}
