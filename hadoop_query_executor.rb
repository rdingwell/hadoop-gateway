require 'lib/hadoop-gateway.jar'

class HadoopQueryExecutor 

    @executor = nil
    
    def initialize(map,reduce,functions,query_id,filter)
        @executor = org.projecthquery.hadoop.HadoopQueryExecutor.new(mapJs, reduceJs, functionsJs, filter, queryId);
        dir = File.dirname(__FILE__)
        
        @executor.setJarFiles([File.join(dir,"lib/rhino-1.7R3.jar'),File.join(dir,"lib/guava-10.0.jar')].to_java(:string))
    end
   
    
    def execute
        return @executor.execute()
    end

end