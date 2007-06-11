var currentPhotoId;
var intervalhandler;

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
	var curr = currentPhotoId;
	var xmlHTTP = ptGetXMLHTTP();
	xmlHTTP.onreadystatechange = function() {
		if(xmlHTTP.readyState == 4) {
			if(xmlHTTP.status == 200) {
				currentPhotoId = xmlHTTP.responseXML.documentElement.firstChild.nodeValue;
				if (curr == currentPhotoId){
					clearInterval(intervalhandler);
					window.setTimeout("Hinweis()", 5000);
					return;
				}
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

function Hinweis () {
  alert("Last photo of this album");
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
	img.src = "/album/photo/" + currentPhotoId;
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
	
	intervalhandler = window.setInterval("ptLoadNextImage()", 5000);
	
}