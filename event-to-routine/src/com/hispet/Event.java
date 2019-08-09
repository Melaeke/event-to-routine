package com.hispet;

import java.util.ArrayList;
import java.util.List;

public class Event {
	private String uid;

    private String event;

    private String orgUnit;

    private String eventDate;

    private String dueDate;

    private String storedBy;

    private List<DataValue> dataValues = new ArrayList<>();

    private String attributeOptionCombo;



    public String getUid()
    {
        return uid;
    }

    public void setUid( String uid )
    {
        this.uid = uid;
    }

    public String getEvent()
    {
        return event;
    }

    public void setEvent( String event )
    {
        this.event = event;
    }
    
    public String getOrgUnit()
    {
        return orgUnit;
    }

    public void setOrgUnit( String orgUnit )
    {
        this.orgUnit = orgUnit;
    }

    public String getEventDate()
    {
        return eventDate;
    }

    public void setEventDate( String eventDate )
    {
        this.eventDate = eventDate;
    }

   
    public String getDueDate()
    {
        return dueDate;
    }

    public void setDueDate( String dueDate )
    {
        this.dueDate = dueDate;
    }

    public String getStoredBy()
    {
        return storedBy;
    }

    public void setStoredBy( String storedBy )
    {
        this.storedBy = storedBy;
    }

    public List<DataValue> getDataValues()
    {
        return dataValues;
    }

    public void setDataValues( List<DataValue> dataValues )
    {
        this.dataValues = dataValues;
    }

    
    public String getAttributeOptionCombo()
    {
        return attributeOptionCombo;
    }

    public void setAttributeOptionCombo( String attributeOptionCombo )
    {
        this.attributeOptionCombo = attributeOptionCombo;
    }

    @Override 
    public String toString()
    {
        return "Event{" +
            "uid='" + uid + '\'' +
            ", event='" + event + '\'' +
            ", orgUnit='" + orgUnit + '\'' +
            ", eventDate='" + eventDate + '\'' +
            ", dueDate='" + dueDate + '\'' +
            ", storedBy='" + storedBy + '\'' +
            ", dataValues=" + dataValues +
            ", attributeOptionCombo='" + attributeOptionCombo + '\'' +
            '}';
    }
}
