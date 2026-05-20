package de.ericbrand.passportphotocreator.platform.printing

import android.app.Activity
import android.content.Context
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintManager

fun launchPrintJob(
    activity: Activity,
    jobName: String,
    adapter: PrintDocumentAdapter
){
    val printManager = activity.getSystemService(Context.PRINT_SERVICE) as PrintManager
    val attrs = PrintAttributes.Builder()
        .setMediaSize(PrintAttributes.MediaSize.JPN_HAGAKI.asPortrait())
        .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
        .build()

    printManager.print(jobName, adapter, attrs)
}