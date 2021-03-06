/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package httpserver.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 *
 * @author cuong
 */
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class FailedResponse extends BaseResponse {
    public final String error;

    public FailedResponse(String error) {
        this.error = error;
    }
}
