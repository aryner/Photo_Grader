/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.sql.*;

/**
 *
 * @author aryner
 */
public abstract class Model {
	public abstract Model getModel(ResultSet resultSet);
}
