package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors.MilieuAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.MilieuElement;
import eu.epycsolutions.labyaddon.playeraccessories.events.milieu.MilieuInitializeEvent;
import eu.epycsolutions.labyaddon.playeraccessories.gui.screen.activity.playeraccessories.childs.MilieuContentActivity;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.service.Identifiable;
import net.labymod.api.service.Registry;
import net.labymod.api.util.CharSequences;
import net.labymod.api.util.CollectionHelper;
import net.labymod.api.util.KeyValue;
import net.labymod.api.util.io.Filter;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public interface Milieu extends Identifiable, Registry<Milieu> {

  default boolean isEnabled() {
    MilieuHandler handler = handler();
    return (handler == null || handler.isEnabled(this));
  }

  default boolean hasControlButton() { return false; }

  default List<Milieu> getMilieus() { return values(); }

  default String getTranslationId() { return getId(); }

  default MilieuContentActivity createActivity() {
    return new MilieuContentActivity(this, false);
  }

  default MilieuContentActivity createActivityLazy() {
    return new MilieuContentActivity(this);
  }

  default void initialize() {
    forEach(Milieu::initialize);

    Laby.fireEvent(new MilieuInitializeEvent(this));
    MilieuHandler handler = handler();
    if(handler != null) handler.initialized(this);
  }

  default Optional<Milieu> findMilieu(CharSequence path) {
    return findMilieu(CharSequences.split(path, "\\."));
  }

  default Optional<Milieu> findMilieu(CharSequence[] nodes) {
    String node = CharSequences.toString(nodes[0]);

    if(nodes.length == 1 && Objects.equals(getId(), node)) return Optional.of(this);

    for(KeyValue<Milieu> element : getElements()) {
      Milieu milieu = element.getValue();

      if(Objects.equals(milieu.getId(), node)) {
        if(nodes.length == 1) return Optional.of(milieu);

        CharSequence[] newNode = new CharSequence[nodes.length - 1];
        CollectionHelper.copyOfRange(nodes, newNode, 1, nodes.length);

        return milieu.findMilieu(newNode);
      }
    }

    return Optional.empty();
  }

  default boolean isElement() {
    return this instanceof MilieuElement;
  }

  default MilieuElement asElement() {
    return (MilieuElement) this;
  }

  default boolean hasParent() {
    return (parent() != null);
  }

  default boolean isHoldable() { return true; }

  default List<Milieu> collect(Filter<Milieu> filter) {
    List<Milieu> milieus = new ArrayList<>();

    for(KeyValue<Milieu> element : getElements()) {
      Milieu milieu = element.getValue();
      if(filter.matches(milieu)) milieus.add(milieu);

      milieus.addAll(milieu.collect(filter));
    }

    return milieus;
  }

  @Nullable
  default MilieuHandler handler() { return null; }

  default boolean isResettable() {
    if(!isElement()) return false;

    MilieuElement milieuElement = asElement();
    if(milieuElement.getResetListener() != null) return true;

    MilieuAccessor accessor = milieuElement.getAccessor();
    if(accessor != null) return !accessor.property().isDefaultValue();

    return false;
  }

  default void reset() {
    MilieuHandler handler = handler();
    if(handler != null) handler.reset(this);
    if(!isElement()) return;

    MilieuElement element = asElement();
    Runnable resetListener = element.getResetListener();
    if(resetListener != null) {
      resetListener.run();
      return;
    }

    MilieuAccessor accessor = element.getAccessor();
    if(accessor != null) accessor.property().reset();
  }

  Icon getIcon();

  Component displayName();

  @Nullable
  Component description();

  boolean hasAdvancedButton();

  Milieu parent();

  String getPath();

  String getTranslationKey();

  String[] getSearchTags();

  @Nullable
  String getRequiredPermission();

  boolean canForceEnable();

  boolean isInitialized();

  boolean isExperimental();

  void setExperimental(boolean experimental);

}
