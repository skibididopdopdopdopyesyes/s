package com.swill.killaura;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.MinecraftClient;
import java.util.Comparator;
import java.util.List;

public class TargetSelector {
    public static LivingEntity selectBestTarget(List<LivingEntity> targets, Config config) {
        MinecraftClient mc = MinecraftClient.getInstance();
        
        switch (config.priority) {
            case "HEALTH":
                return targets.stream()
                    .min(Comparator.comparingDouble(e -> e.getHealth()))
                    .orElse(null);
            case "DISTANCE":
                return targets.stream()
                    .min(Comparator.comparingDouble(e -> mc.player.distanceTo(e)))
                    .orElse(null);
            case "ARMOR":
                return targets.stream()
                    .min(Comparator.comparingDouble(TargetSelector::getArmorValue))
                    .orElse(null);
            case "CROSSHAIR":
                return targets.stream()
                    .min(Comparator.comparingDouble(e -> getAngleToEntity(e)))
                    .orElse(null);
            default: // NEAREST
                return targets.stream()
                    .min(Comparator.comparingDouble(e -> mc.player.distanceTo(e)))
                    .orElse(null);
        }
    }
    
    private static double getArmorValue(LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            return ((PlayerEntity) entity).getArmor();
        }
        return 0;
    }
    
    private static double getAngleToEntity(LivingEntity target) {
        MinecraftClient mc = MinecraftClient.getInstance();
        return Math.abs(RotationManager.getAngleTo(target));
    }
}
