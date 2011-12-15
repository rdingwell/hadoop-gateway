package org.projecthquery.hadoop;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.projecthquery.js.JavaScriptManager;
import org.projecthquery.js.JavaScriptSource;

public class JavaScriptReducer extends JavaScriptMRBase implements Reducer<JSONWritableComaprable, JSONWritableComaprable,Writable, Writable>{
    private JavaScriptManager jsm;

    @Override
    public void configure(JobConf job) {
        List<JavaScriptSource> js = setUpSource(job);
        jsm = new JavaScriptManager(js,null);
        jsm.injectObject("$mapper", this);
    }

    @Override
    public void reduce(JSONWritableComaprable key, Iterator<JSONWritableComaprable> values,
            OutputCollector<Writable, Writable> oc, Reporter reporter)
            throws IOException {
        // TODO: Currently only works with Text/Double pairs

        jsm.injectObject("key", key.getValue());
        jsm.injectObject("values", new WrappedIterator(values));
        Object o = jsm.evaluate("reduce(key, values);", null);
        oc.collect(new Text(key.toString()), new Text(new JSONWritableComaprable(o).toString()));
    }
    
    private class WrappedIterator implements Iterator{
        Iterator<JSONWritableComaprable> wrapped;
        
        public boolean hasNext() {
            return wrapped.hasNext();
        }

        public Object next() {
            JSONWritableComaprable jwc = wrapped.next();
            return jwc.getValue();
          }

        public void remove() {
            wrapped.remove();
        }

        public WrappedIterator(Iterator<JSONWritableComaprable> iter){
            this.wrapped=iter;
        }
    }
}