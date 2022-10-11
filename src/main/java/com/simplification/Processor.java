package com.simplification;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.TreeVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.Log;
import com.github.javaparser.utils.SourceRoot;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.PackageDeclaration;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Processor {
    private static final String FILE_PATH = "src/main/resources/Blabla.java";

    private static void processClassOrInterfaceNode(ClassOrInterfaceType n){
        if (n.findAncestor(ClassOrInterfaceType.class).toString().contains("Optional")){
            for (Node node : n.getChildNodesByType(ClassOrInterfaceType.class)){
                if (n.getTypeArguments().toString().contains(node.toString())){
                    node.accept(new ModifierVisitor<Void>() {
                        @Override
                        public Visitable visit(ClassOrInterfaceType n, Void arg) {
                            processClassOrInterfaceNode(n);
                            return super.visit(n, arg);
                        }
                    }, null);
                    continue;
                }
                n.remove(node);
            }
        }
    }

    private static void processMethodCallNode(MethodCallExpr n){
        // System.out.println(n);
        // System.out.println(n.getChildNodes());
        int count = 0;
        SimpleName field = new SimpleName("...");
        for (MethodCallExpr node: n.getChildNodesByType(MethodCallExpr.class)){
            count += 1;
            if (count == 1) field = node.getName();
//            System.out.println(node + " : "  + node.getClass().getName());
            n.remove(node);
            break;
        }

        SimpleName previous = n.getName();
        if (field.asString() != "..."){
            // System.out.println(field);
            n.setName(field + "." + previous);
        }
    }

    // private static void processFieldAccessExprNode(Node n){
    //     System.out.println(n);
    //     System.out.println(n.getChildNodes());
    //     // n = n.getChildNodes().get(n.getChildNodes().size() - 1);
    //     FieldAccessExpr last_node = new FieldAccessExpr();
    //     for (FieldAccessExpr node: n.getChildNodesByType(FieldAccessExpr.class)){
    //         System.out.println("remove " + node + "            " + node.getClass().getName() + "            " + node.getTypeArguments());
    //         last_node = node;
    //         // n.remove(node);
    //     }
    //     System.out.println("==END==");
    //     System.out.println(n);
    //     System.out.println(n.getChildNodes());
    //     // System.out.println(last_node.getChildNodes());
    //     // n.setName(last_node.getName());

    //     // SimpleName previous = n.getName();
    //     // if (field.asString() != "..."){
    //     //     // System.out.println(field);
    //     //     n.setName(field + "." + previous);
    //     // }
    // }
    private static void processFieldAccessExprNode(FieldAccessExpr n) {
        Node scope = n.getScope();
        
        // System.out.println("Before FieldAccessExpr " + n + " " + scope.toString() + " " + scope.getClass().getName());
        if (scope instanceof FieldAccessExpr) {
            FieldAccessExpr _scope = (FieldAccessExpr) scope;
            n.setScope(new NameExpr(_scope.getName()));
        } else {
            // ???
        }

        // System.out.println(n + " || " + n.getParentNode() + " || " + n.findAncestor(FieldAccessExpr.class) + " || " + n.getScope() + " || " + n.getName());
        // if (n.findAncestor(FieldAccessExpr.class).toString() != n.getParentNode().toString()) {
        //     for (Node node : n.getChildNodesByType(FieldAccessExpr.class)) {
        //         System.out.println(node + " | " + n.getTypeArguments().toString());
        //         // if (n.getTypeArguments().toString().contains(node.toString())) {
        //         //     node.accept(new ModifierVisitor<Void>() {
        //         //         @Override
        //         //         public Visitable visit(FieldAccessExpr n, Void arg) {
        //         //             processFieldAccessExprNode(n);
        //         //             return super.visit(n, arg);
        //         //         }
        //         //     }, null);
        //         //     continue;
        //         // }
        //         System.out.println("  -remove  " + node);
        //         n.remove(node);
        //     }
        // }
        
        // System.out.println("==" + n + " || " + n.getParentNode() + " || " + n.findAncestor(FieldAccessExpr.class));
    }

    private static void old_main(String[] args){

        // JavaParser has a minimal logging class that normally logs nothing.
        // Let's ask it to write to standard out:
        // Log.setAdapter(new Log.StandardOutStandardErrorAdapter());

        // SourceRoot is a tool that read and writes Java files from packages on a certain root directory.
        // In this case the root directory is found by taking the root from the current Maven module,
        // with src/main/resources appended.

        TypeSolver typeSolver = new ReflectionTypeSolver();

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser
                .getConfiguration()
                .setSymbolResolver(symbolSolver);


        // Our sample is in the root of this directory, so no package name.
        // CompilationUnit cu = sourceRoot.parse("", "Blabla.java");
        // Log.info("Positivizing!");

        SourceRoot sourceRoot = new SourceRoot(CodeGenerationUtils.mavenModuleRoot(Processor.class).resolve("src/main/resources"));
        CompilationUnit cu = sourceRoot.parse("", "Code.java");
        // cu.accept(new ModifierVisitor<Void>() {
        //     /**
        //      * For every if-statement, see if it has a comparison using "!=".
        //      * Change it to "==" and switch the "then" and "else" statements around.
        //      */
        //     @Override
        //     public Visitable visit(VoidType n, Void arg) {
        //         System.out.println(n.toString());
        //         return super.visit(n, arg);
        //     }
        // }, null);

        // new TreeVisitor(){
        //     @Override
        //     public void process(Node node) {
        //         System.out.println(node.toString() + " --- " + node.getClass().getName());
        //     }
        // }.visitPreOrder(cu);

        cu.accept(new ModifierVisitor<Void>() {
            @Override
            public Visitable visit(ClassOrInterfaceType n, Void arg) {
                processClassOrInterfaceNode(n);
                return super.visit(n, arg);
            }
        }, null);

        // cu.accept(new ModifierVisitor<Void>() {
        //     @Override
        //     public Visitable visit(MethodCallExpr n, Void arg) {
        //         processMethodCallNode(n);
        //         return super.visit(n, arg);
        //     }
        // }, null);

        cu.accept(new ModifierVisitor<Void>() {
            @Override
            public Visitable visit(FieldAccessExpr n, Void arg) {
                processFieldAccessExprNode(n);
                return super.visit(n, arg);
            }
        }, null);
        // This saves all the files we just read to an output directory.
        sourceRoot.saveAll(
                // The path of the Maven module/project which contains the Processor class.
                CodeGenerationUtils.mavenModuleRoot(Processor.class)
                        // appended with a path to "output"
                        .resolve(Paths.get("output")));
    }

    // assertEquals(a, b) --> class X{ void var24678(){ assertEquals(a, b); } }
    private static String assertLine2cu(String assertLine){

        String result = "class X { void var24678() { "+assertLine+"; } }";
        return result;
    }

    // func(Type, Type){} --> class Y{ void func(Type a, Type b){} }
    private static  String testMethods2cu(String testMethod){
        String result = "class Y { void "+ add_parameter2cu(testMethod)+" }";
        return result;
    }

    // func(Type, Type) {}  -->  func(Type a, Type b) {}
    private static String add_parameter2cu(String testMethod){
        String signature = testMethod.substring(0, testMethod.indexOf("{"));
        String body = testMethod.substring(testMethod.indexOf("{"));
//        System.out.println(signature);



        String method_name = signature.substring(0, signature.indexOf("("));
        // get type in signature
        String[] types;
        try {
            types = signature.split("\\(")[1].split("\\)")[0].split(",");
        }catch (Exception e) {
//            System.out.println("No parameter in method");
            return testMethod;
        }
//        System.out.println(types);
        String new_signature = method_name + "(";
        for(int i = 0; i < types.length; i++){
            if(types[i].trim().equals("")){
                continue;
            }
            new_signature += types[i].trim() + " var24678";
            if (i!=types.length-1){
                new_signature += ", ";
            }
        }
        new_signature+=") ";
        String result = new_signature + body;

        return result;
    }
    private static boolean visit_one_pair(String[] code_pair, List<String> output_assertLines, List<String> output_testMethods){
        try{
            String assertLine = "";
            String testMethod = "";
            for(int i=0;i<2;i++){
                String code = code_pair[i];
                if(i==0){
                    code = assertLine2cu(code);
                }else if(i==1){
                    code = code.replaceAll("\"<AssertPlaceHolder>\"",code_pair[0]+";");
                    code = testMethods2cu(code);
                }
                CompilationUnit cu = StaticJavaParser.parse(code);

                cu.accept(new ModifierVisitor<Void>() {
                    @Override
                    public Visitable visit(ClassOrInterfaceType n, Void arg) {
                        processClassOrInterfaceNode(n);
                        return super.visit(n, arg);
                    }
                }, null);

                cu.accept(new ModifierVisitor<Void>() {
                    @Override
                    public Visitable visit(FieldAccessExpr n, Void arg) {
                        processFieldAccessExprNode(n);
                        return super.visit(n, arg);
                    }
                }, null);

                LexicalPreservingPrinter.setup(cu);
                if(i==0){
                    assertLine = LexicalPreservingPrinter.print(cu);
                }else if(i==1){
                    testMethod = LexicalPreservingPrinter.print(cu);
                }
            }

            if(!assertLine.equals("") && !testMethod.equals("")){
                output_assertLines.add(assertLine);
                output_testMethods.add(testMethod);
                return true;
            }else{
                return false;
            }

        }catch(Exception e){
            return false;
        }
    }

    private static void new_main(String[] args){

        // JavaParser has a minimal logging class that normally logs nothing.
        // Let's ask it to write to standard out:
        // Log.setAdapter(new Log.StandardOutStandardErrorAdapter());

        // SourceRoot is a tool that read and writes Java files from packages on a certain root directory.
        // In this case the root directory is found by taking the root from the current Maven module,
        // with src/main/resources appended.

        TypeSolver typeSolver = new ReflectionTypeSolver();

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser
                .getConfiguration()
                .setSymbolResolver(symbolSolver);


        List<String> output_assertLines = new ArrayList<>();
        List<String> output_testMethods = new ArrayList<>();

        String file1 = "input/Testing/assertLines.txt";
        String file1_output = "output/Testing/assertLines.txt";
        String file2 = "input/Testing/testMethods.txt";
        String file2_output = "output/Testing/testMethods.txt";

        File f1 = new File(file1);
        File f1_output = new File(file1_output);
        File f2 = new File(file2);
        File f2_output = new File(file2_output);

        // read lines in f1
        List<String> lines1 = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(f1));
            String line;
            while ((line = br.readLine()) != null) {
                lines1.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // read lines in f2
        List<String> lines2 = new ArrayList<>();
        try{
            BufferedReader br = new BufferedReader(new FileReader(f2));
            String line;
            while ((line = br.readLine()) != null) {
                lines2.add(line);
            }
            br.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        assert lines1.size()==lines2.size();
        for(int i=0;i<lines1.size();i++){
            String[] code_pair = new String[2];
            code_pair[0] = lines1.get(i);
            code_pair[1] = lines2.get(i);
            if(visit_one_pair(code_pair, output_assertLines, output_testMethods)){
                ;
            }else{
                System.out.println("Error in processing pair " + i);
            }
        }

        // write output_assertLines to file
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f1_output));
            for(String line: output_assertLines){
                bw.write(line);
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try{
            BufferedWriter bw = new BufferedWriter(new FileWriter(f2_output));
            for(String line: output_testMethods){
                bw.write(line);
                bw.newLine();
            }
            bw.close();
        }catch (IOException e){
            e.printStackTrace();
        }


    }

    private static void mytest1(String args[]){
//        String code = "func(){}";
        String[] codes = {"func(){}","func(Type){}","func(Type,    Type){}","func  (    Type ,   Type ) { }   ","  func  (    Type ,   Type   ,  Type) { }   "};
        for(String code :codes){
            System.out.println(add_parameter2cu(code));
        }
    }
    public static void main(String[] args) {
        new_main(args);
        /*
        忘记testMethods.txt里面应该分成 test method 和 focal method了
        所以现在跑出来结果不太对. 看起来好像完全没有简化成功.
         */
//        mytest1(args);
    }
}