/*
 * The MIT License
 *
 * Copyright (c) 2004-2009, Sun Microsystems, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.gem.persistentparameter;

import hudson.Extension;
import hudson.model.BooleanParameterValue;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.SimpleParameterDefinition;
import net.sf.json.JSONObject;
import org.jenkinsci.Symbol;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * {@link ParameterDefinition} that is either 'true' or 'false'.
 */
public class PersistentBooleanParameterDefinition extends SimpleParameterDefinition
{
  private static final long serialVersionUID = 2085073955513133606L;
  private final boolean defaultValue;
  private final boolean successfulOnly;

  @DataBoundConstructor
  public PersistentBooleanParameterDefinition(String name, boolean defaultValue, boolean successfulOnly, String description)
  {
    super(name, description);
    this.defaultValue = defaultValue;
    this.successfulOnly = successfulOnly;
  }

  @Override
  public ParameterDefinition copyWithDefaultValue(ParameterValue defaultValue)
  {
    if(defaultValue instanceof BooleanParameterValue)
    {
      BooleanParameterValue value = (BooleanParameterValue)defaultValue;
      return new PersistentBooleanParameterDefinition(getName(), value.value, isSuccessfulOnly(), getDescription());
    }
    else
    {
      return this;
    }
  }

  public boolean isDefaultValue()
  {
    return defaultValue;
  }

  public boolean isSuccessfulOnly()
  {
    return successfulOnly;
  }

  @Override
  public ParameterValue createValue(StaplerRequest req, JSONObject jo)
  {
    BooleanParameterValue value = req.bindJSON(BooleanParameterValue.class, jo);
    value.setDescription(getDescription());
    return value;
  }

  @Override
  public ParameterValue createValue(String value)
  {
    return new BooleanParameterValue(getName(), Boolean.parseBoolean(value), getDescription());
  }

  @Override
  public BooleanParameterValue getDefaultParameterValue()
  {
    ParameterValue lastValue = CurrentProject.getLastValue(this, successfulOnly);
    boolean value = (lastValue instanceof BooleanParameterValue) ? ((BooleanParameterValue)lastValue).getValue() : defaultValue;
    return new BooleanParameterValue(getName(), value, getDescription());
  }

  @Extension
  @Symbol({"persistentBoolean", "persistentBooleanParam"})
  public static class DescriptorImpl extends ParameterDescriptor
  {
    @Override
    public String getDisplayName()
    {
      return "Persistent Boolean Parameter";
    }

    @Override
    public String getHelpFile()
    {
      return "/help/parameter/boolean.html";
    }
  }
}
