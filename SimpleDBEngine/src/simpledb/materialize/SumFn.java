package simpledb.materialize;

import simpledb.query.Constant;
import simpledb.query.Scan;

/**
 * The <i>sum</i> aggregation function.
 * @author Edward Sciore
 */
public class SumFn implements AggregationFn {
   private String fldname;
   private int sum;
   
   /**
    * Create a sum aggregation function for the specified field.
    * @param fldname the name of the aggregated field
    */
   public SumFn(String fldname) {
      this.fldname = fldname;
   }
   
   /**
    * Start a new sum.
    * Since SimpleDB does not support null values,
    * every record will be counted,
    * regardless of the field.
    * The current count is thus set to 1.
    * @see AggregationFn#processFirst(Scan)
    */
   public void processFirst(Scan s) {
	   sum = s.getVal(fldname).asInt();
   }
   
   /**
    * Since SimpleDB does not support null values,
    * this method always increments the count,
    * regardless of the field.
    * @see AggregationFn#processNext(Scan)
    */
   public void processNext(Scan s) {
	  int nextVal = s.getVal(fldname).asInt();
      sum += nextVal;
   }
   
   /**
    * Return the field's name, prepended by "sumof".
    * @see AggregationFn#fieldName()
    */
   public String fieldName() {
      return "sumof" + fldname;
   }
   
   /**
    * Return the current sum.
    * @see AggregationFn#value()
    */
   public Constant value() {
      return new Constant(sum);
   }
}