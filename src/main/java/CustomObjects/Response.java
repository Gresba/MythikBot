package CustomObjects;

public class Response {
    private boolean deleteTriggerMsg;
    private boolean contains;
    private String response;
    private final String triggerString;
    public Response(boolean deleteTriggerMsg, boolean contains, String response, String triggerString) {
        this.deleteTriggerMsg = deleteTriggerMsg;
        this.contains = contains;
        this.response = response;
        this.triggerString = triggerString;
    }

    public boolean isDeleteTriggerMsg() {
        return deleteTriggerMsg;
    }
    public boolean isContains() {return contains;}
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
    public String getTriggerString() {return triggerString; }
    public void setDeleteTriggerMsg(boolean deleteTriggerMsg) {
        this.deleteTriggerMsg = deleteTriggerMsg;
    }
    public void setContains(boolean contains) {
        this.contains = contains;
    }
}
