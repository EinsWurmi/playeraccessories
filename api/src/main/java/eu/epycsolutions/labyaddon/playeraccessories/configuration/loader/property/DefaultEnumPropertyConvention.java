package eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property;

import net.labymod.api.property.PropertyConvention;

public class DefaultEnumPropertyConvention<E extends Enum<E>> implements PropertyConvention<E> {

  private final E defaultEnum;

  public DefaultEnumPropertyConvention(E defaultEnum) {
    this.defaultEnum = defaultEnum;
  }

  @Override
  public E convention(E value) {
    return value == null ? this.defaultEnum : value;
  }

}
