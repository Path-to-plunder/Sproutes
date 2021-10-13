package com.casadetasha.kexp.sproute.processor.annotatedloader

import com.casadetasha.kexp.sproute.processor.MemberNames.convertToMemberNames
import com.casadetasha.kexp.sproute.processor.ktx.getClassData
import com.casadetasha.kexp.sproute.processor.ktx.primaryConstructor
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.metadata.ImmutableKmClass
import com.squareup.kotlinpoet.metadata.ImmutableKmPackage
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.specs.ClassData
import javax.lang.model.element.Element

sealed class KotlinContainer(
    val element: Element,
    val packageName: String,
    val classSimpleName: String
) : Comparable<KotlinContainer> {

    val memberName = MemberName(packageName, classSimpleName)
    abstract val kotlinFunctions: Set<KotlinFunction>

    override fun compareTo(other: KotlinContainer): Int {
        return memberName.toString().compareTo(other.memberName.toString())
    }


    @OptIn(KotlinPoetMetadataPreview::class)
    class KotlinClass(
        element: Element,
        kmClass: ImmutableKmClass,
        private val functionMap: Map<String, Element>
    ) : KotlinContainer(
        element = element,
        packageName = element.getClassData().className.packageName,
        classSimpleName = element.getClassData().className.simpleName
    ) {

        val classData: ClassData by lazy { element.getClassData() }
        val primaryConstructorParams: List<MemberName>? by lazy {
            classData.primaryConstructor()
                ?.valueParameters
                ?.convertToMemberNames()
        }

        override val kotlinFunctions: Set<KotlinFunction> by lazy {
            classData.methods
                .filter { functionMap.containsKey(it.key.name) }
                .map { entry ->
                    KotlinFunction.KotlinClassMemberFunction(
                        packageName = packageName,
                        methodElement = functionMap[entry.key.name]!!,
                        function = entry.key
                    )
                }
                .toSortedSet()
        }
    }

    @OptIn(KotlinPoetMetadataPreview::class)
    class KotlinFileFacade(
        element: Element,
        val immutableKmPackage: ImmutableKmPackage,
        packageName: String,
        val fileName: String,
        functionMap: Map<String, Element>
    ) : KotlinContainer(
        element = element,
        packageName = packageName,
        classSimpleName = fileName
    ) {

        override val kotlinFunctions: Set<KotlinFunction> by lazy {
            immutableKmPackage.functions
                .filter { functionMap.containsKey(it.name) }
                .map {
                    val methodElement = functionMap[it.name]!!
                    KotlinFunction.KotlinTopLevelFunction(
                        packageName = packageName,
                        methodElement = methodElement,
                        function = it
                    )
                }.toSortedSet()
        }
    }
}