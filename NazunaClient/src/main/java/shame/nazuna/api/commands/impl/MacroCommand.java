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
                   
                   if (astra.INSTANCE.macroStorage.getMacro(name) != null) {
                     ChatUtils.sendMessage("Макрос " + name + " уже существует!");
                     
                     return 1;
                   } 
                   
                   try {
                     int key = "NONE".equals(bind) ? -1 : GLFW.class.getField("GLFW_KEY_" + bind).getInt(null);
                     
                     astra.INSTANCE.macroStorage.add(new Macro(name, command, new BindSetting("bind", key)));
                     
                     ChatUtils.sendMessage("Макрос " + name + " был добавлен!");
                   } catch (Exception ignored) {
                     ChatUtils.sendMessage("Неверный бинд: " + bind);
                   } 
                   
                   return 1;
                 })))))).then(literal("remove")
         .then(arg("name", (ArgumentType)StringArgumentType.word())
           .suggests((context, builder1) -> {
               Objects.requireNonNull(builder1);
               
               astra.INSTANCE.macroStorage.getNames().stream().filter(()).forEach(builder1::suggest);
               
               return builder1.buildFuture();
             }).executes(context -> {
               String name = (String)context.getArgument("name", String.class);
               
               if (astra.INSTANCE.macroStorage.isEmpty()) {
                 ChatUtils.sendMessage("Список макросов пуст!");
                 
                 return 1;
               } 
               
               Macro macro = astra.INSTANCE.macroStorage.getMacro(name);
               if (macro == null) {
                 ChatUtils.sendMessage("Макрос " + name + " не найден!");
                 return 1;
               } 
               astra.INSTANCE.macroStorage.remove(macro);
               ChatUtils.sendMessage("Макрос " + name + " был удалён!");
               return 1;
             })))).then(literal("list")
         .executes(context -> {
             StringBuilder builder1 = new StringBuilder();
             
             if (astra.INSTANCE.macroStorage.getNames().isEmpty()) {
               ChatUtils.sendMessage("Список макросов пуст!");
             } else {
               for (int i = 0; i < astra.INSTANCE.macroStorage.getNames().size(); i++) {
                 builder1.append(astra.INSTANCE.macroStorage.getNames().get(i));
                 
                 if (i < astra.INSTANCE.macroStorage.getNames().size() - 1) {
                   builder1.append(", ");
                 }
               } 
               
               builder1.append(".");
               ChatUtils.sendMessage("Макросы: " + String.valueOf(builder1));
             } 
             return 1;
           }))).then(literal("clear")
         .executes(context -> {
             if (!astra.INSTANCE.macroStorage.isEmpty()) {
               astra.INSTANCE.macroStorage.clear();
               ChatUtils.sendMessage("Все макросы были удалены!");
             } else {
               ChatUtils.sendMessage("Список макросов пуст!");
             } 
             return 1;
           }));
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\commands\impl\MacroCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */