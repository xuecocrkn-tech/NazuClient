package shame.nazuna.mixin;
 import java.util.Comparator;
 import java.util.List;
 import net.minecraft.Entity;
 import net.minecraft.PlayerEntity;
 import net.minecraft.Text;
 import net.minecraft.ScoreboardObjective;
 import net.minecraft.Team;
 import net.minecraft.Scoreboard;
 import net.minecraft.AbstractTeam;
 import net.minecraft.InGameHud;
 import net.minecraft.DrawContext;
 import net.minecraft.MutableText;
 import net.minecraft.TextColor;
 import net.minecraft.StringVisitable;
 import net.minecraft.ScoreboardEntry;
 import net.minecraft.NumberFormat;
 import net.minecraft.StyledNumberFormat;
 import net.minecraft.RenderTickCounter;
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
 
 @Mixin({InGameHud.class})
 public class InGameGuiMixin implements QClient {
   private static final int DOMAIN_COLOR = 15557921;
   
   @Inject(method = {"method_1735"}, at = {@At("HEAD")}, cancellable = true)
   private void renderVignetteOverlay(DrawContext context, Entity entity, CallbackInfo ci) {
     if (ModuleClass.noVignette.isEnable())
       ci.cancel(); 
   } @Shadow
   @Final
   private MinecraftClient field_2035;
   @Inject(method = {"method_1753"}, at = {@At("HEAD")})
   private void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
     BlurProgram.getInstance().beginFrame();
     if (EventInvoker.hasListeners(EventRender.Default.class)) {
       (new EventRender.Default(context, tickCounter.method_60637(true))).call();
     }
   }
 
 
 
 
 
 
 
 
   
   @Shadow
   private PlayerEntity method_1737() {
     return null;
   }
 
 
 
 
 
   
   @Inject(method = {"method_1757"}, at = {@At("HEAD")}, cancellable = true)
   private void astra$renderPatchedScoreboard(DrawContext drawContext, ScoreboardObjective objective, CallbackInfo ci) {
     if (!astra$shouldPatchScoreboard()) {
       return;
     }
     
     Scoreboard scoreboard = objective.method_1117();
     NumberFormat numberFormat = objective.method_55380((NumberFormat)StyledNumberFormat.field_47567);
 
 
 
 
 
 
 
 
 
 
 
 
     
     List<SidebarEntry> lines = scoreboard.method_1184(objective).stream().filter(entry -> !entry.method_55385()).sorted(Comparator.comparing(ScoreboardEntry::comp_2128).reversed().thenComparing(ScoreboardEntry::comp_2127, String.CASE_INSENSITIVE_ORDER)).limit(15L).map(entry -> { Team team = scoreboard.method_1164(entry.comp_2127()); Text name = astra$patchText((Text)Team.method_1142((AbstractTeam)team, entry.method_55387())); MutableText MutableText = entry.method_55386(numberFormat); int scoreWidth = this.field_2035.field_1772.method_27525((StringVisitable)MutableText); return new SidebarEntry(name, (Text)MutableText, scoreWidth); }).toList();
     
     Text title = astra$patchText(objective.method_1114());
     int titleWidth = this.field_2035.field_1772.method_27525((StringVisitable)title);
     int maxWidth = titleWidth;
     int separatorWidth = this.field_2035.field_1772.method_1727(": ");
     
     for (SidebarEntry line : lines) {
       maxWidth = Math.max(maxWidth, this.field_2035.field_1772.method_27525((StringVisitable)line.name) + ((line.scoreWidth > 0) ? (separatorWidth + line.scoreWidth) : 0));
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
   private Text astra$patchText(Text text) {
     NameProtect nameProtect = ModuleClass.nameProtect;
     Text patched = nameProtect.patchText(text);
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
     return (Text)Text.method_43470(patchedString).method_10862(patched.method_10866().method_27703(TextColor.method_27717(15557921)));
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\mixin\InGameGuiMixin.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */