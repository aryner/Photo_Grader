/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var back_pressed;
window.onpopstate = function(e) {
	check_for_back();
};


$(document).ready(function() {
	back_pressed = document.getElementById('back_pressed');

	var photoCount = Number($('input[name=photoCount]').val());

	addPhotoClickListeners(photoCount);
	addKeyboardRanking();

	$('input[type=submit][value=Submit]').click(function(e) {

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
		else { mark_page(); }
	});
});

function check_for_back() {
	if(back_pressed.value === '1') { window.location.replace('home'); }
}

function mark_page() {
	back_pressed.value = '1';
}

function checkInput() {
	var errors = [];
	var buttons = document.getElementsByName("compare");

	var oneChecked = false;
	for(var i=0; i<buttons.length && !oneChecked; i++) {
		oneChecked = buttons[i].checked;
	}
	if(!oneChecked) {
		errors.push('You must select which group is worse, or select \'Equal\' if they are the same.');
	}

	return errors; 
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
		$("body").append("<img class='examineImg' src='"+src+"'>");
		$('.examineImg').fadeIn("fast");

		$('.examineImg').click(function() {
			$('.examineImg').remove();
		});
	});
}

function addKeyboardRanking() {
	var choices = document.getElementsByName("compare");

	$(document).bind('keydown',{choices:choices},function(e) {
		var unicode = e.keyCode || e.which;
		//return || t
		if(unicode === 13 || unicode === 84) { $(':submit[value=Submit]').click(); }

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
