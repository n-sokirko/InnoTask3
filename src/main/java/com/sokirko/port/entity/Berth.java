package com.sokirko.port.entity;

public class Berth {

  private final int berthId;

  public Berth(int berthId) {
    this.berthId = berthId;
  }

  public int getBerthId() {
    return berthId;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || getClass() != object.getClass()) {
      return false;
    }
    Berth berth = (Berth) object;
    return berthId == berth.berthId;
  }

  @Override
  public int hashCode() {
    return Integer.hashCode(berthId);
  }

  @Override
  public String toString() {
    return "Berth #" + berthId;
  }
}
