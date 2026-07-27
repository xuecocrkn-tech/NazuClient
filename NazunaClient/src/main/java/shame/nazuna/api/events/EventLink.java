package shame.nazuna.api.events;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface EventLink {
  int priority() default 0;
}


/* Location:              C:\User\\user\Downloads\astra-1.0.0.jar!\shame\astra\api\events\EventLink.class
 * Java compiler version: 21 (65.0)
 * JD-Core Version:       1.1.3
 */