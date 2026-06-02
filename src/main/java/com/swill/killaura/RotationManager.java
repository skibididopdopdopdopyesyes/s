package com.swill.killaura;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RotationManager {
    private static float currentYaw, currentPitch;
    private static long lastRotationTime = 0;
    
    public static void rotateToEntity(LivingEntity target, boolean smooth) {
        float[] rotations = getRotationsToEntity(target);
        if (smooth) {
            smoothRotate(rotations[0], rotations[1]);
        } else {
            setRotation(rotations[0], rotations[1]);
        }
    }
    
    private static float[] getRotationsToEntity(Entity target) {
        Vec3d eyePos = MinecraftClient.getInstance().player.getEyePos();
        Vec3d targetPos = target.getBoundingBox().getCenter();
        
        double dx = targetPos.x - eyePos.x;
        double dy = targetPos.y - eyePos.y;
        double dz = targetPos.z - eyePos.z;
        
        double dh = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90;
        float pitch = (float) -Math.toDegrees(Math.atan2(dy, dh));
        
        return new float[]{yaw, pitch};
    }
    
    private static void smoothRotate(float targetYaw, float targetPitch) {
        float currentYaw = MinecraftClient.getInstance().player.yaw;
        float currentPitch = MinecraftClient.getInstance().player.pitch;
        
        float deltaYaw = MathHelper.wrapDegrees(targetYaw - currentYaw);
        float deltaPitch = targetPitch - currentPitch;
        
        deltaYaw = MathHelper.clamp(deltaYaw, -4, 4);
        deltaPitch = MathHelper.clamp(deltaPitch, -4, 4);
        
        setRotation(currentYaw + deltaYaw, currentPitch + deltaPitch);
    }
    
    private static void setRotation(float yaw, float pitch) {
        MinecraftClient.getInstance().player.yaw = yaw;
        MinecraftClient.getInstance().player.pitch = pitch;
    }
    
    public static boolean isLookingAtEntity(Entity target, float tolerance) {
        float[] needed = getRotationsToEntity(target);
        float currentYaw = MinecraftClient.getInstance().player.yaw;
        float currentPitch = MinecraftClient.getInstance().player.pitch;
        
        return Math.abs(MathHelper.wrapDegrees(needed[0] - currentYaw)) <= tolerance &&
               Math.abs(needed[1] - currentPitch) <= tolerance;
    }
    
    public static void reset() {
        // Ничего не делаем, сохраняем текущие повороты
    }
}
