package com.alice.mel.engine;

import com.alice.mel.components.Component;
import com.alice.mel.components.ComponentType;
import com.alice.mel.utils.collections.Bits;
import com.alice.mel.utils.collections.ObjectMap;

public class Family {
    private static final ObjectMap<String, Family> families = new ObjectMap<>();
    private static int familyIndex = 0;
    private static final Builder builder = new Builder();
    private static final Bits zeroBits = new Bits();

    private final Bits all;
    private final Bits one;
    private final Bits exclude;
    private final int index;

    private Family (Bits all, Bits any, Bits exclude) {
        this.all = all;
        this.one = any;
        this.exclude = exclude;
        this.index = familyIndex++;
    }

    public int getIndex () {
        return this.index;
    }

    public boolean matches (Entity entity) {
        Bits entityComponentBits = entity.getComponentBits();

        if (!entityComponentBits.containsAll(all)) {
            return false;
        }

        if (!one.isEmpty() && !one.intersects(entityComponentBits)) {
            return false;
        }

        if (!exclude.isEmpty() && exclude.intersects(entityComponentBits)) {
            return false;
        }

        return true;
    }

    @SafeVarargs
    public static final Builder all (Class<? extends Component>... componentTypes) {
        return builder.reset().all(componentTypes);
    }

    @SafeVarargs
    public static final Builder one (Class<? extends Component>... componentTypes) {
        return builder.reset().one(componentTypes);
    }

    @SafeVarargs
    public static final Builder exclude (Class<? extends Component>... componentTypes) {
        return builder.reset().exclude(componentTypes);
    }

    public static class Builder {
        private Bits all = zeroBits;
        private Bits one = zeroBits;
        private Bits exclude = zeroBits;

        Builder() {

        }

        public Builder reset () {
            all = zeroBits;
            one = zeroBits;
            exclude = zeroBits;
            return this;
        }

        public final Builder all (Class<? extends Component>... componentTypes) {
            all = ComponentType.getBitsFor(componentTypes);
            return this;
        }

        public final Builder one (Class<? extends Component>... componentTypes) {
            one = ComponentType.getBitsFor(componentTypes);
            return this;
        }

        public final Builder exclude (Class<? extends Component>... componentTypes) {
            exclude = ComponentType.getBitsFor(componentTypes);
            return this;
        }

        public Family get () {
            String hash = getFamilyHash(all, one, exclude);
            Family family = families.get(hash, null);
            if (family == null) {
                family = new Family(all, one, exclude);
                families.put(hash, family);
            }
            return family;
        }
    }

    @Override
    public int hashCode () {
        return index;
    }

    @Override
    public boolean equals (Object obj) {
        return this == obj;
    }

    private static String getFamilyHash (Bits all, Bits one, Bits exclude) {
        StringBuilder stringBuilder = new StringBuilder();
        if (!all.isEmpty()) {
            stringBuilder.append("{all:").append(getBitsString(all)).append("}");
        }
        if (!one.isEmpty()) {
            stringBuilder.append("{one:").append(getBitsString(one)).append("}");
        }
        if (!exclude.isEmpty()) {
            stringBuilder.append("{exclude:").append(getBitsString(exclude)).append("}");
        }
        return stringBuilder.toString();
    }

    private static String getBitsString (Bits bits) {
        StringBuilder stringBuilder = new StringBuilder();

        int numBits = bits.length();
        for (int i = 0; i < numBits; ++i) {
            stringBuilder.append(bits.get(i) ? "1" : "0");
        }

        return stringBuilder.toString();
    }
}
