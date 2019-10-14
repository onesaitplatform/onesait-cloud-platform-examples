var digitalTwinApi = Java.type('com.minsait.onesait.platform.digitaltwin.logic.api.DigitalTwinApi').getInstance();
var ObjectMapper = Java.type('com.fasterxml.jackson.databind.ObjectMapper');
var objectMapper = new ObjectMapper();
var HashMap = Java.type('java.util.HashMap');

Date.prototype.fechaDashboard = function() {
  var mn = this.getMonth() + 1;
  var dd = this.getDate();
  var hh = this.getHours();
  var mm = this.getMinutes();

  return [(dd>9 ? '' : '0') + dd,
          '/',
          (mn>9 ? '' : '0') + mn,
          '/',
          this.getFullYear(),
          ' ',
          (hh>9 ? '' : '0') + hh,
          ':',
          (mm>9 ? '' : '0') + mm
          ].join('');
};

function init(){
  digitalTwinApi.log('Init Termostato');
}

function main(){
    digitalTwinApi.log('Main Termostato');
    // Convertimos los datos a un Map, luego lo pasamos por Jackson para convertirlo en un String con el JSON para realizar el guardado.
    var hm = new HashMap();
    var temperatura = Math.round(getRandom(10, 38));
    var fecha = new Date();

    digitalTwinApi.setStatusValue("temperatura", temperatura)
    hm.put('temperatura', temperatura);
    hm.put('fecha_dashboard', fecha.fechaDashboard());

    digitalTwinApi.sendUpdateShadow(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(hm));
    digitalTwinApi.log('Set updateShadow');
}

/**
 * Obtiene un número aleatorio comprendido entre min y max.
 */
function getRandom(min, max) {
    return Math.random() * (max - min) + min;
}

var onActionObtenerTemperatura=function(data){
    digitalTwinApi.log('ObtenerTemperatura:' + data);
}
