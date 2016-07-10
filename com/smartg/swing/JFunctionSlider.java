package com.smartg.swing;

import java.util.function.Function;

import javax.swing.BoundedRangeModel;
import javax.swing.JSlider;

/**
 * JFunctionSlider uses two functions: Integer->Integer and Integer->Float. The
 * first one is used to change model values. For example to make slider to snap
 * on ticks (where minor tick interval = 5 and major tick interval = 10) we may
 * use function (t)->(t / 5) * 5.
 * 
 * The second one is used to add support for float values. For example for range
 * from 0.0 to 1.0 use range from 0 to 100 and set following float function
 * (t)-> t / 100f.
 * 
 * @author andro
 *
 */
public class JFunctionSlider extends JSlider {
    private static final long serialVersionUID = 6905535212146487179L;

    private Function<Integer, Integer> function = new Function<Integer, Integer>() {
	public Integer apply(Integer t) {
	    return t;
	}
    };
    private Function<Integer, Float> floatFunction = new Function<Integer, Float>() {
	public Float apply(Integer t) {
	    return t + 0f;
	}
    };

    public JFunctionSlider() {
    }

    public JFunctionSlider(BoundedRangeModel brm) {
	super(brm);
    }

    public JFunctionSlider(int orientation, int min, int max, int value) {
	super(orientation, min, max, value);
    }

    public JFunctionSlider(int min, int max, int value) {
	super(min, max, value);
    }

    public JFunctionSlider(int min, int max) {
	super(min, max);
    }

    public JFunctionSlider(int orientation) {
	super(orientation);
    }

    @Override
    public void setValue(int n) {
	super.setValue(function.apply(n));
    }

    public float getFloatValue() {
	return floatFunction.apply(getValue());
    }

    public float getFloatSecondValue() {
	return floatFunction.apply(getValue() + getExtent());
    }
    
    public Function<Integer, Integer> getFunction() {
	return function;
    }

    /**
     * Set function used to alter model values.
     * 
     * @param function
     */
    public void setFunction(Function<Integer, Integer> function) {
	if (function != null) {
	    this.function = function;
	} else {
	    this.function = new Function<Integer, Integer>() {
		public Integer apply(Integer t) {
		    return t;
		}
	    };
	}
    }

    /**
     * Set function used to support float ranges/values
     * 
     * @return
     */
    public Function<Integer, Float> getFloatFunction() {
	return floatFunction;
    }

    public void setFloatFunction(Function<Integer, Float> floatFunction) {
	if (floatFunction != null) {
	    this.floatFunction = floatFunction;
	} else {
	    this.floatFunction = new Function<Integer, Float>() {
		public Float apply(Integer t) {
		    return t + 0f;
		}
	    };
	}
    }
}