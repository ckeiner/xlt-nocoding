package com.xceptance.xlt.nocoding.util.dataStorage;

import com.xceptance.xlt.nocoding.util.dataStorage.storageUnits.DuplicateStorage;
import com.xceptance.xlt.nocoding.util.dataStorage.storageUnits.SingleStorage;
import com.xceptance.xlt.nocoding.util.dataStorage.storageUnits.uniqueStorage.DefaultKeyValueStorage;
import com.xceptance.xlt.nocoding.util.dataStorage.storageUnits.uniqueStorage.RecentKeyUniqueSingleStorage;
import com.xceptance.xlt.nocoding.util.dataStorage.storageUnits.uniqueStorage.UniqueSingleStorage;
import com.xceptance.xlt.nocoding.util.dataStorage.storageUnits.uniqueStorage.UniqueStorage;

/**
 * Handles all data storage related matters. This means, the class saves variables, default
 * definitions/headers/parameters/static requests.
 * 
 * @author ckeiner
 */
public class DataStorage
{
    protected SingleStorage defaultCookies;

    protected DuplicateStorage defaultParameters;

    protected SingleStorage defaultStatics;

    protected UniqueSingleStorage defaultHeaders;

    protected UniqueStorage variables;

    protected DefaultKeyValueStorage defaultItems;

    public DataStorage()
    {
        this.defaultCookies = new SingleStorage();
        this.defaultParameters = new DuplicateStorage();
        this.defaultStatics = new SingleStorage();
        this.defaultHeaders = new RecentKeyUniqueSingleStorage();
        this.variables = new UniqueStorage();
        this.defaultItems = new DefaultKeyValueStorage();
    }

    public SingleStorage getDefaultCookies()
    {
        return defaultCookies;
    }

    public DuplicateStorage getDefaultParameters()
    {
        return defaultParameters;
    }

    public SingleStorage getDefaultStatics()
    {
        return defaultStatics;
    }

    public UniqueSingleStorage getDefaultHeaders()
    {
        return defaultHeaders;
    }

    public UniqueStorage getVariables()
    {
        return variables;
    }

    public DefaultKeyValueStorage getDefaultItems()
    {
        return defaultItems;
    }

}