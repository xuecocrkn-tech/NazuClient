package shame.nazuna.client.modules.impl.render;
 
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.util.ArrayList;
 import java.util.HashSet;
 import java.util.List;
 import java.util.Map;
 import java.util.Set;
 import java.util.concurrent.ConcurrentHashMap;
 import net.minecraft.ShaderProgramKeys;
 import net.minecraft.ChunkPos;
 import net.minecraft.BlockPos;
 import net.minecraft.Position;
 import net.minecraft.Vec3i;
 import net.minecraft.Vec3d;
 import net.minecraft.BlockState;
 import net.minecraft.WorldChunk;
 import net.minecraft.BufferRenderer;
 import net.minecraft.BufferBuilder;
 import net.minecraft.Tessellator;
 import net.minecraft.VertexFormats;
 import net.minecraft.VertexFormat;
 import net.minecraft.MatrixStack;
 import net.minecraft.Registries;
 import org.joml.Matrix4f;
 import shame.nazuna.api.events.EventLink;
 import shame.nazuna.api.events.implement.Event3DRender;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.FloatSetting;
 
 public class BlockESP extends Module {
   public static BlockESP INSTANCE = new BlockESP();
   
   private static final float BOX_LINE_WIDTH = 2.0F;
   
   private static final float FILL_ALPHA = 0.18F;
   private static final float GREEN_R = 0.1F;
   private static final float GREEN_G = 1.0F;
   private static final float GREEN_B = 0.15F;
   private static final long SCAN_INTERVAL_MS = 50L;
   private static final int MAX_CHUNKS_PER_PASS = 2;
   private final FloatSetting distance = new FloatSetting("Дистанция", 60.0F, 10.0F, 120.0F, 1.0F);
   
   private final Set<String> trackedBlocks = ConcurrentHashMap.newKeySet();
   private final Map<BlockPos, String> foundBlocks = new ConcurrentHashMap<>();
   private final Set<ChunkPos> scannedChunks = ConcurrentHashMap.newKeySet();
   
   private ChunkPos lastPlayerChunk;
   private int lastScanRadius = -1;
   private long lastScanTime;
   
   public BlockESP() {
     super("BlockESP", "Показывает выбранные блоки через стену", Module.ModuleCategory.RENDER);
     addSettings(new Setting[] { (Setting)this.distance });
   }
 
   
   public void onEnable() {
     resetScanState();
     super.onEnable();
   }
 
   
   public void onDisable() {
     resetScanState();
     super.onDisable();
   }
   
   @EventLink(priority = 100)
   public void onRender3D(Event3DRender event) {
     if (mc.field_1687 == null || mc.field_1724 == null || this.trackedBlocks.isEmpty()) {
       return;
     }
     
     int scanRadius = getDistance();
     ChunkPos currentChunk = new ChunkPos(mc.field_1724.method_24515());
     
     if (scanRadius != this.lastScanRadius) {
       resetScanState();
       this.lastScanRadius = scanRadius;
     } 
     
     if (this.lastPlayerChunk == null || !this.lastPlayerChunk.equals(currentChunk)) {
       this.scannedChunks.clear();
       this.lastPlayerChunk = currentChunk;
     } 
     
     long now = System.currentTimeMillis();
     if (now - this.lastScanTime >= 50L) {
       scanNearbyBlocks(scanRadius);
       this.lastScanTime = now;
     } 
     
     cleanupInvalidAndDistantBlocks(mc.field_1724.method_19538(), scanRadius);
     renderFoundBlocks(event.getMatrices());
   }
   
   private void scanNearbyBlocks(int scanRadius) {
     if (mc.field_1687 == null || mc.field_1724 == null) {
       return;
     }
     
     BlockPos playerPos = mc.field_1724.method_24515();
     int playerChunkX = playerPos.method_10263() >> 4;
     int playerChunkZ = playerPos.method_10260() >> 4;
     int chunkRange = (scanRadius >> 4) + 2;
     
     List<ChunkPos> candidates = new ArrayList<>();
     for (int cx = -chunkRange; cx <= chunkRange; cx++) {
       for (int cz = -chunkRange; cz <= chunkRange; cz++) {
         ChunkPos chunkPos = new ChunkPos(playerChunkX + cx, playerChunkZ + cz);
         if (!this.scannedChunks.contains(chunkPos)) {
           candidates.add(chunkPos);
         }
       } 
     } 
     
     candidates.sort((a, b) -> {
           long da = chunkDistanceSq(a, playerChunkX, playerChunkZ);
           
           long db = chunkDistanceSq(b, playerChunkX, playerChunkZ);
           return Long.compare(da, db);
         });
     int scannedThisPass = 0;
     for (ChunkPos chunkPos : candidates) {
       if (scannedThisPass >= 2) {
         break;
       }
       
       WorldChunk chunk = mc.field_1687.method_8497(chunkPos.field_9181, chunkPos.field_9180);
       if (chunk == null) {
         continue;
       }
       
       scanChunk(chunk, playerPos, scanRadius);
       this.scannedChunks.add(chunkPos);
       scannedThisPass++;
     } 
   }
   
   private void scanChunk(WorldChunk chunk, BlockPos playerPos, int scanRadius) {
     int minX = chunk.method_12004().method_8326();
     int minZ = chunk.method_12004().method_8328();
     int maxX = minX + 15;
     int maxZ = minZ + 15;
     
     int minY = Math.max(mc.field_1687.method_31607(), playerPos.method_10264() - scanRadius);
     int maxY = Math.min(mc.field_1687.method_31600(), playerPos.method_10264() + scanRadius);
     int radiusSq = scanRadius * scanRadius;
     
     BlockPos.class_2339 mutable = new BlockPos.class_2339();
     
     for (int x = minX; x <= maxX; x++) {
       for (int z = minZ; z <= maxZ; z++) {
         for (int y = minY; y <= maxY; y++) {
           mutable.method_10103(x, y, z);
           
           if (mutable.method_10262((Vec3i)playerPos) <= radiusSq) {
 
 
             
             BlockState state = chunk.method_8320((BlockPos)mutable);
             if (!state.method_26215()) {
 
 
               
               String blockName = Registries.field_41175.method_10221(state.method_26204()).method_12832().toLowerCase();
               if (this.trackedBlocks.contains(blockName))
                 this.foundBlocks.put(mutable.method_10062(), blockName); 
             } 
           } 
         } 
       } 
     } 
   }
   private void cleanupInvalidAndDistantBlocks(Vec3d playerPos, int renderDistance) {
     if (mc.field_1687 == null) {
       this.foundBlocks.clear();
       
       return;
     } 
     int renderDistanceSq = renderDistance * renderDistance;
     this.foundBlocks.entrySet().removeIf(entry -> {
           BlockPos pos = (BlockPos)entry.getKey();
           BlockState currentState = mc.field_1687.method_8320(pos);
           if (currentState.method_26215()) {
             return true;
           }
           String currentBlockName = Registries.field_41175.method_10221(currentState.method_26204()).method_12832().toLowerCase();
           return !this.trackedBlocks.contains(currentBlockName) ? true : ((pos.method_19770((Position)playerPos) > renderDistanceSq));
         });
   }
 
 
 
 
 
   
   private void renderFoundBlocks(MatrixStack matrices) {
     if (this.foundBlocks.isEmpty()) {
       return;
     }
     
     Vec3d camera = mc.field_1773.method_19418().method_19326();
     
     matrices.method_22903();
     matrices.method_22904(-camera.field_1352, -camera.field_1351, -camera.field_1350);
     Matrix4f matrix = matrices.method_23760().method_23761();
     
     RenderSystem.enableBlend();
     RenderSystem.defaultBlendFunc();
     RenderSystem.disableCull();
     RenderSystem.disableDepthTest();
     RenderSystem.depthMask(false);
     RenderSystem.setShader(ShaderProgramKeys.field_53876);
     
     Tessellator tessellator = Tessellator.method_1348();
     BufferBuilder fillBuffer = tessellator.method_60827(VertexFormat.class_5596.field_27382, VertexFormats.field_1576);
     for (BlockPos pos : this.foundBlocks.keySet()) {
       addFilledBox(fillBuffer, matrix, pos, 0.1F, 1.0F, 0.15F, 0.18F);
     }
     BufferRenderer.method_43433(fillBuffer.method_60800());
     
     RenderSystem.lineWidth(2.0F);
     BufferBuilder lineBuffer = tessellator.method_60827(VertexFormat.class_5596.field_29344, VertexFormats.field_1576);
     for (BlockPos pos : this.foundBlocks.keySet()) {
       addOutlinedBox(lineBuffer, matrix, pos, 0.1F, 1.0F, 0.15F, 1.0F);
     }
     BufferRenderer.method_43433(lineBuffer.method_60800());
     
     RenderSystem.enableCull();
     RenderSystem.enableDepthTest();
     RenderSystem.depthMask(true);
     RenderSystem.disableBlend();
     matrices.method_22909();
   }
   
   private void addFilledBox(BufferBuilder buffer, Matrix4f matrix, BlockPos pos, float r, float g, float b, float a) {
     float minX = pos.method_10263();
     float minY = pos.method_10264();
     float minZ = pos.method_10260();
     float maxX = minX + 1.0F;
     float maxY = minY + 1.0F;
     float maxZ = minZ + 1.0F;
     
     buffer.method_22918(matrix, minX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, minY, maxZ).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, minX, maxY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, minZ).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, minX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, minZ).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, minX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, maxZ).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, minX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, minZ).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, maxX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, maxZ).method_22915(r, g, b, a);
   }
   
   private void addOutlinedBox(BufferBuilder buffer, Matrix4f matrix, BlockPos pos, float r, float g, float b, float a) {
     float minX = pos.method_10263();
     float minY = pos.method_10264();
     float minZ = pos.method_10260();
     float maxX = minX + 1.0F;
     float maxY = minY + 1.0F;
     float maxZ = minZ + 1.0F;
     
     buffer.method_22918(matrix, minX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, minY, minZ).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, minX, maxY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, minZ).method_22915(r, g, b, a);
     
     buffer.method_22918(matrix, minX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, minZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, maxX, maxY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, minY, maxZ).method_22915(r, g, b, a);
     buffer.method_22918(matrix, minX, maxY, maxZ).method_22915(r, g, b, a);
   }
   
   public void addBlock(String blockName) {
     this.trackedBlocks.add(blockName.toLowerCase());
     this.scannedChunks.clear();
     this.foundBlocks.clear();
   }
   
   public void removeBlock(String blockName) {
     this.trackedBlocks.remove(blockName.toLowerCase());
     this.foundBlocks.entrySet().removeIf(entry -> ((String)entry.getValue()).equalsIgnoreCase(blockName));
   }
   
   public void clearBlocks() {
     this.trackedBlocks.clear();
     resetScanState();
   }
   
   public Set<String> getTrackedBlocks() {
     return new HashSet<>(this.trackedBlocks);
   }
   
   public boolean isTracking(String blockName) {
     return this.trackedBlocks.contains(blockName.toLowerCase());
   }
   
   private int getDistance() {
     return Math.round(this.distance.get());
   }
   
   private long chunkDistanceSq(ChunkPos chunkPos, int playerChunkX, int playerChunkZ) {
     long dx = (chunkPos.field_9181 - playerChunkX);
     long dz = (chunkPos.field_9180 - playerChunkZ);
     return dx * dx + dz * dz;
   }
   
   private void resetScanState() {
     this.foundBlocks.clear();
     this.scannedChunks.clear();
     this.lastPlayerChunk = null;
     this.lastScanTime = 0L;
     this.lastScanRadius = -1;
   }
 }

