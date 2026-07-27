package shame.nazuna.api.commands.impl;
 import com.mojang.brigadier.arguments.ArgumentType;
 import com.mojang.brigadier.arguments.StringArgumentType;
 import com.mojang.brigadier.builder.LiteralArgumentBuilder;
 import com.mojang.brigadier.context.CommandContext;
 import com.mojang.brigadier.exceptions.CommandSyntaxException;
 import com.mojang.brigadier.suggestion.SuggestionsBuilder;
 import java.util.Objects;
 import java.util.concurrent.CompletableFuture;
 import net.minecraft.class_2172;
 import net.minecraft.class_640;
 import shame.nazuna.api.utils.chat.ChatUtils;
 import shame.nazuna.astra;
 
 public class StaffCommand extends Command {
   public StaffCommand() {
     super("staff");
   }
 
 
   
   public void execute(LiteralArgumentBuilder<class_2172> builder) {
     ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder
       .then(literal("add")
         .then(arg("player", (ArgumentType)StringArgumentType.word())
           .suggests((context, builder1) -> {
               for (class_640 entry : mc.method_1562().method_2880()) {
                 String name = entry.method_2966().getName();
                 
                 if (name.toLowerCase().startsWith(builder1.getRemaining().toLowerCase())) {
                   builder1.suggest(name);
                 }
               } 
               return builder1.buildFuture();
             }).executes(context -> {
               String player = (String)context.getArgument("player", String.class);
               
               if (!astra.INSTANCE.staffStorage.isStaff(player)) {
                 astra.INSTANCE.staffStorage.add(player);
                 
                 ChatUtils.sendMessage("Игрок " + player + " добавлен в список стаффов!");
               } else {
                 ChatUtils.sendMessage("Игрок " + player + " уже в списке стаффов!");
               } 
               
               return 1;
             })))).then(literal("remove")
         .then(arg("player", (ArgumentType)StringArgumentType.word())
           .suggests((context, builder1) -> {
               Objects.requireNonNull(builder1);
 
               
               astra.INSTANCE.staffStorage.getStaffs().stream().sorted(String::compareTo).filter(()).forEach(builder1::suggest);
               
               return builder1.buildFuture();
             }).executes(context -> {
               String player = (String)context.getArgument("player", String.class);
               
               if (astra.INSTANCE.staffStorage.isStaff(player)) {
                 astra.INSTANCE.staffStorage.remove(player);
                 
                 ChatUtils.sendMessage("Игрок " + player + " удалён из списка стаффов!");
               } else {
                 ChatUtils.sendMessage("Игрок " + player + " не найден в списке стаффов!");
               } 
               
               return 1;
             })))).then(literal("list")
         .executes(context -> {
             StringBuilder builder1 = new StringBuilder();
             
             if (astra.INSTANCE.staffStorage.getStaffs().isEmpty()) {
               ChatUtils.sendMessage("Список стаффов пуст!");
             } else {
               for (int i = 0; i < astra.INSTANCE.staffStorage.getStaffs().size(); i++) {
                 builder1.append(astra.INSTANCE.staffStorage.getStaffs().get(i));
                 
                 if (i < astra.INSTANCE.staffStorage.getStaffs().size() - 1) {
                   builder1.append(", ");
                 }
               } 
               builder1.append(".");
               ChatUtils.sendMessage("Стаффы: " + String.valueOf(builder1));
             } 
             return 1;
           }))).then(literal("clear")
         .executes(context -> {
             if (!astra.INSTANCE.staffStorage.isEmpty()) {
               astra.INSTANCE.staffStorage.clear();
               ChatUtils.sendMessage("Список стаффов очищен!");
             } else {
               ChatUtils.sendMessage("Список стаффов пуст!");
             } 
             return 1;
           }));
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\commands\impl\StaffCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */