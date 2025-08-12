package com.guhao.perfect_dodge.camera;

import net.minecraft.world.phys.Vec3;

public class CameraMathUtils {
    // 精确的圆柱坐标转换（与动画数据完全匹配）
    public static Vec3 toCylindrical(Vec3 cartesian) {
        double radius = Math.sqrt(cartesian.x * cartesian.x + cartesian.z * cartesian.z);
        double angle = Math.atan2(-cartesian.z, cartesian.x); // 注意负号
        double height = cartesian.y;
        return new Vec3(radius, angle, height);
    }

    // 精确的笛卡尔坐标转换
    public static Vec3 toCartesian(Vec3 cylindrical) {
        double x = cylindrical.x * Math.cos(cylindrical.y);
        double z = cylindrical.x * Math.sin(-cylindrical.y); // 注意负号
        return new Vec3(x, cylindrical.z, z);
    }

    // 精确的角度差值
    public static float lerpAngle(float start, float end, float progress) {
        float diff = ((end - start + 180) % 360) - 180;
        return start + diff * progress;
    }
}