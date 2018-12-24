package electicsynergic.mexsycorp.com.ticketingsystem.Objects;

/**
 * Created by Emeka David Chukumah on 7/10/2017.
 */

public class Ticket {
    private int TicketNumber;
    private String StateCode;

    public Ticket(String stateCode, int ticketNumber) {
        StateCode = stateCode;
        TicketNumber = ticketNumber;
    }

    public String getStateCode() {
        return StateCode;
    }

    public int getTicketNumber() {
        return TicketNumber;
    }
}