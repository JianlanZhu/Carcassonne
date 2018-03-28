package edu.cmu.cs.cs214.hw4.element;

import edu.cmu.cs.cs214.hw4.element.bean.TileBean;
import edu.cmu.cs.cs214.hw4.parameter.Rules;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * The Tile program represents a tile.
 */
public class Tile {
    private int tileId;
    private int direction;
    private int x, y;
    private List<List<Segment>> borders; // 0: up; 1: right; 2: down; 3: left
    private Segment cloister; // true means that the tile contains cloister
    private int surroundings; // number of tiles around the tile
    private List<Segment> cities;

    /**
     * This method is used to initialize a new Tile using a TileBean.
     *
     * @param tileId tile ID
     * @param tileBean TileBean used to initialize a new tile
     */
    public Tile(int tileId, TileBean tileBean) {
        this.tileId = tileId;
        this.direction = 0;
        this.borders = new ArrayList<>();
        this.cities = new ArrayList<>();
        List<Segment> segmentList = new ArrayList<>();
        for (Segment origin : tileBean.getSegments()) {
            // deep copy
            Segment seg = new Segment();
            seg.setSegmentType(origin.getSegmentType());
            seg.setOpen(origin.getOpen());
            seg.setPennants(origin.getPennants());
            segmentList.add(seg);
        }
        for (int i = 0; i < 4; i++) {
            int[] segmentIdx = tileBean.getPositions().get(i);
            List<Segment> border = new ArrayList<>();
            for (int index : segmentIdx) {
                border.add(segmentList.get(index));
            }
            borders.add(border);
        }
        this.cloister = tileBean.isCloister() ? Segment.getCloisterSegment() : null;
        this.surroundings = 0;
        Map<Integer, int[]> cityNeighbors = tileBean.getCityNeighbors();
        if (cityNeighbors != null && !cityNeighbors.isEmpty()) {
            for (Integer cityIdx : cityNeighbors.keySet()) {
                Segment city = segmentList.get(cityIdx);
                this.cities.add(city);
                for (int fieldIdx : cityNeighbors.get(cityIdx)) {
                    Segment field = segmentList.get(fieldIdx);
                    city.getSupportFields().add(field);
                }
            }
        }
    }

    /* ******************** main functionality ******************** */
    /**
     * This method is used to rotate the tile 90 degree clockwise.
     */
    public void rotate() {
        this.direction = (this.direction + 1) % 4;
        List<List<Segment>> temp = new ArrayList<>();
        temp.add(borders.get(3));
        temp.add(borders.get(0));
        temp.add(borders.get(1));
        temp.add(borders.get(2));
        borders = temp;
    }

    /**
     * This method is used to set the coordinate of the current tile.
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public void setCoordinate(int x, int y) {
        this.x = x;
        this.y = y;
        String coordinate = Rules.getCoordinate(x, y);
        for (List<Segment> list : borders) {
            for (Segment segment : list) {
                segment.getAreas().add(coordinate);
            }
        }
    }

    @Override
    public String toString() {
        return "Tile{" +
                "\ntileId=" + tileId +
                "\n, direction=" + direction +
                "\n, x=" + x +
                "\n, y=" + y +
                "\n, borders=" + borders +
                "\n, cloister=" + cloister +
                "\n, surroundings=" + surroundings +
                '}';
    }

    /* ******************** getters and setters ******************** */
    public int getTileId() {
        return tileId;
    }

    public int getDirection() {
        return direction;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<List<Segment>> getBorders() {
        return borders;
    }

    public Segment getCloister() {
        return cloister;
    }

    public int getSurroundings() {
        return surroundings;
    }

    public void setSurroundings(int surroundings) {
        this.surroundings = surroundings;
    }

    public List<Segment> getCities() {
        return cities;
    }

}

