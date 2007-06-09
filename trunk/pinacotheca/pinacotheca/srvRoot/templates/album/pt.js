var currentPhotoId;

function setHasNext(hasNext) {
}

function setHasPrev(hasPrev) {
}

function ptLoadPrevImage() {
	var xmlHTTP = ptGetXMLHTTP();
	xmlHTTP.onreadystatechange = function() {
		if(xmlHTTP.readyState == 4) {
			if(xmlHTTP.status == 200) {
				currentPhotoId = xmlHTTP.responseXML.documentElement.firstChild.nodeValue;
				ptSetImage();
			} else {
				alert("Failed to load previous Photo.");
			}
		}
	}
	xmlHTTP.open("POST", "/ajax/prevphoto", true);
	xmlHTTP.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xmlHTTP.send("currentphoto=" + currentPhotoId);
}

function ptLoadNextImage() {
	var xmlHTTP = ptGetXMLHTTP();
	xmlHTTP.onreadystatechange = function() {
		if(xmlHTTP.readyState == 4) {
			if(xmlHTTP.status == 200) {
				currentPhotoId = xmlHTTP.responseXML.documentElement.firstChild.nodeValue;
				ptSetImage();
			} else {
				alert("Failed to load next Photo.");
			}
		}
	}
	xmlHTTP.open("POST", "/ajax/nextphoto", true);
	xmlHTTP.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xmlHTTP.send("currentphoto=" + currentPhotoId);
}

function ptGetFileName(filename) {
    var re = new RegExp(/([^\/\\]+)$/);
    var m = re.exec(filename);
    if (m == null) {
    	return null;
    } else {
    	return m[0];
    }
}

function ptSetImage() {
	var img = document.getElementById("photo");
	var xmlHTTP = ptGetXMLHTTP();
	xmlHTTP.onreadystatechange = function() {
		if(xmlHTTP.readyState == 4) {
			if(xmlHTTP.status == 200) {
				name = xmlHTTP.responseXML.getElementsByTagName("name")[0].firstChild.nodeValue;
				description = xmlHTTP.responseXML.getElementsByTagName("description")[0].firstChild.nodeValue;
				document.getElementById("photoName").innerHTML = name;
				document.getElementById("photoDescription").innerHTML = description;
			} else {
				alert("Failed to load Photo Information.");
			}
		}
	}
			
	img.src = "/album/photo/" + currentPhotoId;
	xmlHTTP.open("POST", "/ajax/photoinfo", true);
	xmlHTTP.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	xmlHTTP.send("photo=" + currentPhotoId);
}

function ptGetXMLHTTP() {
	try { return new ActiveXObject("Msxml2.XMLHTTP"); } catch (e) {}
	try { return new ActiveXObject("Microsoft.XMLHTTP"); } catch (e) {}
	try { return new XMLHttpRequest(); } catch(e) {}
	alert("XMLHttpRequest not supported.");
	return null;
}

window.onload = function() {
	currentPhotoId = ptGetFileName(location.href);
	ptSetImage();
}