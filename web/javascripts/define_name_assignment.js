/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var completedTracker = [[],[],[]];
var fieldsCheckedStatus = [];
var index = 1;
var START = 1, END = 2, NUMBER = 3, DELIMITER = 4, BEFORE = 5, AFTER = 6,
	NEXT_NUMBER = 7, NEXT_LETTER = 8, NEXT_NOT_NUMBER = 9, NEXT_NOT_LETTER = 10;

$(document).ready(function() {
	for(var i=0; i<3; i++) {
		completedTracker[i][0] = 0;
	}

	initFields();
	setEmitters();
	checkFieldMatching();

	$(':Submit[value=Submit]').click(function(e) {
		var errors = getErrorMsg();
		if(errors.length > 0) {
			console.log('error in name assignment');
			e.preventDefault();
			var msg = "";
			for(var i=0; i<errors.length; i++) {
				msg += errors[i];
			}
			var div = document.getElementsByClassName('errorDiv');
			div[0].innerHTML = msg;
		}
	});
});

function getErrorMsg() {
	var errors = getLimitSelectionErrors(getTypeSelectionErrors([]));

	return errors;
}

function getTypeSelectionErrors(errors) {
	var radios = document.getElementsByName('type_1_1');

	for(var i=1; i<radios.length; i++) {
		if(fieldsCheckedStatus[i] === undefined) {
			errors.push(radios[i].value+', ');
		}
	}
	if(errors.length > 0) {
		errors.unshift('The following fields have not yet been accounted for in the photo name meta-data section:<br>');
		errors[errors.length-1] = errors[errors.length-1].substring(0,errors[errors.length-1].length-2);
		errors.push("<br><br>");
	}

	return errors;
}

function getLimitSelectionErrors(errors) {
	for(var i=1; i<fieldsCheckedStatus.length; i++) {
		if(fieldsCheckedStatus[i] === index) {
			errors.push('Make sure the start and end for the last photo name meta-data selection have been set.<br><br>');
		}
	}
	for(var i=1; i<index; i++) {
		var error = badLimitsCheck(i);
		if (error !== null) {
			errors.push(error);
		}
	}

	return errors;
}

function badLimitsCheck(index) {
	for(var i=NEXT_NUMBER; i<=NEXT_NOT_LETTER; i++) {
		if($('input[name=start_'+index+'][value='+i+']').prop('checked') &&
		  $('input[name=end_'+index+'][value='+i+']').prop('checked')) {
			return '<br>Photo name meta-data ('+index+') cannot have the same start and end point.';
		}
		if(index < (window.index-1) && $('input[name=end_'+index+'][value='+END+']').prop('checked')) {
			return 'Photo name meta-data ('+index+') is marked as ending at the end of the file name '+
				'but only the last photo name meta-data can have this ending.';
		}
	}

	if($('input[name=end_'+index+'][value='+BEFORE+']').prop('checked') &&
	  $('input[name=start_'+(index+1)+'][value='+AFTER+']').prop('checked')) {
		return 'Photo name meta-data ('+index+') is marked as ending before the start of meta-data ('+
			(index+1)+') but this meta-data is marked as ending before ('+index+')';
	}

	return null;
}

function initFields() {
	var fields = document.getElementsByName('type_1_1');
	for(var i=0; i<fields; i++) {
		fieldsCheckedStatus[i] = null;
	}
}

function setEmitters() {
	setTypeCheckedEmitter(index);
	setLimitsEmitter(index);
}

function setTypeCheckedEmitter(index) {
	var radios = document.getElementsByName('type_1_'+index);

	for(var i=0; i<radios.length; i++) {
		radios[i].onclick = function(e){
			if(fieldUnused(this)) {
				var event = new CustomEvent("type"+index, {'detail':{'elementNumber':this.title,'index':index}});
				document.dispatchEvent(event);
				document.removeEventListener('type'+index, typeFunction, false);
				setFieldToUsed(this);
			}
			else {
				e.preventDefault();
			}
		};
		document.addEventListener('type'+index, typeFunction, false);
	}
}

