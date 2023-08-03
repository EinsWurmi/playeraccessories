package eu.epycsolutions.labyaddon.playeraccessories.configuration.loader;

import eu.epycsolutions.labyaddon.playeraccessories.Accessories;
import eu.epycsolutions.labyaddon.playeraccessories.AccessoriesAPI;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.annotation.Exclude;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.annotation.OSCompatibility;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.annotation.ParentSwap;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.annotation.PermissionRequired;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.annotation.SearchTag;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.annotation.SpriteSlot;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.annotation.SpriteTexture;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.annotation.VersionCompatibility;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property.ConfigProperty;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.MilieuHandler;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.MilieuInfo;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.SwappableHandler;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.SwappableInfo;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors.MilieuAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors.impl.ConfigPropertyMilieuAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors.impl.ReflectionMilieuAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.CustomTranslation;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuDevelopment;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuExperimental;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuListener;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuListener.EventType;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuOrder;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuOrder.Order;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuRequires;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuRequiresExclude;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuSection;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.MilieuElement;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.MilieuHeader;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.RootMilieuRegistry;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.list.ListMilieu;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.widget.WidgetRegistry;
import eu.epycsolutions.labyaddon.playeraccessories.events.milieu.MilieuCreateEvent;
import net.labymod.api.Laby;
import net.labymod.api.LabyAPI;
import net.labymod.api.addon.AddonService;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.resources.texture.ThemeTextureLocation;
import net.labymod.api.configuration.loader.annotation.ModRequirement;
import net.labymod.api.configuration.loader.annotation.ModRequirement.RequirementState;
import net.labymod.api.configuration.loader.annotation.ModRequirement.RequirementType;
import net.labymod.api.configuration.loader.annotation.OptiFineRequirement;
import net.labymod.api.models.OperatingSystem;
import net.labymod.api.models.version.Version;
import net.labymod.api.modloader.ModLoader;
import net.labymod.api.modloader.ModLoaderRegistry;
import net.labymod.api.thirdparty.optifine.OptiFine;
import net.labymod.api.util.logging.Logging;
import net.labymod.api.util.reflection.Reflection;
import net.labymod.api.util.version.VersionRange;
import net.labymod.api.util.version.serial.VersionCompatibilityDeserializer;
import net.labymod.api.util.version.serial.VersionDeserializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BooleanSupplier;

public class Config implements ConfigAccessor {

  @Exclude
  private static final Logging LOGGER = Logging.create(Config.class);

  @Exclude
  private Map<String, String> configMeta;

  public void reset() {
    for(Milieu milieu : toMilieus()) {
      milieu.initialize();
      milieu.reset();
    }
  }

  @Override
  public List<Milieu> toMilieus() {
    return toMilieus(null);
  }

  @Override
  public List<Milieu> toMilieus(Milieu parent) {
    return toMilieus(parent, null);
  }

  @Override
  public RootMilieuRegistry asRegistry(String id) {
    String namespace = Laby.labyAPI().getNamespace(this);
    RootMilieuRegistry registry = RootMilieuRegistry.custom(namespace, id);

    registry.addMilieus(toMilieus());
    registry.initialize();

    return registry;
  }

  @NotNull
  public List<Milieu> toMilieus(@Nullable Milieu parent, SpriteTexture texture) {
    LabyAPI labyAPI = Laby.labyAPI();
    AccessoriesAPI accessoriesAPI = Accessories.accessoriesAPI();
    List<TempMilieu> tempMilieus = new ArrayList<>();

    if(this.getClass().isAnnotationPresent(SpriteTexture.class)) {
      texture = this.getClass().getAnnotation(SpriteTexture.class);
    }
    SpriteTexture finalTexture = texture;

    MilieuRequires swappable = Reflection.getAnnotation(this.getClass(), MilieuRequires.class);
    MilieuOrder order = Reflection.getAnnotation(this.getClass(), MilieuOrder.class);
    CustomTranslation customTranslation = Reflection.getAnnotation(this.getClass(), CustomTranslation.class);

    Reflection.getMembers(this.getClass(), true, (member) -> {
      if(member.isAnnotationPresent(Exclude.class)) return;

      MilieuHeader header = null;
      MilieuSection milieuSection = member.getAnnotation(MilieuSection.class);
      if(milieuSection != null) {
        String value = milieuSection.value();
        String id = value.replace(".", "_");

        header = new MilieuHeader(id, milieuSection.center(), milieuSection.translation(), value);
      }

      if(member.getAnnotation(MilieuDevelopment.class) != null && (!labyAPI.labyModLoader().isAddonDevelopmentEnvironment())) return;

      if(!this.isVersionCompatible(member)) return;
      if(!this.isOperatingSystemCompatible(member)) return;

      MilieuElement milieu = this.createMilieu(parent, finalTexture, swappable, order, customTranslation, member);
      tempMilieus.add(new TempMilieu(header, milieu));
    });

    tempMilieus.sort(Comparator.comparingInt((o) -> o.milieu.getOrderValue()));
    List<Milieu> milieus = new ArrayList<>();

    for(TempMilieu tempMilieu : tempMilieus) {
      if(tempMilieu.header != null) milieus.add(tempMilieu.header);
      milieus.add(tempMilieu.milieu);
    }

    for(Milieu milieu : milieus) {
      if(!milieu.isElement() || milieu.asElement().swappableInfo() == null) continue;

      MilieuElement element = milieu.asElement();

      for(Milieu swapMilieu : milieus) {
        SwappableInfo swappableInfo = element.swappableInfo();

        if(swapMilieu.isElement() && swapMilieu.getId().equalsIgnoreCase(swappableInfo.swapId())) {
          Annotation milieuAnnotation = swapMilieu.asElement().getAnnotation();

          if(milieuAnnotation != null) {
            eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuElement swapElement =
                milieuAnnotation.annotationType().getAnnotation(
                    eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuElement.class);

            if(swapElement != null) {
              Class<? extends SwappableHandler> swappableClass = swapElement.swappable();

              if(swappableClass != SwappableHandler.class) {
                SwappableHandler swappableHandler = accessoriesAPI.referenceStorage().swappableHandlerRegistry().getHandler(swappableClass);
                if(swappableHandler != null) swappableInfo.setHandler(swappableHandler);
              }
            }
          }
        }
      }
    }

    List<MilieuListenerMethod> listenerMethods = getListenerMethods();
    if(listenerMethods.isEmpty()) return milieus;

    for(Milieu milieu : milieus) {
      if(!milieu.isElement()) continue;

      for(MilieuListenerMethod listener : listenerMethods) {
        if(!listener.target.equals(milieu.getId())) continue;

        MilieuListener.EventType type = listener.type;
        if(type == EventType.RESET) {
          milieu.asElement().setResetListener(() -> listener.invoke(this, milieu));
          continue;
        }

        if(type == EventType.INITIALIZE) listener.invoke(this, milieu);
      }
    }

    return milieus;
  }

  private String getSpritePath(SpriteSlot slot, SpriteTexture texture) {
    int page = slot.page();
    String pageSuffix = page == 0 ? "" : "_" + page;

    return texture.value().replace(".png", "") + pageSuffix + "." + texture.type();
  }

