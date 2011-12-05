package org.projecthquery.hadoop;

import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

public class HadoopQueryExecutorTest {



    @Test
    public void testExecute() {
        String mapJs = "function map(p){emit(p,p)}";
        String reduceJs = "function reduce(k,v){return v;}";
        String functionsJs = "";
        String filter= "";
        String queryId= "";

       HadoopQueryExecutor ex = new HadoopQueryExecutor(mapJs, reduceJs, functionsJs, filter, queryId) ;
       
       try {
        Object results = ex.execute();
        
       } catch (Exception e) {

       fail( e.toString());
    }
       
    }

}
