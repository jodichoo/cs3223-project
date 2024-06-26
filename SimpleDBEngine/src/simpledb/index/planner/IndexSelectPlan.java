package simpledb.index.planner;

import simpledb.index.Index;
import simpledb.index.query.IndexSelectScan;
import simpledb.metadata.IndexInfo;
import simpledb.plan.Plan;
import simpledb.query.Constant;
import simpledb.query.Scan;
import simpledb.record.Schema;
import simpledb.record.TableScan;

/** The Plan class corresponding to the <i>indexselect</i>
  * relational algebra operator.
  * @author Edward Sciore
  */
public class IndexSelectPlan implements Plan {
   private Plan p;
   private IndexInfo ii;
   private Constant val;
   
   /**
    * Creates a new indexselect node in the query tree
    * for the specified index and selection constant.
    * @param p the input table
    * @param ii information about the index
    * @param val the selection constant
    */
   public IndexSelectPlan(Plan p, IndexInfo ii, Constant val) {
      this.p = p;
      this.ii = ii;
      this.val = val;
   }
   
   /** 
    * Creates a new indexselect scan for this query
    * @see Plan#open()
    */
   public Scan open() {
      // throws an exception if p is not a tableplan.
      TableScan ts = (TableScan) p.open();
      Index idx = ii.open();
      return new IndexSelectScan(ts, idx, val);
   }
   
   /**
    * Estimates the number of block accesses to compute the 
    * index selection, which is the same as the 
    * index traversal cost plus the number of matching data records.
    * @see Plan#blocksAccessed()
    */
   public int blocksAccessed() {
      return ii.blocksAccessed() + recordsOutput();
   }
   
   /**
    * Estimates the number of output records in the index selection,
    * which is the same as the number of search key values
    * for the index.
    * @see Plan#recordsOutput()
    */
   public int recordsOutput() {
      return ii.recordsOutput();
   }
   
   /** 
    * Returns the distinct values as defined by the index.
    * @see Plan#distinctValues(String)
    */
   public int distinctValues(String fldname) {
      return ii.distinctValues(fldname);
   }
   
   /**
    * Returns the schema of the data table.
    * @see Plan#schema()
    */
   public Schema schema() {
      return p.schema(); 
   }
   
   public String getQueryPlan(String tblname, String currQueryPlan) {
      return String.format("(index select %s on %s=%s)", tblname, ii.getField(), val);
   }

   @Override
   public String getQueryPlan(String tblname, String currQueryPlan, int margin) {
       return String.format("Index scan on %s (using %s: %s = %s)", tblname, ii.getIndexType(), ii.getField(), val);
   }
}
