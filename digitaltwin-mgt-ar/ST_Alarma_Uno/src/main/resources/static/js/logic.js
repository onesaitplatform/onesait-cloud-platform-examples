var digitalTwinApi = Java.type('com.minsait.onesait.platform.digitaltwin.logic.api.DigitalTwinApi').getInstance();
var JSONHelper = Java.type('digitaltwin.device.JSONHelper');
var HashMap = Java.type('java.util.HashMap');
var ObjectMapper = Java.type('com.fasterxml.jackson.databind.ObjectMapper');
var objectMapper = new ObjectMapper();
var JsonNode = Java.type("com.fasterxml.jackson.databind.JsonNode");

function init(){
  // Escribimos en el log.
  digitalTwinApi.log("Init");
  // Inicializamos el estado a false.
  digitalTwinApi.setStatusValue("isOn", false);
}
function main(){
}

var onActionSetActivacion=function(data){
    // Creamos el árbol de nodos de JSON.
    var jsonNodeRoot = objectMapper.readTree(data);
    // Obtenemos el nodo status.
    var nodoHumoDetectado = jsonNodeRoot.get("status");
    // Obtenemos el nodo humoDetectado
    nodoHumoDetectado = nodoHumoDetectado.get("humoDetectado");
    // Obtenemos el nuevo estado de isOn de los datos recibidos.
    var isOn = nodoHumoDetectado.booleanValue();

    digitalTwinApi.log("onActionSetActivacion: data: " + data);
    digitalTwinApi.log("onActionSetActivacion: isOn: " + isOn);

    // Convertimos los datos a un Map, luego lo pasamos por Jackson para convertirlo en un String con el JSON para realizar el guardado.
    var hm = new HashMap();
    digitalTwinApi.setStatusValue("isOn", isOn)
    hm.put('isOn', isOn);

    digitalTwinApi.sendUpdateShadow(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(hm));
    digitalTwinApi.log('Set updateShadow');
}
