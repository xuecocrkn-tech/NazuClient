package shame.nazuna.api.commands.impl;
 import com.mojang.brigadier.arguments.ArgumentType;
 import com.mojang.brigadier.arguments.StringArgumentType;
 import com.mojang.brigadier.builder.LiteralArgumentBuilder;
 import com.mojang.brigadier.context.CommandContext;
 import com.mojang.brigadier.exceptions.CommandSyntaxException;
 import com.mojang.brigadier.suggestion.SuggestionsBuilder;
 import java.lang.reflect.Field;
 import java.util.Objects;
 import java.util.concurrent.CompletableFuture;
 import net.minecraft.CommandSource;
 import org.lwjgl.glfw.GLFW;
 import shame.nazuna.api.commands.Command;
 import shame.nazuna.api.utils.chat.ChatUtils;
 import shame.nazuna.api.utils.cmd.macro.Macro;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.settings.implement.BindSetting;
 
 public class MacroCommand extends Command {
   public MacroCommand() {
     super("macro");
   }
 
   
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
     ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder
       .then(literal("add")
         .then(arg("name", (ArgumentType)StringArgumentType.word())
           .then(arg("bind", (ArgumentType)StringArgumentType.word())
             .suggests((context, builder1) -> {
                 for (Field field : GLFW.class.getDeclaredFields()) {
                   String name = field.getName();
                   
                   if (name.startsWith("GLFW_KEY_")) {
                     String bind = name.replace("GLFW_KEY_", "");
                     
                     if (bind.startsWith(builder1.getRemaining())) {
                       builder1.suggest(bind);
                     }
                   } 
                 } 
                 
                 if ("NONE".startsWith(builder1.getRemaining().toUpperCase())) {
                   builder1.suggest("NONE");
                 }
                 
                 return builder1.buildFuture();
               }).then(arg("command", (ArgumentType)StringArgumentType.greedyString())
               .executes(context -> {
                   String name = (String)context.getArgument("name", String.class);
 
                   
                   String bind = ((String)context.getArgument("bind", String.class)).toUpperCase();
 
                   
                   String command = (String)context.getArgument("command", String.class);
                   
                   if (NazunaClient.INSTANCE.macroStorage.getMacro(name) != null) {
                     ChatUtils.sendMessage("Макрос " + name + " уже существует!");
                     
                     return 1;
                   } 
                   
                   try {
                     int key = "NONE".equals(bind) ? -1 : GLFW.class.getField("GLFW_KEY_" + bind).getInt(null);
                     
                     NazunaClient.INSTANCE.macroStorage.add(new Macro(name, command, new BindSetting("bind", key)));
                     
                     ChatUtils.sendMessage("Макрос " + name + " был добавлен!");
                   } catch (Exception ignored) {
                     ChatUtils.sendMessage("Неверный бинд: " + bind);
                   } 
                   
                   return 1;
                 })))))).then(literal("remove")
         .then(arg("name", (ArgumentType)StringArgumentType.word())
           .suggests((context, builder1) -> {
               Objects.requireNonNull(builder1);
               
               NazunaClient.INSTANCE.macroStorage.getNames().stream().filter(x -> true).forEach(builder1::suggest);
               
               return builder1.buildFuture();
             }).executes(context -> {
               String name = (String)context.getArgument("name", String.class);
               
               if (NazunaClient.INSTANCE.macroStorage.isEmpty()) {
                 ChatUtils.sendMessage("Список макросов пуст!");
                 
                 return 1;
               } 
               
               Macro macro = NazunaClient.INSTANCE.macroStorage.getMacro(name);
               if (macro == null) {
                 ChatUtils.sendMessage("Макрос " + name + " не найден!");
                 return 1;
               } 
               NazunaClient.INSTANCE.macroStorage.remove(macro);
               ChatUtils.sendMessage("Макрос " + name + " был удалён!");
               return 1;
             })))).then(literal("list")
         .executes(context -> {
             StringBuilder builder1 = new StringBuilder();
             
             if (NazunaClient.INSTANCE.macroStorage.getNames().isEmpty()) {
               ChatUtils.sendMessage("Список макросов пуст!");
             } else {
               for (int i = 0; i < NazunaClient.INSTANCE.macroStorage.getNames().size(); i++) {
                 builder1.append(NazunaClient.INSTANCE.macroStorage.getNames().get(i));
                 
                 if (i < NazunaClient.INSTANCE.macroStorage.getNames().size() - 1) {
                   builder1.append(", ");
                 }
               } 
               
               builder1.append(".");
               ChatUtils.sendMessage("Макросы: " + String.valueOf(builder1));
             } 
             return 1;
           }))).then(literal("clear")
         .executes(context -> {
             if (!NazunaClient.INSTANCE.macroStorage.isEmpty()) {
               NazunaClient.INSTANCE.macroStorage.clear();
               ChatUtils.sendMessage("Все макросы были удалены!");
             } else {
               ChatUtils.sendMessage("Список макросов пуст!");
             } 
             return 1;
           }));
   }
 }