  private <T extends Member & AnnotatedElement> MilieuElement createMilieu(
      Milieu parent,
      SpriteTexture texture,
      MilieuRequires swappable,
      MilieuOrder order,
      CustomTranslation customTranslationAnnotation,
      T member
  ) {
    AccessoriesAPI accessoriesAPI = Accessories.accessoriesAPI();
    WidgetRegistry registry = accessoriesAPI.widgetRegistry();

    String id = member.getName();
    Icon icon = null;

    if(texture != null) {
      SpriteSlot slot = member.getAnnotation(SpriteSlot.class);

      if(slot != null) {
        String namespace = Laby.labyAPI().getNamespace(this);

        ThemeTextureLocation textureLocation = Laby.references()
            .resourceLocationFactory()
            .createThemeTexture(
                namespace,
                "textures/" + this.getSpritePath(slot, texture),
                128, 128
            );

        icon = Icon.sprite(
            textureLocation,
            slot.x(),
            slot.y(),
            slot.size(),
            slot.size()
        );
      }
    }

    String customTranslation;
    if(member.isAnnotationPresent(CustomTranslation.class)) {
      customTranslation = member.getAnnotation(CustomTranslation.class).value();
    } else if(customTranslationAnnotation != null) {
      customTranslation = customTranslationAnnotation.value();
      if(!customTranslation.endsWith(".")) customTranslation += ".";
    } else {
      customTranslation = null;
    }

    if(customTranslation != null && customTranslation.endsWith(".")) customTranslation += id;

    SearchTag searchTag = member.getAnnotation(SearchTag.class);
    PermissionRequired permissionRequired = member.getAnnotation(PermissionRequired.class);

    if(member.isAnnotationPresent(MilieuRequires.class)) swappable = member.getAnnotation(MilieuRequires.class);
    if(member.isAnnotationPresent(MilieuOrder.class)) order = member.getAnnotation(MilieuOrder.class);

    String[] tags = new String[0];
    if(searchTag != null) tags = searchTag.value();

    String requiredPermission = permissionRequired != null ? permissionRequired.value() : null;
    boolean canForceEnable = permissionRequired != null && permissionRequired.canForceEnable();

    SwappableInfo swappableInfo = swappable == null
        || id.equals(swappable.value())
        || member.isAnnotationPresent(MilieuRequiresExclude.class)
        ? null
        : new SwappableInfo(swappable.value(), swappable.invert(), swappable.required());

    byte orderValue = order == null ? Order.NORMAL : order.value();

    MilieuElement milieu = new MilieuElement(id, icon, customTranslation, tags, requiredPermission, orderValue, swappableInfo, canForceEnable);
    milieu.setVisibleSupplier(this.createVisibleSupplier(member));
    milieu.setExperimental(member.isAnnotationPresent(MilieuExperimental.class));

    MilieuAccessor accessor = this.getMilieuAccessor(member, milieu);

    if(accessor != null && List.class.isAssignableFrom(accessor.getType())) {
      try {
        return new ListMilieu(
            id,
            icon,
            customTranslation,
            tags,
            requiredPermission,
            canForceEnable,
            swappableInfo,
            orderValue,
            accessor
        );
      } catch(Exception exception) {
        exception.printStackTrace();
      }
    }

    milieu.setAccessor(accessor);

    if(member.isAnnotationPresent(ParentSwap.class)) {
      if(parent instanceof MilieuElement parentElement) {
        parentElement.setSearchTags(tags);
        parentElement.setAdvancedAccessor(accessor);
        parentElement.setRequiredPermission(requiredPermission);

        return milieu;
      }
    } else {
      milieu.setParent(parent);
      milieu.setWidgets(registry.createWidgets(milieu, new MilieuInfo<>(this, member), accessor));
    }

    if(member instanceof Field field) {
      if(Config.class.isAssignableFrom(field.getType())) {
        Config child = Reflection.invokeGetterField(this, field);
        if(child != null) milieu.addMilieus(child.toMilieus(milieu, texture));
      }
    }

    Laby.fireEvent(new MilieuCreateEvent(milieu));

    MilieuHandler handler = milieu.handler();
    if(handler != null) handler.created(milieu);

    return milieu;
  }

  private <T extends Member & AnnotatedElement> BooleanSupplier createVisibleSupplier(T member) {
    if(member.isAnnotationPresent(ModRequirement.class)) {
      ModRequirement modRequirement = member.getAnnotation(ModRequirement.class);

      boolean requiresInstalled = modRequirement.state() == RequirementState.INSTALLED;
      String namespace = modRequirement.namespace();
      BooleanSupplier installedSupplier;

      if(namespace.equals(OptiFine.NAMESPACE) || namespace.equals(OptiFine.FABRIC_MOD_ID)) {
        throw new IllegalStateException("Use @OptiFineRequirement instead of @ModRequirement for " + namespace);
      }

      RequirementType type = modRequirement.type();
      if(type == RequirementType.ADDON) {
        AddonService addonService = Laby.labyAPI().addonService();
        installedSupplier = () -> addonService.getAddon(namespace).isPresent();
      } else {
        ModLoaderRegistry modLoaderRegistry = Laby.references().modLoaderRegistry();
        installedSupplier = () -> {
          ModLoader modLoader = modLoaderRegistry.getById(type.getLoaderId());
          if(modLoader == null) return false;

          return modLoader.isModLoaded(namespace);
        };
      }

      return () -> requiresInstalled == installedSupplier.getAsBoolean();
    }

    if(member.isAnnotationPresent(OptiFineRequirement.class)) {
      OptiFineRequirement optiFineRequirement = member.getAnnotation(OptiFineRequirement.class);
      boolean requiresInstalled = optiFineRequirement.value() == RequirementState.INSTALLED;
      OptiFine optiFine = Laby.references().optiFine();

      return () -> requiresInstalled == optiFine.isOptiFinePresent();
    }

    return null;
  }

