package shame.nazuna.api.commands.impl;
 import com.mojang.brigadier.arguments.ArgumentType;
 import com.mojang.brigadier.arguments.IntegerArgumentType;
 import com.mojang.brigadier.builder.LiteralArgumentBuilder;
 import com.mojang.brigadier.context.CommandContext;
 import com.mojang.brigadier.exceptions.CommandSyntaxException;
 import net.minecraft.BlockView;
 import net.minecraft.CommandSource;
 import net.minecraft.BlockPos;
 import net.minecraft.Direction;
 import net.minecraft.VoxelShape;
 import net.minecraft.BlockState;
 import shame.nazuna.api.commands.Command;
 
 public class VClipCommand extends Command {
   public VClipCommand() {
     super("vclip");
   }
 
 
   
   public void execute(LiteralArgumentBuilder<CommandSource> builder) {
     builder.then(arg("Y", (ArgumentType)IntegerArgumentType.integer())
         .executes(context -> {
             int y = ((Integer)context.getArgument("Y", Integer.class)).intValue();
             
             mc.field_1724.method_5814(mc.field_1724.method_23317(), mc.field_1724.method_23318() + y, mc.field_1724.method_23321());
             
             return 1;
           }));
     builder.then(literal("up")
         .executes(context -> {
             clipToSafeBlock(true);
             
             return 1;
           }));
     
     builder.then(literal("down")
         .executes(context -> {
             clipToSafeBlock(false);
             return 1;
           }));
   }
 
 
   
   private void clipToSafeBlock(boolean up) {
     if (mc.field_1724 == null || mc.field_1687 == null) {
       return;
     }
     
     int startY = mc.field_1724.method_31478();
     int minY = mc.field_1687.method_31607();
     int maxY = mc.field_1687.method_31600() - 2;
     int step = up ? 1 : -1;
     int from = up ? (startY + 1) : (startY - 1);
     int to = up ? maxY : minY;
     int y;
     for (y = from; up ? (y <= to) : (y >= to); ) {
       if (!isSafeStandPosition(y)) {
         y += step;
         continue;
       } 
       VoxelShape shape = mc.field_1687.method_8320(new BlockPos(mc.field_1724.method_31477(), y - 1, mc.field_1724.method_31479())).method_26220((BlockView)mc.field_1687, new BlockPos(mc.field_1724.method_31477(), y - 1, mc.field_1724.method_31479()));
       double offsetY = shape.method_1110() ? 0.0D : shape.method_1105(Direction.class_2351.field_11052);
       mc.field_1724.method_5814(mc.field_1724.method_23317(), y + offsetY, mc.field_1724.method_23321());
       return;
     } 
   }
 
   
   private boolean isSafeStandPosition(int y) {
     BlockPos floorPos = new BlockPos(mc.field_1724.method_31477(), y - 1, mc.field_1724.method_31479());
     BlockPos feetPos = floorPos.method_10084();
     BlockPos headPos = feetPos.method_10084();
     
     BlockState floorState = mc.field_1687.method_8320(floorPos);
     if (floorState.method_26220((BlockView)mc.field_1687, floorPos).method_1110()) {
       return false;
     }
     
     return (mc.field_1687.method_8320(feetPos).method_26220((BlockView)mc.field_1687, feetPos).method_1110() && mc.field_1687
       .method_8320(headPos).method_26220((BlockView)mc.field_1687, headPos).method_1110());
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\commands\impl\VClipCommand.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */