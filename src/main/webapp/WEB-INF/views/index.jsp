<html>
<head>
<!-- Load Leaflet from CDN-->
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/leaflet/1.0.0-beta.2/leaflet.css" />
  <script src="https://cdn.jsdelivr.net/leaflet/1.0.0-beta.2/leaflet.js"></script>
<script src="http://code.jquery.com/jquery-1.11.3.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/leaflet-providers/1.1.7/leaflet-providers.min.js"></script>
<!-- Load Esri Leaflet from CDN -->
  <script src="https://cdn.jsdelivr.net/leaflet.esri/2.0.0-beta.8/esri-leaflet.js"></script>
</head>
<body>


<form name="QueryTypeForm">
    <label>ComID</label><input aria-label="ComID" property="comID" name="ComIDField"></input>
    <label>Query Type</label>
    <select name="QueryType" property="query_type" size="1">
        <option selected="selected"> </option>
        <option value="UM">Upstream Main</option>
        <option value="DM">Downstream Main</option>
        <option value="DD">Downstream with Diversions</option>
        <option value="UT">Upstream with Tributaries</option>
    </select>
    <label>distance</label><input property="distance" name="DistanceField">
    <button type="button" onclick="on_submit_action();">submit!</button>
</form>
<div id="map" style="width: 100%; height: 600px;"></div>

<script type="application/javascript">

                </script>

<script type="text/javascript">
var map = L.map('map');
L.esri.basemapLayer("Gray").addTo(map);
  L.esri.tiledMapLayer({
    url: "http://hydrology.esri.com/arcgis/rest/services/WorldHydroReferenceOverlay/MapServer"
  }).addTo(map);
map.setView([35.9908385, -78.9005222], 3);

var geojsonMarkerOptions = {
    radius: 4,
    fillColor: "#ff7800",
    color: "#000",
    weight: 1,
    opacity: 1,
    fillOpacity: 0.8
};

function onEachPointFeature(feature, layer) {
            var popupText = "Data Source: " + "WQP"
                + "<br>Station Name: " + feature.properties.name
                + "<br>Station ID: " + feature.properties.identifier
                + "<br>Station ComID: " + feature.properties.comid
                + "<br>Station Data: " + "<a href="+feature.properties.uri+">click for csv</a>";
            layer.bindPopup(popupText);
            }

function onEachLineFeature(feature, layer) {
            var popupText = "Data Source: NHD+"
                + "<br>Reach ComID: " + feature.properties.nhdplus_comid
            layer.bindPopup(popupText);
            }

function addPointDataToMap(data, map) {
    var pointLayer = L.geoJson(data, {
        onEachFeature: onEachPointFeature,
        pointToLayer: function (feature, latlng) {
        return L.circleMarker(latlng, geojsonMarkerOptions);
        }
        });
    pointLayer.addTo(map);
    map.fitBounds(L.geoJson(data).getBounds());
}

function addLineDataToMap(data, map) {

    var lineLayer = L.geoJson(data, {
        onEachFeature: onEachLineFeature
        });
    lineLayer.addTo(map);
    //map.fitBounds(L.geoJson(data).getBounds());
}

//you will need to replace this with the actual url of the endpoint once the CORS headers are properly set
var nldiURL = "http://cidasdpdasnldi.cr.usgs.gov:8080/nldi-services/comid/";

function on_submit_action()
    {

        var e=document.getElementsByName("QueryType")[0];
        var c=document.getElementsByName("ComIDField")[0];
        var d=document.getElementsByName("DistanceField")[0];
        var wqpURL = nldiURL+c.value+"/navigate/"+e.value+"/wqp";
        var nhdURL = nldiURL+c.value+"/navigate/"+e.value;
        console.log(d.value);
        console.log(wqpURL);
        //$.get(wqpURL, {}, function(data) { addPointDataToMap(data, map); };);
        console.log("getting sites");
        $.getJSON( wqpURL, {distance:d.value}, function(data) { addPointDataToMap(data, map); });
        console.log("sites added, getting streams");
        $.getJSON( nhdURL, {distance:d.value}, function(data) { addLineDataToMap(data, map); });


    }


</script>
</body>
</html>