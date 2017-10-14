package subtopic_mining_system;


public class TermInfo {


  private int mountPerDoc;
  private String termStr;
  private double rawWeight;
  private double weight;
  private double tf;

  
  public double getTf() {
          return tf;
  }

  public void setTf(double tf) {
          this.tf = tf;
  }


  public int getMountPerDoc() {
          return mountPerDoc;
  }


  public void setMountPerDoc(int mountPerDoc) {
          this.mountPerDoc = mountPerDoc;
  }


  public String getTermStr() {
          return termStr;
  }


  public void setTermStr(String termStr) {
          this.termStr = termStr;
  }



  public double getRawWeight() {
          return rawWeight;
  }



  public void setRawWeight(double rawWeight) {
          this.rawWeight = rawWeight;
  }


  public double getWeight() {
          return weight;
  }


  public void setWeight(double weight) {
          this.weight = weight;
  }


}

