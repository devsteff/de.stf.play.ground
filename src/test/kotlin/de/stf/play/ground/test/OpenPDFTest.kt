package de.stf.play.ground.test

import com.lowagie.text.Document
import com.lowagie.text.DocumentException
import com.lowagie.text.Element
import com.lowagie.text.Paragraph
import com.lowagie.text.Phrase
import com.lowagie.text.alignment.HorizontalAlignment
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
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
            for (i: Int in 1..125) {
                document.add(Paragraph("Hello World $i"))
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
            val table = PdfPTable(3)
            var cell = PdfPCell(Phrase("Cell with colspan 3"))
            with(cell) {
                colspan = 3
                backgroundColor = Color.GRAY
                defaultPadding()
                horizontalAlignment = HorizontalAlignment.CENTER.id
            }
            table.addCell(cell)
            cell = PdfPCell(Phrase("Cell with rowspan 2")).defaultPadding()
            cell.rowspan = 2
            cell.backgroundColor = Color.LIGHT_GRAY
            table.addCell(cell)
            document.add(
                table.addTextCell("R1, C1")
                    .addTextCell("R1, C2", HorizontalAlignment.RIGHT.id)
                    .addTextCell("R2,\nC1")
                    .addTextCell("R2, C2", HorizontalAlignment.CENTER.id)
            )
        }
    }

    // -----------------------------------------------------------------------------------------------
    // Helper functions
    // -----------------------------------------------------------------------------------------------
    private fun PdfPTable.addTextCell(text: String,
        hAlign: Int = HorizontalAlignment.LEFT.id): PdfPTable {
        addCell(createCell(text, hAlign))
        return this
    }

    private fun createCell(text: String, hAlign: Int = HorizontalAlignment.LEFT.id): PdfPCell {
        val cell = PdfPCell(Phrase(text)).defaultPadding()
        cell.horizontalAlignment = hAlign
        return cell
    }

    private fun PdfPCell.defaultPadding(): PdfPCell {
        paddingTop = 1f
        paddingBottom = 5f
        paddingLeft = 5f
        paddingRight = 5f
        return this
    }

    // -----------------------------------------------------------------------------------------------
    // Testcase executor
    // -----------------------------------------------------------------------------------------------
    private fun executeTest(testName: String, pdfCreator: (document: Document) -> Unit) {
        val document = Document() // step 1: creation of a document-object
        val pdfFile = File("$BASEDIR/$testName.pdf")
        try {
            // step 2:
            // we create a writer that listens to the document
            // and directs a PDF-stream to a file
            PdfWriter.getInstance(document, FileOutputStream(pdfFile))
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
        Assert.assertTrue("### PDF file '$pdfFile' content not created",
            pdfFile.length() > 890)
    }

    // -----------------------------------------------------------------------------------------------
    // JUnit cycle functions for each testcase
    // -----------------------------------------------------------------------------------------------
    @Before
    fun setup() {
        println("*** setup test")
        // Thread.currentThread().stackTrace.iterator().forEach {
        //    println("    ${it.className}.${it.methodName} (line ${it.lineNumber})")
        // }
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
