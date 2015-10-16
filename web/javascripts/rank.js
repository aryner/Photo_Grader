/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var keys = ['s','d','f','t'];

$(document).ready(function() {
	var photoCount = Number($('input[name=photoCount]').val());

	addPhotoClickListeners(photoCount);
	addKeyboardRanking();

	$('input[type=submit][value=Submit]').click(function(e) {
		console.log('submitting form');
		e.preventDefault();

		var errors = checkInput();
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

function checkInput() {
	return [];
}

function addPhotoClickListeners(photoCount) {
	for(var i=0; i<photoCount; i++) {
		var img = $('img[name=photo_'+i+']');
		addPhotoClickListener(img);
	}
}

function addPhotoClickListener(img) {
	var src = img.prop('src');

	img.click(function() {
		console.log('click');
		$("body").append("<img class='examineImg' src='"+src+"'>");
		$('.examineImg').fadeIn("fast");

		$('.examineImg').click(function() {
			$('.examineImg').remove();
		});
	});
}

function addKeyboardRanking() {
	var choices = document.getElementsByName("compare");

	for (var i=0; i< choices.length; i++) {
		console.log(choices[i].value);
	}

	$(document).bind('keydown',{choices:choices},function(e) {
		var unicode = e.keyCode || e.which;
		//return || t
		if(unicode === 13 || 84) $(':submit[value=Submit]').click();

		//s
		if(unicode === 83) {
			choices[0].checked = true;
		} 
		//d
		else if (unicode === 68) {
			choices[1].checked = true;
		} 
		//f
		else if (unicode === 70) {
			choices[2].checked = true;
		}
	});
}
