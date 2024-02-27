package server.ResponseTypes;


public class BadRequestResponse extends Response {
    public BadRequestResponse(String message) {
        this.message = message;
        this.success = false;
    }



}
