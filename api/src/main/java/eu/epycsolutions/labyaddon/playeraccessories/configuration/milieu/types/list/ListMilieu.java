package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.list;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.Config;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.SwappableInfo;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors.MilieuAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.MilieuElement;
import net.labymod.api.Laby;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.util.KeyValue;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ListMilieu extends MilieuElement {

  private final Class<?> type;
  private final List<Config> list;

  public ListMilieu(
      String id,
      Icon icon,
      String customTranslation,
      String[] searchTags,
      String requiredPermission,
      boolean canForceEnable,
      SwappableInfo swappableInfo,
      byte orderValue,
      MilieuAccessor accessor
  ) {
    super(id, icon, customTranslation, searchTags, requiredPermission, orderValue, swappableInfo, canForceEnable);

    Type parameter = accessor.getGenericType();
    if(!(parameter instanceof ParameterizedType)) throw new IllegalStateException("Cannot determine type for config field " + id);

    this.type = (Class)((ParameterizedType) parameter).getActualTypeArguments()[0];
    this.list = accessor.get();
  }

  public static Component defaultEntryName() {
    return Component.translatable("player-accessories.ui.milieu.list.entry");
  }

  public static Component defaultNewEntryName() {
    return Component.translatable("player-accessories.ui.milieu.list.newEntry");
  }

  public ListMilieuEntry createNew() {
    ListMilieuEntry entry = new ListMilieuEntry(this, Component.empty(), list.size());

    try {
      Config config = (Config) type.getConstructor(new Class[0]).newInstance(new Object[0]);
      list.add(config);

      if(config instanceof ListMilieuConfig) entry.displayName().append(((ListMilieuConfig) config).newEntryTitle());
      else entry.displayName().append(defaultNewEntryName());

      entry.addMilieus((List<Milieu>) config);
    } catch(Exception exception) {
      exception.printStackTrace();
    }

    return entry;
  }

  public void remove(ListMilieuEntry entry) {
    list.remove(entry.listIndex());
  }

  public List<KeyValue<Milieu>> getElements() {
    List<KeyValue<Milieu>> list = new ArrayList<>();

    for(int i = 0; i < this.list.size(); i++) {
      Config config = this.list.get(i);

      if(isInvalid(config)) list.remove(i--);
      else {
        try {
          ListMilieuEntry entry = new ListMilieuEntry(this, displayName(config), i);
          entry.addMilieus((List<Milieu>) config);

          list.add(new KeyValue<>(entry.getId(), entry));
        } catch(Exception exception) {
          exception.printStackTrace();
        }
      }
    }

    return list;
  }

  @Override
  public boolean hasAdvancedButton() {
    return true;
  }

  private boolean isInvalid(Config config) {
    if(config instanceof ListMilieuConfig) return ((ListMilieuConfig) config).isInvalid();
    return false;
  }

  private Component displayName(Config config) {
    if(!(config instanceof ListMilieuConfig)) return defaultEntryName();

    ListMilieuConfig listMilieuConfig = (ListMilieuConfig) config;
    Component component = listMilieuConfig.entryDisplayName();
    if(component == null) {
      if(Laby.labyAPI().labyModLoader().isAddonDevelopmentEnvironment()) {
        throw new NullPointerException("ListMilieuConfig#entryDisplayName() must not return null");
      }

      return defaultEntryName();
    }

    return component;
  }

}
