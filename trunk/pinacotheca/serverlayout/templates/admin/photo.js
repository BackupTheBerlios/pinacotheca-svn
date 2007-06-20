function getSelectedOptions(element) {
	options = element.options;
	selected = new Array();
	
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

function unassignSelected() {
	assignSel = document.getElementById("assignedtags");
	unassignSel = document.getElementById("unassignedtags");
	selected = getSelectedOptions(assignSel);
	
	for(i = 0; i < selected.length; i++) {
		addOptions(unassignSel, selected);
	}
}

function assignSelected() {
	assignSel = document.getElementById("assignedtags");
	unassignSel = document.getElementById("unassignedtags");
	selected = getSelectedOptions(unassignSel);
	
	for(i = 0; i < selected.length; i++) {
		addOptions(assignSel, selected);
	}
}

function setTagAssignments(xmlObj) {
	elements = xmlObj.getElementsByTagName("assignedtag");
	assignedSelect = document.getElementById("assignedtags");
	
	for(i = 0; i < elements.length; i++) {
		tagName = elements[i].getAttribute("name");
		tagId = elements[i].getAttribute("id");
		opt = document.createElement("option");
		opt.value = tagId;
		opt.innerHTML = tagName;
		assignedSelect.appendChild(opt);
	}
	
	elements = xmlObj.getElementsByTagName("unassignedtag");
	unassignedSelect = document.getElementById("unassignedtags");
	
	for(i = 0; i < elements.length; i++) {
		tagName = elements[i].getAttribute("name");
		tagId = elements[i].getAttribute("id");
		opt = document.createElement("option");
		opt.value = tagId;
		opt.innerHTML = tagName;
		unassignedSelect.appendChild(opt);
	}
}

function setComments(xmlObj) {
	var elements = xmlObj.getElementsByTagName("comment");
	var commentbox = document.getElementById("commentbox");
	
	for(i = 0; i < elements.length; i++) {
		var commentdiv = document.createElement("div");
		var commentdelete = document.createElement("p");
		var commentdeletelink = document.createElement("a");
		var commenthead = document.createElement("h3");
		var commenttext = document.createElement("p");
		
		commentdeletelink.href = "/admin/photo/deletecomment/" + elements[i].getAttribute("id");
		commentdeletelink.innerHTML = "delete";
		commentdelete.appendChild(commentdeletelink);
		commentdiv.className = "comment";
		var commentheadtext = document.createTextNode(elements[i].getAttribute("name") + " writes ...");
		var commenttexttext = document.createTextNode(elements[i].getAttribute("text"));
		commenthead.appendChild(commentheadtext);
		commenttext.appendChild(commenttexttext);
		commentdiv.appendChild(commenthead);
		commentdiv.appendChild(commentdelete);
		commentdiv.appendChild(commenttext);
		commentbox.appendChild(commentdiv);
	}
}

window.onload = function() {
	photoId = ptGetFileName(window.location.href);
	ptGetTagAssignments(photoId, setTagAssignments);
	ptGetComments(photoId, setComments);
}