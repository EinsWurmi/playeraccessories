package eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widget.widgets.activity.milieus;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.Milieu;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.MilieuInfo;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.accessors.MilieuAccessor;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuElement;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuFactory;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.annotation.MilieuWidget;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.utils.MilieuActivitySupplier;
import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.widget.WidgetFactory;
import net.labymod.api.Textures.SpriteCommon;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.gui.icon.Icon;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.activity.Activity;
import net.labymod.api.client.gui.screen.widget.widgets.input.ButtonWidget;
import net.labymod.api.util.I18n;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Supplier;

@AutoWidget
@MilieuWidget
public class MilieuActivityWidget extends ButtonWidget implements MilieuActivitySupplier {

  private final Supplier<Activity> supplier;
  private final String translationKey;

  private MilieuActivityWidget(String translationKey, Supplier<Activity> supplier) {
    this.translationKey = translationKey;
    this.supplier = supplier;
  }

  @Override
  public void initialize(Parent parent) {
    String translation = I18n.getTranslation(this.translationKey);

    if(translation == null) {
      this.component = null;
      icon().set(Icon.sprite16(SpriteCommon.TEXTURE, 2, 0));
    } else {
      this.component = Component.text(translation);
      icon().set(null);
    }

    super.initialize(parent);
  }

  @Override
  public Activity activity(Milieu milieu) {
    return this.supplier.get();
  }

  @MilieuFactory
  public static class Factory implements WidgetFactory<ActivityMilieu, MilieuActivityWidget> {

    @Override
    public MilieuActivityWidget[] create(Milieu milieu, ActivityMilieu annotation, MilieuInfo<?> info, MilieuAccessor accessor) {
      MilieuActivityWidget activityMilieuWidget = new MilieuActivityWidget(milieu.getTranslationKey() + ".text", invokeButtonPress(info));
      return new MilieuActivityWidget[] { activityMilieuWidget };
    }

    @Override
    public Class<?>[] types() {
      return new Class[0];
    }

    private Supplier<Activity> invokeButtonPress(MilieuInfo<?> milieuInfo) {
      return () -> {
        try {
          return (Activity) ((Method) milieuInfo.member()).invoke(milieuInfo.config());
        } catch(IllegalAccessException | InvocationTargetException exception) {
          exception.printStackTrace();
          return null;
        }
      };
    }
  }

  @MilieuElement
  @Target({ ElementType.METHOD })
  @Retention(RetentionPolicy.RUNTIME)
  public static @interface ActivityMilieu { }

}
