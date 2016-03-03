<html>
<head>
<link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.css"/>
<script src="http://cdn.leafletjs.com/leaflet-0.7.3/leaflet.js"></script>
<script src="http://code.jquery.com/jquery-1.11.3.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/leaflet-providers/1.1.7/leaflet-providers.min.js"></script>
</head>
<body>
<div id="map" style="width: 100%; height: 600px;"></div>
<script type="text/javascript">
var map = L.map('map');
var OpenStreetMap_BlackAndWhite = L.tileLayer('http://{s}.tiles.wmflabs.org/bw-mapnik/{z}/{x}/{y}.png', {
	maxZoom: 18,
	attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
});
OpenStreetMap_BlackAndWhite.addTo(map);
map.setView([35.9908385, -78.9005222], 3);

var geojsonMarkerOptions = {
    radius: 4,
    fillColor: "#ff7800",
    color: "#000",
    weight: 1,
    opacity: 1,
    fillOpacity: 0.8
};
var myStyle = {
	    "color": "#ff7800",
	    "weight": 5,
	    "opacity": 0.65
	};
function onEachFeature(feature, layer) {
            var popupText = "NHDPlus comid: " + feature.properties.nhdplus_comid;
            layer.bindPopup(popupText);
            }

function addDataToMap(data, map) {
    var dataLayer = L.geoJson(data, {
        onEachFeature: onEachFeature,
        style: myStyle});
    dataLayer.addTo(map);
    map.fitBounds(L.geoJson(data).getBounds());
}


$.getJSON("navigation", function(data) { addDataToMap(data, map); });
</script>
</body>
</html>