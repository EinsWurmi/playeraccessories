package eu.epycsolutions.labyaddon.playeraccessories.v1_12_2.mixins;

import eu.epycsolutions.labyaddon.playeraccessories.PlayerAccessories;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ Minecraft.class })
public abstract class MixinMinecraft {

  @Inject(method = { "init" }, at = { @At("HEAD") })
  private void accessories$firePreGameStarted(CallbackInfo info) {
    PlayerAccessories.instance().onPreGameStarted();
  }

}
