package com.example.addon.modules;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.friends.Friends;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.player.PlayerEntity;

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
            .description("Дистанция до врага.")
            .defaultValue(2.5)
            .min(0.5)
            .sliderMax(6.0)
            .build()
    );

    public TargetStrafe() {
        super(new Category("HvH"), "target-strafe", "Автоматически наводит и следит за ближайшим игроком.");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (mc.player == null || mc.world == null) return;

        PlayerEntity target = getClosestPlayer();

        if (target != null) {
            // Плавное/точное вращение взгляда на цель через Meteor API
            Rotations.rotate(Rotations.getYaw(target), Rotations.getPitch(target));
        }
    }

    private PlayerEntity getClosestPlayer() {
        PlayerEntity closest = null;
        double closestDistance = range.get();

        for (PlayerEntity player : mc.world.getPlayers()) {
            if (player == mc.player) continue;
            if (player.isDead() || player.getHealth() <= 0) continue;
            if (!Friends.get().shouldAttack(player)) continue;

            double distance = mc.player.distanceTo(player);
            if (distance < closestDistance) {
                closestDistance = distance;
                closest = player;
            }
        }

        return closest;
    }
}
