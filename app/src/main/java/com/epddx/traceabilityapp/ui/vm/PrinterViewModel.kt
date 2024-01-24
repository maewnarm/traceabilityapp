package com.epddx.traceabilityapp.ui.vm

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.sunmi.printerx.PrinterSdk
import com.sunmi.printerx.PrinterSdk.Printer
import com.sunmi.printerx.api.PrintResult
import com.sunmi.printerx.enums.HumanReadable
import com.sunmi.printerx.enums.PrinterInfo
import com.sunmi.printerx.enums.PrinterType
import com.sunmi.printerx.enums.Shape
import com.sunmi.printerx.style.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

var selectPrinter: Printer? = null

class PrinterViewModel() : ViewModel() {
    private var printer: PrinterSdk.Printer? = null

    private var _showPrinters = MutableStateFlow<MutableList<PrinterSdk.Printer>>(arrayListOf())
    private var _printerStatus = MutableStateFlow("")
    private var _printerName = MutableStateFlow("")
    private var _printerType = MutableStateFlow("")
    private var _printerPaper = MutableStateFlow("")

    val showPrinters: StateFlow<MutableList<PrinterSdk.Printer>> get() = _showPrinters.asStateFlow()
    val printerStatus: StateFlow<String> get() = _printerStatus.asStateFlow()
    val printerName: StateFlow<String> get() = _printerName.asStateFlow()
    val printerType: StateFlow<String> get() = _printerType.asStateFlow()
    val printerPaper: StateFlow<String> get() = _printerPaper.asStateFlow()

    private var count = MutableStateFlow("1")

    fun initPrinter(context: Context,onGotPrinter: () -> Unit) {
        PrinterSdk.getInstance().getPrinter(context, object : PrinterSdk.PrinterListen {

            override fun onDefPrinter(printer: PrinterSdk.Printer?) {
                if (selectPrinter == null) {
                    selectPrinter = printer
                    onGotPrinter()
                }
            }

            override fun onPrinters(printers: MutableList<PrinterSdk.Printer>?) {
                _showPrinters.value = printers ?: arrayListOf()
            }

        })
    }

    fun changeSelectPrinter() {
        selectPrinter = printer
    }

    fun showPrinter() {
        this.printer = selectPrinter
        if (this.printer != null) {
            val printer: Printer = this.printer!!
            _printerStatus.value = printer.queryApi().status.name
            _printerName.value = printer.queryApi().getInfo(PrinterInfo.NAME)
            _printerType.value = printer.queryApi().getInfo(PrinterInfo.TYPE)
            _printerPaper.value = printer.queryApi().getInfo(PrinterInfo.PAPER)
        }
    }

    fun changeText() {
        Log.d("PRINTER", "change text")
        _printerStatus.value = "Test"
    }

    fun checkPrinterPaper(printer: Printer) {
        printer.let {
            val paper = it.queryApi().getInfo(PrinterInfo.PAPER)
            val printerType = it.queryApi().getInfo(PrinterInfo.TYPE)
            when (paper) {
                "58mm" -> println("Machine paper 58mm")
                "80mm" -> println("Machine paper 80mm")
                else -> {
                    if (printerType == PrinterType.THERMAL.toString()) {
                        println("Machine paper 58mm")
                    } else {
                        println("Non-heat machine")
                    }
                }
            }
        }
    }

    fun releaseSdk() {
        PrinterSdk.getInstance().destroy()
    }

    fun testPrint() {
        this.printer?.canvasApi()?.run {
            initCanvas(BaseStyle.getStyle().setWidth(384).setHeight(340))
            renderArea(
                AreaStyle.getStyle().setStyle(Shape.BOX).setPosX(0).setPosY(0).setWidth(384)
                    .setHeight(219)
            )
            renderText(
                "可口可乐(2L)", TextStyle.getStyle().setTextSize(30).enableBold(true)
                    .setPosX(10).setPosY(20)
            )
            renderText(
                "2L", TextStyle.getStyle().setTextSize(20)
                    .setPosX(10).setPosY(60)
            )
            renderText(
                "200000", TextStyle.getStyle().setTextSize(20)
                    .setPosX(10).setPosY(85)
            )
            renderText(
                "瓶", TextStyle.getStyle().setTextSize(24)
                    .setPosX(10).setPosY(130)
            )
            renderBarCode(
                "12345678", BarcodeStyle.getStyle().setPosX(200).setPosY(60)
                    .setReadable(HumanReadable.POS_TWO).setDotWidth(2).setBarHeight(60)
                    .setWidth(160)
            )
            renderQrCode(
                "www.sunmi.com",
                QrStyle.getStyle().setDot(3).setPosX(20).setPosY(180).setWidth(120).setHeight(120)
            )
            renderText(
                "￥ 7.8",
                TextStyle.getStyle().setTextSize(16).setTextWidthRatio(1).setTextHeightRatio(1)
                    .enableBold(true).setPosX(190).setPosY(160)
            )
            printCanvas(count.value?.toInt() ?: 1, object : PrintResult() {
                override fun onResult(resultCode: Int, message: String?) {
                    if (resultCode == 0) {
                    } else {
                        println(selectPrinter?.queryApi()?.status)
                    }
                }
            })
        }
        this.printer?.lineApi()?.autoOut()
    }
}
