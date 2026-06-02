package com.swill.killaura;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class KillAuraModule {
    private boolean enabled = false;
    private long lastAttack = 0;
    private Random random = new Random();
    private MinecraftClient mc = MinecraftClient.getInstance();
    
    private Config config = new Config();
    
    public void toggle() {
        enabled = !enabled;
        if (enabled) {
            RotationManager.reset();
        }
    }
    
    public boolean isEnabled() { return enabled; }
    
    public void onTick() {
        if (mc.player == null || mc.world == null) return;
        if (mc.player.isDead()) return;
        
        List<LivingEntity> targets = getValidTargets();
        if (targets.isEmpty()) return;
        
        LivingEntity target = TargetSelector.selectBestTarget(targets, config);
        if (target == null) return;
        
        if (config.autoRotate) {
            RotationManager.rotateToEntity(target, config.smoothRotations);
        }
        
        long now = System.currentTimeMillis();
        int delay = config.minDelay + random.nextInt(config.maxDelay - config.minDelay);
        
        if (now - lastAttack >= delay) {
            if (config.onlyWhenHoldingWeapon && !isHoldingWeapon()) return;
            
            if (isInRange(target)) {
                attack(target);
                lastAttack = now;
                
                if (config.swingItem) {
                    mc.player.swingHand(Hand.MAIN_HAND);
                }
            }
        }
    }
    
    private List<LivingEntity> getValidTargets() {
        Box box = mc.player.getBoundingBox().expand(config.range);
        return mc.world.getEntitiesByClass(LivingEntity.class, box, entity -> {
            if (entity == mc.player) return false;
            if (entity instanceof PlayerEntity) {
                if (!config.attackPlayers) return false;
                if (config.teams && isTeammate((PlayerEntity) entity)) return false;
            }
            if (entity.isDead()) return false;
            if (entity.getHealth() <= 0) return false;
            if (config.invisibleCheck && entity.isInvisible()) return false;
            if (mc.player.distanceTo(entity) > config.range) return false;
            return true;
        });
    }
    
    private boolean isInRange(LivingEntity target) {
        return mc.player.distanceTo(target) <= config.range + 0.5;
    }
    
    private void attack(LivingEntity target) {
        if (config.onlyWhenLooking && !RotationManager.isLookingAtEntity(target, 45)) return;
        
        mc.interactionManager.attackEntity(mc.player, target);
        
        if (config.autoBlock) {
            mc.options.keyUse.setPressed(true);
            new Thread(() -> {
                try { Thread.sleep(50); } catch (Exception e) {}
                mc.options.keyUse.setPressed(false);
            }).start();
        }
    }
    
    private boolean isHoldingWeapon() {
        return mc.player.getMainHandStack().getItem().isSword() ||
               mc.player.getMainHandStack().getItem().isAxe();
    }
    
    private boolean isTeammate(PlayerEntity other) {
        if (mc.player.getScoreboardTeam() == null) return false;
        return mc.player.getScoreboardTeam() == other.getScoreboardTeam();
    }
}
