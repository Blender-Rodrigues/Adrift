package ee.taltech.iti0200.ai;

import ee.taltech.iti0200.physics.Vector;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;

public class Memory {

    private ArrayList<ImmutablePair<Vector, TargetType>> targets = new ArrayList<>();

    public ArrayList<ImmutablePair<Vector, TargetType>> getTargets() {
        return targets;
    }

    public void setTargets(ArrayList<ImmutablePair<Vector, TargetType>> targets) {
        this.targets = targets;
    }

    public void addTarget(Vector location, TargetType type) {
        targets.add(new ImmutablePair<>(location, type));
    }
}
