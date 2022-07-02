package BotObjects;

public class GuildObject {
    private String prefix;
    private int ticketLimit;
    private String ownerId;
    private String ticketCategoryId;
    private String staffId;
    private String logChannelId;
    private String customerRoleId;
    private String memberRoleId;
    private String joinChannelId;
    private String leaveChannelId;
    private final String guildId;
    public String getOwnerId() { return ownerId; }
    public String getStaffId() { return staffId; }
    public GuildObject(
            String prefix,
            int ticketLimit,
            String ownerId,
            String ticketCategoryId,
            String staffId,
            String logChannelId,
            String customerRoleId,
            String memberRoleId,
            String joinChannelId,
            String leaveChannelId,
            String guildId
            ) {
        this.prefix = prefix;
        this.ticketLimit = ticketLimit;
        this.ownerId = ownerId;
        this.ticketCategoryId = ticketCategoryId;
        this.staffId = staffId;
        this.logChannelId = logChannelId;
        this.customerRoleId = customerRoleId;
        this.memberRoleId = memberRoleId;
        this.joinChannelId = joinChannelId;
        this.leaveChannelId = leaveChannelId;
        this.guildId = guildId;
    }
    public String getGuildId() {
        return guildId;
    }
    public String getPrefix() {
        return prefix;
    }
    public int getTicketLimit() {
        return ticketLimit;
    }
    public String getTicketCategoryId() { return ticketCategoryId; }
    public String getLogChannelId() { return logChannelId; }
    public String getCustomerRoleId() { return customerRoleId; }
    public String getJoinChannelId() {
        return joinChannelId;
    }

    public String getLeaveChannelId() {
        return leaveChannelId;
    }

    public String getMemberRoleId() {
        return memberRoleId;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    public void setTicketLimit(int ticketLimit) {
        this.ticketLimit = ticketLimit;
    }
    public void setTicketCategoryId(String ticketCategoryId) {this.ticketCategoryId = ticketCategoryId; }
    public void setLogChannelId(String logChannelId) {
        this.logChannelId = logChannelId;
    }
    public void setCustomerRoleId(String customerRoleId) { this.customerRoleId = customerRoleId; }
    public void setMemberRoleId(String memberRoleId) {
        this.memberRoleId = memberRoleId;
    }
    public void setJoinChannelId(String joinChannelId) {
        this.joinChannelId = joinChannelId;
    }
    public void setLeaveChannelId(String leaveChannelId) {
        this.leaveChannelId = leaveChannelId;
    }

    @Override
    public String toString() {
        return "GuildObject{" +
                "guildId='" + guildId + '\'' +
                ", ticketLimit=" + ticketLimit +
                ", prefix='" + prefix + '\'' +
                ", serverOwnerId='" + ownerId + '\'' +
                ", ticketCategoryId='" + ticketCategoryId + '\'' +
                ", logChannelId='" + logChannelId + '\'' +
                ", staffRoleId='" + staffId + '\'' +
                ", customerRoleId='" + customerRoleId + '\'' +
                ", memberRoldId='" + memberRoleId + '\'' +
                ", joinChannelId='" + joinChannelId + '\'' +
                ", leaveChannelId='" + leaveChannelId + '\'' +
                '}';
    }
}
