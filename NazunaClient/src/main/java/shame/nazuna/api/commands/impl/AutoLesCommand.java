package shame.nazuna.api.commands.impl;
 import com.mojang.brigadier.arguments.ArgumentType;
 import com.mojang.brigadier.arguments.FloatArgumentType;
 import com.mojang.brigadier.builder.ArgumentBuilder;
 import com.mojang.brigadier.builder.LiteralArgumentBuilder;
 import com.mojang.brigadier.context.CommandContext;
 import com.mojang.brigadier.exceptions.CommandSyntaxException;
 import com.mojang.brigadier.suggestion.SuggestionsBuilder;
 import java.util.function.Consumer;
 import java.util.function.Supplier;
 import net.minecraft.class_2172;
 import shame.nazuna.api.utils.chat.ChatUtils;
 import shame.nazuna.client.modules.impl.player.AutoForest;
 
 public class AutoLesCommand extends Command {
   public AutoLesCommand() {
     super("autoles");
   }
 
   
   public void execute(LiteralArgumentBuilder<class_2172> builder) {
     builder.executes(ctx -> {
           sendStatus();
           
           return 1;
         });
     builder.then(literal("enable").executes(ctx -> {
             if (!module().isCurrentSessionEnabled()) {
               module().enableForCurrentSession();
             }
             
             ChatUtils.sendMessage("АвтоЛес включён");
             return 1;
           }));
     builder.then(literal("disable").executes(ctx -> {
             if (module().isCurrentSessionEnabled()) {
               module().disableForCurrentSession();
             }
             
             ChatUtils.sendMessage("АвтоЛес выключен");
             return 1;
           }));
     builder.then(literal("mode")
         .then(arg("value", (ArgumentType)StringArgumentType.word())
           .suggests((ctx, suggestions) -> {
               Objects.requireNonNull(suggestions);
               module().getModeSuggestions().forEach(suggestions::suggest);
               return suggestions.buildFuture();
             }).executes(ctx -> {
               String value = (String)ctx.getArgument("value", String.class);
               
               if (!module().setModeAlias(value)) {
                 ChatUtils.sendMessage("Неизвестный режим. Доступно: normal, fast");
                 return 1;
               } 
               ChatUtils.sendMessage("Режим: " + module().getModeAlias());
               return 1;
             })));
     builder.then((ArgumentBuilder)booleanSetting("swing", value -> module().setSwingEnabled(value.booleanValue()), () -> Boolean.valueOf(module().isSwingEnabled())));
     builder.then((ArgumentBuilder)booleanSetting("autosell", value -> module().setAutoSellEnabled(value.booleanValue()), () -> Boolean.valueOf(module().isAutoSellEnabled())));
     builder.then((ArgumentBuilder)booleanSetting("autopay", value -> module().setAutoPayEnabled(value.booleanValue()), () -> Boolean.valueOf(module().isAutoPayEnabled())));
     builder.then((ArgumentBuilder)booleanSetting("visuals", value -> module().setPreserveVisualsEnabled(value.booleanValue()), () -> Boolean.valueOf(module().isPreserveVisualsEnabled())));
     
     builder.then((ArgumentBuilder)floatSetting("pps", value -> module().setPacketsPerSecond(value.floatValue()), () -> Float.valueOf(module().getPacketsPerSecond())));
     builder.then((ArgumentBuilder)floatSetting("radius", value -> module().setBreakRadius(value.floatValue()), () -> Float.valueOf(module().getBreakRadius())));
     builder.then((ArgumentBuilder)floatSetting("payamount", value -> module().setPayAmount(value.floatValue()), () -> Float.valueOf(module().getPayAmount())));
     builder.then((ArgumentBuilder)floatSetting("interval", value -> module().setIntervalSeconds(value.floatValue()), () -> Float.valueOf(module().getIntervalSeconds())));
     
     builder.then(((LiteralArgumentBuilder)literal("pay")
         .then(literal("clear").executes(ctx -> {
               module().clearPayTarget();
               
               ChatUtils.sendMessage("Ник для перевода очищен");
               return 1;
             }))).then(arg("nick", (ArgumentType)StringArgumentType.word()).executes(ctx -> {
               String nick = (String)ctx.getArgument("nick", String.class);
               
               if (!module().setPayTarget(nick)) {
                 ChatUtils.sendMessage("Ник не может быть пустым");
                 return 1;
               } 
               ChatUtils.sendMessage("Ник для перевода: " + module().getPayTarget());
               return 1;
             })));
     builder.then(literal("status").executes(ctx -> {
             sendStatus();
             return 1;
           }));
   }
   
   private LiteralArgumentBuilder<class_2172> booleanSetting(String name, Consumer<Boolean> setter, Supplier<Boolean> getter) {
     return (LiteralArgumentBuilder<class_2172>)literal(name).then(arg("value", (ArgumentType)BoolArgumentType.bool())
         .suggests((ctx, suggestions) -> {
             suggestions.suggest("true");
             
             suggestions.suggest("false");
             return suggestions.buildFuture();
           }).executes(ctx -> {
             boolean value = BoolArgumentType.getBool(ctx, "value");
             setter.accept(Boolean.valueOf(value));
             ChatUtils.sendMessage(settingLabel(name) + ": " + settingLabel(name));
             return 1;
           }));
   }
   
   private LiteralArgumentBuilder<class_2172> floatSetting(String name, Consumer<Float> setter, Supplier<Float> getter) {
     return (LiteralArgumentBuilder<class_2172>)literal(name).then(arg("value", (ArgumentType)FloatArgumentType.floatArg())
         .executes(ctx -> {
             float value = FloatArgumentType.getFloat(ctx, "value");
             setter.accept(Float.valueOf(value));
             ChatUtils.sendMessage(settingLabel(name) + ": " + settingLabel(name));
             return 1;
           }));
   }
   
   private void sendStatus() {
     AutoForest module = module();
     ChatUtils.sendMessage("АвтоЛес: " + (module.isCurrentSessionEnabled() ? "включён" : "выключен"));
     ChatUtils.sendMessage("Режим=" + module.getModeAlias() + ", Мах рукой=" + 
         booleanText(module.isSwingEnabled()) + ", Автопродажа=" + 
         booleanText(module.isAutoSellEnabled()) + ", AutoPay=" + 
         booleanText(module.isAutoPayEnabled()) + ", Визуализация=" + 
         booleanText(module.isPreserveVisualsEnabled()));
     ChatUtils.sendMessage("Пакетов в секунду=" + module.getPacketsPerSecond() + ", Радиус=" + module
         .getBreakRadius() + ", Сумма перевода=" + module
         .getPayAmount() + ", Задержка=" + module
         .getIntervalSeconds() + ", Ник перевода=" + (
         module.getPayTarget().isBlank() ? "<пусто>" : module.getPayTarget()));
   }
   
   private AutoForest module() {
     return ModuleClass.autoForest;
   }
   
   private String booleanText(boolean value) {
     return value ? "включено" : "выключено";
   }
   
   private String settingLabel(String name) {
     switch (name) { case "swing": case "autosell": case "autopay": case "visuals": case "pps": case "radius": case "payamount": case "interval":  }  return 
 
 
 
 
 
 
 
       
       name;
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\commands\impl\AutoLesCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */