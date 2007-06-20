function setTags(xmlObj) {
	tagSel = document.getElementById("tagid");
	tags = xmlObj.getElementsByTagName("tag");
	
	for(i = 0; i < tags.length; i++) {
		tagId = tags[i].getAttribute("id");
		tagVal = tags[i].getAttribute("name");
		opt = document.createElement("option");
		opt.value = tagId;
		opt.innerHTML = tagVal;
		tagSel.appendChild(opt);
	}
}

window.onload = function() {
	ptGetTags(setTags);
}