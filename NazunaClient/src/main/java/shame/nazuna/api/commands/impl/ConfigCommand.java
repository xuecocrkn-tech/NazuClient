package shame.nazuna.api.commands.impl;
 import com.mojang.brigadier.arguments.ArgumentType;
 import com.mojang.brigadier.arguments.StringArgumentType;
 import com.mojang.brigadier.builder.LiteralArgumentBuilder;
 import com.mojang.brigadier.context.CommandContext;
 import com.mojang.brigadier.exceptions.CommandSyntaxException;
 import com.mojang.brigadier.suggestion.SuggestionsBuilder;
 import java.io.File;
 import java.util.Arrays;
 import java.util.Objects;
 import java.util.concurrent.CompletableFuture;
 import net.minecraft.CommandSource;
 import shame.nazuna.api.commands.Command;
 import shame.nazuna.api.utils.chat.ChatUtils;
 import shame.nazuna.astra;
 
 public class ConfigCommand extends Command {
   public ConfigCommand() {
     super("config");
   }
 
 
   
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
     ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder
       .then(literal("save")
         .then(arg("config", (ArgumentType)StringArgumentType.word())
           .suggests((context, builder1) -> {
               if (NazunaClient.INSTANCE.configsDir.exists() && NazunaClient.INSTANCE.configsDir.isDirectory()) {
                 File[] files = NazunaClient.INSTANCE.configsDir.listFiles();
 
                 
                 if (files != null) {
                   Objects.requireNonNull(builder1);
 
                   
                   Arrays.<File>stream(files).map(File::getName).map(x -> x).forEach(builder1::suggest);
                 } 
               } 
               
               return builder1.buildFuture();
             }).executes(context -> {
               String config = (String)context.getArgument("config", String.class);
               try {
                 NazunaClient.INSTANCE.configStorage.saveConfig(config);
                 ChatUtils.sendMessage("Конфиг " + config + " успешно сохранён!");
               } catch (Exception e) {
                 ChatUtils.sendMessage("Ошибка при сохранении конфига " + config + "!");
 
                 
                 e.printStackTrace();
               } 
 
               
               return 1;
             })))).then(literal("load")
         .then(arg("config", (ArgumentType)StringArgumentType.word())
           .suggests((context, builder1) -> {
               if (NazunaClient.INSTANCE.configsDir.exists() && NazunaClient.INSTANCE.configsDir.isDirectory()) {
                 File[] files = NazunaClient.INSTANCE.configsDir.listFiles();
 
                 
                 if (files != null) {
                   Objects.requireNonNull(builder1);
 
                   
                   Arrays.<File>stream(files).map(File::getName).map(x -> x).forEach(builder1::suggest);
                 } 
               } 
               
               return builder1.buildFuture();
             }).executes(context -> {
               String config = (String)context.getArgument("config", String.class);
               try {
                 NazunaClient.INSTANCE.configStorage.loadConfig(config);
                 ChatUtils.sendMessage("Конфиг " + config + " успешно загружен!");
               } catch (Exception e) {
                 ChatUtils.sendMessage("Ошибка при загрузке конфига " + config + "!");
 
                 
                 e.printStackTrace();
               } 
 
               
               return 1;
             })))).then(literal("list")
         .executes(context -> {
             File[] files = NazunaClient.INSTANCE.configsDir.listFiles();
             
             if (files == null || files.length == 0) {
               ChatUtils.sendMessage("Список конфигов пуст!");
             } else {
               StringBuilder builder1 = new StringBuilder();
               
               for (int i = 0; i < files.length; i++) {
                 String fileName = files[i].getName().replace(".wonder", "");
                 builder1.append(fileName);
                 if (i < files.length - 1) {
                   builder1.append(", ");
                 }
               } 
               ChatUtils.sendMessage("Конфиги: " + String.valueOf(builder1));
             } 
             return 1;
           }))).then(literal("dir")
         .executes(context -> {
             try {
               File configsDir = new File(NazunaClient.INSTANCE.globalsDir, "configs");
               
               if (!configsDir.exists()) {
                 configsDir.mkdirs();
               }
               (new ProcessBuilder(new String[] { "explorer.exe", configsDir.getAbsolutePath() })).start();
               ChatUtils.sendMessage("Папка с конфигами открыта!");
             } catch (Exception e) {
               ChatUtils.sendMessage("Ошибка при открытии папки с конфигами!");
               e.printStackTrace();
             } 
             return 1;
           }));
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\commands\impl\ConfigCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */