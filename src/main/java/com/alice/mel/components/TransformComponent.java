package com.alice.mel.components;

import com.alice.mel.utils.collections.Array;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TransformComponent extends Component{

    public TransformComponent parent = null;
    public final Array<TransformComponent> children = new Array<>();
    public final Vector3f position = new Vector3f();
    public final Quaternionf rotation = new Quaternionf();
    public final Vector3f scale = new Vector3f(1,1,1);

    private TransformComponent lastParent = null;
    private final Vector3f lastPosition = new Vector3f();
    private final Quaternionf lastRotation = new Quaternionf();
    private final Vector3f lastScale = new Vector3f(1,1,1);


    @Override
    public boolean isDirty() {
        return (lastParent != parent) || !lastPosition.equals(position) || !lastRotation.equals(rotation) || !lastScale.equals(scale);
    }

    @Override
    public void doClean() {
        lastParent = parent;
        lastPosition.set(position);
        lastRotation.set(rotation);
        lastScale.set(scale);
    }
}
