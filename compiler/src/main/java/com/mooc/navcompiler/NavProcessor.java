package com.mooc.navcompiler;

import com.alibaba.fastjson.JSONObject;
import com.google.auto.service.AutoService;
import com.mooc.navannotation.ActivityDestination;
import com.mooc.navannotation.FragmentDestination;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

@SupportedAnnotationTypes({
        "com.mooc.navannotation.ActivityDestination",
        "com.mooc.navannotation.FragmentDestination"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class NavProcessor extends AbstractProcessor {
    private static final String FILE_NAME = "destination.json";

    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment environment) {
        super.init(environment);
        filer = environment.getFiler();
        messager = environment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment environment) {

        messager.printMessage(Diagnostic.Kind.NOTE, "nav process..........");
        Set<? extends Element> fragmentElements = environment.getElementsAnnotatedWith(FragmentDestination.class);
        Set<? extends Element> activityElements = environment.getElementsAnnotatedWith(ActivityDestination.class);

        Map<String, JSONObject> destMap = new HashMap<>();

        // 不为空才执行解析操作
        if (!fragmentElements.isEmpty()) {
            handleElements(fragmentElements, FragmentDestination.class, destMap);
        }

        if (!activityElements.isEmpty()) {
            handleElements(activityElements, ActivityDestination.class, destMap);
        }

        if (!destMap.isEmpty()) {
            // 将数据写入文件中
            FileOutputStream fos = null;
            BufferedOutputStream os = null;
            try {
                FileObject resource = filer.getResource(StandardLocation.CLASS_OUTPUT, "", FILE_NAME);
                String resourcePath = resource.getName();
                messager.printMessage(Diagnostic.Kind.NOTE, "resourcePath: " + resourcePath);

                // app/src/main/assets
                String suffix = "src" + File.separator + "main" + File.separator + "assets";
                String assetsPath = resourcePath.substring(0, resourcePath.indexOf("app") + 4) + suffix;
                messager.printMessage(Diagnostic.Kind.NOTE, "assetsPath: " + assetsPath);

                File assetsDir = new File(assetsPath);
                boolean mkdirs = true;
                if (!assetsDir.exists()) {
                    mkdirs = assetsDir.mkdirs();
                }

                if (!mkdirs) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "创建路径失败: " + assetsPath);
                    return true;
                }

                File assetsFile = new File(assetsPath, FILE_NAME);
                boolean delete = true;
                if (assetsFile.exists()) {
                    delete = assetsFile.delete();
                }

                if (!delete) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "无法删除该文件: " + assetsFile.getAbsolutePath());
                    return true;
                }
                boolean newFile = assetsFile.createNewFile();
                if (!newFile) {
                    messager.printMessage(Diagnostic.Kind.ERROR, "无法创建该文件: " + assetsFile.getAbsolutePath());
                    return true;
                }
                fos = new FileOutputStream(assetsFile);
                os = new BufferedOutputStream(fos);
                String content = JSONObject.toJSONString(destMap);
                os.write(content.getBytes());
                os.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeQuietly(os, fos);
            }
        }

        return true;
    }

    /**
     * 处理被标记的元素，并生成jsonObject对象
     */
    private void handleElements(Set<? extends Element> elements,
                                Class<? extends Annotation> annotationClass,
                                Map<String, JSONObject> destMap) {

        for (Element element : elements) {
            TypeElement typeElement = (TypeElement) element;

            String pageUrl;
            boolean isNeedLogin;
            boolean asStarter;
            boolean isFragment;
            int id = Math.abs(typeElement.hashCode());
            String name = typeElement.getQualifiedName().toString();

            Annotation annotation = element.getAnnotation(annotationClass);

            if (annotation instanceof ActivityDestination) {
                ActivityDestination destination = (ActivityDestination) annotation;
                pageUrl = destination.pageUrl();
                isNeedLogin = destination.isNeedLogin();
                asStarter = destination.asStarter();
                isFragment = false;
            } else {
                FragmentDestination destination = (FragmentDestination) annotation;
                pageUrl = destination.pageUrl();
                isNeedLogin = destination.isNeedLogin();
                asStarter = destination.asStarter();
                isFragment = true;
            }

            if (destMap.containsKey(pageUrl)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "不同的页面不能使用相同的pageUrl, class: " + name);
                return;
            }

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("name", name);
            jsonObject.put("pageUrl", pageUrl);
            jsonObject.put("isFragment", isFragment);
            jsonObject.put("isNeedLogin", isNeedLogin);
            jsonObject.put("asStarter", asStarter);

            destMap.put(pageUrl, jsonObject);
        }
    }

    private static void closeQuietly(@Nonnull Closeable... closeables) {

        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException ignore) {
                //e.printStackTrace();
            }
        }
    }
}
