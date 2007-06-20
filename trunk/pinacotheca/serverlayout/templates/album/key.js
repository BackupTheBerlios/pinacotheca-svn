function showAlbum() {
	var input = document.getElementById("key");
	
	window.location.href = "/album/show/" + input.value;
}