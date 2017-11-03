package com.xceptance.xlt.nocoding.util.variableResolver;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.xceptance.xlt.api.data.GeneralDataProvider;
import com.xceptance.xlt.nocoding.util.Context;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Tries to resolve variables. A variable is specified as "${(.)*}". Resolves values from the inside to the outside by
 * first looking into the dataStorage, then tries resolving it via beanshell and lastly it looks into the property
 * files.
 * 
 * @author ckeiner
 */
public class VariableResolver
{

    // The maximum amount you can re-resolve a value (so if variables reference another variable, we only resolve this many
    // times)
    private final int resolveDepth = 2;

    public Interpreter interpreter;

    public VariableResolver(final GeneralDataProvider dataProvider)
    {
        interpreter = new Interpreter();
        try
        {
            interpreter.set("NOW", new ParameterInterpreterNow());
            interpreter.set("RANDOM", new ParameterInterpreterRandom());
            interpreter.set("DATE", new Date());
            interpreter.set("DATA", dataProvider);
        }
        catch (final EvalError e)
        {
            e.printStackTrace();
        }
    }

    public VariableResolver()
    {
        this(GeneralDataProvider.getInstance());
    }

    public String resolveString(final String toResolve, final Context context)
    {
        final List<String> resolvedValues = new ArrayList<String>();
        String resolvedValue = resolveExpression(toResolve, false, context).getLeft();
        resolvedValues.add(resolvedValue);
        // As long as we can still resolve another value, continue
        while (!resolvedValue.equals(resolveExpression(resolvedValue, false, context).getLeft()))
        {
            //
            resolvedValue = resolveExpression(resolvedValue, false, context).getLeft();
            if (resolvedValues.contains(resolvedValue))
            {
                throw new IllegalArgumentException("Endless recursion detected at variable: " + toResolve);
            }
            else
            {
                resolvedValues.add(resolvedValue);
            }
        }
        return resolvedValue;
    }

    public Pair<String, Integer> resolveExpression(final String expression, final boolean mustBeResolved, final Context context)
    {
        String resolvedValue = "";
        char current;
        boolean isResolved = false;
        boolean ignoreNextChars = false;
        int index = 0;

        // Main iteration over the string
        for (; index < expression.length(); index++)
        {
            current = expression.charAt(index);
            // if (current == '\\')
            // {
            // // Add the next char and increment index
            // resolvedValue += expression.charAt(++index);
            // }
            // Change "mode", so every character that is following literally
            if (current == '\'' && expression.substring(index + 1).contains("\'"))
            {
                ignoreNextChars = true;
            }
            // Stop ignoring every character that is following
            else if (current == '\'' && ignoreNextChars)
            {
                ignoreNextChars = false;
            }
            // If we find a "}" and we don't want to ignore it and if we found a variable beforehand
            else if (current == '}' && !ignoreNextChars && mustBeResolved)
            {
                isResolved = true;
                // Resolve it
                final String resolved = resolveVariable(resolvedValue, context);
                if (resolved == null)
                {
                    resolvedValue += current;
                    continue;
                }
                else
                {
                    resolvedValue = resolved;
                    // Exit the loop
                    break;
                }
            }
            // We found a variable start and there is a variable end
            else if (current == '$' && expression.length() > index + 2 && expression.charAt(index + 1) == '{'
                     && expression.substring(index + 2).contains("}"))
            {
                // Resolve the variable
                final Pair<String, Integer> resolvedPair = resolveExpression(expression.substring(index + 2), true, context);
                // Add it to the function output resolvedValue
                resolvedValue += resolvedPair.getLeft();
                // Increment index by length of the variableName plus 3
                index += resolvedPair.getRight() + 2;
            }
            else
            {
                resolvedValue += current;
            }
        }

        if (mustBeResolved && !isResolved)
        {
            resolvedValue = "${" + resolvedValue;
        }

        return new ImmutablePair<String, Integer>(resolvedValue, index);
    }

    /**
     * Asks dataStorage etc for the value of the variable
     * 
     * @param variableName
     * @param context
     * @return
     */
    private String resolveVariable(final String variableName, final Context context)
    {
        // Try to resolve it in the dataStorage
        String resolvedValue = context.getDataStorage().getVariableByKey(variableName);
        // If we didn't find it, let beanshell handle the variable
        if (resolvedValue == null && !variableName.equals("{") && !variableName.equals("}"))
        {
            try
            {
                final Object beanShellEval = interpreter.eval(variableName);
                // If beanshell found something, save it as a string and add it to the dataStorage
                if (beanShellEval != null)
                {
                    resolvedValue = beanShellEval.toString();
                    // TODO added
                    // if we define a variable as ${RANDOM.String(8)} we only want to resolve it once. Thus we need to save
                    // variables in our dataStorage afterwards
                    context.getDataStorage().storeVariable(variableName, resolvedValue);

                }
                // BeanSheall doesn't know the expression
                else
                {
                    // Therefore try to find it in the properties
                    resolvedValue = context.getPropertyByKey(variableName);
                    // If it still cannot be found, it isn't resolvable
                    if (resolvedValue == null)
                    {
                        // So we simply add ${ and } again.
                        resolvedValue = "${" + variableName + "}";
                    }
                }
            }
            catch (final EvalError e)
            {
                // throw new RuntimeException("Evaluation Error: ", e);
                // We couldn't resolve it, therefore we simply can ignore it
            }
        }
        // This fixes Text${'{'}
        else if (resolvedValue == null)
        {
            resolvedValue = variableName;
        }

        // Return the resolved expression
        return resolvedValue;
    }

}
