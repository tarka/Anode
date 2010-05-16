package net.haltcondition.anode;

/**
 * Copyright Steve Smith (tarkasteve@gmail.com): 15/05/2010
 */
public class Service
{
    private String serviceName;
    private String serviceId;

    public Service()
    {
    }

    public Service(String serviceName, String serviceId)
    {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
    }

    public String getServiceId()
    {
        return serviceId;
    }

    public void setServiceId(String serviceId)
    {
        this.serviceId = serviceId;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }
}
