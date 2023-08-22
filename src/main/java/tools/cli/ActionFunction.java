package tools.cli;

@FunctionalInterface
public interface ActionFunction<T> {
    T perform();
}