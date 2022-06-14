package BotObjects;

public class GuildObject {
    private String guildId;
    private String prefix;
    private int ticketLimit;
    private String serverOwnerId;

    public GuildObject(String guildId, String prefix, int ticketLimit, String serverOwnerId) {
        this.guildId = guildId;
        this.prefix = prefix;
        this.ticketLimit = ticketLimit;
        this.serverOwnerId = serverOwnerId;
    }

    public String getGuildId() {
        return guildId;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public int getTicketLimit() {
        return ticketLimit;
    }

    public void setTicketLimit(int ticketLimit) {
        this.ticketLimit = ticketLimit;
    }

    public String getServerOwnerId() {
        return serverOwnerId;
    }

    public void setServerOwnerId(String serverOwnerId) {
        this.serverOwnerId = serverOwnerId;
    }
}
