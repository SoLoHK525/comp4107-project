package SLC.SLC.DataStore.Interface;

import SLC.SLC.DataStore.Dto.Common.LockerDto;

public class Locker {
    private String slotId;
    private LockerSize size;
    private boolean locked;
    private boolean containPackage;
    private boolean reserved;
    private int lastUpdate; // timestamp

    public Locker(String slotId, LockerSize size) {
        this.slotId = slotId;
        this.size = size;
        this.locked = true;
        this.containPackage = false;
        this.reserved = false;
    }

    private Locker(String slotId, LockerSize size, boolean locked, boolean containPackage, boolean reserved, int lastUpdate) {
        this.slotId = slotId;
        this.size = size;
        this.locked = locked;
        this.containPackage = containPackage;
        this.reserved = reserved;
        this.lastUpdate = lastUpdate;
    }

    public static Locker fromDto(LockerDto dto) {
        return new Locker(dto.slotId, dto.size, dto.locked, dto.containPackage, dto.reserved, dto.lastUpdate);
    }

    public String getSlotId() {
        return slotId;
    }

    public LockerSize getSize() {
        return size;
    }

    public boolean getLocked() {
        return this.locked;
    }

    public boolean getContainPackage() {
        return this.containPackage;
    }

    public boolean getReserved() {
        return this.reserved;
    }

    public int getLastUpdate() {
        return this.lastUpdate;
    }

    public void setLock(boolean bool){
        this.locked = bool;
        this.updateTimestamp();
    }

    public void setContainPackage(boolean bool) {
        this.containPackage = bool;
        this.updateTimestamp();
    }

    public void setReserved(boolean bool) {
        this.reserved = bool;
        this.updateTimestamp();
    }

    public LockerDto toDto() {
        LockerDto dto = new LockerDto();
        dto.slotId = this.slotId;
        dto.size = this.size;
        dto.locked = this.locked;
        dto.containPackage = this.containPackage;
        dto.reserved = this.reserved;
        dto.lastUpdate = this.lastUpdate;

        return dto;
    }

    private void updateTimestamp() {
        this.lastUpdate = (int) (System.currentTimeMillis() / 1000L);
    }
}