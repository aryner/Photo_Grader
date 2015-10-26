/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var number_of_mms = 3;
var xScale = 1;
var yScale = 1;
var radius;

var keyboardRows = [['q','w','e','r','t','y','u','i','o','p'],
		    ['a','s','d','f','g','h','j','k','l'],
		    ['z','x','c','v','b','n','m']];

$(document).ready(function(){
	var metaCount = Number($('input[name=meta_count]').val());

	addPhotoClickListener();
	addKeyboardAssignmentListeners(metaCount);

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
		changeRadius(unicode);
	});
});

function addPhotoClickListener() {
	var img = $('.gradeImg');
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

function addKeyboardAssignmentListeners(metaCount) {
	var checkRadioIndices = getCheckRadioMetaAttributes(metaCount);
	var optionCounts = getOptionCounts(checkRadioIndices);
	var onRow = 0;
	var keyRadios = [];

	for(var i=0; i<checkRadioIndices.length; i++) {
		if(optionCounts[i] < keyboardRows[onRow].length) {
			for(var j=0; j<optionCounts[i]; j++) {
				var span = document.getElementsByClassName('span_'+j+"_"+checkRadioIndices[i]);
				var key = keyboardRows[onRow].shift();
				keyRadios.push([key,$('#'+j+'.'+checkRadioIndices[i])]);
				span[0].innerHTML = "<b>("+key+")</b>";
			}
			keyboardRows[onRow].reverse();
			onRow = (onRow + 1) % 3;
		}
	}
	setListeners(keyRadios);
}

function setListeners(keyRadios) {
	$(document).bind('keydown', {keys_radios : keyRadios}, function(e) {
		var unicode = e.keyCode || e.which;
		if(unicode === 13) $(':submit[value=Submit]').click();
		var key = String.fromCharCode(unicode);
		if($(':focus').attr('type') !== 'text') { 
			var keyRadios = e.data.keys_radios;

			for(var i=0; i<keyRadios.length; i++) {
				if(key.toLowerCase() === keyRadios[i][0]) {
					keyRadios[i][1].prop('checked',(keyRadios[i][1].prop('checked')?false:true));
				}
			}
		}
	});
}

function getCheckRadioMetaAttributes(metaCount) {
	var checkRadioIndices = [];

	for(var i=0; i<metaCount; i++) {
		if(isCheckRadio(i)) {
			checkRadioIndices.push(i);
		}
	}
	return checkRadioIndices;
}

function isCheckRadio(index) {
	var meta = Number($('.'+index).prop('title'));
	return meta === 2 || meta === 3;
}

function getOptionCounts(checkRadioIndices){
	var counts = [];
	for (var i=0; i<checkRadioIndices.length; i++) {
		counts[i] = $('.'+checkRadioIndices[i]).length;
	}
	return counts;
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


