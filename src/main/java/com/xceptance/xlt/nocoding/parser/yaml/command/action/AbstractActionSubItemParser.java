package com.xceptance.xlt.nocoding.parser.yaml.command.action;

import java.util.List;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.Node;

import com.xceptance.xlt.nocoding.command.action.AbstractActionSubItem;

/**
 * The class for parsing action items.
 *
 * @author ckeiner
 */
public abstract class AbstractActionSubItemParser
{

    /**
     * Parses the defined action item at the node.
     *
     * @param context
     *            The {@link Mark} of the surrounding {@link Node}/context.
     * @param actionItemNode
     *            The {@link Node} the item starts at
     * @return A list of {@link AbstractActionSubItem}
     */
    public abstract List<AbstractActionSubItem> parse(final Mark context, final Node actionItemNode);

}
