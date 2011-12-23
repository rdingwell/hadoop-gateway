package org.projecthquery.hadoop;

import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

public class HadoopQueryExecutorTest {



    @Test
    public void testExecute() {
        String mapJs = "function map(p){emit({\"gender\":p.p.gender},1)}";
        String reduceJs = "function reduce(k,v){var sum = 0; while(v.hasNext()){sum+=v.next();} return sum;}";
        String functionsJs = "var hQuery = {}; hQuery.Patient = function(x){this.p=x;}; var count=1; ";
        String filter= "";
        String queryId= "";

        HadoopQueryExecutor ex = new HadoopQueryExecutor(mapJs, reduceJs, functionsJs, filter, System.currentTimeMillis() +"");
        ex.setJar("lib/jars/hadoop-gateway.jar");
       try {
       Object results = ex.execute();
       System.out.println(results);
        
       } catch (Exception e) {

       fail( e.toString());
    }
       
    }

}
