# Manual regression tests for line-item editors

The following smoke tests verify that editing quantities and prices triggers the automatic subtotal recalculation without requiring the **Enter** key, and that validation prevents saving inconsistent data.

## ClientRemitUpsertView
1. Abrir un remito nuevo.
2. Agregar un artículo cualquiera desde la búsqueda.
3. Cambiar la cantidad directamente en la celda (sin presionar **Enter**) y hacer clic en otra celda.
4. Verificar que la columna **Total** y el total general se actualizan automáticamente.
5. Intentar guardar con un subtotal modificado manualmente y confirmar que aparece el mensaje “El subtotal no coincide…”.

## ClientBudgetUpsertView
1. Crear un presupuesto vacío.
2. Agregar un artículo desde la búsqueda o con código `99`.
3. Editar la cantidad y el precio sin presionar **Enter** y mover el foco a otra celda.
4. Confirmar que la columna **Total** de la fila y el total general se actualizan.
5. Intentar guardar con una fila cuyo subtotal editado no coincide con cantidad × precio y comprobar que se muestra la advertencia.

## ClientInvoiceInsertView
1. Iniciar una nueva factura/presupuesto y cargar un cliente válido.
2. Añadir un artículo y modificar la cantidad o el precio sin confirmar con **Enter**.
3. Verificar que el subtotal de la fila, los campos de impuestos y el total se recalculan automáticamente al salir de la celda.
4. Probar a guardar tras alterar manualmente el subtotal de una fila y confirmar que se bloquea el guardado con el mensaje correspondiente.

> Nota: en los tres formularios, el guardado únicamente continúa cuando cada fila cumple `subtotal == precio × cantidad` (considerando bonificaciones e IVA en la factura).
