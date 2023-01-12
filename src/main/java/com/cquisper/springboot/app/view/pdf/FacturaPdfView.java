package com.cquisper.springboot.app.view.pdf;

import com.cquisper.springboot.app.models.entities.Factura;
import com.cquisper.springboot.app.models.entities.ItemFactura;
import com.lowagie.text.Document;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfCell;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.i18n.LocaleContextResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import java.awt.*;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;

@Component("factura/ver")
public class FacturaPdfView extends AbstractPdfView {
    @Value("${format.date.complete}")
    private String formatDateTime;
    @Autowired
    private MessageSource messageSource;
    @Autowired
    private LocaleResolver localeResolver;
    @Override
    protected void buildPdfDocument(Map<String, Object> model, Document document, PdfWriter writer, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Factura factura = (Factura) model.get("factura");

        Locale locale = localeResolver.resolveLocale(request);

        MessageSourceAccessor mensajes = getMessageSourceAccessor();

        PdfPTable tabla = new PdfPTable(1);
        PdfPCell cell = null;

        tabla.setSpacingAfter(20);

        cell = new PdfPCell(new Phrase(messageSource.getMessage("text.factura.ver.datos.cliente",null, locale)));
        cell.setBackgroundColor(new Color(70, 192, 24));
        cell.setPadding(8f);
        tabla.addCell(cell);

        tabla.addCell(factura.getCliente().getNombre().concat(" ").concat(factura.getCliente().getApellido()));
        tabla.addCell(factura.getCliente().getEmail());

        PdfPTable tabla2 = new PdfPTable(1);
        tabla2.setSpacingAfter(20);

        cell = new PdfPCell(new Phrase(messageSource.getMessage("text.factura.ver.datos.factura",null, locale)));
        cell.setBackgroundColor(new Color(195, 230, 203));
        cell.setPadding(8f);
        tabla2.addCell(cell);

        tabla2.addCell(mensajes.getMessage("text.cliente.factura.folio")+ ": " + factura.getId());
        tabla2.addCell(mensajes.getMessage("text.cliente.factura.descripcion")+": " + factura.getDescripcion());
        tabla2.addCell(mensajes.getMessage("text.cliente.factura.fecha")+": " + factura.getCreateAt().format(DateTimeFormatter.ofPattern(formatDateTime)));

        document.add(tabla);
        document.add(tabla2);

        PdfPTable tabla3 = new PdfPTable(4);
        tabla3.setWidths(new float[]{2, 1, 1, 1});
        tabla3.addCell(mensajes.getMessage("text.factura.form.item.nombre"));
        tabla3.addCell(mensajes.getMessage("text.factura.form.item.precio"));
        tabla3.addCell(mensajes.getMessage("text.factura.form.item.cantidad"));
        tabla3.addCell(mensajes.getMessage("text.factura.form.item.total"));

        factura.getItemFacturas().forEach(itemFactura ->  {
            tabla3.addCell(itemFactura.getProducto().getNombre());
            tabla3.addCell(DecimalFormat.getCurrencyInstance().format(itemFactura.getProducto().getPrecio()));
            tabla3.addCell(itemFactura.getCantidad().toString());
            tabla3.addCell(DecimalFormat.getCurrencyInstance().format(itemFactura.calcularImporte()));
        });

        cell = new PdfPCell(new Phrase(mensajes.getMessage("text.factura.form.total")));
        cell.setColspan(3);
        cell.setBackgroundColor(new Color(158,150,137));
        cell.setHorizontalAlignment(PdfPCell.ALIGN_RIGHT);
        tabla3.addCell(cell);
        tabla3.addCell(DecimalFormat.getCurrencyInstance().format(factura.getTotal()));

        document.add(tabla3);
        document.addTitle(mensajes.getMessage("text.factura.report.title"));
    }
}
