package com.robolang;

import org.antlr.runtime.tree.Tree;


public class Walker {
    private Tree root;
    private String className;

    public Walker(Tree t, String className) {
        this.root = t;
        this.className = className;
        assert root.getText().equals("LIST_INSTR");
    }

    public String getCode() {
        String tmp = "public class %s {";
        tmp += "public static void main(String[] args) {";
        tmp += writeMain();
        tmp += "}";
        tmp += writeFunctions();
        tmp += "}";
        return String.format(tmp, className);
    }

    private String writeMain() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < root.getChildCount(); i++) {
            Tree child = root.getChild(i);
            if (!child.getText().equals("FUNCTION"))
                sb.append(getNodeCode(child));
        }
        return sb.toString();
    }

    private String writeFunctions() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < root.getChildCount(); i++) {
            Tree child = root.getChild(i);
            if (child.getText().equals("FUNCTION")) {
                String tmp = "private %s %s (%s) {";
                tmp += getNodeCode(child.getChild(2));
                tmp += "}";
                sb.append(String.format(tmp, getRetType(child), child.getChild(0), getParams(child.getChild(1))));
            }
        }
        return sb.toString();
    }

    private String getNodeCode(Tree t) {
        // TODO: Returns the code generated by a subtree
        return "";
    }

    private String getRetType(Tree t) {
        // TODO: Get return type of a function
        assert t.getText().equals("FUNCTION");
        return "";
    }

    private String getParams(Tree t) {
        // TODO: Get parameters of a function
        assert t.getText().equals("PARAMS");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < t.getChildCount(); i++) {

        }
        return sb.toString();
    }
}
