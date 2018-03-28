package edu.cmu.cs.cs214.hw4.element.bean;

import edu.cmu.cs.cs214.hw4.element.Segment;
import java.util.List;
import java.util.Map;

/**
 * Bean versions of Tile class, used to initialize Tile class.
 */
public class TileBean {
    private List<Segment> segments;
    private List<int[]> positions; // segments' positions
    private boolean cloister;
    private Map<Integer, int[]> cityNeighbors; // <cityIdx, fieldIdx>

    public TileBean() {
    }

    @Override
    public String toString() {
        return "TileBean{" +
                "\nsegments=" + segments +
                ",\n positions=" + positions.size() +
                ",\n cloister=" + cloister +
                "}\n";
    }

    /**************** getters and setters ****************/

    public boolean isCloister() {
        return cloister;
    }

    public void setCloister(boolean cloister) {
        this.cloister = cloister;
    }

    public List<Segment> getSegments() {
        return segments;
    }

    public void setSegments(List<Segment> segments) {
        this.segments = segments;
    }

    public List<int[]> getPositions() {
        return positions;
    }

    public void setPositions(List<int[]> positions) {
        this.positions = positions;
    }

    public Map<Integer, int[]> getCityNeighbors() {
        return cityNeighbors;
    }

    public void setCityNeighbors(Map<Integer, int[]> cityNeighbors) {
        this.cityNeighbors = cityNeighbors;
    }
}
