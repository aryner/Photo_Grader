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
	var errors = [];

	for(var i=0; i<questionCount; i++) {
		var meta = $('input[name=question_'+i+']');
		var optionCount = Number(meta.val());
		var label = meta.prop('id');
		var type_constraint = meta.prop('title');
		var type = Number(type_constraint.substring(0,type_constraint.indexOf("_")));
		var constraint = Number(type_constraint.substring(type_constraint.indexOf("_")+1));

		var error = checkQuestion(label, type, optionCount, constraint);
		if (error !== undefined) errors.push(error);
	}

	return errors;
}

function checkQuestion(label, type, optionCount, constraint) {
	if (constraint < 0) return;

	switch(type) {
		//text
		case 1:
			if($('input[name='+label+']').val().length === 0)
				return "Make sure no text inputs are left empty";
			break;
		//radio
		case 2:
			var radios = document.getElementsByName(label);
			var selected = false;
			for(var i=0; i<optionCount && !selected; i++) {
				if(radios[i].checked)
					selected = true;
			}
			if(!selected)
				return "Make sure each radio button question has an answer";
			break;
		//check
		case 3:
			break;
	}
}