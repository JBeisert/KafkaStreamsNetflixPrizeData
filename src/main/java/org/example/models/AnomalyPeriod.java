package org.example.models;


public class AnomalyPeriod {
    private Anomaly anomaly;
    private String startTs;
    private String endTs;

    public AnomalyPeriod() {
    }

    public AnomalyPeriod(Anomaly anomaly, String startTs, String endTs) {
        this.anomaly = anomaly;
        this.startTs = startTs;
        this.endTs = endTs;
    }

    public Anomaly getAnomaly() {
        return anomaly;
    }

    public void setAnomaly(Anomaly anomaly) {
        this.anomaly = anomaly;
    }

    public String getStartTs() {
        return startTs;
    }

    public void setStartTs(String startTs) {
        this.startTs = startTs;
    }

    public String getEndTs() {
        return endTs;
    }

    public void setEndTs(String endTs) {
        this.endTs = endTs;
    }

    @Override
    public String toString() {
        return "CountFlightsPeriodRecord{" +
                "anomaly=" + anomaly +
                ", startTs='" + startTs + '\'' +
                ", endTs='" + endTs + '\'' +
                '}';
    }
}
