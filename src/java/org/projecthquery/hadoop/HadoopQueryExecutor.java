package org.projecthquery.hadoop;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RunningJob;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class HadoopQueryExecutor implements Tool {

    private Configuration configuration;
    private String map, reduce,filter,functions,queryId;
    private String jar;
    private Map<String,String>  hadoopParams = new HashMap<String,String>();
    //private Map results = new ;
   
    private Map results = new HashMap();
    public HadoopQueryExecutor() {
    }
    
    public HadoopQueryExecutor(String map, String reduce, String filter, String functions, String queryId){
        this.map=map;
        this.reduce=reduce;
        this.filter=filter;
        this.functions=functions;
        this.queryId=queryId;
        setConf(new Configuration());
    }
    
    public HadoopQueryExecutor(String[] args){
        this.map=args[0];
        this.reduce=args[1];
        this.filter=args[2];
        this.functions=args[3];
        this.queryId=args[4];
    }
    
    public void setJar(String jar){
        this.jar = jar;
    }
    
    public void setHadoopParams(Map<String,String> params){
        this.hadoopParams = params;
    }
    public Object execute() throws Exception{
       run(new String[]{map,reduce,filter,functions,queryId});
       
       return results;
    }
    
    public static void main(String[] args) throws Exception
    {
        // Let ToolRunner handle generic command-line options 
        int res = ToolRunner.run(new Configuration(), new HadoopQueryExecutor(args), args);
        
        System.exit(res);
    }

    @Override
    public Configuration getConf() {
        return configuration;
    }

    @Override
    public void setConf(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public int run(String[] args) throws Exception {
        this.results = new HashMap();
        Path outputPath = new Path("hq_"+queryId);
         Path resultPath = new Path(outputPath,"results");
        Context context = Context.enter();
        Scriptable scope = context.initStandardObjects();
        
        
        Configuration conf = getConf();
        JobConf jobConf = new JobConf(conf);
       
        jobConf.set("map.js", map);
        jobConf.set("reduce.js", reduce);
        jobConf.set("filter.js", filter);
        jobConf.set("functions.js", functions);
        jobConf.set("query_id", queryId);
        
        for (Iterator<Map.Entry<String, String>> iterator = hadoopParams.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String, String> entry =  iterator.next();
            jobConf.set(entry.getKey(), entry.getValue());
        }

        jobConf.set("mapred.job.tracker", "localhost:9001");
        jobConf.set("fs.default.name", "hdfs://localhost:9000");
        jobConf.setMapperClass(JavaScriptMapper.class);
        jobConf.setReducerClass(JavaScriptReducer.class);
        
        jobConf.setOutputKeyClass(JSONWritableComaprable.class);
        jobConf.setOutputValueClass(JSONWritableComaprable.class);
        FileSystem fs = DistributedFileSystem.get(jobConf);
        jobConf.setJar(jar);
        
        
        FileInputFormat.addInputPath(jobConf, new Path("in"));
        FileOutputFormat.setOutputPath(jobConf,outputPath);

        RunningJob j = JobClient.runJob(jobConf);
        FileUtil.copyMerge(fs, outputPath,fs, resultPath , false, jobConf, null);
        FSDataInputStream fin = fs.open(resultPath);
        String ln = null;
        while((ln = fin.readLine()) != null){
            if(ln.trim().equals("")) continue;
          String[] bits =   ln.split("\t");
          scope.put("_str", scope, bits[0]);
          Object key = context.evaluateString(scope, "JSON.parse(_str)", "object", 1, null);
          scope.put("_str", scope, bits[1]);
          Object val = context.evaluateString(scope, "JSON.parse(_str)", "object", 1, null);
          this.results.put(key, val);
        }
        return 0;
    }
}
