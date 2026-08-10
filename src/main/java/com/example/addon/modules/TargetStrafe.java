package com.example.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.world.entity.player.Player;

public class TargetStrafe extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
            .name("range")
            .description("Дистанция поиска игрока.")
            .defaultValue(15.0)
            .min(1.0)
            .sliderMax(30.0)
            .build()
    );

    private final Setting<Double> strafeRadius = sgGeneral.add(new DoubleSetting.Builder()
            .name("strafe-radius")
            .description("Дистанция кружения вокруг врага.")
            .defaultValue(2.5)
            .min(0.5)
            .sliderMax(6.0)
            .build()
    );

    private final Setting<Boolean> autoJump = sgGeneral.add(new BoolSetting.Builder()
            .name("auto-jump")
            .description("Прыгать во время стрейфа.")
            .defaultValue(true)
            .build()
    );

    private int direction = 1;

    public TargetStrafe() {
        super(new Category("HvH"), "target-strafe", "Липнет к врагу и кружится вокруг него.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.level == null) return;

        Player target = getClosestPlayer();

        if (target != null) {
            PlayerUtils.turnToEntity(target);

            if (mc.player.horizontalCollision) {
                direction *= -1;
            }

            double distance = mc.player.distanceTo(target);

            if (distance > strafeRadius.get() + 0.5) {
                mc.options.keyUp.setPressed(true);
                mc.options.keyLeft.setPressed(false);
                mc.options.keyRight.setPressed(false);
            } else {
                mc.options.keyUp.setPressed(distance > strafeRadius.get());
                
                if (direction == 1) {
                    mc.options.keyLeft.setPressed(true);
                    mc.options.keyRight.setPressed(false);
                } else {
                    mc.options.keyLeft.setPressed(false);
                    mc.options.keyRight.setPressed(true);
                }
            }

            if (autoJump.get() && mc.player.onGround()) {
                mc.player.jumpFromGround();
            }

        } else {
            resetKeys();
        }
    }

    private Player getClosestPlayer() {
        Player closest = null;
        double closestDistance = range.get();

        for (Player player : mc.level.players()) {
            if (player == mc.player) continue;
            if (player.isDeadOrDying()) continue;
            if (!Friends.get().shouldAttack(player)) continue;

            double distance = mc.player.distanceTo(player);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = player;
            }
        }

        return closest;
    }

    private void resetKeys() {
        if (mc.options == null) return;
        mc.options.keyUp.setPressed(false);
        mc.options.keyLeft.setPressed(false);
        mc.options.keyRight.setPressed(false);
    }

    @Override
    public void onDeactivate() {
        resetKeys();
    }
}
