package de.stf.play.ground.test

import com.lowagie.text.Document
import com.lowagie.text.DocumentException
import com.lowagie.text.Element
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.Phrase
import com.lowagie.text.Rectangle
import com.lowagie.text.pdf.ColumnText
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfPageEventHelper
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.pdf.draw.LineSeparator
import de.stf.play.ground.PDFExtensionFunctions.COLOR_EVEN
import de.stf.play.ground.PDFExtensionFunctions.COLOR_ODD
import de.stf.play.ground.PDFExtensionFunctions.FONT_BOLD
import de.stf.play.ground.PDFExtensionFunctions.FONT_CODE
import de.stf.play.ground.PDFExtensionFunctions.FONT_HEADER
import de.stf.play.ground.PDFExtensionFunctions.FONT_ITALIC
import de.stf.play.ground.PDFExtensionFunctions.FONT_TEXT
import de.stf.play.ground.PDFExtensionFunctions.FONT_TEXTBOLD
import de.stf.play.ground.PDFExtensionFunctions.FONT_TEXTSMALL
import de.stf.play.ground.PDFExtensionFunctions.createCell
import de.stf.play.ground.addTextCell
import de.stf.play.ground.defaultPadding
import jdk.nashorn.tools.Shell.SUCCESS
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.awt.Color
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class OpenPDFTest {
    @Test
    //@DisplayName("This is my test")
    //@Tag("It is my tag")
    fun helloWorld() { // TestInfo testInfo) {
        val pdfFile = File("$BASEDIR/helloWorld.pdf")
        // step 1: creation of a document-object
        val document = Document()
        try {
            // step 2:
            // we create a writer that listens to the document
            // and directs a PDF-stream to a file
            PdfWriter.getInstance(document, FileOutputStream(pdfFile))
            // step 3: we open the document
            document.open()
            // step 4: we add a paragraph to the document
            document.add(Paragraph("Hello World"))
        } catch (de: DocumentException) {
            println(de.message)
        } catch (ioe: IOException) {
            println(ioe.message)
        }
        // step 5: we close the document
        document.close()
        Assert.assertTrue("### PDF file $pdfFile not created", pdfFile.exists())
    }

    @Test
    fun simpleLine() {
        executeTest("simpleLine") { document ->
            document.add(Paragraph("Hello World"))
        }
    }

    @Test
    fun simpleText() {
        executeTest("simpleText") { document ->
            with(document) {
                for (i: Int in 1..125) {
                    add(Paragraph("$i. Hello World"))
                    if (i % 30 == 0) {
                        newPage()
                    } else
                    if (i % 5 == 0) {
                        add(Paragraph(" "))
                        add(LineSeparator(0.2f, 85f, Color.BLUE, Element.ALIGN_CENTER, 3.5f))
                    }
                }
            }
        }
    }

    @Test
    fun marginTest() {
        executeTest("marginTest") { document ->
            with(document) {
                add(
                    Paragraph(
                        "The left margin of this document is 36pt (0.5 inch); the right margin 72pt " +
                            "(1 inch); the top margin 108pt (1.5 inch); the bottom margin 180pt (2.5 inch). "
                    )
                )
                val paragraph = Paragraph()
                paragraph.setAlignment(Element.ALIGN_JUSTIFIED)
                for (i in 0..19) {
                    paragraph.add(
                        "Hello World, Hello Sun, Hello Moon, Hello Stars, Hello Sea, Hello Land, " +
                            "Hello People. "
                    )
                }
                add(paragraph)
                setMargins(180F, 108F, 72F, 36F)
                add(Paragraph("Now we change the margins. You will see the effect on the next page."))
                add(paragraph)
                setMarginMirroring(true)
                add(Paragraph("Starting on the next page, the margins will be mirrored."))
                add(paragraph)
            }
        }
    }

    @Test
    fun simpleTable() {
        executeTest("simpleTable") { document ->
            val table = PdfPTable(4)
            // Headers and Footers
            with(
                createCell(
                    "Header -- Cell with colspan 4",
                    font = FONT_HEADER,
                    bgColor = Color.GRAY
                )
            ) {
                colspan = 4
                defaultPadding()
                horizontalAlignment = Element.ALIGN_CENTER
                table.addCell(this)
            }
            with(createCell("Cell with rowspan 2", bgColor = Color.LIGHT_GRAY)) {
                defaultPadding()
                rowspan = 2
                colspan = 2
                setLeading(25f, 0f)
                table.addCell(this)
            }
            with(table) {
                setWidths(intArrayOf(12, 50, 24, 24))
                addTextCell("R1, C1")
                addTextCell("R1, C2", Element.ALIGN_RIGHT)
                addTextCell("R2,\nC1")
                addTextCell("R2, C2", Element.ALIGN_CENTER)
                for(i in 1..2) {
                    addTextCell("Number", font = FONT_BOLD, bgColor = Color.LIGHT_GRAY)
                    addTextCell("Column 1", hAlign = Element.ALIGN_RIGHT, font = FONT_BOLD, bgColor = Color.LIGHT_GRAY)
                    addTextCell("Column 2", hAlign = Element.ALIGN_CENTER, font = FONT_BOLD, bgColor = Color.LIGHT_GRAY)
                    addTextCell("Column 3", hAlign = Element.ALIGN_LEFT, font = FONT_BOLD, bgColor = Color.LIGHT_GRAY)
                }
                headerRows = 5
                footerRows = 1
            }

            // Content
            for (i in 3..202) {
                val bg = if (i % 2 == 0) COLOR_EVEN else COLOR_ODD
                table
                    .addTextCell("${i - 2}.", font = FONT_ITALIC, bgColor = bg)
                    .addTextCell("R$i, C1", hAlign = Element.ALIGN_RIGHT, font = FONT_TEXT, bgColor = bg)
                    .addTextCell("R$i, C2", hAlign = Element.ALIGN_CENTER, font = FONT_TEXTBOLD, bgColor = bg)
                    .addTextCell("R$i, C3", hAlign = Element.ALIGN_LEFT, font = FONT_CODE, bgColor = bg)
            }
            document.add(table)
        }
    }

    // -----------------------------------------------------------------------------------------------
    // Testcase executor
    // -----------------------------------------------------------------------------------------------
    private fun executeTest(testName: String, pdfCreator: (document: Document) -> Unit) {
        val document = Document() // step 1: creation of a document-object
        with(document) {
            pageSize = PageSize.A4
            left(16f)
            setMargins(0f, 0f, 36f, 36f)
        }
        val pdfFile = File("$BASEDIR/$testName.pdf")
        try {
            // step 2:
            // we create a writer that listens to the document
            // and directs a PDF-stream to a file
            val writer = PdfWriter.getInstance(document, FileOutputStream(pdfFile))
            writer.setBoxSize("art", Rectangle(50f, 50f, 545f, 792f))
            writer.pageEvent = HeaderFooter(testName)
            // step 3: we open the document
            with(document) {
                open()
                // step 4: we use the given pdfCreator to construct the pdf
                addTitle("Testcase example for $pdfFile")
                addSubject("This is the produced testcase content")
                addKeywords("iText, DFS, test, openPDF, $testName, metadata")
                addCreator(javaClass.canonicalName)
                addAuthor("st@deutschefin.tech")
            }
            pdfCreator(document)
        } catch (ex: Exception) {
            println("error executing test '$testName': ${ex::class.java.name} ${ex.message}")
        }
        // step 5: we close the document
        document.close()
        val successful = pdfFile.exists() && pdfFile.isFile
        if (SUCCESS) SUCCESS = successful
        Assert.assertTrue("### PDF file '$pdfFile' not created", successful)
        Assert.assertTrue(
            "### PDF file '$pdfFile' content not created",
            pdfFile.length() > 890
        )
    }

    // -----------------------------------------------------------------------------------------------
    // JUnit cycle functions for each testcase
    // -----------------------------------------------------------------------------------------------
    @Before
    fun setup() {
        println("*** setup test")
    }

    @After
    fun teardown() {
        println("*** teardown test")
    }

    // -----------------------------------------------------------------------------------------------
    // JUnit cycle functions for each test class
    // -----------------------------------------------------------------------------------------------
    companion object {
        val BASEDIR = File("./output/test/pdf")
        var SUCCESS = true

        @JvmStatic
        @BeforeClass
        fun setupBeforeClass() {
            println("*** class setup $javaClass")
            if (BASEDIR.exists()) {
                BASEDIR.deleteRecursively()
                println("*** class cleanup $BASEDIR")
            }
            println("*** class create basedir for PDF output at $BASEDIR")
            BASEDIR.mkdirs()
        }

        @JvmStatic
        @AfterClass
        fun teardownAfterClass() {
            println("*** class teardown $javaClass")
        }
    }
}

