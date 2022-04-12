package SLC.SLC.Services;

import AppKickstarter.misc.Msg;
import SLC.SLC.DataStore.Dto.Diagnostic.HealthPoolDto;
import SLC.SLC.SLC;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DiagnosticService extends Service {
    private HealthPoolDto healthPoolDto;
    public int lastUpdate;
    public HashMap<String, Integer> HWFailure;
    public String[] modules = new String[]{
            "BarcodeReaderDriver",
            "TouchDisplayHandler",
            "OctopusCardReaderDriver",
            "Locker"};

    public DiagnosticService(SLC instance) {
        super(instance);
        healthPoolDto = new HealthPoolDto();
        HWFailure = new HashMap<>();
        initHWFailure();
    }

    public void initHWFailure(){
        for (String module: modules){
            HWFailure.put(module, 0);
        }
    }

    public void scanHWFailure(){
        for (Map.Entry<String, Integer> entry : HWFailure.entrySet()){
            int val = entry.getValue();
            if (val == 3) {
                String module = entry.getKey();
                System.out.println(module + "is down! Sent Message to SLC Server.");
                //serverMBox.send(new Msg(slc.getID(), slc.getMBox(), Msg.Type.SVR_HWOffLine, ""));
                HWFailure.put(module, HWFailure.get(module) + 1); //val = 4 -> Msg sent to Server
            }
        }
    }

    @Override
    public void onMessage(Msg message) {
        switch(message.getType()){
            case PollAck:
                slc.getLogger().info("PollAck: " + message.getDetails());
                receiveHWHealthPoll(message);
                break;

            case PollNak:
                slc.getLogger().info("PollNak: " + message.getDetails());
                receiveHWHealthPoll(message);
                break;
        }
    }

    public void receiveHWHealthPoll(Msg message){
        boolean isOnline = false;
        int i = 0;
        if (message.getType().equals(Msg.Type.PollAck)){
            isOnline= true;
        }
        // Set DTO's attributes
        try {
            switch (message.getSender()) {
                case "BarcodeReaderDriver":
                    healthPoolDto.isBarcodeReaderOnline = isOnline;
                    break;
                case "TouchDisplayHandler":
                    healthPoolDto.isTouchScreenOnline = isOnline;
                    i = 1;
                    break;
                case "OctopusCardReaderDriver":
                    healthPoolDto.isOctopusCardReaderOnline = isOnline;
                    i = 2;
                    break;
                case "Locker":
                    healthPoolDto.isLockerControllerOnline = isOnline;
                    i = 3;
                    break;
            }
        }finally{
            if (!isOnline) HWFailure.put(modules[i], HWFailure.get(modules[i]) + 1);
            scanHWFailure();
        }
    }

    public void saveDto(){
        try {
            File healthPollFile = new File("healthPoll.bin");
            healthPoolDto.save(healthPollFile);
        }catch(IOException e){
            System.out.println(e); // Exception Handling
        }
    }

    public void loadDto(){
        try{
            File healthPollFile = new File("healthPoll.bin");
            if (healthPollFile.exists() && !healthPollFile.isDirectory())
                healthPoolDto = HealthPoolDto.<HealthPoolDto>from(healthPollFile);
        }catch (IOException | ClassNotFoundException e) {
            System.out.println(e); // Exception Handling
        }
    }

    public boolean isBRShutDown(){
        return HWFailure.get(modules[0]) > 3;
    }

    public boolean isTSShutDown(){
        return HWFailure.get(modules[1]) > 3;
    }

    public boolean isORRShutDown(){
        return HWFailure.get(modules[2]) > 3;
    }

    public boolean isLKShutDown(){
        return HWFailure.get(modules[3]) > 3;
    }

    /**
     * Respond to the health poll request made by SLC Server
     */
    public void sendHealthPoll(){
        try {
            healthPoolDto.lastUpdate = this.lastUpdate;
            String healthPoll = healthPoolDto.toBase64();
            //serverMBox.send(new Msg(slc.getID(), slc.getMBox(), Msg.Type.SVR_HealthPollResponse, healthPoll));
        }catch(IOException e){
            System.out.println(e); //Exception Handling
        }
    }
}
