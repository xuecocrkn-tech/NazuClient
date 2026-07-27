package shame.nazuna.api.events.implement;

import shame.nazuna.api.events.Event;

public class EventMove extends Event {
    private Vec3d movePos;

    public EventMove() {
    }

    public EventMove(Vec3d movePos) {
        this.movePos = movePos;
    }

    public Vec3d getMovePos() {
        return this.movePos;
    }
}
