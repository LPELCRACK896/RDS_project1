package tools.cli;
import tools.cli.ActionFunction;

public class Action <N> {

    private final ActionFunction<N> actionFunction;  // Action as a function/method as an attribute

    public Action(ActionFunction<N> actionFunction) {
        this.actionFunction = actionFunction;
    }
    public N act(){ // Call the method
        return actionFunction.perform();
    }


}
