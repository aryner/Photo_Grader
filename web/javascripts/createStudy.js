/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function() {
	$(':submit[value=Submit]').on('click',function(e) {
		e.preventDefault();
		var maxCount = +($('input[type=hidden][name=maxCount]').val());
		var actualCount = maxCount;
		var focus = false;

		for(var i=0; i<maxCount; i++) {
			var name = $('input[type=text][name=name'+i+']').val();

			if(name.trim() === '') { $('div[name=error'+i+']').addClass('hidden'); actualCount--; continue; }

			var type = $('input[name=type'+i+'][value=1]').prop('checked') 
				|| $('input[name=type'+i+'][value=2]').prop('checked')
				|| $('input[name=type'+i+'][value=3]').prop('checked');
			var collect = $('input[name=collect'+i+'][value=1]').prop('checked')
				|| $('input[name=collect'+i+'][value=2]').prop('checked')
				|| $('input[name=collect'+i+'][value=3]').prop('checked')
				|| $('input[name=collect'+i+'][value=4]').prop('checked');

			if(!type || !collect) {
				e.preventDefault();
				$('div[name=error'+i+']').removeClass('hidden');
				if(!focus) { $('input[name=name'+i+']').focus(); focus = true; }
			}
			else { $('div[name=error'+i+']').addClass('hidden'); }
		}
	});
});