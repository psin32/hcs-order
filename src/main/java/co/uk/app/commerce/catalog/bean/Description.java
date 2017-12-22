package co.uk.app.commerce.catalog.bean;

public class Description {

    private String name;

    private String shortdescription;

    private String longdescription;

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getShortdescription ()
    {
        return shortdescription;
    }

    public void setShortdescription (String shortdescription)
    {
        this.shortdescription = shortdescription;
    }

    public String getLongdescription ()
    {
        return longdescription;
    }

    public void setLongdescription (String longdescription)
    {
        this.longdescription = longdescription;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [name = "+name+", shortdescription = "+shortdescription+", longdescription = "+longdescription+"]";
    }
	
}
