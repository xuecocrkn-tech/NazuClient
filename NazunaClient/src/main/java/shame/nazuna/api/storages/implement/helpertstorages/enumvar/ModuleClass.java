package shame.nazuna.api.storages.implement.helpertstorages.enumvar;
 
 import java.util.List;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.impl.render.Chams;
 
 
 public class ModuleClass
   extends GlobalObject<Module>
   implements ModuleRewords
 {
   public static ModuleClass INSTANCE = new ModuleClass();
   public static Chams chams = Chams.INSTANCE;
   
   public void initialize() {
     add(new Module[] { (Module)antibot, (Module)aimBot, (Module)airStuck, (Module)arrows, (Module)aura, (Module)autoAccept, (Module)autoArmor, (Module)autoBuy, (Module)autoDuel, (Module)autoEat, (Module)autoLeave, (Module)autoExplosion, (Module)autoForest, (Module)autoJoin, (Module)autoPvp, (Module)nameProtect, (Module)autoFish, (Module)autoJump, (Module)autoBuy, (Module)autoSwap, (Module)autoTool, (Module)autoTotem, (Module)autoTrap, (Module)blockesp, (Module)blockOverlay, (Module)chestStealer, (Module)clientSounds, (Module)clickPearl, (Module)cosmetics, (Module)cubes, (Module)deathCoord, (Module)ecopen, (Module)elytraBoost, (Module)elytraMotion, (Module)elytraSwap, (Module)elytraTarget, (Module)elytraresolver, (Module)entityESP, (Module)fireworkESP, (Module)fastBreak, (Module)fastExp, (Module)flight, (Module)freeCam, (Module)fullBright, (Module)grimGlide, (Module)grimNoFall, (Module)highJump, (Module)helpMessage, (Module)hitBubbles, (Module)hitMarker, (Module)interfaceModule, (Module)interpolateF5, (Module)inventoryWalk, (Module)itemAim, (Module)itemRelease, (Module)itemScroller, (Module)jumpCircle, (Module)killEffect, (Module)kTLeave, (Module)leavetracker, (Module)lineGlyphes, (Module)lockSlot, (Module)lootTracker, (Module)noClip, (Module)noJumpDelay, (Module)nuker, (Module)noPush, (Module)noSlow, (Module)noVelocity, (Module)noVignette, (Module)noControllerWeb, (Module)noWeb, (Module)packetCriticals, (Module)particle, (Module)playerFakeLags, (Module)projectile, (Module)scoreboardHP, (Module)removals, (Module)rPSpoofer, (Module)sattelite, (Module)seeInvisibles, (Module)shaderEsp, (Module)shaderHands, (Module)serverHelper, (Module)shulkerPreview, (Module)sonar, (Module)speed, (Module)spider, (Module)sprint, (Module)step, (Module)swingAnimations, (Module)targetESP, (Module)targetPearl, (Module)timer, (Module)totemAngel, (Module)tpsSync, (Module)tpBack, (Module)tpLoot, (Module)trails, (Module)trajectories, (Module)triggerBot, (Module)viewModel, (Module)worldTweaks, (Module)xCarry });
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   private void add(Module... mod) {
     getObject().addAll(List.of(mod));
   }
 }

