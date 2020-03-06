/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package externalapi.db.helper;

import java.sql.PreparedStatement;
import java.sql.SQLException;


@FunctionalInterface
public interface StatementFunction<T> {
    T apply(PreparedStatement t) throws SQLException;
}
