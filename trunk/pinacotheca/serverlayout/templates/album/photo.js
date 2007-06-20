var nextPhoto;
var prevPhoto;
var currentPhotoId;
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
	var description;
	var date;
	
	if(descriptionElems[0].firstChild)
		description = descriptionElems[0].firstChild.nodeValue;
	else
		description = "-";
		
	var dateElems = xmlObj.getElementsByTagName("date");
	
	if(dateElems[0])
		date = dateElems[0].firstChild.nodeValue;
	else
		date = "-";
		
	nextPhoto   = xmlObj.getElementsByTagName("next")[0].firstChild.nodeValue;
	prevPhoto   = xmlObj.getElementsByTagName("prev")[0].firstChild.nodeValue;
	
	document.getElementById("photoName").innerHTML = name;
	document.getElementById("photoDescription").innerHTML = description;
	document.getElementById("photoDate").innerHTML = date;
	
	var thumbimg = document.getElementById("prevthumb");
	var thumbbox = document.getElementById("prevbox");
	
	if(prevPhoto == -1) {
		thumbbox.style.display = "none";
	} else {
		thumbimg.src = "/album/photo/thumb/" + prevPhoto;
		thumbbox.style.display = "block";
	}
	
	thumbimg = document.getElementById("nextthumb");
	thumbbox = document.getElementById("nextbox");
	
	if(nextPhoto == -1) {
		thumbbox.style.display = "none";
	} else {
		thumbimg.src = "/album/photo/thumb/" + nextPhoto;
		thumbbox.style.display = "block";
	}
	
	setMetadata(xmlObj);
}

function setTags(xmlObj) {
	var elements = xmlObj.getElementsByTagName("assignedtag");
	var tags = (elements.length > 0) ? "" : "-";
	
	for(i = 0; i < elements.length; i++) {
		if(i > 0) tags += ", ";
		tags += elements[i].getAttribute("name");
	}
	
	taglist = document.getElementById("tags");
	taglist.innerHTML = tags;
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

function showPhoto(photoId) {
	if(photoId == -1)
		return;
	
	var img = document.getElementById("photo");
	img.src = "/album/photo/small/" + photoId;
	var comments = document.getElementById("commentbox");
	
	comments.innerHTML = "";
	ptGetPhotoInfo(photoId, setPhotoData);
	ptGetTagAssignments(photoId, setTags);
	ptGetComments(photoId, setComments);
	currentPhotoId = photoId;
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