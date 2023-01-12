$(document).ready(function (){
        $("#buscar_producto").autocomplete({
            source: function (request, response){
                $.ajax({
                    url: "/factura/cargar-productos/" + request.term,
                    dataType: "json", // Tipo de envio como json o xml
                    data: { // cuerpo del request o @RequestBody en el back
                        term: request.term // Debe ser obtenido con @RequestParam
                    },
                    success: function (data){ //Recibe el ResponseBody de la respuesta
                        response($.map(data, (item) =>{
                            return{
                                value: item.id,
                                label: item.nombre,
                                precio: item.precio,
                            }
                        } ));
                    },
                    error: function (error){
                        console.log(error)
                    }
                });
            },
            select: function (event, ui){
                $("#buscar_producto").val(ui.item.label);
                console.log(ui.item);
                console.log($("#buscar_producto"));
                console.log(document.querySelector("#buscar_producto").value);

                if(itemsHelper.hasProducto(ui.item.value)){
                    itemsHelper.increment(ui.item.value, ui.item.precio);
                    return false;
                }
                let linea = $("#plantillaItemsFactura").html();

                linea = linea.replace(/{ID}/g, ui.item.value);
                linea = linea.replace(/{NOMBRE}/g, ui.item.label);
                linea = linea.replace(/{PRECIO}/g, ui.item.precio);

                $("#cargarItemProductos tbody").append(linea);
                itemsHelper.calcularImporte(ui.item.value, ui.item.precio, 1);
                return false;
            }
        });
        $("#form").submit(function (){
            console.log("eliminando....")
            $("#plantillaItemsFactura").remove();
            return;
        });
    },
);

let itemsHelper = {
    calcularImporte: function (id, precio, cantidad) {
        $("#total_importe_" + id).html(parseInt(precio) * parseInt(cantidad));
        this.calcularGranTotal();
    },
    hasProducto: function (id){
        let resultado = false;
        $('input[name="item_id[]"]').each(function () {
            console.log("Producto id: " + $(this).val());
            if(parseInt(id) == parseInt($(this).val())){
                resultado = true;
            }
        })
        return resultado;
    },
    increment: function (id, precio){
        let cantidad = $("#cantidad_"+id).val() ? parseInt($("#cantidad_"+id).val()) : 0;
        $("#cantidad_" + id).val(++cantidad);
        this.calcularImporte(id, precio, $("#cantidad_" + id).val());
        console.log("cantidad: " + ++cantidad);
    },
    eliminarLineaFactura: function (id){
        $("#row_"+id).remove();
        this.calcularGranTotal();
    },
    calcularGranTotal: function (){
        let granTotal = 0;
        $('span[id^="total_importe_"]').each(function (){
            console.log("total importe: " + parseInt($(this).text()));
            granTotal += parseInt($(this).text());
        });
        $("#gran_total").text(granTotal);
    }
}