package com.machnetinc.annotation.processor;


import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.StandardLocation;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


@AutoService(Processor.class)
public class CSVProcessor extends AbstractProcessor {

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
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(CSVToObject.class)) {
                if (annotatedElement.getKind() != ElementKind.CLASS) {
                    throw new ProcessingException(annotatedElement, "Only classes can be annotated with @%s",
                                                  CSVToObject.class.getSimpleName());
                }
                TypeElement typeElement = (TypeElement) annotatedElement;
                CSVToObject annotation = annotatedElement.getAnnotation(CSVToObject.class);
                String csvFileName = annotation.value();
                final Path doesntmatter =
                        Path.of(processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, "", "doesntmatter")
                                             .toUri())
                            .getParent()
                            .getParent()
                            .getParent()
                            .getParent()
                            .getParent();
                csvFileName = Path.of(doesntmatter.toString(), "src", "main", "resources", csvFileName).toString();

                String className = new File(csvFileName).getName().replaceAll("\\.csv", "");


                boolean headerRead = false;
                List<String> fileds = new ArrayList<>();

                final CodeBlock.Builder builder = CodeBlock.builder();
                for (String line : Files.readAllLines(Paths.get(csvFileName))) {
                    if (!headerRead) {
                        String[] headers = line.split(",");
                        for (String header : headers) {
                            fileds.add(header.trim());
                        }

                        headerRead = true;
                    } else {
                        String[] data = line.split(",");
                        builder.addStatement("result.add(new JournalDto($S, $S))", data[0], data[1]);
                    }

                }

                final TypeElement journal = processingEnv.getElementUtils().getTypeElement("com.machnetinc.impl.JournalDto");
                final TypeElement requestClass =
                        processingEnv.getElementUtils().getTypeElement("com.machnetinc.impl.TransactionRequest");

                final CodeBlock body = CodeBlock.builder()
                                                .addStatement("var result = new $T<$T>()", ArrayList.class, journal.asType())
                                                .build();

                ClassName cls = ClassName.get("com.machnetinc.impl", "JournalDto");
                ClassName list = ClassName.get("java.util", "List");

                final TypeName returnType = ParameterizedTypeName.get(list, cls);

                TypeSpec classSpec = TypeSpec.classBuilder(className.toUpperCase())
                                             .addField(String.class, "message", Modifier.PUBLIC)
                                             .addMethod(
                                                     MethodSpec.methodBuilder("create")
                                                               .addModifiers(Modifier.PUBLIC)
                                                               .addParameter(TypeName.get(requestClass.asType()), "request")
                                                               .addCode(body)
                                                               .addCode(builder.build())
                                                               .returns(returnType)
                                                               .addStatement("return result")
                                                               .build()
                                                       )
                                             .build();

                Filer filer = processingEnv.getFiler();
                JavaFile javaFile =
                        JavaFile.builder(processingEnv.getElementUtils().getPackageOf(typeElement).toString(), classSpec)
                                .build();
                javaFile.writeTo(filer);

            }

        } catch (ProcessingException e) {
            error(e.getElement(), e.getMessage());
        } catch (IOException e) {
            error(null, e.getMessage());
        }

        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<String>();
        annotations.add(CSVToObject.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    public void error(Element e, String msg) {
        messager.printMessage(Diagnostic.Kind.ERROR, msg, e);
    }
}
