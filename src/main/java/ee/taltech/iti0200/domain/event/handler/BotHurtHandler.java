package ee.taltech.iti0200.domain.event.handler;

import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.entity.Living;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.DealDamage;

import static ee.taltech.iti0200.ai.Sensor.DAMAGE;

public class BotHurtHandler implements Subscriber<DealDamage> {

    private Bot bot;

    public BotHurtHandler(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void handle(DealDamage event) {
        if (!bot.getId().equals(event.getTarget().getId())) {
            return;
        }

        Living owner = event.getSource().getOwner();
        if (owner == null) {
            bot.getBrain().updateSensor(DAMAGE, bot.getBoundingBox().getCentre(), bot);
        } else {
            bot.getBrain().updateSensor(DAMAGE, owner.getBoundingBox().getCentre(), owner);
        }
    }

}
