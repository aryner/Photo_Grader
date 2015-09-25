/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utilities;

/**
 *
 * @author aryner
 */
public class ColorPicker {
	private final String [] colors;
	private int index;

	public ColorPicker() {
		index = -1;

		colors = new String[13];
		colors[0] = "aqua";
		colors[1] = "red";
		colors[2] = "lime";
		colors[3] = "orange";
		colors[4] = "fuchsia";
		colors[5] = "yellow";
		colors[6] = "gray";
		colors[7] = "green";
		colors[8] = "maroon";
		colors[9] = "olive";
		colors[10] = "purple";
		colors[11] = "teal";
		colors[12] = "silver";
	}

	public String nextColor() {
		index = (index+1) % 13;
		return colors[index];
	}

	public String nextBackgroundColor() {
		return "style='background:"+nextColor()+"'";
	}
}
