package shame.nazuna.mixin;
 import java.util.Comparator;
 import java.util.List;
 import net.minecraft.class_1297;
 import net.minecraft.class_1657;
 import net.minecraft.class_2561;
 import net.minecraft.class_266;
 import net.minecraft.class_268;
 import net.minecraft.class_269;
 import net.minecraft.class_270;
 import net.minecraft.class_329;
 import net.minecraft.class_332;
 import net.minecraft.class_5250;
 import net.minecraft.class_5251;
 import net.minecraft.class_5348;
 import net.minecraft.class_9011;
 import net.minecraft.class_9022;
 import net.minecraft.class_9025;
 import net.minecraft.class_9779;
 import org.spongepowered.asm.mixin.Final;
 import org.spongepowered.asm.mixin.Mixin;
 import org.spongepowered.asm.mixin.Shadow;
 import org.spongepowered.asm.mixin.injection.At;
 import org.spongepowered.asm.mixin.injection.Inject;
 import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
 import shame.nazuna.api.QClient;
 import shame.nazuna.api.events.EventInvoker;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.SidebarEntry;
 import shame.nazuna.client.modules.impl.misc.NameProtect;
 
 @Mixin({class_329.class})
 public class InGameGuiMixin implements QClient {
   private static final int DOMAIN_COLOR = 15557921;
   
   @Inject(method = {"method_1735"}, at = {@At("HEAD")}, cancellable = true)
   private void renderVignetteOverlay(class_332 context, class_1297 entity, CallbackInfo ci) {
     if (ModuleClass.noVignette.isEnable())
       ci.cancel(); 
   } @Shadow
   @Final
   private class_310 field_2035;
   @Inject(method = {"method_1753"}, at = {@At("HEAD")})
   private void render(class_332 context, class_9779 tickCounter, CallbackInfo ci) {
     BlurProgram.getInstance().beginFrame();
     if (EventInvoker.hasListeners(EventRender.Default.class)) {
       (new EventRender.Default(context, tickCounter.method_60637(true))).call();
     }
   }
 
 
 
 
 
 
 
 
   
   @Shadow
   private class_1657 method_1737() {
     return null;
   }
 
 
 
 
 
   
   @Inject(method = {"method_1757"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$renderPatchedScoreboard(class_332 drawContext, class_266 objective, CallbackInfo ci) {
     if (!astra$shouldPatchScoreboard()) {
       return;
     }
     
     class_269 scoreboard = objective.method_1117();
     class_9022 numberFormat = objective.method_55380((class_9022)class_9025.field_47567);
 
 
 
 
 
 
 
 
 
 
 
 
     
     List<SidebarEntry> lines = scoreboard.method_1184(objective).stream().filter(entry -> !entry.method_55385()).sorted(Comparator.comparing(class_9011::comp_2128).reversed().thenComparing(class_9011::comp_2127, String.CASE_INSENSITIVE_ORDER)).limit(15L).map(entry -> { class_268 team = scoreboard.method_1164(entry.comp_2127()); class_2561 name = astra$patchText((class_2561)class_268.method_1142((class_270)team, entry.method_55387())); class_5250 class_5250 = entry.method_55386(numberFormat); int scoreWidth = this.field_2035.field_1772.method_27525((class_5348)class_5250); return new SidebarEntry(name, (class_2561)class_5250, scoreWidth); }).toList();
     
     class_2561 title = astra$patchText(objective.method_1114());
     int titleWidth = this.field_2035.field_1772.method_27525((class_5348)title);
     int maxWidth = titleWidth;
     int separatorWidth = this.field_2035.field_1772.method_1727(": ");
     
     for (SidebarEntry line : lines) {
       maxWidth = Math.max(maxWidth, this.field_2035.field_1772.method_27525((class_5348)line.name) + ((line.scoreWidth > 0) ? (separatorWidth + line.scoreWidth) : 0));
     }
     
     int lineCount = lines.size();
     int totalHeight = lineCount * 9;
     int bottom = drawContext.method_51443() / 2 + totalHeight / 3;
     int left = drawContext.method_51421() - maxWidth - 3;
     int right = drawContext.method_51421() - 1;
     int bodyColor = this.field_2035.field_1690.method_19345(0.3F);
     int headerColor = this.field_2035.field_1690.method_19345(0.4F);
     int top = bottom - lineCount * 9;
     
     drawContext.method_25294(left - 2, top - 10, right, top - 1, headerColor);
     drawContext.method_25294(left - 2, top - 1, right, bottom, bodyColor);
     drawContext.method_51439(this.field_2035.field_1772, title, left + maxWidth / 2 - titleWidth / 2, top - 9, -1, false);
     
     for (int index = 0; index < lineCount; index++) {
       SidebarEntry line = lines.get(index);
       int y = bottom - (lineCount - index) * 9;
       drawContext.method_51439(this.field_2035.field_1772, line.name, left, y, -1, false);
       drawContext.method_51439(this.field_2035.field_1772, line.score, right - line.scoreWidth, y, -1, false);
     } 
     
     ci.cancel();
   }
   
   private boolean astra$shouldPatchScoreboard() {
     if (ModuleClass.INSTANCE != null) if (ModuleClass.nameProtect != null) if (ModuleClass.nameProtect
           
           .isEnable());  
     return false;
   }
   private class_2561 astra$patchText(class_2561 text) {
     NameProtect nameProtect = ModuleClass.nameProtect;
     class_2561 patched = nameProtect.patchText(text);
     String patchedString = patched.getString();
     
     if (nameProtect.shouldHideGrief()) {
       if (patchedString.contains("Анархия-")) {
         patchedString = patchedString.replaceAll("Анархия-\\d+", "AstraBETA.fun");
       }
       if (patchedString.contains("ГРИФ #")) {
         patchedString = patchedString.replaceAll("ГРИФ #\\d+", "AstraBETA.fun");
       }
     } 
     
     if (patchedString.equals(patched.getString())) {
       return patched;
     }
     return (class_2561)class_2561.method_43470(patchedString).method_10862(patched.method_10866().method_27703(class_5251.method_27717(15557921)));
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\InGameGuiMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */