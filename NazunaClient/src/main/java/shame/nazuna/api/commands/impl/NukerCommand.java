package shame.nazuna.api.commands.impl;
 import com.mojang.brigadier.arguments.StringArgumentType;
 import com.mojang.brigadier.builder.LiteralArgumentBuilder;
 import com.mojang.brigadier.context.CommandContext;
 import com.mojang.brigadier.exceptions.CommandSyntaxException;
 import com.mojang.brigadier.suggestion.SuggestionsBuilder;
 import java.util.Objects;
 import java.util.Set;
 import java.util.concurrent.CompletableFuture;
 import java.util.stream.Collectors;
 import net.minecraft.CommandSource;
 import net.minecraft.Block;
 import net.minecraft.Identifier;
 import net.minecraft.Registries;
 import shame.nazuna.api.utils.chat.ChatUtils;
 import shame.nazuna.client.modules.impl.player.Nuker;
 
 public class NukerCommand extends Command {
   public NukerCommand() {
     super("nuker");
   }
   
   public NukerCommand(String command) {
     super(command);
   }
 
   
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
     ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder
       .then(literal("add")
         .then(arg("block", (ArgumentType)StringArgumentType.word())
           .suggests((context, builder1) -> {
               String input = Nuker.normalizeBlockName(builder1.getRemaining());
               
               Objects.requireNonNull(Registries.field_41175);
               
               Objects.requireNonNull(builder1);
               
               Registries.field_41175.method_10220().map(Registries.field_41175::method_10221).map(Identifier::method_12832).filter(x -> true).limit(20L).forEach(builder1::suggest);
               
               return builder1.buildFuture();
             }).executes(context -> {
               String blockName = Nuker.normalizeBlockName((String)context.getArgument("block", String.class));
               
               if (Nuker.INSTANCE.isTargetBlock(blockName)) {
                 ChatUtils.sendMessage("§cБлок §e" + blockName + "§c уже в списке Nuker!");
                 
                 return 1;
               } 
               
               if (!blockExists(blockName)) {
                 ChatUtils.sendMessage("§cБлок §e" + blockName + "§c не найден!");
                 
                 return 1;
               } 
               
               Nuker.INSTANCE.addBlock(blockName);
               
               ChatUtils.sendMessage("§aБлок §e" + blockName + "§a добавлен в Nuker!");
               return 1;
             })))).then(literal("remove")
         .then(arg("block", (ArgumentType)StringArgumentType.word())
           .suggests((context, builder1) -> {
               String input = Nuker.normalizeBlockName(builder1.getRemaining());
               
               Objects.requireNonNull(builder1);
               
               Nuker.INSTANCE.getTargetBlocks().stream().sorted(String::compareTo).filter(x -> true).forEach(builder1::suggest);
               
               return builder1.buildFuture();
             }).executes(context -> {
               String blockName = Nuker.normalizeBlockName((String)context.getArgument("block", String.class));
               
               if (!Nuker.INSTANCE.isTargetBlock(blockName)) {
                 ChatUtils.sendMessage("§cБлока §e" + blockName + "§c нет в списке Nuker!");
                 
                 return 1;
               } 
               
               Nuker.INSTANCE.removeBlock(blockName);
               
               ChatUtils.sendMessage("§aБлок §e" + blockName + "§a удален из Nuker!");
               
               return 1;
             })))).then(literal("list")
         .executes(context -> {
             Set<String> blocks = Nuker.INSTANCE.getTargetBlocks();
 
             
             if (blocks.isEmpty()) {
               ChatUtils.sendMessage("§cСписок Nuker пуст!");
               
               return 1;
             } 
             
             String blockList = blocks.stream().sorted().collect(Collectors.joining("§7, §e"));
             
             ChatUtils.sendMessage("§aБлоки Nuker §7(§e" + blocks.size() + "§7)§a: §e" + blockList);
             
             return 1;
           }))).then(literal("clear")
         .executes(context -> {
             if (Nuker.INSTANCE.getTargetBlocks().isEmpty()) {
               ChatUtils.sendMessage("§cСписок Nuker уже пуст!");
               return 1;
             } 
             Nuker.INSTANCE.clearBlocks();
             ChatUtils.sendMessage("§aСписок Nuker очищен!");
             return 1;
           }));
   }
 
 
   
   private boolean blockExists(String blockName) {
     return Registries.field_41175.method_10220()
       .anyMatch(block -> Registries.field_41175.method_10221(block).method_12832().equalsIgnoreCase(blockName));
   }
 }

