package me.danny125.byteutilitymod.modules.combat;

import me.danny125.byteutilitymod.modules.Module;
import me.danny125.byteutilitymod.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

public class KillAura extends Module {

    public NumberSetting cooldown = new NumberSetting("Cooldown",625.0,1.0,2000.0,50.0,"ms");
    public NumberSetting range = new NumberSetting("Range",4.0,1.0,6.0,1.0,"blocks");

    public KillAura() {
        super("KillAura", GLFW.GLFW_KEY_R,CATEGORY.COMBAT,false);
        this.addSettings(cooldown,range);
    }

    public long lastMS = System.currentTimeMillis();

    public void reset() {
        this.lastMS = System.currentTimeMillis();
    }

    public boolean hasTimeElapsed(long time, boolean reset) {
        if (System.currentTimeMillis() - this.lastMS > time) {
            if (reset) {
                reset();
            }
            return true;
        }

        return false;
    }

    public void setTime(long Time) {
        this.lastMS = Time;
    }

    public long getTime() {
        return System.currentTimeMillis() - this.lastMS;
    }

    private float[] calculateLookAt(PlayerEntity player, Entity target) {
        double deltaX = target.getX() - player.getX();
        double deltaY = (target.getY() + target.getEyeHeight(player.getPose())) - (player.getY() + player.getEyeHeight(player.getPose()));
        double deltaZ = target.getZ() - player.getZ();

        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) (Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0F);
        float pitch = (float) -Math.toDegrees(Math.atan2(deltaY, distance));

        return new float[]{yaw, pitch};
    }

    @Override
    public void onTick(CallbackInfo info) {
        super.onTick(info);
        if(this.toggled) {
            if(MinecraftClient.getInstance().world != null) {
                PlayerEntity player = MinecraftClient.getInstance().player;

                if (!player.isAlive() || player.isSpectator()) {
                    return;
                }

                double auraRange = range.getValue();

                List<Entity> nearbyEntities = MinecraftClient.getInstance().world.getOtherEntities(player, player.getBoundingBox().expand(auraRange));

                if(!nearbyEntities.isEmpty()) {
                    for (Entity entity : nearbyEntities) {
                        if (entity instanceof LivingEntity && entity.isAlive() && !entity.isTeammate(player)) {
                            if (hasTimeElapsed((long)(cooldown.getValue())+1, true)) {
                                float[] rotations = calculateLookAt(player, entity);
                                float originalYaw = player.getYaw();
                                float originalPitch = player.getPitch();
                                player.setYaw(rotations[0]);
                                player.setPitch(rotations[1]);
                                MinecraftClient.getInstance().interactionManager.attackEntity(player,entity);
                                player.swingHand(Hand.MAIN_HAND);
                                player.setYaw(originalYaw);
                                player.setPitch(originalPitch);
                            }
                        }
                    }
                }
            }
        }
    }
}
