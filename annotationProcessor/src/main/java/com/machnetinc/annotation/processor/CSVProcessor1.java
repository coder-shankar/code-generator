package com.machnetinc.annotation.processor;


import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@AutoService(Processor.class)
public class CSVProcessor1 extends AbstractProcessor {

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
//        messager.printMessage(Diagnostic.Kind.ERROR, "Error occured");
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(CSVToObject.class)) {
            CSVToObject annotation = element.getAnnotation(CSVToObject.class);
            String csvFileName = annotation.value();
            try {
                generateJavaClassFromCSV(csvFileName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

    @Override public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<String>();
        annotations.add(CSVToObject.class.getCanonicalName());
        return annotations;
    }

    @Override public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }


    private void generateJavaClassFromCSV(String csvFileName) throws IOException {
        try {
            final Path doesntmatter =
                    Path.of(processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", "doesntmatter")
                                         .toUri())
                        .getParent()
                        .getParent()
                        .getParent()
                        .getParent()
                        .getParent();
           csvFileName= Path.of(doesntmatter.toString(), "src", "main", "resources", csvFileName).toString();

            String className = new File(csvFileName).getName().replaceAll("\\.csv", "");
            List<String> fields = new ArrayList<>();
            List<String> fieldTypes = new ArrayList<>();
            boolean headerRead = false;
            for (String line : Files.readAllLines(Paths.get(csvFileName))) {
                if (!headerRead) {
                    String[] headers = line.split(",");
                    for (String header : headers) {
                        fields.add(header.trim());
                        fieldTypes.add("String"); // Assuming all fields are of type String
                    }
                    headerRead = true;
                } else {
                    // You can process each data line here if needed
                }
            }
            generateJavaClass(className, fields, fieldTypes);
        } catch (IOException e) {
            processingEnv.getMessager()
                         .printMessage(Diagnostic.Kind.ERROR, "Failed to generate Java class from CSV: " + e);
            throw new RuntimeException(e);
        }
    }

    private void generateJavaClass(String className, List<String> fields, List<String> fieldTypes) {
        try {
            JavaFileObject sourceFile = processingEnv.getFiler().createSourceFile(className);
            try (Writer writer = sourceFile.openWriter();
                 BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
                bufferedWriter.write("public class " + className + " {\n");
                for (int i = 0; i < fields.size(); i++) {
                    bufferedWriter.write("    private " + fieldTypes.get(i) + " " + fields.get(i) + ";\n");
                    // Add getter and setter methods if needed
                }
                bufferedWriter.write("}");
            }
        } catch (IOException e) {
            processingEnv.getMessager()
                         .printMessage(Diagnostic.Kind.ERROR, "Failed to generate Java source file: " + e.getMessage());
        }
    }
}
