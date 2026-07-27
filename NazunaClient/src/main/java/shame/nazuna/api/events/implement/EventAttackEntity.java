package shame.nazuna.api.events.implement;

import shame.nazuna.api.events.Event;

public class EventAttackEntity extends Event {
    private final PlayerEntity target;

    public EventAttackEntity(PlayerEntity target) {
        this.target = target;
    }

    public PlayerEntity getTarget() {
        return this.target;
    }
}
