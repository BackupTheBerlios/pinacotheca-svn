var nextPhoto;

function showLastPhoto() {
  alert("Last photo of this album");
}

function setPhotoData(xmlObj) {
	nextPhoto = xmlObj.getElementsByTagName("next")[0].firstChild.nodeValue;
}

function showNextPhoto() {
	window.setTimeout("showPhoto(nextPhoto)", 5000);
}

function showPhoto(photoId) {
	if(photoId == -1) {
		showLastPhoto();
		return;
	}
	ptGetPhotoInfo(photoId, setPhotoData);
	img = document.getElementById("photo");
	img.src = "/album/photo/small/" + photoId;
}


window.onload = function() {
	currentPhotoId = ptGetFileName(location.href);
	img = document.getElementById("photo");
	img.onload = function() {
		showNextPhoto()
	}
	showPhoto(currentPhotoId);
}