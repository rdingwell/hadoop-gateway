package org.projecthquery.hadoop;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.io.WritableComparable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class JSONWritableComaprable implements WritableComparable<Object> {
    static int idex = 0;
    Object o;
    
    public JSONWritableComaprable(Object o){
        this.o = o;
    }
    
    public JSONWritableComaprable(){
       
    }
    
    public Object getValue(){
        return this.o;
    }
    
    @Override
    public void readFields(DataInput di) throws IOException {
        String s = di.readUTF();
        Context context = Context.enter();
        Scriptable scope = context.initStandardObjects();
        scope.put("_str", scope, s);
        Object obj = context.evaluateString(scope, "JSON.parse(_str)", "object", 1, null);
        this.o = obj;
    }

    @Override
    public void write(DataOutput dataOut) throws IOException {
        
        dataOut.writeUTF(toString());
    }

    @Override
    public int compareTo(Object w) {
        if (w instanceof JSONWritableComaprable){
            w = ((JSONWritableComaprable)w).getValue();
        }
        if(w instanceof Comparable && this.o instanceof Comparable){
          
           return ((Comparable)w).compareTo(((Comparable)this.o));
        }
        else if (w instanceof Map && this.o instanceof Map){
          
            Map wm = (Map)w;
            Map om = (Map)o;
            boolean b = wm.entrySet().equals(om.entrySet());
           
            return ( b)? 0 : -1;
        }
      
        return (w.equals(this.o))? 0 : -1;
    }

    public boolean equals(Object o){
        
        if (o instanceof JSONWritableComaprable){
            
            return ((JSONWritableComaprable)o).compareTo(this) == 0;
            
        }
        
        return false;
    }
    public String toString(){

        Context context = Context.enter();
        Scriptable scope = context.initStandardObjects();
        scope.put("_obj", scope, this.o);
        Object obj = context.evaluateString(scope, "JSON.stringify(_obj)", "object", 1, null);
        return obj.toString();
    }




}
