package com.robolang;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTreeAdaptor;
import org.antlr.runtime.tree.DOTTreeGenerator;
import org.antlr.runtime.tree.Tree;
import org.antlr.stringtemplate.StringTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

class Main {

    private static boolean makeDot = false;
    private static boolean compile = true;

    public static void main(String[] args) {
        if (args.length > 0) {
            int s = 0;
            if (args[0].startsWith("-dot")) {
                makeDot = true;
                s = 1;
            }
            if (args[s].startsWith("-nocompile")) {
                compile = false;
                s += 1;
            }
            parse(new File(args[s]));
        } else {
            System.err.println("Usage: java -jar robolang-1.0-jar-with-dependencies.jar <directory | filename.rl>");
        }
    }

    private static void parse(File source) {
        String sourceFile = source.getName();
        if (sourceFile.length() > 3) {
            String suffix = sourceFile.substring(sourceFile.length() - 3).toLowerCase();
            if (suffix.compareTo(".rl") == 0) {
                parseSource(source.getAbsolutePath());
            }
        }
    }

    private static void parseSource(String source) {
        try {
            TLexer lexer = new TLexer();
            lexer.setCharStream(new ANTLRFileStream(source, "UTF8"));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            TParser parser = new TParser(tokens);
            Tree t = (Tree) parser.prog().getTree();
            source = source.substring(0, source.length() - 2);

            if (makeDot && tokens.size() < 4096) {
                DOTTreeGenerator gen = new DOTTreeGenerator();
                String outputName = source + "dot";

                StringTemplate st = gen.toDOT(t, new CommonTreeAdaptor());

                FileWriter outputStream = new FileWriter(outputName);
                outputStream.write(st.toString());
                outputStream.close();

                Process proc = Runtime.getRuntime().exec("dot -Tpng -o" + source + "png " + outputName);
                proc.waitFor();
            }

            if (compile) {
                String outputName = source + "java";
                Compiler compiler = new Compiler(t, outputName);
                compiler.writeFile();
            }
        } catch (FileNotFoundException ex) {
            System.err.println("\n  !!The file " + source + " does not exist!!\n");
        } catch (RecognitionException ex) {
            System.err.println("The input file contains invalid Robolang code.");
        } catch (IOException ex) {
            System.err.println("Could not read the file.");
        } catch (InterruptedException ex) {
            System.err.println("Could not create dot file.");
        }
    }

}
