package edu.cmu.cs.cs214.hw4.element;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.cmu.cs.cs214.hw4.parameter.SegmentType;

/**
 * The Segment program represent a segment in the game.
 */
public class Segment {
    private SegmentType segmentType;
    private int open; // count the number of open segments, open == 0 means complete
    private int pennants; // number of pennants
    private Map<Integer, Integer> followers; // <PlayerId, numberOfFollower>
    private Set<String> areas; // how many tiles the segment contains
    private Set<Segment> features;
    private Set<Segment> supportFields;

    public Segment() {
        this.areas = new HashSet<>();
        this.followers = new HashMap<>();
        this.features = new HashSet<>();
        features.add(this);
        this.supportFields = new HashSet<>();
    }

    /* ******************** main functionality ******************** */
    /**
     * This method is used to get a cloister segment.
     *
     * @return Segment return a cloister segment
     */
    public static Segment getCloisterSegment() {
        Segment cloisterSegment = new Segment();
        cloisterSegment.setSegmentType(SegmentType.CLOISTER);
        cloisterSegment.setOpen(0);
        cloisterSegment.setPennants(0);
        cloisterSegment.setFollowers(new HashMap<>());
        return cloisterSegment;
    }

    /**
     * This method is used to merge two segments into one.
     *
     * @param mainSegment the segment that been merged into
     * @param subSegment the segment that will be merged into other segment
     */
    public static void merge(Segment mainSegment, Segment subSegment) {
        // mainSegment is the main part, merge this segment to input segment
        // merge open
        int totalOpen = mainSegment.getOpen() + subSegment.getOpen() - 2;
        System.out.println("Current open = " + totalOpen + ", mainOpen = " + mainSegment.getOpen() + ", subOpen = " + subSegment.getOpen());
        mainSegment.setOpen(totalOpen);
        // merge pennants
        int totalPennants = mainSegment.getPennants() + subSegment.getPennants();
        mainSegment.setPennants(totalPennants);
        // merge followers
        Map<Integer, Integer> mainFollowers = mainSegment.getFollowers();
        Map<Integer, Integer> subFollowers = subSegment.getFollowers();
        for (Integer playerId : subFollowers.keySet()) {
            if (mainFollowers.containsKey(playerId)) {
                // merge followers
                int totalFollowers = mainFollowers.get(playerId) + subFollowers.get(playerId);
                mainFollowers.put(playerId, totalFollowers);
            } else {
                // add new followers
                mainFollowers.put(playerId, subFollowers.get(playerId));
            }
        }
        mainSegment.setFollowers(mainFollowers);
        // merge as one segment
        mainSegment.getAreas().addAll(subSegment.getAreas());
        mainSegment.getFeatures().addAll(subSegment.getFeatures());
        mainSegment.getSupportFields().addAll(subSegment.getSupportFields());
        // modify subSegment
        for (Segment seg : mainSegment.getFeatures()) {
            seg.setSegmentType(mainSegment.getSegmentType());
            seg.setOpen(mainSegment.getOpen());
            seg.setPennants(mainSegment.getPennants());
            seg.setFollowers(mainSegment.getFollowers());
            seg.setAreas(mainSegment.getAreas());
            seg.setFeatures(mainSegment.getFeatures());
            seg.setSupportFields(mainSegment.getSupportFields());
        }
    }

    @Override
    public String toString() {
        return "Segment{" +
                "\nsegmentType=" + segmentType +
                ",\n open=" + open +
                ",\n pennants=" + pennants +
                ",\n followers=" + followers +
                ",\n areas=" + areas +
                "}\n";
    }

    /* ******************** getters and setters ******************** */

    public int getOpen() {
        return open;
    }

    public void setOpen(int open) {
        this.open = open;
    }

    public int getPennants() {
        return pennants;
    }

    public void setPennants(int pennants) {
        this.pennants = pennants;
    }

    public Map<Integer, Integer> getFollowers() {
        return followers;
    }

    public void setFollowers(Map<Integer, Integer> followers) {
        this.followers = followers;
    }

    public SegmentType getSegmentType() {
        return segmentType;
    }

    public void setSegmentType(SegmentType segmentType) {
        this.segmentType = segmentType;
    }

    public Set<String> getAreas() {
        return areas;
    }

    public void setAreas(Set<String> areas) {
        this.areas = areas;
    }

    public Set<Segment> getFeatures() {
        return features;
    }

    public void setFeatures(Set<Segment> features) {
        this.features = features;
    }

    public Set<Segment> getSupportFields() {
        return supportFields;
    }

    public void setSupportFields(Set<Segment> supportFields) {
        this.supportFields = supportFields;
    }
}
