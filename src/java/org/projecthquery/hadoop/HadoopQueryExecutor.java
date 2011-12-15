package org.projecthquery.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class HadoopQueryExecutor implements Tool {

    private Configuration configuration;
    private String map, reduce,filter,functions,queryId;
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
    
    public Object execute() throws Exception{
       run(new String[]{map,reduce,filter,functions,queryId});
       return null;
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
        Configuration conf = getConf();
        JobConf jobConf = new JobConf(conf);
        
        jobConf.set("map.js", map);
        jobConf.set("reduce.js", reduce);
        jobConf.set("filter.js", filter);
        jobConf.set("functions.js", functions);
        jobConf.set("query_id", queryId);

        
        jobConf.setMapperClass(JavaScriptMapper.class);
        jobConf.setReducerClass(JavaScriptReducer.class);
        // TODO: Need to generalize these output classes
        jobConf.setOutputKeyClass(JSONWritableComaprable.class);
        jobConf.setOutputValueClass(JSONWritableComaprable.class);
        
        FileInputFormat.addInputPath(jobConf, new Path("in"));
        FileOutputFormat.setOutputPath(jobConf, new Path("out"));
        
        JobClient.runJob(jobConf);
        return 0;
    }
}
