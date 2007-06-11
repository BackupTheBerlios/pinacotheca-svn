function ptGetTags() {
	var tagSelect = document.getElementById("tagid");
	var reqObj = ptGetXMLHTTP();
	
	reqObj.open("GET", "/ajax/taglist", false);
	reqObj.send(null);
	
	var tagSel = document.getElementById("tagid");
	var tags = reqObj.responseXML.getElementsByTagName("tag");
	
	for(i = 0; i < tags.length; i++) {
		var tagId = tags[i].getAttribute("id");
		var tagVal = tags[i].getAttribute("name");
		var opt = document.createElement("option");
		opt.value = tagId;
		var optVal = document.createTextNode(tagVal);
		opt.appendChild(optVal);
		tagSel.appendChild(opt);
	}
}

function ptHandleTagAssignment(albumId) {
	var elements = document.getElementsByTagName("input");
	var checked = new String();
	var hasValues;
	
	for(i = 0; i < elements.length; i++) {
		if(elements[i].type != "checkbox" || !elements[i].checked) continue;
		var name = elements[i].name;
		name = name.substring(name.length - 1);
		checked += (i == 0) ? name : "," + name;
		hasValue = true;
	}
}

function ptGetXMLHTTP() {
	try { return new ActiveXObject("Msxml2.XMLHTTP"); } catch (e) {}
	try { return new ActiveXObject("Microsoft.XMLHTTP"); } catch (e) {}
	try { return new XMLHttpRequest(); } catch(e) {}
	alert("XMLHttpRequest not supported.");
}

window.onload = function() {
	ptGetTags();
}