package ee.taltech.iti0200.domain.entity;

import ee.taltech.iti0200.graphics.renderer.Renderer;
import ee.taltech.iti0200.physics.BoundingBox;

import static ee.taltech.iti0200.graphics.renderer.EntityRenderFacade.DEFAULT;

public class Equipment extends Entity {

    protected Living owner;
    protected boolean isActive;

    public Equipment(BoundingBox boundingBox) {
        super(0, boundingBox);
    }

//    public Equipment setOwner(Living owner) {
//        this.owner = owner;
//        this.boundingBox = owner.getBoundingBox();
//        return this;
//    }

    public Living getOwner() {
        return owner;
    }

    public Renderer getRenderer() {
        return renderers.get(DEFAULT);
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isActive() {
        return isActive;
    }

}
