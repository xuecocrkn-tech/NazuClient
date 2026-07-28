package shame.nazuna.api.commands.impl;
 import com.mojang.brigadier.arguments.ArgumentType;
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
 import shame.nazuna.client.modules.impl.render.BlockESP;
 
 public class BlockESPCommand extends Command {
   public BlockESPCommand() {
     super("blockesp");
   }
 
 
   
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
     ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)builder
       .then(literal("add")
         .then(arg("block", (ArgumentType)StringArgumentType.word())
           .suggests((context, builder1) -> {
               String input = builder1.getRemaining().toLowerCase();
               
               Objects.requireNonNull(Registries.field_41175);
               
               Objects.requireNonNull(builder1);
               
               Registries.field_41175.method_10220().map(Registries.field_41175::method_10221).map(Identifier::method_12832).filter(x -> true).limit(20L).forEach(builder1::suggest);
               
               return builder1.buildFuture();
             }).executes(context -> {
               String blockName = (String)context.getArgument("block", String.class);
 
               
               if (BlockESP.INSTANCE.isTracking(blockName)) {
                 ChatUtils.sendMessage("§cБлок §e" + blockName + "§c уже отслеживается!");
 
                 
                 return 1;
               } 
 
               
               boolean exists = Registries.field_41175.method_10220().anyMatch(x -> true);
               
               if (!exists) {
                 ChatUtils.sendMessage("§cБлок §e" + blockName + "§c не найден!");
                 
                 return 1;
               } 
               
               BlockESP.INSTANCE.addBlock(blockName);
               
               ChatUtils.sendMessage("§aБлок §e" + blockName + "§a добавлен в отслеживание!");
               
               return 1;
             })))).then(literal("remove")
         .then(arg("block", (ArgumentType)StringArgumentType.word())
           .suggests((context, builder1) -> {
               Objects.requireNonNull(builder1);
 
               
               BlockESP.INSTANCE.getTrackedBlocks().stream().sorted(String::compareTo).filter(x -> true).forEach(builder1::suggest);
               
               return builder1.buildFuture();
             }).executes(context -> {
               String blockName = (String)context.getArgument("block", String.class);
               
               if (!BlockESP.INSTANCE.isTracking(blockName)) {
                 ChatUtils.sendMessage("§cБлок §e" + blockName + "§c не отслеживается!");
                 
                 return 1;
               } 
               
               BlockESP.INSTANCE.removeBlock(blockName);
               
               ChatUtils.sendMessage("§aБлок §e" + blockName + "§a удалён из отслеживания!");
               
               return 1;
             })))).then(literal("list")
         .executes(context -> {
             Set<String> blocks = BlockESP.INSTANCE.getTrackedBlocks();
 
             
             if (blocks.isEmpty()) {
               ChatUtils.sendMessage("§cСписок отслеживаемых блоков пуст!");
 
               
               return 1;
             } 
             
             String blockList = blocks.stream().sorted().collect(Collectors.joining("§7, §e"));
             
             ChatUtils.sendMessage("§aОтслеживаемые блоки §7(§e" + blocks.size() + "§7)§a: §e" + blockList);
             
             return 1;
           }))).then(literal("clear")
         .executes(context -> {
             if (BlockESP.INSTANCE.getTrackedBlocks().isEmpty()) {
               ChatUtils.sendMessage("§cСписок отслеживаемых блоков уже пуст!");
               return 1;
             } 
             BlockESP.INSTANCE.clearBlocks();
             ChatUtils.sendMessage("§aСписок отслеживаемых блоков очищен!");
             return 1;
           }));
   }
 }

