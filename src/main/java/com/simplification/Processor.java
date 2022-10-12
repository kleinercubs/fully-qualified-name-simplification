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
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Processor {
    private static final String FILE_PATH = "src/main/resources/Blabla.java";

    private static void processClassOrInterfaceNode(ClassOrInterfaceType n) {
        if (n.findAncestor(ClassOrInterfaceType.class).toString().contains("Optional")) {
            for (Node node : n.getChildNodesByType(ClassOrInterfaceType.class)) {
                if (n.getTypeArguments().toString().contains(node.toString())) {
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

    private static void processMethodCallNode(MethodCallExpr n) {
        // System.out.println(n);
        // System.out.println(n.getChildNodes());
        int count = 0;
        SimpleName field = new SimpleName("...");
        for (MethodCallExpr node : n.getChildNodesByType(MethodCallExpr.class)) {
            count += 1;
            if (count == 1) field = node.getName();
//            System.out.println(node + " : "  + node.getClass().getName());
            n.remove(node);
            break;
        }

        SimpleName previous = n.getName();
        if (field.asString() != "...") {
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

    private static void old_main(String[] args) {
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


    // 读入一个完整的java文件的字符串, 返回简化后的字符串
    private static String simplify_str(String code) {
        TypeSolver typeSolver = new ReflectionTypeSolver();

        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(typeSolver);
        StaticJavaParser
                .getConfiguration()
                .setSymbolResolver(symbolSolver);

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

        return cu.toString();
    }



    private static void new_main(String[] args) throws FileNotFoundException {
        // 从json中读取已经可以被Java Parser处理成compilation unit的字符串
        // 调用上面的方法，处理

        Gson gson = new Gson();
        // read jsonInput4JavaParser/Testing.json

        Map<String, LinkedTreeMap> jsonInput = gson.fromJson(new FileReader("jsonInput4JavaParser/Testing.json"), Map.class);
        int cnt = 0;
        for (String key : jsonInput.keySet()) {
//            System.out.println("Processing " + key);

            if (cnt > 1000) break;
            try {
                LinkedTreeMap<String, String> value = jsonInput.get(key);
//                System.out.println("key: " + key);

                String simplified_assertion = simplify_str(value.get("assertion"));
//                System.out.println("simplified assertion: " + simplified_assertion);
                String simplified_testMethod = simplify_str(value.get("testMethod"));
//                System.out.println("simplified testMethod: " + simplified_testMethod);
                String simplified_focalMethod = simplify_str(value.get("focalMethod"));
//                System.out.println("simplified focalMethod: " + simplified_focalMethod);

            /* todo:
                1. 现在的simplify_str是昨天更新的版本. 可以把a.b.c.D.E处理成D.E, 不会丢弃. 但是和最早的版本相比. 会多留下来一些类似 nio. 这样的前缀
                2. 这里输出的simplified assertion, testMethod, focalMethod和atlas相比
                    1. assertion变化不大.
                    2. testMethod中, "<AssertPlaceHolder>;" 变成了  \/* "<AssertPlaceHolder>" ;*\/  <-- 这里又加了转义符号, 具体是什么样你看一下数据.
                    3. focalMethod中, func(Type, Type) 变成 func(Type var24678, Type var24678)
                    4. 除此之外, 就是前后加上了 "Class XYZ { void" 和 "}" 了.
             */
            }catch (Exception e){
                error_lines.add(cnt);
            }

            cnt += 1;

        }
        System.out.println(error_lines);
    }


    public static void main(String[] args) throws FileNotFoundException {
        new_main(args);
    }
    // assertEquals(a, b) --> class X{ void var24678(){ assertEquals(a, b); } }




}