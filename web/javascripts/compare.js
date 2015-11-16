/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



var number_of_mms = 3;
var xScale = 1;
var yScale = 1;
var radius;

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
	if(back_pressed.value === '1') { 
		alert('You cannot use the back button while comparing.');
		window.location.replace('home'); 
	}
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

	img.click(function(e) {
		startExamine(src);

		var image = $('.examineImg');
		var circle = $('#circle');
		setScale(image[0],circle);

		var xp = e.pageX, yp = e.pageY;
		$(window).mousemove(function(e){
			xp = e.clientX;
			yp = e.clientY;
		});
		setInterval(function() {
			circle.css("top",yp-(xScale*30)).css("left",xp-(xScale*28));
		},30);

		window.addEventListener('resize', function(e) {
			var image = $('.examineImg');
			var circle = $('#circle');
			setScale(image[0],circle);
		});

		$(document).bind('keydown', function(e) {
			var unicode = e.keyCode || e.which;
			if(unicode>=49 && unicode<=57) {
				var image = $('.examineImg');
				var circle = $('#circle');
				setScale(image[0],circle);
			}

			if(unicode === 27) {
				endExamine();
			}
		});

		$('#circle').click(endExamine);
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
		//g
		else if (unicode === 71) {
			choices[3].checked = true;
		}

		changeRadius(unicode);
	});
}

function startExamine(src) {
	$("body").append("<img class='examineImg' src='"+src+"'>");
	$('body').append("<div id='circle'></div>");
}

function endExamine() {
	$('.examineImg').remove();
	$('#circle').remove();
}

function setScale(image,circle) {
	xScale = image.clientWidth / 1865;
	//yScale = image.clientHeight /1399;
	yScale = window.innerHeight / 868;
	radius = 19 * number_of_mms * xScale;
	circle.css("width",radius).css("height",radius).css("border-radius",radius);
}

function changeRadius(unicode) {
	switch (unicode) {
		case 50:
			number_of_mms = 2;
			break;
		case 51:
			number_of_mms = 3;
			break;
		case 52:
			number_of_mms = 4;
			break;
		case 53:
			number_of_mms = 5;
			break;
	}
}
