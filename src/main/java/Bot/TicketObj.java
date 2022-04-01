package Bot;

public class TicketObj {
    private String memberID;
    private String ticketStatus;
    private String orderID;

    public TicketObj(String memberID, String ticketStatus) {
        this.memberID = memberID;
        this.ticketStatus = ticketStatus;
        orderID = "";
    }

    public TicketObj(String memberID, String ticketStatus, String orderID) {
        this(memberID, ticketStatus);
        this.orderID = orderID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getMemberID() {
        return memberID;
    }

    public void setMemberID(String memberID) {
        this.memberID = memberID;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }
}
