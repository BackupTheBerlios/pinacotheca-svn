var tags = new Array();
var idCount = new Array();

function getImage(xmlObj) {
	var id = xmlObj.getAttribute("id");
	var fileName = xmlObj.getAttribute("name");
	
	var div = document.createElement("div");
	div.className = "photothumbnail";
	div.id = "thumb" + id;
	var link = document.createElement("a");
	link.href = "/album/show/simplephoto/" + id;
	var img = document.createElement("img");
	img.src = "/album/photo/thumb/" + id;
	img.alt = "fileName";
	img.className = "photothumbnail";
	link.appendChild(img);
	div.appendChild(link);
	div.appendChild(document.createElement("br"));
	div.innerHTML += fileName;
	return div;
}

function setPhotos(xmlObj) {
	var elements = xmlObj.getElementsByTagName("photo");
	var tagId = xmlObj.getElementsByTagName("responsedata")[0].getAttribute("id");
	var photolist = document.getElementById("tagphotolist");

	for(i = 0; i < elements.length; i++) {
		element = elements[i];
		id = element.getAttribute("id");
		tags[tagId].push(id);
		
		if(document.getElementById("thumb" + id)) {
			idCount[id]++;
			continue;
		}
		
		img = getImage(element);
		photolist.appendChild(img);
		idCount[id] = 1;
	}
}

function addToFilter() {
	var unapplied = document.getElementById("unapplied");
	var newtag = null;

	for(i = 0; i < unapplied.options.length; i++) {
		if(unapplied.options[i].selected)
			newtag = unapplied.options[i];
	}

	if(!newtag)
		return;
	
	tags[newtag.value] = new Array();
	ptGetTaggedPhotos(newtag.value, setPhotos);
	var applied = document.getElementById("applied");
	applied.appendChild(newtag);
}

function removeFromFilter() {
	var applied = document.getElementById("applied");
	var removetag = null;
	
	for(i = 0; i < applied.options.length; i++) {
		if(applied.options[i].selected)
			removetag = applied.options[i];
	}
	
	if(!removetag)
		return;
	
	var tagId = removetag.value;
	var photolist = document.getElementById("tagphotolist");
	var cnt = tags[tagId].length;
	
	while(cnt-- > 0) {
		id = tags[tagId].pop();
		idCount[id]--;
		if(idCount[id] == 0) {
			elem = document.getElementById("thumb" + id);
			if(elem)
				photolist.removeChild(elem);
		}
	}
	
	var unapplied = document.getElementById("unapplied");
	unapplied.appendChild(removetag);
}