  @SuppressWarnings("rawtypes")
  private <T extends Member & AnnotatedElement> MilieuAccessor getMilieuAccessor(T member, MilieuElement milieu) {
    MilieuAccessor accessor = null;

    if(member instanceof Field fieldMember) {
      int modifiers = fieldMember.getModifiers();
      if(Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers)) return null;
      if(Modifier.isTransient(modifiers)) return null;

      if(ConfigProperty.class.isAssignableFrom(fieldMember.getType())) {
        fieldMember.setAccessible(true);

        ConfigProperty configProperty = Reflection.invokeGetterField(this, fieldMember);
        if(configProperty == null) {
          LOGGER.warn("Could not create ConfigPropertyMilieuAccessor for \"{}\"", fieldMember.getName());
          return null;
        }

        configProperty.withMilieus(milieu);

        Type type = fieldMember.getGenericType();
        if(type instanceof ParameterizedType) configProperty.setGenericType(((ParameterizedType) type).getActualTypeArguments()[0]);

        accessor = new ConfigPropertyMilieuAccessor(milieu, configProperty, this, fieldMember);
      } else {
        if(!Config.class.isAssignableFrom(fieldMember.getType())) {
          LOGGER.warn(
              "*** Code of Conduct ***\nIt was detected that the property \"{}\" in the configuration \"{}\" is not a ConfigProperty of PlayerAccessories or not even a ConfigProperty.\nPlease update the proerty to a ConfigProperty from PlayerAccessories!",
              fieldMember.getName(),
              this.getClass().getName()
          );
        }

        accessor = new ReflectionMilieuAccessor(milieu, fieldMember, this);
      }
    }

    return accessor;
  }

  private boolean isVersionCompatible(AnnotatedElement element) {
    VersionCompatibility annotation = element.getAnnotation(VersionCompatibility.class);
    if(annotation == null) return true;

    String version = annotation.value();
    if(version == null || version.isEmpty()) return true;

    Version runningVersion = Laby.labyAPI().labyModLoader().version();
    if(version.contains("<")) {
      VersionRange multiRange = new VersionRange(version);
      return multiRange.isCompatible(runningVersion);
    } else if(version.contains(",")) {
      net.labymod.api.models.version.VersionCompatibility compatibility = VersionCompatibilityDeserializer.from(version);
      return compatibility.isCompatible(runningVersion);
    } else {
      return runningVersion.isCompatible(VersionDeserializer.from(version));
    }
  }

  private boolean isOperatingSystemCompatible(AnnotatedElement element) {
    OSCompatibility annotation = element.getAnnotation(OSCompatibility.class);
    if(annotation == null) return true;

    OperatingSystem operatingSystem = OperatingSystem.getPlatform();
    OperatingSystem[] compatible = annotation.value();
    for(OperatingSystem os : compatible) {
      if(os == operatingSystem) return true;
    }

    return false;
  }

  public boolean hasConfigMeta(String key) {
    return this.configMeta != null && this.configMeta.containsKey(key);
  }

  public Map<String, String> configMeta() {
    if(this.configMeta == null) this.configMeta = new HashMap<>();
    return this.configMeta;
  }

  private List<MilieuListenerMethod> getListenerMethods() {
    List<MilieuListenerMethod> listenerMethods = new ArrayList<>();
    Reflection.getMethods(this.getClass(), false, (method) -> {
      if(!method.isAnnotationPresent(MilieuListener.class)) return;

      MilieuListener annotation = method.getAnnotation(MilieuListener.class);
      if(method.getReturnType() != void.class) return;
      if(method.getParameterCount() != 1 || !method.getParameterTypes()[0].isAssignableFrom(MilieuElement.class)) return;

      listenerMethods.add(new MilieuListenerMethod(annotation.target(), annotation.type(), method));
    });

    return listenerMethods;
  }


  private record TempMilieu(MilieuHeader header, MilieuElement milieu) { }

  private record MilieuListenerMethod(String target, EventType type, Method method) {
    public void invoke(Object instance, Milieu milieu) {
      try {
        this.method.invoke(instance, milieu);
      } catch(Exception exception) {
        exception.printStackTrace();
      }
    }
  }

}
