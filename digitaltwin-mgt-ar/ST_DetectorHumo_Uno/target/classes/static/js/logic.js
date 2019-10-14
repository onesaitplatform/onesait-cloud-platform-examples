var digitalTwinApi = Java.type('com.minsait.onesait.platform.digitaltwin.logic.api.DigitalTwinApi').getInstance();
var ObjectMapper = Java.type('com.fasterxml.jackson.databind.ObjectMapper');
var objectMapper = new ObjectMapper();
var HashMap = Java.type('java.util.HashMap');

function init(){
  // Escribimos en el log.
  digitalTwinApi.log("Init");
  // Inicializamos el estado de la detección de humo a false.
  digitalTwinApi.setStatusValue("humoDetectado", false);
  // Llamamos a updateShadow para registrar el estado en la plataforma.
  var hm = new HashMap();
  hm.put("humoDetectado", false);
  digitalTwinApi.sendUpdateShadow(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(hm));
}

function main(){
}
/*
 * Acción que controla el cambio de humo detectado.
 * Si el estado es que no se ha detectado humo, ponemos el estado como que se ha detectado el humo
 * y llamamos al evento correspondiente.
 * Si el estado es que se ha detectado humo, ponemos el estado como que no se ha detectado el humo
 * y llamamos al evento correspondiente.
 */
var onActionToggleHumoDetectado=function(data){
    // Escribimos en el log.
  digitalTwinApi.log("onActionToggleHumoDetectado");
  // Obtengo el estado actual de la detección del humo.
  var isHumoDetectado = digitalTwinApi.getStatusValue("humoDetectado");
  
  // Intercambiamos el estado de la variable de humo detectado.
  isHumoDetectado = !isHumoDetectado
  // Guardamos el estado.
  digitalTwinApi.setStatusValue("humoDetectado", isHumoDetectado);
  
  // Llamamos a updateShadow para registrar el estado en la plataforma.
  var hm = new HashMap();
  hm.put("humoDetectado", isHumoDetectado);
  digitalTwinApi.sendUpdateShadow(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(hm));

  // Lanzo el evento correspondiente según el estado de la detección del humo.
  if (isHumoDetectado){
    digitalTwinApi.sendCustomEvent("humoDetectadoEvent");
  }
  else{
    digitalTwinApi.sendCustomEvent("humoNoDetectadoEvent");
  }
}
