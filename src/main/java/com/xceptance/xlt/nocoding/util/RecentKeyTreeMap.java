package com.xceptance.xlt.nocoding.util;

import java.util.Comparator;
import java.util.TreeMap;

public class RecentKeyTreeMap<K, V> extends TreeMap<K, V>
{

    /**
     * Randomly generated UID
     */
    private static final long serialVersionUID = -7785650967625657832L;

    public RecentKeyTreeMap(final Comparator<? super K> caseInsensitiveOrder)
    {
        super(caseInsensitiveOrder);
    }

    /**
     * Uses the most recent Key-Name as Key-Value
     */
    @Override
    public V put(final K arg0, final V arg1)
    {
        V output = null;
        if (this.containsKey(arg0))
        {
            output = this.remove(arg0);
            super.put(arg0, arg1);
        }
        else
        {
            output = super.put(arg0, arg1);
        }
        return output;
    };

}
