package com.cquisper.springboot.app.view.xlsx;

import com.cquisper.springboot.app.models.entities.Factura;
import com.cquisper.springboot.app.models.entities.ItemFactura;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@Component("factura/ver.xlsx") //No se pueden tener dos componentes con el mismo nombre
public class FacturaXlsxView extends AbstractXlsxView {
    @Value("${format.date.complete}")
    private String patternDateComplete;
    @Autowired
    private LocaleResolver localeResolver;
    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Disposition", "attachment; filename=\"factura_view.xlsx\"");
        Factura factura = (Factura) model.get("factura");
        Locale locale = localeResolver.resolveLocale(request);
        MessageSourceAccessor mensajes = getMessageSourceAccessor();

        Sheet sheet = workbook.createSheet(mensajes.getMessage("text.factura.report.title"));

        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(mensajes.getMessage("text.factura.ver.datos.cliente"));

        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido());

        row = sheet.createRow(2);
        cell = row.createCell(0);
        cell.setCellValue(factura.getCliente().getEmail());

        sheet.createRow(4).createCell(0).setCellValue(mensajes.getMessage("text.factura.ver.datos.factura"));

        row = sheet.createRow(5);
        row.createCell(0).setCellValue(mensajes.getMessage("text.cliente.factura.folio") + ": ");
        row.createCell(1).setCellValue(factura.getId());

        row = sheet.createRow(6);
        row.createCell(0).setCellValue(mensajes.getMessage("text.cliente.factura.descripcion") + ": ");
        row.createCell(1).setCellValue(factura.getDescripcion());

        row = sheet.createRow(7);
        row.createCell(0).setCellValue(mensajes.getMessage("text.cliente.factura.fecha") + ": ");
        row.createCell(1).setCellValue(factura.getCreateAt().format(DateTimeFormatter.ofPattern(patternDateComplete, locale)));

        CellStyle theaderStyle = getCellStyle(workbook, BorderStyle.MEDIUM, IndexedColors.GOLD, FillPatternType.SOLID_FOREGROUND);

        CellStyle tbodyStyle = getCellStyle(workbook, BorderStyle.THIN, IndexedColors.WHITE, FillPatternType.SOLID_FOREGROUND);

        Row header = sheet.createRow(9);
        header.createCell(0).setCellValue(mensajes.getMessage("text.factura.form.item.nombre"));
        header.createCell(1).setCellValue(mensajes.getMessage("text.factura.form.item.precio"));
        header.createCell(2).setCellValue(mensajes.getMessage("text.factura.form.item.cantidad"));
        header.createCell(3).setCellValue(mensajes.getMessage("text.factura.form.item.total"));

        header.getCell(0).setCellStyle(theaderStyle);
        header.getCell(1).setCellStyle(theaderStyle);
        header.getCell(2).setCellStyle(theaderStyle);
        header.getCell(3).setCellStyle(theaderStyle);

        int rownum = 10;
        for (ItemFactura itemFactura : factura.getItemFacturas()) {
            Row fila = sheet.createRow(rownum++);
            cell = fila.createCell(0);
            cell.setCellValue(itemFactura.getProducto().getNombre());
            cell.setCellStyle(tbodyStyle);

            cell = fila.createCell(1);
            cell.setCellValue(DecimalFormat.getCurrencyInstance(locale).format(itemFactura.getProducto().getPrecio()));
            cell.setCellStyle(tbodyStyle);

            cell = fila.createCell(2);
            cell.setCellValue(itemFactura.getCantidad());
            cell.setCellStyle(tbodyStyle);

            cell = fila.createCell(3);
            cell.setCellValue(DecimalFormat.getCurrencyInstance(locale).format(itemFactura.calcularImporte()));
            cell.setCellStyle(tbodyStyle);
        }

        Row filaTotal = sheet.createRow(rownum);
        cell = filaTotal.createCell(2);
        cell.setCellValue(mensajes.getMessage("text.factura.form.total"));
        cell.setCellStyle(tbodyStyle);

        cell = filaTotal.createCell(3);
        cell.setCellValue(DecimalFormat.getCurrencyInstance(locale).format(factura.getTotal()));
        cell.setCellStyle(tbodyStyle);
    }

    private static CellStyle getCellStyle(Workbook workbook, BorderStyle borderStyle, IndexedColors colors, FillPatternType fillType) {
        CellStyle cellStyle = workbook.createCellStyle();

        cellStyle.setBorderBottom(borderStyle);
        cellStyle.setBorderTop(borderStyle);
        cellStyle.setBorderRight(borderStyle);
        cellStyle.setBorderLeft(borderStyle);
        cellStyle.setFillForegroundColor(colors.getIndex());
        cellStyle.setFillPattern(fillType);

        return cellStyle;
    }
}
