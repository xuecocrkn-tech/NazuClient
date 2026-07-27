package shame.nazuna.api.commands.impl;
 
 import com.mojang.brigadier.arguments.ArgumentType;
 import com.mojang.brigadier.arguments.StringArgumentType;
 import com.mojang.brigadier.builder.LiteralArgumentBuilder;
 import com.mojang.brigadier.context.CommandContext;
 import com.mojang.brigadier.exceptions.CommandSyntaxException;
 import com.mojang.brigadier.suggestion.SuggestionsBuilder;
 import java.util.List;
 import java.util.Objects;
 import java.util.concurrent.CompletableFuture;
 import net.minecraft.CommandSource;
 import shame.nazuna.api.commands.Command;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.api.utils.chat.ChatUtils;
 import shame.nazuna.client.modules.impl.combat.Aura;
 
 public class DataCommand extends Command {
   public DataCommand() {
     super("data");
   }
 
 
   
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
     ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder
       .executes(context -> {
           sendStatus();
           
           return 1;
         })).then(literal("record")
         .executes(context -> {
             Aura aura = getAura();
             
             if (aura == null) {
               return 1;
             }
             
             aura.getDataSystem().startRecording();
             ChatUtils.sendMessage("Data: запись начата, старые паттерны в памяти очищены");
             return 1;
           }))).then(((LiteralArgumentBuilder)literal("stop")
         .executes(context -> {
             stopRecording("data_" + System.currentTimeMillis());
             
             return 1;
           })).then(arg("name", (ArgumentType)StringArgumentType.greedyString())
           .executes(context -> {
               stopRecording((String)context.getArgument("name", String.class));
               
               return 1;
             })))).then(literal("play")
         .then(arg("name", (ArgumentType)StringArgumentType.word())
           .suggests((context, suggestions) -> {
               Objects.requireNonNull(suggestions);
               
               getAuraPatterns().stream().filter(x -> true).forEach(suggestions::suggest);
               
               return suggestions.buildFuture();
             }).executes(context -> {
               playProfile((String)context.getArgument("name", String.class));
               
               return 1;
             })))).then(literal("delete")
         .then(arg("name", (ArgumentType)StringArgumentType.word())
           .suggests((context, suggestions) -> {
               Objects.requireNonNull(suggestions);
               
               getAuraPatterns().stream().filter(x -> true).forEach(suggestions::suggest);
               
               return suggestions.buildFuture();
             }).executes(context -> {
               deleteProfile((String)context.getArgument("name", String.class));
               
               return 1;
             })))).then(literal("list")
         .executes(context -> {
             listProfiles();
             
             return 1;
           }))).then(literal("clear")
         .executes(context -> {
             Aura aura = getAura();
             
             if (aura == null) {
               return 1;
             }
             
             aura.getDataSystem().clearPatterns();
             ChatUtils.sendMessage("Data: паттерны очищены");
             return 1;
           }))).then(literal("status")
         .executes(context -> {
             sendStatus();
             return 1;
           }));
   }
   
   private void stopRecording(String name) {
     Aura aura = getAura();
     if (aura == null) {
       return;
     }
     
     if (!aura.getDataSystem().isRecording()) {
       ChatUtils.sendMessage("Data: запись не запущена");
       
       return;
     } 
     if (!aura.getDataSystem().savePatterns(name)) {
       ChatUtils.sendMessage("Data: нечего сохранять");
       
       return;
     } 
     aura.getDataSystem().stopRecording();
     ChatUtils.sendMessage("Data: запись остановлена и сохранена как " + name);
   }
   
   private void playProfile(String name) {
     Aura aura = getAura();
     if (aura == null) {
       return;
     }
     
     if (!aura.getDataSystem().loadPatterns(name)) {
       ChatUtils.sendMessage("Data: профиль " + name + " не найден или поврежден");
       
       return;
     } 
     aura.getDataSystem().setRecording(false);
     aura.getDataSystem().setUsingNeuro(true);
     aura.getDataSystem().resetState();
     ChatUtils.sendMessage("Data: загружен профиль " + name + " (" + aura.getDataSystem().getPatternCount() + " паттернов)");
     ChatUtils.sendMessage("Data: выбери режим ротации Data в Aura");
   }
   
   private void deleteProfile(String name) {
     Aura aura = getAura();
     if (aura == null) {
       return;
     }
     
     if (aura.getDataSystem().deletePatterns(name)) {
       ChatUtils.sendMessage("Data: профиль " + name + " удален");
     } else {
       ChatUtils.sendMessage("Data: профиль " + name + " не найден");
     } 
   }
   
   private void listProfiles() {
     List<String> patterns = getAuraPatterns();
     if (patterns.isEmpty()) {
       ChatUtils.sendMessage("Data: нет сохраненных профилей");
       return;
     } 
     ChatUtils.sendMessage("Data: сохраненные профили (" + patterns.size() + "):");
     for (String name : patterns) {
       ChatUtils.sendMessage("  - " + name);
     }
   }
   
   private void sendStatus() {
     Aura aura = getAura();
     if (aura == null) {
       return;
     }
     
     ChatUtils.sendMessage(aura.getDataSystem().getStatusString());
     if (aura.getDataSystem().getPatternCount() > 0) {
       ChatUtils.sendMessage("Новых в сессии: " + aura.getDataSystem().getRecordedThisSession());
     }
   }
   
   private Aura getAura() {
     Aura aura = (ModuleClass.INSTANCE == null) ? null : ModuleClass.aura;
     if (aura == null) {
       ChatUtils.sendMessage("Data: модуль Aura не найден");
     }
     return aura;
   }
   
   private List<String> getAuraPatterns() {
     Aura aura = (ModuleClass.INSTANCE == null) ? null : ModuleClass.aura;
     return (aura == null) ? List.<String>of() : aura.getDataSystem().getPatternNames();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\commands\impl\DataCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */