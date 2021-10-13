package com.casadetasha.kexp.sproute.processor.annotatedloader

import com.casadetasha.kexp.sproute.processor.MemberNames.convertToMemberNames
import com.casadetasha.kexp.sproute.processor.ktx.toMemberName
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.metadata.ImmutableKmFunction
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import kotlinx.metadata.KmClassifier
import javax.lang.model.element.Element

@OptIn(KotlinPoetMetadataPreview::class)
sealed class KotlinFunction(
    val packageName: String,
    val methodElement: Element,
    val function: ImmutableKmFunction,
) : Comparable<KotlinFunction> {

    val simpleName: String = function.name
    abstract val memberName: MemberName
    val parameters: List<MemberName> = function.valueParameters.convertToMemberNames()
    val receiver: MemberName? by lazy {
        val receiverType = function.receiverParameterType
        if (receiverType == null) null
        else when (receiverType.classifier) {
            is KmClassifier.Class -> receiverType.toMemberName()
            else -> throw IllegalStateException(
                "Unable to generate $memberName method, extension parameter must be a class."
            )
        }
    }

    override fun compareTo(other: KotlinFunction): Int {
        return this.memberName.toString().compareTo(other.memberName.toString())
    }

    class KotlinTopLevelFunction(
        packageName: String,
        methodElement: Element,
        function: ImmutableKmFunction,
    ) : KotlinFunction(
        packageName = packageName,
        methodElement = methodElement,
        function = function
    ) {
        override val memberName: MemberName = MemberName(packageName, simpleName)
    }


    class KotlinClassMemberFunction(
        packageName: String,
        methodElement: Element,
        function: ImmutableKmFunction,
    ) : KotlinFunction(
        packageName = packageName,
        methodElement = methodElement,
        function = function
    ) {
        override val memberName: MemberName = MemberName(packageName, simpleName)
    }
}
