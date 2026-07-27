package shame.nazuna.api.commands;
 import com.mojang.brigadier.CommandDispatcher;
 import com.mojang.brigadier.arguments.ArgumentType;
 import com.mojang.brigadier.builder.LiteralArgumentBuilder;
 import com.mojang.brigadier.builder.RequiredArgumentBuilder;
 import net.minecraft.CommandSource;
 import shame.nazuna.api.QClient;
 
 public abstract class Command implements QClient {
   public String getCommand() {
     return this.command;
   } private final String command;
   public Command(String command) {
     this.command = command;
   }
 
 
   
   public void register(CommandDispatcher<CommandSource> dispatcher) {
     LiteralArgumentBuilder<CommandSource> builder = LiteralArgumentBuilder.literal(this.command);
     execute(builder);
     dispatcher.register(builder);
   }
   
   protected <T> RequiredArgumentBuilder<CommandSource, T> arg(String name, ArgumentType<T> type) {
     return RequiredArgumentBuilder.argument(name, type);
   }
   
   protected LiteralArgumentBuilder<CommandSource> literal(String name) {
     return LiteralArgumentBuilder.literal(name);
   }
   
   public abstract void execute(LiteralArgumentBuilder<CommandSource> paramLiteralArgumentBuilder);
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\commands\Command.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */