package shame.nazuna.api.utils.bot;
 import io.netty.channel.Channel;
 import io.netty.channel.ChannelDuplexHandler;
 import io.netty.channel.ChannelHandlerContext;
 import io.netty.util.ReferenceCountUtil;
 import java.lang.reflect.Constructor;
 import java.lang.reflect.Field;
 import java.lang.reflect.Method;
 import java.lang.reflect.RecordComponent;
 import java.util.ArrayList;
 import java.util.LinkedHashSet;
 import java.util.List;
 import java.util.Optional;
 import java.util.Set;
 import java.util.UUID;
 import java.util.concurrent.CopyOnWriteArrayList;
 import net.minecraft.EntityPositionSyncS2CPacket;
 import net.minecraft.Hand;
 import net.minecraft.Vec3d;
 import net.minecraft.ClientConnection;
 import net.minecraft.Text;
 import net.minecraft.Packet;
 import net.minecraft.KeepAliveS2CPacket;
 import net.minecraft.PlayerPositionLookS2CPacket;
 import net.minecraft.ResourcePackSendS2CPacket;
 import net.minecraft.HealthUpdateS2CPacket;
 import net.minecraft.TeleportConfirmC2SPacket;
 import net.minecraft.KeepAliveC2SPacket;
 import net.minecraft.PlayerMoveC2SPacket;
 import net.minecraft.ResourcePackStatusC2SPacket;
 import net.minecraft.HandSwingC2SPacket;
 import net.minecraft.PlayerInteractItemC2SPacket;
 import net.minecraft.MinecraftClient;
 import net.minecraft.Session;
 import net.minecraft.MathHelper;
 import net.minecraft.ConnectScreen;
 import net.minecraft.Screen;
 import net.minecraft.TitleScreen;
 import net.minecraft.MultiplayerScreen;
 import net.minecraft.ClientPlayNetworkHandler;
 import net.minecraft.ClientPlayerInteractionManager;
 import net.minecraft.CommonPingS2CPacket;
 import net.minecraft.CommonPongC2SPacket;
 import net.minecraft.ClientWorld;
 import net.minecraft.ServerAddress;
 import net.minecraft.ServerInfo;
 import net.minecraft.ClientPlayerEntity;
 import shame.nazuna.api.storages.implement.helpertstorages.enumvar.ModuleClass;
 import shame.nazuna.client.modules.impl.player.AutoForest;
 import shame.nazuna.mixin.IMinecraftClientAccessor;
 
 public final class BotSessionManager {
   private static final List<BotConnection> connections = new CopyOnWriteArrayList<>();
   private static volatile boolean ignoreBotMessages;
   private static volatile boolean bypassResourcePacksDuringBotConnect;
   
   public static List<BotConnection> getConnections() {
     pruneDeadConnections();
     return new ArrayList<>(connections);
   }
   
   public static boolean shouldBypassResourcePacks() {
     return bypassResourcePacksDuringBotConnect;
   }
   
   public static void finishBotConnectStage() {
     bypassResourcePacksDuringBotConnect = false;
   }
   
   public static String getCurrentSessionName() {
     MinecraftClient mc = MinecraftClient.method_1551();
     return (mc.method_1548() == null) ? "" : mc.method_1548().method_1676();
   }
   
   public static List<String> getSessionNames(boolean includeCurrent) {
     pruneDeadConnections();
     Set<String> names = new LinkedHashSet<>();
     if (includeCurrent) {
       String currentName = getCurrentSessionName();
       if (!currentName.isBlank()) {
         names.add(currentName);
       }
     } 
     
     for (BotConnection bot : connections) {
       if (bot.name() != null && !bot.name().isBlank()) {
         names.add(bot.name());
       }
     } 
     return new ArrayList<>(names);
   }
   
   public static boolean toggleIgnoreBotMessages() {
     ignoreBotMessages = !ignoreBotMessages;
     return ignoreBotMessages;
   }
   
   public static boolean isIgnoreBotMessages() {
     return ignoreBotMessages;
   }
   
   public static void connect(String name, String address) {
     MinecraftClient mc = MinecraftClient.method_1551();
     if (mc.method_1548() == null || name == null || name.isBlank() || address == null || address.isBlank()) {
       return;
     }
     
     Session originalSession = mc.method_1548();
     ServerInfo originalServerInfo = mc.method_1558();
     pruneDeadConnections();
     disconnectSessionsByName(name, (Text)Text.method_43470("Replaced"));
     BotConnection previous = freezeCurrentSession();
     ModuleClass.autoForest.resetToDefaults();
     ((IMinecraftClientAccessor)mc).setSession(createSessionWithName(mc.method_1548(), name));
     bypassResourcePacksDuringBotConnect = true;
     mc.execute(() -> {
 
 
 
 
           
           try {
 
             
             ConnectScreen.method_36877((Screen)new MultiplayerScreen((Screen)new TitleScreen()), mc, ServerAddress.method_2950(address), new ServerInfo(address, address, ServerInfo.class_8678.field_45611), false, null);
           } catch (Exception ignored) {
             bypassResourcePacksDuringBotConnect = false;
             restoreAfterConnectFailure(mc, previous, originalSession, originalServerInfo);
           } 
         });
   }
   
   public static void pulseBots(boolean rightClick) {
     for (BotConnection bot : connections) {
       if (!isConnectionUsable(bot))
         continue;  if (rightClick) {
         bot.handler().method_52787((Packet)new PlayerInteractItemC2SPacket(Hand.field_5808, 0, bot.player().method_36454(), bot.player().method_36455())); continue;
       } 
       bot.handler().method_52787((Packet)new HandSwingC2SPacket(Hand.field_5808));
     } 
   }
 
   
   public static void sayAll(String message) {
     for (BotConnection bot : connections) {
       if (!isConnectionUsable(bot))
         continue;  if (message.startsWith("/")) {
         bot.handler().method_45730(message.substring(1)); continue;
       } 
       bot.handler().method_45729(message);
     } 
   }
 
   
   public static boolean control(String name) {
     if (name == null || name.isBlank()) {
       return false;
     }
     
     pruneDeadConnections();
     MinecraftClient mc = MinecraftClient.method_1551();
     if (mc.field_1724 != null && mc.field_1687 != null && name.equalsIgnoreCase(getCurrentSessionName())) {
       return true;
     }
     
     return ((Boolean)connections.stream()
       .filter(bot -> matchesName(bot.name(), name))
       .findFirst()
       .map(bot -> {
           if (!isConnectionUsable(bot)) {
             connections.remove(bot);
             
             return Boolean.valueOf(false);
           } 
           BotConnection previous = freezeCurrentSession();
           if (!activateSession(bot)) {
             if (previous != null && activateSession(previous)) {
               connections.remove(previous);
             }
             return Boolean.valueOf(false);
           } 
           connections.remove(bot);
           return Boolean.valueOf(true);
         }).orElse(Boolean.valueOf(false))).booleanValue();
   }
   
   public static boolean say(String name, String message) {
     pruneDeadConnections();
     return ((Boolean)connections.stream()
       .filter(bot -> matchesName(bot.name(), name))
       .findFirst()
       .map(bot -> {
           if (!isConnectionUsable(bot)) {
             connections.remove(bot);
             
             return Boolean.valueOf(false);
           } 
           if (message.startsWith("/")) {
             bot.handler().method_45730(message.substring(1));
           } else {
             bot.handler().method_45729(message);
           } 
           return Boolean.valueOf(true);
         }).orElse(Boolean.valueOf(false))).booleanValue();
   }
   
   public static boolean remove(String name) {
     if (name == null || name.isBlank()) {
       return false;
     }
     
     return (disconnectSessionsByName(name, (Text)Text.method_43470("Removed")) > 0);
   }
   
   public static boolean restore() {
     return restore(null);
   }
   
   public static boolean restore(String name) {
     pruneDeadConnections();
 
     
     String targetName = (name == null || name.isBlank()) ? (connections.isEmpty() ? "" : ((BotConnection)connections.get(connections.size() - 1)).name()) : name;
     return (!targetName.isBlank() && control(targetName));
   }
   
   private static BotConnection freezeCurrentSession() {
     MinecraftClient mc = MinecraftClient.method_1551();
     if (mc.method_1562() == null || mc.field_1687 == null || mc.field_1724 == null) {
       return null;
     }
     
     ClientPlayNetworkHandler handler = mc.method_1562();
     makeNettyBot(handler, mc.method_1548().method_1676(), mc.field_1724);
 
 
 
 
 
 
 
 
 
     
     BotConnection connection = new BotConnection(mc.method_1548().method_1676(), (mc.method_1558() != null) ? (mc.method_1558()).field_3761 : "", handler.method_48296(), handler, mc.field_1687, mc.field_1724, mc.field_1761, mc.method_1548(), mc.method_1558(), ModuleClass.autoForest.captureState());
     
     replaceConnection(connection);
     clearActiveSession(mc);
     return connection;
   }
   
   private static boolean activateSession(BotConnection bot) {
     if (!isConnectionUsable(bot)) {
       return false;
     }
     
     MinecraftClient mc = MinecraftClient.method_1551();
     IMinecraftClientAccessor accessor = (IMinecraftClientAccessor)mc;
     
     Channel channel = getChannel(bot.connection());
     if (channel != null && channel.pipeline().get("bot_filter") != null) {
       channel.pipeline().remove("bot_filter");
     }
     
     try {
       setMinecraftClientField(mc, ClientPlayNetworkHandler.class, bot.handler());
       accessor.setSession((bot.session() != null) ? bot.session() : createSessionWithName(mc.method_1548(), bot.name()));
       setMinecraftClientField(mc, ServerInfo.class, (bot.serverInfo() != null) ? bot.serverInfo() : createServerInfo(bot.name(), bot.address()));
       accessor.setItemUseCooldown(0);
       
       mc.field_1687 = bot.world();
       mc.field_1724 = bot.player();
       mc.field_1719 = (Entity)bot.player();
       mc.field_1761 = bot.interactionManager();
       
       if (mc.field_1769 != null) {
         mc.field_1769.method_3244(bot.world());
       }
       
       ModuleClass.autoForest.applyState(bot.autoForestState());
       bot.handler().method_52787((Packet)new PlayerMoveC2SPacket.class_2830(bot
             .player().method_23317(), bot
             .player().method_23318(), bot
             .player().method_23321(), bot
             .player().method_36454(), bot
             .player().method_36455(), bot
             .player().method_24828(), 
             (bot.player()).field_5976));
       
       mc.method_1507(null);
       return true;
     } catch (Exception ignored) {
       return false;
     } 
   }
   
   private static void clearActiveSession(MinecraftClient mc) {
     IMinecraftClientAccessor accessor = (IMinecraftClientAccessor)mc;
     setMinecraftClientField(mc, ClientPlayNetworkHandler.class, null);
     accessor.setItemUseCooldown(0);
     mc.field_1687 = null;
     mc.field_1724 = null;
     mc.field_1719 = null;
     mc.field_1761 = null;
     if (mc.field_1769 != null) {
       mc.field_1769.method_3244(null);
     }
   }
   
   private static void replaceConnection(BotConnection connection) {
     disconnectSessionsByName(connection.name(), (Text)Text.method_43470("Replaced"));
     connections.add(connection);
   }
   
   private static void makeNettyBot(final ClientPlayNetworkHandler handler, final String name, final ClientPlayerEntity botPlayer) {
     Channel channel = getChannel(handler.method_48296());
     if (channel == null)
       return;  if (channel.pipeline().get("bot_filter") != null) {
       channel.pipeline().remove("bot_filter");
     }
     if (channel.pipeline().get("packet_handler") == null) {
       return;
     }
     
     channel.pipeline().addBefore("packet_handler", "bot_filter", (ChannelHandler)new ChannelDuplexHandler()
         {
           public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
             if (msg instanceof KeepAliveS2CPacket) { KeepAliveS2CPacket packet = (KeepAliveS2CPacket)msg;
               handler.method_48296().method_10743((Packet)new KeepAliveC2SPacket(packet.method_11517()));
               if (botPlayer != null) {
                 handler.method_52787((Packet)new PlayerMoveC2SPacket.class_5911(botPlayer.method_24828(), botPlayer.field_5976));
               }
               ReferenceCountUtil.release(msg);
               
               return; }
             
             if (msg instanceof CommonPingS2CPacket) { CommonPingS2CPacket packet = (CommonPingS2CPacket)msg;
               handler.method_48296().method_10743((Packet)new CommonPongC2SPacket(packet.method_36950()));
               ReferenceCountUtil.release(msg);
               
               return; }
             
             if (msg instanceof ResourcePackSendS2CPacket) { ResourcePackSendS2CPacket packet = (ResourcePackSendS2CPacket)msg;
               handler.method_52787((Packet)new ResourcePackStatusC2SPacket(packet.comp_2158(), ResourcePackStatusC2SPacket.class_2857.field_13016));
               handler.method_52787((Packet)new ResourcePackStatusC2SPacket(packet.comp_2158(), ResourcePackStatusC2SPacket.class_2857.field_13017));
               ReferenceCountUtil.release(msg);
               
               return; }
             
             if (msg instanceof PlayerPositionLookS2CPacket) { PlayerPositionLookS2CPacket packet = (PlayerPositionLookS2CPacket)msg;
               BotSessionManager.applyFrozenPositionLook(botPlayer, packet);
               handler.method_52787((Packet)new TeleportConfirmC2SPacket(packet.comp_3133()));
               if (botPlayer != null) {
                 handler.method_52787((Packet)new PlayerMoveC2SPacket.class_2830(botPlayer
                       .method_23317(), botPlayer
                       .method_23318(), botPlayer
                       .method_23321(), botPlayer
                       .method_36454(), botPlayer
                       .method_36455(), botPlayer
                       .method_24828(), botPlayer.field_5976));
               }
 
               
               ReferenceCountUtil.release(msg);
               
               return; }
             
             if (msg instanceof EntityPositionSyncS2CPacket) { EntityPositionSyncS2CPacket packet = (EntityPositionSyncS2CPacket)msg;
               BotSessionManager.applyFrozenEntityPositionSync(botPlayer, packet);
               ReferenceCountUtil.release(msg);
               
               return; }
             
             if (msg instanceof HealthUpdateS2CPacket) { HealthUpdateS2CPacket packet = (HealthUpdateS2CPacket)msg;
               if (botPlayer != null) {
                 botPlayer.method_6033(packet.method_11833());
               }
               ReferenceCountUtil.release(msg);
               
               return; }
             
             if (msg instanceof net.minecraft.DisconnectS2CPacket) {
               BotSessionManager.connections.removeIf(bot -> BotSessionManager.matchesName(bot.name(), name));
               ctx.close();
               ReferenceCountUtil.release(msg);
               
               return;
             } 
             String packetName = msg.getClass().getSimpleName();
             if (packetName.contains("Sound") || packetName
               .contains("Particle") || packetName
               .contains("Screen") || (BotSessionManager.ignoreBotMessages && 
               BotSessionManager.isBotMessagePacket(packetName)) || packetName
               .contains("Explosion") || packetName
               .contains("BossBar") || packetName
               .contains("Scoreboard") || packetName
               .contains("OverlayMessage")) {
               ReferenceCountUtil.release(msg);
               return;
             } 
             super.channelRead(ctx, msg);
           }
 
           
           public void channelInactive(ChannelHandlerContext ctx) throws Exception {
             BotSessionManager.connections.removeIf(bot -> BotSessionManager.matchesName(bot.name(), name));
             super.channelInactive(ctx);
           }
 
           
           public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
             BotSessionManager.connections.removeIf(bot -> BotSessionManager.matchesName(bot.name(), name));
             ctx.close();
           }
         });
   }
   
   private static boolean isBotMessagePacket(String packetName) {
     return (packetName.contains("Chat") || packetName
       .contains("Message") || packetName
       .contains("Title") || packetName
       .contains("Overlay"));
   }
   
   private static Channel getChannel(ClientConnection connection) {
     try {
       for (Field field : ClientConnection.class.getDeclaredFields()) {
         if (Channel.class.isAssignableFrom(field.getType())) {
           field.setAccessible(true);
           return (Channel)field.get(connection);
         } 
       } 
     } catch (Exception exception) {}
     
     return null;
   }
   
   private static void applyFrozenPositionLook(ClientPlayerEntity botPlayer, PlayerPositionLookS2CPacket packet) {
     if (botPlayer == null || packet == null) {
       return;
     }
     
     double x = readPacketDouble(packet, "x", botPlayer.method_23317());
     double y = readPacketDouble(packet, "y", botPlayer.method_23318());
     double z = readPacketDouble(packet, "z", botPlayer.method_23321());
     float yaw = (float)readPacketDouble(packet, "yaw", botPlayer.method_36454());
     float pitch = (float)readPacketDouble(packet, "pitch", botPlayer.method_36455());
     Object change = readPacketComponent(packet, "change");
     if (change == null) {
       change = readPacketComponent(packet, "flags");
     }
     
     if (hasRelativeFlag(change, "X")) {
       x += botPlayer.method_23317();
     }
     if (hasRelativeFlag(change, "Y")) {
       y += botPlayer.method_23318();
     }
     if (hasRelativeFlag(change, "Z")) {
       z += botPlayer.method_23321();
     }
     if (hasRelativeFlag(change, "Y_ROT")) {
       yaw += botPlayer.method_36454();
     }
     if (hasRelativeFlag(change, "X_ROT")) {
       pitch += botPlayer.method_36455();
     }
     
     pitch = MathHelper.method_15363(pitch, -90.0F, 90.0F);
     botPlayer.method_5808(x, y, z, yaw, pitch);
     botPlayer.method_36456(yaw);
     botPlayer.method_36457(pitch);
   }
   
   private static void applyFrozenEntityPositionSync(ClientPlayerEntity botPlayer, EntityPositionSyncS2CPacket packet) {
     if (botPlayer == null || packet == null || packet.comp_3223() != botPlayer.method_5628() || packet.comp_3224() == null) {
       return;
     }
     
     Vec3d position = packet.comp_3224().comp_3148();
     if (position == null) {
       return;
     }
     
     float yaw = packet.comp_3224().comp_3150();
     float pitch = MathHelper.method_15363(packet.comp_3224().comp_3151(), -90.0F, 90.0F);
     botPlayer.method_5808(position.field_1352, position.field_1351, position.field_1350, yaw, pitch);
     botPlayer.method_36456(yaw);
     botPlayer.method_36457(pitch);
     if (packet.comp_3224().comp_3149() != null) {
       botPlayer.method_18799(packet.comp_3224().comp_3149());
     }
     botPlayer.method_24830(packet.comp_3225());
   }
   
   private static double readPacketDouble(Object packet, String name, double fallback) {
     Object value = readPacketComponent(packet, name);
     Number number = (Number)value; return (value instanceof Number) ? number.doubleValue() : fallback;
   }
   
   private static Object readPacketComponent(Object packet, String name) {
     if (packet == null || name == null || name.isBlank()) {
       return null;
     }
     
     try {
       Method method = packet.getClass().getMethod(name, new Class[0]);
       method.setAccessible(true);
       return method.invoke(packet, new Object[0]);
     } catch (Exception exception) {
 
       
       try {
         Method method = packet.getClass().getMethod("get" + Character.toUpperCase(name.charAt(0)) + name.substring(1), new Class[0]);
         method.setAccessible(true);
         return method.invoke(packet, new Object[0]);
       } catch (Exception exception1) {
 
         
         try {
           RecordComponent[] components = packet.getClass().getRecordComponents();
           if (components != null) {
             for (RecordComponent component : components) {
               if (name.equals(component.getName())) {
                 return component.getAccessor().invoke(packet, new Object[0]);
               }
             } 
           }
         } catch (Exception exception2) {}
 
         
         try {
           for (Field field : packet.getClass().getDeclaredFields()) {
             if (name.equalsIgnoreCase(field.getName())) {
               field.setAccessible(true);
               return field.get(packet);
             } 
           } 
         } catch (Exception exception2) {}
 
         
         return null;
       } 
     } 
   }
   private static boolean hasRelativeFlag(Object flags, String flagName) { if (flags instanceof Iterable) { Iterable<?> iterable = (Iterable)flags; if (flagName != null) {
 
 
         
         for (Object flag : iterable) {
           if (flag instanceof Enum) { Enum<?> enumFlag = (Enum)flag; if (flagName.equals(enumFlag.name()))
               return true;  }
         
         } 
         return false;
       }  }
     
     return false; } private static Session createSessionWithName(Session current, String name) {
     try {
       Constructor<Session> constructor = Session.class.getDeclaredConstructor(new Class[] { String.class, UUID.class, String.class, Optional.class, Optional.class, Session.class_321.class });
 
 
 
 
 
 
       
       constructor.setAccessible(true);
       return constructor.newInstance(new Object[] { name, 
             
             UUID.randomUUID(), 
             (current == null) ? "" : current.method_1674(), 
             Optional.empty(), 
             Optional.empty(), Session.class_321.field_1988 });
     
     }
     catch (Exception e) {
       throw new RuntimeException(e);
     } 
   }
   
   private static void setMinecraftClientField(MinecraftClient mc, Class<?> fieldType, Object value) {
     try {
       for (Field field : MinecraftClient.class.getDeclaredFields()) {
         if (field.getType() == fieldType) {
           field.setAccessible(true);
           field.set(mc, value);
           return;
         } 
       } 
     } catch (Exception exception) {}
   }
 
   
   private static ServerInfo createServerInfo(String name, String address) {
     String safeAddress = (address == null) ? "" : address;
     String safeName = (name == null || name.isBlank()) ? safeAddress : name;
     return new ServerInfo(safeName, safeAddress, ServerInfo.class_8678.field_45611);
   }
   
   private static void restoreAfterConnectFailure(MinecraftClient mc, BotConnection previous, Session originalSession, ServerInfo originalServerInfo) {
     try {
       bypassResourcePacksDuringBotConnect = false;
       if (previous != null && activateSession(previous)) {
         connections.remove(previous);
         
         return;
       } 
       IMinecraftClientAccessor accessor = (IMinecraftClientAccessor)mc;
       accessor.setSession(originalSession);
       setMinecraftClientField(mc, ServerInfo.class, originalServerInfo);
     } catch (Exception exception) {}
   }
 
   
   private static int disconnectSessionsByName(String name, Text reason) {
     if (name == null || name.isBlank()) {
       return 0;
     }
     
     int removed = 0;
     for (BotConnection bot : new ArrayList(connections)) {
       if (!matchesName(bot.name(), name)) {
         continue;
       }
       
       connections.remove(bot);
       removed++;
       try {
         if (bot.connection() != null) {
           bot.connection().method_10747(reason);
         }
       } catch (Exception exception) {}
     } 
     
     return removed;
   }
   
   private static void pruneDeadConnections() {
     connections.removeIf(bot -> !isConnectionUsable(bot));
   }
   
   private static boolean isConnectionUsable(BotConnection bot) {
     if (bot == null || bot.name() == null || bot.name().isBlank()) {
       return false;
     }
     if (bot.connection() == null || bot.handler() == null || bot.world() == null || bot.player() == null) {
       return false;
     }
     if ((bot.player()).field_3944 != bot.handler()) {
       return false;
     }
     
     Channel channel = getChannel(bot.connection());
     return (channel == null || channel.isOpen());
   }
   
   private static boolean matchesName(String left, String right) {
     return (left != null && right != null && left.equalsIgnoreCase(right));
   }
   
   public static final class BotConnection {
     private final String name;
     private final String address;
     private final ClientConnection connection;
     private final ClientPlayNetworkHandler handler;
     private final ClientWorld world;
     private final ClientPlayerEntity player;
     private final ClientPlayerInteractionManager interactionManager;
     private final Session session;
     private final ServerInfo serverInfo;
     private final AutoForest.SessionState autoForestState;
     
     public BotConnection(String name, String address, ClientConnection connection, ClientPlayNetworkHandler handler, ClientWorld world, ClientPlayerEntity player, ClientPlayerInteractionManager interactionManager, Session session, ServerInfo serverInfo, AutoForest.SessionState autoForestState) {
       this.name = name;
       this.address = address;
       this.connection = connection;
       this.handler = handler;
       this.world = world;
       this.player = player;
       this.interactionManager = interactionManager;
       this.session = session;
       this.serverInfo = serverInfo;
       this.autoForestState = autoForestState;
     }
     
     public String name() { return this.name; }
     public String address() { return this.address; }
     public ClientConnection connection() { return this.connection; }
     public ClientPlayNetworkHandler handler() { return this.handler; }
     public ClientWorld world() { return this.world; }
     public ClientPlayerEntity player() { return this.player; }
     public ClientPlayerInteractionManager interactionManager() { return this.interactionManager; }
     public Session session() { return this.session; }
     public ServerInfo serverInfo() { return this.serverInfo; } public AutoForest.SessionState autoForestState() {
       return this.autoForestState;
     }
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\bot\BotSessionManager.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */