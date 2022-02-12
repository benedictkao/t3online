package com.benkao.processor

import com.benkao.annotations.CreateToDestroy
import com.benkao.annotations.InitToClear
import com.benkao.annotations.LifecycleViewModel
import com.benkao.annotations.StartToStop
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.reactivex.rxjava3.core.Completable
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
class Generator: AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    private var kaptKotlinGeneratedDir: String? = null

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(
            LifecycleViewModel::class.java.name,
            InitToClear::class.java.name,
            CreateToDestroy::class.java.name,
            StartToStop::class.java.name
        )
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {
        kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]

        roundEnv.getElementsAnnotatedWith(LifecycleViewModel::class.java)
            .forEach {
                if (it.kind != ElementKind.CLASS) {
                    printErrorMessage("Only classes can be annotated with @LifecycleViewModel")
                    return true
                }
                processLifecycleViewModel(it)
            }
        return false
    }

    private fun processLifecycleViewModel(viewModel: Element) {
        val className = viewModel.simpleName.toString()
        val pack = processingEnv.elementUtils.getPackageOf(viewModel).toString()

        val fileName = "${className}_StreamContainer"
        val fileBuilder = FileSpec.builder(pack, fileName)
        val classBuilder = TypeSpec
            .classBuilder(fileName)
//            .addSuperinterface()
            .primaryConstructor(getConstructor())
            .addProperty(
                PropertySpec.builder(
                    "createToDestroyStreams",
                    List::class.parameterizedBy(Completable::class),
                    KModifier.PRIVATE
                ).initializer("createToDestroyStreams").build())
            .addProperty(
                PropertySpec.builder(
                    "startToStopStreams",
                    List::class.parameterizedBy(Completable::class),
                    KModifier.PRIVATE
                ).initializer("startToStopStreams").build()
            )

        val vm =
            ParameterSpec.builder(
                name = "viewModel",
                type = viewModel.asType().asTypeName()
            ).build()

        val initToClearStringBuilder = StringBuilder("return listOf(")

        viewModel.enclosedElements.forEach { enclosed ->

            enclosed.getAnnotation(InitToClear::class.java)
                ?.takeIf { isCompletableMethod(enclosed) }
                ?.let {
//                    initToClearStringBuilder.append(
//                        "    viewModel.${enclosed.simpleName}(),"
//                    )
                }

            enclosed.getAnnotation(CreateToDestroy::class.java)
                ?.takeIf { isCompletableMethod(enclosed) }
                ?.let {

                }

            enclosed.getAnnotation(StartToStop::class.java)
                ?.takeIf { isCompletableMethod(enclosed) }
                ?.let {

                }
        }

        initToClearStringBuilder.append(")")

        classBuilder.addFunction(
            FunSpec.builder("getCreateToDestroyStreams")
                .returns(List::class.parameterizedBy(Completable::class))
                .addStatement(initToClearStringBuilder.toString())
                .addModifiers(KModifier.OVERRIDE)
                .build()
        )

        classBuilder.addFunction(
            FunSpec.builder("getStartToStopStreams")
                .returns(List::class.parameterizedBy(Completable::class))
                .addStatement(initToClearStringBuilder.toString())
                .addModifiers(KModifier.OVERRIDE)
                .build()
        )

        val file = fileBuilder.addType(classBuilder.build()).build()
        val kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
        kaptKotlinGeneratedDir?.let { file.writeTo(File(it)) }
            ?: printErrorMessage("Could not generate directory")
    }

    private fun getConstructor() =
        FunSpec.constructorBuilder()
            .addModifiers(KModifier.PRIVATE)
            .addParameter(
                ParameterSpec.builder(
                    "createToDestroyStreams",
                    List::class.parameterizedBy(Completable::class)
                ).build()
            )
            .addParameter(
                ParameterSpec.builder(
                    "startToStopStreams",
                    List::class.parameterizedBy(Completable::class)
                ).build()
            )
            .build()

    private fun isCompletableMethod(element: Element): Boolean =
        element.kind == ElementKind.METHOD &&
                (element as ExecutableElement)
                    .returnType.toString() == Completable::class.qualifiedName

    private fun printErrorMessage(msg: String) {
        processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, msg)
    }
}