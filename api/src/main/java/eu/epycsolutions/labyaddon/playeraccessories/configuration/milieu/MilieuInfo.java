package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.Config;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;

public record MilieuInfo<M extends Member & AnnotatedElement>(Config config, M member) { }
