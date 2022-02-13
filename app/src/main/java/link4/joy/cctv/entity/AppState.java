package link4.joy.cctv.entity;

public interface AppState<T> {
    T getCurrentState();

    void setCurrentState(T currentState);

    T getDesiredState();

    void setDesiredState(T desiredStatus);

    void adjustState();

    /**
     * @return {name} : {currentState}
     */
    String toString();
}
