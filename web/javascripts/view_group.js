/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function() {
	var photoCount = Number($('input[name=photoCount]').val());
	addPhotoClickListeners(photoCount);

	$(document).bind('keydown', function(e) {
		var unicode = e.keyCode || e.which;

		if($(':focus').attr('type') !== 'text') { 
			//right arrow
			if(unicode === 37) {
				$(':submit[name=prev]').click();
			}
			//left arrow
			if(unicode === 39) {
				$(':submit[name=next]').click();
			}
		}
	});


	$('input[type=submit][value="Delete Image From Program"]').click(function(e) {
		var result = window.confirm("Are you sure you want to delte this photo from the program?");
		if(result === false) { e.preventDefault(); }
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


