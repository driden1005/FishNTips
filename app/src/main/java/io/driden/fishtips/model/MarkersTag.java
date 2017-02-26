package io.driden.fishtips.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "markers", strict = false)
public class MarkersTag {

    @ElementList(entry = "marker", inline = true)
    private List<MarkerTag> list;

    public List<MarkerTag> getList() {
        return list;
    }

    public void setList(List<MarkerTag> list) {
        this.list = list;
    }
}