function fieldUnused(field) {
	var fields = document.getElementsByName('type_1_1');

	for(var i=1; i<fields.length; i++) {
		if(fields[i].value === field.value && fieldsCheckedStatus[i] !== undefined) return false;
	}
	return true;
}

function setFieldToUsed(field) {
	var fields = document.getElementsByName('type_1_1');

	for (var i=0; i<fields.length; i++) {
		if(fields[i].value === field.value) {
			var typeIndex = Number(field.name.substring(field.name.lastIndexOf("_")+1));
			setFieldToUnused(typeIndex);
			fieldsCheckedStatus[i] = typeIndex;
		}
	};
}

function setFieldToUnused(field) {
	for(var i=0; i<fieldsCheckedStatus.length; i++) {
		if(fieldsCheckedStatus[i] === field) {
			fieldsCheckedStatus[i] = undefined;
		}
	}
}

var typeFunction = function(event) {
	completedTracker[0][event.detail.index-1] = 1;

	checkForRows(event.detail.index);
};

function setLimitsEmitter(index) {
	var radios = Array.prototype.slice.call(document.getElementsByName('start_'+index));
	radios = radios.concat(Array.prototype.slice.call(document.getElementsByName('end_'+index)));

	for(var i=0; i<radios.length; i++) {
		radios[i].onclick = function(e) {
			var side = (this.name.indexOf('start')>-1 ? START : END);
			var side_text = (side === START) ? 'start' : 'end';
			if(validLimit(this,side,index)) {
				var event = new CustomEvent(side_text+index, {'detail':{'index':index,'type':this.value,'section':side}});
				document.dispatchEvent(event);
			}
			else {
				e.preventDefault();
			}
		};
	}

	setTextBoxLimitsEmitter(START,'start',index);
	setTextBoxLimitsEmitter(END,'end',index);

	document.addEventListener('start'+index, setLimitsFunction, false);
	document.addEventListener('end'+index, setLimitsFunction, false);
}

function validLimit(limit, side, index) {
	if(side === START) {
		switch(limit.value) {
			case NEXT_NUMBER:
			case NEXT_LETTER:
			case NEXT_NOT_NUMBER:
			case NEXT_NOT_LETTER:
				break;
			case AFTER:
				break;
			default:
				return true;
		}
	}
	else {
		switch(limit.value) {
			case NEXT_NUMBER:
				break;
			case NEXT_LETTER:
				break;
			case NEXT_NOT_NUMBER:
				break;
			case NEXT_NOT_LETTER:
				break;
			case AFTER:
				break;
			default:
				return true;
		}
	}
	return false;
}

function setTextBoxLimitsEmitter(side, side_text, index) {
	try{
		document.getElementsByName(side_text+'_'+NUMBER+'_'+index)[0].oninput = function() {
			this.value = this.value.replace(/[^0-9]/,'');
			var event = new CustomEvent(side_text+index, {'detail':{'index':index,'type':NUMBER,'section':side}});
			document.dispatchEvent(event);
		};
		document.getElementsByName(side_text+'_'+DELIMITER+'_'+index)[0].oninput= function() {
			var event = new CustomEvent(side_text+index, {'detail':{'index':index,'type':DELIMITER,'section':side}});
			document.dispatchEvent(event);
		};
	}
	catch(e) { console.log(e); }
}

var setLimitsFunction = function(event) {
	var section = Number(event.detail.section);

	switch(Number(event.detail.type)) {
		case START:
		case BEFORE:
		case AFTER:
		case END:
		case NEXT_NUMBER:
		case NEXT_LETTER:
		case NEXT_NOT_NUMBER:
		case NEXT_NOT_LETTER:
			completedTracker[section][event.detail.index-1] = 1;
			break;
		case NUMBER:
			checkNumberDeliminter(NUMBER, Number(event.detail.index), section);
			break;
		case DELIMITER:
			checkNumberDeliminter(DELIMITER, Number(event.detail.index), section);
			break;
	}

	checkForRows(event.detail.index);
};

