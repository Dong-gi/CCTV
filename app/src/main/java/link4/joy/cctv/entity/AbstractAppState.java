package link4.joy.cctv.entity;

public abstract class AbstractAppState<T> implements AppState<T> {
    final String name;
    T currentState;
    T desiredState;

    public AbstractAppState(String name) {
        this.name = name;
    }

    @Override
    public T getCurrentState() {
        return currentState;
    }

    @Override
    public T getDesiredState() {
        return desiredState;
    }

    @Override
    public void setCurrentState(T currentState) {
        this.currentState = currentState;
        if (desiredState != null && currentState != desiredState)
            adjustState();
    }

    @Override
    public void setDesiredState(T desiredState) {
        this.desiredState = desiredState;
        if (currentState != desiredState)
            adjustState();
    }

    @Override
    public String toString() {
        return name + " >>> " + currentState;
    }
}
