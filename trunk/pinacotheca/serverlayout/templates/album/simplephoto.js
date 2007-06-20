var nextPhoto;
var prevPhoto;
var metadataVisible = false;

function setMetadata(responseXML) {
	var metadataObj = document.getElementById("metadata");
	var elements = responseXML.getElementsByTagName("metadata");
	var metadatalink = document.getElementById("metadatalink");
	
	while(metadataObj.hasChildNodes()) {
		child = metadataObj.firstChild;
		metadataObj.removeChild(child);
	}
	
	if(elements.length > 0) {
		metadatalink.style.visibility = "visible";
	} else {
		metadatalink.style.visibility = "hidden";
	}
	
	for(i = 0; i < elements.length; i++) {
		var element = elements[i];
		var dtObj = document.createElement("dt");
		var ddObj = document.createElement("dd");
		
		dtObj.innerHTML = element.getAttribute("type");
		if(element.firstChild) {
			ddObj.innerHTML = element.firstChild.nodeValue;
		}
		
		metadataObj.appendChild(dtObj);
		metadataObj.appendChild(ddObj);
	}
}

function toggleAdditionalMetadata() {
	var metadata = document.getElementById("metadata");
	
	if(!metadataVisible)
		metadata.style.display = "block";
	else
		metadata.style.display = "none";
	
	metadataVisible = !metadataVisible;
}

function setPhotoData(xmlObj) {
	var name = xmlObj.getElementsByTagName("name")[0].firstChild.nodeValue;
	var descriptionElems = xmlObj.getElementsByTagName("description");
	
	if(descriptionElems[0].firstChild)
		var description = descriptionElems[0].firstChild.nodeValue;
	else
		var description = "-";
		
	var dateElems = xmlObj.getElementsByTagName("date");
	
	if(dateElems[0])
		var date = dateElems[0].firstChild.nodeValue;
	else
		var date = "-";
	
	document.getElementById("photoName").innerHTML = name;
	document.getElementById("photoDescription").innerHTML = description;
	document.getElementById("photoDate").innerHTML = date;
	
	setMetadata(xmlObj);
}

function setTags(xmlObj) {
	var elements = xmlObj.getElementsByTagName("assignedtag");
	var tags = (elements.length > 0) ? "" : "-";
	
	for(i = 0; i < elements.length; i++) {
		if(i > 0) tags += ", ";
		tags += elements[i].getAttribute("name");
	}
	
	var taglist = document.getElementById("tags");
	taglist.innerHTML = tags;
}

function showPhoto(photoId) {
	if(photoId == -1)
		return;
		
	var img = document.getElementById("photo");
	img.src = "/album/photo/small/" + photoId;
	
	ptGetPhotoInfo(photoId, setPhotoData);
	ptGetTagAssignments(photoId, setTags);
	ptGetComments(photoId, setComments);
}

function setComments(xmlObj) {
	var elements = xmlObj.getElementsByTagName("comment");
	var commentbox = document.getElementById("commentbox");
	
	for(i = 0; i < elements.length; i++) {
		var commentdiv = document.createElement("div");
		var commenthead = document.createElement("h2");
		var commenttext = document.createElement("p");
		
		commentdiv.className = "comment";
		var commentheadtext = document.createTextNode(elements[i].getAttribute("name") + " writes ...");
		var commenttexttext = document.createTextNode(elements[i].getAttribute("text"));
		commenthead.appendChild(commentheadtext);
		commenttext.appendChild(commenttexttext);
		commentdiv.appendChild(commenthead);
		commentdiv.appendChild(commenttext);
		commentbox.appendChild(commentdiv);
	}
}

function addComment() {
	var name = document.getElementById("commentname").value;
	var comment = document.getElementById("commenttext").value;
	
	ptAddComment(currentPhotoId, name, comment, setComments);
}

window.onload = function() {
	currentPhotoId = ptGetFileName(location.href);
	showPhoto(currentPhotoId);
}