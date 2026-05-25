package de.ericbrand.passportphotocreator.core.model

import android.util.SizeF
import de.ericbrand.passportphotocreator.feature.editor.Guides

const val InchesToMm = 25.4f

data class PhotoSpec(
    val sizeMm: SizeF,
    val faceHeightRangeMm: ClosedRange<Float>,
    val faceHeightToleranceRangeMm: ClosedRange<Float>? = null,
    val faceCentered: Boolean = false,
    val eyePositionYRangeMm: ClosedRange<Float>? = null,
    val nosePositionXRangeMm: ClosedRange<Float>? = null,
    val guideOverlays: Array<Guides>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PhotoSpec

        if (faceCentered != other.faceCentered) return false
        if (sizeMm != other.sizeMm) return false
        if (faceHeightRangeMm != other.faceHeightRangeMm) return false
        if (faceHeightToleranceRangeMm != other.faceHeightToleranceRangeMm) return false
        if (eyePositionYRangeMm != other.eyePositionYRangeMm) return false
        if (nosePositionXRangeMm != other.nosePositionXRangeMm) return false
        if (!guideOverlays.contentEquals(other.guideOverlays)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = faceCentered.hashCode()
        result = 31 * result + sizeMm.hashCode()
        result = 31 * result + faceHeightRangeMm.hashCode()
        result = 31 * result + (faceHeightToleranceRangeMm?.hashCode() ?: 0)
        result = 31 * result + (eyePositionYRangeMm?.hashCode() ?: 0)
        result = 31 * result + (nosePositionXRangeMm?.hashCode() ?: 0)
        result = 31 * result + guideOverlays.contentHashCode()
        return result
    }
}

val GermanyPassportSpec = PhotoSpec(
    sizeMm = SizeF(35f, 45f),
    faceHeightRangeMm = 32f..36f,
    faceHeightToleranceRangeMm = 28f..40f,
    eyePositionYRangeMm = 13.5f..21.5f,
    nosePositionXRangeMm =  16f..19f,
    guideOverlays = arrayOf(Guides.POSITION, Guides.FACE_HEIGHT)
)

val UsaPassportSpec = PhotoSpec(
    sizeMm = SizeF(2 * InchesToMm, 2 * InchesToMm),
    faceHeightRangeMm = 1 * InchesToMm..1.375f * InchesToMm,
    faceCentered = true,
    guideOverlays = arrayOf(Guides.COMBINED)
)