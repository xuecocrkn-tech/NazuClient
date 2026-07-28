package shame.nazuna.client.modules.impl.render;
 import com.mojang.blaze3d.systems.RenderSystem;
 import java.util.Map;
 import java.util.concurrent.ConcurrentHashMap;
 import shame.nazuna.api.events.implement.EventRender;
 import shame.nazuna.astra;
 import shame.nazuna.client.modules.Module;
 import shame.nazuna.client.modules.impl.render.base.InterfaceProcessing;
 import shame.nazuna.client.modules.impl.render.base.implement.HelperBinds;
 import shame.nazuna.client.modules.impl.render.base.implement.Information;
 import shame.nazuna.client.modules.impl.render.base.implement.KeyBinds;
 import shame.nazuna.client.modules.impl.render.base.implement.KeyStrokes;
 import shame.nazuna.client.modules.impl.render.base.implement.Notifications;
 import shame.nazuna.client.modules.impl.render.base.implement.Potions;
 import shame.nazuna.client.modules.impl.render.base.implement.StaffList;
 import shame.nazuna.client.modules.impl.render.base.implement.WaterMark;
 import shame.nazuna.client.modules.settings.Setting;
 import shame.nazuna.client.modules.settings.implement.BooleanSetting;
 import shame.nazuna.client.modules.settings.implement.ListSetting;
 
 public class Interface extends Module {
   public static Interface INSTANCE = new Interface();
   public final BooleanSetting youGameWatermark = new BooleanSetting("YouGame HUD", true);
   private static final ConcurrentHashMap<String, Long> PERF_WARNINGS = new ConcurrentHashMap<>();
   private static final boolean PERF_DEBUG = Boolean.parseBoolean(System.getProperty("astra.perf.debug", "false"));
   private static final long SLOW_HUD_ELEMENT_NANOS = Long.getLong("astra.perf.hudMs", 5L).longValue() * 1000000L;
   private static final long PERF_WARN_COOLDOWN_NANOS = Long.getLong("astra.perf.cooldownMs", 1000L).longValue() * 1000000L;
   
   private final WaterMark waterMark;
   
   private final ArrayListHud arrayListHud;
   private final KeyBinds keyBinds;
   private final HelperBinds helperBinds;
   private final Potions potions;
   private final KeyStrokes keyStrokes;
   private final Notifications notifications;
   private final TargetHud targetHud;
   private final Session session;
   private final Information information;
   private final StaffList staffList;
   public ModeSetting style = new ModeSetting("Стиль", "Обычный", new String[] { "Обычный" });
   
   private final ListSetting hudModules = new ListSetting("Элементы", new BooleanSetting[] { new BooleanSetting("Ватермарка", true), new BooleanSetting("Аррай лист", true), new BooleanSetting("Горячие клавиши", true), new BooleanSetting("Серверные бинды", true), new BooleanSetting("Зелья", true), new BooleanSetting("Таргет худ", true), new BooleanSetting("Уведомления", true), new BooleanSetting("Стафф", true), (new BooleanSetting("Сессия", true))
 
 
 
 
 
 
 
         
         .visible(() -> Boolean.valueOf(this.style.is("Wave"))), (new BooleanSetting("КейСтроки", true))
         .visible(() -> Boolean.valueOf(this.style.is("Wave"))), new BooleanSetting("Информация", true) });
 
   
   public Interface() {
     super("Interface", "Интерфейс клиента", Module.ModuleCategory.RENDER);
     addSettings(new Setting[] { (Setting)this.hudModules, (Setting)this.style, (Setting)this.youGameWatermark });
     this.waterMark = new WaterMark(astra.draggable(this, "WaterMark", 10.0F, 10.0F));
     this.arrayListHud = new ArrayListHud(astra.draggable(this, "ArrayList", 5.0F, 24.0F));
     this.keyBinds = new KeyBinds(astra.draggable(this, "KeyBinds", 30.0F, 30.0F));
     this.helperBinds = new HelperBinds(astra.draggable(this, "HelperBinds", 90.0F, 30.0F));
     this.potions = new Potions(astra.draggable(this, "Potions", 30.0F, 60.0F));
     this.staffList = new StaffList(astra.draggable(this, "StaffList", 60.0F, 100.0F));
     this.session = new Session(astra.draggable(this, "Session", 70.0F, 30.0F));
     this.keyStrokes = new KeyStrokes(astra.draggable(this, "KeyStrokes", 150.0F, 120.0F));
     this.information = new Information(astra.draggable(this, "Information", 50.0F, 100.0F));
     this.notifications = new Notifications(astra.draggable(this, "Notifications", 0.0F, 0.0F));
     this.targetHud = new TargetHud(astra.draggable(this, "TargetHud", 30.0F, 90.0F));
   }
   
   private void renderHudElement(InterfaceProcessing element, EventRender.Default event) {
     long start = PERF_DEBUG ? System.nanoTime() : 0L;
     element.draggable.beginRenderTilt(event.getContext().method_51448());
     try {
       element.onRender(event);
     } finally {
       element.draggable.endRenderTilt(event.getContext().method_51448());
       if (PERF_DEBUG) {
         long elapsed = System.nanoTime() - start;
         if (elapsed >= SLOW_HUD_ELEMENT_NANOS) {
           logSlowHudElement(element, elapsed);
         }
       } 
     } 
   }
   
   private void logSlowHudElement(InterfaceProcessing element, long elapsedNanos) {
     String name = element.getClass().getSimpleName();
     long now = System.nanoTime();
     Long lastWarn = PERF_WARNINGS.get(name);
     if (lastWarn != null && now - lastWarn.longValue() < PERF_WARN_COOLDOWN_NANOS) {
       return;
     }
     PERF_WARNINGS.put(name, Long.valueOf(now));
     System.out.println(String.format(Locale.ROOT, "[PerfDebug] Slow HUD element: Interface -> %s took %.2f ms", new Object[] { name, 
 
             
             Double.valueOf(elapsedNanos / 1000000.0D) }));
   }
   
   public Map<String, InterfaceProcessing> getConfigurableHudElements() {
     Map<String, InterfaceProcessing> elements = new LinkedHashMap<>();
     elements.put("waterMark", this.waterMark);
     elements.put("arrayList", this.arrayListHud);
     elements.put("keyBinds", this.keyBinds);
     elements.put("helperBinds", this.helperBinds);
     elements.put("potions", this.potions);
     elements.put("keyStrokes", this.keyStrokes);
     elements.put("notifications", this.notifications);
     elements.put("targetHud", this.targetHud);
     elements.put("session", this.session);
     elements.put("information", this.information);
     elements.put("staffList", this.staffList);
     return elements;
   }
   
   @EventLink(priority = -200)
   public void onEvent(EventRender.Default event) {
     boolean waveStyle = this.style.is("Wave");
     boolean showWaterMark = this.hudModules.is("Ватермарка");
     boolean showArrayList = this.hudModules.is("Аррай лист");
     boolean showKeyBinds = this.hudModules.is("Горячие клавиши");
     boolean showHelperBinds = this.hudModules.is("Серверные бинды");
     boolean showPotions = this.hudModules.is("Зелья");
     boolean showKeyStrokes = this.hudModules.is("КейСтроки");
     boolean showInformation = this.hudModules.is("Информация");
     boolean showStaff = this.hudModules.is("Стафф");
     boolean showSession = this.hudModules.is("Сессия");
     boolean showNotifications = this.hudModules.is("Уведомления");
     boolean showTargetHud = this.hudModules.is("Таргет худ");
     
     RenderSystem.disableDepthTest();
     RenderSystem.depthMask(false);
     try {
       if (showWaterMark) renderHudElement((InterfaceProcessing)this.waterMark, event); 
       if (showArrayList && waveStyle) renderHudElement((InterfaceProcessing)this.arrayListHud, event); 
       if (showKeyBinds) renderHudElement((InterfaceProcessing)this.keyBinds, event); 
       if (showHelperBinds) renderHudElement((InterfaceProcessing)this.helperBinds, event); 
       if (showPotions) renderHudElement((InterfaceProcessing)this.potions, event); 
       if (showKeyStrokes && waveStyle) renderHudElement((InterfaceProcessing)this.keyStrokes, event); 
       if (showInformation) renderHudElement((InterfaceProcessing)this.information, event); 
       if (showStaff) renderHudElement((InterfaceProcessing)this.staffList, event); 
       if (showSession && waveStyle) renderHudElement((InterfaceProcessing)this.session, event); 
       if (showNotifications) renderHudElement((InterfaceProcessing)this.notifications, event); 
       if (showTargetHud) renderHudElement((InterfaceProcessing)this.targetHud, event); 
     } finally {
       RenderSystem.depthMask(true);
       RenderSystem.enableDepthTest();
     } 
   }
 }

