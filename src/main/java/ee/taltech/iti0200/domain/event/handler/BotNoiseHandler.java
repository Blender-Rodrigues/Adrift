package ee.taltech.iti0200.domain.event.handler;

import ee.taltech.iti0200.ai.Sensor;
import ee.taltech.iti0200.domain.entity.Bot;
import ee.taltech.iti0200.domain.event.Subscriber;
import ee.taltech.iti0200.domain.event.entity.GunShot;

public class BotNoiseHandler implements Subscriber<GunShot> {

    private Bot bot;

    public BotNoiseHandler(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void handle(GunShot event) {
        bot.getBrain().updateSensor(Sensor.AUDIO, event.getGun().getBoundingBox().getCentre(), event.getGun());
    }

}
