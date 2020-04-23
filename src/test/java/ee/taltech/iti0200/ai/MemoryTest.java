package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.physics.Vector;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class MemoryTest {

    private Memory memory;

    @BeforeEach
    void setUp() {
        memory = new Memory();
    }

    @Test
    void getTargets() {
        assertThat(memory.getTargets()).isEmpty();
    }

    @Test
    void setTargets() {
        ArrayList<ImmutablePair<Vector, TargetType>> targets = new ArrayList<>();
        ImmutablePair<Vector, TargetType> target = new ImmutablePair<>(new Vector(5, 0), TargetType.GUNSHOT);
        targets.add(target);
        memory.setTargets(targets);
        assertThat(memory.getTargets()).containsExactlyInAnyOrder(target);
    }

    @Test
    void addTarget() {
        Vector targetLocation = new Vector(5, 0);
        TargetType targetType = TargetType.GUNSHOT;
        ImmutablePair<Vector, TargetType> target = new ImmutablePair<>(targetLocation, targetType);
        memory.addTarget(targetLocation, targetType);
        assertThat(memory.getTargets()).containsExactlyInAnyOrder(target);
    }
}