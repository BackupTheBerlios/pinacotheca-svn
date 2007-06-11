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

function ptGetFileName(filename) {
    var re = new RegExp(/([^\/\\]+)$/);
    var m = re.exec(filename);
    if (m == null) {
    	return null;
    } else {
    	return m[0];
    }
}

function getSelectedOptions(element) {
	var options = element.options;
	var selected = new Array();
	
	for(i = 0; i < options.length; i++) {
		if(options[i].selected) {
			selected.push(options[i]);
		}
	}
	return selected;
}

function addOptions(selectElem, elements) {
	for(i = 0; i < elements.length; i++) {
		selectElem.appendChild(elements[i]);
	}
}

function ptUnassignSelected() {
	var assignSel = document.getElementById("assignedtags");
	var unassignSel = document.getElementById("unassignedtags");
	var selected = getSelectedOptions(assignSel);
	
	for(i = 0; i < selected.length; i++) {
		addOptions(unassignSel, selected);
	}
}

function ptAssignSelected() {
	var assignSel = document.getElementById("assignedtags");
	var unassignSel = document.getElementById("unassignedtags");
	var selected = getSelectedOptions(unassignSel);
	
	for(i = 0; i < selected.length; i++) {
		addOptions(assignSel, selected);
	}
}

function ptSetTagAssignments(photoId) {
	var reqObj = ptGetXMLHTTP();
	
	reqObj.open("POST", "/ajax/tagassignment", false);
	reqObj.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	reqObj.send("photo=" + photoId);
	
	if(reqObj.status != 200) {
		alert("Failed to get tag data for photo.");
		return;
	}
	
	var elements = reqObj.responseXML.getElementsByTagName("assignedtag");
	var assignedSelect = document.getElementById("assignedtags");
	
	for(i = 0; i < elements.length; i++) {
		var tagName = elements[i].getAttribute("name");
		var tagId = elements[i].getAttribute("id");
		var opt = document.createElement("option");
		opt.value = tagId;
		var optVal = document.createTextNode(tagName);
		opt.appendChild(optVal);
		assignedSelect.appendChild(opt);
	}
	
	elements = reqObj.responseXML.getElementsByTagName("unassignedtag");
	var unassignedSelect = document.getElementById("unassignedtags");
	
	for(i = 0; i < elements.length; i++) {
		var tagName = elements[i].getAttribute("name");
		var tagId = elements[i].getAttribute("id");
		var opt = document.createElement("option");
		opt.value = tagId;
		var optVal = document.createTextNode(tagName);
		opt.appendChild(optVal);
		unassignedSelect.appendChild(opt);
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
	var photoId = ptGetFileName(window.location.href);
	ptSetTagAssignments(photoId);
}