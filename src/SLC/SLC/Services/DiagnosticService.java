package SLC.SLC.Services;

import AppKickstarter.misc.Msg;
import AppKickstarter.timer.Timer;
import SLC.SLC.DataStore.Dto.Common.LockerDto;
import SLC.SLC.DataStore.Dto.Common.LockerStatusDto;
import SLC.SLC.DataStore.Dto.Diagnostic.HealthPoolDto;
import SLC.SLC.DataStore.Interface.Locker;
import SLC.SLC.Handlers.MouseClick.MainMenuMouseClickHandler;
import SLC.SLC.Handlers.MouseClick.MouseClickHandler;
import SLC.SLC.SLC;
import SLC.SLC.Screen;
import SLC.SLC.UserService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DiagnosticService extends Service {
    private HealthPoolDto healthPoolDto;
    public int lastUpdate;
    public int responseTimerID;

    public HashMap<String, Integer> HWFailure;
    public HashMap<String, Boolean> HWResponse;
    public String[] modules = new String[]{
            "BarcodeReaderDriver",
            "TouchDisplayHandler",
            "OctopusCardReaderDriver",
            "Locker"};

    public DiagnosticService(SLC instance) {
        super(instance);
        healthPoolDto = new HealthPoolDto();
        HWFailure = new HashMap<>();
        HWResponse = new HashMap<>();
        initHashMap();
    }

    public void initHashMap() {
        for (String module : modules) {
            HWFailure.put(module, 0);
            HWResponse.put(module, false);
        }
    }

    public void scanHWFailure() {
        for (Map.Entry<String, Integer> entry : HWFailure.entrySet()) {
            int val = entry.getValue();
            if (val == 3) {
                String module = entry.getKey();
                slc.getLogger().info(module + " is down! Sent Message to SLC Server.");
                sendHealthPoll();
                HWFailure.put(module, HWFailure.get(module) + 1); //val = 4 -> Msg sent to Server
            }
        }
    }

    @Override
    public void onMessage(Msg message) {
        switch (message.getType()) {
            case PollAck:
                slc.getLogger().info("PollAck: " + message.getDetails());
                receiveHWHealthPoll(message);
                break;

            case PollNak:
                slc.getLogger().info("PollNak: " + message.getDetails());
                receiveHWHealthPoll(message);
                break;

            case TimesUp:
                if (Timer.getTimesUpMsgTimerId(message) == responseTimerID) {
                    scanHWResponse();
                }
        }
    }

    public void scanHWResponse() {
        for (Map.Entry<String, Boolean> entry : HWResponse.entrySet()) {
            boolean responded = entry.getValue();
            if (!responded) {
                HWFailure.put(entry.getKey(), HWFailure.get(entry.getKey()) + 1);
            }
            scanHWFailure();
        }
    }


    public void receiveHWHealthPoll(Msg message) {
        boolean isOnline = false;
        if (message.getType().equals(Msg.Type.PollAck)) {
            isOnline = true;
        }
        // Set DTO's attributes
        switch (message.getSender()) {
            case "BarcodeReaderDriver":
                healthPoolDto.isBarcodeReaderOnline = isOnline;
                HWResponse.put(modules[0], isOnline);
                break;
            case "TouchDisplayHandler":
                healthPoolDto.isTouchScreenOnline = isOnline;
                HWResponse.put(modules[1], isOnline);
                break;
            case "OctopusCardReaderDriver":
                healthPoolDto.isOctopusCardReaderOnline = isOnline;
                HWResponse.put(modules[2], isOnline);
                break;
            case "Locker":
                healthPoolDto.isLockerControllerOnline = isOnline;
                HWResponse.put(modules[3], isOnline);
                break;
        }
    }

    public void saveDto() {
        try {
            File healthPollFile = new File("healthPoll.bin");
            healthPoolDto.save(healthPollFile);
        } catch (IOException e) {
            System.out.println(e); // Exception Handling
        }
    }

    public void loadDto() {
        try {
            File healthPollFile = new File("healthPoll.bin");
            if (healthPollFile.exists() && !healthPollFile.isDirectory())
                healthPoolDto = HealthPoolDto.<HealthPoolDto>from(healthPollFile);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e); // Exception Handling
        }
    }

    public boolean isBRShutDown() {
        if (HWFailure.get(modules[0]) > 3) {
            slc.getLogger().info("Error: Already Shut down Barcode Reader: Hardware Failure.");
            return true;
        }
        return false;
    }

    public boolean isTSShutDown() {
        if (HWFailure.get(modules[1]) > 3) {
            slc.getLogger().info("Error: Already Shut down Touch Screen: Hardware Failure.");
            return true;
        }
        return false;
    }

    public boolean isORRShutDown() {
        if (HWFailure.get(modules[2]) > 3) {
            slc.getLogger().info("Error: Already Shut down Octopus Card Reader: Hardware Failure.");
            return true;
        }
        return false;
    }

    public boolean isLKShutDown() {
        if (HWFailure.get(modules[3]) > 3) {
            slc.getLogger().info("Error: Already Shut down Locker: Hardware Failure.");
            return true;
        }
        return false;
    }

    /**
     * Respond to the health poll request made by SLC Server
     */
    public void sendHealthPoll() {
        try {
            healthPoolDto.lastUpdate = this.lastUpdate;
/*            for (int i = 0; i < slc.getLockers().size(); i++){
                healthPoolDto.lockers.set(i,slc.getLockers().get(i).toDto());
            }*/
            LockerStatusDto lockerStatusDto = new LockerStatusDto();
            ArrayList<LockerDto> lockers = new ArrayList<>();
            for (int i = 0; i < slc.getLockers().size(); i++){
                lockers.add(slc.getLockers().get(i).toDto());
            }
            //lockerStatusDto.lockers = slc.getLockers();
            lockerStatusDto.lockers = lockers;
            healthPoolDto.lockers = lockerStatusDto.toBase64();
            String healthPoll = healthPoolDto.toBase64();
            slc.getServerMBox().send(new Msg(slc.getID(), slc.getMBox(), Msg.Type.SVR_HealthPollResponse, healthPoll));
        } catch (IOException e) {
            System.out.println(e); //Exception Handling
        }
    }

    public void showSystemStatus() {
        ArrayList<Locker> lockers = slc.getLockers();
        HashMap<String, String> lockerMap = slc.getCheckInPackage();
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        String formatted = dateFormatter.format(date);
        System.out.println("------------------------System Diagnostic Report------------------------");
        System.out.print("Report Generation Time: " + formatted + "\n");
        System.out.print("Hardware Encountered Failure?\n" +
                "  --Barcode Reader:       " + isBRShutDown() + "\n" +
                "  --Touch Screen Display: " + isTSShutDown() + "\n" +
                "  --Octopus Card Reader:  " + isORRShutDown() + "\n" +
                "  --Locker:               " + isLKShutDown() + "\n");
        System.out.println("Locker Slot: {");

        for (Locker lockerSlot : lockers) {
            System.out.println("[ID:" + lockerSlot.getSlotId() + ",");
            System.out.println("    Locked:" + lockerSlot.getLocked() + ",");
            System.out.println("    Contain Package:" + lockerSlot.getContainPackage() + ",");
            System.out.println("    Reserved:" + lockerSlot.getReserved() + ",");
            System.out.println("    Last Updated:" + lockerSlot.getLastUpdate() + ",");
            //System.out.println("    Access Code:" +  + "]"); //TODO: GET ACCESS CODE!!!
        }

        System.out.println("}");
        System.out.println("------------------------------End of Report-----------------------------");
    }

    public void setResponseTimer() {
        responseTimerID = Timer.setTimer(slc.getID(), slc.getMBox(), slc.getResponseTime());
    }

/*    public void displayHWFailure(String module){
        slc.setScreen(Screen.Text);
        slc.setOnScreenLoaded(() -> {
            slc.setOnScreenLoaded(() -> {
                slc.setScreenText("title", "Hardware Failure");
                slc.setScreenText("subtitle", module + " is down. Please contact the Smart Locker Company.");
                slc.setScreenText("body", "");
            });
        });
    }*/

}
