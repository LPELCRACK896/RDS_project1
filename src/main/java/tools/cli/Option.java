package tools.cli;

import tools.cli.Action;

public class Option<N> {
    private String name;
    private Action<N> action;

    public boolean selectOption(){
        try {
            action.act();
            return true;
        }
        catch (Exception e){
            return false;
        }

    }
    @Override
    public String toString() {
        return name;
    }
}
