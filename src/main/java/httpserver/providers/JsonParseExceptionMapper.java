package httpserver.providers;

import com.fasterxml.jackson.core.JsonParseException;
import httpserver.common.FailedResponse;
import javax.annotation.Priority;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
@Priority(1)
public class JsonParseExceptionMapper implements ExceptionMapper<JsonParseException> {

    @Override
    public Response toResponse(JsonParseException e) {
        return Response
            .status(Response.Status.BAD_REQUEST)
            .type(MediaType.APPLICATION_JSON)
            .entity(new FailedResponse(e.toString()))
            .build();
    }
}