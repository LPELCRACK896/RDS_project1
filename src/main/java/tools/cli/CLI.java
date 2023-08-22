package tools.cli;

import java.util.ArrayList;
import java.util.HashMap;

public class CLI {
    private boolean run;
    private String name;
    private boolean isAction;

    private HashMap<String, Option> optionsMenu;

    public CLI (String name, HashMap<String, Option> optionsMenu){
        this.name = name;
        this.optionsMenu = optionsMenu;
    }



}
