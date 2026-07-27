package shame.nazuna.client.modules.impl.render;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.util.ArrayList;
 import java.util.List;
 import java.util.Random;
 import net.minecraft.class_1297;
 import net.minecraft.class_1684;
 import net.minecraft.class_1685;
 import net.minecraft.class_1922;
 import net.minecraft.class_2338;
 import net.minecraft.class_2374;
 import net.minecraft.class_238;
 import net.minecraft.class_243;
 import net.minecraft.class_2596;
 import net.minecraft.class_2663;
 import net.minecraft.class_2680;
 import net.minecraft.class_286;
 import net.minecraft.class_287;
 import net.minecraft.class_289;
 import net.minecraft.class_2960;
 import net.minecraft.class_310;
 import net.minecraft.class_3532;
 import net.minecraft.class_3959;
 import net.minecraft.class_3965;
 import net.minecraft.class_4587;
 import net.minecraft.class_7833;
 import org.joml.Matrix4f;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.api.events.implement.EventAttackEntity;
 import shame.nazuna.api.events.implement.EventPacket;
 import shame.nazuna.api.utils.color.ColorUtils;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 import shame.nazuna.client.modules.settings.implement.ListSetting;
 import shame.nazuna.client.modules.settings.implement.ModeSetting;
 
 public class Particle extends Module {
   public static Particle INSTANCE = new Particle();
   private static final class_2960 STAR_TEXTURE = class_2960.method_60655("astra", "textures/particle/star.png");
   private static final class_2960 HEART_TEXTURE = class_2960.method_60655("astra", "textures/particle/heart.png");
   private static final class_2960 DOLLAR_TEXTURE = class_2960.method_60655("astra", "textures/particle/dollar.png");
   private static final class_2960 BLOOM_TEXTURE = class_2960.method_60655("astra", "textures/particle/bloom.png");
   private static final class_2960 SPARKLE_TEXTURE = class_2960.method_60655("astra", "textures/particle/sparkle.png");
   
   private final ModeSetting type = new ModeSetting("Тип частиц", "Звездочки", new String[] { "Звездочки", "Сердечки", "Доллары", "Блум", "Сияние" });
 
   
   private final ListSetting reason = new ListSetting("Добавлять при", new BooleanSetting[] { new BooleanSetting("Бездействии", false), new BooleanSetting("Беге", false), new BooleanSetting("Ударе", true), new BooleanSetting("Падении перла", false), new BooleanSetting("Падении трезубца", false), new BooleanSetting("Сносе тотема", true) });
 
 
 
 
 
 
   
   private final FloatSetting count = new FloatSetting("Количество", 10.0F, 2.0F, 40.0F, 1.0F);
   
   private final BooleanSetting glow = new BooleanSetting("Свечение", true);
   
   private final ArrayList<ParticleData> particles = new ArrayList<>();
   private final Random rnd = new Random();
   
   public Particle() {
     super("Particles", "Красивые партиклы при разных действиях", Module.ModuleCategory.RENDER);
     addSettings(new Setting[] { (Setting)this.type, (Setting)this.reason, (Setting)this.count, (Setting)this.glow });
   }
 
   
   public void onDisable() {
     this.particles.clear();
     super.onDisable();
   }
   
   private class_2960 getTexture() {
     switch (this.type.getIndex()) { case 1: case 2: case 3: case 4:  }  return 
 
 
 
       
       STAR_TEXTURE;
   }
 
   
   private boolean isPositionInBlock(class_243 position) {
     if (mc.field_1687 == null || mc.field_1724 == null) return true; 
     class_2338 blockPos = class_2338.method_49638((class_2374)position);
     if (mc.field_1687.method_8320(blockPos).method_26212((class_1922)mc.field_1687, blockPos)) {
       return true;
     }
     
     class_3959 context = new class_3959(new class_243(mc.field_1724.method_23317(), mc.field_1724.method_23318() + mc.field_1724.method_5751(), mc.field_1724.method_23321()), position, class_3959.class_3960.field_17558, class_3959.class_242.field_1348, (class_1297)mc.field_1724);
 
 
 
 
     
     class_3965 result = mc.field_1687.method_17742(context);
     return (result.method_17783() == class_239.class_240.field_1332);
   }
   
   private float random(float min, float max) {
     return min + this.rnd.nextFloat() * (max - min);
   }
   
   private boolean isMoving() {
     return (mc.field_1724 != null && (mc.field_1724.field_3913.field_3905 != 0.0F || mc.field_1724.field_3913.field_3907 != 0.0F));
   }
   
   @EventLink
   public void onAttack(EventAttackEntity event) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return;  if (this.reason.is("Ударе")) {
       
       class_1297 target = event.getTarget();
       if (target != null)
         for (int i = 0; i < 35; i++) {
           double targetX = target.method_23317() + random(-0.4F, 0.4F);
           double targetY = target.method_23318() + random(-0.4F, target.method_17682() + 0.4F);
           double targetZ = target.method_23321() + random(-0.4F, 0.4F);
           
           if (!isPositionInBlock(new class_243(targetX, targetY, targetZ))) {
             
             float baseMx = random(-0.8F, 0.8F) * 2.0F;
             float baseMy = random(-0.25F, 1.4F);
             float baseMz = random(-0.8F, 0.8F) * 2.0F;
             
             class_243 velocity = new class_243((baseMx * 0.075F), (baseMy * 0.075F), (baseMz * 0.075F));
             long life = (long)random(1000.0F, 1200.0F);
             
             addParticle(targetX, targetY, targetZ, velocity, ColorUtils.getThemeColor(), 0.3F, life, 0.5F, 6.99999975040555E-4D);
           } 
         }  
     } 
   }
   
   @EventLink
   public void onPacket(EventPacket e) {
     if (mc.field_1687 == null || mc.field_1724 == null)
       return;  if (!this.reason.is("Сносе тотема"))
       return; 
     class_2596 class_2596 = e.getPacket(); if (class_2596 instanceof class_2663) { class_2663 packet = (class_2663)class_2596;
       if (packet.method_11470() == 35) {
         class_1297 entity = packet.method_11469((class_1937)mc.field_1687);
         if (entity != null) {
           double centerX = entity.method_23317();
           double centerY = entity.method_23318() + entity.method_17682() / 2.0D;
           double centerZ = entity.method_23321();
           
           for (int i = 0; i < 50; i++) {
             double theta = this.rnd.nextDouble() * 2.0D * Math.PI;
             double phi = this.rnd.nextDouble() * Math.PI;
             double speed = (this.rnd.nextDouble() * 0.5D + 0.5D) * 0.1D;
             
             double vx = Math.sin(phi) * Math.cos(theta) * speed;
             double vy = Math.sin(phi) * Math.sin(theta) * speed;
             double vz = Math.cos(phi) * speed;
             
             double spawnX = centerX + random(-0.3F, 0.3F);
             double spawnY = centerY + random(-0.3F, 0.3F);
             double spawnZ = centerZ + random(-0.3F, 0.3F);
             
             if (!isPositionInBlock(new class_243(spawnX, spawnY, spawnZ))) {
               
               int color = (this.rnd.nextDouble() < 0.7D) ? -16711936 : -256;
               long life = (long)random(1500.0F, 2000.0F);
               
               addParticle(spawnX, spawnY, spawnZ, new class_243(vx, vy, vz), color, 0.3F, life, 2.0F, 4.999999873689376E-5D);
             } 
           } 
         } 
       }  }
   
   }
   @EventLink
   public void onUpdate(EventUpdate e) {
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     int particleCount = (int)this.count.get();
     
     if (this.reason.is("Бездействии")) {
       class_243 base = new class_243(mc.field_1724.method_23317(), mc.field_1724.method_23318() + mc.field_1724.method_17682() / 2.0D, mc.field_1724.method_23321());
       
       for (int i = 0; i < particleCount; i++) {
         double distance = random(7.0F, 35.0F);
         double angle = Math.toRadians(random(0.0F, 360.0F));
         double height = random(-7.0F, 25.0F);
         
         double spawnX = base.field_1352 + Math.cos(angle) * distance;
         double spawnY = base.field_1351 + height;
         double spawnZ = base.field_1350 + Math.sin(angle) * distance;
         
         class_243 spawnPos = new class_243(spawnX, spawnY, spawnZ);
         if (!isPositionInBlock(spawnPos)) {
           
           long life = (long)random(1500.0F, 2000.0F);
           double speed = (this.rnd.nextDouble() < 0.8D) ? random(0.015F, 0.03F) : 0.125D;
           double phi = Math.toRadians(random(0.0F, 360.0F));
 
 
 
           
           class_243 velocity = new class_243(Math.cos(phi) * speed, random((float)(-speed * 0.10000000149011612D), (float)(speed * 0.10000000149011612D)), Math.sin(phi) * speed);
 
           
           addParticle(spawnX, spawnY, spawnZ, velocity, ColorUtils.getThemeColor(), 0.3F, life, 3.0F, 4.999999873689376E-5D);
         } 
       } 
     } 
     if (this.reason.is("Беге") && isMoving()) {
       class_243 direction, motion = mc.field_1724.method_18798();
       double speed = Math.sqrt(motion.field_1352 * motion.field_1352 + motion.field_1350 * motion.field_1350);
 
       
       if (speed < 0.01D) {
         direction = mc.field_1724.method_5720().method_1021(-1.0D);
       } else if (mc.field_1724.method_6128()) {
         direction = motion.method_1029().method_1021(-1.0D);
       } else {
         direction = new class_243(-motion.field_1352 / speed, 0.0D, -motion.field_1350 / speed);
       } 
       
       double distanceBehind = (mc.field_1724.method_6128() ? 1.2D : 0.5D) + ((speed > 0.1D) ? (speed * 1.5D) : 0.0D);
       double offsetX = random(-0.35F, 0.35F);
       double offsetZ = random(-0.35F, 0.35F);
       
       double posX = mc.field_1724.method_23317() + direction.field_1352 * distanceBehind + offsetX;
 
       
       double posY = mc.field_1724.method_6128() ? (mc.field_1724.method_23318() + mc.field_1724.method_17682() / 2.0D + direction.field_1351 * distanceBehind + random(-0.35F, 0.35F)) : (mc.field_1724.method_23318() + random(0.2F, mc.field_1724.method_17682() + 0.1F));
       double posZ = mc.field_1724.method_23321() + direction.field_1350 * distanceBehind + offsetZ;
       
       if (!isPositionInBlock(new class_243(posX, posY, posZ))) {
         double baseSpeed = 0.075D;
 
 
 
         
         class_243 velocity = direction.method_1021(baseSpeed).method_1031(random(-0.01F, 0.01F), random(-0.05F, 0.01F), random(-0.01F, 0.01F)).method_1021(0.1D);
         
         long life = (long)random(1500.0F, 2000.0F);
         addParticle(posX, posY, posZ, velocity, ColorUtils.getThemeColor(), 0.3F, life, 3.0F, 4.999999873689376E-5D);
       } 
     } 
     
     boolean trackPearls = this.reason.is("Падении перла");
     boolean trackTridents = this.reason.is("Падении трезубца");
     if (trackPearls || trackTridents) {
       class_238 searchBox = mc.field_1724.method_5829().method_1014(100.0D);
       List<class_1297> entities = mc.field_1687.method_8333(null, searchBox, e2 -> true);
       
       for (class_1297 entity : entities) {
         if (trackPearls && entity instanceof class_1684) { class_1684 pearl = (class_1684)entity;
           if (!pearl.method_24828()) {
             createProjectileParticles(pearl.method_19538(), 1);
           } }
 
         
         if (trackTridents && entity instanceof class_1685) { class_1685 trident = (class_1685)entity;
           if (trident.method_18798().method_1027() > 0.01D) {
             createProjectileParticles(trident.method_19538(), 1);
           } }
       
       } 
     } 
   }
   
   private void createProjectileParticles(class_243 position, int cnt) {
     int particleColor = ColorUtils.getThemeColor();
     
     for (int i = 0; i < cnt * 2.5D; i++) {
       double dy = random(0.1F, 0.35F);
       class_243 particlePos = new class_243(position.field_1352, position.field_1351 + dy, position.field_1350);
       
       if (!isPositionInBlock(particlePos)) {
         
         float speedMin = random(0.015F, 0.0375F);
         float speedMax = random(0.05F, 0.075F);
         double speedFinal = random(speedMin, speedMax);
         double speedFinalY = speedFinal * 0.4D;
         
         double angleVel = Math.toRadians(random(0.0F, 360.0F));
 
 
 
         
         class_243 velocity = new class_243(Math.cos(angleVel) * speedFinal, random((float)-speedFinalY, (float)speedFinalY), Math.sin(angleVel) * speedFinal);
 
         
         long life = (long)random(2400.0F, 2800.0F);
         addParticle(particlePos.field_1352, particlePos.field_1351, particlePos.field_1350, velocity, particleColor, 0.25F, life, 2.0F, 4.999999873689376E-5D);
       } 
     } 
   }
   private void addParticle(double x, double y, double z, class_243 velocity, int color, float size, long lifeTime, float smooth, double gravity) {
     if (ParticleData.checkCollision(x, y, z, size, mc))
       synchronized (this.particles) {
         this.particles.add(new ParticleData(new class_243(x, y, z), velocity, color, size, lifeTime, smooth, gravity));
       }  
   }
   
   @EventLink
   public void onRender3D(Event3DRender e) {
     ArrayList<ParticleData> renderList;
     if (mc.field_1724 == null || mc.field_1687 == null)
       return; 
     synchronized (this.particles) {
       this.particles.removeIf(ParticleData::isDead);
     } 
     
     if (this.particles.isEmpty())
       return; 
     class_4587 matrices = e.getMatrices();
     class_243 camera = mc.field_1773.method_19418().method_19326();
     class_2960 texture = getTexture();
     
     RenderSystem.enableBlend();
     RenderSystem.disableDepthTest();
     RenderSystem.depthMask(false);
     RenderSystem.disableCull();
     
     if (this.glow.isState()) {
       RenderSystem.blendFunc(770, 1);
     } else {
       RenderSystem.defaultBlendFunc();
     } 
     
     RenderSystem.setShaderTexture(0, texture);
     RenderSystem.setShader(class_10142.field_53880);
 
     
     synchronized (this.particles) {
       renderList = new ArrayList<>(this.particles);
     } 
     
     for (ParticleData particle : renderList) {
       particle.update(mc);
       
       double x = particle.position.field_1352 - camera.field_1352;
       double y = particle.position.field_1351 - camera.field_1351;
       double z = particle.position.field_1350 - camera.field_1350;
       
       matrices.method_22903();
       matrices.method_46416((float)x, (float)y, (float)z);
       matrices.method_22907(class_7833.field_40716.rotationDegrees(-mc.field_1773.method_19418().method_19330()));
       matrices.method_22907(class_7833.field_40714.rotationDegrees(mc.field_1773.method_19418().method_19329()));
       
       Matrix4f matrix = matrices.method_23760().method_23761();
       
       float half = particle.size / 2.0F;
       int alpha = (int)(particle.alpha * 255.0F);
       int r = particle.color >> 16 & 0xFF;
       int g = particle.color >> 8 & 0xFF;
       int b = particle.color & 0xFF;
       
       class_287 buffer = class_289.method_1348().method_60827(class_293.class_5596.field_27382, class_290.field_1575);
       
       buffer.method_22918(matrix, -half, -half, 0.0F).method_22913(0.0F, 1.0F).method_1336(r, g, b, alpha);
       buffer.method_22918(matrix, -half, half, 0.0F).method_22913(0.0F, 0.0F).method_1336(r, g, b, alpha);
       buffer.method_22918(matrix, half, half, 0.0F).method_22913(1.0F, 0.0F).method_1336(r, g, b, alpha);
       buffer.method_22918(matrix, half, -half, 0.0F).method_22913(1.0F, 1.0F).method_1336(r, g, b, alpha);
       
       class_286.method_43433(buffer.method_60800());
       
       matrices.method_22909();
     } 
     
     RenderSystem.enableCull();
     RenderSystem.depthMask(true);
     RenderSystem.enableDepthTest();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableBlend();
   }
   
   static class ParticleData {
     class_243 position;
     class_243 velocity;
     int color;
     float size;
     long lifeTime;
     long birthTime;
     float alpha = 1.0F;
     float smoothFactor;
     long lastUpdateNs;
     double gravity;
     
     ParticleData(class_243 position, class_243 velocity, int color, float size, long lifeTime, float smooth, double gravity) {
       this.position = position;
       this.velocity = velocity;
       this.color = color;
       this.size = size;
       this.lifeTime = lifeTime;
       this.birthTime = System.currentTimeMillis();
       this.lastUpdateNs = System.nanoTime();
       this.smoothFactor = smooth;
       this.gravity = gravity;
     }
     
     boolean isDead() {
       return (System.currentTimeMillis() - this.birthTime >= this.lifeTime);
     }
     
     void update(class_310 mc) {
       long nowNs = System.nanoTime();
       double deltaSec = (nowNs - this.lastUpdateNs) / 1.0E9D;
       this.lastUpdateNs = nowNs;
       
       float progress = Math.min(1.0F, (float)(System.currentTimeMillis() - this.birthTime) / (float)this.lifeTime);
       double factor = Math.pow(1.0D - progress, this.smoothFactor);
       
       double vx = this.velocity.field_1352;
       double vy = this.velocity.field_1351;
       double vz = this.velocity.field_1350;
       
       double newX = this.position.field_1352;
       double newY = this.position.field_1351;
       double newZ = this.position.field_1350;
       
       newX += vx * factor * deltaSec * 60.0D;
       if (!checkCollision(newX, this.position.field_1351, this.position.field_1350, this.size, mc)) {
         vx = -vx * 0.8D;
         newX = this.position.field_1352;
       } 
       
       newY += vy * factor * deltaSec * 60.0D;
       if (!checkCollision(newX, newY, this.position.field_1350, this.size, mc)) {
         vy = -vy * 1.5D;
         newY = this.position.field_1351;
       } 
       
       newZ += vz * factor * deltaSec * 60.0D;
       if (!checkCollision(newX, newY, newZ, this.size, mc)) {
         vz = -vz * 0.8D;
         newZ = this.position.field_1350;
       } 
       
       this.position = new class_243(newX, newY, newZ);
       this.velocity = new class_243(vx * 0.9999D, vy * 0.9999D - this.gravity, vz * 0.9999D);
       this.alpha = 1.0F - progress;
     }
     
     static boolean checkCollision(double x, double y, double z, float size, class_310 mc) {
       if (mc.field_1687 == null) return false; 
       double half = size * 0.5D;
       int minX = class_3532.method_15357(x - half);
       int maxX = class_3532.method_15357(x + half);
       int minY = class_3532.method_15357(y - half);
       int maxY = class_3532.method_15357(y + half);
       int minZ = class_3532.method_15357(z - half);
       int maxZ = class_3532.method_15357(z + half);
       
       class_2338.class_2339 pos = new class_2338.class_2339();
       for (int bx = minX; bx <= maxX; bx++) {
         for (int by = minY; by <= maxY; by++) {
           for (int bz = minZ; bz <= maxZ; bz++) {
             pos.method_10103(bx, by, bz);
             class_2680 state = mc.field_1687.method_8320((class_2338)pos);
             if (!state.method_26215() && state.method_26212((class_1922)mc.field_1687, (class_2338)pos)) {
               return false;
             }
           } 
         } 
       } 
       return true;
     }
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\client\modules\impl\render\Particle.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */