# Cobertura de errores en la interacción con AFIP

Este documento resume cómo maneja la aplicación la autorización de comprobantes
con AFIP, qué garantías ofrece frente a errores o demoras y qué validaciones
adicionales se realizan antes y después del guardado de una factura.

## Flujo de autorización

1. **Preparación de archivos**. Antes de invocar a PyAfipWs se genera el archivo
   `entrada.txt` con los datos de la factura y, si corresponde, del
   comprobante asociado. 【F:src/main/java/utils/pyAfip/AfipManagement.java†L63-L108】
2. **Ejecución del comando**. Se lanza el ejecutable configurado (`pyafipws`,
   `rece1.exe` o el que se indique por propiedad del sistema) y se espera a que
   termine. La invocación se realiza dentro del directorio de trabajo de
   PyAfipWs para que los archivos de entrada/salida estén disponibles. 【F:src/main/java/services/afip/AfipAuthorizationService.java†L44-L92】
3. **Lectura del resultado**. Tras finalizar el proceso se interpreta el
   `salida.txt` generado, extrayendo estado, CAE, fecha de vencimiento y
   mensajes informativos o de error. 【F:src/main/java/services/afip/AfipAuthorizationService.java†L94-L112】
4. **Persistencia**. Sólo si AFIP aprobó el comprobante **y** devolvió un CAE se
   continúa con el guardado de la factura y de sus detalles en la base de datos.
   En caso contrario se detiene el flujo y se informa al usuario. 【F:src/main/java/views/clients/ClientInvoiceInsertView.java†L1445-L1473】

## Garantías ante demoras o fallos

- **Esperar al resultado oficial**. El proceso externo se invoca con
  `Process.waitFor()`, por lo que la aplicación queda a la espera hasta que
  AFIP devuelve el control. Esto evita que se guarden comprobantes sin
  confirmación de la AFIP. 【F:src/main/java/services/afip/AfipAuthorizationService.java†L70-L92】
- **Validación de CAE obligatorio**. Aunque AFIP marque un comprobante como
  aprobado, el sistema verifica que el CAE esté presente antes de persistir la
  factura, lo que impide guardar facturas sin el código electrónico. 【F:src/main/java/views/clients/ClientInvoiceInsertView.java†L1453-L1473】
- **Manejo de interrupciones/IO**. Si el proceso es interrumpido o falla la
  ejecución del comando, se lanza una excepción específica que se muestra al
  usuario y detiene el flujo de guardado. 【F:src/main/java/services/afip/AfipAuthorizationService.java†L52-L67】
- **Rechazos de AFIP**. Cuando AFIP rechaza el comprobante se muestra el mensaje
  devuelto por el servicio y no se guarda información parcial. 【F:src/main/java/views/clients/ClientInvoiceInsertView.java†L1447-L1460】

## Casos de error contemplados

### Validaciones previas al guardado

- Falta seleccionar CUIT emisor o cliente. 【F:src/main/java/views/clients/ClientInvoiceInsertView.java†L1513-L1536】
- Punto de venta incompatible con tipos manuales (Presupuesto / Nota de
  Devolución) o viceversa. 【F:src/main/java/views/clients/ClientInvoiceInsertView.java†L1769-L1785】
- Comprobante asociado inexistente, inválido o emitido por otro CUIT. 【F:src/main/java/views/clients/ClientInvoiceInsertView.java†L1409-L1444】
- Artículos sin stock suficiente salvo en devoluciones/notas de crédito. 【F:src/main/java/views/clients/ClientInvoiceInsertView.java†L1716-L1752】

### Autorización AFIP

- Comando externo no configurado o retorno distinto de cero. 【F:src/main/java/services/afip/AfipAuthorizationService.java†L59-L92】
- Rechazo explícito por parte de AFIP, mostrando el detalle provisto. 【F:src/main/java/views/clients/ClientInvoiceInsertView.java†L1447-L1460】
- Aprobación sin CAE: el flujo se detiene y se notifica para que pueda
  reintentarse la consulta antes de continuar. 【F:src/main/java/views/clients/ClientInvoiceInsertView.java†L1453-L1473】

### Persistencia y operaciones posteriores

- Fallos al guardar factura/detalles o al ajustar stock generan mensajes de
  error y registros en el log. 【F:src/main/java/views/clients/ClientInvoiceInsertView.java†L1475-L1493】
- Excepciones de autorización se muestran al usuario y evitan guardar datos
  incompletos. 【F:src/main/java/views/clients/ClientInvoiceInsertView.java†L1477-L1484】

## Documentos manuales (sin AFIP)

Los tipos **Presupuesto** y **Nota de Devolución** ahora forman parte del
listado de comprobantes disponibles, pero están marcados como documentos
manuales: no disparan autorización ante AFIP y se validan con punto de venta
`0000`. Esto permite que impacten en cuentas corrientes y stock como el resto de
los comprobantes, sin requerir comunicación con la AFIP. 【F:src/main/java/repositories/impl/InvoiceTypeRepositoryImpl.java†L13-L31】【F:src/main/java/utils/InvoiceTypeUtils.java†L19-L60】
