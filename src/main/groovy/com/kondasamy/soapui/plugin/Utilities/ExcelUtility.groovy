package com.kondasamy.soapui.plugin.Utilities

import org.apache.poi.xssf.usermodel.XSSFWorkbook
/**
 * Created by Kondasamy J
 * Contact: Kondasamy@outlook.com
 */

XSSFWorkbook wb = new XSSFWorkbook()
FileOutputStream fileOut = new FileOutputStream("D:/dummy.xlsx")
wb.write(fileOut)
wb.close()

