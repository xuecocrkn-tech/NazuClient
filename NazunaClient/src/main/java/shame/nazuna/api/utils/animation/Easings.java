package shame.nazuna.api.utils.animation;public final class Easings { public static final double c1 = 1.70158D; public static final double c2 = 2.5949095D; public static final double c3 = 2.70158D; public static final double c4 = 2.0943951023931953D; public static final double c5 = 1.3962634015954636D;
   public static final Easing LINEAR;
   
   private Easings() {
     throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
 
 
   
   static {
     LINEAR = (value -> value);
   } public static final Easing QUAD_IN = powIn(2);
   public static final Easing QUAD_OUT = powOut(2);
   public static final Easing QUAD_IN_OUT = powIN_OUT(2.0D);
   public static final Easing CUBIC_IN = powIn(3);
   public static final Easing CUBIC_OUT = powOut(3);
   public static final Easing CUBIC_IN_OUT = powIN_OUT(3.0D);
   public static final Easing QUART_IN = powIn(4);
   public static final Easing QUART_OUT = powOut(4);
   public static final Easing QUART_IN_OUT = powIN_OUT(4.0D);
   public static final Easing QUINT_IN = powIn(5);
   public static final Easing QUINT_OUT = powOut(5);
   public static final Easing QUINT_IN_OUT = powIN_OUT(5.0D); public static final Easing SINE_IN; public static final Easing SINE_OUT; public static final Easing SINE_IN_OUT; public static final Easing CIRC_IN; public static final Easing CIRC_OUT; public static final Easing CIRC_IN_OUT; public static final Easing ELASTIC_IN; public static final Easing ELASTIC_OUT; public static final Easing ELASTIC_IN_OUT; static {
     SINE_IN = (value -> 1.0D - Math.cos(value * Math.PI / 2.0D));
     SINE_OUT = (value -> Math.sin(value * Math.PI / 2.0D));
     SINE_IN_OUT = (value -> -(Math.cos(Math.PI * value) - 1.0D) / 2.0D);
     CIRC_IN = (value -> 1.0D - Math.sqrt(1.0D - Math.pow(value, 2.0D)));
     CIRC_OUT = (value -> Math.sqrt(1.0D - Math.pow(value - 1.0D, 2.0D)));
     CIRC_IN_OUT = (value -> (value < 0.5D) ? ((1.0D - Math.sqrt(1.0D - Math.pow(2.0D * value, 2.0D))) / 2.0D) : ((Math.sqrt(1.0D - Math.pow(-2.0D * value + 2.0D, 2.0D)) + 1.0D) / 2.0D));
     ELASTIC_IN = (value -> (value != 0.0D && value != 1.0D) ? (Math.pow(-2.0D, 10.0D * value - 10.0D) * Math.sin((value * 10.0D - 10.75D) * 2.0943951023931953D)) : value);
     ELASTIC_OUT = (value -> (value != 0.0D && value != 1.0D) ? (Math.pow(2.0D, -10.0D * value) * Math.sin((value * 10.0D - 0.75D) * 2.0943951023931953D) + 1.0D) : value);
     ELASTIC_IN_OUT = (value -> 
       (value != 0.0D && value != 1.0D) ? ((value < 0.5D) ? (-(Math.pow(2.0D, 20.0D * value - 10.0D) * Math.sin((20.0D * value - 11.125D) * 1.3962634015954636D)) / 2.0D) : (Math.pow(2.0D, -20.0D * value + 10.0D) * Math.sin((20.0D * value - 11.125D) * 1.3962634015954636D) / 2.0D + 1.0D)) : value);
 
 
 
 
     
     EXPO_IN = (value -> (value != 0.0D) ? Math.pow(2.0D, 10.0D * value - 10.0D) : value);
     EXPO_OUT = (value -> (value != 1.0D) ? (1.0D - Math.pow(2.0D, -10.0D * value)) : value);
     EXPO_IN_OUT = (value -> 
       (value != 0.0D && value != 1.0D) ? ((value < 0.5D) ? (Math.pow(2.0D, 20.0D * value - 10.0D) / 2.0D) : ((2.0D - Math.pow(2.0D, -20.0D * value + 10.0D)) / 2.0D)) : value);
 
 
 
 
     
     BACK_IN = (value -> 2.70158D * Math.pow(value, 3.0D) - 1.70158D * Math.pow(value, 2.0D));
     BACK_OUT = (value -> 1.0D + 2.70158D * Math.pow(value - 1.0D, 3.0D) + 1.70158D * Math.pow(value - 1.0D, 2.0D));
     BACK_IN_OUT = (value -> (value < 0.5D) ? (Math.pow(2.0D * value, 2.0D) * (7.189819D * value - 2.5949095D) / 2.0D) : ((Math.pow(2.0D * value - 2.0D, 2.0D) * (3.5949095D * (value * 2.0D - 2.0D) + 2.5949095D) + 2.0D) / 2.0D));
     BOUNCE_OUT = (x -> {
         double n1 = 7.5625D;
 
         
         double d1 = 2.75D;
 
         
         return (x < 1.0D / d1) ? (n1 * Math.pow(x, 2.0D)) : ((x < 2.0D / d1) ? (n1 * Math.pow(x - 1.5D / d1, 2.0D) + 0.75D) : ((x < 2.5D / d1) ? (n1 * Math.pow(x - 2.25D / d1, 2.0D) + 0.9375D) : (n1 * Math.pow(x - 2.625D / d1, 2.0D) + 0.984375D)));
       });
 
     
     BOUNCE_IN = (value -> 1.0D - BOUNCE_OUT.ease(1.0D - value));
     BOUNCE_IN_OUT = (value -> (value < 0.5D) ? ((1.0D - BOUNCE_OUT.ease(1.0D - 2.0D * value)) / 2.0D) : ((1.0D + BOUNCE_OUT.ease(2.0D * value - 1.0D)) / 2.0D));
   }
   public static final Easing EXPO_IN; public static final Easing EXPO_OUT; public static final Easing EXPO_IN_OUT; public static final Easing BACK_IN; public static final Easing BACK_OUT; public static final Easing BACK_IN_OUT; public static final Easing BOUNCE_OUT; public static final Easing BOUNCE_IN; public static final Easing BOUNCE_IN_OUT;
   public static Easing powIn(double n) {
     return value -> Math.pow(value, n);
   }
   
   public static Easing powIn(int n) {
     return powIn(n);
   }
   
   public static Easing powOut(double n) {
     return value -> 1.0D - Math.pow(1.0D - value, n);
   }
   
   public static Easing powOut(int n) {
     return powOut(n);
   }
   
   public static Easing powIN_OUT(double n) {
     return value -> (value < 0.5D) ? (Math.pow(2.0D, n - 1.0D) * Math.pow(value, n)) : (1.0D - Math.pow(-2.0D * value + 2.0D, n) / 2.0D);
   } }

