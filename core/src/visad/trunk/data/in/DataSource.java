package visad.data.in;

abstract public class DataSource
    extends DataFilter
{
    protected DataSource(DataSink downstream)
    {
	super(downstream);
    }

    public abstract boolean open(String spec);
}
