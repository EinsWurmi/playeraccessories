package eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.MilieuHandler;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.MilieuElement;
import eu.epycsolutions.labyaddon.playeraccessories.events.MilieuResetEvent;
import eu.epycsolutions.labyaddon.playeraccessories.events.MilieuUpdateEvent;
import net.labymod.api.Laby;
import net.labymod.api.event.Phase;
import net.labymod.api.property.Property;
import net.labymod.api.property.PropertyConvention;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.Nullable;
import java.lang.reflect.Type;
import java.util.Objects;
import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class ConfigProperty<T> extends Property<T> {

  private final Class<T> type;
  private Type genericType;
  private MilieuElement milieu;
  private MilieuHandler handler;

  @Nullable
  private CustomRequires<T> customRequires;

  public ConfigProperty(T value) {
    super(value);

    Objects.requireNonNull(value, "Null provided for ConfigProperty value in PlayerAccessories");
    this.type = (Class<T>) value.getClass();
  }

  public ConfigProperty(T value, PropertyConvention<T> propertyConvention) {
    super(value, propertyConvention);

    Objects.requireNonNull(value, "Null provided for ConfigProperty value in PlayerAccessories");
    this.type = (Class<T>) value.getClass();
  }

  public static <E extends Enum<E>> ConfigProperty<E> createEnum(E value) {
    return new ConfigProperty<>(value, new DefaultEnumPropertyConvention<>(value));
  }

  public static <T> ConfigProperty<T> create(T value, Consumer<T> consumer) {
    consumer.accept(value);
    return new ConfigProperty<>(value);
  }

  public static <T> ConfigProperty<T> create(T value) {
    return new ConfigProperty<>(value);
  }

  @Internal
  public final void withMilieus(MilieuElement milieu) {
    this.milieu = milieu;
    this.milieu.setHandler(handler);
  }

  public ConfigProperty<T> withHandler(MilieuHandler handler) {
    this.handler = handler;
    return this;
  }

  @Override
  public void set(T value) {
    if(Objects.equals(value, value)) return;

    boolean initialized = milieu != null && milieu.isInitialized();
    if(initialized) {
      MilieuUpdateEvent event = fireEvent(Phase.PRE, value);
      value = event.getValue();
    }

    super.set(value);

    if(initialized) fireEvent(Phase.POST, value);
  }

  @Override
  public void reset() {
    boolean initialized = milieu != null && milieu.isInitialized();
    if(initialized) Laby.fireEvent(new MilieuResetEvent(Phase.PRE, milieu));

    super.reset();

    if(initialized) Laby.fireEvent(new MilieuResetEvent(Phase.POST, milieu));
  }

  public Class<T> getType() {
    return this.type;
  }

  @Nullable
  public CustomRequires<T> getCustomRequires() {
    return this.customRequires;
  }

  @Nullable
  public Type getGenericType() {
    return this.genericType;
  }

  @Internal
  public void setGenericType(Type genericType) {
    this.genericType = genericType;
  }

  private MilieuUpdateEvent fireEvent(Phase phase, T value) {
    return Laby.fireEvent(new MilieuUpdateEvent(phase, this.milieu, value));
  }

}
