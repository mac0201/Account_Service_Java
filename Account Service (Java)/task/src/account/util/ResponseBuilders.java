package account.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

public class ResponseBuilders {

    @Data
    @Builder
//    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorResponse {
        private int status;
        private String error;
        private String message;
        private String path;
    }

    @Data
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class SuccessResponse {
        private String user;
        private String status;
    }

}
