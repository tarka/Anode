package net.haltcondition.anode;

/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 15/05/2010
 */
public class Service
{
    private String serviceId;

    public Service(String serviceId)
    {
        this.serviceId = serviceId;
    }

    public String getServiceId()
    {
        return serviceId;
    }

    public void setServiceId(String serviceId)
    {
        this.serviceId = serviceId;
    }
}
