package com.port.entity;

public class PortSettings {

  private final int berthCount;
  private final int warehouseCapacity;
  private final int containersInWarehouse;

  public PortSettings(int berthCount, int warehouseCapacity, int containersInWarehouse) {
    this.berthCount = berthCount;
    this.warehouseCapacity = warehouseCapacity;
    this.containersInWarehouse = containersInWarehouse;
  }

  public int getBerthCount() {
    return berthCount;
  }

  public int getWarehouseCapacity() {
    return warehouseCapacity;
  }

  public int getContainersInWarehouse() {
    return containersInWarehouse;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }
    PortSettings settings = (PortSettings) object;
    return berthCount == settings.berthCount
        && warehouseCapacity == settings.warehouseCapacity
        && containersInWarehouse == settings.containersInWarehouse;
  }

  @Override
  public int hashCode() {
    int result = Integer.hashCode(berthCount);
    result = 31 * result + Integer.hashCode(warehouseCapacity);
    return 31 * result + Integer.hashCode(containersInWarehouse);
  }

  @Override
  public String toString() {
    return "PortSettings{berthCount=" + berthCount
        + ", warehouseCapacity=" + warehouseCapacity
        + ", containersInWarehouse=" + containersInWarehouse + '}';
  }
}
