/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.common;

/**
 *
 * @author cuong
 */
public class FailedResponse {
    public final String error;

    public FailedResponse(String error) {
        this.error = error;
    }
}