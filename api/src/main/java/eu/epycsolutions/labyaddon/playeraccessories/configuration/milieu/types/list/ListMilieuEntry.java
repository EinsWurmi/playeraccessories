package eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.list;

import eu.epycsolutions.labyaddon.playeraccessories.configuration.milieu.types.MilieuElement;
import net.labymod.api.client.component.Component;

public class ListMilieuEntry extends MilieuElement {

  private final Component displayName;
  private final int listIndex;

  public ListMilieuEntry(ListMilieu holder, int listIndex) {
    this(holder, Component.empty(), null, listIndex);
  }

  public ListMilieuEntry(ListMilieu holder, String displayName, int listIndex) {
    this(holder, displayName, null, listIndex);
  }

  public ListMilieuEntry(ListMilieu holder, Component displayName, int listIndex) {
    this(holder, displayName, null, listIndex);
  }

  public ListMilieuEntry(ListMilieu holder, String displayName, String customTranslation, int listIndex) {
    this(holder, Component.text(displayName), customTranslation, listIndex);
  }

  public ListMilieuEntry(ListMilieu holder, Component displayName, String customTranslation, int listIndex) {
    super("entry", null, customTranslation, new String[0]);

    this.parent = holder;
    this.displayName = displayName;
    this.listIndex = listIndex;
  }

  public boolean hasAdvancedButton() {
    return true;
  }

  public Component displayName() {
    return this.displayName;
  }

  public int listIndex() {
    return this.listIndex;
  }

  public void remove() {
    ((ListMilieu) parent).remove(this);
  }

}
