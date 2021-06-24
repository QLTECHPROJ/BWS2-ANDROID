package com.brainwellnessspa

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/* This class file is the create custom pdf view */

class PdfActivity : AppCompatActivity() {
    private lateinit var btnCreatePdf: Button
    var tvTitle: TextView? = null
    var tvSubTitle: TextView? = null
    private var tvLocation: TextView? = null
    private var tvCity: TextView? = null
    private var fileNamePath = ""
    private var permissionAll = 1
    private var permisions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission_group.STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        if (!hasPermissions(this@PdfActivity, *permisions)) {
            ActivityCompat.requestPermissions(this@PdfActivity, permisions, permissionAll)
        }
        val file = File(getExternalFilesDir(null)!!.absolutePath, "pdfsdcard_location")
        if (!file.exists()) {
            file.mkdir()
        }

        //this.getExternalFilesDir(null)?.getAbsolutePath()
        btnCreatePdf = findViewById(R.id.btnCreatePdf)
        tvTitle = findViewById(R.id.tv_title)
        tvSubTitle = findViewById(R.id.tv_sub_title)
        tvLocation = findViewById(R.id.tv_location)
        tvCity = findViewById(R.id.tv_city)
        btnCreatePdf.setOnClickListener { createpdf() }
    }

    private fun createpdf() {
        val bounds = Rect()
        val pageWidth = 300
        val pageheight = 470
        val pathHeight = 2
        val fileName = "mypdf"
        fileNamePath = "/pdfsdcard_location/$fileName.pdf"
        val myPdfDocument = PdfDocument()
        val paint = Paint()
        val paint2 = Paint()
        val path = Path()
        val myPageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageheight, 1).create()
        val documentPage = myPdfDocument.startPage(myPageInfo)
        val canvas = documentPage.canvas
        var y = 25 // x = 10,
        var x = 10 //int x = (canvas.getWidth() / 2);
        paint.getTextBounds(tvTitle!!.text.toString(), 0, tvTitle!!.text.toString().length, bounds)
        x = canvas.width / 2 - bounds.width() / 2
        canvas.drawText(tvTitle!!.text.toString(), x.toFloat(), y.toFloat(), paint)
        paint.getTextBounds(tvSubTitle!!.text.toString(), 0, tvSubTitle!!.text.toString().length, bounds)
        x = canvas.width / 2 - bounds.width() / 2
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText(tvSubTitle!!.text.toString(), x.toFloat(), y.toFloat(), paint)
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)

        //horizontal line
        path.lineTo(pageWidth.toFloat(), pathHeight.toFloat())
        paint2.color = Color.GRAY
        paint2.style = Paint.Style.STROKE
        path.moveTo(x.toFloat(), y.toFloat())
        canvas.drawLine(0f, y.toFloat(), pageWidth.toFloat(), y.toFloat(), paint2)

        //blank space
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)
        y += (paint.descent() - paint.ascent()).toInt()
        x = 10
        canvas.drawText(tvLocation!!.text.toString(), x.toFloat(), y.toFloat(), paint)
        y += (paint.descent() - paint.ascent()).toInt()
        x = 10
        canvas.drawText(tvCity!!.text.toString(), x.toFloat(), y.toFloat(), paint)

        //blank space
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)

        //horizontal line
        path.lineTo(pageWidth.toFloat(), pathHeight.toFloat())
        paint2.color = Color.GRAY
        paint2.style = Paint.Style.STROKE
        path.moveTo(x.toFloat(), y.toFloat())
        canvas.drawLine(0f, y.toFloat(), pageWidth.toFloat(), y.toFloat(), paint2)

        //blank space
        y += (paint.descent() - paint.ascent()).toInt()
        canvas.drawText("", x.toFloat(), y.toFloat(), paint)
        val res = resources
        val bitmap = BitmapFactory.decodeResource(res, R.drawable.app_new_icon)
        val b = Bitmap.createScaledBitmap(bitmap, 100, 50, false)
        canvas.drawBitmap(b, x.toFloat(), y.toFloat(), paint)
        y += 25
        canvas.drawText(getString(R.string.app_name), 120f, y.toFloat(), paint)
        myPdfDocument.finishPage(documentPage)
        val file = File(getExternalFilesDir(null)!!.absolutePath + fileNamePath)
        try {
            myPdfDocument.writeTo(FileOutputStream(file))
        } catch (e: IOException) {
            e.printStackTrace()
        }
        myPdfDocument.close()
        viewPdfFile()
    }

    private fun viewPdfFile() {
        val file = File(getExternalFilesDir(null)!!.absolutePath + fileNamePath)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(file), "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
        startActivity(intent)
    }

    companion object {
        fun hasPermissions(context: Context?, vararg permissions: String?): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission!!) != PackageManager.PERMISSION_GRANTED) {
                        return false
                    }
                }
            }
            return true
        }
    }
}