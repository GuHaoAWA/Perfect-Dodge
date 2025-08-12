package com.guhao.perfect_dodge.camera;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.guhao.perfect_dodge.PDMod;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CameraAnimation {

    public final FloatSheet x;
    public final FloatSheet y;
    public final FloatSheet z;
    public final FloatSheet rx;
    public final FloatSheet ry;
    public final FloatSheet rz;
    public final FloatSheet fov;
    public final float totalTime;
    public CameraAnimation(FloatSheet x, FloatSheet y, FloatSheet z, FloatSheet rx, FloatSheet ry, FloatSheet rz,FloatSheet fov) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rx = rx;
        this.ry = ry;
        this.rz = rz;
        this.fov = fov;

        float tt = Math.max(x.getMaxTime(), y.getMaxTime());
        tt = Math.max(z.getMaxTime(), tt);
        tt = Math.max(rx.getMaxTime(), tt);
        tt = Math.max(ry.getMaxTime(), tt);
        tt = Math.max(fov.getMaxTime(), tt);
        totalTime = tt;
    }

    public Pose getPose(float time) {
        return new Pose(x.getValueByTime(time),
                y.getValueByTime(time),
                z.getValueByTime(time),
                rx.getValueByTime(time),
                ry.getValueByTime(time),
                rz.getValueByTime(time),
                fov.getValueByTime(time)
        );
    }

    public static CameraAnimation load(ResourceLocation resourceLocation) {
        Minecraft mc = Minecraft.getInstance();

        try {
            InputStream is = mc.getResourceManager().getResource(resourceLocation).get().open();
            OutputStream os = new ByteArrayOutputStream();;
            byte[] bytes=new byte[1024];
            int len;
            while ((len=is.read(bytes))!=-1){
                os.write(bytes,0,len);
            }
            is.close();
            String json = os.toString();
            JsonObject animJson = JsonParser.parseString(json).getAsJsonObject();

            FloatSheet x,y,z,rx,ry,rz,fov;
            float timeScale = 1.f;

            if(animJson.has("time_scale"))
                timeScale = animJson.get("time_scale").getAsFloat();

            // pos - time sheet
            JsonObject sheets = animJson.getAsJsonObject("pos");

            if(sheets.isJsonArray()){

                x = new FloatSheet();
                x.getFromJson(sheets.getAsJsonObject("x"), "value");
                y = new FloatSheet();
                y.getFromJson(sheets.getAsJsonObject("y"), "value");
                z = new FloatSheet();
                z.getFromJson(sheets.getAsJsonObject("z"), "value");
            }
            else {

                x = new FloatSheet();
                y = new FloatSheet();
                z = new FloatSheet();
                x.getFromJson(sheets, "x");
                y.getFromJson(sheets, "y");
                z.getFromJson(sheets, "z");
            }

            // rot - time sheet
            sheets = animJson.getAsJsonObject("rot");
            rx = new FloatSheet();
            ry = new FloatSheet();
            rz = new FloatSheet();
            rx.getFromJson(sheets, "rx");
            ry.getFromJson(sheets, "ry");
            rz.getFromJson(sheets, "rz");

            //fov
            sheets = animJson.getAsJsonObject("fov");
            fov = new FloatSheet();
            fov.getFromJson(sheets, "value");

            x.scaleTimes(timeScale);
            y.scaleTimes(timeScale);
            z.scaleTimes(timeScale);
            rx.scaleTimes(timeScale);
            ry.scaleTimes(timeScale);
            rz.scaleTimes(timeScale);
            fov.scaleTimes(timeScale);

            PDMod.LOGGER.info("Load Camera Animation: " + resourceLocation);
            return new CameraAnimation(x,y,z,rx,ry,rz,fov);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class Pose{
        public final Vec3 pos;
        public final float rotY;
        public final float rotX;
        public final float rotZ;
        public final float fov;
        public Pose(Vec3 pos, float rotX, float rotY, float rotZ, float fov){
            this.pos = pos;
            this.rotY = rotY;
            this.rotX = rotX;
            this.rotZ = rotZ;
            this.fov = fov;
        }

        public Pose(float x, float y, float z, float rotX, float rotY,float rotZ,float fov){
            this(new Vec3(x,y,z), rotX, rotY, rotZ, fov);
        }

        @Override
        public String toString() {
            return "Pose{" +
                    "pos=" + pos +
                    ", rotY=" + rotY +
                    ", rotX=" + rotX +
                    ", rotZ=" + rotZ +
                    ", fov=" + fov +
                    '}';
        }
    }

    public static abstract class TimeSheet{
        public float[] timeSheet;
        public int getIndexByTime(float time){
            if (time <= 0) return 0;
            for (int i = 0; i < timeSheet.length; i++) {
                if(timeSheet[i] >= time) return Math.max(i-1, 0);
            }
            return timeSheet.length-1;
        }
        public void getFromJson(JsonObject json){
            timeSheet = JsonUtils.getAsFloatArray(json.getAsJsonArray("time"));
        }

        public float getMaxTime(){
            return timeSheet[timeSheet.length-1];
        }

        public void scaleTimes(float scale){
            for (int i = 0; i < timeSheet.length; i++) {
                timeSheet[i] /= scale;
            }
        }
    }


    public static class FloatSheet extends TimeSheet{
        public float[] floatSheet;
        public float getValueByTime(float time){
            int idx = getIndexByTime(time);

            if(idx == timeSheet.length-1){
                return floatSheet[idx];
            }
            else {
                float t = (timeSheet[idx+1] - timeSheet[idx]);
                if(t > 0.00001f){
                    t = (time - timeSheet[idx]) / t;
                    return floatSheet[idx] * (1.f - t) + floatSheet[idx+1] * t;
                }else {
                    return floatSheet[idx];
                }
            }
        }

        public void getFromJson(JsonObject json, String valueKey) {
            getFromJson(json);
            floatSheet = JsonUtils.getAsFloatArray(json.getAsJsonArray(valueKey));
        }
    }
    ////////////////////////////
    public static CameraAnimation createTransitionAnimation(Pose targetPose, float transitionDuration) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || targetPose == null) return null;

        try {
            // 1. 获取当前精确的第三人称相机位置
            Vec3 currentPos = getThirdPersonCameraPos(mc.player, mc.getFrameTime());
            Vec3 playerPos = mc.player.position();

            // 2. 转换为相对坐标并应用圆柱转换（与动画数据完全一致）
            Vec3 relativePos = currentPos.subtract(playerPos);
            Vec3 cylindricalCurrent = CameraMathUtils.toCylindrical(relativePos);

            // 3. 对目标姿势应用相同的变换
            Vec3 targetPosTransformed = targetPose.pos
                    .yRot((float)Math.toRadians(-CameraEvents.yawLock - 90f));
            Vec3 cylindricalTarget = CameraMathUtils.toCylindrical(targetPosTransformed);

            // 4. 创建精确匹配的过渡动画
            return new CameraAnimation(
                    createFloatSheet(-(float) cylindricalCurrent.x, (float) targetPose.pos.x, transitionDuration), // radius
                    createFloatSheet((float) cylindricalCurrent.y, (float) targetPose.pos.y, transitionDuration), // angle
                    createFloatSheet(-(float) cylindricalCurrent.z, (float) targetPose.pos.z, transitionDuration), // height
                    createFloatSheet(mc.gameRenderer.getMainCamera().getXRot(), targetPose.rotX, transitionDuration),
                    createFloatSheet(mc.gameRenderer.getMainCamera().getYRot(),
                            CameraEvents.yawLock - targetPose.rotY, transitionDuration),
                    createFloatSheet(0, targetPose.rotZ, transitionDuration),
                    createFloatSheet(mc.options.fov().get(), targetPose.fov, transitionDuration)
            );
        } catch (Exception e) {
            PDMod.LOGGER.error("Camera transition error", e);
            return null;
        }
    }

    private static FloatSheet createFloatSheet(float start, float end, float duration) {
        FloatSheet sheet = new FloatSheet();
        sheet.timeSheet = new float[]{0f, duration};
        sheet.floatSheet = new float[]{start, end};
        return sheet;
    }
    private static Vec3 getThirdPersonCameraPos(Player player, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();

        // 1. 计算基础第三人称位置
        float distance = 5.0f;
        Vec3 lookVec = Vec3.directionFromRotation(
                camera.getXRot(),
                camera.getYRot()
        );
        Vec3 idealPos = player.getEyePosition(partialTicks)
                .subtract(lookVec.scale(distance));

        // 2. 精确的碰撞检测
        ClipContext context = new ClipContext(
                player.getEyePosition(partialTicks),
                idealPos,
                ClipContext.Block.VISUAL,
                ClipContext.Fluid.NONE,
                player
        );
        HitResult hit = mc.level.clip(context);

        // 3. 返回调整后的位置
        return hit.getType() == HitResult.Type.MISS ?
                idealPos :
                hit.getLocation().add(0, 0.1, 0); // 微小偏移防止嵌入方块
    }
    public static CameraAnimation createCombinedAnimationWithTransition(CameraAnimation original, float transitionDuration) {
        if (original == null) {
            PDMod.LOGGER.error("Original animation is null");
            return null;
        }

        try {
            CameraAnimation.Pose mainAnimStartPose = original.getPose(0f);
            if (mainAnimStartPose == null) {
                PDMod.LOGGER.error("Failed to get start pose from animation");
                return null;
            }

            CameraAnimation transitionAnim = createTransitionAnimation(mainAnimStartPose, transitionDuration);
            if (transitionAnim == null) {
                PDMod.LOGGER.warn("Failed to create transition animation, using original animation");
                return original; // 回退到原始动画
            }

            return combineAnimations(transitionAnim, original);
        } catch (Exception e) {
            PDMod.LOGGER.error("Error creating combined animation", e);
            return original; // 出错时回退到原始动画
        }
    }
    // 新增方法：合并两个动画
    public static CameraAnimation combineAnimations(CameraAnimation first, CameraAnimation second) {
        // 获取第一个动画的持续时间
        float firstDuration = first.totalTime;

        // 创建合并后的关键帧
        FloatSheet x = combineSheets(first.x, second.x, firstDuration);
        FloatSheet y = combineSheets(first.y, second.y, firstDuration);
        FloatSheet z = combineSheets(first.z, second.z, firstDuration);
        FloatSheet rx = combineSheets(first.rx, second.rx, firstDuration);
        FloatSheet ry = combineSheets(first.ry, second.ry, firstDuration);
        FloatSheet rz = combineSheets(first.rz, second.rz, firstDuration);
        FloatSheet fov = combineSheets(first.fov, second.fov, firstDuration);

        return new CameraAnimation(x, y, z, rx, ry, rz, fov);
    }

    // 新增方法：合并两个FloatSheet
    private static FloatSheet combineSheets(FloatSheet first, FloatSheet second, float timeOffset) {
        FloatSheet combined = new FloatSheet();

        // 创建新的时间轴和值数组
        int totalLength = first.timeSheet.length + second.timeSheet.length;
        combined.timeSheet = new float[totalLength];
        combined.floatSheet = new float[totalLength];

        // 填充第一个动画的数据
        System.arraycopy(first.timeSheet, 0, combined.timeSheet, 0, first.timeSheet.length);
        System.arraycopy(first.floatSheet, 0, combined.floatSheet, 0, first.floatSheet.length);

        // 填充第二个动画的数据，并加上时间偏移
        for (int i = 0; i < second.timeSheet.length; i++) {
            combined.timeSheet[first.timeSheet.length + i] = second.timeSheet[i] + timeOffset;
            combined.floatSheet[first.timeSheet.length + i] = second.floatSheet[i];
        }

        return combined;
    }
}
