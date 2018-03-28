package edu.cmu.cs.cs214.hw4.parameter;

/**
 * All the possible features.
 */
public enum SegmentType {
    CITY("city", 2, 1), ROAD("road", 1, 1), FIELD("field", 0, 0), CLOISTER("cloister", 1, 1);
    private String segmentTypeName;
    private int completePoints;
    private int incompletePoints;

    SegmentType(String segmentTypeName, int completePoints, int incompletePoints) {
        this.segmentTypeName = segmentTypeName;
        this.completePoints = completePoints;
        this.incompletePoints = incompletePoints;
    }

    public int getCompletePoints() {
        return completePoints;
    }

    public int getIncompletePoints() {
        return incompletePoints;
    }
}