class HeaderFooter(val testName: String) : PdfPageEventHelper() {
    val headers = arrayOf(Phrase(""), Phrase(""), Phrase(""))
    var pagenumber = 0

    override fun onOpenDocument(writer: PdfWriter?, document: Document?) {
        headers[0] = Phrase("*** UnitTest '$testName' ***", FONT_TEXT) // Top centered
        headers[1] = Phrase("Created at ${SimpleDateFormat("dd.MM.yyy hh:mm:ss").format(Date())}",
            FONT_TEXTSMALL) // Bottom left
    }

    override fun onStartPage(writer: PdfWriter?, document: Document?) {
        pagenumber++
    }

    override fun onEndPage(writer: PdfWriter?, document: Document?) {
        val rect = writer?.getBoxSize("art")
        if(rect != null) {
            headers[2] = Phrase("Page $pagenumber", FONT_TEXTSMALL) // Bottom right
            // print Header top, centered (title)
            ColumnText.showTextAligned(writer.directContent, Element.ALIGN_CENTER,
                headers[0], (rect.left + rect.right) / 2, rect.top + 25,0f)
            // print Footer bottom, left (date)
            ColumnText.showTextAligned(writer.directContent, Element.ALIGN_LEFT,
                headers[1], rect.left, rect.bottom - 25,0f)
            // print Footer bottom, left (date)
            ColumnText.showTextAligned(writer.directContent, Element.ALIGN_RIGHT,
                headers[2], rect.right + 25, rect.bottom - 15,45f)
        }
    }
}
