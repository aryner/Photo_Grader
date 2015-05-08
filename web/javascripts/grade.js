/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function() {
	var photoCount = Number($('input[name=photoCount]').val());
	var questionCount = Number($('input[name=questionCount]').val());

	addPhotoClickListeners(photoCount);

	$('input[type=submit][value=Submit]').click(function(e) {
		e.preventDefault();

		var errors = checkInput(questionCount);
		if(errors.length > 0) {
			e.preventDefault();

			var errorMsg = "";
			for(var i=0; i<errors.length; i++) {
				errorMsg += "<span class='error'>"+errors[i]+"</span><br>";
			}

			var div = document.getElementsByClassName('errorDiv');
			div[0].innerHTML = errorMsg;
		}
	});
});

function addPhotoClickListeners(photoCount) {
	for(var i=0; i<photoCount; i++) {
		var img = $('img[name=photo_'+i+']');
		addPhotoClickListener(img);
	}
}

function addPhotoClickListener(img) {
	var src = img.prop('src');

	img.click(function() {
		$("body").append("<img class='examineImg' src='"+src+"'>");
		$('.examineImg').fadeIn("fast");

		$('.examineImg').click(function() {
			$('.examineImg').remove();
		});
	});
}

function checkInput(questionCount) {
	return ['blah','blah again'];
}
