package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.loader.property.CustomRequires;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.MilieuHandler;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.SwappableInfo;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors.MilieuAccessor;
import net.labymod.api.Laby;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.screen.widget.Widget;
import net.labymod.api.loader.LabyModLoader;
import org.jetbrains.annotations.Nullable;
import java.lang.annotation.Annotation;

public class MilieuElement extends AbstractMilieuRegistry {

  private String customTranslation;
  private String[] searchTags;
  private String requiredPermission;

  private SwappableInfo swappableInfo;

  private final byte orderValue;
  private MilieuHandler handler;
  private Widget[] widgets;

  private MilieuAccessor accessor;
  private MilieuAccessor advancedAccessor;

  private Annotation annotation;
  private Runnable resetListener;

  private boolean canForceEnable;
  private boolean extended;

  public MilieuElement(
      String id,
      Icon icon,
      String customTranslation,
      String[] searchTags,
      String requiredPermission,
      byte orderValue,
      SwappableInfo swappableInfo,
      boolean canForceEnable) {
    super(id, icon);

    this.customTranslation = customTranslation;
    this.searchTags = searchTags;
    this.requiredPermission = requiredPermission;

    this.orderValue = orderValue;
    this.swappableInfo = swappableInfo;

    this.canForceEnable = canForceEnable;
  }

  public MilieuElement(String id, Icon icon, String customTranslation, String[] searchTags) {
    this(id, icon, customTranslation, searchTags, null, (byte) 0, null, false);
  }

  public Widget[] getWidgets() {
    return this.widgets;
  }

  public void setWidgets(Widget[] widgets) {
    this.widgets = widgets;
  }

  public MilieuAccessor getAccessor() {
    return this.accessor;
  }

  public void setAccessor(MilieuAccessor accessor) {
    this.accessor = accessor;
  }

  public Annotation getAnnotation() {
    return this.annotation;
  }

  public void setAnnotation(Annotation annotation) {
    this.annotation = annotation;
  }

  public Runnable getResetListener() {
    return this.resetListener;
  }

  public void setResetListener(Runnable resetListener) {
    this.resetListener = resetListener;
  }

  public boolean isExtended() {
    return this.extended;
  }

  public void setExtended(boolean extended) {
    this.extended = extended;
  }

  public boolean isEnabled() {
    if(!super.isEnabled()) return false;
    if(this.swappableInfo == null || this.swappableInfo.accessor() == null) return true;

    MilieuAccessor enabledAccessor = this.swappableInfo.accessor();
    MilieuElement milieuElement = enabledAccessor.milieu();
    if(!milieuElement.isEnabled()) return false;

    Object value = enabledAccessor.get();
    boolean invert = this.swappableInfo.isInvert();

    if(value instanceof Boolean) return (invert != ((Boolean) value));

    if(milieuElement.advancedAccessor != null) {
      Object advancedValue = milieuElement.advancedAccessor.get();
      if(advancedValue instanceof Boolean) return (invert != (Boolean) advancedValue);
    }

    if(this.swappableInfo.handler() != null) {
      return (invert != this.swappableInfo.handler().isEnabled(this, value, this.swappableInfo));
    }

    CustomRequires<Object> swappable = enabledAccessor.property().getCustomRequires();
    if(swappable == null) {
      LabyModLoader loader = Laby.labyAPI().labyModLoader();

      if(loader.isAddonDevelopmentEnvironment()) {
        Laby.labyAPI().minecraft().crashGame("No CustomSwappable was set for the config property.", new RuntimeException());
        return false;
      }

      return true;
    }

    return (invert != swappable.isEnabled(value));
  }

  public boolean hasControlButton() {
    return (this.advancedAccessor != null);
  }

  public MilieuAccessor getAdvancedAccessor() {
    return this.advancedAccessor;
  }

  public void setAdvancedAccessor(MilieuAccessor accessor) {
    this.advancedAccessor = accessor;
  }

  public String getTranslationKey() {
    return (this.customTranslation == null) ? super.getTranslationKey() : this.customTranslation;
  }

  public void setCustomTranslation(String customTranslation) {
    this.customTranslation = customTranslation;
  }

  public String[] getSearchTags() {
    return this.searchTags;
  }

  public void setSearchTags(String[] searchTags) {
    this.searchTags = searchTags;
  }

  @Nullable
  public String getRequiredPermission() {
    return this.requiredPermission;
  }

  public void setRequiredPermission(@Nullable String requiredPermission) {
    this.requiredPermission = requiredPermission;
  }

  public boolean canForceEnable() {
    return this.canForceEnable;
  }

  public void setCanForceEnable(boolean canForceEnable) {
    this.canForceEnable = canForceEnable;
  }

  public SwappableInfo swappableInfo() {
    return this.swappableInfo;
  }

  public void setSwappableInfo(SwappableInfo swappableInfo) {
    this.swappableInfo = swappableInfo;
  }

  public byte getOrderValue() {
    return this.orderValue;
  }

  public MilieuHandler handler() {
    return this.handler;
  }

  public void setHandler(MilieuHandler handler) {
    this.handler = handler;
  }

}
