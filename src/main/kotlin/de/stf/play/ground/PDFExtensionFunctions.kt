package de.stf.play.ground

import com.lowagie.text.Element
import com.lowagie.text.Font
import com.lowagie.text.FontFactory
import com.lowagie.text.Phrase
import com.lowagie.text.alignment.HorizontalAlignment
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import de.stf.play.ground.PDFExtensionFunctions.FONT_NORMAL
import de.stf.play.ground.PDFExtensionFunctions.createCell
import de.stf.play.ground.data.AbstractType
import de.stf.play.ground.data.Type1
import de.stf.play.ground.data.TypeInterface
import java.awt.Color
import kotlin.reflect.full.isSubclassOf

object PDFExtensionFunctions {
    val FONT_CODE: Font = FontFactory.getFont(FontFactory.COURIER, "UTF-8", 9f)
    val FONT_NORMAL: Font = FontFactory.getFont(FontFactory.HELVETICA, "UTF-8", 10f)
    val FONT_BOLD: Font = FontFactory.getFont(FontFactory.HELVETICA, "UTF-8", 10f, Font.BOLD)
    val FONT_TEXT: Font = FontFactory.getFont("Arial", "UTF-8", 10f, Font.NORMAL)
    val FONT_TEXTSMALL: Font = FontFactory.getFont("Arial", "UTF-8", 7f, Font.NORMAL)
    val FONT_TEXTBOLD: Font = FontFactory.getFont("Arial", "UTF-8", 10f, Font.BOLD)
    val FONT_ITALIC: Font = FontFactory.getFont(FontFactory.HELVETICA, "UTF-8", 10f, Font.ITALIC)
    val FONT_HEADER: Font = FontFactory.getFont(FontFactory.HELVETICA, "UTF-8", 12f, Font.BOLD)
    val COLOR_EVEN: Color = Color(246,245,244)
    val COLOR_ODD: Color = Color(222,221,220)

    // ----------------------------------------------------------------------------------------------
    // Helper functions
    // -----------------------------------------------------------------------------------------------
    fun createCell(text: String,
        hAlign: Int = HorizontalAlignment.LEFT.id,
        font: Font = FONT_NORMAL,
        color: Color = Color.BLACK,
        bgColor: Color = Color.WHITE): PdfPCell {
        val pFont = Font(font)
        pFont.color = color
        val phrase = Phrase(text, pFont)
        val cell = PdfPCell(phrase).defaultPadding()
        cell.horizontalAlignment = hAlign
        cell.backgroundColor = bgColor
        return cell
    }
}

// -----------------------------------------------------------------------------------------------
// Extension functions
// -----------------------------------------------------------------------------------------------
internal fun PdfPTable.addTextCell(text: String,
    hAlign: Int = Element.ALIGN_LEFT,
    font: Font = FONT_NORMAL,
    color: Color = Color.BLACK,
    bgColor: Color = Color.WHITE): PdfPTable = also {
        addCell(createCell(text, hAlign, font, color, bgColor))
    }


internal fun PdfPCell.defaultPadding(): PdfPCell = apply {
    paddingTop = 1f
    paddingBottom = 5f
    paddingLeft = 5f
    paddingRight = 5f
}

inline fun <reified T> PdfPTable.testRefied() =
    when {
        T::class == Type1::class -> {
            println("exact type ${T::class}, ${T::class.java}, " +
                "typeparams=${T::class.typeParameters}")
            }

        T::class.isSubclassOf(AbstractType::class) ->
            println("subclass of AbstractType ${T::class}, ${T::class.java}, " +
                "typeparams=${T::class.typeParameters}")

        T::class.isSubclassOf(TypeInterface::class) ->
            println("implements TypeInterface ${T::class}, ${T::class.java}, " +
                "typeparams=${T::class.typeParameters}")

        T::class.isSubclassOf(Collection::class) ->
            println("collection type $${T::class}, ${T::class.java}, " +
                "typeparams=[${T::class.typeParameters[0].variance}, " +
                "${T::class.typeParameters[0].name}, " +
                "${T::class.typeParameters[0].isReified}, " +
                "${T::class.typeParameters[0].upperBounds}]")

        else ->
            println("default type ${T::class}, ${T::class.java}, ${T::class.qualifiedName}")
    }
