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

$(document).ready(function() {
	var photoCount = Number($('input[name=photoCount]').val());
	var questionCount = Number($('input[name=questionCount]').val());

	addPhotoClickListeners(photoCount);
	addKeyboardGrading(questionCount);
	addTextConstraints();
	
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

function addTextConstraints() {
	var ints = $(".constraint_2");
	var decs = $(".constraint_3");
	console.log(ints[0]);

	ints.on('input',function() {
		console.log('in the thing');
		while(this.value.match(/[^0-9]/)) {
			this.value = this.value.substring(0,this.value.length-1);
		}
	});
	decs.on('input',function() {
		console.log('in the thing');
		while(this.value.match(/[^0-9\.]/) || this.value.indexOf(".") !== this.value.lastIndexOf(".")) {
			this.value = this.value.substring(0,this.value.length-1);
		}
	});
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

function addKeyboardGrading(questionCount) {
	var radioQuestionIndices = getRadioQuestionIndices(questionCount);
	var optionCounts = getOptionCounts(radioQuestionIndices);
	var onRow = 0;
	var keyRadios = [];
	var keyChecks = [];

	for(var i=0; i<radioQuestionIndices.length; i++) {
		if(optionCounts[i] < keyboardRows[onRow].length) {
			for(var j=0; j<optionCounts[i]; j++) {
				var span = document.getElementsByName(radioQuestionIndices[i]+"_"+j);
				var key = keyboardRows[onRow].shift();
				if(span[0].title === 'radio') {
					addKey(key, span[0].getAttribute('name'), keyRadios);
				} else {
					addKey(key, span[0].getAttribute('name'), keyChecks);
				}
				span[0].innerHTML = "<b>("+key+")</b>";
			}
			keyboardRows[onRow].reverse();
			onRow = (onRow + 1) % 3;
		}
	}
	setRadioListeners(keyRadios);
	setCheckListeners(keyChecks);
}

function setRadioListeners(keyRadios) {
	$(document).bind('keydown', {keys_radios : keyRadios}, function(e) {
		var unicode = e.keyCode || e.which;
		if(unicode === 13) $(':submit[value=Submit]').click();

		var key = String.fromCharCode(unicode);
		if($(':focus').attr('type') !== 'text') { 
			var keyRadios = e.data.keys_radios;

			for(var i=0; i<keyRadios.length; i++) {
				if(key.toLowerCase() === keyRadios[i][0]) {
					keyRadios[i][1].prop('checked',true);
				}
			}
		}
		changeRadius(unicode);
	});
}

function setCheckListeners(keyChecks) {
	$(document).bind('keydown',{key_checks : keyChecks}, function(e) {
		var unicode = e.keyCode || e.wich;
		var key = String.fromCharCode(unicode);
		if($(':focus').attr('type') !== 'text') { 
			var keyChecks = e.data.key_checks;

			for(var i=0; i<keyChecks.length; i++) {
				if(key.toLowerCase() === keyChecks[i][0]) {
					keyChecks[i][1].prop('checked',!keyChecks[i][1].prop('checked'));
				}
			}
		}
	});
}

function addKey(key, title, keys) {
	keys.push([key , $('input[title='+title+']')]);
}

function getOptionCounts(radioQuestionIndices) {
	var optionCounts = [];

	for(var i=0; i<radioQuestionIndices.length; i++) {
		optionCounts.push($('input[name=question_'+i+']').val());
	}

	return optionCounts;
}

function getRadioQuestionIndices(questionCount) {
	var radioQuestionIndices = [];

	for(var i=0; i<questionCount; i++) {
		if(isRadioQuestion(i)) {
			radioQuestionIndices.push(i);
		}
	}

	return radioQuestionIndices;
}

function isRadioQuestion(index) {
	var questionTypeConstraint = $('input[name=question_'+index+']').prop('title');
	var type = Number(questionTypeConstraint.substring(0,1));
	return type === 2 || type === 3;
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


