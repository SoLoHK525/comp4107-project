package AppKickstarter.misc;


//======================================================================
// Msg
public class Msg {
    private String sender;
    private MBox senderMBox;
    private Type type;
    private String details;

    //------------------------------------------------------------
    // Msg

    /**
     * Constructor for a msg.
     *
     * @param sender     id of the msg sender (String)
     * @param senderMBox mbox of the msg sender
     * @param type       message type
     * @param details    details of the msg (free format String)
     */
    public Msg(String sender, MBox senderMBox, Type type, String details) {
        this.sender = sender;
        this.senderMBox = senderMBox;
        this.type = type;
        this.details = details;
    } // Msg


    //------------------------------------------------------------
    // getSender

    /**
     * Returns the id of the msg sender
     *
     * @return the id of the msg sender
     */
    public String getSender() {
        return sender;
    }


    //------------------------------------------------------------
    // getSenderMBox

    /**
     * Returns the mbox of the msg sender
     *
     * @return the mbox of the msg sender
     */
    public MBox getSenderMBox() {
        return senderMBox;
    }


    //------------------------------------------------------------
    // getType

    /**
     * Returns the message type
     *
     * @return the message type
     */
    public Type getType() {
        return type;
    }


    //------------------------------------------------------------
    // getDetails

    /**
     * Returns the details of the msg
     *
     * @return the details of the msg
     */
    public String getDetails() {
        return details;
    }


    //------------------------------------------------------------
    // toString

    /**
     * Returns the msg as a formatted String
     *
     * @return the msg as a formatted String
     */
    public String toString() {
        return sender + " (" + type + ") -- " + details;
    } // toString


    //------------------------------------------------------------
    // Msg Types

    /**
     * Message Types used in Msg.
     *
     * @see Msg
     */
    public enum Type {
        /**
         * Terminate the running thread
         */
        Terminate,
        /**
         * Generic error msg
         */
        Error,
        /**
         * Set a timer
         */
        SetTimer,
        /**
         * Set a timer
         */
        CancelTimer,
        /**
         * Timer clock ticks
         */
        Tick,
        /**
         * Time's up for the timer
         */
        TimesUp,
        /**
         * Health poll
         */
        Poll,
        /**
         * Health poll +ve acknowledgement
         */
        PollAck,
        /**
         * Health poll -ve acknowledgement
         */
        PollNak,
        /**
         * Update Display
         */
        TD_UpdateDisplay,
        /**
         * Mouse Clicked
         */
        TD_MouseClicked,
        /**
         * Screen Loaded
         */
        TD_ScreenLoaded,
        /**
         * Change Text Label
         */
        TD_ChangeTextLabel,
        /**
         * Barcode Reader Go Activate
         */
        BR_GoActive,
        /**
         * Barcode Reader Go Standby
         */
        BR_GoStandby,
        /**
         *  Barcode Reader return active
         */
        BR_ReturnActive,
        /**
         *  Barcode Reader return standby
         */
        BR_ReturnStandby,
        /**
         * Card inserted
         */
        BR_BarcodeRead,
        /**
         * Octopus Card Reader Go Activate
         */
        OCR_GoActive,
        /**
         * Octopus Card Reader Go Standby
         */
        OCR_GoStandby,
        /**
         * Octopus Card inserted
         */
        OCR_CardRead,
        /**
         * Octopus Card is charged
         */
        OCR_Charged,
        /*
         * Unlock a locker
         */
        LK_Unlock,
        /**
         *  Check lock status of a locker
         */
        LK_CheckStatus,
        /**
         *  Return lock status of a locker with given slotID
         */
        LK_ReturnStatus,
        /**
         *  Return locked event to slc once locker is closed
         */
        LK_Locked,
        /**
         * Reserve request [Server]
         */
        SVR_ReserveRequest,
        /**
         * Reserved response [SLC]
         */
        SVR_ReservedResponse,
        /**
         * Verify barcode during checkin [SLC]
         */
        SVR_VerifyBarcode,
        /**
         * Barcode verified message [Server]
         */
        SVR_BarcodeVerified,
        /**
         * On item check in [SLC]
         */
        SVR_CheckIn,
        /**
         * On item checkout [SLC]
         */
        SVR_CheckOut,
        /**
         * Health poll request [Server]
         */
        SVR_HealthPollRequest,
        /**
         * Health poll response [SLC]
         */
        SVR_HealthPollResponse,


    } // Type
} // Msg
