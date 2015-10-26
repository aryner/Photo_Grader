/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var number_of_mms = 3;
var xScale = 1;
var yScale = 1;
var radius;

$(document).ready(function() {
	var photoCount = Number($('input[name=photoCount]').val());
	addPhotoClickListeners(photoCount);

	$(document).bind('keydown', function(e) {
		var unicode = e.keyCode || e.which;

		if($(':focus').attr('type') !== 'text') { 
			switch (unicode) {
				case 37:
					$(':submit[name=prev]').click();
					break;
				case 39:
					$(':submit[name=next]').click();
					break;
			}
		}
		changeRadius(unicode);
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
	radius = 27 * number_of_mms * xScale;
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


