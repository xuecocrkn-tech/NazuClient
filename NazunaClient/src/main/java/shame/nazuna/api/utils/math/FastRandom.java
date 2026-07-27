package shame.nazuna.api.utils.math;
 
 import java.util.Random;
 import java.util.concurrent.ThreadLocalRandom;
 
 public class FastRandom extends Random {
   private final ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
   private Random random = null;
   
   private volatile boolean seedSet;
   
   private void validateRandom() {
     if (this.random == null) {
       this.random = new Random(this.seed);
       this.seedUpdated = false;
     } else if (this.seedUpdated) {
       this.random.setSeed(this.seed);
       this.seedUpdated = false;
     } 
   }
   private volatile boolean seedUpdated; private volatile long seed;
   public static long mix(long left, long right) {
     left *= left * 6364136223846793005L + 1442695040888963407L;
     return left + right;
   }
   public void setSeed(long seed) {
     this.seed = seed;
     this.seedSet = true;
     this.seedUpdated = true;
   }
   
   public void nextBytes(byte[] bytes) {
     if (this.seedSet) {
       validateRandom();
       this.random.nextBytes(bytes);
     } else {
       this.threadLocalRandom.nextBytes(bytes);
     } 
   }
   
   public int nextInt() {
     if (this.seedSet) {
       validateRandom();
       return this.random.nextInt();
     } 
     return this.threadLocalRandom.nextInt();
   }
 
   
   public int nextInt(int bound) {
     if (this.seedSet) {
       validateRandom();
       return this.random.nextInt(bound);
     } 
     return this.threadLocalRandom.nextInt(bound);
   }
 
   
   public long nextLong() {
     if (this.seedSet) {
       validateRandom();
       return this.random.nextLong();
     } 
     return this.threadLocalRandom.nextLong();
   }
 
   
   public boolean nextBoolean() {
     if (this.seedSet) {
       validateRandom();
       return this.random.nextBoolean();
     } 
     return this.threadLocalRandom.nextBoolean();
   }
 
   
   public float nextFloat() {
     if (this.seedSet) {
       validateRandom();
       return this.random.nextFloat();
     } 
     return this.threadLocalRandom.nextFloat();
   }
 
   
   public double nextDouble() {
     if (this.seedSet) {
       validateRandom();
       return this.random.nextDouble();
     } 
     return this.threadLocalRandom.nextDouble();
   }
 
   
   public double nextGaussian() {
     if (this.seedSet) {
       validateRandom();
       return this.random.nextGaussian();
     } 
     return this.threadLocalRandom.nextGaussian();
   }
 }


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\ap\\utils\math\FastRandom.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */