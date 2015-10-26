/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
var number_of_mms = 3;
var scale = 1;
var yDiff = 0;

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
		$("body").append("<img class='examineImg' src='"+src+"'>");
		$('body').append("<div id='circle'></div>");
		var image = $('.examineImg');
		var circle = $('#circle');

		scale = image[0].clientWidth / 1865;
		yDiff = scale* 1920 - window.innerWidth;
		var radius = 27 * number_of_mms * scale;
		circle.css("width",radius).css("height",radius).css("border-radius",radius);

		var xp = e.pageX, yp = e.pageY;
		$(window).mousemove(function(e){
			xp = e.clientX;
			yp = e.clientY;
		});
		setInterval(function() {
			console.log(yDiff);
			circle.css("top",yp-(scale*615)+yDiff*10).css("left",xp-(scale*28));
		},30);

		window.addEventListener('resize', function(e) {
			var image = $('.examineImg');
		var circle = $('#circle');
			scale = image[0].clientWidth / 1865;
			yDiff = scale*1920 - window.innerWidth;
			radius = 27 * number_of_mms * scale;
			circle.css("width",radius).css("height",radius).css("border-radius",radius);
		});

		$(document).bind('keydown', function(e) {
			if(+e.keyCode>=49 && +e.keyCode<=57) {
				var circle = $('#circle');
				var radius = 27 * number_of_mms * scale;
				circle.css("width",radius).css("height",radius).css("border-radius",radius);
			}

			if(e.keyCode === 27) {
				$('.examineImg').remove();
				$('#circle').remove();
			}
		});

		$('#circle').click(function() {
			$('.examineImg').remove();
			$('#circle').remove();
		});
	});
}


