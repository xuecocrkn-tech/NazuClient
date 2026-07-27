package shame.nazuna.api.storages.implement;
 import com.mojang.brigadier.CommandDispatcher;
 import java.util.ArrayList;
 import java.util.List;
 import net.minecraft.CommandSource;
 import net.minecraft.ClientCommandSource;
 import shame.nazuna.api.commands.Command;
 import shame.nazuna.api.commands.impl.AutoLesCommand;
 import shame.nazuna.api.commands.impl.BindCommand;
 import shame.nazuna.api.commands.impl.BlockESPCommand;
 import shame.nazuna.api.commands.impl.BotCommand;
 import shame.nazuna.api.commands.impl.ConfigCommand;
 import shame.nazuna.api.commands.impl.MacroCommand;
 import shame.nazuna.api.commands.impl.NukerCommand;
 import shame.nazuna.api.commands.impl.VClipCommand;
 
 public class CommandStorage {
   
   public CommandStorage() {
     registry();
   }
   
   private void registry() {
     addCommands(new Command[] { (Command)new AutoLesCommand(), (Command)new FriendCommand(), (Command)new ConfigCommand(), (Command)new MacroCommand(), (Command)new BotCommand(), (Command)new BlockESPCommand(), (Command)new NukerCommand(), (Command)new NukerCommand("nuk"), (Command)new GPSCommand(), (Command)new BindCommand(), (Command)new StaffCommand(), (Command)new VClipCommand(), (Command)new DataCommand() });
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   public CommandSource getSource() {
     return (CommandSource)new ClientCommandSource(null, MinecraftClient.method_1551());
   }
   
   private void addCommands(Command... command) {
     for (Command cmd : command) {
       cmd.register(this.dispatcher);
       this.commands.add(cmd);
     } 
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\storages\implement\CommandStorage.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */