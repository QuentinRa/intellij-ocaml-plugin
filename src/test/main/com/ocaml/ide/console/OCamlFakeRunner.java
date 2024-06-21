package com.ocaml.ide.console;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.wm.ToolWindow;

import java.util.ArrayList;

/**
 * Save the commands in an ArrayList
 */
public class OCamlFakeRunner implements OCamlConsoleRunner {

    @Override public ToolWindow getWindow() { return null; }
    @Override public ProcessHandler getProcessHandler() { return null; }
    @Override public boolean isRunning() { return false; }
    @Override public void setRunning(boolean running) {}
    @Override public boolean isVariableViewEnabled() { return false; }
    @Override public void showVariableView(boolean show) {}
    @Override public void rebuildVariableView(String text) {}
    @Override public boolean isNotAbleToRun() { return false; }

    // save commands
    public ArrayList<String> commands = new ArrayList<>();

    @Override public void processCommand(String s) {
        commands.add(s);
    }
}
