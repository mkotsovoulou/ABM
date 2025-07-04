package com.eydap.abm;

public enum ReportType {
    FACT_STATS("Αναλυτικά Δεδομένα (Fact Stats)", "2022SRV", "dss_dev", "ABM_PRocess_factstats"),
    FACT_STATS2("Κόστος ΤΦ (Fact Stats2)", "2022SRV", "dss_dev", "abm_process_factstats2"),
    FACT_STATS3("Water Report (Fact Stats3)", "2019SRV\\MSSQLSERVER2019", "EYDAP", "abm_process_factstats3");

    private final String displayName;
    private final String server;
    private final String database;
    private final String procedure;

    ReportType(String displayName, String server, String database, String procedure) {
        this.displayName = displayName;
        this.server = server;
        this.database = database;
        this.procedure = procedure;
    }

    public String getDisplayName() { return displayName; }
    public String getServer() { return server; }
    public String getDatabase() { return database; }
    public String getProcedure() { return procedure; }

    @Override
    public String toString() {
        return displayName;
    }
}