package eu.epycsolutions.labyaddon.playeraccessories.v1_16_5.mixins;

import eu.epycsolutions.labyaddon.playeraccessories.PlayerAccessories;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.io.File;

@Mixin({ Minecraft.class })
public abstract class MixinMinecraft {

  @Redirect(method = { "<init>" }, at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;gameDirectory:Ljava/io/File;", shift = Shift.BEFORE, ordinal = 0))
  private File accessories$firePreGameStarted(GameConfig.FolderData instance) {
    PlayerAccessories.instance().onPreGameStarted();
    return instance.gameDirectory;
  }

}