function checkNumberDeliminter(type, index, tense) {
	var tense_text = (tense === START) ? 'start' : 'end';
	var checked = $('input[type=radio][name='+tense_text+'_'+index+'][value='+type+']').prop('checked');

	if(checked) {
		var text = $('input[type=text][name='+tense_text+'_'+type+'_'+index+']').val().length > 0;

		if(text){
			completedTracker[Number(tense)][Number(index)-1] = 1;
		}
		else {
			completedTracker[Number(tense)][Number(index)-1] = 0;
		}
	}
}

function checkForRows(index) {
	var completed = completedTracker[0][index-1] + completedTracker[1][index-1] + completedTracker[2][index-1];

	if(completed === 3) {
		if(window.index === index) {
			$('input[name=sectionCount]').val(index);
			window.index++;
			for(var i=0; i<3; i++) {
				completedTracker[i][index] = 0;
			}

			makeRow();

			setEmitters();
		}
	}
	else {
		var oldIndex = window.index;
		window.index = index;

		removeRows(oldIndex);
	}
	$('input[type=hidden][name=nameCount]').val(index);
}

function makeRow() {
	var newRow = "<div class='row_"+index+"'><div class='newRow'></div>"+
		     "<div class='meta-row'><div class='meta-col'>"+
		     "<h4>("+index+") Which type of meta-data?</h4>"+
		     radioFields()+
		     "</div><div class='meta-col'>"+
		     "<h4>Where does this section start?</h4>"+
		     "<input type='radio' name='start_"+index+"' value='"+NUMBER+"' > After "+
		     "<input type='text'  class='small_text_box' name='start_"+NUMBER+"_"+index+"'> characters (0 is the start, 1 is after the first character, etc...)<br>"+
		     "<input type='radio' name='start_"+index+"' value='"+DELIMITER+"' > After "+
		     "<input type='text' class='small_text_box' name='start_"+DELIMITER+"_"+index+"'> (Not including the delimiter character)<br>"+
		     "<input type='radio' name='start_"+index+"' value='"+AFTER+"'> Right after the end of the previous section<br>"+
		     "<input type='radio' name='start_"+index+"' value='"+NEXT_NUMBER+"'> The next digit character<br>"+
		     "<input type='radio' name='start_"+index+"' value='"+NEXT_LETTER+"'> The next letter character<br>"+
		     "<input type='radio' name='start_"+index+"' value='"+NEXT_NOT_NUMBER+"'> The next character that is not a digit<br>"+
		     "<input type='radio' name='start_"+index+"' value='"+NEXT_NOT_LETTER+"'> The next character that is not a letter<br>"+
		     "</div><div class='meta-col'>"+
		     "<h4>Where does this section end?</h4>"+
		     "<input type='radio' name='end_"+index+"' value='"+END+"'> The end of the file name (not including the extension)<br>"+
		     "<input type='radio' name='end_"+index+"' value='"+NUMBER+"'> After "+
		     "<input type='text' name='end_"+NUMBER+"_"+index+"' class='small_text_box'> characters from the start section"+
		     " (this number is the same as the length of this section in characters)<br>"+
		     "<input type='radio' name='end_"+index+"' value='"+DELIMITER+"'> Before "+
		     "<input type='text' name='end_"+DELIMITER+"_"+index+"' class='small_text_box'> "+
		     " (Not including the delmiter character)<br>"+
		     "<input type='radio' name='end_"+index+"' value='"+BEFORE+"'> Right before the start of the next section<br>"+
		     "<input type='radio' name='end_"+index+"' value='"+NEXT_NUMBER+"'> The next digit character<br>"+
		     "<input type='radio' name='end_"+index+"' value='"+NEXT_LETTER+"'> The next letter character<br>"+
		     "<input type='radio' name='end_"+index+"' value='"+NEXT_NOT_NUMBER+"'> The next character that is not a digit<br>"+
		     "<input type='radio' name='end_"+index+"' value='"+NEXT_NOT_LETTER+"'> The next character that is not a letter<br>"+
		     "</div>"+
			
		     "</div></div>";
	$('div[name=name-assignment]').append(newRow);
}

function radioFields() {
	var fields = document.getElementsByName("type_1_1");
	var buttons = "";

	for(var i=0; i<fields.length; i++) {
		buttons += "<input type='radio' name='type_1_"+index+"' value='"+fields[i].value+"'>"+
			(i>0?fields[i].value:'Not meta-data (use to help break name into sections)')+"<br>";
	}

	return buttons;
}

