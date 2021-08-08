package link4.joy.cctv.entity;

public interface AppState<T> {
    T getCurrentState();

    T getDesiredState();

    void setCurrentState(T currentState);

    void setDesiredState(T desiredStatus);

    void adjustState();

    /**
     * @return {name} : {currentState}
     */
    String toString();
}
