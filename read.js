var fs = require("fs");
fs.readFile("neighbourhoods.geojson", (err, text) => {
	if (err === null) {
		var data = JSON.parse(text);
		loop1:
		for (var i = 0; i < data.length; i++) {                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
			var coordinatesArray;
			if (data[i].geometry.type == "MultiPolygon") {
				coordinatesArray = data[i].geometry.coordinates;
			} else {
				coordinatesArray = [data[i].geometry.coordinates];
			}
			for (var h = 0; h < coordinatesArray.length; h++) {
				coordinates = coordinatesArray[h];
				for (var j = 0; j < coordinates.length; j++) {
					var area = coordinates[j];
					for (var k = 0; k < area.length; k++) {
						var lat, lon;
						[lon, lat] = area[k];
						if (data[i].geometry.type == "MultiPolygon") {
							data[i].geometry.coordinates[h][j][k][1] = Math.round(lat * 1000000) / 1000000;
							data[i].geometry.coordinates[h][j][k][0] = Math.round(lon * 1000000) / 1000000;
						} else {
							data[i].geometry.coordinates[j][k][1] = Math.round(lat * 1000000) / 1000000;
							data[i].geometry.coordinates[j][k][0] = Math.round(lon * 1000000) / 1000000;
						}
					}
				}
			}
		}
		fs.writeFile("neighbourhoods.json", JSON.stringify(data), (err) => {
			console.log("Done.");
		});
	}
});