function removeRows(oldIndex) {
	// - remove the emitters above the new index
	// - remove the rows above the new index
	for(var i=oldIndex; i>index; i--) {
		document.removeEventListener('start'+i, setLimitsFunction, false);
		document.removeEventListener('end'+i, setLimitsFunction, false);

		setFieldToUnused(i);

		$('div.row_'+i).remove();
	}
}

function checkFieldMatching() {
	$('input[type=text][name=exampleInput]').on('input', function() {
		document.getElementsByName("example")[0].innerHTML = sectionText(this.value);
	});
}

function sectionText(text) {
	if(index === 1) return text;
	var types = [];
	var starts = [];
	var ends = [];

	for(var i=1; i<index; i++) {
		types[i-1] = $('input[name=type_1_'+i+']:checked').val();
		starts[i-1] = $('input[name=start_'+i+']:checked').val();
		ends[i-1] = $('input[name=end_'+i+']:checked').val();
	}
	
	return processText(text, starts, ends, getColorCodes(types));
}

function processText(text, starts, ends, colorCodes) {
	var processedTexts = [];
	var currIndex = 0;
	var endIndex = 0;
	for(var i=0; i<starts.length; i++) {
		var nextIndex = getSectionIndex(currIndex, starts[i], text, (i+1), 'start');
		if(endIndex >= 0) processedTexts.push(text.substring(currIndex, nextIndex));
		else {
			processedTexts.push("<span "+colorCodes[i-1]+">"+text.substring(currIndex,nextIndex)+"</span>");
		}

		currIndex = nextIndex;

		var endIndex = getSectionIndex(currIndex, ends[i], text, (i+1), 'end');
		if(endIndex > 0) {
			processedTexts.push("<span "+colorCodes[i]+">"+text.substring(currIndex,endIndex)+"</span>");
			currIndex = endIndex;
		}
	}

	if(currIndex<text.length) processedTexts.push(text.substring(currIndex,text.length));

	return processedTexts.join('');
}

function getSectionIndex(currIndex, start_end, text, index, tense) {
	switch(+start_end) {
		case START:
		case AFTER:
			return currIndex;
		case BEFORE:
			return -1;
		case NUMBER:
			return currIndex + Number($('input[name='+tense+'_'+NUMBER+'_'+index+']').val());
		case DELIMITER:
			return getDelimIndex(currIndex, text, index, tense);
		case END:
			var end = text.lastIndexOf(".");
			return (end > 0) ? end : text.length;
		case NEXT_NUMBER:
		case NEXT_LETTER:
		case NEXT_NOT_NUMBER:
		case NEXT_NOT_LETTER:
			return getNextIndex(currIndex, text, start_end);
	}
	return null;
}

function getNextIndex(currIndex, text, type) {
	var regex;
	switch(+type) {
		case NEXT_NUMBER:
			regex = /[0-9]/;
			break;
		case NEXT_LETTER:
			regex = /[a-zA-Z]/;
			break;
		case NEXT_NOT_NUMBER:
			regex = /[^0-9]/;
			break;
		case NEXT_NOT_LETTER:
			regex = /[^a-zA-Z]/;
			break;
	}

	var index = text.substring(currIndex).search(regex);
	return +currIndex + (index > -1 ? index : text.length-currIndex);
}

function getDelimIndex(currIndex, text, index, tense) {
	var delimiter = $('input[name='+tense+'_'+DELIMITER+'_'+index+']').val();
	return tense === 'end' ? text.indexOf(delimiter,currIndex) : text.indexOf(delimiter,currIndex)+1;
}

function getColorCodes(types) {
	var colorCodes = [];
	for(var i=0; i<types.length; i++) {
		if(types[i] === '') { colorCodes[i] = ''; continue; }
		var element = document.getElementsByName(types[i]);
		if(element[0] === undefined) {
			colorCodes[i] = "sytle='background:white'";
			continue;
		}
		colorCodes[i] = "style='background:"+element[0].style.background+"'";
	}

	return colorCodes;
}