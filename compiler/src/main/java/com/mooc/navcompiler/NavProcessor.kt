package com.mooc.navcompiler

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.google.auto.service.AutoService
import com.mooc.annotation.Destination
import com.mooc.annotation.Destination.Activity
import com.mooc.annotation.Destination.Fragment
import java.io.File
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import javax.tools.JavaFileManager
import javax.tools.StandardLocation
import kotlin.math.absoluteValue
import kotlin.reflect.KClass

@SupportedAnnotationTypes(
    "com.mooc.annotation.Destination.Activity",
    "com.mooc.annotation.Destination.Fragment"
)
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class NavProcessor : AbstractProcessor() {

    private lateinit var filter: Filer
    private lateinit var messager: Messager

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        filter = processingEnv.filer
        messager = processingEnv.messager
    }


    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        val activityElements = roundEnv.getElementsAnnotatedWith(Activity::class.java)
        val fragmentElements = roundEnv.getElementsAnnotatedWith(Fragment::class.java)
        if (activityElements.isEmpty() && fragmentElements.isEmpty()) {
            return false
        }

        val destMap = HashMap<String, JSONObject>()
        handleAnnotationElements(activityElements, Activity::class, destMap)
        handleAnnotationElements(fragmentElements, Fragment::class, destMap)

        if (destMap.isEmpty()) {
            return false
        }
        val content = JSON.toJSONString(destMap)
        messager.printMessage(Diagnostic.Kind.NOTE, "content: $content")
        val resourcePath =
            filter.getResource(StandardLocation.CLASS_OUTPUT, "", "destination.json").name
        // app/src/main/assets
        val assetPath =
            "${resourcePath.substringBefore("app")}${File.separator}app${File.separator}src${File.separator}main${File.separator}assets"

        var result = true
        val dir = File(assetPath)
        if (!dir.exists()) {
            result = dir.mkdirs()
        }
        if (!result) {
            messager.printMessage(Diagnostic.Kind.ERROR, "无法创建目录：${dir.absolutePath}")
            return false
        }

        val file = File(dir, "destination.json")
        if (file.exists()) {
            result = file.delete()
        }
        if (!result) {
            messager.printMessage(Diagnostic.Kind.ERROR, "无法删除文件：${file.absoluteFile}")
            return false
        }

        result = file.createNewFile()
        if (!result) {
            messager.printMessage(Diagnostic.Kind.ERROR, "无法创建文件：${file.absoluteFile}")
            return false
        }
        file.writeText(content)
        return true
    }

    private fun handleAnnotationElements(
        elements: Set<Element>,
        clazz: KClass<out Annotation>,
        hashMap: HashMap<String, JSONObject>
    ) {
        elements.map { it as TypeElement }.forEach { element ->
            val annotation = element.getAnnotation(clazz.java)
            val name = element.qualifiedName.toString()
            val id = element.hashCode().absoluteValue
            val pageUrl: String
            val isNeedLogin: Boolean
            val asStarter: Boolean
            val isFragment: Boolean

            if (annotation is Activity) {
                pageUrl = annotation.pageUrl
                isNeedLogin = annotation.isNeedLogin
                asStarter = annotation.asStarter
                isFragment = false
            } else {
                val fragment = annotation as Fragment
                pageUrl = fragment.pageUrl
                isNeedLogin = fragment.isNeedLogin
                asStarter = fragment.asStarter
                isFragment = true
            }

            val jsonObject = JSONObject()
            jsonObject["id"] = id
            jsonObject["name"] = name
            jsonObject["pageUrl"] = pageUrl
            jsonObject["isNeedLogin"] = isNeedLogin
            jsonObject["asStarter"] = asStarter
            jsonObject["isFragment"] = isFragment

            if (hashMap.containsKey(pageUrl)) {
                messager.printMessage(Diagnostic.Kind.ERROR, "pageUrl不能相同")
                return@forEach
            }

            hashMap[pageUrl] = jsonObject
        }
    }

}