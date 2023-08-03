package eu.epycsolutions.labyaddon.playeraccessories.v1_8_9.mixins;

import eu.epycsolutions.labyaddon.playeraccessories.PlayerAccessories;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ Minecraft.class })
@Implements({ @Interface(iface = net.labymod.api.client.Minecraft.class, prefix = "minecraft$", remap = Interface.Remap.NONE) })
public abstract class MixinMinecraft {

  @Inject(method = {"startGame"}, at = { @At("HEAD") })
  private void accessories$firePreGameStarted(CallbackInfo info) {
    PlayerAccessories.instance().onPreGameStarted();
  }

}
