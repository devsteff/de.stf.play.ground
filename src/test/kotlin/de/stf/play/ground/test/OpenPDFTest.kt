package de.stf.play.ground.test

import com.lowagie.text.Document
import com.lowagie.text.DocumentException
import com.lowagie.text.Element
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfWriter
import de.stf.play.ground.test.OpenPDFTest.Companion.BASEDIR
import de.stf.play.ground.test.OpenPDFTest.Companion.success
import org.junit.After
import org.junit.AfterClass
import org.junit.Assert
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class OpenPDFTest {
    companion object {
        val BASEDIR = File("./output/test/pdf")
        var success = true

        @JvmStatic
        @BeforeClass
        fun setupBeforeClass() {
            println("*** setup Class")
            if (BASEDIR.exists()) {
                BASEDIR.deleteRecursively()
                println("*** cleanup $BASEDIR")
            }
            println("*** create basedir for PDF output at $BASEDIR")
            BASEDIR.mkdirs()
        }

        @JvmStatic
        @AfterClass
        fun teardownAfterClass() {
            println("*** teardown Class")
        }
    }

    @Before
    fun setup() {
        println("*** setup")
    }

    @After
    fun teardown() {
        println("*** teardown")
    }

    @Test
    fun helloWorld() {
        val pdfFile = File("$BASEDIR/HelloWorld.pdf")
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
        val result = executeTest("simpleLine") { document ->
            document.add(Paragraph("Hello World"))
        }
        result.hashCode()
    }

    @Test
    fun simpleText() {
        val result = executeTest("simpleText") { document ->
            for (i: Int in 1..125) {
                document.add(Paragraph("Hello World $i"))
            }
        }
    }

    @Test
    fun marginTest() {
        val result = executeTest("marginTest") { document ->
            with(document) {
                add(Paragraph("The left margin of this document is 36pt (0.5 inch); the right margin 72pt " +
                    "(1 inch); the top margin 108pt (1.5 inch); the bottom margin 180pt (2.5 inch). "))
                val paragraph = Paragraph()
                paragraph.setAlignment(Element.ALIGN_JUSTIFIED)
                for (i in 0..19) {
                    paragraph.add("Hello World, Hello Sun, Hello Moon, Hello Stars, Hello Sea, Hello Land, " +
                        "Hello People. ")
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
        } catch (ex: Exception ) {
            println("error executing test '$testName': ${ex::class.java.name} ${ex.message}")
        }
        // step 5: we close the document
        document.close()
        val successfull = pdfFile.exists() && pdfFile.isFile
        if(success) success = successfull
        Assert.assertTrue("### PDF file '$pdfFile' not created", successfull)
        Assert.assertTrue("### PDF file '$pdfFile' content not created", pdfFile.length()>890)
    }
}
