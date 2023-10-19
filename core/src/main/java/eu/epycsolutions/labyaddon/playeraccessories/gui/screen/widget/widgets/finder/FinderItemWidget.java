package eu.epycsolutions.labyaddon.playeraccessories.gui.screen.widget.widgets.finder;

import eu.epycsolutions.labyaddon.playeraccessories.environ.finder.FinderController;
import eu.epycsolutions.labyaddon.playeraccessories.environ.finder.FinderEnviron;
import eu.epycsolutions.labyaddon.playeraccessories.environ.finder.index.FinderIndex.FinderIndexEnviron;
import net.labymod.api.Textures.SpriteServerSelection;
import net.labymod.api.client.component.Component;
import net.labymod.api.client.component.TextComponent;
import net.labymod.api.client.component.TranslatableComponent;
import net.labymod.api.client.gui.lss.property.LssProperty;
import net.labymod.api.client.gui.lss.property.annotation.AutoWidget;
import net.labymod.api.client.gui.screen.Parent;
import net.labymod.api.client.gui.screen.widget.SimpleWidget;
import net.labymod.api.client.gui.screen.widget.action.Pressable;
import net.labymod.api.client.gui.screen.widget.widgets.ComponentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.RatingWidget;
import net.labymod.api.client.gui.screen.widget.widgets.layout.FlexibleContentWidget;
import net.labymod.api.client.gui.screen.widget.widgets.renderer.VariableIconWidget;
import net.labymod.api.client.sound.SoundType;
import net.labymod.api.util.I18n;

@AutoWidget
public class FinderItemWidget extends SimpleWidget {

  private final Pressable finderPressable;
  private FinderEnviron environ;

  private final LssProperty<Integer> installedColor = new LssProperty<>(0);
  // TODO: PLANED?: private final LssProperty<Integer> restartColor = new LssProperty<>(0);
  // TODO: Add function to "delete" environs: private final LssProperty<Integer> deletedColor = new LssProperty<>(0);

  private final String loadingString;
  private boolean loadingEnviron;

  public FinderItemWidget(FinderEnviron environ, Pressable pressable) {
    this.environ = environ;
    this.finderPressable = pressable;

    this.loadingString = I18n.translate("");

    this.lazy = true;
  }

  @Override
  public void initialize(Parent parent) {
    super.initialize(parent);

    loadEnviron();
    initializeStoreItem();
  }

  protected void initializeStoreItem() {
    TranslatableComponent translatableComponent = null;
    if(this.environ == null) return;

    FlexibleContentWidget container = new FlexibleContentWidget();
    container.addId("item-container");

    FinderEnviron.Image icon = this.environ.getIcon();

    VariableIconWidget iconWidget = new VariableIconWidget(
        SpriteServerSelection.TEXTURE, // TODO: Replace Texture for this
        (icon == null) ? null : icon.getIconUrl(),
        FinderController::getVariableBrandUrl
    );

    iconWidget.setCleanupOnDispose(true);
    iconWidget.addId("item-icon");
    container.addContent(iconWidget);

    FlexibleContentWidget textContainer = new FlexibleContentWidget();
    textContainer.addId("item-text-container");

    TextComponent textComponent = Component.text(this.environ.getName());
    if(this.environ instanceof FinderIndexEnviron) {
      translatableComponent = Component.translatable("player-accessories.environs.loading", textComponent);
    }


    ComponentWidget nameWidget = ComponentWidget.component(translatableComponent);
    nameWidget.addId("item-name");
    textContainer.addContent(nameWidget);

    String authorName = this.environ.getAuthor();
    ComponentWidget authorWidget = ComponentWidget.i18n("player-accessories.finder.store.environs.author.name", authorName);
    authorWidget.addId("item-author");

    textContainer.addContent(authorWidget);

    if(!(this.environ instanceof FinderIndexEnviron)) {
      FinderEnviron.Rating rating = this.environ.getRating();
      textContainer.addContent(new RatingWidget(rating.getRating(), rating.getCount()));
    }

    ComponentWidget descriptionWidget = ComponentWidget.text(this.environ.getShortDescription());
    descriptionWidget.addId("item-description");

    textContainer.addContent(descriptionWidget);
    container.addFlexibleContent(textContainer);

    if(this.finderPressable != null && !(this.environ instanceof FinderIndexEnviron)) {
      setPressable(this.finderPressable);
    }

    addChild(container);
  }

  @Override
  public void handleAttributes() {
    super.handleAttributes();
    setBackgroundColor();
  }

  @Override
  protected SoundType getInteractionSound() {
    return SoundType.BUTTON_CLICK;
  }

  private void setBackgroundColor() {
    if(this.environ != null && this.environ.isInstalled()) {
      // Integer deletedColor = this.deletedColor.get();

      /**
       * TODO: Add function to "delete" environs
       * if(this.environ.isDeleted() && deletedColor != 0) {
       *   backgroundColor().set(deletedColor);
       * } else {
       *  Integer installedColor = this.installedColor.get();
       *  if(installedColor != 0) {
       *    backgroundColor().set(installedColor);
       *  }
       * }
       */

      Integer installedColor = this.installedColor.get();
      if(installedColor != 0) {
        backgroundColor().set(installedColor);
      }
    }
  }

  public FinderEnviron environ() {
    return this.environ;
  }

  public Pressable finderPressable() {
    return this.finderPressable;
  }

  public LssProperty<Integer> installedColor() {
    return this.installedColor;
  }

  protected void setEnviron(FinderEnviron environ) {
    this.environ = environ;
  }

  protected void loadEnviron() {
    if(!this.loadingEnviron || this.environ == null) {
      return;
    }

    if(!(this.environ instanceof FinderIndexEnviron)) {
      return;
    }

    FinderEnviron envi = this.environ.loadEnviron((result) -> {
      if(!result.isPresent() || !(this.environ instanceof FinderIndexEnviron)) {
        return;
      }

      this.environ = result.get();

      this.loadingEnviron = false;
      this.labyAPI.minecraft().executeOnRenderThread(this::reInitialize);
    });

    if(envi != null) {
      this.environ = envi;
    } else {
      this.loadingEnviron = true;
    }
  }

}
