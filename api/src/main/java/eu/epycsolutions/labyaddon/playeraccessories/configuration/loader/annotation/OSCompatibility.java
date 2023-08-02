package eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.annotation;

import net.labymod.api.models.OperatingSystem;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface OSCompatibility {

  OperatingSystem[] value();
}
