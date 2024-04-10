package webSocketMessages.serverMessages;

import java.util.Objects;

public final class Error extends ServerMessage {
    private final String errorMessage;

    public Error(String errorMessage) {
        super(ServerMessageType.ERROR);
        this.errorMessage = errorMessage;
    }

    public String errorMessage() {
        return errorMessage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Error) obj;
        return Objects.equals(this.errorMessage, that.errorMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(errorMessage);
    }

    @Override
    public String toString() {
        return "Error[" +
                "errorMessage=" + errorMessage + ']';
    }

}
