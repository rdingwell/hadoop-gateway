require 'lib/hadoop-gateway.jar'

class HadoopQueryExecutor 

    @executor = nil
    
    def initialize(map,reduce,functions,query_id,filter)
        @executor = org.projecthquery.hadoop.HadoopQueryExecutor.new(mapJs, reduceJs, functionsJs, filter, queryId);
    end
   
    
    def execute
        return @executor.execute()
    end

end