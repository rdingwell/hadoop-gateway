package org.projecthquery.hadoop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Mapper;
import org.mozilla.javascript.Function;
import org.projecthquery.js.JavaScriptManager;
import org.projecthquery.js.JavaScriptSource;
import org.projecthquery.js.TypeConverter;

public class JavaScriptMapper extends
    Mapper<LongWritable, Text, Writable, Writable> {
    Function js_map;
    private JavaScriptManager jsMan;
    @Override
    protected void map(LongWritable key, Text value,
            org.apache.hadoop.mapreduce.Mapper.Context context)
            throws IOException, InterruptedException {
        
    }

    @Override
    protected void setup(org.apache.hadoop.mapreduce.Mapper.Context context)
            throws IOException, InterruptedException {
 
        super.setup(context);
        Configuration conf = context.getConfiguration();
        List<JavaScriptSource> js = new ArrayList<JavaScriptSource>();
        js.add(new JavaScriptSource("map.js", conf.get("map_js")));
        js.add(new JavaScriptSource("functions.js", conf.get("functions_js")));
        js.add(new JavaScriptSource("emit.js", "function emit(k,v){_mapper.emit(k,v,_hadoop_context)"));
        
        Map<String,Object> cos = new HashMap<String,Object>();
        cos.put("_hadoop_context", context);
        cos.put("_mapper", this);
        
        jsMan = new JavaScriptManager(js,cos);
        
       
    }
    
    
    public void emit(Object k, Object v, org.apache.hadoop.mapreduce.Mapper.Context  context) throws IOException, InterruptedException{
         context.write(TypeConverter.convert(k), TypeConverter.convert(v));
    }

}
