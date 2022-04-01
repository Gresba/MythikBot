package Bot;

import java.util.HashMap;

public class Response {
    private boolean deleteResponse;
    private boolean deleteTriggerMsg;
    private String response;
    private int deleteDelay;

    public Response(boolean deleteResponse, boolean deleteTriggerMsg, String response, int deleteDelay) {
        this.deleteResponse = deleteResponse;
        this.deleteTriggerMsg = deleteTriggerMsg;
        this.response = response;
        this.deleteDelay = deleteDelay;
    }

    public boolean isDeleteResponse() {
        return deleteResponse;
    }

    public void setDeleteResponse(boolean deleteResponse) {
        this.deleteResponse = deleteResponse;
    }

    public boolean isDeleteTriggerMsg() {
        return deleteTriggerMsg;
    }

    public void setDeleteTriggerMsg(boolean deleteTriggerMsg) {
        this.deleteTriggerMsg = deleteTriggerMsg;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getDeleteDelay() {
        return deleteDelay;
    }

    public void setDeleteDelay(int deleteDelay) {
        this.deleteDelay = deleteDelay;
    }
}
