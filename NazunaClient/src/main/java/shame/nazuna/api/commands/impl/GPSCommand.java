package shame.nazuna.api.commands.impl;
 
 import com.mojang.brigadier.arguments.ArgumentType;
 import com.mojang.brigadier.arguments.IntegerArgumentType;
 import com.mojang.brigadier.builder.LiteralArgumentBuilder;
 import com.mojang.brigadier.context.CommandContext;
 import com.mojang.brigadier.exceptions.CommandSyntaxException;
 import net.minecraft.I18n;
 import net.minecraft.CommandSource;
 import shame.nazuna.api.commands.Command;
 import shame.nazuna.api.utils.chat.ChatUtils;
 import shame.nazuna.api.utils.cmd.waypoint.Waypoint;
 import shame.nazuna.astra;
 
 public class GPSCommand
   extends Command {
   public GPSCommand() {
     super("gps");
   }
 
 
   
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
     ((LiteralArgumentBuilder)builder
       .then(arg("X", (ArgumentType)IntegerArgumentType.integer())
         .then(arg("Z", (ArgumentType)IntegerArgumentType.integer())
           .executes(context -> {
               int x = ((Integer)context.getArgument("X", Integer.class)).intValue();
               
               int z = ((Integer)context.getArgument("Z", Integer.class)).intValue();
               
               Waypoint waypoint = new Waypoint(x, z);
               
               NazunaClient.INSTANCE.waypointStorage.set(waypoint);
               
               ChatUtils.sendMessage(I18n.method_4662("Метка поставлена: ", new Object[] { Integer.valueOf(x), Integer.valueOf(z) }));
               
               return 1;
             })))).then(literal("remove")
         .executes(context -> {
             if (!NazunaClient.INSTANCE.waypointStorage.isEmpty()) {
               NazunaClient.INSTANCE.waypointStorage.clear();
               ChatUtils.sendMessage(I18n.method_4662("Метка удалена!", new Object[0]));
             } else {
               ChatUtils.sendMessage(I18n.method_4662("Метки не было", new Object[0]));
             } 
             return 1;
           }));
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\commands\impl\GPSCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */