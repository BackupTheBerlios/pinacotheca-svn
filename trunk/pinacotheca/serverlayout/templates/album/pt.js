function ptHTMLEscape(text) {
	var ret = text.replace(/&/,"&amp;");
	ret = ret.replace(/</,"&lt;");
	ret = ret.replace(/>/,"&gt;");
	ret = ret.replace(/\r\n/,"<br/>");
	ret = ret.replace(/\n/,"<br/>");
	ret = ret.replace(/\r/,"<br/>");
	return ret;
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

function ptGetTags(callback) {
	var reqObj = ptGetXMLHTTP();
	reqObj.open("GET", "/ajax/taglist", true);
	reqObj.onreadystatechange = function() {
		if(reqObj.readyState == 4) {
			if(reqObj.status == 200) {
				callback(reqObj.responseXML);
			} else {
				alert("Failed to get tag information!.");
			}
		}
	}
	
	reqObj.send(null);
}

function ptGetTaggedPhotos(tagId, callback) {
	var reqObj = ptGetXMLHTTP();
	reqObj.open("POST", "/ajax/taggedphotos", true);
	reqObj.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	reqObj.onreadystatechange = function() {
		if(reqObj.readyState == 4) {
			if(reqObj.status == 200) {
				callback(reqObj.responseXML);
			} else {
				alert("Failed to get photo information!.");
			}
		}
	}
	
	reqObj.send("tag=" + tagId);
}

function ptGetPhotoInfo(photoId, callback) {
	var reqObj = ptGetXMLHTTP();

	reqObj.open("POST", "/ajax/photoinfo", true);
	reqObj.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	reqObj.onreadystatechange = function() {
		if(reqObj.readyState == 4) {
			if(reqObj.status == 200) {
				callback(reqObj.responseXML);
			} else {
				alert("Failed to get photo information!.");
			}
		}
	}
	
	reqObj.send("photo=" + photoId);
}

function ptGetTagAssignments(photoId, callback) {
	var reqObj = ptGetXMLHTTP();
	reqObj.open("POST", "/ajax/tagassignments", true);
	reqObj.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	reqObj.onreadystatechange = function() {
		if(reqObj.readyState == 4) {
			if(reqObj.status == 200) {
				callback(reqObj.responseXML);
			} else {
				alert("Failed to get tag information!.");
			}
		}
	}
	
	reqObj.send("photo=" + photoId);
}

function ptGetComments(photoId, callback) {
	var reqObj = ptGetXMLHTTP();
	
	reqObj.open("POST", "/ajax/comments", true);
	reqObj.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	reqObj.onreadystatechange = function() {
		if(reqObj.readyState == 4) {
			if(reqObj.status == 200) {
				callback(reqObj.responseXML);
			} else {
				alert("Failed to get Comment!.");
			}
		}
	}
	
	reqObj.send("photo=" + photoId);
}

function ptAddComment(photoId, name, text, callback) {
	var reqObj = ptGetXMLHTTP();
	
	reqObj.open("POST", "/ajax/comment", true);
	reqObj.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	reqObj.onreadystatechange = function() {
		if(reqObj.readyState == 4) {
			if(reqObj.status == 200) {
				callback(reqObj.responseXML);
			} else {
				alert("Failed to add Comment!.");
			}
		}
	}
	
	var encName = encodeURIComponent(name);
	var encText = encodeURIComponent(text);
	
	reqObj.send("photo=" + photoId + "&name=" + encName + "&text=" + encText);
}

function ptGetXMLHTTP() {
	try { return new ActiveXObject("Msxml2.XMLHTTP"); } catch (e) {}
	try { return new ActiveXObject("Microsoft.XMLHTTP"); } catch (e) {}
	try { return new XMLHttpRequest(); } catch(e) {}
	alert("XMLHttpRequest not supported.");
	return null;
}