package shame.nazuna.api.utils.tps;

import net.minecraft.MinecraftClient;
import net.minecraft.MathHelper;
import shame.nazuna.api.events.EventLink;
import shame.nazuna.api.events.implement.EventPacket;

public class TPSCalc {
    private float TPS = 20.0F;
    private long timestamp = 0L;
    private long lastPacketTime = 0L;
    private int sampleIndex = 0;
    private float[] tpsSamples;
    public float adjustTicks = 0.0F;

    public TPSCalc() {
        this.tpsSamples = new float[20];
    }

    public long getLastPacketTime() { return this.lastPacketTime; }
    public float[] getTpsSamples() { return this.tpsSamples; }

    @EventLink
    public void onPacket(EventPacket e) {
        if (e.getType() == EventPacket.Type.RECEIVE && e.getPacket() instanceof net.minecraft.WorldTimeUpdateS2CPacket) {
            updateTPS();
        }
    }

    public float getTPS() {
        if (this.lastPacketTime == 0L) {
            return this.TPS;
        }

        MinecraftClient mc = MinecraftClient.method_1551();
        if (mc == null || mc.method_1562() == null || System.currentTimeMillis() - this.lastPacketTime > 3500L) {
            return 20.0F;
        }

        return this.TPS;
    }

    private void updateTPS() {
        long now = System.nanoTime();
        this.lastPacketTime = System.currentTimeMillis();
        if (this.timestamp == 0L) {
            this.timestamp = now;
            return;
        }
        long delay = now - this.timestamp;
        this.timestamp = now;
        if (delay <= 0L) {
            return;
        }

        float maxTPS = 20.0F;
        float rawTPS = maxTPS * 1.0E9F / (float)delay;
        float boundedTPS = MathHelper.method_15363(rawTPS, 0.0F, maxTPS);

        this.tpsSamples[this.sampleIndex % 20] = boundedTPS;
        this.sampleIndex++;

        int sampleCount = Math.min(this.sampleIndex, 20);
        float sum = 0.0F;
        for (int i = 0; i < sampleCount; i++) {
            float sample = this.tpsSamples[i];
            sum += sample;
        }

        this.TPS = (float)round((sum / sampleCount));
        this.adjustTicks = this.TPS - maxTPS;
    }

    public double round(double input) {
        return Math.round(input * 10.0D) / 10.0D;
    }
}
